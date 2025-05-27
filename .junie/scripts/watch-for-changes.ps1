# PowerShell script to watch for changes in source files and automatically re-run quality assessment
# This script monitors source directories for changes and triggers the quality assessment process
#
# Parameters:
#   -WatchDirs: Optional. Directories to watch for changes, separated by semicolons. Default is "src\main\java;src\test\java"
#   -FilePatterns: Optional. File patterns to watch, separated by semicolons. Default is "*.java"
#   -DebouncePeriod: Optional. Time in seconds to wait before running quality assessment after a change. Default is 10
#   -SkipInitialRun: Optional. If specified, skips the initial quality assessment run.
#   -SkipBuild: Optional. If specified, skips the Maven build step in quality assessment.
#   -ProjectRoot: Optional. The root directory of the project. Default is determined from script location.
param (
    [string]$WatchDirs = "src\main\java;src\test\java",
    [string]$FilePatterns = "*.java",
    [int]$DebouncePeriod = 10,
    [switch]$SkipInitialRun = $false,
    [switch]$SkipBuild = $false,
    [string]$ProjectRoot = ""
)

# Get the script directory and project root if not provided
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $ProjectRoot) {
    $ProjectRoot = Split-Path -Parent (Split-Path -Parent $scriptDir)
}

# Function to handle errors
function Handle-Error {
    param (
        [string]$errorMessage,
        [bool]$exitOnError = $true
    )

    Write-Host "ERROR: $errorMessage" -ForegroundColor Red
    if ($exitOnError) {
        exit 1
    }
}

# Parse the watch directories and file patterns
$watchDirectories = $WatchDirs -split ";"
$patterns = $FilePatterns -split ";"

Write-Host "Starting file watcher for quality assessment..."
Write-Host "Project root: $ProjectRoot"
Write-Host "Monitoring directories: $($watchDirectories -join ", ")"
Write-Host "File patterns: $($patterns -join ", ")"
Write-Host "Debounce period: $DebouncePeriod seconds"
Write-Host "Press Ctrl+C to stop monitoring."

# Function to create a watcher for a specific directory
function New-DirectoryWatcher {
    param (
        [string]$directory,
        [array]$patterns
    )

    try {
        $fullPath = Join-Path -Path $ProjectRoot -ChildPath $directory

        # Check if the directory exists
        if (-not (Test-Path $fullPath)) {
            Write-Host "Warning: Directory $fullPath does not exist. Skipping..." -ForegroundColor Yellow
            return $null
        }

        # Create a FileSystemWatcher
        $watcher = New-Object System.IO.FileSystemWatcher
        $watcher.Path = $fullPath
        $watcher.IncludeSubdirectories = $true
        $watcher.EnableRaisingEvents = $true

        # We'll handle patterns in the event handler
        # FileSystemWatcher only supports one filter at a time
        $watcher.Filter = "*.*"

        Write-Host "Created watcher for directory: $fullPath"
        return $watcher
    } catch {
        $errorMsg = $_.Exception.Message
        Write-Host "Error creating watcher for directory $directory`: $errorMsg" -ForegroundColor Yellow
        return $null
    }
}

# Create watchers for each directory
$watchers = @()
foreach ($dir in $watchDirectories) {
    $watcher = New-DirectoryWatcher -directory $dir -patterns $patterns
    if ($watcher) {
        $watchers += $watcher
    }
}

if ($watchers.Count -eq 0) {
    Handle-Error "No valid directories to watch. Please check the WatchDirs parameter."
}

# Define a variable to track the last run time to prevent multiple runs for the same change
$lastRunTime = Get-Date

# Function to check if a file matches any of the patterns
function Test-FileMatchesPattern {
    param (
        [string]$filePath,
        [array]$patterns
    )

    $fileName = Split-Path -Leaf $filePath
    foreach ($pattern in $patterns) {
        if ($fileName -like $pattern) {
            return $true
        }
    }
    return $false
}

# Define the action to take when a file is changed
$action = {
    $path = $Event.SourceEventArgs.FullPath
    $changeType = $Event.SourceEventArgs.ChangeType
    $timeStamp = Get-Date

    # Check if the file matches any of the patterns
    if (Test-FileMatchesPattern -filePath $path -patterns $patterns) {
        # Check if enough time has passed since the last run
        $timeSinceLastRun = ($timeStamp - $script:lastRunTime).TotalSeconds
        if ($timeSinceLastRun -ge $script:DebouncePeriod) {
            $script:lastRunTime = $timeStamp

            Write-Host ""
            Write-Host "File $path was $changeType at $timeStamp"
            Write-Host "Running quality assessment..."

            try {
                # Run the quality assessment script with parameters
                $qualityScriptPath = Join-Path -Path $scriptDir -ChildPath "run-quality-assessment.ps1"

                # Check if the script exists
                if (-not (Test-Path $qualityScriptPath)) {
                    Write-Host "Error: Quality assessment script not found at $qualityScriptPath" -ForegroundColor Red
                    return
                }

                # Build the command with parameters
                $skipBuildParam = if ($script:SkipBuild) { "-SkipBuild" } else { "" }
                $command = "$qualityScriptPath $skipBuildParam -ProjectRoot '$script:ProjectRoot'"

                Write-Host "Executing: $command"
                Invoke-Expression $command

                Write-Host "Quality assessment completed. Continuing to monitor for changes..."
                Write-Host "Press Ctrl+C to stop monitoring."
            } catch {
                $errorMsg = $_.Exception.Message
                Write-Host "Error running quality assessment: $errorMsg" -ForegroundColor Red
                Write-Host "Continuing to monitor for changes..."
                Write-Host "Press Ctrl+C to stop monitoring."
            }
        } else {
            # Debounce in effect, log but don't run
            Write-Host "Change detected in $path, but waiting for debounce period ($($script:DebouncePeriod - $timeSinceLastRun) seconds remaining)..." -ForegroundColor Gray
        }
    }
}

# Register the event handlers for each watcher
$handlers = @()
foreach ($watcher in $watchers) {
    try {
        $handlers += Register-ObjectEvent -InputObject $watcher -EventName Created -Action $action
        $handlers += Register-ObjectEvent -InputObject $watcher -EventName Changed -Action $action
        $handlers += Register-ObjectEvent -InputObject $watcher -EventName Deleted -Action $action
        $handlers += Register-ObjectEvent -InputObject $watcher -EventName Renamed -Action $action

        Write-Host "Registered event handlers for watcher on $($watcher.Path)"
    } catch {
        $errorMsg = $_.Exception.Message
        Write-Host "Error registering event handlers for watcher on $($watcher.Path): $errorMsg" -ForegroundColor Yellow
    }
}

if ($handlers.Count -eq 0) {
    Handle-Error "Failed to register any event handlers. File watching cannot proceed."
}

# Run the initial quality assessment if not skipped
if (-not $SkipInitialRun) {
    Write-Host "Running initial quality assessment..."
    try {
        $qualityScriptPath = Join-Path -Path $scriptDir -ChildPath "run-quality-assessment.ps1"

        # Check if the script exists
        if (-not (Test-Path $qualityScriptPath)) {
            Handle-Error "Quality assessment script not found at $qualityScriptPath"
        }

        # Build the command with parameters
        $skipBuildParam = if ($SkipBuild) { "-SkipBuild" } else { "" }
        $command = "$qualityScriptPath $skipBuildParam -ProjectRoot '$ProjectRoot'"

        Write-Host "Executing: $command"
        Invoke-Expression $command

        Write-Host "Initial quality assessment completed. Now monitoring for changes..."
    } catch {
        $errorMsg = $_.Exception.Message
        Write-Host "Error running initial quality assessment: $errorMsg" -ForegroundColor Red
        Write-Host "Continuing with file monitoring..."
    }
} else {
    Write-Host "Skipping initial quality assessment as requested. Now monitoring for changes..."
}

Write-Host "Press Ctrl+C to stop monitoring."

try {
    # Keep the script running until Ctrl+C is pressed
    while ($true) {
        Start-Sleep -Seconds 1
    }
}
catch {
    $errorMsg = $_.Exception.Message
    Write-Host "Error in file watcher: $errorMsg" -ForegroundColor Red
}
finally {
    # Clean up the event handlers when the script is stopped
    Write-Host "Cleaning up event handlers and watchers..."

    # Unregister all event handlers
    foreach ($handler in $handlers) {
        try {
            Unregister-Event -SourceIdentifier $handler.Name -ErrorAction SilentlyContinue
        } catch {
            $errorMsg = $_.Exception.Message
            Write-Host "Error unregistering event handler $($handler.Name): $errorMsg" -ForegroundColor Yellow
        }
    }

    # Dispose all watchers
    foreach ($watcher in $watchers) {
        try {
            $watcher.Dispose()
        } catch {
            $errorMsg = $_.Exception.Message
            Write-Host "Error disposing watcher: $errorMsg" -ForegroundColor Yellow
        }
    }

    Write-Host "File monitoring stopped at $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")"
}

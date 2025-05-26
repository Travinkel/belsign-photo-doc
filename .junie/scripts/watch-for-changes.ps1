# PowerShell script to watch for changes in source files and automatically re-run quality assessment
# This script monitors src/main/java and src/test/java directories for changes

# Get the project root directory (parent of .junie/scripts)
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent (Split-Path -Parent $scriptDir)

Write-Host "Starting file watcher for quality assessment..."
Write-Host "Project root: $projectRoot"
Write-Host "Monitoring src/main/java and src/test/java for changes..."
Write-Host "Press Ctrl+C to stop monitoring."

# Create a FileSystemWatcher to monitor the src directory
$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = Join-Path -Path $projectRoot -ChildPath "src"
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

# Define the file patterns to watch
$watcher.Filter = "*.java"

# Define a variable to track the last run time to prevent multiple runs for the same change
$lastRunTime = Get-Date

# Define a debounce period (in seconds) to prevent multiple runs for rapid changes
$debouncePeriod = 10

# Define the action to take when a file is changed
$action = {
    $path = $Event.SourceEventArgs.FullPath
    $changeType = $Event.SourceEventArgs.ChangeType
    $timeStamp = Get-Date

    # Check if the file is in src/main/java or src/test/java
    if ($path -match "src[/\\](main|test)[/\\]java") {
        # Check if enough time has passed since the last run
        $timeSinceLastRun = ($timeStamp - $script:lastRunTime).TotalSeconds
        if ($timeSinceLastRun -ge $script:debouncePeriod) {
            $script:lastRunTime = $timeStamp

            Write-Host ""
            Write-Host "File $path was $changeType at $timeStamp"
            Write-Host "Running quality assessment..."

            # Run the quality assessment script
            $qualityScriptPath = Join-Path -Path $scriptDir -ChildPath "run-quality-assessment.ps1"
            & $qualityScriptPath

            Write-Host "Quality assessment completed. Continuing to monitor for changes..."
            Write-Host "Press Ctrl+C to stop monitoring."
        }
    }
}

# Register the event handlers
$handlers = @()
$handlers += Register-ObjectEvent -InputObject $watcher -EventName Created -Action $action
$handlers += Register-ObjectEvent -InputObject $watcher -EventName Changed -Action $action
$handlers += Register-ObjectEvent -InputObject $watcher -EventName Deleted -Action $action
$handlers += Register-ObjectEvent -InputObject $watcher -EventName Renamed -Action $action

# Run the initial quality assessment
Write-Host "Running initial quality assessment..."
$qualityScriptPath = Join-Path -Path $scriptDir -ChildPath "run-quality-assessment.ps1"
& $qualityScriptPath

Write-Host "Initial quality assessment completed. Now monitoring for changes..."
Write-Host "Press Ctrl+C to stop monitoring."

try {
    # Keep the script running until Ctrl+C is pressed
    while ($true) {
        Start-Sleep -Seconds 1
    }
}
finally {
    # Clean up the event handlers when the script is stopped
    $handlers | ForEach-Object {
        Unregister-Event -SourceIdentifier $_.Name
    }
    $watcher.Dispose()
    Write-Host "File monitoring stopped."
}

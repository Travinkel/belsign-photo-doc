# PowerShell script to run the entire quality assessment process
# This script will:
# 1. Run a full Maven build with verification (optional)
# 2. Collect all quality reports into a unified directory
# 3. Analyze the reports and generate a summary
#
# Parameters:
#   -SkipBuild: Optional. If specified, skips the Maven build step.
#   -MavenArgs: Optional. Additional arguments to pass to Maven. Default is empty.
#   -ProjectRoot: Optional. The root directory of the project. Default is determined from script location.
#   -OutputDir: Optional. The directory where reports will be collected. Default is "target\quality-reports"
param (
    [switch]$SkipBuild = $false,
    [string]$MavenArgs = "",
    [string]$ProjectRoot = "",
    [string]$OutputDir = ""
)

# Get the script directory and project root if not provided
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $ProjectRoot) {
    $ProjectRoot = Split-Path -Parent (Split-Path -Parent $scriptDir)
}

# Set the output directory if not provided
if (-not $OutputDir) {
    $OutputDir = Join-Path -Path $ProjectRoot -ChildPath "target\quality-reports"
}

Write-Host "Starting quality assessment process..."
Write-Host "Project root: $ProjectRoot"
Write-Host "Output directory: $OutputDir"

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

# Step 1: Run Maven verify from the project root (if not skipped)
if (-not $SkipBuild) {
    Write-Host "Running Maven verify to generate quality reports..."
    try {
        Push-Location -Path $ProjectRoot

        # Build the Maven command
        $mavenCommand = "mvn clean verify"
        if ($MavenArgs) {
            $mavenCommand += " $MavenArgs"
        }

        # Execute Maven command
        Write-Host "Executing: $mavenCommand"
        Invoke-Expression $mavenCommand

        # Check if Maven build was successful
        if ($LASTEXITCODE -ne 0) {
            Pop-Location
            Handle-Error "Maven build failed with exit code $LASTEXITCODE. Please fix the build issues and try again."
        }

        # Return to the original directory
        Pop-Location
    } catch {
        if ($null -ne (Get-Location).Path) {
            Pop-Location
        }
        Handle-Error "Error during Maven build: $_"
    }
} else {
    Write-Host "Skipping Maven build as requested..."
}

# Step 2: Collect quality reports
Write-Host "Collecting quality reports..."
try {
    $collectScriptPath = Join-Path -Path $scriptDir -ChildPath "collect-quality-reports.ps1"

    # Check if the script exists
    if (-not (Test-Path $collectScriptPath)) {
        Handle-Error "Collect quality reports script not found at $collectScriptPath"
    }

    # Execute the script with parameters
    Write-Host "Executing: $collectScriptPath -OutputDir '$OutputDir' -ProjectRoot '$ProjectRoot'"
    $collectedReportsDir = & $collectScriptPath -OutputDir $OutputDir -ProjectRoot $ProjectRoot

    # Check if the script executed successfully
    if (-not $collectedReportsDir -or -not (Test-Path $collectedReportsDir)) {
        Handle-Error "Failed to collect quality reports"
    }

    Write-Host "Quality reports collected successfully in $collectedReportsDir"
} catch {
    Handle-Error "Error collecting quality reports: $_"
}

# Step 3: Analyze quality reports
Write-Host "Analyzing quality reports..."
try {
    $analyzeScriptPath = Join-Path -Path $scriptDir -ChildPath "analyze-quality-reports.ps1"

    # Check if the script exists
    if (-not (Test-Path $analyzeScriptPath)) {
        Handle-Error "Analyze quality reports script not found at $analyzeScriptPath"
    }

    # Define the output file for the quality report
    $qualityReportFile = Join-Path -Path $ProjectRoot -ChildPath ".junie\quality-assessment.md"

    # Execute the script with parameters
    Write-Host "Executing: $analyzeScriptPath -ReportsDir '$collectedReportsDir' -OutputFile '$qualityReportFile' -ProjectRoot '$ProjectRoot'"
    & $analyzeScriptPath -ReportsDir $collectedReportsDir -OutputFile $qualityReportFile -ProjectRoot $ProjectRoot

    # Check if the report was generated
    if (-not (Test-Path $qualityReportFile)) {
        Handle-Error "Failed to generate quality report at $qualityReportFile" $false
        Write-Host "Continuing with CSS quality analysis..."
    } else {
        Write-Host "Quality report generated successfully at $qualityReportFile"
    }
} catch {
    Handle-Error "Error analyzing quality reports: $_" $false
    Write-Host "Continuing with CSS quality analysis..."
}

# Step 4: Analyze CSS quality
Write-Host "Analyzing CSS quality..."
try {
    $analyzeCssScriptPath = Join-Path -Path $scriptDir -ChildPath "analyze-css-quality.ps1"

    # Check if the script exists
    if (-not (Test-Path $analyzeCssScriptPath)) {
        Handle-Error "Analyze CSS quality script not found at $analyzeCssScriptPath" $false
        Write-Host "Skipping CSS quality analysis..."
    } else {
        # Define the output file for the CSS quality report
        $cssReportFile = Join-Path -Path $ProjectRoot -ChildPath ".junie\css-quality-assessment.md"
        $cssOutputDir = Join-Path -Path $OutputDir -ChildPath "css"

        # Execute the script with parameters
        Write-Host "Executing: $analyzeCssScriptPath -OutputDir '$cssOutputDir' -ReportFile '$cssReportFile' -ProjectRoot '$ProjectRoot'"
        & $analyzeCssScriptPath -OutputDir $cssOutputDir -ReportFile $cssReportFile -ProjectRoot $ProjectRoot

        # Check if the report was generated
        if (-not (Test-Path $cssReportFile)) {
            Write-Host "Warning: Failed to generate CSS quality report at $cssReportFile" -ForegroundColor Yellow
        } else {
            Write-Host "CSS quality report generated successfully at $cssReportFile"
        }
    }
} catch {
    Write-Host "Error analyzing CSS quality: $_" -ForegroundColor Yellow
    Write-Host "Continuing with quality assessment process..."
}

# Generate a summary of the quality assessment process
$qualityReportFile = Join-Path -Path $ProjectRoot -ChildPath ".junie\quality-assessment.md"
$cssReportFile = Join-Path -Path $ProjectRoot -ChildPath ".junie\css-quality-assessment.md"

# Check which reports were generated
$qualityReportExists = Test-Path $qualityReportFile
$cssReportExists = Test-Path $cssReportFile

# Display a summary of the process
Write-Host "`nQuality Assessment Summary:"
Write-Host "-------------------------"
Write-Host "Maven Build: $(if ($SkipBuild) { "Skipped" } else { "Completed" })"
Write-Host "Quality Reports Collection: Completed"
Write-Host "Quality Analysis: $(if ($qualityReportExists) { "Completed" } else { "Failed" })"
Write-Host "CSS Quality Analysis: $(if ($cssReportExists) { "Completed" } else { "Failed" })"

Write-Host "`nAvailable Reports:"
if ($qualityReportExists) {
    Write-Host "- Quality report: $qualityReportFile"
}
if ($cssReportExists) {
    Write-Host "- CSS quality report: $cssReportFile"
}

Write-Host "`nQuality assessment process completed at $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")"

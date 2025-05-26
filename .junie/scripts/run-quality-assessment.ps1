# PowerShell script to run the entire quality assessment process
# This script will:
# 1. Run a full Maven build with verification
# 2. Collect all quality reports into a unified directory
# 3. Analyze the reports and generate a summary

# Get the script directory and project root
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent (Split-Path -Parent $scriptDir)

Write-Host "Starting quality assessment process..."
Write-Host "Project root: $projectRoot"

# Step 1: Run Maven verify from the project root
Write-Host "Running Maven verify to generate quality reports..."
Push-Location -Path $projectRoot
mvn clean verify

# Check if Maven build was successful
if ($LASTEXITCODE -ne 0) {
    Write-Host "Maven build failed with exit code $LASTEXITCODE"
    Write-Host "Please fix the build issues and try again."
    Pop-Location
    exit 1
}

# Return to the original directory
Pop-Location

# Step 2: Collect quality reports
Write-Host "Collecting quality reports..."
$collectScriptPath = Join-Path -Path $scriptDir -ChildPath "collect-quality-reports.ps1"
& $collectScriptPath

# Step 3: Analyze quality reports
Write-Host "Analyzing quality reports..."
$analyzeScriptPath = Join-Path -Path $scriptDir -ChildPath "analyze-quality-reports.ps1"
& $analyzeScriptPath

Write-Host "Quality assessment process completed."
$reportPath = Join-Path -Path $projectRoot -ChildPath ".junie" -AdditionalChildPath "quality-assessment.md"
Write-Host "Quality report summary is available at $reportPath"

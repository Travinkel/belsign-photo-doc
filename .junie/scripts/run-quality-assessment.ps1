# PowerShell script to run the entire quality assessment process
# This script will:
# 1. Run a full Maven build with verification
# 2. Collect all quality reports into a unified directory
# 3. Analyze the reports and generate a summary

Write-Host "Starting quality assessment process..."

# Step 1: Run Maven verify
Write-Host "Running Maven verify to generate quality reports..."
mvn clean verify

# Check if Maven build was successful
if ($LASTEXITCODE -ne 0) {
    Write-Host "Maven build failed with exit code $LASTEXITCODE"
    Write-Host "Please fix the build issues and try again."
    exit 1
}

# Step 2: Collect quality reports
Write-Host "Collecting quality reports..."
& .\collect-quality-reports.ps1

# Step 3: Analyze quality reports
Write-Host "Analyzing quality reports..."
& .\analyze-quality-reports.ps1

Write-Host "Quality assessment process completed."
Write-Host "Quality report summary is available at .junie/quality-assessment.md"
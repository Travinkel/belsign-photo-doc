# PowerShell script to collect quality reports into a unified directory
# This script should be run after 'mvn verify' to gather all quality reports
#
# Parameters:
#   -OutputDir: Optional. The directory where reports will be collected. Default is "target\quality-reports"
#   -ProjectRoot: Optional. The root directory of the project. Default is determined from script location.
param (
    [string]$OutputDir = "",
    [string]$ProjectRoot = ""
)

# Get the script directory and project root if not provided
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $ProjectRoot) {
    $ProjectRoot = Split-Path -Parent (Split-Path -Parent $scriptDir)
}

Write-Host "Starting quality report collection..."
Write-Host "Project root: $ProjectRoot"

# Set the reports directory if not provided
if (-not $OutputDir) {
    $OutputDir = Join-Path -Path $ProjectRoot -ChildPath "target\quality-reports"
}

# Create the unified quality reports directory
try {
    New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null
    Write-Host "Created unified quality reports directory: $OutputDir"
} catch {
    Write-Host "Error creating directory $OutputDir : $_"
    exit 1
}

# Create subdirectories for each type of report
$jacocoDir = Join-Path -Path $OutputDir -ChildPath "jacoco"
$spotbugsDir = Join-Path -Path $OutputDir -ChildPath "spotbugs"
$pmdDir = Join-Path -Path $OutputDir -ChildPath "pmd"
$surefireDir = Join-Path -Path $OutputDir -ChildPath "surefire"
$failsafeDir = Join-Path -Path $OutputDir -ChildPath "failsafe"

try {
    New-Item -ItemType Directory -Force -Path $jacocoDir | Out-Null
    New-Item -ItemType Directory -Force -Path $spotbugsDir | Out-Null
    New-Item -ItemType Directory -Force -Path $pmdDir | Out-Null
    New-Item -ItemType Directory -Force -Path $surefireDir | Out-Null
    New-Item -ItemType Directory -Force -Path $failsafeDir | Out-Null
} catch {
    Write-Host "Error creating subdirectories: $_"
    exit 1
}

# Function to safely copy files
function Copy-ReportFiles {
    param (
        [string]$SourcePath,
        [string]$DestinationPath,
        [string]$ReportType
    )

    try {
        if (Test-Path $SourcePath) {
            Copy-Item -Path $SourcePath -Destination $DestinationPath -Recurse -Force
            Write-Host "$ReportType reports copied to $DestinationPath"
            return $true
        } else {
            Write-Host "$ReportType reports not found at $SourcePath"
            return $false
        }
    } catch {
        Write-Host "Error copying $ReportType reports: $_"
        return $false
    }
}

# Copy JaCoCo reports
Write-Host "Collecting JaCoCo reports..."
$jacocoSourcePath = Join-Path -Path $ProjectRoot -ChildPath "target\site\jacoco\*"
Copy-ReportFiles -SourcePath $jacocoSourcePath -DestinationPath $jacocoDir -ReportType "JaCoCo"

# Copy SpotBugs reports
Write-Host "Collecting SpotBugs reports..."
$spotbugsSourcePath = Join-Path -Path $ProjectRoot -ChildPath "target\spotbugsXml.xml"
$spotbugsDestPath = Join-Path -Path $spotbugsDir -ChildPath "spotbugsXml.xml"
Copy-ReportFiles -SourcePath $spotbugsSourcePath -DestinationPath $spotbugsDestPath -ReportType "SpotBugs"

# Copy PMD reports
Write-Host "Collecting PMD reports..."
$pmdSourcePath = Join-Path -Path $ProjectRoot -ChildPath "target\pmd.xml"
$pmdDestPath = Join-Path -Path $pmdDir -ChildPath "pmd.xml"
Copy-ReportFiles -SourcePath $pmdSourcePath -DestinationPath $pmdDestPath -ReportType "PMD"

$cpdSourcePath = Join-Path -Path $ProjectRoot -ChildPath "target\cpd.xml"
$cpdDestPath = Join-Path -Path $pmdDir -ChildPath "cpd.xml"
Copy-ReportFiles -SourcePath $cpdSourcePath -DestinationPath $cpdDestPath -ReportType "PMD CPD"

# Copy Surefire reports
Write-Host "Collecting Surefire reports..."
$surefireSourcePath = Join-Path -Path $ProjectRoot -ChildPath "target\surefire-reports\*"
Copy-ReportFiles -SourcePath $surefireSourcePath -DestinationPath $surefireDir -ReportType "Surefire"

# Copy Failsafe reports
Write-Host "Collecting Failsafe reports..."
$failsafeSourcePath = Join-Path -Path $ProjectRoot -ChildPath "target\failsafe-reports\*"
Copy-ReportFiles -SourcePath $failsafeSourcePath -DestinationPath $failsafeDir -ReportType "Failsafe"

# Generate a summary of collected reports
$reportsSummary = @{
    JaCoCo = Test-Path (Join-Path -Path $jacocoDir -ChildPath "*")
    SpotBugs = Test-Path (Join-Path -Path $spotbugsDir -ChildPath "spotbugsXml.xml")
    PMD = Test-Path (Join-Path -Path $pmdDir -ChildPath "pmd.xml")
    PMD_CPD = Test-Path (Join-Path -Path $pmdDir -ChildPath "cpd.xml")
    Surefire = Test-Path (Join-Path -Path $surefireDir -ChildPath "*")
    Failsafe = Test-Path (Join-Path -Path $failsafeDir -ChildPath "*")
}

# Display summary
Write-Host "`nCollection Summary:"
Write-Host "----------------"
foreach ($report in $reportsSummary.Keys) {
    $status = if ($reportsSummary[$report]) { "✓" } else { "✗" }
    Write-Host "$report reports: $status"
}

Write-Host "`nAll quality reports have been collected in $OutputDir"
Write-Host "Collection process completed successfully."

# Return the output directory path for use by other scripts
return $OutputDir

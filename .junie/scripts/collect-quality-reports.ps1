# PowerShell script to collect quality reports into a unified directory
# This script should be run after 'mvn verify' to gather all quality reports

# Create the unified quality reports directory
$reportsDir = "target/quality-reports"
New-Item -ItemType Directory -Force -Path $reportsDir | Out-Null
Write-Host "Created unified quality reports directory: $reportsDir"

# Create subdirectories for each type of report
$jacocoDir = "$reportsDir/jacoco"
$spotbugsDir = "$reportsDir/spotbugs"
$pmdDir = "$reportsDir/pmd"
$surefireDir = "$reportsDir/surefire"
$failsafeDir = "$reportsDir/failsafe"

New-Item -ItemType Directory -Force -Path $jacocoDir | Out-Null
New-Item -ItemType Directory -Force -Path $spotbugsDir | Out-Null
New-Item -ItemType Directory -Force -Path $pmdDir | Out-Null
New-Item -ItemType Directory -Force -Path $surefireDir | Out-Null
New-Item -ItemType Directory -Force -Path $failsafeDir | Out-Null

# Copy JaCoCo reports
Write-Host "Collecting JaCoCo reports..."
if (Test-Path "target/site/jacoco") {
    Copy-Item -Path "target/site/jacoco/*" -Destination $jacocoDir -Recurse -Force
    Write-Host "JaCoCo reports copied to $jacocoDir"
} else {
    Write-Host "JaCoCo reports not found at target/site/jacoco"
}

# Copy SpotBugs reports
Write-Host "Collecting SpotBugs reports..."
if (Test-Path "target/spotbugsXml.xml") {
    Copy-Item -Path "target/spotbugsXml.xml" -Destination "$spotbugsDir/spotbugsXml.xml" -Force
    Write-Host "SpotBugs reports copied to $spotbugsDir"
} else {
    Write-Host "SpotBugs reports not found at target/spotbugsXml.xml"
}

# Copy PMD reports
Write-Host "Collecting PMD reports..."
if (Test-Path "target/pmd.xml") {
    Copy-Item -Path "target/pmd.xml" -Destination "$pmdDir/pmd.xml" -Force
    Write-Host "PMD reports copied to $pmdDir"
} else {
    Write-Host "PMD reports not found at target/pmd.xml"
}

if (Test-Path "target/cpd.xml") {
    Copy-Item -Path "target/cpd.xml" -Destination "$pmdDir/cpd.xml" -Force
    Write-Host "PMD CPD reports copied to $pmdDir"
} else {
    Write-Host "PMD CPD reports not found at target/cpd.xml"
}

# Copy Surefire reports
Write-Host "Collecting Surefire reports..."
if (Test-Path "target/surefire-reports") {
    Copy-Item -Path "target/surefire-reports/*" -Destination $surefireDir -Recurse -Force
    Write-Host "Surefire reports copied to $surefireDir"
} else {
    Write-Host "Surefire reports not found at target/surefire-reports"
}

# Copy Failsafe reports
Write-Host "Collecting Failsafe reports..."
if (Test-Path "target/failsafe-reports") {
    Copy-Item -Path "target/failsafe-reports/*" -Destination $failsafeDir -Recurse -Force
    Write-Host "Failsafe reports copied to $failsafeDir"
} else {
    Write-Host "Failsafe reports not found at target/failsafe-reports"
}

Write-Host "All quality reports have been collected in $reportsDir"
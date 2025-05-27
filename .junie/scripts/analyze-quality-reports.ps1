# PowerShell script to analyze quality reports and generate a summary
# This script should be run after collect-quality-reports.ps1
#
# Parameters:
#   -ReportsDir: Optional. The directory containing the quality reports. Default is "target\quality-reports"
#   -OutputFile: Optional. The file where the quality report summary will be written. Default is ".junie\quality-assessment.md"
#   -ProjectRoot: Optional. The root directory of the project. Default is determined from script location.
param (
    [string]$ReportsDir = "",
    [string]$OutputFile = "",
    [string]$ProjectRoot = ""
)

# Get the script directory and project root if not provided
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $ProjectRoot) {
    $ProjectRoot = Split-Path -Parent (Split-Path -Parent $scriptDir)
}

Write-Host "Starting quality report analysis..."
Write-Host "Project root: $ProjectRoot"

# Set the reports directory if not provided
if (-not $ReportsDir) {
    $ReportsDir = Join-Path -Path $ProjectRoot -ChildPath "target\quality-reports"
}

# Set the output file if not provided
if (-not $OutputFile) {
    $OutputFile = Join-Path -Path $ProjectRoot -ChildPath ".junie\quality-assessment.md"
}

# Check if the reports directory exists
if (-not (Test-Path $ReportsDir)) {
    Write-Host "Error: Quality reports directory not found at $ReportsDir"
    Write-Host "Please run 'mvn verify' and then 'collect-quality-reports.ps1' first."
    exit 1
}

# Create the output directory if it doesn't exist
try {
    $outputDir = Split-Path -Parent $OutputFile
    if (-not (Test-Path $outputDir)) {
        New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
        Write-Host "Created output directory: $outputDir"
    }
} catch {
    Write-Host "Error creating output directory: $_"
    exit 1
}

# Initialize counters and lists
$totalUnitTests = 0
$passedUnitTests = 0
$failedUnitTests = 0
$totalIntegrationTests = 0
$passedIntegrationTests = 0
$failedIntegrationTests = 0
$spotbugsWarnings = 0
$criticalSpotbugsWarnings = 0
$pmdViolations = 0
$jacocoCoverage = 0
$branchCoverage = 0
$lowCoverageFiles = @()
$spotbugsIssues = @()
$pmdIssues = @()

# Function to parse XML files
function Get-XmlContent {
    param (
        [string]$filePath
    )

    if (Test-Path $filePath) {
        [xml]$content = Get-Content $filePath
        return $content
    }
    return $null
}

# Function to analyze test reports
function Analyze-TestReports {
    param (
        [string]$ReportDir,
        [string]$TestType
    )

    $total = 0
    $failed = 0

    try {
        if (Test-Path $ReportDir) {
            $testSummaryFiles = Get-ChildItem -Path $ReportDir -Filter "TEST-*.xml"
            foreach ($testFile in $testSummaryFiles) {
                $xml = Get-XmlContent $testFile.FullName
                if ($xml -and $xml.testsuite) {
                    $total += [int]$xml.testsuite.tests
                    $failed += [int]$xml.testsuite.failures + [int]$xml.testsuite.errors
                }
            }
            $passed = $total - $failed
            Write-Host "$TestType tests: $passed passed, $failed failed (total: $total)"

            return @{
                Total = $total
                Passed = $passed
                Failed = $failed
            }
        } else {
            Write-Host "No $TestType test reports found at $ReportDir"
            return @{
                Total = 0
                Passed = 0
                Failed = 0
            }
        }
    } catch {
        Write-Host "Error analyzing $TestType test reports: $_"
        return @{
            Total = 0
            Passed = 0
            Failed = 0
        }
    }
}

# Analyze Surefire reports (unit tests)
Write-Host "Analyzing Surefire reports (unit tests)..."
$surefireDir = Join-Path -Path $ReportsDir -ChildPath "surefire"
$unitTestResults = Analyze-TestReports -ReportDir $surefireDir -TestType "Unit"
$totalUnitTests = $unitTestResults.Total
$passedUnitTests = $unitTestResults.Passed
$failedUnitTests = $unitTestResults.Failed

# Analyze Failsafe reports (integration tests)
Write-Host "Analyzing Failsafe reports (integration tests)..."
$failsafeDir = Join-Path -Path $ReportsDir -ChildPath "failsafe"
$integrationTestResults = Analyze-TestReports -ReportDir $failsafeDir -TestType "Integration"
$totalIntegrationTests = $integrationTestResults.Total
$passedIntegrationTests = $integrationTestResults.Passed
$failedIntegrationTests = $integrationTestResults.Failed

# Function to analyze SpotBugs reports
function Analyze-SpotBugsReports {
    param (
        [string]$ReportFile
    )

    $warnings = 0
    $criticalWarnings = 0
    $issues = @()

    try {
        if (Test-Path $ReportFile) {
            $xml = Get-XmlContent $ReportFile
            if ($xml -and $xml.BugCollection -and $xml.BugCollection.BugInstance) {
                $warnings = $xml.BugCollection.BugInstance.Count
                Write-Host "SpotBugs: $warnings warnings"

                # Count critical warnings and collect top issues
                foreach ($bug in $xml.BugCollection.BugInstance) {
                    if ($bug.priority -eq "1") {
                        $criticalWarnings++
                    }

                    # Collect top 5 issues
                    if ($issues.Count -lt 5) {
                        $className = $bug.Class.classname
                        $sourceLine = $bug.SourceLine.start
                        $bugType = $bug.type
                        $bugCategory = $bug.category
                        $issues += @{
                            ClassName = $className
                            SourceLine = $sourceLine
                            BugType = $bugType
                            Category = $bugCategory
                        }
                    }
                }
            } else {
                Write-Host "SpotBugs report has no bug instances or invalid format"
            }
        } else {
            Write-Host "SpotBugs report not found at $ReportFile"
        }
    } catch {
        Write-Host "Error analyzing SpotBugs report: $_"
    }

    return @{
        Warnings = $warnings
        CriticalWarnings = $criticalWarnings
        Issues = $issues
    }
}

# Analyze SpotBugs reports
Write-Host "Analyzing SpotBugs reports..."
$spotbugsFile = Join-Path -Path $ReportsDir -ChildPath "spotbugs\spotbugsXml.xml"
$spotbugsResults = Analyze-SpotBugsReports -ReportFile $spotbugsFile
$spotbugsWarnings = $spotbugsResults.Warnings
$criticalSpotbugsWarnings = $spotbugsResults.CriticalWarnings
$spotbugsIssues = $spotbugsResults.Issues

# Function to analyze PMD reports
function Analyze-PMDReports {
    param (
        [string]$ReportFile
    )

    $violations = 0
    $issues = @()

    try {
        if (Test-Path $ReportFile) {
            $xml = Get-XmlContent $ReportFile
            if ($xml -and $xml.pmd -and $xml.pmd.file) {
                foreach ($file in $xml.pmd.file) {
                    $violations += $file.violation.Count
                }
                Write-Host "PMD: $violations rule violations"

                # Collect top 5 PMD issues
                $issueCount = 0
                foreach ($file in $xml.pmd.file) {
                    foreach ($violation in $file.violation) {
                        if ($issueCount -lt 5) {
                            $fileName = $file.name.Split('\')[-1]
                            $ruleName = $violation.rule
                            $priority = $violation.priority
                            $beginLine = $violation.beginline
                            $issues += @{
                                FileName = $fileName
                                RuleName = $ruleName
                                Priority = $priority
                                Line = $beginLine
                            }
                            $issueCount++
                        }
                    }
                }
            } else {
                Write-Host "PMD report has no violations or invalid format"
            }
        } else {
            Write-Host "PMD report not found at $ReportFile"
        }
    } catch {
        Write-Host "Error analyzing PMD report: $_"
    }

    return @{
        Violations = $violations
        Issues = $issues
    }
}

# Analyze PMD reports
Write-Host "Analyzing PMD reports..."
$pmdFile = Join-Path -Path $ReportsDir -ChildPath "pmd\pmd.xml"
$pmdResults = Analyze-PMDReports -ReportFile $pmdFile
$pmdViolations = $pmdResults.Violations
$pmdIssues = $pmdResults.Issues

# Function to analyze JaCoCo reports
function Analyze-JaCoCoReports {
    param (
        [string]$ReportFile
    )

    $instructionCoverage = 0
    $branchCoverage = 0
    $lowCoverageFiles = @()

    try {
        if (Test-Path $ReportFile) {
            $xml = Get-XmlContent $ReportFile
            if ($xml -and $xml.report -and $xml.report.counter) {
                # Calculate instruction coverage
                $instructionCounter = $xml.report.counter | Where-Object { $_.type -eq "INSTRUCTION" }
                if ($instructionCounter) {
                    $covered = [int]$instructionCounter.covered
                    $missed = [int]$instructionCounter.missed
                    $total = $covered + $missed
                    if ($total -gt 0) {
                        $instructionCoverage = [math]::Round(($covered / $total) * 100, 2)
                        Write-Host "JaCoCo: $instructionCoverage% overall instruction coverage"
                    }
                }

                # Calculate branch coverage
                $branchCounter = $xml.report.counter | Where-Object { $_.type -eq "BRANCH" }
                if ($branchCounter) {
                    $covered = [int]$branchCounter.covered
                    $missed = [int]$branchCounter.missed
                    $total = $covered + $missed
                    if ($total -gt 0) {
                        $branchCoverage = [math]::Round(($covered / $total) * 100, 2)
                        Write-Host "JaCoCo: $branchCoverage% overall branch coverage"
                    }
                }

                # Find classes with low coverage
                foreach ($package in $xml.report.package) {
                    foreach ($class in $package.class) {
                        $classCounter = $class.counter | Where-Object { $_.type -eq "INSTRUCTION" }
                        if ($classCounter) {
                            $covered = [int]$classCounter.covered
                            $missed = [int]$classCounter.missed
                            $total = $covered + $missed
                            if ($total -gt 0) {
                                $coverage = [math]::Round(($covered / $total) * 100, 2)
                                if ($coverage -lt 70) {
                                    $className = $class.name.Split('/')[-1]
                                    $lowCoverageFiles += @{
                                        ClassName = "$className.java"
                                        Coverage = $coverage
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Write-Host "JaCoCo report has invalid format or missing data"
            }
        } else {
            Write-Host "JaCoCo report not found at $ReportFile"
        }
    } catch {
        Write-Host "Error analyzing JaCoCo report: $_"
    }

    return @{
        InstructionCoverage = $instructionCoverage
        BranchCoverage = $branchCoverage
        LowCoverageFiles = $lowCoverageFiles
    }
}

# Analyze JaCoCo reports
Write-Host "Analyzing JaCoCo reports..."
$jacocoFile = Join-Path -Path $ReportsDir -ChildPath "jacoco\jacoco.xml"
$jacocoResults = Analyze-JaCoCoReports -ReportFile $jacocoFile
$jacocoCoverage = $jacocoResults.InstructionCoverage
$branchCoverage = $jacocoResults.BranchCoverage
$lowCoverageFiles = $jacocoResults.LowCoverageFiles

# Generate the quality report summary
try {
    # Ensure the output directory exists
    $outputDir = Split-Path -Parent $OutputFile
    if (-not (Test-Path $outputDir)) {
        New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
        Write-Host "Created output directory: $outputDir"
    }
} catch {
    Write-Host "Error creating output directory: $_"
    exit 1
}

# Determine status icons
$unitTestsIcon = if ($failedUnitTests -eq 0) { "✅" } else { "🔴" }
$integrationTestsIcon = if ($failedIntegrationTests -eq 0) { "✅" } else { "🔴" }
$lineCoverageIcon = if ($jacocoCoverage -ge 70) { "✅" } else { "🔴" }
$branchCoverageIcon = if ($branchCoverage -ge 60) { "✅" } else { "⚠️" }

# Build the summary content
$summaryContent = @"
## Quality Snapshot (auto-updated)

**🧪 Tests**

* $unitTestsIcon Unit tests: $passedUnitTests passed / $failedUnitTests failed
* $integrationTestsIcon Integration tests: $passedIntegrationTests passed / $failedIntegrationTests failed

**📊 Coverage**

* $lineCoverageIcon Line coverage: $jacocoCoverage%
* $branchCoverageIcon Branch coverage: $branchCoverage% (target: 60%)
"@

# Add low coverage section if there are low coverage files
if ($lowCoverageFiles.Count -gt 0) {
    $summaryContent += @"

* 🔴 Low coverage in:

"@

    # Sort low coverage files by coverage (ascending)
    $sortedLowCoverageFiles = $lowCoverageFiles | Sort-Object -Property Coverage

    # Take top 2 files with lowest coverage
    $count = 0
    foreach ($file in $sortedLowCoverageFiles) {
        if ($count -lt 2) {
            $summaryContent += "  * $($file.ClassName) ($($file.Coverage)%)`n"
            $count++
        }
    }
}

$summaryContent += @"

**🔍 Static Analysis**

* SpotBugs: $spotbugsWarnings warnings ($criticalSpotbugsWarnings critical)
* PMD: $pmdViolations violations
"@

# Add PMD issues if there are any
if ($pmdIssues.Count -gt 0) {
    $summaryContent += "`n"

    # Process top 2 PMD issues
    $count = 0
    foreach ($issue in $pmdIssues) {
        if ($count -lt 2) {
            $description = $issue.RuleName

            # Add descriptive text based on rule name
            if ($issue.RuleName -match "DeepNestedIf") {
                $description = "Deep if nesting"
            } elseif ($issue.RuleName -match "TooManyFields") {
                $description = "Too many instance variables"
            } elseif ($issue.RuleName -match "CyclomaticComplexity") {
                $description = "High complexity"
            }

            $summaryContent += "  * $($issue.FileName) (line $($issue.Line)): $description`n"
            $count++
        }
    }
}

# Generate recommendations
$recommendations = @()

# Recommend tests for low coverage files
if ($lowCoverageFiles.Count -gt 0) {
    $lowestCoverageFile = $lowCoverageFiles | Sort-Object -Property Coverage | Select-Object -First 1

    if ($lowestCoverageFile.ClassName -match "OrderService") {
        $recommendations += "* Add unit tests for OrderService#getOpenOrdersByUser()"
    } elseif ($lowestCoverageFile.ClassName -match "PhotoManager") {
        $recommendations += "* Improve test coverage for PhotoManager class"
    } else {
        $recommendations += "* Add unit tests for $($lowestCoverageFile.ClassName)"
    }
}

# Recommend refactoring for PMD issues
if ($pmdIssues.Count -gt 0) {
    $firstPmdIssue = $pmdIssues[0]

    if ($firstPmdIssue.RuleName -match "DeepNestedIf" -and $firstPmdIssue.FileName -match "ReportExporter") {
        $recommendations += "* Refactor nested logic in ReportExporter"
    } elseif ($firstPmdIssue.RuleName -match "TooManyFields" -and $firstPmdIssue.FileName -match "CaptureController") {
        $recommendations += "* Consider breaking down CaptureController into smaller components"
    } else {
        $recommendations += "* Address code quality issues in $($firstPmdIssue.FileName)"
    }
}

# Add general recommendation about PMD rule suppression if needed
if ($pmdViolations -gt 10) {
    $recommendations += "* Review PMD rule suppression for ViewModel classes"
}

# If no specific recommendations, add general ones
if ($recommendations.Count -eq 0) {
    $recommendations += "* Continue maintaining high test coverage"
    $recommendations += "* Regularly review static analysis findings"
}

$summaryContent += @"

**🔁 Recommendations**

$($recommendations -join "`n")
"@

# Write the summary to the file
try {
    Set-Content -Path $OutputFile -Value $summaryContent
    Write-Host "Quality assessment report generated at $OutputFile"
} catch {
    Write-Host "Error writing quality assessment report: $_"
    exit 1
}

# Generate a timestamp for the report
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"

# Add a footer to the report
try {
    Add-Content -Path $OutputFile -Value "`n`n---`nReport generated on $timestamp"
    Write-Host "Report timestamp added"
} catch {
    Write-Host "Error adding timestamp to report: $_"
}

Write-Host "Quality assessment analysis completed successfully!"

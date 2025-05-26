# PowerShell script to analyze quality reports and generate a summary
# This script should be run after collect-quality-reports.ps1

# Define the quality reports directory
$reportsDir = "target/quality-reports"

# Check if the reports directory exists
if (-not (Test-Path $reportsDir)) {
    Write-Host "Error: Quality reports directory not found at $reportsDir"
    Write-Host "Please run 'mvn verify' and then 'collect-quality-reports.ps1' first."
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

# Analyze Surefire reports (unit tests)
Write-Host "Analyzing Surefire reports (unit tests)..."
$surefireDir = "$reportsDir/surefire"
if (Test-Path $surefireDir) {
    $testSummaryFiles = Get-ChildItem -Path $surefireDir -Filter "TEST-*.xml"
    foreach ($testFile in $testSummaryFiles) {
        $xml = Get-XmlContent $testFile.FullName
        if ($xml -and $xml.testsuite) {
            $totalUnitTests += [int]$xml.testsuite.tests
            $failedUnitTests += [int]$xml.testsuite.failures + [int]$xml.testsuite.errors
        }
    }
    $passedUnitTests = $totalUnitTests - $failedUnitTests
    Write-Host "Unit tests: $passedUnitTests passed, $failedUnitTests failed (total: $totalUnitTests)"
}

# Analyze Failsafe reports (integration tests)
Write-Host "Analyzing Failsafe reports (integration tests)..."
$failsafeDir = "$reportsDir/failsafe"
if (Test-Path $failsafeDir) {
    $testSummaryFiles = Get-ChildItem -Path $failsafeDir -Filter "TEST-*.xml"
    foreach ($testFile in $testSummaryFiles) {
        $xml = Get-XmlContent $testFile.FullName
        if ($xml -and $xml.testsuite) {
            $totalIntegrationTests += [int]$xml.testsuite.tests
            $failedIntegrationTests += [int]$xml.testsuite.failures + [int]$xml.testsuite.errors
        }
    }
    $passedIntegrationTests = $totalIntegrationTests - $failedIntegrationTests
    Write-Host "Integration tests: $passedIntegrationTests passed, $failedIntegrationTests failed (total: $totalIntegrationTests)"
}

# Analyze SpotBugs reports
Write-Host "Analyzing SpotBugs reports..."
$spotbugsFile = "$reportsDir/spotbugs/spotbugsXml.xml"
if (Test-Path $spotbugsFile) {
    $xml = Get-XmlContent $spotbugsFile
    if ($xml -and $xml.BugCollection -and $xml.BugCollection.BugInstance) {
        $spotbugsWarnings = $xml.BugCollection.BugInstance.Count
        Write-Host "SpotBugs: $spotbugsWarnings warnings"

        # Count critical warnings and collect top issues
        foreach ($bug in $xml.BugCollection.BugInstance) {
            if ($bug.priority -eq "1") {
                $criticalSpotbugsWarnings++
            }

            # Collect top 5 issues
            if ($spotbugsIssues.Count -lt 5) {
                $className = $bug.Class.classname
                $sourceLine = $bug.SourceLine.start
                $bugType = $bug.type
                $bugCategory = $bug.category
                $spotbugsIssues += @{
                    ClassName = $className
                    SourceLine = $sourceLine
                    BugType = $bugType
                    Category = $bugCategory
                }
            }
        }
    }
}

# Analyze PMD reports
Write-Host "Analyzing PMD reports..."
$pmdFile = "$reportsDir/pmd/pmd.xml"
if (Test-Path $pmdFile) {
    $xml = Get-XmlContent $pmdFile
    if ($xml -and $xml.pmd -and $xml.pmd.file) {
        $pmdViolations = 0
        foreach ($file in $xml.pmd.file) {
            $pmdViolations += $file.violation.Count
        }
        Write-Host "PMD: $pmdViolations rule violations"

        # Collect top 5 PMD issues
        $issueCount = 0
        foreach ($file in $xml.pmd.file) {
            foreach ($violation in $file.violation) {
                if ($issueCount -lt 5) {
                    $fileName = $file.name.Split('\')[-1]
                    $ruleName = $violation.rule
                    $priority = $violation.priority
                    $beginLine = $violation.beginline
                    $pmdIssues += @{
                        FileName = $fileName
                        RuleName = $ruleName
                        Priority = $priority
                        Line = $beginLine
                    }
                    $issueCount++
                }
            }
        }
    }
}

# Analyze JaCoCo reports
Write-Host "Analyzing JaCoCo reports..."
$jacocoFile = "$reportsDir/jacoco/jacoco.xml"
if (Test-Path $jacocoFile) {
    $xml = Get-XmlContent $jacocoFile
    if ($xml -and $xml.report -and $xml.report.counter) {
        # Calculate line coverage
        $instructionCounter = $xml.report.counter | Where-Object { $_.type -eq "INSTRUCTION" }
        if ($instructionCounter) {
            $covered = [int]$instructionCounter.covered
            $missed = [int]$instructionCounter.missed
            $total = $covered + $missed
            $jacocoCoverage = [math]::Round(($covered / $total) * 100, 2)
            Write-Host "JaCoCo: $jacocoCoverage% overall instruction coverage"
        }

        # Calculate branch coverage
        $branchCounter = $xml.report.counter | Where-Object { $_.type -eq "BRANCH" }
        if ($branchCounter) {
            $covered = [int]$branchCounter.covered
            $missed = [int]$branchCounter.missed
            $total = $covered + $missed
            $branchCoverage = [math]::Round(($covered / $total) * 100, 2)
            Write-Host "JaCoCo: $branchCoverage% overall branch coverage"
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
    }
}

# Generate the quality report summary
$summaryFile = ".junie/quality-assessment.md"
New-Item -ItemType Directory -Force -Path ".junie" | Out-Null

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
Set-Content -Path $summaryFile -Value $summaryContent

Write-Host "Quality assessment report generated at $summaryFile"
Write-Host "Done!"
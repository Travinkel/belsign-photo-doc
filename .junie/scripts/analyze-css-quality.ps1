# PowerShell script to analyze CSS files for duplicate code and other quality issues
# This script should be run as part of the quality assessment process
#
# Parameters:
#   -CssDir: Optional. The directory containing the CSS files to analyze. Default is "src\main\resources\com\belman\assets\styles"
#   -OutputDir: Optional. The directory where the CSS quality report will be saved. Default is "target\quality-reports\css"
#   -ReportFile: Optional. The file where the CSS quality report will be written. Default is ".junie\css-quality-assessment.md"
#   -ProjectRoot: Optional. The root directory of the project. Default is determined from script location.
param (
    [string]$CssDir = "",
    [string]$OutputDir = "",
    [string]$ReportFile = "",
    [string]$ProjectRoot = ""
)

# Get the script directory and project root if not provided
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $ProjectRoot) {
    $ProjectRoot = Split-Path -Parent (Split-Path -Parent $scriptDir)
}

Write-Host "Starting CSS quality analysis..."
Write-Host "Project root: $ProjectRoot"

# Set the CSS directory if not provided
if (-not $CssDir) {
    $CssDir = Join-Path -Path $ProjectRoot -ChildPath "src\main\resources\com\belman\assets\styles"
}

# Set the output directory if not provided
if (-not $OutputDir) {
    $OutputDir = Join-Path -Path $ProjectRoot -ChildPath "target\quality-reports\css"
}

# Set the report file if not provided
if (-not $ReportFile) {
    $ReportFile = Join-Path -Path $ProjectRoot -ChildPath ".junie\css-quality-assessment.md"
}

# Check if the CSS directory exists
if (-not (Test-Path $CssDir)) {
    Write-Host "Error: CSS directory not found at $CssDir"
    exit 1
}

# Get all CSS files
try {
    $cssFiles = Get-ChildItem -Path $CssDir -Filter "*.css"
    $fileCount = $cssFiles.Count
    if ($fileCount -eq 0) {
        Write-Host "Warning: No CSS files found in $CssDir"
    } else {
        Write-Host "Found $fileCount CSS files to analyze"
    }
} catch {
    Write-Host "Error getting CSS files: $_"
    exit 1
}

# Create a directory for CSS quality reports
try {
    New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null
    Write-Host "Created CSS quality reports directory: $OutputDir"
} catch {
    Write-Host "Error creating CSS quality reports directory: $_"
    exit 1
}

# Initialize counters and lists
$totalCssRules = 0
$totalCssSelectors = 0
$totalCssFiles = $cssFiles.Count
$duplicateRules = @()
$complexSelectors = @()
$overspecificSelectors = @()
$emptyCssBlocks = 0
$importantUsage = 0
$inlineStyles = 0

# Function to parse CSS content and extract rules
function Get-CssRules {
    param (
        [string]$content,
        [ref]$emptyBlocksCount,
        [ref]$importantUsageCount
    )

    try {
        # Remove comments
        $content = $content -replace "/\*.*?\*/", ""

        # Extract rules (simple regex approach)
        $rules = @()
        $matches = [regex]::Matches($content, "([^{]+)\s*{\s*([^}]*)\s*}")

        foreach ($match in $matches) {
            $selector = $match.Groups[1].Value.Trim()
            $declaration = $match.Groups[2].Value.Trim()

            # Skip empty rules
            if ($declaration -eq "") {
                $emptyBlocksCount.Value++
                continue
            }

            $rules += @{
                Selector = $selector
                Declaration = $declaration
            }

            # Count !important usage
            $importantCount = ([regex]::Matches($declaration, "!important")).Count
            $importantUsageCount.Value += $importantCount
        }

        return $rules
    } catch {
        Write-Host "Error parsing CSS content: $_"
        return @()
    }
}

# Function to check for duplicate rules
function Find-DuplicateRules {
    param (
        [array]$allRules,
        [int]$minDeclarationLength = 20
    )

    try {
        $declarationMap = @{}
        $duplicates = @()

        foreach ($rule in $allRules) {
            $declaration = $rule.Declaration
            $selector = $rule.Selector
            $file = $rule.File

            if ($declarationMap.ContainsKey($declaration)) {
                $declarationMap[$declaration] += @{
                    Selector = $selector
                    File = $file
                }
            } else {
                $declarationMap[$declaration] = @(@{
                    Selector = $selector
                    File = $file
                })
            }
        }

        foreach ($declaration in $declarationMap.Keys) {
            # Only consider declarations that appear multiple times and are sufficiently complex
            if ($declarationMap[$declaration].Count -gt 1 -and $declaration.Length -gt $minDeclarationLength) {
                $duplicates += @{
                    Declaration = $declaration
                    Occurrences = $declarationMap[$declaration]
                }
            }
        }

        return $duplicates
    } catch {
        Write-Host "Error finding duplicate rules: $_"
        return @()
    }
}

# Function to check for complex selectors
function Find-ComplexSelectors {
    param (
        [array]$allRules,
        [int]$maxNestingLevel = 3,
        [int]$maxIdSelectors = 2
    )

    try {
        $complex = @()

        foreach ($rule in $allRules) {
            $selector = $rule.Selector

            # Check for selectors with excessive nesting
            $nestingLevel = ($selector -split "\s+").Count
            if ($nestingLevel -gt $maxNestingLevel) {
                $complex += @{
                    Selector = $selector
                    File = $rule.File
                    NestingLevel = $nestingLevel
                    Reason = "Excessive nesting"
                }
            }

            # Check for selectors with too many ID selectors
            $idCount = ([regex]::Matches($selector, "#")).Count
            if ($idCount -gt $maxIdSelectors) {
                # Only add if not already added for nesting
                if (-not ($complex | Where-Object { $_.Selector -eq $selector -and $_.File -eq $rule.File })) {
                    $complex += @{
                        Selector = $selector
                        File = $rule.File
                        IdCount = $idCount
                        Reason = "Too many ID selectors"
                    }
                }
            }
        }

        return $complex
    } catch {
        Write-Host "Error finding complex selectors: $_"
        return @()
    }
}

# Function to check for overspecific selectors
function Find-OverspecificSelectors {
    param (
        [array]$allRules,
        [int]$maxCombinedSelectors = 4
    )

    try {
        $overspecific = @()

        foreach ($rule in $allRules) {
            $selector = $rule.Selector

            # Check for selectors with too many class or element selectors
            $classCount = ([regex]::Matches($selector, "\.")).Count
            $elementCount = ([regex]::Matches($selector, "^[a-z]|\s+[a-z]")).Count
            $totalCount = $classCount + $elementCount

            if ($totalCount -gt $maxCombinedSelectors) {
                # Only add if not already identified as complex
                if (-not ($overspecific | Where-Object { $_.Selector -eq $selector -and $_.File -eq $rule.File })) {
                    $overspecific += @{
                        Selector = $selector
                        File = $rule.File
                        ClassCount = $classCount
                        ElementCount = $elementCount
                        TotalCount = $totalCount
                    }
                }
            }
        }

        return $overspecific
    } catch {
        Write-Host "Error finding overspecific selectors: $_"
        return @()
    }
}

# Function to analyze a CSS file
function Analyze-CssFile {
    param (
        [System.IO.FileInfo]$cssFile,
        [ref]$totalRules,
        [ref]$totalSelectors,
        [ref]$emptyBlocks,
        [ref]$importantUsage
    )

    try {
        Write-Host "Analyzing $($cssFile.Name)..."
        $content = Get-Content -Path $cssFile.FullName -Raw -ErrorAction Stop

        # Extract rules
        $rules = Get-CssRules -content $content -emptyBlocksCount $emptyBlocks -importantUsageCount $importantUsage

        # Add file information to each rule
        foreach ($rule in $rules) {
            $rule.File = $cssFile.Name
        }

        # Count selectors and rules
        $totalRules.Value += $rules.Count
        $selectorCount = ($rules | ForEach-Object { ($_.Selector -split ",").Count } | Measure-Object -Sum).Sum
        $totalSelectors.Value += $selectorCount

        return $rules
    } catch {
        Write-Host "Error analyzing CSS file $($cssFile.Name): $_"
        return @()
    }
}

# Analyze each CSS file
$allRules = @()
$emptyCssBlocks = 0
$importantUsage = 0
$totalCssRules = 0
$totalCssSelectors = 0

foreach ($cssFile in $cssFiles) {
    $fileRules = Analyze-CssFile -cssFile $cssFile -totalRules ([ref]$totalCssRules) -totalSelectors ([ref]$totalCssSelectors) -emptyBlocks ([ref]$emptyCssBlocks) -importantUsage ([ref]$importantUsage)
    $allRules += $fileRules
}

# Find duplicate rules
$duplicateRules = Find-DuplicateRules -allRules $allRules

# Find complex selectors
$complexSelectors = Find-ComplexSelectors -allRules $allRules

# Find overspecific selectors
$overspecificSelectors = Find-OverspecificSelectors -allRules $allRules

# Function to generate CSS quality report
function New-CssQualityReport {
    param (
        [int]$totalFiles,
        [int]$totalRules,
        [int]$totalSelectors,
        [int]$emptyBlocks,
        [int]$importantUsage,
        [array]$duplicateRules,
        [array]$complexSelectors,
        [array]$overspecificSelectors
    )

    try {
        # Build the report content
        $reportContent = @"
# CSS Quality Report

## Summary

- **Total CSS Files:** $totalFiles
- **Total CSS Rules:** $totalRules
- **Total CSS Selectors:** $totalSelectors
- **Empty CSS Blocks:** $emptyBlocks
- **!important Usage:** $importantUsage

## Duplicate Rules

Found $($duplicateRules.Count) potential duplicate rule sets:

"@

        return $reportContent
    } catch {
        Write-Host "Error generating CSS quality report: $_"
        return "# CSS Quality Report\n\nError generating report: $_"
    }
}

# Generate the CSS quality report
$reportFile = Join-Path -Path $OutputDir -ChildPath "css-quality-report.md"

# Build the report content
$reportContent = New-CssQualityReport -totalFiles $cssFiles.Count -totalRules $totalCssRules -totalSelectors $totalCssSelectors -emptyBlocks $emptyCssBlocks -importantUsage $importantUsage -duplicateRules $duplicateRules -complexSelectors $complexSelectors -overspecificSelectors $overspecificSelectors

if ($duplicateRules.Count -gt 0) {
    foreach ($duplicate in $duplicateRules | Select-Object -First 5) {
        $reportContent += @"

### Duplicate Declaration:
```css
{
$($duplicate.Declaration)
}
```

Occurs in:
"@
        foreach ($occurrence in $duplicate.Occurrences) {
            $file = $occurrence.File
            $selector = $occurrence.Selector
            $reportContent += "`n- $file`: $selector"
        }
    }

    if ($duplicateRules.Count -gt 5) {
        $reportContent += @"

... and $($duplicateRules.Count - 5) more duplicate rule sets.
"@
    }
} else {
    $reportContent += @"

No duplicate rules found.
"@
}

$reportContent += @"

## Complex Selectors

Found $($complexSelectors.Count) complex selectors:

"@

if ($complexSelectors.Count -gt 0) {
    foreach ($complex in $complexSelectors | Select-Object -First 5) {
        $file = $complex.File
        $selector = $complex.Selector
        $reportContent += "`n- $file`: $selector"

        if ($complex.NestingLevel) {
            $reportContent += " (Nesting level: $($complex.NestingLevel))"
        }
        if ($complex.IdCount) {
            $reportContent += " (ID selectors: $($complex.IdCount))"
        }
    }

    if ($complexSelectors.Count -gt 5) {
        $reportContent += @"

... and $($complexSelectors.Count - 5) more complex selectors.
"@
    }
} else {
    $reportContent += @"

No complex selectors found.
"@
}

$reportContent += @"

## Overspecific Selectors

Found $($overspecificSelectors.Count) overspecific selectors:

"@

if ($overspecificSelectors.Count -gt 0) {
    foreach ($overspecific in $overspecificSelectors | Select-Object -First 5) {
        $file = $overspecific.File
        $selector = $overspecific.Selector
        $classCount = $overspecific.ClassCount
        $elementCount = $overspecific.ElementCount
        $reportContent += "`n- $file`: $selector (Classes: $classCount, Elements: $elementCount)"
    }

    if ($overspecificSelectors.Count -gt 5) {
        $reportContent += @"

... and $($overspecificSelectors.Count - 5) more overspecific selectors.
"@
    }
} else {
    $reportContent += @"

No overspecific selectors found.
"@
}

$reportContent += @"

## Recommendations

"@

# Add recommendations based on findings
$recommendations = @()

if ($duplicateRules.Count -gt 0) {
    $recommendations += "- Consider refactoring duplicate CSS rules into shared classes or variables"
}

if ($complexSelectors.Count -gt 0) {
    $recommendations += "- Simplify complex selectors to improve CSS specificity and maintainability"
}

if ($overspecificSelectors.Count -gt 0) {
    $recommendations += "- Reduce specificity of overspecific selectors to improve CSS maintainability"
}

if ($emptyCssBlocks -gt 0) {
    $recommendations += "- Remove empty CSS blocks to reduce file size"
}

if ($importantUsage -gt 0) {
    $recommendations += "- Minimize use of !important to maintain proper CSS specificity"
}

if ($recommendations.Count -eq 0) {
    $recommendations += "- CSS code quality looks good, continue maintaining clean CSS"
}

$reportContent += $recommendations -join "`n"

# Write the report to the file
Set-Content -Path $reportFile -Value $reportContent

# Function to save and distribute the report
function Save-CssQualityReport {
    param (
        [string]$reportContent,
        [string]$reportFile,
        [string]$mainReportFile,
        [string]$summaryReportFile
    )

    try {
        # Write the report to the file
        Set-Content -Path $reportFile -Value $reportContent -Force
        Write-Host "CSS quality assessment report generated at $reportFile"

        # Copy the report to the main quality assessment directory
        Copy-Item -Path $reportFile -Destination $mainReportFile -Force
        Write-Host "CSS quality assessment report copied to $mainReportFile"

        # Create the reports directory if it doesn't exist
        $reportsDir = Split-Path -Parent $summaryReportFile
        if (-not (Test-Path $reportsDir)) {
            New-Item -ItemType Directory -Force -Path $reportsDir | Out-Null
            Write-Host "Created reports directory: $reportsDir"
        }

        # Copy the report to the reports directory
        Copy-Item -Path $reportFile -Destination $summaryReportFile -Force
        Write-Host "CSS quality assessment report copied to $summaryReportFile"

        return $true
    } catch {
        Write-Host "Error saving CSS quality report: $_"
        return $false
    }
}

# Save and distribute the report
$mainReportFile = $ReportFile
$reportsDir = Join-Path -Path $ProjectRoot -ChildPath ".junie\reports"
$reportsSummaryFile = Join-Path -Path $reportsDir -ChildPath "css-quality-summary.md"

# Generate a timestamp for the report
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$reportContent += "`n`n---`nReport generated on $timestamp"

$success = Save-CssQualityReport -reportContent $reportContent -reportFile $reportFile -mainReportFile $mainReportFile -summaryReportFile $reportsSummaryFile

if ($success) {
    Write-Host "CSS quality assessment completed successfully!"
} else {
    Write-Host "CSS quality assessment completed with errors."
    exit 1
}

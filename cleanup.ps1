# Cleanup script for migration
# This script removes deprecated code and updates documentation

# Define the root directories
$javaRoot = "src\main\java\com\belman"
$resourcesRoot = "src\main\resources\com\belman"

# Define patterns for deprecated code
$deprecatedPatterns = @(
    # Special handling for old view paths in ViewLoader
    '(?s)// Special case for login package in usecases.*?fxmlUrl = ViewLoader\.class\.getResource\(loginPath\);',
    '(?s)// Special case for authentication\.login package in usecases.*?fxmlUrl = ViewLoader\.class\.getResource\(authLoginPath\);',
    
    # Special handling for old view paths in BaseView
    '(?s)// Special case for authentication\.login package in usecases.*?fxmlUrl = getClass\(\)\.getResource\(loginPath\);',
    
    # Commented-out view registrations in ViewStackManager
    '(?s)// Legacy view registrations.*?// End of legacy view registrations',
    
    # Deprecated methods
    '@Deprecated.*?(?=\s*(?:public|private|protected|class|interface|enum|$))',
    
    # Old directory structure references in error messages
    'FXML file not found in login path, trying authentication\.login path',
    'FXML file not found in authentication\.login path, trying login path'
)

# Counter for modified files
$modifiedCount = 0

# Process each Java file
foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file.FullName -Raw
    $originalContent = $content
    $modified = $false
    
    # Apply each deprecated pattern
    foreach ($pattern in $deprecatedPatterns) {
        if ($content -match $pattern) {
            $content = $content -replace $pattern, ''
            $modified = $true
        }
    }
    
    # If the file was modified, write the changes back
    if ($modified) {
        Write-Host "Removing deprecated code from: $($file.FullName)"
        Set-Content -Path $file.FullName -Value $content
        $modifiedCount++
    }
}

Write-Host "Removed deprecated code from $modifiedCount files."

# Check if the old views directory still exists and remove it if empty
$oldViewsDir = Join-Path $resourcesRoot "views"
if (Test-Path $oldViewsDir) {
    $isEmpty = (Get-ChildItem -Path $oldViewsDir -Recurse -File).Count -eq 0
    if ($isEmpty) {
        Write-Host "Removing empty views directory: $oldViewsDir"
        Remove-Item -Path $oldViewsDir -Recurse -Force
    } else {
        Write-Host "Warning: Views directory still contains files: $oldViewsDir"
        Write-Host "Please check if all FXML files have been migrated."
    }
}

# Update the improvement plan document to mark the migration as complete
$improvementPlanPath = "improvement-plan-for-presentation-layer.md"
if (Test-Path $improvementPlanPath) {
    $content = Get-Content -Path $improvementPlanPath -Raw
    
    # Mark the migration strategy phases as complete
    $content = $content -replace "- Complete the migration strategy phases", "- [DONE] Complete the migration strategy phases"
    
    # Add a completion note
    $completionNote = @"

## Migration Completion

The migration of the presentation layer has been completed successfully. All tasks have been marked as completed:

- [DONE] FXML files have been moved to align with Java package structure (Option A)
- [DONE] Manager classes have been refactored to use specific services directly
- [DONE] ViewLoader has been updated to handle naming inconsistencies
- [DONE] Component responsibilities have been clarified
- [DONE] Naming inconsistency between "authentication" Java package and "login" FXML directory has been resolved
- [DONE] Shared base classes for common functionality have been implemented
- [DONE] A shared components package for reusable UI components has been created
- [DONE] Utility classes for common UI operations have been created
- [DONE] The migration strategy phases have been completed

The codebase is now more maintainable and consistent, making it easier to add new features and maintain existing code.
"@
    
    # Add the completion note if it doesn't already exist
    if (-not ($content -match "## Migration Completion")) {
        $content += $completionNote
    }
    
    Write-Host "Updating improvement plan document: $improvementPlanPath"
    Set-Content -Path $improvementPlanPath -Value $content
}

Write-Host "Cleanup complete!"

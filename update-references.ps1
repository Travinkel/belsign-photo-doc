# Script for updating FXML references
# This script updates references to FXML files in the codebase

# Define the root directory for Java files
$javaRoot = "src\main\java\com\belman"

# Define the mapping of old paths to new paths
$pathMapping = @{
    "/com/belman/presentation/views/admin/dashboard/" = "/com/belman/presentation/usecases/admin/dashboard/"
    "/com/belman/presentation/views/admin/usermanagement/" = "/com/belman/presentation/usecases/admin/usermanagement/"
    "/com/belman/presentation/views/qa/dashboard/" = "/com/belman/presentation/usecases/qa/dashboard/"
    "/com/belman/presentation/views/qa/review/" = "/com/belman/presentation/usecases/qa/review/"
    "/com/belman/presentation/views/qa/done/" = "/com/belman/presentation/usecases/qa/done/"
    "/com/belman/presentation/views/worker/dashboard/" = "/com/belman/presentation/usecases/worker/dashboard/"
    "/com/belman/presentation/views/worker/photocube/" = "/com/belman/presentation/usecases/worker/photocube/"
    "/com/belman/presentation/views/worker/capture/" = "/com/belman/presentation/usecases/worker/capture/"
    "/com/belman/presentation/views/worker/summary/" = "/com/belman/presentation/usecases/worker/summary/"
    "/com/belman/presentation/views/login/" = "/com/belman/presentation/usecases/authentication/login/"
    # Add the missing mapping for usecases/login to usecases/authentication/login
    "/com/belman/presentation/usecases/login/" = "/com/belman/presentation/usecases/authentication/login/"
}

# Find all Java files
$javaFiles = Get-ChildItem -Path $javaRoot -Filter "*.java" -Recurse

# Counter for modified files
$modifiedCount = 0

# Process each Java file
foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file.FullName -Raw
    $originalContent = $content
    $modified = $false
    
    # Apply each path mapping
    foreach ($mapping in $pathMapping.GetEnumerator()) {
        if ($content -match [regex]::Escape($mapping.Key)) {
            $content = $content -replace [regex]::Escape($mapping.Key), $mapping.Value
            $modified = $true
        }
    }
    
    # If the file was modified, write the changes back
    if ($modified) {
        Write-Host "Updating references in: $($file.FullName)"
        Set-Content -Path $file.FullName -Value $content
        $modifiedCount++
    }
}

Write-Host "Updated references in $modifiedCount files."

# Now find all FXML files and update references within them
$fxmlFiles = Get-ChildItem -Path "src\main\resources" -Filter "*.fxml" -Recurse

# Counter for modified FXML files
$modifiedFxmlCount = 0

# Process each FXML file
foreach ($file in $fxmlFiles) {
    $content = Get-Content -Path $file.FullName -Raw
    $originalContent = $content
    $modified = $false
    
    # Apply each path mapping
    foreach ($mapping in $pathMapping.GetEnumerator()) {
        if ($content -match [regex]::Escape($mapping.Key)) {
            $content = $content -replace [regex]::Escape($mapping.Key), $mapping.Value
            $modified = $true
        }
    }
    
    # If the file was modified, write the changes back
    if ($modified) {
        Write-Host "Updating references in FXML: $($file.FullName)"
        Set-Content -Path $file.FullName -Value $content
        $modifiedFxmlCount++
    }
}

Write-Host "Updated references in $modifiedFxmlCount FXML files."
Write-Host "Reference update complete!"
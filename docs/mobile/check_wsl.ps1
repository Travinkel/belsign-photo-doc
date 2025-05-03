# Check if WSL is installed and available
# This script can be used to verify that WSL is properly set up before attempting to build for Android

Write-Host "Checking WSL installation..." -ForegroundColor Cyan

# Check if WSL is installed
try {
    $wslCheck = wsl --status 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "WSL is installed." -ForegroundColor Green
    } else {
        Write-Host "WSL is not properly installed or configured." -ForegroundColor Red
        Write-Host "Please follow the instructions in docs\mobile\wsl_setup_guide.md to set up WSL." -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "Error checking WSL status: $_" -ForegroundColor Red
    Write-Host "WSL might not be installed or accessible." -ForegroundColor Red
    Write-Host "Please follow the instructions in docs\mobile\wsl_setup_guide.md to set up WSL." -ForegroundColor Yellow
    exit 1
}

# Check WSL version
try {
    $wslVersion = wsl --version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "WSL version information:" -ForegroundColor Green
        Write-Host $wslVersion -ForegroundColor Gray
    } else {
        Write-Host "Could not determine WSL version." -ForegroundColor Yellow
        Write-Host "This might indicate an older version of WSL." -ForegroundColor Yellow
        Write-Host "Please make sure you have WSL2 installed." -ForegroundColor Yellow
    }
} catch {
    Write-Host "Error checking WSL version: $_" -ForegroundColor Red
    Write-Host "This might indicate an older version of WSL." -ForegroundColor Yellow
    Write-Host "Please make sure you have WSL2 installed." -ForegroundColor Yellow
}

# Check if Ubuntu is installed
try {
    $distros = wsl --list --verbose 2>&1
    if ($distros -match "Ubuntu") {
        Write-Host "Ubuntu is installed in WSL." -ForegroundColor Green
    } else {
        Write-Host "Ubuntu does not appear to be installed in WSL." -ForegroundColor Red
        Write-Host "Please follow the instructions in docs\mobile\wsl_setup_guide.md to install Ubuntu." -ForegroundColor Yellow
    }
} catch {
    Write-Host "Error checking WSL distributions: $_" -ForegroundColor Red
}

# Check if we can run commands in WSL
try {
    $javaCheck = wsl -d Ubuntu -e java -version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Successfully executed a command in WSL." -ForegroundColor Green
        Write-Host "Java version in WSL:" -ForegroundColor Green
        Write-Host $javaCheck -ForegroundColor Gray
    } else {
        Write-Host "Could not execute a command in WSL." -ForegroundColor Red
        Write-Host "Please make sure Ubuntu is properly set up in WSL." -ForegroundColor Yellow
    }
} catch {
    Write-Host "Error executing command in WSL: $_" -ForegroundColor Red
    Write-Host "Please make sure Ubuntu is properly set up in WSL." -ForegroundColor Yellow
}

Write-Host "`nWSL check completed." -ForegroundColor Cyan
Write-Host "If all checks passed, you should be able to build for Android using WSL." -ForegroundColor Cyan
Write-Host "If any checks failed, please follow the instructions in docs\mobile\wsl_setup_guide.md to set up WSL." -ForegroundColor Cyan

# Instructions for building for Android using WSL
Write-Host "`nTo build for Android using WSL, run the following commands:" -ForegroundColor Cyan
Write-Host "1. Open Ubuntu from the Start menu" -ForegroundColor White
Write-Host "2. Navigate to your project directory:" -ForegroundColor White
Write-Host "   cd /mnt/c/path/to/your/project" -ForegroundColor Gray
Write-Host "3. Build the project for Android:" -ForegroundColor White
Write-Host "   mvn gluonfx:build -Pandroid" -ForegroundColor Gray
Write-Host "4. Package the application:" -ForegroundColor White
Write-Host "   mvn gluonfx:package -Pandroid" -ForegroundColor Gray
Write-Host "5. Install the application on a connected device:" -ForegroundColor White
Write-Host "   mvn gluonfx:install -Pandroid" -ForegroundColor Gray
Write-Host "6. Run the application on a connected device:" -ForegroundColor White
Write-Host "   mvn gluonfx:nativerun -Pandroid" -ForegroundColor Gray
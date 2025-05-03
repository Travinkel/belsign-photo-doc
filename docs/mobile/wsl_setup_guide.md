# WSL Setup Guide for Android Builds

## Overview

This guide provides instructions for setting up Windows Subsystem for Linux (WSL2) to build Android applications with GluonFX on Windows.

The error message "We currently can't compile to aarch64-linux-android when running on x86_64-microsoft-windows" indicates that GluonFX cannot directly cross-compile for Android on Windows. This is a known limitation, and the solution is to use WSL2 (Windows Subsystem for Linux) for building Android applications on Windows.

## Prerequisites

- Windows 10 version 2004 or higher (Build 19041 or higher) or Windows 11
- Administrator access to your Windows machine
- At least 8GB of RAM (16GB recommended)
- At least 50GB of free disk space

## Step 1: Install WSL2

1. Open PowerShell as Administrator
2. Run the following command to install WSL2 with Ubuntu as the default distribution:

```powershell
wsl --install
```

3. Restart your computer when prompted
4. After restart, Ubuntu will automatically start and ask you to create a username and password
5. Complete the setup process

## Step 2: Update Ubuntu

1. Open Ubuntu from the Start menu
2. Update the package lists and upgrade installed packages:

```bash
sudo apt update
sudo apt upgrade -y
```

## Step 3: Install Java Development Kit (JDK) 21

1. Install the necessary dependencies:

```bash
sudo apt install -y wget apt-transport-https gnupg
```

2. Add the Adoptium repository:

```bash
wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo apt-key add -
echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list
```

3. Update the package lists:

```bash
sudo apt update
```

4. Install JDK 21:

```bash
sudo apt install -y temurin-21-jdk
```

5. Verify the installation:

```bash
java -version
```

## Step 4: Install Maven

1. Install Maven:

```bash
sudo apt install -y maven
```

2. Verify the installation:

```bash
mvn -version
```

## Step 5: Install Android SDK

1. Install the necessary dependencies:

```bash
sudo apt install -y unzip lib32z1 lib32stdc++6
```

2. Create a directory for the Android SDK:

```bash
mkdir -p ~/android/sdk
```

3. Download the Android SDK command-line tools:

```bash
cd ~/android
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
```

4. Unzip the downloaded file:

```bash
unzip commandlinetools-linux-9477386_latest.zip -d sdk
```

5. Move the tools to the correct directory:

```bash
mkdir -p ~/android/sdk/cmdline-tools/latest
mv ~/android/sdk/cmdline-tools/* ~/android/sdk/cmdline-tools/latest/
```

6. Set up environment variables:

```bash
echo 'export ANDROID_HOME=$HOME/android/sdk' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools' >> ~/.bashrc
source ~/.bashrc
```

7. Install the required Android SDK components:

```bash
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2" "ndk;25.2.9519653"
```

## Step 6: Install GraalVM

1. Download GraalVM for JDK 21:

```bash
cd ~
wget https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-21.0.2/graalvm-community-jdk-21.0.2_linux-x64_bin.tar.gz
```

2. Extract the archive:

```bash
tar -xzf graalvm-community-jdk-21.0.2_linux-x64_bin.tar.gz
```

3. Set up environment variables:

```bash
echo 'export GRAALVM_HOME=$HOME/graalvm-community-openjdk-21.0.2+13.1' >> ~/.bashrc
echo 'export PATH=$GRAALVM_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

4. Verify the installation:

```bash
java -version
```

## Step 7: Clone and Build the Project

1. Clone the project repository:

```bash
cd ~
git clone <repository-url>
cd <repository-directory>
```

2. Build the project for Android:

```bash
mvn gluonfx:build -Pandroid
```

3. Package the application:

```bash
mvn gluonfx:package -Pandroid
```

4. The APK file will be generated in the `target/gluonfx/aarch64-android/gvm` directory

## Step 8: Install and Run the Application on a Connected Device

1. Connect your Android device to your computer via USB
2. Enable USB debugging on your device
3. Install the application:

```bash
mvn gluonfx:install -Pandroid
```

4. Run the application:

```bash
mvn gluonfx:nativerun -Pandroid
```

## Troubleshooting

### Common Issues

1. **WSL2 Installation Fails**
   - Ensure virtualization is enabled in your BIOS/UEFI settings
   - Make sure you're running Windows 10 version 2004 or higher
   - Run `wsl --update` to update WSL to the latest version

2. **Java Installation Issues**
   - If you encounter issues with the Adoptium repository, you can download the JDK directly from the [Adoptium website](https://adoptium.net/)

3. **Android SDK Installation Issues**
   - If you encounter issues with the sdkmanager, try running it with the `--verbose` flag to get more detailed error messages
   - Ensure you have accepted all licenses with `yes | sdkmanager --licenses`

4. **Build Failures**
   - Check that all environment variables are set correctly
   - Ensure you have enough disk space
   - Check the logs for specific error messages

### Accessing Windows Files from WSL

You can access your Windows files from WSL at `/mnt/c/` (for the C: drive). This can be useful for sharing files between Windows and WSL.

### Accessing WSL Files from Windows

You can access your WSL files from Windows using the `\\wsl$\Ubuntu` path in File Explorer. This can be useful for editing files with Windows applications.

## Additional Resources

- [Microsoft WSL Documentation](https://docs.microsoft.com/en-us/windows/wsl/)
- [Gluon Mobile Documentation](https://docs.gluonhq.com/)
- [GraalVM Documentation](https://www.graalvm.org/docs/)
- [Android SDK Documentation](https://developer.android.com/studio/command-line)
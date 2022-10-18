echo 'test123'
exit 0

# sudo apt -y install openjdk-11-jdk-headless

export CMDLINE_VERSION="8512546"
wget "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_VERSION}_latest.zip"
mkdir -p Android/Sdk
unzip "commandlinetools-linux-${CMDLINE_VERSION}_latest.zip" -d Android/Sdk

export ANDROID_HOME=$(pwd)/Android/Sdk/cmdline-tools
export PATH="$ANDROID_HOME/bin:$ANDROID_HOME/lib:$ANDROID_HOME/emulator:$ANDROID_HOME/patcher:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH"

yes | sdkmanager --update --sdk_root=${ANDROID_HOME}
sdkmanager --list --sdk_root=${ANDROID_HOME} | grep "build-tools"
yes | sdkmanager "build-tools;33.0.0" "platforms;android-33" "sources;android-33" --sdk_root=${ANDROID_HOME}
yes | sdkmanager --licenses --sdk_root=${ANDROID_HOME}

sudo apt -y install gradle

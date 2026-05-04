#!/bin/sh
echo "============================================"
echo " Skyways Airport Dispatch Tycoon"
echo " Building and launching..."
echo "============================================"
echo ""

# Auto-detect JAVA_HOME on macOS
if [ -z "$JAVA_HOME" ]; then
    if [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null)"
    fi
fi

# Fallback: Homebrew openjdk@21
if [ -z "$JAVA_HOME" ]; then
    for brew_prefix in /opt/homebrew /usr/local; do
        if [ -d "$brew_prefix/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ]; then
            export JAVA_HOME="$brew_prefix/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
            break
        fi
    done
fi

if [ -z "$JAVA_HOME" ]; then
    echo "ERROR: Java 21 not found."
    echo "Install Java 21:  brew install openjdk@21"
    exit 1
fi

export PATH="$JAVA_HOME/bin:$PATH"

echo "Using JAVA_HOME: $JAVA_HOME"
echo ""

cd "$(dirname "$0")" || exit 1

# Prefer system Maven (avoids SSL download issues with the wrapper)
if command -v mvn >/dev/null 2>&1; then
    MVN_CMD="mvn"
elif [ -x "/opt/homebrew/bin/mvn" ]; then
    MVN_CMD="/opt/homebrew/bin/mvn"
else
    chmod +x mvnw 2>/dev/null
    MVN_CMD="./mvnw"
fi

echo "Using Maven: $MVN_CMD"
$MVN_CMD javafx:run

if [ $? -ne 0 ]; then
    echo ""
    echo "BUILD FAILED. Check errors above."
fi

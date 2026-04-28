#!/bin/sh
echo "============================================"
echo " Skyways Airport Dispatch Tycoon"
echo " Building and launching..."
echo "============================================"
echo ""

# Auto-detect JAVA_HOME on macOS
if [ -z "$JAVA_HOME" ]; then
    if [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null)"
    fi
fi

if [ -z "$JAVA_HOME" ] && ! command -v java >/dev/null 2>&1; then
    echo "ERROR: Java not found."
    echo "Install Java 21:  brew install openjdk@21"
    exit 1
fi

echo "Using JAVA_HOME: $JAVA_HOME"
echo ""

cd "$(dirname "$0")" || exit 1
chmod +x mvnw 2>/dev/null
./mvnw javafx:run

if [ $? -ne 0 ]; then
    echo ""
    echo "BUILD FAILED. Check errors above."
fi

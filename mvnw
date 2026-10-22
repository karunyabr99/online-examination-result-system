#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper Script for ExamFlow
# ----------------------------------------------------------------------------

export JAVA_HOME=${JAVA_HOME:-/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home}

PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASE_DIR=`cd "$PRGDIR" >/dev/null; pwd`

# Check if local maven is present
if [ -x "$BASE_DIR/apache-maven-3.9.6/bin/mvn" ]; then
    exec "$BASE_DIR/apache-maven-3.9.6/bin/mvn" "$@"
fi

# Fallback to system mvn
if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
fi

echo "Error: Maven could not be located. Please install Apache Maven or set up JAVA_HOME." >&2
exit 1

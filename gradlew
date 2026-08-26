#!/bin/sh

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
APP_NAME="Gradle"
DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

warn() {
    echo "$*" >&2
}

die() {
    echo
    echo "$*" >&2
    echo
    exit 1
}

case "$(uname)" in
  CYGWIN* | MINGW* )
    APP_HOME=$(cygpath --path --mixed "$APP_HOME")
    CLASSPATH=$(cygpath --path --mixed "$APP_HOME/gradle/wrapper/gradle-wrapper.jar")
    ;;
  * )
    CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
    ;;
esac

if [ -n "$JAVA_HOME" ]; then
    JAVACMD=$JAVA_HOME/bin/java
    [ -x "$JAVACMD" ] || die "JAVA_HOME aponta para uma instalação Java inválida: $JAVA_HOME"
else
    JAVACMD=java
    command -v java >/dev/null 2>&1 || die "Java não foi encontrado. Configure JAVA_HOME."
fi

set -- \
    "-Dorg.gradle.appname=$APP_NAME" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"

exec "$JAVACMD" $DEFAULT_JVM_OPTS "$@"

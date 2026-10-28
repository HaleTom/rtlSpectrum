#!/bin/sh
# Launch rtlSpectrum with native access enabled (silences JavaFX warnings on JDK 17+).
# Usage: ./rtlSpectrum.sh [jar] [args...]
JAR="${1:-target/rtlSpectrum_linux.jar}"
shift 2>/dev/null || true
exec java --enable-native-access=ALL-UNNAMED -jar "$JAR" "$@"

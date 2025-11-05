#!/bin/bash
set -e
./gradlew clean build publish
echo "✅ Weather SDK published successfully to GitHub Packages!"
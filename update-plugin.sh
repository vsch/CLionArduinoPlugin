#!/usr/bin/env bash
VERSION="1.5.41-signed"
PLUGIN="CLionArduinoPlugin"
DIST=".zip"
HOME_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
OLD_PLUGIN=
IDE_LIST=(
    "CLion2020.3"
    "CLion2021.1"
    "CLion2021.2"
    "CLion2021.3"
    "CLion2022.1"
    "CLion2022.2"
    "CLion2022.3"
    "CLion2023.1"
    "CLion2023.2"
    "CLion2023.3"
)

cd "${HOME_DIR}" || exit

# copy the versioned file to un-versioned one.
cp "./build/distributions/${PLUGIN}-${VERSION}${DIST}" "./${PLUGIN}${DIST}"
cp "./build/distributions/${PLUGIN}-${VERSION}${DIST}" "./dist"

echo updating "/Volumes/Pegasus/Data" for latest "${PLUGIN}${DIST}"
cp "${PLUGIN}${DIST}" "/Volumes/Pegasus/Data"

../update-plugin.sh "${HOME_DIR}" "${PLUGIN}" "${OLD_PLUGIN}" "${IDE_LIST[@]}"

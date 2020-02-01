#!/usr/bin/env bash
PLUGIN="CLionArduinoPlugin"
PLUGIN_JAR=
HOME_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
OLD_PLUGIN=
SANDBOX_NAME="plugin-sandbox-cl"
IDE_VERSION=
SANDBOX_IDE="CLion2019.3"
IDE_LIST="CLion2019.1 CLion2019.2 CLion2019.3 CLion2020.1"

cd "${HOME_DIR}" || exit

echo updating "/Volumes/Pegasus/Data" for latest "${PLUGIN}"
cp "${PLUGIN}.zip" "/Volumes/Pegasus/Data"

../update-plugin.sh "${HOME_DIR}" "${PLUGIN}" "${PLUGIN_JAR}" "${OLD_PLUGIN}" "${IDE_VERSION}" "${SANDBOX_NAME}" "${SANDBOX_IDE}" "$IDE_LIST"

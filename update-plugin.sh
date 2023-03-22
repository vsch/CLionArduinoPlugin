#!/usr/bin/env bash
PLUGIN="CLionArduinoPlugin"
PLUGIN_JAR=
HOME_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
OLD_PLUGIN=
SANDBOX_NAME="plugin-sandbox-cl"
IDE_VERSION=
SANDBOX_IDE=
IDE_VERSIONS=(
    193
    201
    202
    203
    211
    212
    213
    221
    222
    223
    231
    232
    233
)

IDE_LIST="CLion2019.3 CLion2020.1 CLion2020.2 CLion2020.3 CLion2021.1 CLion2021.2 CLion2021.3 CLion2022.1 CLion2022.2 CLion2022.3 CLion2023.1 CLion2023.2 CLion2023.3"

cd "${HOME_DIR}" || exit

echo updating "/Volumes/Pegasus/Data" for latest "${PLUGIN}"
cp "${PLUGIN}.zip" "/Volumes/Pegasus/Data"

../update-plugin.sh "${HOME_DIR}" "${PLUGIN}" "${PLUGIN_JAR}" "${OLD_PLUGIN}" "${IDE_VERSION}" "${SANDBOX_NAME}" "${SANDBOX_IDE}" "${IDE_VERSIONS[@]}"

#!/usr/bin/env bash
PLUGIN="CLionArduinoPlugin"
PLUGIN_JAR=
HOME_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
OLD_PLUGIN=
SANDBOX_NAME="plugin-sandbox-cl"
SANDBOX_IDE=
IDE_LIST=(
#    "CLion2019.3" only 201.6668.113 is supported
    "CLion2020.1"
    "CLion2020.2"
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

../update-plugin.sh "${HOME_DIR}" "${PLUGIN}" "${PLUGIN_JAR}" "${OLD_PLUGIN}" "${SANDBOX_NAME}" "${SANDBOX_IDE}" "${IDE_LIST[@]}"


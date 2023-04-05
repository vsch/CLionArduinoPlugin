#!/usr/bin/env bash
HOME_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
PLUGIN="CLionArduinoPlugin"

# These use new settings' location directory Application Support/JetBrains/Product
IDE_LIST_NEW_LOCATION=(
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
    "IdeaIC2020-1-EAP"
    "IdeaIC2020-2-EAP"
    "IdeaIC2020-3-EAP"
    "IdeaIC2020.1"
    "IdeaIC2020.2"
    "IdeaIC2020.3"
    "IdeaIC2021-1-EAP"
    "IdeaIC2021-2-EAP"
    "IdeaIC2021-3-EAP"
    "IdeaIC2021.1"
    "IdeaIC2021.2"
    "IdeaIC2021.3"
    "IdeaIC2022-1-EAP"
    "IdeaIC2022-2-EAP"
    "IdeaIC2022-3-EAP"
    "IdeaIC2022.1"
    "IdeaIC2022.2"
    "IdeaIC2022.3"
    "IdeaIC2023-1-EAP"
    "IdeaIC2023-2-EAP"
    "IdeaIC2023-3-EAP"
    "IdeaIC2023.1"
    "IdeaIC2023.2"
    "IdeaIC2023.3"
    "IntelliJIdea2020.1"
    "IntelliJIdea2020.2"
    "IntelliJIdea2020.3"
    "IntelliJIdea2021.1"
    "IntelliJIdea2021.2"
    "IntelliJIdea2021.3"
    "PhpStorm2020.1"
    "PhpStorm2020.2"
    "PhpStorm2020.3"
    "PhpStorm2021.1"
    "PhpStorm2021.2"
    "PhpStorm2021.3"
    "WebStorm2020.1"
    "WebStorm2020.2"
    "WebStorm2020.3"
    "WebStorm2021.1"
    "WebStorm2021.2"
    "WebStorm2021.3"
)

# update all the sandbox directories
function UpdRaw() {
    PRODUCT_PLUGINS=$1
    if [[ -d "${PRODUCT_PLUGINS}" ]]; then
        echo updating "${PRODUCT_PLUGINS}"
    else
        echo creating "${PRODUCT_PLUGINS}"
        mkdir -p "${PRODUCT_PLUGINS}"
    fi

    rm -fr ${PRODUCT_PLUGINS}/${PLUGIN}
    unzip -bq "${PLUGIN}.zip" -d "${PRODUCT_PLUGINS}"
}

function Upd() {
    PRODUCT=$1
    for SANDBOX in "${@:2}"; do
        if [[ " ${IDE_LIST_NEW_LOCATION[*]} " =~ " ${PRODUCT} " ]]; then
            UpdRaw "/Users/vlad/Library/Caches/JetBrains/${PRODUCT}/${SANDBOX}/plugins"
        else
            UpdRaw "/Users/vlad/Library/Caches/${PRODUCT}/${SANDBOX}/plugins"
        fi
    done
}

Upd "CLion2020-2" "plugins-sandbox-mn"
Upd "CLion2020-3" "plugins-sandbox-mn"
Upd "CLion2021-2" "plugins-sandbox"

# for plugin debugging in intellij-community under debugging
# UpdRaw "/Users/vlad/src/intellij-community/system/plugins-sandbox/plugins"
# /Library/Java/JavaVirtualMachines/jbrsdk-11.0.7_b926.3/Contents/Home/bin/java -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:54156,suspend=y,server=n -Xmx3000m -Xms1024m -ea -Didea.is.internal=true -Didea.platform.prefix=Idea -Djava.system.class.loader=com.intellij.util.lang.PathClassLoader -Didea.config.path=/Users/vlad/Library/Caches/JetBrains/IdeaIC2021-3-EAP/plugins-sandbox-mn/config -Didea.system.path=/Users/vlad/Library/Caches/JetBrains/IdeaIC2021-3-EAP/plugins-sandbox-mn/system -Didea.plugins.path=/Users/vlad/Library/Caches/JetBrains/IdeaIC2021-3-EAP/plugins-sandbox-mn/plugins -Didea.classpath.index.enabled=false -Didea.required.plugins.id=com.vladsch.MissingInActions -Dapple.laf.useScreenMenuBar=true -Dapple.awt.fileDialogForDirectories=true -javaagent:/Users/vlad/Library/Caches/JetBrains/IdeaIC2021.3/captureAgent/debugger-agent.jar=file:/private/var/folders/bs/4ktxsyn54pj5mc0l8b43h8vc0000gn/T/capture.props -Dfile.encoding=UTF-8 -classpath "/Applications/IntelliJ IDEA 2021.2 CE.app/Contents/lib/log4j.jar:/Applications/IntelliJ IDEA 2021.2 CE.app/Contents/lib/jdom.jar:/Applications/IntelliJ IDEA 2021.2 CE.app/Contents/lib/trove4j.jar:/Applications/IntelliJ IDEA 2021.2 CE.app/Contents/lib/openapi.jar:/Applications/IntelliJ IDEA 2021.2 CE.app/Contents/lib/util.jar:/Applications/IntelliJ IDEA 2021.2 CE.app/Contents/lib/bootstrap.jar:/Applications/IntelliJ IDEA 2021.2 CE.app/Contents/lib/idea_rt.jar:/Applications/IntelliJ IDEA 2021.2 CE.app/Contents/lib/idea.jar:/Applications/IntelliJ IDEA 2021.3 CE.app/Contents/lib/idea_rt.jar" com.intellij.idea.Main

/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package com.vladsch.clionarduinoplugin.generators

import com.vladsch.clionarduinoplugin.ArduinoSupportBundle
import icons.PluginIcons
import org.jetbrains.annotations.Nls

import javax.swing.Icon

class ArduinoLibraryGenerator : ArduinoProjectGenerator(true) {

    @Nls
    override fun getName(): String {
        return ArduinoSupportBundle.message("new-project.library-project.name")
    }

    override fun getDescription(): String? {
        return ArduinoSupportBundle.message("new-project.library-project.description")
    }

    override fun getLogo(): Icon? {
        return PluginIcons.arduino_library
    }

    override val templateType: String
        get() = if (mySettings.isStaticLibraryType) "project/library_static" else "project/library_arduino"

    override fun templateVars(name: String, pascalName: String, camelName: String, snakeName: String): Map<String, String> =
            mapOf(
                    "LIBRARY_NAME" to snakeName.toUpperCase(),
                    "library_name" to snakeName.toLowerCase(),
                    "LibraryName" to pascalName,
                    "libraryName" to camelName,
                    "LIBRARY_CATEGORY" to mySettings.libraryCategory,
                    "LIBRARY_DISPLAY_NAME" to mySettings.libraryDisplayName,
                    "USER_NAME" to mySettings.authorName,
                    "E_MAIL" to mySettings.authorEMail
            )
}

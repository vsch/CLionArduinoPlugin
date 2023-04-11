package com.vladsch.clionarduinoplugin.generators.cmake

import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandSubType
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType
import com.vladsch.clionarduinoplugin.resources.Strings
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettings
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettingsProxy
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectFileSettings
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.plugin.util.getFileContent
import com.vladsch.plugin.util.plus
import java.io.File

/**
 * Class for creating, reading and modifying CMakeLists.txt
 *
 *
 * A spec CMake parser but only intended for Arduino Support created CMakeLists.txt files:
 *
 *
 * cmake_minimum_required(VERSION 2.8.4)
 * set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
 * set(PROJECT_NAME tft_life)
 *
 *
 * ## This must be set before project call
 * set(${CMAKE_PROJECT_NAME}_BOARD pro)
 * set(ARDUINO_CPU 8MHzatmega328)
 *
 *
 * project(${PROJECT_NAME})
 *
 *
 * # Define the source code
 * set(${PROJECT_NAME}_SRCS tft_life.cpp)
 * #set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
 * link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)
 *
 *
 * #### Uncomment below additional settings as needed.
 * set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
 * set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
 * set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
 * # set(pro.upload.speed 57600)
 *
 *
 * generate_arduino_firmware(${CMAKE_PROJECT_NAME})
 */
class ArduinoCMakeListsTxtBuilder : CMakeListsTxtBuilder {

    constructor() : super(ourCommands, ourAnchors, PROJECT_NAME)

    constructor(text: CharSequence, options: DataHolder? = null, values: Map<String, Any>? = null) : super(
        PROJECT_NAME,
        ourCommands,
        ourAnchors,
        text,
        options,
        values
    )

    constructor(cMakeFile: CMakeFile, values: Map<String, Any>? = null) : super(PROJECT_NAME, ourCommands, ourAnchors, cMakeFile, values)

    companion object {
        // commands can have fixed and variable arguments
        // fixed arguments can have dependency on variable arguments of other commands in the command set for a given cmake file
        // this allows fixed args to be dependent on args of other commands or values in the command set
        //
        // <$COMMAND_NAME$> refers to variable arg 0 of command with name COMMAND_NAME or string mapped by COMMAND_NAME
        // <$COMMAND_NAME[]$> refers to variable arg 0 of command with name COMMAND_NAME
        // <$COMMAND_NAME[2]$> refers to variable arg 2 of command with name COMMAND_NAME
        //
        // if index is invalid then it is the same as if 0 was used.
        //
        // if command is not found or has less args then an empty value will be used
        //

        // @formatter:off
        val GENERATE_ARDUINO_FIRMWARE = CMakeCommandType("GENERATE_ARDUINO_FIRMWARE", "generate_arduino_firmware", arrayOf(), 1, 1, true, false, true, arrayOf("\${PROJECT_NAME}"), 0)
        val GENERATE_ARDUINO_LIBRARY = CMakeCommandType("GENERATE_ARDUINO_LIBRARY", "generate_arduino_library", arrayOf(), 1, 1, true, false, true, arrayOf("\${PROJECT_NAME}"), 0)
        
        val SET_BOARD = CMakeCommandSubType("SET_BOARD", SET, arrayOf("${PROJECT_NAME}_BOARD"), 1, 1, false, false, true)
        val SET_CPU = CMakeCommandSubType("SET_CPU", SET, arrayOf("ARDUINO_CPU"), 1, 1, false, false, true)
        val SET_SKETCH = CMakeCommandSubType("SET_SKETCH", SET, arrayOf("${PROJECT_NAME}_SKETCH"), 1, 1, false, false, true)
        val SET_PROGRAMMER = CMakeCommandSubType("SET_PROGRAMMER", SET, arrayOf("${PROJECT_NAME}_PROGRAMMER"), 1, 1, false, false, true)
        val SET_PORT = CMakeCommandSubType("SET_PORT", SET, arrayOf("${PROJECT_NAME}_PORT"), 1, 1, false, false, true)
        val SET_AFLAGS = CMakeCommandSubType("SET_AFLAGS", SET, arrayOf("${PROJECT_NAME}_AFLAGS"), 1, CMakeCommandType.INF_MAX_ARGS, true, false, true)
        val SET_HDRS = CMakeCommandSubType("SET_HDRS", SET, arrayOf("${PROJECT_NAME}_HDRS"), 0, CMakeCommandType.INF_MAX_ARGS, true, false, true)
        val SET_SRCS = CMakeCommandSubType("SET_SRCS", SET, arrayOf("${PROJECT_NAME}_SRCS"), 0, CMakeCommandType.INF_MAX_ARGS, true, false, true)
        val SET_LIBS = CMakeCommandSubType("SET_LIBS", SET, arrayOf("${PROJECT_NAME}_LIBS"), 0, CMakeCommandType.INF_MAX_ARGS, true, false, true)

        val SET_LIB_NAMES_RECURSE = CMakeCommandSubType("SET_LIB_NAMES_RECURSE",SET, arrayOf("<@@>_RECURSE"), 2, 2, true, true, false)
        val SET_UPLOAD_SPEEDS = CMakeCommandSubType("SET_UPLOAD_SPEEDS",SET, arrayOf("<@@>.upload.speed"), 2, 2, true, true, false)
        // @formatter:on

        // with variables that need to be provided
        val SET_LIB_NAME_RECURSE =
            CMakeCommandSubType("SET_LIB_NAME_RECURSE", SET_LIB_NAMES_RECURSE, arrayOf("<@LIB_NAME@>"), 1, 1, true, false, false)
        val SET_UPLOAD_SPEED =
            CMakeCommandSubType("SET_UPLOAD_SPEED", SET_UPLOAD_SPEEDS, arrayOf("<@SET_BOARD@>"), 1, 1, true, false, false)

        val ourCommands = arrayOf(
            GENERATE_ARDUINO_FIRMWARE,
            GENERATE_ARDUINO_LIBRARY,
            SET_BOARD,
            SET_CPU,
            SET_SKETCH,
            SET_PROGRAMMER,
            SET_PORT,
            SET_AFLAGS,
            SET_HDRS,
            SET_SRCS,
            SET_LIBS,
            SET_LIB_NAMES_RECURSE,
            SET_LIB_NAME_RECURSE,
            SET_UPLOAD_SPEEDS,
            SET_UPLOAD_SPEED
        )

        val ourAnchors = arrayOf(
            CMakeCommandAnchor.first(CMAKE_MINIMUM_REQUIRED_VERSION),
            CMakeCommandAnchor.first(SET_CMAKE_TOOLCHAIN_FILE),
            CMakeCommandAnchor.first(SET_CMAKE_CXX_STANDARD),
            CMakeCommandAnchor.first(SET_PROJECT_NAME),

            CMakeCommandAnchor.before(PROJECT, SET_BOARD),
            CMakeCommandAnchor.before(PROJECT, SET_CPU),

            CMakeCommandAnchor.after(PROJECT, SET_SRCS),
            CMakeCommandAnchor.after(PROJECT, SET_HDRS),
            CMakeCommandAnchor.after(PROJECT, SET_LIBS),
            CMakeCommandAnchor.after(PROJECT, LINK_DIRECTORIES),
            CMakeCommandAnchor.after(PROJECT, ADD_SUBDIRECTORY),
            CMakeCommandAnchor.after(PROJECT, SET_SKETCH),
            CMakeCommandAnchor.after(PROJECT, SET_PROGRAMMER),
            CMakeCommandAnchor.after(PROJECT, SET_PORT),
            CMakeCommandAnchor.after(PROJECT, SET_AFLAGS),
            CMakeCommandAnchor.after(PROJECT, SET_LIB_NAME_RECURSE),
            CMakeCommandAnchor.after(PROJECT, SET_LIB_NAMES_RECURSE),
            CMakeCommandAnchor.after(PROJECT, SET_UPLOAD_SPEED),
            CMakeCommandAnchor.after(PROJECT, SET_UPLOAD_SPEEDS),

            CMakeCommandAnchor.last(GENERATE_ARDUINO_FIRMWARE),
            CMakeCommandAnchor.last(GENERATE_ARDUINO_LIBRARY)
        )

        private val String.extension: String
            get() {
                val pos = lastIndexOf('.')
                return if (pos > 0) substring(pos + 1) else ""
            }

        private val String.name: String
            get() {
                return File(this).name
            }

        private fun String.ifEmpty(defValue: String): String {
            return if (isEmpty()) defValue else this
        }

        // sourceFiles are paths relative to project directory
        fun getCMakeFileContent(
            template: String,
            projectName: String,
            settings: ArduinoApplicationSettingsProxy,
            sourceFiles: List<String>,
            unmodifiedOriginalText: Boolean
        ): String {
            val cppFiles = sourceFiles.filter { it.extension.matches("(?i:cpp|cxx|c)".toRegex()) }
            val hFiles = sourceFiles.filter { it.extension.matches("(?i:hpp|hxx|h)".toRegex()) }
            val sketchFile = sourceFiles.findLast { it.extension.matches("(?i:${Strings.PDE_EXT}|${Strings.INO_EXT})".toRegex()) }

            settings.sources = cppFiles.toTypedArray()
            settings.headers = hFiles.toTypedArray()
            settings.sketch = sketchFile ?: ""
            return getCMakeFileContent(template, projectName, settings, unmodifiedOriginalText)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun getCMakeFileContent(
            template: String,
            projectName: String,
            settings: ArduinoApplicationSettingsProxy,
            unmodifiedOriginalText: Boolean
        ): String {
            @Suppress("CanBeVal")
            var command: CMakeCommand?
            val builder = ArduinoCMakeListsTxtBuilder(template, null) // use default options
            val isStaticLib = settings.isLibrary && settings.isStaticLibraryType
            val appSettings = settings.applicationSettings

            builder.isWantCommented = true

            builder.setOrAddCommand(CMAKE_MINIMUM_REQUIRED_VERSION, "3.0.0")
            builder.setOrAddCommand(
                SET_CMAKE_TOOLCHAIN_FILE,
                if (appSettings.isBundledToolchainTxt) "\${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"
                else appSettings.toolchainTxtPath
            )
            builder.setOrAddCommand(SET_CMAKE_CXX_STANDARD, appSettings.languageVersionLineForCMake)
                .setSuppressible(appSettings.languageVersionName.isEmpty())

            builder.setOrAddCommand(SET_PROJECT_NAME, projectName)

            val boardId = appSettings.boardId.ifEmpty("uno")
            builder.setOrAddCommand(SET_BOARD, boardId)

            val cpuIds = appSettings.arduinoConfig.getBoardById(boardId).cpuIds
            val cpuId = appSettings.cpuId.ifEmpty(cpuIds.firstOrNull() ?: "")
            builder.setOrAddCommand(SET_CPU, cpuId).setSuppressible(cpuId.isEmpty())

            builder.setOrAddCommand(PROJECT).clearToDefaults()

            val cppFiles = settings.sources.toList()
            val hFiles = settings.headers.toList()
            val sketchFile = settings.sketch

            builder.setOrAddCommand(SET_SRCS, cppFiles).setSuppressible(cppFiles.isEmpty())
            builder.setOrAddCommand(SET_HDRS, hFiles).setSuppressible(hFiles.isEmpty())

            // TODO: implement
            val staticLibs = arrayOf<String>();
            builder.setOrAddCommand(SET_LIBS, *staticLibs).setSuppressible(staticLibs.isEmpty())

            builder.setOrAddCommand(SET_SKETCH, sketchFile.ifEmpty(projectName + Strings.DOT_INO_EXT)).setSuppressible(sketchFile.isEmpty())

            command = builder.getCommand(ADD_SUBDIRECTORY)
            if (command == null || command.argCount == 0) {
                builder.setOrAddCommand(ADD_SUBDIRECTORY).setSuppressible(true)
            }

            if (appSettings.libraryDirectories.isEmpty()) {
                builder.setOrAddCommand(LINK_DIRECTORIES, "\${CMAKE_CURRENT_SOURCE_DIR}/")
                    .setSuppressible(!appSettings.isAddLibraryDirectory)
            } else {
                builder.setOrAddCommand(LINK_DIRECTORIES, appSettings.libraryDirectories.map { "\${CMAKE_CURRENT_SOURCE_DIR}/$it" })
                    .setSuppressible(!appSettings.isAddLibraryDirectory || appSettings.libraryDirectories.isEmpty())
            }

            if (!settings.isLibrary) {
                // TODO: add UI for recurse library options
                if (appSettings.nestedLibraries.isEmpty()) {
                    // builder.setOrAddCommand(SET_LIB_NAME_RECURSE, "\${LIB_NAME}", "true").setSuppressible(true)
                } else {
                    for (library in appSettings.nestedLibraries) {
                        builder.setOrAddCommand(SET_LIB_NAME_RECURSE, library, "true")
                    }
                }
            }

            val programmer = appSettings.programmerId
            builder.setOrAddCommand(SET_PROGRAMMER, programmer.ifEmpty("avrispmkii")).setSuppressible(programmer.isEmpty())

            val port = appSettings.port
            builder.setOrAddCommand(SET_PORT, port.ifEmpty("/dev/cu.usbserial-00000000")).setSuppressible(port.isEmpty())

            val baudRateText = appSettings.baudRateText
            builder.setOrAddCommand(SET_UPLOAD_SPEED, baudRateText.ifEmpty("9600")).setSuppressible(baudRateText.isEmpty())

            builder.setOrAddCommand(SET_AFLAGS, "-v").setSuppressible(!appSettings.isVerbose)

            if (isStaticLib) {
                builder.setOrAddCommand(GENERATE_ARDUINO_LIBRARY)
                builder.removeCommand(GENERATE_ARDUINO_FIRMWARE)
            } else {
                builder.setOrAddCommand(GENERATE_ARDUINO_FIRMWARE)
                builder.removeCommand(GENERATE_ARDUINO_LIBRARY)
            }

            // Can add our own values to resolve variables
            
            return builder.getCMakeContents(
                null,
                !appSettings.isCommentUnusedSettings,
                unmodifiedOriginalText && builder.canUseUnmodifiedOriginal
            )
        }

        /**
         * returns application settings determined from the current project files
         * NOTE: these settings are not an instance of "Official" application settings but a non-persisted copy
         */

        @JvmOverloads
        fun loadProjectConfiguration(
            projectDir: File,
            altCMakeListsContent: String? = null,
            applicationSettings: ArduinoApplicationSettings = ArduinoApplicationSettings.getInstance()
        ): ArduinoApplicationSettingsProxy? {
            val cMakeLists = projectDir + Strings.CMAKE_LISTS_FILENAME
            val libraryProperties: File = projectDir + Strings.LIBRARY_PROPERTIES_FILENAME

            if (altCMakeListsContent == null && (!cMakeLists.exists() || !cMakeLists.isFile || !cMakeLists.canRead())) {
                return null
            }

            val notifications = ArrayList<String>()

            val cMakeListsText = altCMakeListsContent ?: getFileContent(cMakeLists)
            val builder = ArduinoCMakeListsTxtBuilder(cMakeListsText)
            builder.isWantCommented = false   // commented commands don't count

            // see if at all our project type by looking for generate_arduino_firmware(${CMAKE_PROJECT_NAME}) or generate_arduino_library(${CMAKE_PROJECT_NAME})
            val arduinoCommand = builder.getCommand(ArduinoCMakeListsTxtBuilder.GENERATE_ARDUINO_FIRMWARE)
                ?: builder.getCommand(ArduinoCMakeListsTxtBuilder.GENERATE_ARDUINO_LIBRARY) ?: return null

            // ok, it is ours
            val settings = ArduinoApplicationSettingsProxy.wrap(applicationSettings, false) as ArduinoProjectFileSettings
            val cMakeVariableValues = builder.cMakeVariableValues
            val cMakeProjectName = builder.cMakeProjectName

            settings.projectName = cMakeProjectName ?: ""
            settings.sources = cMakeVariableValues["${cMakeProjectName}_SRCS"].toTypedArray()
            settings.headers = cMakeVariableValues["${cMakeProjectName}_HDRS"].toTypedArray()
            settings.sketch = cMakeVariableValues["${cMakeProjectName}_SKETCH"].firstOrNull() ?: ""
            settings.boardId = cMakeVariableValues["${cMakeProjectName}_BOARD"].firstOrNull() ?: ""
            settings.cpuId = cMakeVariableValues["ARDUINO_CPU"].firstOrNull() ?: ""

            if (!builder.canUseUnmodifiedOriginal) {
                notifications.add(
                    "project-build-config.invalid-set-project.1\r${builder.cMakeProjectNameMacro}\r${
                        builder.cMakeProjectNameMacro.removeSurrounding(
                            "\${",
                            "}"
                        )
                    }\r ${settings.projectName}"
                )
            }

            when (arduinoCommand.commandType) {
                ArduinoCMakeListsTxtBuilder.GENERATE_ARDUINO_LIBRARY -> {
                    settings.isLibrary = true
                    settings.libraryType = ArduinoProjectFileSettings.STATIC_LIB_TYPE
                }

                ArduinoCMakeListsTxtBuilder.GENERATE_ARDUINO_FIRMWARE -> {
                    if (libraryProperties.exists() && libraryProperties.isFile) {
                        settings.isLibrary = true
                        settings.libraryType = ArduinoProjectFileSettings.ARDUINO_LIB_TYPE
                    } else {
                        settings.isLibrary = false
                    }
                }
            }

            settings.languageVersionId = cMakeVariableValues["CMAKE_CXX_STANDARD"].firstOrNull() ?: ""
            if (true) {
                val list = builder.getCommands(LINK_DIRECTORIES).flatMap { it.args }
                if (!list.isEmpty()) {
                    settings.isAddLibraryDirectory = true
                    settings.libraryDirectories = list.map { it.removePrefix("\${CMAKE_CURRENT_SOURCE_DIR}/") }.toTypedArray()
                }
            }

            if (!settings.isLibrary) {
                val list = ArrayList<String>()
                builder.getCommands(ArduinoCMakeListsTxtBuilder.SET_LIB_NAMES_RECURSE).forEach {
                    list.addAll(it.args)
                }
                settings.nestedLibraries = list.toTypedArray()
            }

            settings.programmerId = cMakeVariableValues["${cMakeProjectName}_PROGRAMMER"].firstOrNull() ?: ""
            settings.port = cMakeVariableValues["${cMakeProjectName}_PORT"].firstOrNull() ?: ""
            settings.baudRate = cMakeVariableValues["pro.upload.speed"].firstOrNull()?.toIntOrNull() ?: 0
            settings.isVerbose = cMakeVariableValues["${cMakeProjectName}_AFLAGS"].contains("-v")

            // TODO: implement getting these from the library.properties file
            if (libraryProperties.exists() && libraryProperties.isFile && libraryProperties.canRead()) {
                // Library Properties file
                // libraryDisplayName: String
                // authorName: String
                // authorEMail: String
                // libraryCategory: String
            }

            if (notifications.isNotEmpty()) {
                (settings as ArduinoApplicationSettingsProxy).setNotifications(notifications)
            }
            return settings as ArduinoApplicationSettingsProxy
        }
    }
}

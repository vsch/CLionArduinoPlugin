package com.vladsch.clionarduinoplugin.resources

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TemplateResolverTest {
    val String.prep: String
        get() {
            return replace('@', '$').replace('&', '@')
        }

    @Before
    fun setUp() {
        TemplateResolver.inTest = true
    }

    fun compareFiles(expected: Map<String, String>, actual: Map<String, String>) {
        val sortedKeys = expected.keys.sorted()
        assertEquals(sortedKeys, actual.keys.sorted())

        for (key in sortedKeys) {
            assertEquals(expected[key], actual[key])
        }
    }

    @Test
    fun test_sketch() {
        val files = TemplateResolver.getTemplates("sketch", null)
        compareFiles(mapOf(SKETCH_FILE to SKETCH_CONTENT), files)
    }

    @Test
    fun test_library() {
        val files = TemplateResolver.getTemplates("library", null)
        compareFiles(mapOf(
                LIBRARY_CPP_FILE to LIBRARY_CPP_CONTENT,
                LIBRARY_H_FILE to LIBRARY_H_CONTENT
        ), files)
    }

    @Test
    fun test_project_static_library() {
        val files = TemplateResolver.getTemplates("project/library_static", null)
        compareFiles(mapOf(
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT,
                LIBRARY_CPP_FILE to LIBRARY_CPP_CONTENT,
                LIBRARY_H_FILE to LIBRARY_H_CONTENT
        ), files)
    }

    @Test
    fun test_project_arduino_library() {
        val files = TemplateResolver.getTemplates("project/library_arduino", null)
        compareFiles(mapOf(
                LIBRARY_CPP_FILE to LIBRARY_CPP_CONTENT,
                LIBRARY_H_FILE to LIBRARY_H_CONTENT,
                LIBRARY_NAME_TEST_INO_FILE to LIBRARY_NAME_TEST_INO_CONTENT,
                //                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT,
                LIBRARY_README_FILE to LIBRARY_README_CONTENT,
                LIBRARY_README_FILE to LIBRARY_README_CONTENT,
                KEYWORDS_TXT_FILE to KEYWORDS_TXT_CONTENT,
                LIBRARY_PROPERTIES_FILE to LIBRARY_PROPERTIES_CONTENT,
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT
        ), files)
    }

    @Test
    fun test_project_sketch() {
        val files = TemplateResolver.getTemplates("project/sketch", null)
        compareFiles(mapOf(
//                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT,
                PROJECT_NAME_INO_FILE to PROJECT_NAME_INO_CONTENT,
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT
        ), files)
    }

    @Test
    fun test_project_sketch_subdir() {
        val files = TemplateResolver.getTemplates("project/sketchsubdir", null)
        compareFiles(mapOf(
                SUB_LIBRARY_CPP_FILE to LIBRARY_CPP_CONTENT,
                SUB_LIBRARY_H_FILE to LIBRARY_H_CONTENT,
//                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT,
                PROJECT_NAME_INO_FILE to PROJECT_NAME_INO_CONTENT,
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT
        ), files)
    }

    @Test
    fun test_variables() {
        val files = TemplateResolver.getTemplates("project/sketchsubdir", null)
        val values = mapOf("library_name" to "lib_name", "LIBRARY_NAME" to "LIB_NAME", "libraryName" to "libName", "LibraryName" to "LibName")
        val templates = TemplateResolver.resolveTemplates(files, values)

        compareFiles(mapOf(
                "sub/lib_name.cpp" to LIBRARY_CPP_CONTENT.replace("<@library_name@>", "lib_name").replace("<@LIBRARY_NAME@>", "LIB_NAME").replace("<@LibraryName@>", "LibName"),
                "sub/lib_name.h" to LIBRARY_H_CONTENT.replace("<@library_name@>", "lib_name").replace("<@LIBRARY_NAME@>", "LIB_NAME").replace("<@LibraryName@>", "LibName"),
//                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT.replace("<@PROJECT_NAME@>", ""),
                //                PROJECT_NAME_INO_FILE to PROJECT_NAME_INO_CONTENT,
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT.replace("<@SET_BOARD@>", "")
        ), templates)
    }

    @Test
    fun test_variables_arduino_library() {
        val files = TemplateResolver.getTemplates("project/library_arduino", null)
        val values = mapOf("library_name" to "lib_name", "LIBRARY_NAME" to "LIB_NAME", "libraryName" to "libName", "LibraryName" to "LibName", "PROJECT_NAME" to "LIB_NAME",
                "LIBRARY_DISPLAY_NAME" to "Library Name", "USER_NAME" to "Author Name", "E_MAIL" to "email@email.com", "LIBRARY_CATEGORY" to "Library Category"
        )
        val templates = TemplateResolver.resolveTemplates(files, values)

        compareFiles(mapOf(
                "lib_name.cpp" to LIBRARY_CPP_CONTENT.replace("<@library_name@>", "lib_name").replace("<@LIBRARY_NAME@>", "LIB_NAME").replace("<@LibraryName@>", "LibName"),
                "lib_name.h" to LIBRARY_H_CONTENT.replace("<@library_name@>", "lib_name").replace("<@LIBRARY_NAME@>", "LIB_NAME").replace("<@LibraryName@>", "LibName"),
                "lib_name_test.cpp" to LIBRARY_NAME_TEST_INO_CONTENT.replace("<@library_name@>", "lib_name").replace("<@LIBRARY_NAME@>", "LIB_NAME").replace("<@LibraryName@>", "LibName")
                        .replace("<@DELETE_IF_BLANK[lib_name]@>", ""),
                KEYWORDS_TXT_FILE to KEYWORDS_TXT_CONTENT.replace("<@library_name@>", "lib_name")
                        .replace("<@LIBRARY_DISPLAY_NAME@>", "Library Name")
                        .replace("<@LIBRARY_NAME@>", "LIB_NAME")
                        .replace("<@LibraryName@>", "LibName"),
                LIBRARY_PROPERTIES_FILE to LIBRARY_PROPERTIES_CONTENT
                        .replace("<@LIBRARY_DISPLAY_NAME@>", "Library Name")
                        .replace("<@USER_NAME@>", "Author Name")
                        .replace("<@LIBRARY_CATEGORY@>", "Library Category")
                        .replace("<@E_MAIL@>", "email@email.com"),
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT,
                //                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT.replace("<@PROJECT_NAME@>", "LIB_NAME"),
                //                PROJECT_NAME_INO_FILE to PROJECT_NAME_INO_CONTENT,
                LIBRARY_README_FILE to LIBRARY_README_CONTENT.replace("<@LIBRARY_DISPLAY_NAME@>", "Library Name"),
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT.replace("<@SET_BOARD@>", "")
        ), templates)
    }

    @Test
    fun test_variablesDirective() {
        val files = TemplateResolver.getTemplates("project/sketchsubdir", null)
        val values = mapOf("PROJECT_NAME" to "PROJECT_NAME", "project_name" to "project_name")
        val templates = TemplateResolver.resolveTemplates(files, values)

        compareFiles(mapOf(
                //                "sub/libName.cpp" to LIBRARY_CPP_CONTENT.replace("<@LIBRARY_NAME@>",""),
                //                "sub/libName.h" to LIBRARY_H_CONTENT.replace("<@LIBRARY_NAME@>",""),
//                USER_SETUP_H_FILE to USER_SETUP_H_CONTENT.replace("<@PROJECT_NAME@>", "PROJECT_NAME"),
                "project_name.ino" to PROJECT_NAME_INO_CONTENT.replace("#include \"<@library_name@>.h\"<@DELETE_IF_BLANK[<@library_name@>]@>\n", ""),
                CMAKELISTS_TXT_FILE to CMAKELISTS_TXT_CONTENT.replace("<@SET_BOARD@>", "")
        ), templates)
    }

    val SKETCH_FILE = "@sketch_name@.ino"
    val SKETCH_CONTENT = """#include <Arduino.h>
#include "<@library_name@>.h"<@DELETE_IF_BLANK[<@library_name@>]@>

void setup() {

}

void loop() {

}
"""
    val LIBRARY_NAME_TEST_INO_FILE = "@library_name@_test.cpp"
    val LIBRARY_NAME_TEST_INO_CONTENT = SKETCH_CONTENT

    val PROJECT_NAME_INO_FILE = "@project_name@.ino"
    val PROJECT_NAME_INO_CONTENT = SKETCH_CONTENT

    val LIBRARY_CPP_FILE = "@library_name@.cpp"
    val SUB_LIBRARY_CPP_FILE = "sub/@library_name@.cpp"
    val LIBRARY_CPP_CONTENT = """#include "<@library_name@>.h"
"""

    val LIBRARY_H_FILE = "@library_name@.h"
    val SUB_LIBRARY_H_FILE = "sub/@library_name@.h"
    val LIBRARY_H_CONTENT = """#ifndef _<@LIBRARY_NAME@>_H_
#define _<@LIBRARY_NAME@>_H_

class <@LibraryName@> {

public:

};

#endif //_<@LIBRARY_NAME@>_H_
"""

    val LIBRARY_README_FILE = "README.md"
    val LIBRARY_README_CONTENT = """# Arduino <@LIBRARY_DISPLAY_NAME@> library

"""

/*
    val USER_SETUP_H_FILE = "User_Setup.h"
    val USER_SETUP_H_CONTENT = """#ifndef _<@PROJECT_NAME@>_USER_SETUP_H_
#define _<@PROJECT_NAME@>_USER_SETUP_H_

#endif //_<@PROJECT_NAME@>_USER_SETUP_H_
"""
*/

    val LIBRARY_PROPERTIES_FILE = Strings.LIBRARY_PROPERTIES_FILENAME
    val LIBRARY_PROPERTIES_CONTENT = """name=<@LIBRARY_DISPLAY_NAME@>
version=0.0.0
author=<@USER_NAME@>
maintainer=<@USER_NAME@> <@E_MAIL@>
sentence=Summary
paragraph=Description
category=<@LIBRARY_CATEGORY@>
url=URL
architectures=*
"""

    val KEYWORDS_TXT_FILE = "keywords.txt"
    val KEYWORDS_TXT_CONTENT = """#######################################
# Syntax Coloring Map For <@LIBRARY_DISPLAY_NAME@>
#######################################

#######################################
# Datatypes (KEYWORD1)
#######################################

#######################################
# Methods and Functions (KEYWORD2)
#######################################

######################################
# Instances (KEYWORD2)
#######################################

#######################################
# Constants (LITERAL1)
#######################################
"""

    val CMAKELISTS_TXT_FILE = Strings.CMAKE_LISTS_FILENAME
    val CMAKELISTS_TXT_CONTENT = """cmake_minimum_required(VERSION 3.5...3.31 FATAL_ERROR)
#set(CMAKE_TOOLCHAIN_FILE @{CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD)
set(PROJECT_NAME)

set(@{PROJECT_NAME}_BOARD)
set(ARDUINO_CPU)
project(@{PROJECT_NAME})

# Define additional source and header files or default arduino sketch files
# These files are now added to the sketch file, if the sketch is defined.
# set(@{PROJECT_NAME}_SRCS)
# set(@{PROJECT_NAME}_HDRS)

### Additional static libraries to include in the target.
# set(@{PROJECT_NAME}_LIBS)

### Main sketch file
# set(@{PROJECT_NAME}_SKETCH)

### Add project directories into the build
# add_subdirectory()

### Additional settings to add non-standard or your own Arduino libraries.
# For this example (libs will contain additional arduino libraries)
# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files
# link_directories(@{CMAKE_CURRENT_SOURCE_DIR}/libs)

# This is no longer need but left in for those occasions where automatics do not work out.
# Current Arduino.cmake will recurse into any libraries which contain source files in src/ subdirectory.
# Files in subdirectories whose name starts with cmake-build or examples are excluded
# For nested library sources replace @{LIB_NAME} with library name for each library
# set(@{LIB_NAME}_RECURSE true)

#### Additional settings for programmer. From programmers.txt
# set(@{PROJECT_NAME}_PROGRAMMER )
# set(@{PROJECT_NAME}_PORT)
# set(<&SET_BOARD&>.upload.speed)

## Verbose build process
# set(@{PROJECT_NAME}_AFLAGS -v)

# generate_arduino_firmware(@{PROJECT_NAME})
# generate_arduino_library(@{PROJECT_NAME})

print_board_list()
print_programmer_list()
""".prep
}

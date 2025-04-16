# CMake Commands Used

[TOC]: # ""

- [cmake_minimum_required](#cmake_minimum_required)
- [add_subdirectory](#add_subdirectory)
- [link_directories](#link_directories)
- [project](#project)
- [set](#set)
- [CMAKE_TOOLCHAIN_FILE](#cmake_toolchain_file)
- [CMAKE_CXX_STANDARD](#cmake_cxx_standard)

## cmake_minimum_required

[CmakeMinimumRequired](https://cmake.org/cmake/help/v3.0/command/cmake_minimum_required.html)

Set the minimum required version of cmake for a project. Currently minimum versions are set to
3\.5 to 3.31. CMake versions lower than this are not supported because they are not supported by
more recent CMake releases.

```
cmake_minimum_required(VERSION major[.minor[.patch[.tweak]]...major2[.minor2[.patch2[.tweak2]]]]
                       [FATAL_ERROR])
```

If the current version of CMake is lower than that required it will stop processing the project
and report an error. When a version higher than 2.4 is specified the command implicitly invokes

```
cmake_policy(VERSION major[.minor[.patch[.tweak]]])
```

which sets the cmake policy version level to the version specified. When version 2.4 or lower is
given the command implicitly invokes

```
cmake_policy(VERSION 2.4)
```

which enables compatibility features for CMake 2.4 and lower.

The FATAL_ERROR option is accepted but ignored by CMake 2.6 and higher. It should be specified
so CMake versions 2.4 and lower fail with an error instead of just a warning.

## add_subdirectory

[AddSubdirectory](https://cmake.org/cmake/help/v3.0/command/add_subdirectory.html)

Add a subdirectory to the build.

```
add_subdirectory(source_dir [binary_dir] [EXCLUDE_FROM_ALL])
```

Add a subdirectory to the build. The source_dir specifies the directory in which the source
CMakeLists.txt and code files are located. If it is a relative path it will be evaluated with
respect to the current directory (the typical usage), but it may also be an absolute path. The
binary_dir specifies the directory in which to place the output files. If it is a relative path
it will be evaluated with respect to the current output directory, but it may also be an
absolute path. If binary_dir is not specified, the value of source_dir, before expanding any
relative path, will be used (the typical usage). The CMakeLists.txt file in the specified source
directory will be processed immediately by CMake before processing in the current input file
continues beyond this command.

If the EXCLUDE_FROM_ALL argument is provided then targets in the subdirectory will not be
included in the ALL target of the parent directory by default, and will be excluded from IDE
project files. Users must explicitly build targets in the subdirectory. This is meant for use
when the subdirectory contains a separate part of the project that is useful but not necessary,
such as a set of examples. Typically the subdirectory should contain its own project() command
invocation so that a full build system will be generated in the subdirectory (such as a VS IDE
solution file). Note that inter-target dependencies supercede this exclusion. If a target built
by the parent project depends on a target in the subdirectory, the dependee target will be
included in the parent project build system to satisfy the dependency.

## link_directories

[link_directories](https://cmake.org/cmake/help/v3.0/command/link_directories.html)

Specify directories in which the linker will look for libraries.

```
link_directories(directory1 directory2 ...)
```

Specify the paths in which the linker should search for libraries. The command will apply only
to targets created after it is called. Relative paths given to this command are interpreted as
relative to the current source directory, see CMP0015.

Note that this command is rarely necessary. Library locations returned by find_package() and
find_library() are absolute paths. Pass these absolute library file paths directly to the
target_link_libraries() command. CMake will ensure the linker finds them.

## project

[project](https://cmake.org/cmake/help/v3.0/command/project.html)

Set a name, version, and enable languages for the entire project.

```
project(<PROJECT-NAME> [LANGUAGES] [<language-name>...])
project(<PROJECT-NAME>
        [VERSION <major>[.<minor>[.<patch>[.<tweak>]]]]
        [LANGUAGES <language-name>...])
```

Sets the name of the project and stores the name in the
[PROJECT_NAME](https://cmake.org/cmake/help/v3.0/variable/PROJECT_NAME.html#variable:PROJECT_NAME
"PROJECT_NAME") variable. Additionally, this sets variables

* [PROJECT_SOURCE_DIR](https://cmake.org/cmake/help/v3.0/variable/PROJECT_SOURCE_DIR.html#variable:PROJECT_SOURCE_DIR
  "PROJECT_SOURCE_DIR"),
  [<PROJECT-NAME>_SOURCE_DIR](https://cmake.org/cmake/help/v3.0/variable/PROJECT-NAME_SOURCE_DIR.html#variable:%3CPROJECT-NAME%3E_SOURCE_DIR
  "<PROJECT-NAME>_SOURCE_DIR")
* [PROJECT_BINARY_DIR](https://cmake.org/cmake/help/v3.0/variable/PROJECT_BINARY_DIR.html#variable:PROJECT_BINARY_DIR
  "PROJECT_BINARY_DIR"),
  [<PROJECT-NAME>_BINARY_DIR](https://cmake.org/cmake/help/v3.0/variable/PROJECT-NAME_BINARY_DIR.html#variable:%3CPROJECT-NAME%3E_BINARY_DIR
  "<PROJECT-NAME>_BINARY_DIR")

If VERSION is specified, given components must be non-negative integers. If VERSION is not
specified, the default version is the empty string. The VERSION option may not be used unless
policy [CMP0048](https://cmake.org/cmake/help/v3.0/policy/CMP0048.html#policy:CMP0048 "CMP0048")
is set to NEW.

The [project()](https://cmake.org/cmake/help/v3.0/command/project.html#command:project
"project") command stores the version number and its components in variables

* [PROJECT_VERSION](https://cmake.org/cmake/help/v3.0/variable/PROJECT_VERSION.html#variable:PROJECT_VERSION
  "PROJECT_VERSION"),
  [<PROJECT-NAME>_VERSION](https://cmake.org/cmake/help/v3.0/variable/PROJECT-NAME_VERSION.html#variable:%3CPROJECT-NAME%3E_VERSION
  "<PROJECT-NAME>_VERSION")
* [PROJECT_VERSION_MAJOR](https://cmake.org/cmake/help/v3.0/variable/PROJECT_VERSION_MAJOR.html#variable:PROJECT_VERSION_MAJOR
  "PROJECT_VERSION_MAJOR"),
  [<PROJECT-NAME>_VERSION_MAJOR](https://cmake.org/cmake/help/v3.0/variable/PROJECT-NAME_VERSION_MAJOR.html#variable:%3CPROJECT-NAME%3E_VERSION_MAJOR
  "<PROJECT-NAME>_VERSION_MAJOR")
* [PROJECT_VERSION_MINOR](https://cmake.org/cmake/help/v3.0/variable/PROJECT_VERSION_MINOR.html#variable:PROJECT_VERSION_MINOR
  "PROJECT_VERSION_MINOR"),
  [<PROJECT-NAME>_VERSION_MINOR](https://cmake.org/cmake/help/v3.0/variable/PROJECT-NAME_VERSION_MINOR.html#variable:%3CPROJECT-NAME%3E_VERSION_MINOR
  "<PROJECT-NAME>_VERSION_MINOR")
* [PROJECT_VERSION_PATCH](https://cmake.org/cmake/help/v3.0/variable/PROJECT_VERSION_PATCH.html#variable:PROJECT_VERSION_PATCH
  "PROJECT_VERSION_PATCH"),
  [<PROJECT-NAME>_VERSION_PATCH](https://cmake.org/cmake/help/v3.0/variable/PROJECT-NAME_VERSION_PATCH.html#variable:%3CPROJECT-NAME%3E_VERSION_PATCH
  "<PROJECT-NAME>_VERSION_PATCH")
* [PROJECT_VERSION_TWEAK](https://cmake.org/cmake/help/v3.0/variable/PROJECT_VERSION_TWEAK.html#variable:PROJECT_VERSION_TWEAK
  "PROJECT_VERSION_TWEAK"),
  [<PROJECT-NAME>_VERSION_TWEAK](https://cmake.org/cmake/help/v3.0/variable/PROJECT-NAME_VERSION_TWEAK.html#variable:%3CPROJECT-NAME%3E_VERSION_TWEAK
  "<PROJECT-NAME>_VERSION_TWEAK")

Variables corresponding to unspecified versions are set to the empty string (if policy
[CMP0048](https://cmake.org/cmake/help/v3.0/policy/CMP0048.html#policy:CMP0048 "CMP0048") is set
to NEW).

Optionally you can specify which languages your project supports. Example languages are C, CXX
(i.e. C++), Fortran, etc. By default C and CXX are enabled if no language options are given.
Specify language NONE, or use the LANGUAGES keyword and list no languages, to skip enabling any
languages.

If a variable exists called
[CMAKE_PROJECT_<PROJECT-NAME>_INCLUDE](https://cmake.org/cmake/help/v3.0/variable/CMAKE_PROJECT_PROJECT-NAME_INCLUDE.html#variable:CMAKE_PROJECT_%3CPROJECT-NAME%3E_INCLUDE
"CMAKE_PROJECT_<PROJECT-NAME>_INCLUDE"), the file pointed to by that variable will be included
as the last step of the project command.

The top-level CMakeLists.txt file for a project must contain a literal, direct call to the
[project()](https://cmake.org/cmake/help/v3.0/command/project.html#command:project "project")
command; loading one through the
[include()](https://cmake.org/cmake/help/v3.0/command/include.html#command:include "include")
command is not sufficient. If no such call exists CMake will implicitly add one to the top that
enables the default languages (C and CXX).

## set

[set](https://cmake.org/cmake/help/v3.0/command/set.html)

Set a CMake, cache or environment variable to a given value.

```
set(<variable> <value>
    [[CACHE <type> <docstring> [FORCE]] | PARENT_SCOPE])
```

Within CMake sets \<variable\> to the value \<value\>. \<value\> is expanded before \<variable\>
is set to it. Normally, set will set a regular CMake variable. If CACHE is present, then the
\<variable\> is put in the cache instead, unless it is already in the cache. See section
'Variable types in CMake' below for details of regular and cache variables and their
interactions. If CACHE is used, \<type\> and \<docstring\> are required. \<type\> is used by the
CMake GUI to choose a widget with which the user sets a value. The value for \<type\> may be one
of

```
FILEPATH = File chooser dialog.
PATH     = Directory chooser dialog.
STRING   = Arbitrary string.
BOOL     = Boolean ON/OFF checkbox.
INTERNAL = No GUI entry (used for persistent variables).
```

If \<type\> is INTERNAL, the cache variable is marked as internal, and will not be shown to the
user in tools like cmake-gui. This is intended for values that should be persisted in the cache,
but which users should not normally change. INTERNAL implies FORCE.

Normally, set(...CACHE...) creates cache variables, but does not modify them. If FORCE is
specified, the value of the cache variable is set, even if the variable is already in the cache.
This should normally be avoided, as it will remove any changes to the cache variable's value by
the user.

If PARENT_SCOPE is present, the variable will be set in the scope above the current scope. Each
new directory or function creates a new scope. This command will set the value of a variable
into the parent directory or calling function (whichever is applicable to the case at hand).
PARENT_SCOPE cannot be combined with CACHE.

If \<value\> is not specified then the variable is removed instead of set. See also: the unset()
command.

```
set(<variable> <value1> ... <valueN>)
```

In this case \<variable\> is set to a semicolon separated list of values.

\<variable\> can be an environment variable such as:

```
set( ENV{PATH} /home/martink )
```

in which case the environment variable will be set.

**\* Variable types in CMake \***

In CMake there are two types of variables: normal variables and cache variables. Normal
variables are meant for the internal use of the script (just like variables in most programming
languages); they are not persisted across CMake runs. Cache variables (unless set with INTERNAL)
are mostly intended for configuration settings where the first CMake run determines a suitable
default value, which the user can then override, by editing the cache with tools such as ccmake
or cmake-gui. Cache variables are stored in the CMake cache file, and are persisted across CMake
runs.

Both types can exist at the same time with the same name but different values. When ${FOO} is
evaluated, CMake first looks for a normal variable 'FOO' in scope and uses it if set. If and
only if no normal variable exists then it falls back to the cache variable 'FOO'.

Some examples:

The code 'set(FOO "x")' sets the normal variable 'FOO'. It does not touch the cache, but it will
hide any existing cache value 'FOO'.

The code 'set(FOO "x" CACHE ...)' checks for 'FOO' in the cache, ignoring any normal variable of
the same name. If 'FOO' is in the cache then nothing happens to either the normal variable or
the cache variable. If 'FOO' is not in the cache, then it is added to the cache.

Finally, whenever a cache variable is added or modified by a command, CMake also *removes* the
normal variable of the same name from the current scope so that an immediately following
evaluation of it will expose the newly cached value.

Normally projects should avoid using normal and cache variables of the same name, as this
interaction can be hard to follow. However, in some situations it can be useful. One example
(used by some projects):

A project has a subproject in its source tree. The child project has its own CMakeLists.txt,
which is included from the parent CMakeLists.txt using add_subdirectory(). Now, if the parent
and the child project provide the same option (for example a compiler option), the parent gets
the first chance to add a user-editable option to the cache. Normally, the child would then use
the same value that the parent uses. However, it may be necessary to hard-code the value for the
child project's option while still allowing the user to edit the value used by the parent
project. The parent project can achieve this simply by setting a normal variable with the same
name as the option in a scope sufficient to hide the option's cache variable from the child
completely. The parent has already set the cache variable, so the child's set(...CACHE...) will
do nothing, and evaluating the option variable will use the value from the normal variable,
which hides the cache variable.

## CMAKE_TOOLCHAIN_FILE

    set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)

## CMAKE_CXX_STANDARD

    set(CMAKE_CXX_STANDARD)

# Arduino CMakeLists.txt file

```
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD)

set(PROJECT_NAME)
set(${CMAKE_PROJECT_NAME}_BOARD)
set(ARDUINO_CPU)
project(${PROJECT_NAME})

### Source (.cpp) and Header (.h) files
set(${PROJECT_NAME}_SRCS)
set(${PROJECT_NAME}_HDRS)

### Additional static libraries to include in the target.
set(${CMAKE_PROJECT_NAME}_LIBS)

### Main sketch file
set(${CMAKE_PROJECT_NAME}_SKETCH)

### Add project sub-directories into the build
add_subdirectory()

### Additional settings to add non-standard or your own Arduino libraries.
link_directories()

# For nested library sources replace ${LIB_NAME} with library name for each library
set(${LIB_NAME}_RECURSE true)

#### Additional settings for programmer. From programmers.txt
set(${CMAKE_PROJECT_NAME}_PROGRAMMER)
set(${CMAKE_PROJECT_NAME}_PORT)
set(<$SET_BOARD$>.upload.speed)

## Verbose build process
set(${CMAKE_PROJECT_NAME}_AFLAGS -v)

### Sketch and Arduino Library projects
generate_arduino_firmware(${CMAKE_PROJECT_NAME})

### Static Library projects
generate_arduino_library(${CMAKE_PROJECT_NAME})
```


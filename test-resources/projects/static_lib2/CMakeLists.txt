cmake_minimum_required(VERSION 3.5...3.31 FATAL_ERROR)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME static_lib2)

set(${CMAKE_PROJECT_NAME}_BOARD robotControl)
# set(ARDUINO_CPU)
project(${CMAKE_PROJECT_NAME})

# Define the source code for cpp files or default arduino sketch files
set(${CMAKE_PROJECT_NAME}_SRCS static_lib2.cpp)
set(${CMAKE_PROJECT_NAME}_HDRS static_lib2.h)

### Additional static libraries to include in the target.
set(${CMAKE_PROJECT_NAME}_LIBS)

### Main sketch file
# set(${CMAKE_PROJECT_NAME}_SKETCH static_lib2.ino)

### Add project directories into the build
# add_subdirectory()

### Additional settings to add non-standard or your own Arduino libraries.
# For this example (libs will contain additional arduino libraries)
# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files
# link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)
link_directories("${CMAKE_CURRENT_SOURCE_DIR}/")

# For nested library sources replace ${LIB_NAME} with library name for each library
# set(${LIB_NAME}_RECURSE true)

#### Additional settings for programmer. From programmers.txt
set(${CMAKE_PROJECT_NAME}_PROGRAMMER buspirate)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
# set(robotControl.upload.speed 9600)

## Verbose build process
# set(${CMAKE_PROJECT_NAME}_AFLAGS -v)


generate_arduino_library(${CMAKE_PROJECT_NAME})


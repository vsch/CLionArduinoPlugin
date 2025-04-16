cmake_minimum_required(VERSION 3.5...3.31 FATAL_ERROR)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(PROJECT_NAME sketch_2)

set(${PROJECT_NAME}_BOARD uno)
# set(ARDUINO_CPU)
project(${PROJECT_NAME})

# Define the source code for cpp files or default arduino sketch files
# set(${PROJECT_NAME}_SRCS)
set(${PROJECT_NAME}_HDRS User_Setup.h)

### Additional static libraries to include in the target.
set(${PROJECT_NAME}_LIBS)

### Main sketch file
set(${PROJECT_NAME}_SKETCH sketch_2.ino)

### Add project directories into the build

# add_subdirectory()
### Additional settings to add non-standard or your own Arduino libraries.
# For this example (libs will contain additional arduino libraries)
# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files
# link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)

# For nested library sources replace ${LIB_NAME} with library name for each library
# set(${LIB_NAME}_RECURSE true)

#### Additional settings for programmer. From programmers.txt
set(${PROJECT_NAME}_PROGRAMMER usbtinyisp)
set(${PROJECT_NAME}_PORT /dev/cu.Bluetooth-Incoming-Port)
# set(uno.upload.speed 9600)

## Verbose build process
# set(${PROJECT_NAME}_AFLAGS -v)

generate_arduino_firmware(${PROJECT_NAME})



cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(PROJECT_NAME static_lib1)

set(${PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 16MHzatmega328)
project(${PROJECT_NAME})

# Define additional source and header files or default arduino sketch files
set(${PROJECT_NAME}_SRCS static_lib1.cpp)
set(${PROJECT_NAME}_HDRS static_lib1.h)

### Additional static libraries to include in the target.
# set(${PROJECT_NAME}_LIBS)

### Main sketch file
# set(${PROJECT_NAME}_SKETCH static_lib1.ino)

### Add project directories into the build
# add_subdirectory()

### Additional settings to add non-standard or your own Arduino libraries.
# For this example (libs will contain additional arduino libraries)
# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files
# link_directories(${CMAKE_CURRENT_SOURCE_DIR}/)

# For nested library sources replace ${LIB_NAME} with library name for each library
# set(${LIB_NAME}_RECURSE true)

#### Additional settings for programmer. From programmers.txt
set(${PROJECT_NAME}_PROGRAMMER usbasp)
set(${PROJECT_NAME}_PORT /dev/cu.Bluetooth-Incoming-Port)
# set(pro.upload.speed 9600)

## Verbose build process
set(${PROJECT_NAME}_AFLAGS -v)


generate_arduino_library(${PROJECT_NAME})

print_board_list()
print_programmer_list()

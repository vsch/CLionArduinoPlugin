cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)    ## This must be set before project call
project(${CMAKE_PROJECT_NAME})

# Define the source code
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp)
# set(${CMAKE_PROJECT_NAME}_HDRS)
# set(${CMAKE_PROJECT_NAME}_LIBS)
# set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.ino)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)
# add_subdirectory()

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 9600)

generate_arduino_firmware(${PROJECT_NAME})

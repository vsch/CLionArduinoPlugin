cmake_minimum_required(VERSION 3.0.0)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(PROJECT_NAME tft_life)

set(${PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)    ## This must be set before project call
project(${PROJECT_NAME})

# Define the source code
set(${PROJECT_NAME}_SRCS tft_life.cpp)
# set(${PROJECT_NAME}_HDRS)
# set(${PROJECT_NAME}_LIBS)
# set(${PROJECT_NAME}_SKETCH tft_life.ino)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)
# add_subdirectory()

#### Uncomment below additional settings as needed.
set(${PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 9600)

generate_arduino_firmware(${PROJECT_NAME})

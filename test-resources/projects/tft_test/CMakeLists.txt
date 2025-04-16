cmake_minimum_required(VERSION 3.5...3.31 FATAL_ERROR)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(${CMAKE_PROJECT_NAME} tft_test)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)

project(${CMAKE_PROJECT_NAME})

#set(SOURCE_FILES Dda.cpp Dda.h test.cpp)
#set(${PROJECT_NAME}_SRCS ${SOURCE_FILES})
set(${CMAKE_PROJECT_NAME}_SKETCH tft_test.ino)

# Extra library directories
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})

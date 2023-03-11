---
title: CMake Builder Spec Test
author: Vladimir Schneider
version: 0.1
date: '2018-11-18'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Generic

actual file

```````````````````````````````` example(Generic: 1) options(board-pro)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

made_up_command(SOME_ARG, more args) 
another_made_up_command()

## This must be set before project call
set(${MAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)
project(${MAKE_PROJECT_NAME})
.
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

made_up_command(SOME_ARG, more args)
another_made_up_command()

## This must be set before project call
set(${MAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)
project(${MAKE_PROJECT_NAME})
.
CMakeFile[0, 377]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "CMAKE_CXX_STANDARD 98"] close:[140, 141, ")"]
    Argument[119, 137] text:[119, 137, "CMAKE_CXX_STANDARD"]
    Argument[138, 140] text:[138, 140, "98"]
  LineEnding[141, 142]
  Command[142, 174] text:[142, 145, "set"] open:[145, 146, "("] arguments:[146, 173, "CMAKE_PROJECT_NAME tft_life"] close:[173, 174, ")"]
    Argument[146, 164] text:[146, 164, "CMAKE_PROJECT_NAME"]
    Argument[165, 173] text:[165, 173, "tft_life"]
  LineEnding[174, 175]
  BlankLine[175, 176]
  Command[176, 212] text:[176, 191, "made_up_command"] open:[191, 192, "("] arguments:[192, 211, "SOME_ARG, more args"] close:[211, 212, ")"]
    Argument[192, 201] text:[192, 201, "SOME_ARG,"]
    Argument[202, 206] text:[202, 206, "more"]
    Argument[207, 211] text:[207, 211, "args"]
  LineEnding[213, 214]
  Command[214, 239] text:[214, 237, "another_made_up_command"] open:[237, 238, "("] arguments:[238, 238] close:[238, 239, ")"]
  LineEnding[239, 240]
  BlankLine[240, 241]
  LineComment[241, 281] open:[241, 242, "#"] text:[242, 281, "# This must be set before project call\n"]
  Command[281, 316] text:[281, 284, "set"] open:[284, 285, "("] arguments:[285, 315, "${MAKE_PROJECT_NAME}_BOARD pro"] close:[315, 316, ")"]
    Argument[285, 311] text:[285, 311, "${MAKE_PROJECT_NAME}_BOARD"]
    Argument[312, 315] text:[312, 315, "pro"]
  LineEnding[316, 317]
  Command[317, 347] text:[317, 320, "set"] open:[320, 321, "("] arguments:[321, 346, "ARDUINO_CPU 8MHzatmega328"] close:[346, 347, ")"]
    Argument[321, 332] text:[321, 332, "ARDUINO_CPU"]
    Argument[333, 346] text:[333, 346, "8MHzatmega328"]
  LineEnding[347, 348]
  Command[348, 377] text:[348, 355, "project"] open:[355, 356, "("] arguments:[356, 376, "${MAKE_PROJECT_NAME}"] close:[376, 377, ")"]
    Argument[356, 376] text:[356, 376, "${MAKE_PROJECT_NAME}"]
````````````````````````````````


## Arduno

actual file

```````````````````````````````` example(Arduno: 1) options(board-pro)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD uno)
set(ARDUINO_CPU none)

project(${CMAKE_PROJECT_NAME})

# Define the source code
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

### Add project sub-directories into the build
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set (${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

## This must be set before project call
set(${MAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)

project(${MAKE_PROJECT_NAME})

# Define the source code
set(${MAKE_PROJECT_NAME}_SRCS tft_life.cpp)
# set(${MAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

### Add project sub-directories into the build
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)

#### Uncomment below additional settings as needed.
set(${MAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${MAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${MAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 851]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "CMAKE_CXX_STANDARD 98"] close:[140, 141, ")"]
    Argument[119, 137] text:[119, 137, "CMAKE_CXX_STANDARD"]
    Argument[138, 140] text:[138, 140, "98"]
  LineEnding[141, 142]
  Command[142, 174] text:[142, 145, "set"] open:[145, 146, "("] arguments:[146, 173, "CMAKE_PROJECT_NAME tft_life"] close:[173, 174, ")"]
    Argument[146, 164] text:[146, 164, "CMAKE_PROJECT_NAME"]
    Argument[165, 173] text:[165, 173, "tft_life"]
  LineEnding[174, 175]
  BlankLine[175, 176]
  LineComment[176, 216] open:[176, 177, "#"] text:[177, 216, "# This must be set before project call\n"]
  Command[216, 252] text:[216, 219, "set"] open:[219, 220, "("] arguments:[220, 251, "${CMAKE_PROJECT_NAME}_BOARD uno"] close:[251, 252, ")"]
    Argument[220, 247] text:[220, 247, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[248, 251] text:[248, 251, "uno"]
  LineEnding[252, 253]
  Command[253, 274] text:[253, 256, "set"] open:[256, 257, "("] arguments:[257, 273, "ARDUINO_CPU none"] close:[273, 274, ")"]
    Argument[257, 268] text:[257, 268, "ARDUINO_CPU"]
    Argument[269, 273] text:[269, 273, "none"]
  LineEnding[274, 275]
  BlankLine[275, 276]
  Command[276, 306] text:[276, 283, "project"] open:[283, 284, "("] arguments:[284, 305, "${CMAKE_PROJECT_NAME}"] close:[305, 306, ")"]
    Argument[284, 305] text:[284, 305, "${CMAKE_PROJECT_NAME}"]
  LineEnding[306, 307]
  BlankLine[307, 308]
  LineComment[308, 333] open:[308, 309, "#"] text:[309, 333, " Define the source code\n"]
  Command[333, 377] text:[333, 336, "set"] open:[336, 337, "("] arguments:[337, 376, "${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp"] close:[376, 377, ")"]
    Argument[337, 363] text:[337, 363, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[364, 376] text:[364, 376, "tft_life.cpp"]
  LineEnding[377, 378]
  CommentedOutCommand[378, 425] comment:[378, 379, "#"] text:[379, 382, "set"] open:[382, 383, "("] arguments:[383, 424, "${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp"] close:[424, 425, ")"]
    Argument[383, 411] text:[383, 411, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[412, 424] text:[412, 424, "tft_life.cpp"]
  LineEnding[425, 426]
  Command[426, 474] text:[426, 442, "link_directories"] open:[442, 443, "("] arguments:[443, 473, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[473, 474, ")"]
    Argument[443, 473] text:[443, 473, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[474, 475]
  BlankLine[475, 476]
  LineComment[476, 523] open:[476, 477, "#"] text:[477, 523, "## Add project sub-directories into the build\n"]
  Command[523, 572] text:[523, 539, "add_subdirectory"] open:[539, 540, "("] arguments:[540, 571, "${CMAKE_CURRENT_SOURCE_DIR}/sub"] close:[571, 572, ")"]
    Argument[540, 571] text:[540, 571, "${CMAKE_CURRENT_SOURCE_DIR}/sub"]
  LineEnding[572, 573]
  BlankLine[573, 574]
  LineComment[574, 626] open:[574, 575, "#"] text:[575, 626, "### Uncomment below additional settings as needed.\n"]
  Command[626, 674] text:[626, 629, "set"] open:[629, 630, "("] arguments:[630, 673, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[673, 674, ")"]
    Argument[630, 662] text:[630, 662, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[663, 673] text:[663, 673, "avrispmkii"]
  LineEnding[674, 675]
  Command[675, 733] text:[675, 678, "set"] open:[678, 679, "("] arguments:[679, 732, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[732, 733, ")"]
    Argument[679, 705] text:[679, 705, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[706, 732] text:[706, 732, "/dev/cu.usbserial-00000000"]
  LineEnding[733, 734]
  Command[734, 771] text:[734, 737, "set"] open:[738, 739, "("] arguments:[739, 770, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[770, 771, ")"]
    Argument[739, 767] text:[739, 767, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[768, 770] text:[768, 770, "-v"]
  LineEnding[771, 772]
  CommentedOutCommand[772, 801] comment:[772, 773, "#"] text:[774, 777, "set"] open:[777, 778, "("] arguments:[778, 800, "pro.upload.speed 57600"] close:[800, 801, ")"]
    Argument[778, 794] text:[778, 794, "pro.upload.speed"]
    Argument[795, 800] text:[795, 800, "57600"]
  LineEnding[801, 802]
  BlankLine[802, 803]
  Command[803, 851] text:[803, 828, "generate_arduino_firmware"] open:[828, 829, "("] arguments:[829, 850, "${CMAKE_PROJECT_NAME}"] close:[850, 851, ")"]
    Argument[829, 850] text:[829, 850, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


actual file

```````````````````````````````` example(Arduno: 2) options(change-all)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD uno)
set(ARDUINO_CPU none)

project(${CMAKE_PROJECT_NAME})

# Define the source code
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp)
set(${CMAKE_PROJECT_NAME}_HDRS tft_life.h)
set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

### Add project sub-directories into the build
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)    
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000) 
set (${CMAKE_PROJECT_NAME}_AFLAGS -v) 

set(uno.upload.speed 57600)
set(libName_RECURSE false) 

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
cmake_minimum_required(VERSION Maj.Min.Rev.Twk)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
set(CMAKE_PROJECT_NAME tft_life)

## This must be set before project call
set(${MAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)

project(${MAKE_PROJECT_NAME})

# Define the source code
set(${MAKE_PROJECT_NAME}_SRCS setSrcs)
set(${MAKE_PROJECT_NAME}_HDRS setHdrs)
set(${MAKE_PROJECT_NAME}_SKETCH setSketch)
link_directories(linkDirectories)

### Add project sub-directories into the build
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
add_subdirectory(addSubdirectory)

#### Uncomment below additional settings as needed.
set(${MAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${MAKE_PROJECT_NAME}_PORT setPort)
set(${MAKE_PROJECT_NAME}_AFLAGS setAflags)

set(setBoard.upload.speed setUploadSpeed)
set(libName_RECURSE setLibNameRecurse)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 926]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "CMAKE_CXX_STANDARD 98"] close:[140, 141, ")"]
    Argument[119, 137] text:[119, 137, "CMAKE_CXX_STANDARD"]
    Argument[138, 140] text:[138, 140, "98"]
  LineEnding[141, 142]
  Command[142, 174] text:[142, 145, "set"] open:[145, 146, "("] arguments:[146, 173, "CMAKE_PROJECT_NAME tft_life"] close:[173, 174, ")"]
    Argument[146, 164] text:[146, 164, "CMAKE_PROJECT_NAME"]
    Argument[165, 173] text:[165, 173, "tft_life"]
  LineEnding[174, 175]
  BlankLine[175, 176]
  LineComment[176, 216] open:[176, 177, "#"] text:[177, 216, "# This must be set before project call\n"]
  Command[216, 252] text:[216, 219, "set"] open:[219, 220, "("] arguments:[220, 251, "${CMAKE_PROJECT_NAME}_BOARD uno"] close:[251, 252, ")"]
    Argument[220, 247] text:[220, 247, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[248, 251] text:[248, 251, "uno"]
  LineEnding[252, 253]
  Command[253, 274] text:[253, 256, "set"] open:[256, 257, "("] arguments:[257, 273, "ARDUINO_CPU none"] close:[273, 274, ")"]
    Argument[257, 268] text:[257, 268, "ARDUINO_CPU"]
    Argument[269, 273] text:[269, 273, "none"]
  LineEnding[274, 275]
  BlankLine[275, 276]
  Command[276, 306] text:[276, 283, "project"] open:[283, 284, "("] arguments:[284, 305, "${CMAKE_PROJECT_NAME}"] close:[305, 306, ")"]
    Argument[284, 305] text:[284, 305, "${CMAKE_PROJECT_NAME}"]
  LineEnding[306, 307]
  BlankLine[307, 308]
  LineComment[308, 333] open:[308, 309, "#"] text:[309, 333, " Define the source code\n"]
  Command[333, 377] text:[333, 336, "set"] open:[336, 337, "("] arguments:[337, 376, "${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp"] close:[376, 377, ")"]
    Argument[337, 363] text:[337, 363, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[364, 376] text:[364, 376, "tft_life.cpp"]
  LineEnding[377, 378]
  Command[378, 420] text:[378, 381, "set"] open:[381, 382, "("] arguments:[382, 419, "${CMAKE_PROJECT_NAME}_HDRS tft_life.h"] close:[419, 420, ")"]
    Argument[382, 408] text:[382, 408, "${CMAKE_PROJECT_NAME}_HDRS"]
    Argument[409, 419] text:[409, 419, "tft_life.h"]
  LineEnding[420, 421]
  Command[421, 467] text:[421, 424, "set"] open:[424, 425, "("] arguments:[425, 466, "${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp"] close:[466, 467, ")"]
    Argument[425, 453] text:[425, 453, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[454, 466] text:[454, 466, "tft_life.cpp"]
  LineEnding[467, 468]
  Command[468, 516] text:[468, 484, "link_directories"] open:[484, 485, "("] arguments:[485, 515, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[515, 516, ")"]
    Argument[485, 515] text:[485, 515, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[516, 517]
  BlankLine[517, 518]
  LineComment[518, 565] open:[518, 519, "#"] text:[519, 565, "## Add project sub-directories into the build\n"]
  Command[565, 614] text:[565, 581, "add_subdirectory"] open:[581, 582, "("] arguments:[582, 613, "${CMAKE_CURRENT_SOURCE_DIR}/sub"] close:[613, 614, ")"]
    Argument[582, 613] text:[582, 613, "${CMAKE_CURRENT_SOURCE_DIR}/sub"]
  LineEnding[614, 615]
  BlankLine[615, 616]
  LineComment[616, 668] open:[616, 617, "#"] text:[617, 668, "### Uncomment below additional settings as needed.\n"]
  Command[668, 716] text:[668, 671, "set"] open:[671, 672, "("] arguments:[672, 715, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[715, 716, ")"]
    Argument[672, 704] text:[672, 704, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[705, 715] text:[705, 715, "avrispmkii"]
  LineEnding[720, 721]
  Command[721, 779] text:[721, 724, "set"] open:[724, 725, "("] arguments:[725, 778, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[778, 779, ")"]
    Argument[725, 751] text:[725, 751, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[752, 778] text:[752, 778, "/dev/cu.usbserial-00000000"]
  LineEnding[780, 781]
  Command[781, 818] text:[781, 784, "set"] open:[785, 786, "("] arguments:[786, 817, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[817, 818, ")"]
    Argument[786, 814] text:[786, 814, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[815, 817] text:[815, 817, "-v"]
  LineEnding[819, 820]
  BlankLine[820, 821]
  Command[821, 848] text:[821, 824, "set"] open:[824, 825, "("] arguments:[825, 847, "uno.upload.speed 57600"] close:[847, 848, ")"]
    Argument[825, 841] text:[825, 841, "uno.upload.speed"]
    Argument[842, 847] text:[842, 847, "57600"]
  LineEnding[848, 849]
  Command[849, 875] text:[849, 852, "set"] open:[852, 853, "("] arguments:[853, 874, "libName_RECURSE false"] close:[874, 875, ")"]
    Argument[853, 868] text:[853, 868, "libName_RECURSE"]
    Argument[869, 874] text:[869, 874, "false"]
  LineEnding[876, 877]
  BlankLine[877, 878]
  Command[878, 926] text:[878, 903, "generate_arduino_firmware"] open:[903, 904, "("] arguments:[904, 925, "${CMAKE_PROJECT_NAME}"] close:[925, 926, ")"]
    Argument[904, 925] text:[904, 925, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 3) options(change-all)
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp)
set(${CMAKE_PROJECT_NAME}_HDRS tft_life.h)
.
set(${PROJECT_NAME}_SRCS setSrcs)
set(${PROJECT_NAME}_HDRS setHdrs)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 4) options(change-all)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. ${CMAKE_CURRENT_SOURCE_DIR}/sub)
.
link_directories(linkDirectories)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 5) options(change-all)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. linkDirectories)
.
link_directories(linkDirectories)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 6) options(change-all)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
.
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
add_subdirectory(addSubdirectory)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 7) options(change-all)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
.
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
add_subdirectory(addSubdirectory)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 8) options(change-all,  set-or-add)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
.
cmake_minimum_required(VERSION Maj.Min.Rev.Twk)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
set(MAKE_PROJECT_NAME setProjectName)
set(${MAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${MAKE_PROJECT_NAME})
set(${MAKE_PROJECT_NAME}_SRCS setSrcs)
set(${MAKE_PROJECT_NAME}_HDRS setHdrs)
link_directories(linkDirectories)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
set(${MAKE_PROJECT_NAME}_SKETCH setSketch)
set(${MAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${MAKE_PROJECT_NAME}_PORT setPort)
set(${MAKE_PROJECT_NAME}_AFLAGS setAflags)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
add_subdirectory(addSubdirectory)
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 9) options(change-all,  set-or-add)
## Top

## Before project
project(${CMAKE_PROJECT_NAME})
## After project

## Bottom
.
cmake_minimum_required(VERSION Maj.Min.Rev.Twk)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
set(MAKE_PROJECT_NAME setProjectName)
## Top

## Before project
set(${MAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${MAKE_PROJECT_NAME})
set(${MAKE_PROJECT_NAME}_SRCS setSrcs)
set(${MAKE_PROJECT_NAME}_HDRS setHdrs)
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${MAKE_PROJECT_NAME}_SKETCH setSketch)
set(${MAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${MAKE_PROJECT_NAME}_PORT setPort)
set(${MAKE_PROJECT_NAME}_AFLAGS setAflags)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 84]
  LineComment[0, 7] open:[0, 1, "#"] text:[1, 7, "# Top\n"]
  BlankLine[7, 8]
  LineComment[8, 26] open:[8, 9, "#"] text:[9, 26, "# Before project\n"]
  Command[26, 56] text:[26, 33, "project"] open:[33, 34, "("] arguments:[34, 55, "${CMAKE_PROJECT_NAME}"] close:[55, 56, ")"]
    Argument[34, 55] text:[34, 55, "${CMAKE_PROJECT_NAME}"]
  LineEnding[56, 57]
  LineComment[57, 74] open:[57, 58, "#"] text:[58, 74, "# After project\n"]
  BlankLine[74, 75]
  LineComment[75, 84] open:[75, 76, "#"] text:[76, 84, "# Bottom"]
````````````````````````````````


```````````````````````````````` example(Arduno: 10) options(add-project,  set-or-add)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME} )
generate_arduino_library(${CMAKE_PROJECT_NAME} )
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${A_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${A_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${A_PROJECT_NAME}_SKETCH setSketch)
set(${A_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${A_PROJECT_NAME}_PORT setPort)
set(${A_PROJECT_NAME}_AFLAGS setAflags)
set(${A_PROJECT_NAME}_HDRS setHdrs)
set(${A_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 719]
  Command[0, 47] text:[0, 3, "set"] open:[3, 4, "("] arguments:[4, 46, "CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile"] close:[46, 47, ")"]
    Argument[4, 24] text:[4, 24, "CMAKE_TOOLCHAIN_FILE"]
    Argument[25, 46] text:[25, 46, "setCmakeToolchainFile"]
  LineEnding[47, 48]
  Command[48, 91] text:[48, 51, "set"] open:[51, 52, "("] arguments:[52, 90, "CMAKE_CXX_STANDARD setCmakeCxxStandard"] close:[90, 91, ")"]
    Argument[52, 70] text:[52, 70, "CMAKE_CXX_STANDARD"]
    Argument[71, 90] text:[71, 90, "setCmakeCxxStandard"]
  LineEnding[91, 92]
  LineComment[92, 99] open:[92, 93, "#"] text:[93, 99, "# Top\n"]
  BlankLine[99, 100]
  LineComment[100, 118] open:[100, 101, "#"] text:[101, 118, "# Before project\n"]
  Command[118, 159] text:[118, 121, "set"] open:[121, 122, "("] arguments:[122, 158, "${CMAKE_PROJECT_NAME}_BOARD setBoard"] close:[158, 159, ")"]
    Argument[122, 149] text:[122, 149, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[150, 158] text:[150, 158, "setBoard"]
  LineEnding[159, 160]
  Command[160, 183] text:[160, 163, "set"] open:[163, 164, "("] arguments:[164, 182, "ARDUINO_CPU setCpu"] close:[182, 183, ")"]
    Argument[164, 175] text:[164, 175, "ARDUINO_CPU"]
    Argument[176, 182] text:[176, 182, "setCpu"]
  LineEnding[183, 184]
  Command[184, 217] text:[184, 200, "link_directories"] open:[200, 201, "("] arguments:[201, 216, "linkDirectories"] close:[216, 217, ")"]
    Argument[201, 216] text:[201, 216, "linkDirectories"]
  LineEnding[217, 218]
  Command[218, 251] text:[218, 234, "add_subdirectory"] open:[234, 235, "("] arguments:[235, 250, "addSubdirectory"] close:[250, 251, ")"]
    Argument[235, 250] text:[235, 250, "addSubdirectory"]
  LineEnding[251, 252]
  Command[252, 295] text:[252, 255, "set"] open:[255, 256, "("] arguments:[256, 294, "${CMAKE_PROJECT_NAME}_SKETCH setSketch"] close:[294, 295, ")"]
    Argument[256, 284] text:[256, 284, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[285, 294] text:[285, 294, "setSketch"]
  LineEnding[295, 296]
  Command[296, 347] text:[296, 299, "set"] open:[299, 300, "("] arguments:[300, 346, "${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer"] close:[346, 347, ")"]
    Argument[300, 332] text:[300, 332, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[333, 346] text:[333, 346, "setProgrammer"]
  LineEnding[347, 348]
  Command[348, 387] text:[348, 351, "set"] open:[351, 352, "("] arguments:[352, 386, "${CMAKE_PROJECT_NAME}_PORT setPort"] close:[386, 387, ")"]
    Argument[352, 378] text:[352, 378, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[379, 386] text:[379, 386, "setPort"]
  LineEnding[387, 388]
  Command[388, 431] text:[388, 391, "set"] open:[391, 392, "("] arguments:[392, 430, "${CMAKE_PROJECT_NAME}_AFLAGS setAflags"] close:[430, 431, ")"]
    Argument[392, 420] text:[392, 420, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[421, 430] text:[421, 430, "setAflags"]
  LineEnding[431, 432]
  Command[432, 471] text:[432, 435, "set"] open:[435, 436, "("] arguments:[436, 470, "${CMAKE_PROJECT_NAME}_HDRS setHdrs"] close:[470, 471, ")"]
    Argument[436, 462] text:[436, 462, "${CMAKE_PROJECT_NAME}_HDRS"]
    Argument[463, 470] text:[463, 470, "setHdrs"]
  LineEnding[471, 472]
  Command[472, 511] text:[472, 475, "set"] open:[475, 476, "("] arguments:[476, 510, "${CMAKE_PROJECT_NAME}_SRCS setSrcs"] close:[510, 511, ")"]
    Argument[476, 502] text:[476, 502, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[503, 510] text:[503, 510, "setSrcs"]
  LineEnding[511, 512]
  Command[512, 550] text:[512, 515, "set"] open:[515, 516, "("] arguments:[516, 549, "libName_RECURSE setLibNameRecurse"] close:[549, 550, ")"]
    Argument[516, 531] text:[516, 531, "libName_RECURSE"]
    Argument[532, 549] text:[532, 549, "setLibNameRecurse"]
  LineEnding[550, 551]
  Command[551, 592] text:[551, 554, "set"] open:[554, 555, "("] arguments:[555, 591, "setBoard.upload.speed setUploadSpeed"] close:[591, 592, ")"]
    Argument[555, 576] text:[555, 576, "setBoard.upload.speed"]
    Argument[577, 591] text:[577, 591, "setUploadSpeed"]
  LineEnding[592, 593]
  LineComment[593, 610] open:[593, 594, "#"] text:[594, 610, "# After project\n"]
  BlankLine[610, 611]
  LineComment[611, 621] open:[611, 612, "#"] text:[612, 621, "# Bottom\n"]
  Command[621, 670] text:[621, 646, "generate_arduino_firmware"] open:[646, 647, "("] arguments:[647, 669, "${CMAKE_PROJECT_NAME} "] close:[669, 670, ")"]
    Argument[647, 668] text:[647, 668, "${CMAKE_PROJECT_NAME}"]
  LineEnding[670, 671]
  Command[671, 719] text:[671, 695, "generate_arduino_library"] open:[695, 696, "("] arguments:[696, 718, "${CMAKE_PROJECT_NAME} "] close:[718, 719, ")"]
    Argument[696, 717] text:[696, 717, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


test uncomment command

```````````````````````````````` example(Arduno: 11) options(add-project,  set-or-add)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
#set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME} )
generate_arduino_library(${CMAKE_PROJECT_NAME} )
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${A_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${A_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${A_PROJECT_NAME}_SKETCH setSketch)
set(${A_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${A_PROJECT_NAME}_PORT setPort)
set(${A_PROJECT_NAME}_AFLAGS setAflags)
set(${A_PROJECT_NAME}_HDRS setHdrs)
set(${A_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 720]
  Command[0, 47] text:[0, 3, "set"] open:[3, 4, "("] arguments:[4, 46, "CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile"] close:[46, 47, ")"]
    Argument[4, 24] text:[4, 24, "CMAKE_TOOLCHAIN_FILE"]
    Argument[25, 46] text:[25, 46, "setCmakeToolchainFile"]
  LineEnding[47, 48]
  Command[48, 91] text:[48, 51, "set"] open:[51, 52, "("] arguments:[52, 90, "CMAKE_CXX_STANDARD setCmakeCxxStandard"] close:[90, 91, ")"]
    Argument[52, 70] text:[52, 70, "CMAKE_CXX_STANDARD"]
    Argument[71, 90] text:[71, 90, "setCmakeCxxStandard"]
  LineEnding[91, 92]
  LineComment[92, 99] open:[92, 93, "#"] text:[93, 99, "# Top\n"]
  BlankLine[99, 100]
  LineComment[100, 118] open:[100, 101, "#"] text:[101, 118, "# Before project\n"]
  Command[118, 159] text:[118, 121, "set"] open:[121, 122, "("] arguments:[122, 158, "${CMAKE_PROJECT_NAME}_BOARD setBoard"] close:[158, 159, ")"]
    Argument[122, 149] text:[122, 149, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[150, 158] text:[150, 158, "setBoard"]
  LineEnding[159, 160]
  Command[160, 183] text:[160, 163, "set"] open:[163, 164, "("] arguments:[164, 182, "ARDUINO_CPU setCpu"] close:[182, 183, ")"]
    Argument[164, 175] text:[164, 175, "ARDUINO_CPU"]
    Argument[176, 182] text:[176, 182, "setCpu"]
  LineEnding[183, 184]
  Command[184, 217] text:[184, 200, "link_directories"] open:[200, 201, "("] arguments:[201, 216, "linkDirectories"] close:[216, 217, ")"]
    Argument[201, 216] text:[201, 216, "linkDirectories"]
  LineEnding[217, 218]
  Command[218, 251] text:[218, 234, "add_subdirectory"] open:[234, 235, "("] arguments:[235, 250, "addSubdirectory"] close:[250, 251, ")"]
    Argument[235, 250] text:[235, 250, "addSubdirectory"]
  LineEnding[251, 252]
  Command[252, 295] text:[252, 255, "set"] open:[255, 256, "("] arguments:[256, 294, "${CMAKE_PROJECT_NAME}_SKETCH setSketch"] close:[294, 295, ")"]
    Argument[256, 284] text:[256, 284, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[285, 294] text:[285, 294, "setSketch"]
  LineEnding[295, 296]
  CommentedOutCommand[296, 348] comment:[296, 297, "#"] text:[297, 300, "set"] open:[300, 301, "("] arguments:[301, 347, "${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer"] close:[347, 348, ")"]
    Argument[301, 333] text:[301, 333, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[334, 347] text:[334, 347, "setProgrammer"]
  LineEnding[348, 349]
  Command[349, 388] text:[349, 352, "set"] open:[352, 353, "("] arguments:[353, 387, "${CMAKE_PROJECT_NAME}_PORT setPort"] close:[387, 388, ")"]
    Argument[353, 379] text:[353, 379, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[380, 387] text:[380, 387, "setPort"]
  LineEnding[388, 389]
  Command[389, 432] text:[389, 392, "set"] open:[392, 393, "("] arguments:[393, 431, "${CMAKE_PROJECT_NAME}_AFLAGS setAflags"] close:[431, 432, ")"]
    Argument[393, 421] text:[393, 421, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[422, 431] text:[422, 431, "setAflags"]
  LineEnding[432, 433]
  Command[433, 472] text:[433, 436, "set"] open:[436, 437, "("] arguments:[437, 471, "${CMAKE_PROJECT_NAME}_HDRS setHdrs"] close:[471, 472, ")"]
    Argument[437, 463] text:[437, 463, "${CMAKE_PROJECT_NAME}_HDRS"]
    Argument[464, 471] text:[464, 471, "setHdrs"]
  LineEnding[472, 473]
  Command[473, 512] text:[473, 476, "set"] open:[476, 477, "("] arguments:[477, 511, "${CMAKE_PROJECT_NAME}_SRCS setSrcs"] close:[511, 512, ")"]
    Argument[477, 503] text:[477, 503, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[504, 511] text:[504, 511, "setSrcs"]
  LineEnding[512, 513]
  Command[513, 551] text:[513, 516, "set"] open:[516, 517, "("] arguments:[517, 550, "libName_RECURSE setLibNameRecurse"] close:[550, 551, ")"]
    Argument[517, 532] text:[517, 532, "libName_RECURSE"]
    Argument[533, 550] text:[533, 550, "setLibNameRecurse"]
  LineEnding[551, 552]
  Command[552, 593] text:[552, 555, "set"] open:[555, 556, "("] arguments:[556, 592, "setBoard.upload.speed setUploadSpeed"] close:[592, 593, ")"]
    Argument[556, 577] text:[556, 577, "setBoard.upload.speed"]
    Argument[578, 592] text:[578, 592, "setUploadSpeed"]
  LineEnding[593, 594]
  LineComment[594, 611] open:[594, 595, "#"] text:[595, 611, "# After project\n"]
  BlankLine[611, 612]
  LineComment[612, 622] open:[612, 613, "#"] text:[613, 622, "# Bottom\n"]
  Command[622, 671] text:[622, 647, "generate_arduino_firmware"] open:[647, 648, "("] arguments:[648, 670, "${CMAKE_PROJECT_NAME} "] close:[670, 671, ")"]
    Argument[648, 669] text:[648, 669, "${CMAKE_PROJECT_NAME}"]
  LineEnding[671, 672]
  Command[672, 720] text:[672, 696, "generate_arduino_library"] open:[696, 697, "("] arguments:[697, 719, "${CMAKE_PROJECT_NAME} "] close:[719, 720, ")"]
    Argument[697, 718] text:[697, 718, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


test uncomment command

```````````````````````````````` example(Arduno: 12) options(add-project,  set-or-add)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
#project(${CMAKE_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
#set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME} )
generate_arduino_library(${CMAKE_PROJECT_NAME} )
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${A_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${A_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${A_PROJECT_NAME}_SKETCH setSketch)
set(${A_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${A_PROJECT_NAME}_PORT setPort)
set(${A_PROJECT_NAME}_AFLAGS setAflags)
set(${A_PROJECT_NAME}_HDRS setHdrs)
set(${A_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 752]
  Command[0, 47] text:[0, 3, "set"] open:[3, 4, "("] arguments:[4, 46, "CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile"] close:[46, 47, ")"]
    Argument[4, 24] text:[4, 24, "CMAKE_TOOLCHAIN_FILE"]
    Argument[25, 46] text:[25, 46, "setCmakeToolchainFile"]
  LineEnding[47, 48]
  Command[48, 91] text:[48, 51, "set"] open:[51, 52, "("] arguments:[52, 90, "CMAKE_CXX_STANDARD setCmakeCxxStandard"] close:[90, 91, ")"]
    Argument[52, 70] text:[52, 70, "CMAKE_CXX_STANDARD"]
    Argument[71, 90] text:[71, 90, "setCmakeCxxStandard"]
  LineEnding[91, 92]
  LineComment[92, 99] open:[92, 93, "#"] text:[93, 99, "# Top\n"]
  BlankLine[99, 100]
  LineComment[100, 118] open:[100, 101, "#"] text:[101, 118, "# Before project\n"]
  Command[118, 159] text:[118, 121, "set"] open:[121, 122, "("] arguments:[122, 158, "${CMAKE_PROJECT_NAME}_BOARD setBoard"] close:[158, 159, ")"]
    Argument[122, 149] text:[122, 149, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[150, 158] text:[150, 158, "setBoard"]
  LineEnding[159, 160]
  Command[160, 183] text:[160, 163, "set"] open:[163, 164, "("] arguments:[164, 182, "ARDUINO_CPU setCpu"] close:[182, 183, ")"]
    Argument[164, 175] text:[164, 175, "ARDUINO_CPU"]
    Argument[176, 182] text:[176, 182, "setCpu"]
  LineEnding[183, 184]
  CommentedOutCommand[184, 215] comment:[184, 185, "#"] text:[185, 192, "project"] open:[192, 193, "("] arguments:[193, 214, "${CMAKE_PROJECT_NAME}"] close:[214, 215, ")"]
    Argument[193, 214] text:[193, 214, "${CMAKE_PROJECT_NAME}"]
  LineEnding[215, 216]
  Command[216, 249] text:[216, 232, "link_directories"] open:[232, 233, "("] arguments:[233, 248, "linkDirectories"] close:[248, 249, ")"]
    Argument[233, 248] text:[233, 248, "linkDirectories"]
  LineEnding[249, 250]
  Command[250, 283] text:[250, 266, "add_subdirectory"] open:[266, 267, "("] arguments:[267, 282, "addSubdirectory"] close:[282, 283, ")"]
    Argument[267, 282] text:[267, 282, "addSubdirectory"]
  LineEnding[283, 284]
  Command[284, 327] text:[284, 287, "set"] open:[287, 288, "("] arguments:[288, 326, "${CMAKE_PROJECT_NAME}_SKETCH setSketch"] close:[326, 327, ")"]
    Argument[288, 316] text:[288, 316, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[317, 326] text:[317, 326, "setSketch"]
  LineEnding[327, 328]
  CommentedOutCommand[328, 380] comment:[328, 329, "#"] text:[329, 332, "set"] open:[332, 333, "("] arguments:[333, 379, "${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer"] close:[379, 380, ")"]
    Argument[333, 365] text:[333, 365, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[366, 379] text:[366, 379, "setProgrammer"]
  LineEnding[380, 381]
  Command[381, 420] text:[381, 384, "set"] open:[384, 385, "("] arguments:[385, 419, "${CMAKE_PROJECT_NAME}_PORT setPort"] close:[419, 420, ")"]
    Argument[385, 411] text:[385, 411, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[412, 419] text:[412, 419, "setPort"]
  LineEnding[420, 421]
  Command[421, 464] text:[421, 424, "set"] open:[424, 425, "("] arguments:[425, 463, "${CMAKE_PROJECT_NAME}_AFLAGS setAflags"] close:[463, 464, ")"]
    Argument[425, 453] text:[425, 453, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[454, 463] text:[454, 463, "setAflags"]
  LineEnding[464, 465]
  Command[465, 504] text:[465, 468, "set"] open:[468, 469, "("] arguments:[469, 503, "${CMAKE_PROJECT_NAME}_HDRS setHdrs"] close:[503, 504, ")"]
    Argument[469, 495] text:[469, 495, "${CMAKE_PROJECT_NAME}_HDRS"]
    Argument[496, 503] text:[496, 503, "setHdrs"]
  LineEnding[504, 505]
  Command[505, 544] text:[505, 508, "set"] open:[508, 509, "("] arguments:[509, 543, "${CMAKE_PROJECT_NAME}_SRCS setSrcs"] close:[543, 544, ")"]
    Argument[509, 535] text:[509, 535, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[536, 543] text:[536, 543, "setSrcs"]
  LineEnding[544, 545]
  Command[545, 583] text:[545, 548, "set"] open:[548, 549, "("] arguments:[549, 582, "libName_RECURSE setLibNameRecurse"] close:[582, 583, ")"]
    Argument[549, 564] text:[549, 564, "libName_RECURSE"]
    Argument[565, 582] text:[565, 582, "setLibNameRecurse"]
  LineEnding[583, 584]
  Command[584, 625] text:[584, 587, "set"] open:[587, 588, "("] arguments:[588, 624, "setBoard.upload.speed setUploadSpeed"] close:[624, 625, ")"]
    Argument[588, 609] text:[588, 609, "setBoard.upload.speed"]
    Argument[610, 624] text:[610, 624, "setUploadSpeed"]
  LineEnding[625, 626]
  LineComment[626, 643] open:[626, 627, "#"] text:[627, 643, "# After project\n"]
  BlankLine[643, 644]
  LineComment[644, 654] open:[644, 645, "#"] text:[645, 654, "# Bottom\n"]
  Command[654, 703] text:[654, 679, "generate_arduino_firmware"] open:[679, 680, "("] arguments:[680, 702, "${CMAKE_PROJECT_NAME} "] close:[702, 703, ")"]
    Argument[680, 701] text:[680, 701, "${CMAKE_PROJECT_NAME}"]
  LineEnding[703, 704]
  Command[704, 752] text:[704, 728, "generate_arduino_library"] open:[728, 729, "("] arguments:[729, 751, "${CMAKE_PROJECT_NAME} "] close:[751, 752, ")"]
    Argument[729, 750] text:[729, 750, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


test takes uncommented over commented

```````````````````````````````` example(Arduno: 13) options(change-all,  set-or-add)
#project(${CMAKE_PROJECT_NAME})
## Top

## Before project
project(${CMAKE_PROJECT_NAME})
## After project

## Bottom
.
cmake_minimum_required(VERSION Maj.Min.Rev.Twk)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
set(MAKE_PROJECT_NAME setProjectName)
# project(${CMAKE_PROJECT_NAME})
## Top

## Before project
set(${MAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${MAKE_PROJECT_NAME})
set(${MAKE_PROJECT_NAME}_SRCS setSrcs)
set(${MAKE_PROJECT_NAME}_HDRS setHdrs)
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${MAKE_PROJECT_NAME}_SKETCH setSketch)
set(${MAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${MAKE_PROJECT_NAME}_PORT setPort)
set(${MAKE_PROJECT_NAME}_AFLAGS setAflags)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 116]
  CommentedOutCommand[0, 31] comment:[0, 1, "#"] text:[1, 8, "project"] open:[8, 9, "("] arguments:[9, 30, "${CMAKE_PROJECT_NAME}"] close:[30, 31, ")"]
    Argument[9, 30] text:[9, 30, "${CMAKE_PROJECT_NAME}"]
  LineEnding[31, 32]
  LineComment[32, 39] open:[32, 33, "#"] text:[33, 39, "# Top\n"]
  BlankLine[39, 40]
  LineComment[40, 58] open:[40, 41, "#"] text:[41, 58, "# Before project\n"]
  Command[58, 88] text:[58, 65, "project"] open:[65, 66, "("] arguments:[66, 87, "${CMAKE_PROJECT_NAME}"] close:[87, 88, ")"]
    Argument[66, 87] text:[66, 87, "${CMAKE_PROJECT_NAME}"]
  LineEnding[88, 89]
  LineComment[89, 106] open:[89, 90, "#"] text:[90, 106, "# After project\n"]
  BlankLine[106, 107]
  LineComment[107, 116] open:[107, 108, "#"] text:[108, 116, "# Bottom"]
````````````````````````````````


test commented suppression command

```````````````````````````````` example(Arduno: 14) options(no-comment-unused)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
#project(${CMAKE_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
#set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME} )
generate_arduino_library(${CMAKE_PROJECT_NAME} )
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
# project(${CMAKE_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${PROJECT_NAME}_SKETCH setSketch)
# set(${PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${PROJECT_NAME}_AFLAGS setAflags)
set(${PROJECT_NAME}_HDRS setHdrs)
set(${PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 752]
  Command[0, 47] text:[0, 3, "set"] open:[3, 4, "("] arguments:[4, 46, "CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile"] close:[46, 47, ")"]
    Argument[4, 24] text:[4, 24, "CMAKE_TOOLCHAIN_FILE"]
    Argument[25, 46] text:[25, 46, "setCmakeToolchainFile"]
  LineEnding[47, 48]
  Command[48, 91] text:[48, 51, "set"] open:[51, 52, "("] arguments:[52, 90, "CMAKE_CXX_STANDARD setCmakeCxxStandard"] close:[90, 91, ")"]
    Argument[52, 70] text:[52, 70, "CMAKE_CXX_STANDARD"]
    Argument[71, 90] text:[71, 90, "setCmakeCxxStandard"]
  LineEnding[91, 92]
  LineComment[92, 99] open:[92, 93, "#"] text:[93, 99, "# Top\n"]
  BlankLine[99, 100]
  LineComment[100, 118] open:[100, 101, "#"] text:[101, 118, "# Before project\n"]
  Command[118, 159] text:[118, 121, "set"] open:[121, 122, "("] arguments:[122, 158, "${CMAKE_PROJECT_NAME}_BOARD setBoard"] close:[158, 159, ")"]
    Argument[122, 149] text:[122, 149, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[150, 158] text:[150, 158, "setBoard"]
  LineEnding[159, 160]
  Command[160, 183] text:[160, 163, "set"] open:[163, 164, "("] arguments:[164, 182, "ARDUINO_CPU setCpu"] close:[182, 183, ")"]
    Argument[164, 175] text:[164, 175, "ARDUINO_CPU"]
    Argument[176, 182] text:[176, 182, "setCpu"]
  LineEnding[183, 184]
  CommentedOutCommand[184, 215] comment:[184, 185, "#"] text:[185, 192, "project"] open:[192, 193, "("] arguments:[193, 214, "${CMAKE_PROJECT_NAME}"] close:[214, 215, ")"]
    Argument[193, 214] text:[193, 214, "${CMAKE_PROJECT_NAME}"]
  LineEnding[215, 216]
  Command[216, 249] text:[216, 232, "link_directories"] open:[232, 233, "("] arguments:[233, 248, "linkDirectories"] close:[248, 249, ")"]
    Argument[233, 248] text:[233, 248, "linkDirectories"]
  LineEnding[249, 250]
  Command[250, 283] text:[250, 266, "add_subdirectory"] open:[266, 267, "("] arguments:[267, 282, "addSubdirectory"] close:[282, 283, ")"]
    Argument[267, 282] text:[267, 282, "addSubdirectory"]
  LineEnding[283, 284]
  Command[284, 327] text:[284, 287, "set"] open:[287, 288, "("] arguments:[288, 326, "${CMAKE_PROJECT_NAME}_SKETCH setSketch"] close:[326, 327, ")"]
    Argument[288, 316] text:[288, 316, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[317, 326] text:[317, 326, "setSketch"]
  LineEnding[327, 328]
  CommentedOutCommand[328, 380] comment:[328, 329, "#"] text:[329, 332, "set"] open:[332, 333, "("] arguments:[333, 379, "${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer"] close:[379, 380, ")"]
    Argument[333, 365] text:[333, 365, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[366, 379] text:[366, 379, "setProgrammer"]
  LineEnding[380, 381]
  Command[381, 420] text:[381, 384, "set"] open:[384, 385, "("] arguments:[385, 419, "${CMAKE_PROJECT_NAME}_PORT setPort"] close:[419, 420, ")"]
    Argument[385, 411] text:[385, 411, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[412, 419] text:[412, 419, "setPort"]
  LineEnding[420, 421]
  Command[421, 464] text:[421, 424, "set"] open:[424, 425, "("] arguments:[425, 463, "${CMAKE_PROJECT_NAME}_AFLAGS setAflags"] close:[463, 464, ")"]
    Argument[425, 453] text:[425, 453, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[454, 463] text:[454, 463, "setAflags"]
  LineEnding[464, 465]
  Command[465, 504] text:[465, 468, "set"] open:[468, 469, "("] arguments:[469, 503, "${CMAKE_PROJECT_NAME}_HDRS setHdrs"] close:[503, 504, ")"]
    Argument[469, 495] text:[469, 495, "${CMAKE_PROJECT_NAME}_HDRS"]
    Argument[496, 503] text:[496, 503, "setHdrs"]
  LineEnding[504, 505]
  Command[505, 544] text:[505, 508, "set"] open:[508, 509, "("] arguments:[509, 543, "${CMAKE_PROJECT_NAME}_SRCS setSrcs"] close:[543, 544, ")"]
    Argument[509, 535] text:[509, 535, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[536, 543] text:[536, 543, "setSrcs"]
  LineEnding[544, 545]
  Command[545, 583] text:[545, 548, "set"] open:[548, 549, "("] arguments:[549, 582, "libName_RECURSE setLibNameRecurse"] close:[582, 583, ")"]
    Argument[549, 564] text:[549, 564, "libName_RECURSE"]
    Argument[565, 582] text:[565, 582, "setLibNameRecurse"]
  LineEnding[583, 584]
  Command[584, 625] text:[584, 587, "set"] open:[587, 588, "("] arguments:[588, 624, "setBoard.upload.speed setUploadSpeed"] close:[624, 625, ")"]
    Argument[588, 609] text:[588, 609, "setBoard.upload.speed"]
    Argument[610, 624] text:[610, 624, "setUploadSpeed"]
  LineEnding[625, 626]
  LineComment[626, 643] open:[626, 627, "#"] text:[627, 643, "# After project\n"]
  BlankLine[643, 644]
  LineComment[644, 654] open:[644, 645, "#"] text:[645, 654, "# Bottom\n"]
  Command[654, 703] text:[654, 679, "generate_arduino_firmware"] open:[679, 680, "("] arguments:[680, 702, "${CMAKE_PROJECT_NAME} "] close:[702, 703, ")"]
    Argument[680, 701] text:[680, 701, "${CMAKE_PROJECT_NAME}"]
  LineEnding[703, 704]
  Command[704, 752] text:[704, 728, "generate_arduino_library"] open:[728, 729, "("] arguments:[729, 751, "${CMAKE_PROJECT_NAME} "] close:[751, 752, ")"]
    Argument[729, 750] text:[729, 750, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


```````````````````````````````` example(Arduno: 15) options(board-pro)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_PROJECT_NAME tft_life)

set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)    ## This must be set before project call
project(${CMAKE_PROJECT_NAME})

# Define the source code
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_PROJECT_NAME tft_life)

set(${MAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)    ## This must be set before project call
project(${MAKE_PROJECT_NAME})

# Define the source code
set(${MAKE_PROJECT_NAME}_SRCS tft_life.cpp)
# set(${MAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

#### Uncomment below additional settings as needed.
set(${MAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${MAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${MAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 734]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 112] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 111, "CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[111, 112, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 111] text:[63, 111, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"]
  LineEnding[112, 113]
  Command[113, 145] text:[113, 116, "set"] open:[116, 117, "("] arguments:[117, 144, "CMAKE_PROJECT_NAME tft_life"] close:[144, 145, ")"]
    Argument[117, 135] text:[117, 135, "CMAKE_PROJECT_NAME"]
    Argument[136, 144] text:[136, 144, "tft_life"]
  LineEnding[145, 146]
  BlankLine[146, 147]
  Command[147, 183] text:[147, 150, "set"] open:[150, 151, "("] arguments:[151, 182, "${CMAKE_PROJECT_NAME}_BOARD pro"] close:[182, 183, ")"]
    Argument[151, 178] text:[151, 178, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[179, 182] text:[179, 182, "pro"]
  LineEnding[183, 184]
  Command[184, 214] text:[184, 187, "set"] open:[187, 188, "("] arguments:[188, 213, "ARDUINO_CPU 8MHzatmega328"] close:[213, 214, ")"]
    Argument[188, 199] text:[188, 199, "ARDUINO_CPU"]
    Argument[200, 213] text:[200, 213, "8MHzatmega328"]
  LineComment[214, 258] spaces:[214, 218, "    "] open:[214, 215, " "] text:[215, 258, "   ## This must be set before project call\n"]
  Command[258, 288] text:[258, 265, "project"] open:[265, 266, "("] arguments:[266, 287, "${CMAKE_PROJECT_NAME}"] close:[287, 288, ")"]
    Argument[266, 287] text:[266, 287, "${CMAKE_PROJECT_NAME}"]
  LineEnding[288, 289]
  BlankLine[289, 290]
  LineComment[290, 315] open:[290, 291, "#"] text:[291, 315, " Define the source code\n"]
  Command[315, 359] text:[315, 318, "set"] open:[318, 319, "("] arguments:[319, 358, "${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp"] close:[358, 359, ")"]
    Argument[319, 345] text:[319, 345, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[346, 358] text:[346, 358, "tft_life.cpp"]
  LineEnding[359, 360]
  CommentedOutCommand[360, 407] comment:[360, 361, "#"] text:[361, 364, "set"] open:[364, 365, "("] arguments:[365, 406, "${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp"] close:[406, 407, ")"]
    Argument[365, 393] text:[365, 393, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[394, 406] text:[394, 406, "tft_life.cpp"]
  LineEnding[407, 408]
  Command[408, 456] text:[408, 424, "link_directories"] open:[424, 425, "("] arguments:[425, 455, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[455, 456, ")"]
    Argument[425, 455] text:[425, 455, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[456, 457]
  BlankLine[457, 458]
  LineComment[458, 510] open:[458, 459, "#"] text:[459, 510, "### Uncomment below additional settings as needed.\n"]
  Command[510, 558] text:[510, 513, "set"] open:[513, 514, "("] arguments:[514, 557, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[557, 558, ")"]
    Argument[514, 546] text:[514, 546, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[547, 557] text:[547, 557, "avrispmkii"]
  LineEnding[558, 559]
  Command[559, 617] text:[559, 562, "set"] open:[562, 563, "("] arguments:[563, 616, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[616, 617, ")"]
    Argument[563, 589] text:[563, 589, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[590, 616] text:[590, 616, "/dev/cu.usbserial-00000000"]
  LineEnding[617, 618]
  Command[618, 654] text:[618, 621, "set"] open:[621, 622, "("] arguments:[622, 653, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[653, 654, ")"]
    Argument[622, 650] text:[622, 650, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[651, 653] text:[651, 653, "-v"]
  LineEnding[654, 655]
  CommentedOutCommand[655, 684] comment:[655, 656, "#"] text:[657, 660, "set"] open:[660, 661, "("] arguments:[661, 683, "pro.upload.speed 57600"] close:[683, 684, ")"]
    Argument[661, 677] text:[661, 677, "pro.upload.speed"]
    Argument[678, 683] text:[678, 683, "57600"]
  LineEnding[684, 685]
  BlankLine[685, 686]
  Command[686, 734] text:[686, 711, "generate_arduino_firmware"] open:[711, 712, "("] arguments:[712, 733, "${CMAKE_PROJECT_NAME}"] close:[733, 734, ")"]
    Argument[712, 733] text:[712, 733, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


## Issue 1

File not parsing for project name macro should not be wrapped in `${}`

```````````````````````````````` example Issue 1: 1
cmake_minimum_required(VERSION 2.8.4)
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
.
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_PROJECT_NAME tft_test)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)

project(${CMAKE_PROJECT_NAME})

# set(SOURCE_FILES Dda.cpp Dda.h test.cpp)
# set(${CMAKE_PROJECT_NAME}_SRCS ${SOURCE_FILES})
set(${CMAKE_PROJECT_NAME}_SKETCH tft_test.ino)

# Extra library directories
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 780]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 112] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 111, "CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[111, 112, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 111] text:[63, 111, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"]
  LineEnding[112, 113]
  Command[113, 148] text:[113, 116, "set"] open:[116, 117, "("] arguments:[117, 147, "${CMAKE_PROJECT_NAME} tft_test"] close:[147, 148, ")"]
    Argument[117, 138] text:[117, 138, "${CMAKE_PROJECT_NAME}"]
    Argument[139, 147] text:[139, 147, "tft_test"]
  LineEnding[148, 149]
  BlankLine[149, 150]
  LineComment[150, 190] open:[150, 151, "#"] text:[151, 190, "# This must be set before project call\n"]
  Command[190, 226] text:[190, 193, "set"] open:[193, 194, "("] arguments:[194, 225, "${CMAKE_PROJECT_NAME}_BOARD pro"] close:[225, 226, ")"]
    Argument[194, 221] text:[194, 221, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[222, 225] text:[222, 225, "pro"]
  LineEnding[226, 227]
  Command[227, 257] text:[227, 230, "set"] open:[230, 231, "("] arguments:[231, 256, "ARDUINO_CPU 8MHzatmega328"] close:[256, 257, ")"]
    Argument[231, 242] text:[231, 242, "ARDUINO_CPU"]
    Argument[243, 256] text:[243, 256, "8MHzatmega328"]
  LineEnding[257, 258]
  BlankLine[258, 259]
  Command[259, 289] text:[259, 266, "project"] open:[266, 267, "("] arguments:[267, 288, "${CMAKE_PROJECT_NAME}"] close:[288, 289, ")"]
    Argument[267, 288] text:[267, 288, "${CMAKE_PROJECT_NAME}"]
  LineEnding[289, 290]
  BlankLine[290, 291]
  CommentedOutCommand[291, 332] comment:[291, 292, "#"] text:[292, 295, "set"] open:[295, 296, "("] arguments:[296, 331, "SOURCE_FILES Dda.cpp Dda.h test.cpp"] close:[331, 332, ")"]
    Argument[296, 308] text:[296, 308, "SOURCE_FILES"]
    Argument[309, 316] text:[309, 316, "Dda.cpp"]
    Argument[317, 322] text:[317, 322, "Dda.h"]
    Argument[323, 331] text:[323, 331, "test.cpp"]
  LineEnding[332, 333]
  CommentedOutCommand[333, 375] comment:[333, 334, "#"] text:[334, 337, "set"] open:[337, 338, "("] arguments:[338, 374, "${PROJECT_NAME}_SRCS ${SOURCE_FILES}"] close:[374, 375, ")"]
    Argument[338, 358] text:[338, 358, "${PROJECT_NAME}_SRCS"]
    Argument[359, 374] text:[359, 374, "${SOURCE_FILES}"]
  LineEnding[375, 376]
  Command[376, 422] text:[376, 379, "set"] open:[379, 380, "("] arguments:[380, 421, "${CMAKE_PROJECT_NAME}_SKETCH tft_test.ino"] close:[421, 422, ")"]
    Argument[380, 408] text:[380, 408, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[409, 421] text:[409, 421, "tft_test.ino"]
  LineEnding[422, 423]
  BlankLine[423, 424]
  LineComment[424, 452] open:[424, 425, "#"] text:[425, 452, " Extra library directories\n"]
  Command[452, 502] text:[452, 468, "link_directories"] open:[468, 469, "("] arguments:[469, 501, "${CMAKE_CURRENT_SOURCE_DIR}/libs"] close:[501, 502, ")"]
    Argument[469, 501] text:[469, 501, "${CMAKE_CURRENT_SOURCE_DIR}/libs"]
  LineEnding[502, 503]
  BlankLine[503, 504]
  LineComment[504, 556] open:[504, 505, "#"] text:[505, 556, "### Uncomment below additional settings as needed.\n"]
  Command[556, 614] text:[556, 559, "set"] open:[559, 560, "("] arguments:[560, 613, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[613, 614, ")"]
    Argument[560, 586] text:[560, 586, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[587, 613] text:[587, 613, "/dev/cu.usbserial-00000000"]
  LineEnding[614, 615]
  Command[615, 663] text:[615, 618, "set"] open:[618, 619, "("] arguments:[619, 662, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[662, 663, ")"]
    Argument[619, 651] text:[619, 651, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[652, 662] text:[652, 662, "avrispmkii"]
  LineEnding[663, 664]
  Command[664, 700] text:[664, 667, "set"] open:[667, 668, "("] arguments:[668, 699, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[699, 700, ")"]
    Argument[668, 696] text:[668, 696, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[697, 699] text:[697, 699, "-v"]
  LineEnding[700, 701]
  CommentedOutCommand[701, 730] comment:[701, 702, "#"] text:[703, 706, "set"] open:[706, 707, "("] arguments:[707, 729, "pro.upload.speed 57600"] close:[729, 730, ")"]
    Argument[707, 723] text:[707, 723, "pro.upload.speed"]
    Argument[724, 729] text:[724, 729, "57600"]
  LineEnding[730, 731]
  BlankLine[731, 732]
  Command[732, 780] text:[732, 757, "generate_arduino_firmware"] open:[757, 758, "("] arguments:[758, 779, "${CMAKE_PROJECT_NAME}"] close:[779, 780, ")"]
    Argument[758, 779] text:[758, 779, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


File not parsing for project name macro should not be wrapped in `${}`

```````````````````````````````` example(Issue 1: 2) options(board-pro)
cmake_minimum_required(VERSION 2.8.4)
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
.
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_PROJECT_NAME tft_test)

## This must be set before project call
set(${MAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)

project(${MAKE_PROJECT_NAME})

# set(SOURCE_FILES Dda.cpp Dda.h test.cpp)
# set(${MAKE_PROJECT_NAME}_SRCS ${SOURCE_FILES})
set(${MAKE_PROJECT_NAME}_SKETCH tft_test.ino)

# Extra library directories
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)

#### Uncomment below additional settings as needed.
set(${MAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${MAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${MAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 780]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 112] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 111, "CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[111, 112, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 111] text:[63, 111, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"]
  LineEnding[112, 113]
  Command[113, 148] text:[113, 116, "set"] open:[116, 117, "("] arguments:[117, 147, "${CMAKE_PROJECT_NAME} tft_test"] close:[147, 148, ")"]
    Argument[117, 138] text:[117, 138, "${CMAKE_PROJECT_NAME}"]
    Argument[139, 147] text:[139, 147, "tft_test"]
  LineEnding[148, 149]
  BlankLine[149, 150]
  LineComment[150, 190] open:[150, 151, "#"] text:[151, 190, "# This must be set before project call\n"]
  Command[190, 226] text:[190, 193, "set"] open:[193, 194, "("] arguments:[194, 225, "${CMAKE_PROJECT_NAME}_BOARD pro"] close:[225, 226, ")"]
    Argument[194, 221] text:[194, 221, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[222, 225] text:[222, 225, "pro"]
  LineEnding[226, 227]
  Command[227, 257] text:[227, 230, "set"] open:[230, 231, "("] arguments:[231, 256, "ARDUINO_CPU 8MHzatmega328"] close:[256, 257, ")"]
    Argument[231, 242] text:[231, 242, "ARDUINO_CPU"]
    Argument[243, 256] text:[243, 256, "8MHzatmega328"]
  LineEnding[257, 258]
  BlankLine[258, 259]
  Command[259, 289] text:[259, 266, "project"] open:[266, 267, "("] arguments:[267, 288, "${CMAKE_PROJECT_NAME}"] close:[288, 289, ")"]
    Argument[267, 288] text:[267, 288, "${CMAKE_PROJECT_NAME}"]
  LineEnding[289, 290]
  BlankLine[290, 291]
  CommentedOutCommand[291, 332] comment:[291, 292, "#"] text:[292, 295, "set"] open:[295, 296, "("] arguments:[296, 331, "SOURCE_FILES Dda.cpp Dda.h test.cpp"] close:[331, 332, ")"]
    Argument[296, 308] text:[296, 308, "SOURCE_FILES"]
    Argument[309, 316] text:[309, 316, "Dda.cpp"]
    Argument[317, 322] text:[317, 322, "Dda.h"]
    Argument[323, 331] text:[323, 331, "test.cpp"]
  LineEnding[332, 333]
  CommentedOutCommand[333, 375] comment:[333, 334, "#"] text:[334, 337, "set"] open:[337, 338, "("] arguments:[338, 374, "${PROJECT_NAME}_SRCS ${SOURCE_FILES}"] close:[374, 375, ")"]
    Argument[338, 358] text:[338, 358, "${PROJECT_NAME}_SRCS"]
    Argument[359, 374] text:[359, 374, "${SOURCE_FILES}"]
  LineEnding[375, 376]
  Command[376, 422] text:[376, 379, "set"] open:[379, 380, "("] arguments:[380, 421, "${CMAKE_PROJECT_NAME}_SKETCH tft_test.ino"] close:[421, 422, ")"]
    Argument[380, 408] text:[380, 408, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[409, 421] text:[409, 421, "tft_test.ino"]
  LineEnding[422, 423]
  BlankLine[423, 424]
  LineComment[424, 452] open:[424, 425, "#"] text:[425, 452, " Extra library directories\n"]
  Command[452, 502] text:[452, 468, "link_directories"] open:[468, 469, "("] arguments:[469, 501, "${CMAKE_CURRENT_SOURCE_DIR}/libs"] close:[501, 502, ")"]
    Argument[469, 501] text:[469, 501, "${CMAKE_CURRENT_SOURCE_DIR}/libs"]
  LineEnding[502, 503]
  BlankLine[503, 504]
  LineComment[504, 556] open:[504, 505, "#"] text:[505, 556, "### Uncomment below additional settings as needed.\n"]
  Command[556, 614] text:[556, 559, "set"] open:[559, 560, "("] arguments:[560, 613, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[613, 614, ")"]
    Argument[560, 586] text:[560, 586, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[587, 613] text:[587, 613, "/dev/cu.usbserial-00000000"]
  LineEnding[614, 615]
  Command[615, 663] text:[615, 618, "set"] open:[618, 619, "("] arguments:[619, 662, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[662, 663, ")"]
    Argument[619, 651] text:[619, 651, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[652, 662] text:[652, 662, "avrispmkii"]
  LineEnding[663, 664]
  Command[664, 700] text:[664, 667, "set"] open:[667, 668, "("] arguments:[668, 699, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[699, 700, ")"]
    Argument[668, 696] text:[668, 696, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[697, 699] text:[697, 699, "-v"]
  LineEnding[700, 701]
  CommentedOutCommand[701, 730] comment:[701, 702, "#"] text:[703, 706, "set"] open:[706, 707, "("] arguments:[707, 729, "pro.upload.speed 57600"] close:[729, 730, ")"]
    Argument[707, 723] text:[707, 723, "pro.upload.speed"]
    Argument[724, 729] text:[724, 729, "57600"]
  LineEnding[730, 731]
  BlankLine[731, 732]
  Command[732, 780] text:[732, 757, "generate_arduino_firmware"] open:[757, 758, "("] arguments:[758, 779, "${CMAKE_PROJECT_NAME}"] close:[779, 780, ")"]
    Argument[758, 779] text:[758, 779, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


```````````````````````````````` example(Issue 1: 3) options(unmodified-original,  dump-variables)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)     ## This must be set before project call
project(${CMAKE_PROJECT_NAME})

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
.
# cMakeProjectNameMacro = ${CMAKE_PROJECT_NAME}
# outputCMakeProjectNameMacro = 
# cMakeProjectName = tft_life
# VariableExpander(valueMap=ARDUINO_CPU -> [ 8MHzatmega328 ]
# , CMAKE_CXX_STANDARD -> [ 98 ]
# , CMAKE_PROJECT_NAME -> [ tft_life ]
# , CMAKE_TOOLCHAIN_FILE -> [ /cmake/ArduinoToolchain.cmake ]
# , PROJECT_NAME -> [ tft_life ]
# , tft_life_AFLAGS -> [ -v ]
# , tft_life_BOARD -> [ pro ]
# , tft_life_PORT -> [ /dev/cu.usbserial-00000000 ]
# , tft_life_PROGRAMMER -> [ avrispmkii ]
# , tft_life_SRCS -> [ tft_life.cpp ]
# asMacroMap=[  ])
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)     ## This must be set before project call
project(${CMAKE_PROJECT_NAME})

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
.
CMakeFile[0, 803]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 112] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 111, "CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[111, 112, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 111] text:[63, 111, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"]
  LineEnding[112, 113]
  Command[113, 139] text:[113, 116, "set"] open:[116, 117, "("] arguments:[117, 138, "CMAKE_CXX_STANDARD 98"] close:[138, 139, ")"]
    Argument[117, 135] text:[117, 135, "CMAKE_CXX_STANDARD"]
    Argument[136, 138] text:[136, 138, "98"]
  LineEnding[139, 140]
  Command[140, 172] text:[140, 143, "set"] open:[143, 144, "("] arguments:[144, 171, "CMAKE_PROJECT_NAME tft_life"] close:[171, 172, ")"]
    Argument[144, 162] text:[144, 162, "CMAKE_PROJECT_NAME"]
    Argument[163, 171] text:[163, 171, "tft_life"]
  LineEnding[172, 173]
  BlankLine[173, 174]
  Command[174, 210] text:[174, 177, "set"] open:[177, 178, "("] arguments:[178, 209, "${CMAKE_PROJECT_NAME}_BOARD pro"] close:[209, 210, ")"]
    Argument[178, 205] text:[178, 205, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[206, 209] text:[206, 209, "pro"]
  LineEnding[210, 211]
  Command[211, 241] text:[211, 214, "set"] open:[214, 215, "("] arguments:[215, 240, "ARDUINO_CPU 8MHzatmega328"] close:[240, 241, ")"]
    Argument[215, 226] text:[215, 226, "ARDUINO_CPU"]
    Argument[227, 240] text:[227, 240, "8MHzatmega328"]
  LineComment[241, 286] spaces:[241, 246, "     "] open:[241, 242, " "] text:[242, 286, "    ## This must be set before project call\n"]
  Command[286, 316] text:[286, 293, "project"] open:[293, 294, "("] arguments:[294, 315, "${CMAKE_PROJECT_NAME}"] close:[315, 316, ")"]
    Argument[294, 315] text:[294, 315, "${CMAKE_PROJECT_NAME}"]
  LineEnding[316, 317]
  BlankLine[317, 318]
  LineComment[318, 343] open:[318, 319, "#"] text:[319, 343, " Define the source code\n"]
  Command[343, 381] text:[343, 346, "set"] open:[346, 347, "("] arguments:[347, 380, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[380, 381, ")"]
    Argument[347, 367] text:[347, 367, "${PROJECT_NAME}_SRCS"]
    Argument[368, 380] text:[368, 380, "tft_life.cpp"]
  LineEnding[381, 382]
  CommentedOutCommand[382, 409] comment:[382, 383, "#"] text:[384, 387, "set"] open:[387, 388, "("] arguments:[388, 408, "${PROJECT_NAME}_HDRS"] close:[408, 409, ")"]
    Argument[388, 408] text:[388, 408, "${PROJECT_NAME}_HDRS"]
  LineEnding[409, 410]
  CommentedOutCommand[410, 437] comment:[410, 411, "#"] text:[412, 415, "set"] open:[415, 416, "("] arguments:[416, 436, "${PROJECT_NAME}_LIBS"] close:[436, 437, ")"]
    Argument[416, 436] text:[416, 436, "${PROJECT_NAME}_LIBS"]
  LineEnding[437, 438]
  CommentedOutCommand[438, 480] comment:[438, 439, "#"] text:[440, 443, "set"] open:[443, 444, "("] arguments:[444, 479, "${PROJECT_NAME}_SKETCH tft_life.ino"] close:[479, 480, ")"]
    Argument[444, 466] text:[444, 466, "${PROJECT_NAME}_SKETCH"]
    Argument[467, 479] text:[467, 479, "tft_life.ino"]
  LineEnding[480, 481]
  Command[481, 529] text:[481, 497, "link_directories"] open:[497, 498, "("] arguments:[498, 528, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[528, 529, ")"]
    Argument[498, 528] text:[498, 528, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[529, 530]
  CommentedOutCommand[530, 550] comment:[530, 531, "#"] text:[532, 548, "add_subdirectory"] open:[548, 549, "("] arguments:[549, 549] close:[549, 550, ")"]
  LineEnding[550, 551]
  BlankLine[551, 552]
  LineComment[552, 604] open:[552, 553, "#"] text:[553, 604, "### Uncomment below additional settings as needed.\n"]
  Command[604, 646] text:[604, 607, "set"] open:[607, 608, "("] arguments:[608, 645, "${PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[645, 646, ")"]
    Argument[608, 634] text:[608, 634, "${PROJECT_NAME}_PROGRAMMER"]
    Argument[635, 645] text:[635, 645, "avrispmkii"]
  LineEnding[646, 647]
  Command[647, 699] text:[647, 650, "set"] open:[650, 651, "("] arguments:[651, 698, "${PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[698, 699, ")"]
    Argument[651, 671] text:[651, 671, "${PROJECT_NAME}_PORT"]
    Argument[672, 698] text:[672, 698, "/dev/cu.usbserial-00000000"]
  LineEnding[699, 700]
  Command[700, 730] text:[700, 703, "set"] open:[703, 704, "("] arguments:[704, 729, "${PROJECT_NAME}_AFLAGS -v"] close:[729, 730, ")"]
    Argument[704, 726] text:[704, 726, "${PROJECT_NAME}_AFLAGS"]
    Argument[727, 729] text:[727, 729, "-v"]
  LineEnding[730, 731]
  CommentedOutCommand[731, 759] comment:[731, 732, "#"] text:[733, 736, "set"] open:[736, 737, "("] arguments:[737, 758, "pro.upload.speed 9600"] close:[758, 759, ")"]
    Argument[737, 753] text:[737, 753, "pro.upload.speed"]
    Argument[754, 758] text:[754, 758, "9600"]
  LineEnding[759, 760]
  BlankLine[760, 761]
  Command[761, 803] text:[761, 786, "generate_arduino_firmware"] open:[786, 787, "("] arguments:[787, 802, "${PROJECT_NAME}"] close:[802, 803, ")"]
    Argument[787, 802] text:[787, 802, "${PROJECT_NAME}"]
````````````````````````````````


```````````````````````````````` example(Issue 1: 4) options(unmodified-original,  dump-variables,  reset-project)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME a_project)

set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)     ## This must be set before project call
project(${CMAKE_PROJECT_NAME})

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
.
# cMakeProjectNameMacro = ${CMAKE_PROJECT_NAME}
# outputCMakeProjectNameMacro = 
# cMakeProjectName = a_project
# VariableExpander(valueMap=ARDUINO_CPU -> [ 8MHzatmega328 ]
# , CMAKE_CXX_STANDARD -> [ 98 ]
# , CMAKE_PROJECT_NAME -> [ a_project ]
# , CMAKE_TOOLCHAIN_FILE -> [ /cmake/ArduinoToolchain.cmake ]
# , PROJECT_NAME -> [ a_project ]
# , a_project_AFLAGS -> [ -v ]
# , a_project_BOARD -> [ pro ]
# , a_project_PORT -> [ /dev/cu.usbserial-00000000 ]
# , a_project_PROGRAMMER -> [ avrispmkii ]
# , a_project_SRCS -> [ tft_life.cpp ]
# asMacroMap=[  ])
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME a_project)

set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)     ## This must be set before project call
project(${CMAKE_PROJECT_NAME})

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
.
CMakeFile[0, 804]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 112] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 111, "CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[111, 112, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 111] text:[63, 111, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"]
  LineEnding[112, 113]
  Command[113, 139] text:[113, 116, "set"] open:[116, 117, "("] arguments:[117, 138, "CMAKE_CXX_STANDARD 98"] close:[138, 139, ")"]
    Argument[117, 135] text:[117, 135, "CMAKE_CXX_STANDARD"]
    Argument[136, 138] text:[136, 138, "98"]
  LineEnding[139, 140]
  Command[140, 173] text:[140, 143, "set"] open:[143, 144, "("] arguments:[144, 172, "CMAKE_PROJECT_NAME a_project"] close:[172, 173, ")"]
    Argument[144, 162] text:[144, 162, "CMAKE_PROJECT_NAME"]
    Argument[163, 172] text:[163, 172, "a_project"]
  LineEnding[173, 174]
  BlankLine[174, 175]
  Command[175, 211] text:[175, 178, "set"] open:[178, 179, "("] arguments:[179, 210, "${CMAKE_PROJECT_NAME}_BOARD pro"] close:[210, 211, ")"]
    Argument[179, 206] text:[179, 206, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[207, 210] text:[207, 210, "pro"]
  LineEnding[211, 212]
  Command[212, 242] text:[212, 215, "set"] open:[215, 216, "("] arguments:[216, 241, "ARDUINO_CPU 8MHzatmega328"] close:[241, 242, ")"]
    Argument[216, 227] text:[216, 227, "ARDUINO_CPU"]
    Argument[228, 241] text:[228, 241, "8MHzatmega328"]
  LineComment[242, 287] spaces:[242, 247, "     "] open:[242, 243, " "] text:[243, 287, "    ## This must be set before project call\n"]
  Command[287, 317] text:[287, 294, "project"] open:[294, 295, "("] arguments:[295, 316, "${CMAKE_PROJECT_NAME}"] close:[316, 317, ")"]
    Argument[295, 316] text:[295, 316, "${CMAKE_PROJECT_NAME}"]
  LineEnding[317, 318]
  BlankLine[318, 319]
  LineComment[319, 344] open:[319, 320, "#"] text:[320, 344, " Define the source code\n"]
  Command[344, 382] text:[344, 347, "set"] open:[347, 348, "("] arguments:[348, 381, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[381, 382, ")"]
    Argument[348, 368] text:[348, 368, "${PROJECT_NAME}_SRCS"]
    Argument[369, 381] text:[369, 381, "tft_life.cpp"]
  LineEnding[382, 383]
  CommentedOutCommand[383, 410] comment:[383, 384, "#"] text:[385, 388, "set"] open:[388, 389, "("] arguments:[389, 409, "${PROJECT_NAME}_HDRS"] close:[409, 410, ")"]
    Argument[389, 409] text:[389, 409, "${PROJECT_NAME}_HDRS"]
  LineEnding[410, 411]
  CommentedOutCommand[411, 438] comment:[411, 412, "#"] text:[413, 416, "set"] open:[416, 417, "("] arguments:[417, 437, "${PROJECT_NAME}_LIBS"] close:[437, 438, ")"]
    Argument[417, 437] text:[417, 437, "${PROJECT_NAME}_LIBS"]
  LineEnding[438, 439]
  CommentedOutCommand[439, 481] comment:[439, 440, "#"] text:[441, 444, "set"] open:[444, 445, "("] arguments:[445, 480, "${PROJECT_NAME}_SKETCH tft_life.ino"] close:[480, 481, ")"]
    Argument[445, 467] text:[445, 467, "${PROJECT_NAME}_SKETCH"]
    Argument[468, 480] text:[468, 480, "tft_life.ino"]
  LineEnding[481, 482]
  Command[482, 530] text:[482, 498, "link_directories"] open:[498, 499, "("] arguments:[499, 529, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[529, 530, ")"]
    Argument[499, 529] text:[499, 529, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[530, 531]
  CommentedOutCommand[531, 551] comment:[531, 532, "#"] text:[533, 549, "add_subdirectory"] open:[549, 550, "("] arguments:[550, 550] close:[550, 551, ")"]
  LineEnding[551, 552]
  BlankLine[552, 553]
  LineComment[553, 605] open:[553, 554, "#"] text:[554, 605, "### Uncomment below additional settings as needed.\n"]
  Command[605, 647] text:[605, 608, "set"] open:[608, 609, "("] arguments:[609, 646, "${PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[646, 647, ")"]
    Argument[609, 635] text:[609, 635, "${PROJECT_NAME}_PROGRAMMER"]
    Argument[636, 646] text:[636, 646, "avrispmkii"]
  LineEnding[647, 648]
  Command[648, 700] text:[648, 651, "set"] open:[651, 652, "("] arguments:[652, 699, "${PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[699, 700, ")"]
    Argument[652, 672] text:[652, 672, "${PROJECT_NAME}_PORT"]
    Argument[673, 699] text:[673, 699, "/dev/cu.usbserial-00000000"]
  LineEnding[700, 701]
  Command[701, 731] text:[701, 704, "set"] open:[704, 705, "("] arguments:[705, 730, "${PROJECT_NAME}_AFLAGS -v"] close:[730, 731, ")"]
    Argument[705, 727] text:[705, 727, "${PROJECT_NAME}_AFLAGS"]
    Argument[728, 730] text:[728, 730, "-v"]
  LineEnding[731, 732]
  CommentedOutCommand[732, 760] comment:[732, 733, "#"] text:[734, 737, "set"] open:[737, 738, "("] arguments:[738, 759, "pro.upload.speed 9600"] close:[759, 760, ")"]
    Argument[738, 754] text:[738, 754, "pro.upload.speed"]
    Argument[755, 759] text:[755, 759, "9600"]
  LineEnding[760, 761]
  BlankLine[761, 762]
  Command[762, 804] text:[762, 787, "generate_arduino_firmware"] open:[787, 788, "("] arguments:[788, 803, "${PROJECT_NAME}"] close:[803, 804, ")"]
    Argument[788, 803] text:[788, 803, "${PROJECT_NAME}"]
````````````````````````````````



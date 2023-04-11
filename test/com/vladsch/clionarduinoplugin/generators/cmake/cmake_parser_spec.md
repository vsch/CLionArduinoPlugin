---
title: CMake Parser Spec Test
author: Vladimir Schneider
version: 0.1
date: '2018-11-18'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Comments

line comments

```````````````````````````````` example(Comments: 1) options(ast-comments)
# comment
.
.
CMakeFile[0, 9]
  LineComment[0, 9] open:[0, 1, "#"] text:[1, 9, " comment"]
````````````````````````````````


multi line bracket comments

```````````````````````````````` example(Comments: 2) options(ast-comments,  bracket-comments)
#[[ comment
# inner comment
]]

# line comment
.
.
CMakeFile[0, 46]
  BracketComment[0, 30] open:[0, 3, "#[["] text:[3, 28, " comment\n# inner comment\n"] close:[28, 30, "]]"]
  LineComment[32, 46] open:[32, 33, "#"] text:[33, 46, " line comment"]
````````````````````````````````


in-line bracket comments

```````````````````````````````` example(Comments: 3) options(ast-comments,  bracket-comments)
#[[ comment # inner comment ]] # line comment

# line comment
.
.
CMakeFile[0, 61]
  BracketComment[0, 30] open:[0, 3, "#[["] text:[3, 28, " comment # inner comment "] close:[28, 30, "]]"]
  LineComment[30, 46] spaces:[30, 31, " "] open:[30, 31, " "] text:[31, 46, "# line comment\n"]
  LineComment[47, 61] open:[47, 48, "#"] text:[48, 61, " line comment"]
````````````````````````````````


bracket comments disabled

```````````````````````````````` example(Comments: 4) options(ast-comments)
#[[ comment
# inner comment
]]

# line comment
.
Unrecognized input: 2:0: ]]
.
CMakeFile[0, 46]
  LineComment[0, 12] open:[0, 1, "#"] text:[1, 12, "[[ comment\n"]
  LineComment[12, 28] open:[12, 13, "#"] text:[13, 28, " inner comment\n"]
  UnrecognizedInput[28, 31]
  LineComment[32, 46] open:[32, 33, "#"] text:[33, 46, " line comment"]
````````````````````````````````


not a bracket comment

```````````````````````````````` example(Comments: 5) options(ast-comments,  bracket-comments)
# [[ comment
# inner comment
]]

# line comment
.
Unrecognized input: 2:0: ]]
.
CMakeFile[0, 47]
  LineComment[0, 13] open:[0, 1, "#"] text:[1, 13, " [[ comment\n"]
  LineComment[13, 29] open:[13, 14, "#"] text:[14, 29, " inner comment\n"]
  UnrecognizedInput[29, 32]
  LineComment[33, 47] open:[33, 34, "#"] text:[34, 47, " line comment"]
````````````````````````````````


## AST

default no eol

```````````````````````````````` example(AST: 1) options(ast-comments)
if ()

# line comment
.
.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineComment[7, 21] open:[7, 8, "#"] text:[8, 21, " line comment"]
````````````````````````````````


with eol

```````````````````````````````` example(AST: 2) options(ast-comments,  ast-line-eol)
if ()

# line comment

.
.
CMakeFile[0, 23]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[5, 6]
  LineComment[7, 22] open:[7, 8, "#"] text:[8, 22, " line comment\n"]
````````````````````````````````


default no eol, trailing spaces

```````````````````````````````` example(AST: 3) options(ast-comments)
if () 

# line comment
.
.
CMakeFile[0, 22]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineComment[8, 22] open:[8, 9, "#"] text:[9, 22, " line comment"]
````````````````````````````````


with eol, trailing spaces

```````````````````````````````` example(AST: 4) options(ast-comments,  ast-line-eol)
if () 

# line comment

.
.
CMakeFile[0, 24]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[6, 7]
  LineComment[8, 23] open:[8, 9, "#"] text:[9, 23, " line comment\n"]
````````````````````````````````


no blanks

```````````````````````````````` example(AST: 5) options(ast-comments,  ast-line-eol)
if ()

# line comment
.
.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[5, 6]
  LineComment[7, 21] open:[7, 8, "#"] text:[8, 21, " line comment"]
````````````````````````````````


blanks

```````````````````````````````` example(AST: 6) options(ast-comments,  ast-blank)
if ()

# line comment
.
.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  BlankLine[6, 7]
  LineComment[7, 21] open:[7, 8, "#"] text:[8, 21, " line comment"]
````````````````````````````````


with blanks, with eol, with comments

```````````````````````````````` example(AST: 7) options(ast-comments,  ast-line-eol,  ast-blank)
if ()

# line comment
.
.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[5, 6]
  BlankLine[6, 7]
  LineComment[7, 21] open:[7, 8, "#"] text:[8, 21, " line comment"]
````````````````````````````````


no blanks, no eol, no comments

```````````````````````````````` example AST: 8
if ()

# line comment
.
.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
````````````````````````````````


no blanks, no eol, no comments, empty file

```````````````````````````````` example AST: 9
# line comment

# line comment
.
.
CMakeFile[0, 30]
````````````````````````````````


no blanks, no eol, no comments

```````````````````````````````` example(AST: 10) options(bracket-comments)
# line comment

#[[ gone ]]

# line comment
.
.
CMakeFile[0, 43]
````````````````````````````````


## Commands

no args

```````````````````````````````` example Commands: 1
if()
.
.
CMakeFile[0, 4]
  Command[0, 4] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 3] close:[3, 4, ")"]
````````````````````````````````


```````````````````````````````` example Commands: 2
if ()
.
.
CMakeFile[0, 5]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
````````````````````````````````


one arg

```````````````````````````````` example Commands: 3
if(arg)
.
.
CMakeFile[0, 7]
  Command[0, 7] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 6, "arg"] close:[6, 7, ")"]
    Argument[3, 6] text:[3, 6, "arg"]
````````````````````````````````


one arg

```````````````````````````````` example Commands: 4
if( arg)
.
.
CMakeFile[0, 8]
  Command[0, 8] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 7, " arg"] close:[7, 8, ")"]
    Argument[4, 7] text:[4, 7, "arg"]
````````````````````````````````


one arg

```````````````````````````````` example Commands: 5
if(arg )
.
.
CMakeFile[0, 8]
  Command[0, 8] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 7, "arg "] close:[7, 8, ")"]
    Argument[3, 6] text:[3, 6, "arg"]
````````````````````````````````


one arg

```````````````````````````````` example Commands: 6
if( arg )
.
.
CMakeFile[0, 9]
  Command[0, 9] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 8, " arg "] close:[8, 9, ")"]
    Argument[4, 7] text:[4, 7, "arg"]
````````````````````````````````


one ref arg

```````````````````````````````` example Commands: 7
if(${ref})
.
.
CMakeFile[0, 10]
  Command[0, 10] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 9, "${ref}"] close:[9, 10, ")"]
    Argument[3, 9] text:[3, 9, "${ref}"]
````````````````````````````````


one ref arg

```````````````````````````````` example Commands: 8
if(${ref}_complex)
.
.
CMakeFile[0, 18]
  Command[0, 18] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 17, "${ref}_complex"] close:[17, 18, ")"]
    Argument[3, 17] text:[3, 17, "${ref}_complex"]
````````````````````````````````


one quoted ref arg

```````````````````````````````` example Commands: 9
if("${ref}_complex")
.
.
CMakeFile[0, 20]
  Command[0, 20] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 19, "\"${ref}_complex\""] close:[19, 20, ")"]
    Argument[3, 19] open:[3, 4, "\""] text:[4, 18, "${ref}_complex"] close:[18, 19, "\""]
````````````````````````````````


one bracket ref arg

```````````````````````````````` example Commands: 10
if([[${ref}_complex]])
.
.
CMakeFile[0, 22]
  Command[0, 22] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 21, "[[${ref}_complex]]"] close:[21, 22, ")"]
    Argument[3, 21] open:[3, 5, "[["] text:[5, 19, "${ref}_complex"] close:[19, 21, "]]"]
````````````````````````````````


one bracket ref arg multi-line

```````````````````````````````` example Commands: 11
if([[${ref}_complex
more data
]])
.
.
CMakeFile[0, 33]
  Command[0, 33] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 32, "[[${ref}_complex\nmore data\n]]"] close:[32, 33, ")"]
    Argument[3, 32] open:[3, 5, "[["] text:[5, 30, "${ref}_complex\nmore data\n"] close:[30, 32, "]]"]
````````````````````````````````


one unquoted legacy

```````````````````````````````` example Commands: 12
if(abc" "def)
.
.
CMakeFile[0, 13]
  Command[0, 13] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 12, "abc\" \"def"] close:[12, 13, ")"]
    Argument[3, 12] text:[3, 12, "abc\" \"def"]
````````````````````````````````


one parenthesized legacy

```````````````````````````````` example Commands: 13
if((abc" "def))
.
.
CMakeFile[0, 15]
  Command[0, 15] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 14, "(abc\" \"def)"] close:[14, 15, ")"]
    Argument[3, 4] text:[3, 4, "("]
    Argument[4, 13] text:[4, 13, "abc\" \"def"]
    Argument[13, 14] text:[13, 14, ")"]
````````````````````````````````


one escaped semi

```````````````````````````````` example Commands: 14
if(abc\;def)
.
.
CMakeFile[0, 12]
  Command[0, 12] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 11, "abc\;def"] close:[11, 12, ")"]
    Argument[3, 11] text:[3, 11, "abc\;def"]
````````````````````````````````


several args

```````````````````````````````` example Commands: 15
if(abc" "def xyz /path/def)
.
.
CMakeFile[0, 27]
  Command[0, 27] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 26, "abc\" \"def xyz /path/def"] close:[26, 27, ")"]
    Argument[3, 12] text:[3, 12, "abc\" \"def"]
    Argument[13, 16] text:[13, 16, "xyz"]
    Argument[17, 26] text:[17, 26, "/path/def"]
````````````````````````````````


several args

```````````````````````````````` example Commands: 16
foreach(arg
    NoSpace
    Escaped\ Space
    This;Divides;Into;Five;Arguments
    Escaped\;Semicolon
    )
  message("${arg}")
endforeach()

.
.
CMakeFile[0, 143]
  Command[0, 108] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 107, "arg\n    NoSpace\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    Escaped\;Semicolon\n    "] close:[107, 108, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    Argument[16, 23] text:[16, 23, "NoSpace"]
    Argument[28, 42] text:[28, 42, "Escaped\ Space"]
    Argument[47, 51] text:[47, 51, "This"]
    Argument[52, 59] text:[52, 59, "Divides"]
    Argument[60, 64] text:[60, 64, "Into"]
    Argument[65, 69] text:[65, 69, "Five"]
    Argument[70, 79] text:[70, 79, "Arguments"]
    Argument[84, 102] text:[84, 102, "Escaped\;Semicolon"]
  Command[108, 128] spaces:[108, 111, "\n  "] text:[111, 118, "message"] open:[118, 119, "("] arguments:[119, 127, "\"${arg}\""] close:[127, 128, ")"]
    Argument[119, 127] open:[119, 120, "\""] text:[120, 126, "${arg}"] close:[126, 127, "\""]
  Command[128, 141] spaces:[128, 129, "\n"] text:[129, 139, "endforeach"] open:[139, 140, "("] arguments:[140, 140] close:[140, 141, ")"]
````````````````````````````````


several args with blanks

```````````````````````````````` example(Commands: 17) options(ast-blank)
foreach(arg
    NoSpace
    Escaped\ Space
    This;Divides;Into;Five;Arguments
    
    Escaped\;Semicolon
    )
  message("${arg}")
  
endforeach()

.
.
CMakeFile[0, 151]
  Command[0, 113] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 112, "arg\n    NoSpace\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    \n    Escaped\;Semicolon\n    "] close:[112, 113, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    Argument[16, 23] text:[16, 23, "NoSpace"]
    Argument[28, 42] text:[28, 42, "Escaped\ Space"]
    Argument[47, 51] text:[47, 51, "This"]
    Argument[52, 59] text:[52, 59, "Divides"]
    Argument[60, 64] text:[60, 64, "Into"]
    Argument[65, 69] text:[65, 69, "Five"]
    Argument[70, 79] text:[70, 79, "Arguments"]
    BlankLine[84, 85]
    Argument[89, 107] text:[89, 107, "Escaped\;Semicolon"]
  Command[113, 133] spaces:[113, 116, "\n  "] text:[116, 123, "message"] open:[123, 124, "("] arguments:[124, 132, "\"${arg}\""] close:[132, 133, ")"]
    Argument[124, 132] open:[124, 125, "\""] text:[125, 131, "${arg}"] close:[131, 132, "\""]
  BlankLine[136, 137]
  Command[137, 149] text:[137, 147, "endforeach"] open:[147, 148, "("] arguments:[148, 148] close:[148, 149, ")"]
  BlankLine[150, 151]
````````````````````````````````


several args

```````````````````````````````` example(Commands: 18) options(ast-comments,  ast-arg-seps,  ast-line-eol,  ast-blank)
foreach(arg
    NoSpace     # comment
    Escaped\ Space
    This;Divides;Into;Five;Arguments
    
    Escaped\;Semicolon
    )
  message("${arg}")
  
endforeach()

.
.
CMakeFile[0, 165]
  Command[0, 127] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 126, "arg\n    NoSpace     # comment\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    \n    Escaped\;Semicolon\n    "] close:[126, 127, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    LineEnding[11, 12]
    Argument[16, 23] text:[16, 23, "NoSpace"]
    LineComment[28, 38] open:[28, 29, "#"] text:[29, 38, " comment\n"]
    Argument[42, 56] text:[42, 56, "Escaped\ Space"]
    LineEnding[56, 57]
    Argument[61, 65] text:[61, 65, "This"]
    Separator[65, 66]
    Argument[66, 73] text:[66, 73, "Divides"]
    Separator[73, 74]
    Argument[74, 78] text:[74, 78, "Into"]
    Separator[78, 79]
    Argument[79, 83] text:[79, 83, "Five"]
    Separator[83, 84]
    Argument[84, 93] text:[84, 93, "Arguments"]
    LineEnding[93, 94]
    BlankLine[98, 99]
    Argument[103, 121] text:[103, 121, "Escaped\;Semicolon"]
    LineEnding[121, 122]
  LineEnding[127, 128]
  Command[128, 147] spaces:[128, 130, "  "] text:[130, 137, "message"] open:[137, 138, "("] arguments:[138, 146, "\"${arg}\""] close:[146, 147, ")"]
    Argument[138, 146] open:[138, 139, "\""] text:[139, 145, "${arg}"] close:[145, 146, "\""]
  LineEnding[147, 148]
  BlankLine[150, 151]
  Command[151, 163] text:[151, 161, "endforeach"] open:[161, 162, "("] arguments:[162, 162] close:[162, 163, ")"]
  LineEnding[163, 164]
  BlankLine[164, 165]
````````````````````````````````


several args with seps

```````````````````````````````` example(Commands: 19) options(ast-arg-seps)
foreach(arg
    NoSpace
    Escaped\ Space
    This;Divides;Into;Five;Arguments
    Escaped\;Semicolon
    )
  message("${arg}")
endforeach()

.
.
CMakeFile[0, 143]
  Command[0, 108] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 107, "arg\n    NoSpace\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    Escaped\;Semicolon\n    "] close:[107, 108, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    Argument[16, 23] text:[16, 23, "NoSpace"]
    Argument[28, 42] text:[28, 42, "Escaped\ Space"]
    Argument[47, 51] text:[47, 51, "This"]
    Separator[51, 52]
    Argument[52, 59] text:[52, 59, "Divides"]
    Separator[59, 60]
    Argument[60, 64] text:[60, 64, "Into"]
    Separator[64, 65]
    Argument[65, 69] text:[65, 69, "Five"]
    Separator[69, 70]
    Argument[70, 79] text:[70, 79, "Arguments"]
    Argument[84, 102] text:[84, 102, "Escaped\;Semicolon"]
  Command[108, 128] spaces:[108, 111, "\n  "] text:[111, 118, "message"] open:[118, 119, "("] arguments:[119, 127, "\"${arg}\""] close:[127, 128, ")"]
    Argument[119, 127] open:[119, 120, "\""] text:[120, 126, "${arg}"] close:[126, 127, "\""]
  Command[128, 141] spaces:[128, 129, "\n"] text:[129, 139, "endforeach"] open:[139, 140, "("] arguments:[140, 140] close:[140, 141, ")"]
````````````````````````````````


several args with seps

```````````````````````````````` example(Commands: 20) options(ast-arg-seps,  ast-line-eol)
foreach(arg
    NoSpace       # comment
    Escaped\ Space
    This;Divides;Into;Five;Arguments
    Escaped\;Semicolon
    )
  message("${arg}")
endforeach()

.
.
CMakeFile[0, 159]
  Command[0, 124] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 123, "arg\n    NoSpace       # comment\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    Escaped\;Semicolon\n    "] close:[123, 124, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    LineEnding[11, 12]
    Argument[16, 23] text:[16, 23, "NoSpace"]
    LineEnding[39, 40]
    Argument[44, 58] text:[44, 58, "Escaped\ Space"]
    LineEnding[58, 59]
    Argument[63, 67] text:[63, 67, "This"]
    Separator[67, 68]
    Argument[68, 75] text:[68, 75, "Divides"]
    Separator[75, 76]
    Argument[76, 80] text:[76, 80, "Into"]
    Separator[80, 81]
    Argument[81, 85] text:[81, 85, "Five"]
    Separator[85, 86]
    Argument[86, 95] text:[86, 95, "Arguments"]
    LineEnding[95, 96]
    Argument[100, 118] text:[100, 118, "Escaped\;Semicolon"]
    LineEnding[118, 119]
  LineEnding[124, 125]
  Command[125, 144] spaces:[125, 127, "  "] text:[127, 134, "message"] open:[134, 135, "("] arguments:[135, 143, "\"${arg}\""] close:[143, 144, ")"]
    Argument[135, 143] open:[135, 136, "\""] text:[136, 142, "${arg}"] close:[142, 143, "\""]
  LineEnding[144, 145]
  Command[145, 157] text:[145, 155, "endforeach"] open:[155, 156, "("] arguments:[156, 156] close:[156, 157, ")"]
  LineEnding[157, 158]
````````````````````````````````


## Commented Out Commands

one escaped semi

```````````````````````````````` example(Commented Out Commands: 1) options(commented-out)
# if(abc\;def)
.
.
CMakeFile[0, 14]
  CommentedOutCommand[0, 14] comment:[0, 1, "#"] text:[2, 4, "if"] open:[4, 5, "("] arguments:[5, 13, "abc\;def"] close:[13, 14, ")"]
    Argument[5, 13] text:[5, 13, "abc\;def"]
````````````````````````````````


one escaped semi

```````````````````````````````` example(Commented Out Commands: 2) options(commented-out)
# if(abc\;def)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

.
.
CMakeFile[0, 65]
  CommentedOutCommand[0, 14] comment:[0, 1, "#"] text:[2, 4, "if"] open:[4, 5, "("] arguments:[5, 13, "abc\;def"] close:[13, 14, ")"]
    Argument[5, 13] text:[5, 13, "abc\;def"]
  Command[14, 63] spaces:[14, 15, "\n"] text:[15, 31, "link_directories"] open:[31, 32, "("] arguments:[32, 62, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[62, 63, ")"]
    Argument[32, 62] text:[32, 62, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
````````````````````````````````


## CMake

CMake on line
[language manual](https://cmake.org/cmake/help/latest/manual/cmake-language.7.html) examples
options(ast-blank, ast-line-eol, ast-no-comments)

```````````````````````````````` example CMake: 1
message([=[
This is the first line in a bracket argument with bracket length 1.
No \-escape sequences or ${variable} references are evaluated.
This is always one argument even though it contains a ; character.
The text does not end on a closing bracket of length 0 like ]].
It does end in a closing bracket of length 1.
]=])
.
.
CMakeFile[0, 324]
  Command[0, 324] text:[0, 7, "message"] open:[7, 8, "("] arguments:[8, 323, "[=[\nThis is the first line in a bracket argument with bracket length 1.\nNo \-escape sequences or ${variable} references are evaluated.\nThis is always one argument even though it contains a ; character.\nThe text does not end on a closing bracket of length 0 like ]].\nIt does end in a closing bracket of length 1.\n]=]"] close:[323, 324, ")"]
    Argument[8, 323] open:[8, 11, "[=["] text:[11, 320, "\nThis is the first line in a bracket argument with bracket length 1.\nNo \-escape sequences or ${variable} references are evaluated.\nThis is always one argument even though it contains a ; character.\nThe text does not end on a closing bracket of length 0 like ]].\nIt does end in a closing bracket of length 1.\n"] close:[320, 323, "]=]"]
````````````````````````````````


```````````````````````````````` example CMake: 2
message("This is a quoted argument containing multiple lines.
This is always one argument even though it contains a ; character.
Both \\-escape sequences and ${variable} references are evaluated.
The text does not end on an escaped double-quote like \".
It does end in an unescaped double quote.
")
.
.
CMakeFile[0, 298]
  Command[0, 298] text:[0, 7, "message"] open:[7, 8, "("] arguments:[8, 297, "\"This is a quoted argument containing multiple lines.\nThis is always one argument even though it contains a ; character.\nBoth \\-escape sequences and ${variable} references are evaluated.\nThe text does not end on an escaped double-quote like \\".\nIt does end in an unescaped double quote.\n\""] close:[297, 298, ")"]
    Argument[8, 297] open:[8, 9, "\""] text:[9, 296, "This is a quoted argument containing multiple lines.\nThis is always one argument even though it contains a ; character.\nBoth \\-escape sequences and ${variable} references are evaluated.\nThe text does not end on an escaped double-quote like \\".\nIt does end in an unescaped double quote.\n"] close:[296, 297, "\""]
````````````````````````````````


no continuation

```````````````````````````````` example CMake: 3
message("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")
.
Closing ) expected: 0:8: "\
Unrecognized input: 0:0: message("\
Unrecognized input: 1:0: This is the first line of a quoted argument. \
Unrecognized input: 2:0: In fact it is the only line but since it is long \
Unrecognized input: 3:0: the source code uses line continuation.\
Unrecognized input: 4:0: ")
.
CMakeFile[0, 152]
  UnrecognizedInput[8, 11]
  UnrecognizedInput[0, 11]
  UnrecognizedInput[11, 58]
  UnrecognizedInput[58, 109]
  UnrecognizedInput[109, 150]
  UnrecognizedInput[150, 152]
````````````````````````````````


continuation

```````````````````````````````` example(CMake: 4) options(line-cont)
message("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")
.
.
CMakeFile[0, 152]
  Command[0, 152] text:[0, 7, "message"] open:[7, 8, "("] arguments:[8, 151, "\"\\nThis is the first line of a quoted argument. \\nIn fact it is the only line but since it is long \\nthe source code uses line continuation.\\n\""] close:[151, 152, ")"]
    Argument[8, 151] open:[8, 9, "\""] text:[9, 150, "\\nThis is the first line of a quoted argument. \\nIn fact it is the only line but since it is long \\nthe source code uses line continuation.\\n"] close:[150, 151, "\""]
````````````````````````````````


## AutoConfig

no bracket comments

```````````````````````````````` example(AutoConfig: 1) options(auto-config,  ast-comments,  ast-line-eol,  ast-blank)
cmake_minimum_required(VERSION 2.99.99)
#[[this is a comment]] set(${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii)
.
.
CMakeFile[0, 117]
  Command[0, 39] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 38, "VERSION 2.99.99"] close:[38, 39, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 38] text:[31, 38, "2.99.99"]
  LineEnding[39, 40]
  LineComment[40, 117] open:[40, 41, "#"] text:[41, 117, "[[this is a comment]] set(${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii)"]
````````````````````````````````


bracket comments

```````````````````````````````` example(AutoConfig: 2) options(auto-config,  ast-comments,  ast-line-eol,  ast-blank)
cmake_minimum_required(VERSION 3)
#[[this is a comment]] set(${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii)
.
.
CMakeFile[0, 111]
  Command[0, 33] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 32, "VERSION 3"] close:[32, 33, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 32] text:[31, 32, "3"]
  LineEnding[33, 34]
  BracketComment[34, 56] open:[34, 37, "#[["] text:[37, 54, "this is a comment"] close:[54, 56, "]]"]
  Command[56, 111] spaces:[56, 57, " "] text:[57, 60, "set"] open:[60, 61, "("] arguments:[61, 110, "${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii"] close:[110, 111, ")"]
    Argument[61, 93] text:[61, 93, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    BracketComment[94, 99] open:[94, 97, "#[["] text:[97, 97] close:[97, 99, "]]"]
    Argument[100, 110] text:[100, 110, "avrispmkii"]
````````````````````````````````


no line continuation

```````````````````````````````` example(AutoConfig: 3) options(auto-config,  ast-comments,  ast-line-eol,  ast-blank)
cmake_minimum_required(VERSION 2.99.99)
message("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")
.
Closing ) expected: 1:8: "\
Unrecognized input: 1:0: message("\
Unrecognized input: 2:0: This is the first line of a quoted argument. \
Unrecognized input: 3:0: In fact it is the only line but since it is long \
Unrecognized input: 4:0: the source code uses line continuation.\
Unrecognized input: 5:0: ")
````````````````````````````````


line continuation

```````````````````````````````` example(AutoConfig: 4) options(auto-config,  ast-comments,  ast-line-eol,  ast-blank)
cmake_minimum_required(VERSION 3)
message("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")
.
.
CMakeFile[0, 186]
  Command[0, 33] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 32, "VERSION 3"] close:[32, 33, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 32] text:[31, 32, "3"]
  LineEnding[33, 34]
  Command[34, 186] text:[34, 41, "message"] open:[41, 42, "("] arguments:[42, 185, "\"\\nThis is the first line of a quoted argument. \\nIn fact it is the only line but since it is long \\nthe source code uses line continuation.\\n\""] close:[185, 186, ")"]
    Argument[42, 185] open:[42, 43, "\""] text:[43, 184, "\\nThis is the first line of a quoted argument. \\nIn fact it is the only line but since it is long \\nthe source code uses line continuation.\\n"] close:[184, 185, "\""]
````````````````````````````````


Should save options in file node

```````````````````````````````` example(AutoConfig: 5) options(auto-config,  ast-comments,  ast-line-eol,  ast-blank,  dump-options)
cmake_minimum_required(VERSION 3)
#[[this is a comment]] set(${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii)
.
AST_ARGUMENT_SEPARATORS->false
AST_BLANK_LINES->true
AST_COMMAND_BLOCKS->false
AST_COMMENTED_OUT_COMMANDS->false
AST_COMMENTS->true
AST_LINE_END_EOL->true
AUTO_CONFIG->true
BRACKET_COMMENTS->true
DUMP_OPTIONS->true
LINE_CONTINUATION->true
MAX_ERROR_LINE_RECOVERIES->5
.
CMakeFile[0, 111]
  Command[0, 33] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 32, "VERSION 3"] close:[32, 33, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 32] text:[31, 32, "3"]
  LineEnding[33, 34]
  BracketComment[34, 56] open:[34, 37, "#[["] text:[37, 54, "this is a comment"] close:[54, 56, "]]"]
  Command[56, 111] spaces:[56, 57, " "] text:[57, 60, "set"] open:[60, 61, "("] arguments:[61, 110, "${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii"] close:[110, 111, ")"]
    Argument[61, 93] text:[61, 93, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    BracketComment[94, 99] open:[94, 97, "#[["] text:[97, 97] close:[97, 99, "]]"]
    Argument[100, 110] text:[100, 110, "avrispmkii"]
````````````````````````````````


## Arduno

actual file

```````````````````````````````` example(Arduno: 1) options(auto-config)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)

project(${PROJECT_NAME})

# Define the source code
set(${PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set (${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
.
CMakeFile[0, 717]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  Command[37, 114] spaces:[37, 38, "\n"] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  Command[114, 141] spaces:[114, 115, "\n"] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "PROJECT_NAME tft_life"] close:[140, 141, ")"]
    Argument[119, 131] text:[119, 131, "PROJECT_NAME"]
    Argument[132, 140] text:[132, 140, "tft_life"]
  Command[141, 219] spaces:[141, 183, "\n\n## This must be set before project call\n"] text:[183, 186, "set"] open:[186, 187, "("] arguments:[187, 218, "${CMAKE_PROJECT_NAME}_BOARD pro"] close:[218, 219, ")"]
    Argument[187, 214] text:[187, 214, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[215, 218] text:[215, 218, "pro"]
  Command[219, 250] spaces:[219, 220, "\n"] text:[220, 223, "set"] open:[223, 224, "("] arguments:[224, 249, "ARDUINO_CPU 8MHzatmega328"] close:[249, 250, ")"]
    Argument[224, 235] text:[224, 235, "ARDUINO_CPU"]
    Argument[236, 249] text:[236, 249, "8MHzatmega328"]
  Command[250, 276] spaces:[250, 252, "\n\n"] text:[252, 259, "project"] open:[259, 260, "("] arguments:[260, 275, "${PROJECT_NAME}"] close:[275, 276, ")"]
    Argument[260, 275] text:[260, 275, "${PROJECT_NAME}"]
  Command[276, 341] spaces:[276, 303, "\n\n# Define the source code\n"] text:[303, 306, "set"] open:[306, 307, "("] arguments:[307, 340, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[340, 341, ")"]
    Argument[307, 327] text:[307, 327, "${PROJECT_NAME}_SRCS"]
    Argument[328, 340] text:[328, 340, "tft_life.cpp"]
  Command[341, 438] spaces:[341, 390, "\n#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)\n"] text:[390, 406, "link_directories"] open:[406, 407, "("] arguments:[407, 437, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[437, 438, ")"]
    Argument[407, 437] text:[407, 437, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  Command[438, 540] spaces:[438, 492, "\n\n#### Uncomment below additional settings as needed.\n"] text:[492, 495, "set"] open:[495, 496, "("] arguments:[496, 539, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[539, 540, ")"]
    Argument[496, 528] text:[496, 528, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[529, 539] text:[529, 539, "avrispmkii"]
  Command[540, 599] spaces:[540, 541, "\n"] text:[541, 544, "set"] open:[544, 545, "("] arguments:[545, 598, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[598, 599, ")"]
    Argument[545, 571] text:[545, 571, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[572, 598] text:[572, 598, "/dev/cu.usbserial-00000000"]
  Command[599, 637] spaces:[599, 600, "\n"] text:[600, 603, "set"] open:[604, 605, "("] arguments:[605, 636, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[636, 637, ")"]
    Argument[605, 633] text:[605, 633, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[634, 636] text:[634, 636, "-v"]
  Command[637, 717] spaces:[637, 669, "\n# set(pro.upload.speed 57600)\n\n"] text:[669, 694, "generate_arduino_firmware"] open:[694, 695, "("] arguments:[695, 716, "${CMAKE_PROJECT_NAME}"] close:[716, 717, ")"]
    Argument[695, 716] text:[695, 716, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


actual file all the fixings in ast

```````````````````````````````` example(Arduno: 2) options(auto-config,  ast-blank,  ast-line-eol,  ast-comments,  ast-arg-seps)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)

project(${PROJECT_NAME})

# Define the source code
set(${PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set (${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
.
CMakeFile[0, 717]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "PROJECT_NAME tft_life"] close:[140, 141, ")"]
    Argument[119, 131] text:[119, 131, "PROJECT_NAME"]
    Argument[132, 140] text:[132, 140, "tft_life"]
  LineEnding[141, 142]
  BlankLine[142, 143]
  LineComment[143, 183] open:[143, 144, "#"] text:[144, 183, "# This must be set before project call\n"]
  Command[183, 219] text:[183, 186, "set"] open:[186, 187, "("] arguments:[187, 218, "${CMAKE_PROJECT_NAME}_BOARD pro"] close:[218, 219, ")"]
    Argument[187, 214] text:[187, 214, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[215, 218] text:[215, 218, "pro"]
  LineEnding[219, 220]
  Command[220, 250] text:[220, 223, "set"] open:[223, 224, "("] arguments:[224, 249, "ARDUINO_CPU 8MHzatmega328"] close:[249, 250, ")"]
    Argument[224, 235] text:[224, 235, "ARDUINO_CPU"]
    Argument[236, 249] text:[236, 249, "8MHzatmega328"]
  LineEnding[250, 251]
  BlankLine[251, 252]
  Command[252, 276] text:[252, 259, "project"] open:[259, 260, "("] arguments:[260, 275, "${PROJECT_NAME}"] close:[275, 276, ")"]
    Argument[260, 275] text:[260, 275, "${PROJECT_NAME}"]
  LineEnding[276, 277]
  BlankLine[277, 278]
  LineComment[278, 303] open:[278, 279, "#"] text:[279, 303, " Define the source code\n"]
  Command[303, 341] text:[303, 306, "set"] open:[306, 307, "("] arguments:[307, 340, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[340, 341, ")"]
    Argument[307, 327] text:[307, 327, "${PROJECT_NAME}_SRCS"]
    Argument[328, 340] text:[328, 340, "tft_life.cpp"]
  LineEnding[341, 342]
  LineComment[342, 390] open:[342, 343, "#"] text:[343, 390, "set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)\n"]
  Command[390, 438] text:[390, 406, "link_directories"] open:[406, 407, "("] arguments:[407, 437, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[437, 438, ")"]
    Argument[407, 437] text:[407, 437, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[438, 439]
  BlankLine[439, 440]
  LineComment[440, 492] open:[440, 441, "#"] text:[441, 492, "### Uncomment below additional settings as needed.\n"]
  Command[492, 540] text:[492, 495, "set"] open:[495, 496, "("] arguments:[496, 539, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[539, 540, ")"]
    Argument[496, 528] text:[496, 528, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[529, 539] text:[529, 539, "avrispmkii"]
  LineEnding[540, 541]
  Command[541, 599] text:[541, 544, "set"] open:[544, 545, "("] arguments:[545, 598, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[598, 599, ")"]
    Argument[545, 571] text:[545, 571, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[572, 598] text:[572, 598, "/dev/cu.usbserial-00000000"]
  LineEnding[599, 600]
  Command[600, 637] text:[600, 603, "set"] open:[604, 605, "("] arguments:[605, 636, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[636, 637, ")"]
    Argument[605, 633] text:[605, 633, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[634, 636] text:[634, 636, "-v"]
  LineEnding[637, 638]
  LineComment[638, 668] open:[638, 639, "#"] text:[639, 668, " set(pro.upload.speed 57600)\n"]
  BlankLine[668, 669]
  Command[669, 717] text:[669, 694, "generate_arduino_firmware"] open:[694, 695, "("] arguments:[695, 716, "${CMAKE_PROJECT_NAME}"] close:[716, 717, ")"]
    Argument[695, 716] text:[695, 716, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


file from builder

```````````````````````````````` example(Arduno: 3) options(auto-config,  ast-blank,  ast-line-eol,  ast-arg-seps,  ast-comments)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD uno)
set(ARDUINO_CPU none)

project(${PROJECT_NAME})

# Define the source code
set(${PROJECT_NAME}_SRCS tft_life.cpp)    
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)      
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)        

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)        
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)          
set (${CMAKE_PROJECT_NAME}_AFLAGS -v)      

set(pro.upload.speed 57600)           
set(libName_RECURSE false)                  

generate_arduino_firmware("${CMAKE_PROJECT_NAME}")       
.
.
CMakeFile[0, 814]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "PROJECT_NAME tft_life"] close:[140, 141, ")"]
    Argument[119, 131] text:[119, 131, "PROJECT_NAME"]
    Argument[132, 140] text:[132, 140, "tft_life"]
  LineEnding[141, 142]
  BlankLine[142, 143]
  LineComment[143, 183] open:[143, 144, "#"] text:[144, 183, "# This must be set before project call\n"]
  Command[183, 219] text:[183, 186, "set"] open:[186, 187, "("] arguments:[187, 218, "${CMAKE_PROJECT_NAME}_BOARD uno"] close:[218, 219, ")"]
    Argument[187, 214] text:[187, 214, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[215, 218] text:[215, 218, "uno"]
  LineEnding[219, 220]
  Command[220, 241] text:[220, 223, "set"] open:[223, 224, "("] arguments:[224, 240, "ARDUINO_CPU none"] close:[240, 241, ")"]
    Argument[224, 235] text:[224, 235, "ARDUINO_CPU"]
    Argument[236, 240] text:[236, 240, "none"]
  LineEnding[241, 242]
  BlankLine[242, 243]
  Command[243, 267] text:[243, 250, "project"] open:[250, 251, "("] arguments:[251, 266, "${PROJECT_NAME}"] close:[266, 267, ")"]
    Argument[251, 266] text:[251, 266, "${PROJECT_NAME}"]
  LineEnding[267, 268]
  BlankLine[268, 269]
  LineComment[269, 294] open:[269, 270, "#"] text:[270, 294, " Define the source code\n"]
  Command[294, 332] text:[294, 297, "set"] open:[297, 298, "("] arguments:[298, 331, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[331, 332, ")"]
    Argument[298, 318] text:[298, 318, "${PROJECT_NAME}_SRCS"]
    Argument[319, 331] text:[319, 331, "tft_life.cpp"]
  LineEnding[336, 337]
  LineComment[337, 391] open:[337, 338, "#"] text:[338, 391, "set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)      \n"]
  Command[391, 439] text:[391, 407, "link_directories"] open:[407, 408, "("] arguments:[408, 438, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[438, 439, ")"]
    Argument[408, 438] text:[408, 438, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[447, 448]
  BlankLine[448, 449]
  LineComment[449, 501] open:[449, 450, "#"] text:[450, 501, "### Uncomment below additional settings as needed.\n"]
  Command[501, 549] text:[501, 504, "set"] open:[504, 505, "("] arguments:[505, 548, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[548, 549, ")"]
    Argument[505, 537] text:[505, 537, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[538, 548] text:[538, 548, "avrispmkii"]
  LineEnding[557, 558]
  Command[558, 616] text:[558, 561, "set"] open:[561, 562, "("] arguments:[562, 615, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[615, 616, ")"]
    Argument[562, 588] text:[562, 588, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[589, 615] text:[589, 615, "/dev/cu.usbserial-00000000"]
  LineEnding[626, 627]
  Command[627, 664] text:[627, 630, "set"] open:[631, 632, "("] arguments:[632, 663, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[663, 664, ")"]
    Argument[632, 660] text:[632, 660, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[661, 663] text:[661, 663, "-v"]
  LineEnding[670, 671]
  BlankLine[671, 672]
  Command[672, 699] text:[672, 675, "set"] open:[675, 676, "("] arguments:[676, 698, "pro.upload.speed 57600"] close:[698, 699, ")"]
    Argument[676, 692] text:[676, 692, "pro.upload.speed"]
    Argument[693, 698] text:[693, 698, "57600"]
  LineEnding[710, 711]
  Command[711, 737] text:[711, 714, "set"] open:[714, 715, "("] arguments:[715, 736, "libName_RECURSE false"] close:[736, 737, ")"]
    Argument[715, 730] text:[715, 730, "libName_RECURSE"]
    Argument[731, 736] text:[731, 736, "false"]
  LineEnding[755, 756]
  BlankLine[756, 757]
  Command[757, 807] text:[757, 782, "generate_arduino_firmware"] open:[782, 783, "("] arguments:[783, 806, "\"${CMAKE_PROJECT_NAME}\""] close:[806, 807, ")"]
    Argument[783, 806] open:[783, 784, "\""] text:[784, 805, "${CMAKE_PROJECT_NAME}"] close:[805, 806, "\""]
````````````````````````````````


actual file no comments

```````````````````````````````` example(Arduno: 4) options(auto-config,  ast-blank,  ast-line-eol,  ast-arg-seps)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD pro)    
set(ARDUINO_CPU 8MHzatmega328)

project(${PROJECT_NAME})

# Define the source code
set(${PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set (${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
.
CMakeFile[0, 721]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "PROJECT_NAME tft_life"] close:[140, 141, ")"]
    Argument[119, 131] text:[119, 131, "PROJECT_NAME"]
    Argument[132, 140] text:[132, 140, "tft_life"]
  LineEnding[141, 142]
  BlankLine[142, 143]
  LineEnding[182, 183]
  Command[183, 219] text:[183, 186, "set"] open:[186, 187, "("] arguments:[187, 218, "${CMAKE_PROJECT_NAME}_BOARD pro"] close:[218, 219, ")"]
    Argument[187, 214] text:[187, 214, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[215, 218] text:[215, 218, "pro"]
  LineEnding[223, 224]
  Command[224, 254] text:[224, 227, "set"] open:[227, 228, "("] arguments:[228, 253, "ARDUINO_CPU 8MHzatmega328"] close:[253, 254, ")"]
    Argument[228, 239] text:[228, 239, "ARDUINO_CPU"]
    Argument[240, 253] text:[240, 253, "8MHzatmega328"]
  LineEnding[254, 255]
  BlankLine[255, 256]
  Command[256, 280] text:[256, 263, "project"] open:[263, 264, "("] arguments:[264, 279, "${PROJECT_NAME}"] close:[279, 280, ")"]
    Argument[264, 279] text:[264, 279, "${PROJECT_NAME}"]
  LineEnding[280, 281]
  BlankLine[281, 282]
  LineEnding[306, 307]
  Command[307, 345] text:[307, 310, "set"] open:[310, 311, "("] arguments:[311, 344, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[344, 345, ")"]
    Argument[311, 331] text:[311, 331, "${PROJECT_NAME}_SRCS"]
    Argument[332, 344] text:[332, 344, "tft_life.cpp"]
  LineEnding[345, 346]
  Command[346, 442] spaces:[346, 394, "#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)\n"] text:[394, 410, "link_directories"] open:[410, 411, "("] arguments:[411, 441, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[441, 442, ")"]
    Argument[411, 441] text:[411, 441, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[442, 443]
  BlankLine[443, 444]
  LineEnding[495, 496]
  Command[496, 544] text:[496, 499, "set"] open:[499, 500, "("] arguments:[500, 543, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[543, 544, ")"]
    Argument[500, 532] text:[500, 532, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[533, 543] text:[533, 543, "avrispmkii"]
  LineEnding[544, 545]
  Command[545, 603] text:[545, 548, "set"] open:[548, 549, "("] arguments:[549, 602, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[602, 603, ")"]
    Argument[549, 575] text:[549, 575, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[576, 602] text:[576, 602, "/dev/cu.usbserial-00000000"]
  LineEnding[603, 604]
  Command[604, 641] text:[604, 607, "set"] open:[608, 609, "("] arguments:[609, 640, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[640, 641, ")"]
    Argument[609, 637] text:[609, 637, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[638, 640] text:[638, 640, "-v"]
  LineEnding[641, 642]
  BlankLine[672, 673]
  Command[673, 721] text:[673, 698, "generate_arduino_firmware"] open:[698, 699, "("] arguments:[699, 720, "${CMAKE_PROJECT_NAME}"] close:[720, 721, ")"]
    Argument[699, 720] text:[699, 720, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


actual file

```````````````````````````````` example(Arduno: 5) options(auto-config,  ast-blank,  ast-line-eol,  ast-arg-seps,  ast-comments)
cmake_minimum_required(VERSION 3.0.0)
set(CMAKE_TOOLCHAIN_FILE /usr/local/etc/arduino-cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(PROJECT_NAME CookiePortioner)

# use UART with interrupts if set to non-zero
set(UART_INT 0)
# use DEBUG_MODE dev version of options
set(DEBUG_MODE 1)
set(DEBUG_MODE_PRESCALER 1)
set(DEBUG_MODE_ENCODER 1)
set(DEBUG_MODE_LARGE_FONTS 0)
set(GFX_BIT_BLIT 0)
set(PROFILE_GFX 1)
set(LARGE_FONTS 1)

# SSD1306 definition to use TWI interrupts
add_compile_definitions(INCLUDE_TWI_INT)
add_compile_definitions(GFX_NO_LINE_PATTERNS)
#add_compile_definitions(GFX_SMALL_FONT)

add_link_options(-Xlinker -Map=../output.map)

if (DEBUG_MODE)
    # SSD1306 subs large font for double size digits, `m`, `l`, and `.`
    if (DEV_MODE)
        add_compile_definitions(DEV_MODE)
    endif ()

    if (GFX_BIT_BLIT)
    else ()
        add_compile_definitions(GFX_NO_BIT_BLIT)
    endif ()

    if (PROFILE_GFX)
        add_compile_definitions(PROFILE_GFX)
        set(PROFILE_GFX_C_FILES ProfileForm.cpp)
        set(PROFILE_GFX_H_FILES ProfileForm.h)
    endif ()

    if (DEBUG_MODE_ENCODER)
        add_compile_definitions(DEBUG_MODE_ENCODER)
    endif ()

    if (DEBUG_MODE_LARGE_FONTS)
        add_compile_definitions(GFX_LARGE_FONT)
    endif ()

    add_compile_definitions(DEBUG_MODE)
    if (DEBUG_MODE_PRESCALER)
        add_compile_definitions(DEBUG_MODE_PRESCALER)
        set(ARDUINO_CPU 8MHzatmega328)
    else ()
        set(ARDUINO_CPU 16MHzatmega328)
    endif ()
else ()
    if (LARGE_FONTS)
        add_compile_definitions(GFX_LARGE_FONT)
    endif ()

    set(ARDUINO_CPU 8MHzatmega328)
endif ()

set(${PROJECT_NAME}_BOARD pro)
project(${PROJECT_NAME})

if (UART_INT)
    add_compile_definitions(SSD1306_UART_INT)
    set(UART_C_FILES drivers/uartint.c drivers/ring.c)
    set(UART_H_FILES drivers/uartint.h drivers/ring.h)
else ()
    set(UART_C_FILES drivers/uart.c)
    set(UART_H_FILES drivers/uart.h)
endif ()

# Define additional source and header files or default arduino sketch files
set(${PROJECT_NAME}_SRCS
    CookiePortioner.cpp
    drivers/interrupt_handlers.c
    EventTranslator.cpp
    MainScreen.cpp
    ${PROFILE_GFX_C_FILES}
    tasks/AdcHandler.cpp
    tasks/MotorControl.cpp
    tasks/PortionControl.cpp
    tasks/SoundHandler.cpp
    util/HistoryBuffer.cpp
    util/non_volatile_data.c
    util/power_down.c
    util/Strings.c
    util/TimeIt.cpp
    util/TimeIt.h
    ${UART_C_FILES}
    )
set(${PROJECT_NAME}_HDRS
    config.h
    drivers/interrupt_handlers.h
    EventTranslator.cpp
    InputEvents.h
    MainScreen.h
    ${PROFILE_GFX_H_FILES}
    pin_config.h
    tasks/AdcHandler.h
    tasks/MotorControl.h
    tasks/PortionControl.h
    tasks/SoundHandler.h
    type_defs.h
    util/HistoryBuffer.h
    util/non_volatile_data.h
    util/power_down.h
    util/Strings.h
    ${UART_H_FILES}
    )

### Additional static libraries to include in the target.
# set(${PROJECT_NAME}_LIBS)

.
.
CMakeFile[0, 2962]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 3.0.0"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "3.0.0"]
  LineEnding[37, 38]
  Command[38, 115] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 114, "CMAKE_TOOLCHAIN_FILE /usr/local/etc/arduino-cmake/ArduinoToolchain.cmake"] close:[114, 115, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 114] text:[63, 114, "/usr/local/etc/arduino-cmake/ArduinoToolchain.cmake"]
  LineEnding[115, 116]
  Command[116, 142] text:[116, 119, "set"] open:[119, 120, "("] arguments:[120, 141, "CMAKE_CXX_STANDARD 98"] close:[141, 142, ")"]
    Argument[120, 138] text:[120, 138, "CMAKE_CXX_STANDARD"]
    Argument[139, 141] text:[139, 141, "98"]
  LineEnding[142, 143]
  Command[143, 176] text:[143, 146, "set"] open:[146, 147, "("] arguments:[147, 175, "PROJECT_NAME CookiePortioner"] close:[175, 176, ")"]
    Argument[147, 159] text:[147, 159, "PROJECT_NAME"]
    Argument[160, 175] text:[160, 175, "CookiePortioner"]
  LineEnding[176, 177]
  BlankLine[177, 178]
  LineComment[178, 224] open:[178, 179, "#"] text:[179, 224, " use UART with interrupts if set to non-zero\n"]
  Command[224, 239] text:[224, 227, "set"] open:[227, 228, "("] arguments:[228, 238, "UART_INT 0"] close:[238, 239, ")"]
    Argument[228, 236] text:[228, 236, "UART_INT"]
    Argument[237, 238] text:[237, 238, "0"]
  LineEnding[239, 240]
  LineComment[240, 280] open:[240, 241, "#"] text:[241, 280, " use DEBUG_MODE dev version of options\n"]
  Command[280, 297] text:[280, 283, "set"] open:[283, 284, "("] arguments:[284, 296, "DEBUG_MODE 1"] close:[296, 297, ")"]
    Argument[284, 294] text:[284, 294, "DEBUG_MODE"]
    Argument[295, 296] text:[295, 296, "1"]
  LineEnding[297, 298]
  Command[298, 325] text:[298, 301, "set"] open:[301, 302, "("] arguments:[302, 324, "DEBUG_MODE_PRESCALER 1"] close:[324, 325, ")"]
    Argument[302, 322] text:[302, 322, "DEBUG_MODE_PRESCALER"]
    Argument[323, 324] text:[323, 324, "1"]
  LineEnding[325, 326]
  Command[326, 351] text:[326, 329, "set"] open:[329, 330, "("] arguments:[330, 350, "DEBUG_MODE_ENCODER 1"] close:[350, 351, ")"]
    Argument[330, 348] text:[330, 348, "DEBUG_MODE_ENCODER"]
    Argument[349, 350] text:[349, 350, "1"]
  LineEnding[351, 352]
  Command[352, 381] text:[352, 355, "set"] open:[355, 356, "("] arguments:[356, 380, "DEBUG_MODE_LARGE_FONTS 0"] close:[380, 381, ")"]
    Argument[356, 378] text:[356, 378, "DEBUG_MODE_LARGE_FONTS"]
    Argument[379, 380] text:[379, 380, "0"]
  LineEnding[381, 382]
  Command[382, 401] text:[382, 385, "set"] open:[385, 386, "("] arguments:[386, 400, "GFX_BIT_BLIT 0"] close:[400, 401, ")"]
    Argument[386, 398] text:[386, 398, "GFX_BIT_BLIT"]
    Argument[399, 400] text:[399, 400, "0"]
  LineEnding[401, 402]
  Command[402, 420] text:[402, 405, "set"] open:[405, 406, "("] arguments:[406, 419, "PROFILE_GFX 1"] close:[419, 420, ")"]
    Argument[406, 417] text:[406, 417, "PROFILE_GFX"]
    Argument[418, 419] text:[418, 419, "1"]
  LineEnding[420, 421]
  Command[421, 439] text:[421, 424, "set"] open:[424, 425, "("] arguments:[425, 438, "LARGE_FONTS 1"] close:[438, 439, ")"]
    Argument[425, 436] text:[425, 436, "LARGE_FONTS"]
    Argument[437, 438] text:[437, 438, "1"]
  LineEnding[439, 440]
  BlankLine[440, 441]
  LineComment[441, 484] open:[441, 442, "#"] text:[442, 484, " SSD1306 definition to use TWI interrupts\n"]
  Command[484, 524] text:[484, 507, "add_compile_definitions"] open:[507, 508, "("] arguments:[508, 523, "INCLUDE_TWI_INT"] close:[523, 524, ")"]
    Argument[508, 523] text:[508, 523, "INCLUDE_TWI_INT"]
  LineEnding[524, 525]
  Command[525, 570] text:[525, 548, "add_compile_definitions"] open:[548, 549, "("] arguments:[549, 569, "GFX_NO_LINE_PATTERNS"] close:[569, 570, ")"]
    Argument[549, 569] text:[549, 569, "GFX_NO_LINE_PATTERNS"]
  LineEnding[570, 571]
  LineComment[571, 612] open:[571, 572, "#"] text:[572, 612, "add_compile_definitions(GFX_SMALL_FONT)\n"]
  BlankLine[612, 613]
  Command[613, 658] text:[613, 629, "add_link_options"] open:[629, 630, "("] arguments:[630, 657, "-Xlinker -Map=../output.map"] close:[657, 658, ")"]
    Argument[630, 638] text:[630, 638, "-Xlinker"]
    Argument[639, 657] text:[639, 657, "-Map=../output.map"]
  LineEnding[658, 659]
  BlankLine[659, 660]
  Command[660, 675] text:[660, 662, "if"] open:[663, 664, "("] arguments:[664, 674, "DEBUG_MODE"] close:[674, 675, ")"]
    Argument[664, 674] text:[664, 674, "DEBUG_MODE"]
  LineEnding[675, 676]
  LineComment[676, 748] spaces:[676, 680, "    "] open:[676, 677, " "] text:[677, 748, "   # SSD1306 subs large font for double size digits, `m`, `l`, and `.`\n"]
  Command[748, 765] spaces:[748, 752, "    "] text:[752, 754, "if"] open:[755, 756, "("] arguments:[756, 764, "DEV_MODE"] close:[764, 765, ")"]
    Argument[756, 764] text:[756, 764, "DEV_MODE"]
  LineEnding[765, 766]
  Command[766, 807] spaces:[766, 774, "        "] text:[774, 797, "add_compile_definitions"] open:[797, 798, "("] arguments:[798, 806, "DEV_MODE"] close:[806, 807, ")"]
    Argument[798, 806] text:[798, 806, "DEV_MODE"]
  LineEnding[807, 808]
  Command[808, 820] spaces:[808, 812, "    "] text:[812, 817, "endif"] open:[818, 819, "("] arguments:[819, 819] close:[819, 820, ")"]
  LineEnding[820, 821]
  BlankLine[821, 822]
  Command[822, 843] spaces:[822, 826, "    "] text:[826, 828, "if"] open:[829, 830, "("] arguments:[830, 842, "GFX_BIT_BLIT"] close:[842, 843, ")"]
    Argument[830, 842] text:[830, 842, "GFX_BIT_BLIT"]
  LineEnding[843, 844]
  Command[844, 855] spaces:[844, 848, "    "] text:[848, 852, "else"] open:[853, 854, "("] arguments:[854, 854] close:[854, 855, ")"]
  LineEnding[855, 856]
  Command[856, 904] spaces:[856, 864, "        "] text:[864, 887, "add_compile_definitions"] open:[887, 888, "("] arguments:[888, 903, "GFX_NO_BIT_BLIT"] close:[903, 904, ")"]
    Argument[888, 903] text:[888, 903, "GFX_NO_BIT_BLIT"]
  LineEnding[904, 905]
  Command[905, 917] spaces:[905, 909, "    "] text:[909, 914, "endif"] open:[915, 916, "("] arguments:[916, 916] close:[916, 917, ")"]
  LineEnding[917, 918]
  BlankLine[918, 919]
  Command[919, 939] spaces:[919, 923, "    "] text:[923, 925, "if"] open:[926, 927, "("] arguments:[927, 938, "PROFILE_GFX"] close:[938, 939, ")"]
    Argument[927, 938] text:[927, 938, "PROFILE_GFX"]
  LineEnding[939, 940]
  Command[940, 984] spaces:[940, 948, "        "] text:[948, 971, "add_compile_definitions"] open:[971, 972, "("] arguments:[972, 983, "PROFILE_GFX"] close:[983, 984, ")"]
    Argument[972, 983] text:[972, 983, "PROFILE_GFX"]
  LineEnding[984, 985]
  Command[985, 1033] spaces:[985, 993, "        "] text:[993, 996, "set"] open:[996, 997, "("] arguments:[997, 1032, "PROFILE_GFX_C_FILES ProfileForm.cpp"] close:[1032, 1033, ")"]
    Argument[997, 1016] text:[997, 1016, "PROFILE_GFX_C_FILES"]
    Argument[1017, 1032] text:[1017, 1032, "ProfileForm.cpp"]
  LineEnding[1033, 1034]
  Command[1034, 1080] spaces:[1034, 1042, "        "] text:[1042, 1045, "set"] open:[1045, 1046, "("] arguments:[1046, 1079, "PROFILE_GFX_H_FILES ProfileForm.h"] close:[1079, 1080, ")"]
    Argument[1046, 1065] text:[1046, 1065, "PROFILE_GFX_H_FILES"]
    Argument[1066, 1079] text:[1066, 1079, "ProfileForm.h"]
  LineEnding[1080, 1081]
  Command[1081, 1093] spaces:[1081, 1085, "    "] text:[1085, 1090, "endif"] open:[1091, 1092, "("] arguments:[1092, 1092] close:[1092, 1093, ")"]
  LineEnding[1093, 1094]
  BlankLine[1094, 1095]
  Command[1095, 1122] spaces:[1095, 1099, "    "] text:[1099, 1101, "if"] open:[1102, 1103, "("] arguments:[1103, 1121, "DEBUG_MODE_ENCODER"] close:[1121, 1122, ")"]
    Argument[1103, 1121] text:[1103, 1121, "DEBUG_MODE_ENCODER"]
  LineEnding[1122, 1123]
  Command[1123, 1174] spaces:[1123, 1131, "        "] text:[1131, 1154, "add_compile_definitions"] open:[1154, 1155, "("] arguments:[1155, 1173, "DEBUG_MODE_ENCODER"] close:[1173, 1174, ")"]
    Argument[1155, 1173] text:[1155, 1173, "DEBUG_MODE_ENCODER"]
  LineEnding[1174, 1175]
  Command[1175, 1187] spaces:[1175, 1179, "    "] text:[1179, 1184, "endif"] open:[1185, 1186, "("] arguments:[1186, 1186] close:[1186, 1187, ")"]
  LineEnding[1187, 1188]
  BlankLine[1188, 1189]
  Command[1189, 1220] spaces:[1189, 1193, "    "] text:[1193, 1195, "if"] open:[1196, 1197, "("] arguments:[1197, 1219, "DEBUG_MODE_LARGE_FONTS"] close:[1219, 1220, ")"]
    Argument[1197, 1219] text:[1197, 1219, "DEBUG_MODE_LARGE_FONTS"]
  LineEnding[1220, 1221]
  Command[1221, 1268] spaces:[1221, 1229, "        "] text:[1229, 1252, "add_compile_definitions"] open:[1252, 1253, "("] arguments:[1253, 1267, "GFX_LARGE_FONT"] close:[1267, 1268, ")"]
    Argument[1253, 1267] text:[1253, 1267, "GFX_LARGE_FONT"]
  LineEnding[1268, 1269]
  Command[1269, 1281] spaces:[1269, 1273, "    "] text:[1273, 1278, "endif"] open:[1279, 1280, "("] arguments:[1280, 1280] close:[1280, 1281, ")"]
  LineEnding[1281, 1282]
  BlankLine[1282, 1283]
  Command[1283, 1322] spaces:[1283, 1287, "    "] text:[1287, 1310, "add_compile_definitions"] open:[1310, 1311, "("] arguments:[1311, 1321, "DEBUG_MODE"] close:[1321, 1322, ")"]
    Argument[1311, 1321] text:[1311, 1321, "DEBUG_MODE"]
  LineEnding[1322, 1323]
  Command[1323, 1352] spaces:[1323, 1327, "    "] text:[1327, 1329, "if"] open:[1330, 1331, "("] arguments:[1331, 1351, "DEBUG_MODE_PRESCALER"] close:[1351, 1352, ")"]
    Argument[1331, 1351] text:[1331, 1351, "DEBUG_MODE_PRESCALER"]
  LineEnding[1352, 1353]
  Command[1353, 1406] spaces:[1353, 1361, "        "] text:[1361, 1384, "add_compile_definitions"] open:[1384, 1385, "("] arguments:[1385, 1405, "DEBUG_MODE_PRESCALER"] close:[1405, 1406, ")"]
    Argument[1385, 1405] text:[1385, 1405, "DEBUG_MODE_PRESCALER"]
  LineEnding[1406, 1407]
  Command[1407, 1445] spaces:[1407, 1415, "        "] text:[1415, 1418, "set"] open:[1418, 1419, "("] arguments:[1419, 1444, "ARDUINO_CPU 8MHzatmega328"] close:[1444, 1445, ")"]
    Argument[1419, 1430] text:[1419, 1430, "ARDUINO_CPU"]
    Argument[1431, 1444] text:[1431, 1444, "8MHzatmega328"]
  LineEnding[1445, 1446]
  Command[1446, 1457] spaces:[1446, 1450, "    "] text:[1450, 1454, "else"] open:[1455, 1456, "("] arguments:[1456, 1456] close:[1456, 1457, ")"]
  LineEnding[1457, 1458]
  Command[1458, 1497] spaces:[1458, 1466, "        "] text:[1466, 1469, "set"] open:[1469, 1470, "("] arguments:[1470, 1496, "ARDUINO_CPU 16MHzatmega328"] close:[1496, 1497, ")"]
    Argument[1470, 1481] text:[1470, 1481, "ARDUINO_CPU"]
    Argument[1482, 1496] text:[1482, 1496, "16MHzatmega328"]
  LineEnding[1497, 1498]
  Command[1498, 1510] spaces:[1498, 1502, "    "] text:[1502, 1507, "endif"] open:[1508, 1509, "("] arguments:[1509, 1509] close:[1509, 1510, ")"]
  LineEnding[1510, 1511]
  Command[1511, 1518] text:[1511, 1515, "else"] open:[1516, 1517, "("] arguments:[1517, 1517] close:[1517, 1518, ")"]
  LineEnding[1518, 1519]
  Command[1519, 1539] spaces:[1519, 1523, "    "] text:[1523, 1525, "if"] open:[1526, 1527, "("] arguments:[1527, 1538, "LARGE_FONTS"] close:[1538, 1539, ")"]
    Argument[1527, 1538] text:[1527, 1538, "LARGE_FONTS"]
  LineEnding[1539, 1540]
  Command[1540, 1587] spaces:[1540, 1548, "        "] text:[1548, 1571, "add_compile_definitions"] open:[1571, 1572, "("] arguments:[1572, 1586, "GFX_LARGE_FONT"] close:[1586, 1587, ")"]
    Argument[1572, 1586] text:[1572, 1586, "GFX_LARGE_FONT"]
  LineEnding[1587, 1588]
  Command[1588, 1600] spaces:[1588, 1592, "    "] text:[1592, 1597, "endif"] open:[1598, 1599, "("] arguments:[1599, 1599] close:[1599, 1600, ")"]
  LineEnding[1600, 1601]
  BlankLine[1601, 1602]
  Command[1602, 1636] spaces:[1602, 1606, "    "] text:[1606, 1609, "set"] open:[1609, 1610, "("] arguments:[1610, 1635, "ARDUINO_CPU 8MHzatmega328"] close:[1635, 1636, ")"]
    Argument[1610, 1621] text:[1610, 1621, "ARDUINO_CPU"]
    Argument[1622, 1635] text:[1622, 1635, "8MHzatmega328"]
  LineEnding[1636, 1637]
  Command[1637, 1645] text:[1637, 1642, "endif"] open:[1643, 1644, "("] arguments:[1644, 1644] close:[1644, 1645, ")"]
  LineEnding[1645, 1646]
  BlankLine[1646, 1647]
  Command[1647, 1677] text:[1647, 1650, "set"] open:[1650, 1651, "("] arguments:[1651, 1676, "${PROJECT_NAME}_BOARD pro"] close:[1676, 1677, ")"]
    Argument[1651, 1672] text:[1651, 1672, "${PROJECT_NAME}_BOARD"]
    Argument[1673, 1676] text:[1673, 1676, "pro"]
  LineEnding[1677, 1678]
  Command[1678, 1702] text:[1678, 1685, "project"] open:[1685, 1686, "("] arguments:[1686, 1701, "${PROJECT_NAME}"] close:[1701, 1702, ")"]
    Argument[1686, 1701] text:[1686, 1701, "${PROJECT_NAME}"]
  LineEnding[1702, 1703]
  BlankLine[1703, 1704]
  Command[1704, 1717] text:[1704, 1706, "if"] open:[1707, 1708, "("] arguments:[1708, 1716, "UART_INT"] close:[1716, 1717, ")"]
    Argument[1708, 1716] text:[1708, 1716, "UART_INT"]
  LineEnding[1717, 1718]
  Command[1718, 1763] spaces:[1718, 1722, "    "] text:[1722, 1745, "add_compile_definitions"] open:[1745, 1746, "("] arguments:[1746, 1762, "SSD1306_UART_INT"] close:[1762, 1763, ")"]
    Argument[1746, 1762] text:[1746, 1762, "SSD1306_UART_INT"]
  LineEnding[1763, 1764]
  Command[1764, 1818] spaces:[1764, 1768, "    "] text:[1768, 1771, "set"] open:[1771, 1772, "("] arguments:[1772, 1817, "UART_C_FILES drivers/uartint.c drivers/ring.c"] close:[1817, 1818, ")"]
    Argument[1772, 1784] text:[1772, 1784, "UART_C_FILES"]
    Argument[1785, 1802] text:[1785, 1802, "drivers/uartint.c"]
    Argument[1803, 1817] text:[1803, 1817, "drivers/ring.c"]
  LineEnding[1818, 1819]
  Command[1819, 1873] spaces:[1819, 1823, "    "] text:[1823, 1826, "set"] open:[1826, 1827, "("] arguments:[1827, 1872, "UART_H_FILES drivers/uartint.h drivers/ring.h"] close:[1872, 1873, ")"]
    Argument[1827, 1839] text:[1827, 1839, "UART_H_FILES"]
    Argument[1840, 1857] text:[1840, 1857, "drivers/uartint.h"]
    Argument[1858, 1872] text:[1858, 1872, "drivers/ring.h"]
  LineEnding[1873, 1874]
  Command[1874, 1881] text:[1874, 1878, "else"] open:[1879, 1880, "("] arguments:[1880, 1880] close:[1880, 1881, ")"]
  LineEnding[1881, 1882]
  Command[1882, 1918] spaces:[1882, 1886, "    "] text:[1886, 1889, "set"] open:[1889, 1890, "("] arguments:[1890, 1917, "UART_C_FILES drivers/uart.c"] close:[1917, 1918, ")"]
    Argument[1890, 1902] text:[1890, 1902, "UART_C_FILES"]
    Argument[1903, 1917] text:[1903, 1917, "drivers/uart.c"]
  LineEnding[1918, 1919]
  Command[1919, 1955] spaces:[1919, 1923, "    "] text:[1923, 1926, "set"] open:[1926, 1927, "("] arguments:[1927, 1954, "UART_H_FILES drivers/uart.h"] close:[1954, 1955, ")"]
    Argument[1927, 1939] text:[1927, 1939, "UART_H_FILES"]
    Argument[1940, 1954] text:[1940, 1954, "drivers/uart.h"]
  LineEnding[1955, 1956]
  Command[1956, 1964] text:[1956, 1961, "endif"] open:[1962, 1963, "("] arguments:[1963, 1963] close:[1963, 1964, ")"]
  LineEnding[1964, 1965]
  BlankLine[1965, 1966]
  LineComment[1966, 2042] open:[1966, 1967, "#"] text:[1967, 2042, " Define additional source and header files or default arduino sketch files\n"]
  Command[2042, 2462] text:[2042, 2045, "set"] open:[2045, 2046, "("] arguments:[2046, 2461, "${PROJECT_NAME}_SRCS\n    CookiePortioner.cpp\n    drivers/interrupt_handlers.c\n    EventTranslator.cpp\n    MainScreen.cpp\n    ${PROFILE_GFX_C_FILES}\n    tasks/AdcHandler.cpp\n    tasks/MotorControl.cpp\n    tasks/PortionControl.cpp\n    tasks/SoundHandler.cpp\n    util/HistoryBuffer.cpp\n    util/non_volatile_data.c\n    util/power_down.c\n    util/Strings.c\n    util/TimeIt.cpp\n    util/TimeIt.h\n    ${UART_C_FILES}\n    "] close:[2461, 2462, ")"]
    Argument[2046, 2066] text:[2046, 2066, "${PROJECT_NAME}_SRCS"]
    LineEnding[2066, 2067]
    Argument[2071, 2090] text:[2071, 2090, "CookiePortioner.cpp"]
    LineEnding[2090, 2091]
    Argument[2095, 2123] text:[2095, 2123, "drivers/interrupt_handlers.c"]
    LineEnding[2123, 2124]
    Argument[2128, 2147] text:[2128, 2147, "EventTranslator.cpp"]
    LineEnding[2147, 2148]
    Argument[2152, 2166] text:[2152, 2166, "MainScreen.cpp"]
    LineEnding[2166, 2167]
    Argument[2171, 2193] text:[2171, 2193, "${PROFILE_GFX_C_FILES}"]
    LineEnding[2193, 2194]
    Argument[2198, 2218] text:[2198, 2218, "tasks/AdcHandler.cpp"]
    LineEnding[2218, 2219]
    Argument[2223, 2245] text:[2223, 2245, "tasks/MotorControl.cpp"]
    LineEnding[2245, 2246]
    Argument[2250, 2274] text:[2250, 2274, "tasks/PortionControl.cpp"]
    LineEnding[2274, 2275]
    Argument[2279, 2301] text:[2279, 2301, "tasks/SoundHandler.cpp"]
    LineEnding[2301, 2302]
    Argument[2306, 2328] text:[2306, 2328, "util/HistoryBuffer.cpp"]
    LineEnding[2328, 2329]
    Argument[2333, 2357] text:[2333, 2357, "util/non_volatile_data.c"]
    LineEnding[2357, 2358]
    Argument[2362, 2379] text:[2362, 2379, "util/power_down.c"]
    LineEnding[2379, 2380]
    Argument[2384, 2398] text:[2384, 2398, "util/Strings.c"]
    LineEnding[2398, 2399]
    Argument[2403, 2418] text:[2403, 2418, "util/TimeIt.cpp"]
    LineEnding[2418, 2419]
    Argument[2423, 2436] text:[2423, 2436, "util/TimeIt.h"]
    LineEnding[2436, 2437]
    Argument[2441, 2456] text:[2441, 2456, "${UART_C_FILES}"]
    LineEnding[2456, 2457]
  LineEnding[2462, 2463]
  Command[2463, 2873] text:[2463, 2466, "set"] open:[2466, 2467, "("] arguments:[2467, 2872, "${PROJECT_NAME}_HDRS\n    config.h\n    drivers/interrupt_handlers.h\n    EventTranslator.cpp\n    InputEvents.h\n    MainScreen.h\n    ${PROFILE_GFX_H_FILES}\n    pin_config.h\n    tasks/AdcHandler.h\n    tasks/MotorControl.h\n    tasks/PortionControl.h\n    tasks/SoundHandler.h\n    type_defs.h\n    util/HistoryBuffer.h\n    util/non_volatile_data.h\n    util/power_down.h\n    util/Strings.h\n    ${UART_H_FILES}\n    "] close:[2872, 2873, ")"]
    Argument[2467, 2487] text:[2467, 2487, "${PROJECT_NAME}_HDRS"]
    LineEnding[2487, 2488]
    Argument[2492, 2500] text:[2492, 2500, "config.h"]
    LineEnding[2500, 2501]
    Argument[2505, 2533] text:[2505, 2533, "drivers/interrupt_handlers.h"]
    LineEnding[2533, 2534]
    Argument[2538, 2557] text:[2538, 2557, "EventTranslator.cpp"]
    LineEnding[2557, 2558]
    Argument[2562, 2575] text:[2562, 2575, "InputEvents.h"]
    LineEnding[2575, 2576]
    Argument[2580, 2592] text:[2580, 2592, "MainScreen.h"]
    LineEnding[2592, 2593]
    Argument[2597, 2619] text:[2597, 2619, "${PROFILE_GFX_H_FILES}"]
    LineEnding[2619, 2620]
    Argument[2624, 2636] text:[2624, 2636, "pin_config.h"]
    LineEnding[2636, 2637]
    Argument[2641, 2659] text:[2641, 2659, "tasks/AdcHandler.h"]
    LineEnding[2659, 2660]
    Argument[2664, 2684] text:[2664, 2684, "tasks/MotorControl.h"]
    LineEnding[2684, 2685]
    Argument[2689, 2711] text:[2689, 2711, "tasks/PortionControl.h"]
    LineEnding[2711, 2712]
    Argument[2716, 2736] text:[2716, 2736, "tasks/SoundHandler.h"]
    LineEnding[2736, 2737]
    Argument[2741, 2752] text:[2741, 2752, "type_defs.h"]
    LineEnding[2752, 2753]
    Argument[2757, 2777] text:[2757, 2777, "util/HistoryBuffer.h"]
    LineEnding[2777, 2778]
    Argument[2782, 2806] text:[2782, 2806, "util/non_volatile_data.h"]
    LineEnding[2806, 2807]
    Argument[2811, 2828] text:[2811, 2828, "util/power_down.h"]
    LineEnding[2828, 2829]
    Argument[2833, 2847] text:[2833, 2847, "util/Strings.h"]
    LineEnding[2847, 2848]
    Argument[2852, 2867] text:[2852, 2867, "${UART_H_FILES}"]
    LineEnding[2867, 2868]
  LineEnding[2873, 2874]
  BlankLine[2874, 2875]
  LineComment[2875, 2933] open:[2875, 2876, "#"] text:[2876, 2933, "## Additional static libraries to include in the target.\n"]
  LineComment[2933, 2961] open:[2933, 2934, "#"] text:[2934, 2961, " set(${PROJECT_NAME}_LIBS)\n"]
  BlankLine[2961, 2962]
````````````````````````````````

actual file no eol

```````````````````````````````` example(Arduno: 6) options(auto-config,  ast-blank,  ast-arg-seps,  ast-comments)
cmake_minimum_required(VERSION 3.0.0)
set(CMAKE_TOOLCHAIN_FILE /usr/local/etc/arduino-cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(PROJECT_NAME CookiePortioner)

# use UART with interrupts if set to non-zero
set(UART_INT 0)
# use DEBUG_MODE dev version of options
set(DEBUG_MODE 1)
set(DEBUG_MODE_PRESCALER 1)
set(DEBUG_MODE_ENCODER 1)
set(DEBUG_MODE_LARGE_FONTS 0)
set(GFX_BIT_BLIT 0)
set(PROFILE_GFX 1)
set(LARGE_FONTS 1)

# SSD1306 definition to use TWI interrupts
add_compile_definitions(INCLUDE_TWI_INT)
add_compile_definitions(GFX_NO_LINE_PATTERNS)
#add_compile_definitions(GFX_SMALL_FONT)

add_link_options(-Xlinker -Map=../output.map)

if (DEBUG_MODE)
    # SSD1306 subs large font for double size digits, `m`, `l`, and `.`
    if (DEV_MODE)
        add_compile_definitions(DEV_MODE)
    endif ()

    if (GFX_BIT_BLIT)
    else ()
        add_compile_definitions(GFX_NO_BIT_BLIT)
    endif ()

    if (PROFILE_GFX)
        add_compile_definitions(PROFILE_GFX)
        set(PROFILE_GFX_C_FILES ProfileForm.cpp)
        set(PROFILE_GFX_H_FILES ProfileForm.h)
    endif ()

    if (DEBUG_MODE_ENCODER)
        add_compile_definitions(DEBUG_MODE_ENCODER)
    endif ()

    if (DEBUG_MODE_LARGE_FONTS)
        add_compile_definitions(GFX_LARGE_FONT)
    endif ()

    add_compile_definitions(DEBUG_MODE)
    if (DEBUG_MODE_PRESCALER)
        add_compile_definitions(DEBUG_MODE_PRESCALER)
        set(ARDUINO_CPU 8MHzatmega328)
    else ()
        set(ARDUINO_CPU 16MHzatmega328)
    endif ()
else ()
    if (LARGE_FONTS)
        add_compile_definitions(GFX_LARGE_FONT)
    endif ()

    set(ARDUINO_CPU 8MHzatmega328)
endif ()

set(${PROJECT_NAME}_BOARD pro)
project(${PROJECT_NAME})

if (UART_INT)
    add_compile_definitions(SSD1306_UART_INT)
    set(UART_C_FILES drivers/uartint.c drivers/ring.c)
    set(UART_H_FILES drivers/uartint.h drivers/ring.h)
else ()
    set(UART_C_FILES drivers/uart.c)
    set(UART_H_FILES drivers/uart.h)
endif ()

# Define additional source and header files or default arduino sketch files
set(${PROJECT_NAME}_SRCS
    CookiePortioner.cpp
    drivers/interrupt_handlers.c
    EventTranslator.cpp
    MainScreen.cpp
    ${PROFILE_GFX_C_FILES}
    tasks/AdcHandler.cpp
    tasks/MotorControl.cpp
    tasks/PortionControl.cpp
    tasks/SoundHandler.cpp
    util/HistoryBuffer.cpp
    util/non_volatile_data.c
    util/power_down.c
    util/Strings.c
    util/TimeIt.cpp
    util/TimeIt.h
    ${UART_C_FILES}
    )
set(${PROJECT_NAME}_HDRS
    config.h
    drivers/interrupt_handlers.h
    EventTranslator.cpp
    InputEvents.h
    MainScreen.h
    ${PROFILE_GFX_H_FILES}
    pin_config.h
    tasks/AdcHandler.h
    tasks/MotorControl.h
    tasks/PortionControl.h
    tasks/SoundHandler.h
    type_defs.h
    util/HistoryBuffer.h
    util/non_volatile_data.h
    util/power_down.h
    util/Strings.h
    ${UART_H_FILES}
    )

### Additional static libraries to include in the target.
# set(${PROJECT_NAME}_LIBS)

.
.
CMakeFile[0, 2962]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 3.0.0"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "3.0.0"]
  Command[37, 115] spaces:[37, 38, "\n"] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 114, "CMAKE_TOOLCHAIN_FILE /usr/local/etc/arduino-cmake/ArduinoToolchain.cmake"] close:[114, 115, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 114] text:[63, 114, "/usr/local/etc/arduino-cmake/ArduinoToolchain.cmake"]
  Command[115, 142] spaces:[115, 116, "\n"] text:[116, 119, "set"] open:[119, 120, "("] arguments:[120, 141, "CMAKE_CXX_STANDARD 98"] close:[141, 142, ")"]
    Argument[120, 138] text:[120, 138, "CMAKE_CXX_STANDARD"]
    Argument[139, 141] text:[139, 141, "98"]
  Command[142, 176] spaces:[142, 143, "\n"] text:[143, 146, "set"] open:[146, 147, "("] arguments:[147, 175, "PROJECT_NAME CookiePortioner"] close:[175, 176, ")"]
    Argument[147, 159] text:[147, 159, "PROJECT_NAME"]
    Argument[160, 175] text:[160, 175, "CookiePortioner"]
  BlankLine[177, 178]
  LineComment[178, 224] open:[178, 179, "#"] text:[179, 224, " use UART with interrupts if set to non-zero\n"]
  Command[224, 239] text:[224, 227, "set"] open:[227, 228, "("] arguments:[228, 238, "UART_INT 0"] close:[238, 239, ")"]
    Argument[228, 236] text:[228, 236, "UART_INT"]
    Argument[237, 238] text:[237, 238, "0"]
  LineComment[240, 280] open:[240, 241, "#"] text:[241, 280, " use DEBUG_MODE dev version of options\n"]
  Command[280, 297] text:[280, 283, "set"] open:[283, 284, "("] arguments:[284, 296, "DEBUG_MODE 1"] close:[296, 297, ")"]
    Argument[284, 294] text:[284, 294, "DEBUG_MODE"]
    Argument[295, 296] text:[295, 296, "1"]
  Command[297, 325] spaces:[297, 298, "\n"] text:[298, 301, "set"] open:[301, 302, "("] arguments:[302, 324, "DEBUG_MODE_PRESCALER 1"] close:[324, 325, ")"]
    Argument[302, 322] text:[302, 322, "DEBUG_MODE_PRESCALER"]
    Argument[323, 324] text:[323, 324, "1"]
  Command[325, 351] spaces:[325, 326, "\n"] text:[326, 329, "set"] open:[329, 330, "("] arguments:[330, 350, "DEBUG_MODE_ENCODER 1"] close:[350, 351, ")"]
    Argument[330, 348] text:[330, 348, "DEBUG_MODE_ENCODER"]
    Argument[349, 350] text:[349, 350, "1"]
  Command[351, 381] spaces:[351, 352, "\n"] text:[352, 355, "set"] open:[355, 356, "("] arguments:[356, 380, "DEBUG_MODE_LARGE_FONTS 0"] close:[380, 381, ")"]
    Argument[356, 378] text:[356, 378, "DEBUG_MODE_LARGE_FONTS"]
    Argument[379, 380] text:[379, 380, "0"]
  Command[381, 401] spaces:[381, 382, "\n"] text:[382, 385, "set"] open:[385, 386, "("] arguments:[386, 400, "GFX_BIT_BLIT 0"] close:[400, 401, ")"]
    Argument[386, 398] text:[386, 398, "GFX_BIT_BLIT"]
    Argument[399, 400] text:[399, 400, "0"]
  Command[401, 420] spaces:[401, 402, "\n"] text:[402, 405, "set"] open:[405, 406, "("] arguments:[406, 419, "PROFILE_GFX 1"] close:[419, 420, ")"]
    Argument[406, 417] text:[406, 417, "PROFILE_GFX"]
    Argument[418, 419] text:[418, 419, "1"]
  Command[420, 439] spaces:[420, 421, "\n"] text:[421, 424, "set"] open:[424, 425, "("] arguments:[425, 438, "LARGE_FONTS 1"] close:[438, 439, ")"]
    Argument[425, 436] text:[425, 436, "LARGE_FONTS"]
    Argument[437, 438] text:[437, 438, "1"]
  BlankLine[440, 441]
  LineComment[441, 484] open:[441, 442, "#"] text:[442, 484, " SSD1306 definition to use TWI interrupts\n"]
  Command[484, 524] text:[484, 507, "add_compile_definitions"] open:[507, 508, "("] arguments:[508, 523, "INCLUDE_TWI_INT"] close:[523, 524, ")"]
    Argument[508, 523] text:[508, 523, "INCLUDE_TWI_INT"]
  Command[524, 570] spaces:[524, 525, "\n"] text:[525, 548, "add_compile_definitions"] open:[548, 549, "("] arguments:[549, 569, "GFX_NO_LINE_PATTERNS"] close:[569, 570, ")"]
    Argument[549, 569] text:[549, 569, "GFX_NO_LINE_PATTERNS"]
  LineComment[571, 612] open:[571, 572, "#"] text:[572, 612, "add_compile_definitions(GFX_SMALL_FONT)\n"]
  BlankLine[612, 613]
  Command[613, 658] text:[613, 629, "add_link_options"] open:[629, 630, "("] arguments:[630, 657, "-Xlinker -Map=../output.map"] close:[657, 658, ")"]
    Argument[630, 638] text:[630, 638, "-Xlinker"]
    Argument[639, 657] text:[639, 657, "-Map=../output.map"]
  BlankLine[659, 660]
  Command[660, 675] text:[660, 662, "if"] open:[663, 664, "("] arguments:[664, 674, "DEBUG_MODE"] close:[674, 675, ")"]
    Argument[664, 674] text:[664, 674, "DEBUG_MODE"]
  LineComment[676, 748] spaces:[676, 680, "    "] open:[676, 677, " "] text:[677, 748, "   # SSD1306 subs large font for double size digits, `m`, `l`, and `.`\n"]
  Command[748, 765] spaces:[748, 752, "    "] text:[752, 754, "if"] open:[755, 756, "("] arguments:[756, 764, "DEV_MODE"] close:[764, 765, ")"]
    Argument[756, 764] text:[756, 764, "DEV_MODE"]
  Command[765, 807] spaces:[765, 774, "\n        "] text:[774, 797, "add_compile_definitions"] open:[797, 798, "("] arguments:[798, 806, "DEV_MODE"] close:[806, 807, ")"]
    Argument[798, 806] text:[798, 806, "DEV_MODE"]
  Command[807, 820] spaces:[807, 812, "\n    "] text:[812, 817, "endif"] open:[818, 819, "("] arguments:[819, 819] close:[819, 820, ")"]
  BlankLine[821, 822]
  Command[822, 843] spaces:[822, 826, "    "] text:[826, 828, "if"] open:[829, 830, "("] arguments:[830, 842, "GFX_BIT_BLIT"] close:[842, 843, ")"]
    Argument[830, 842] text:[830, 842, "GFX_BIT_BLIT"]
  Command[843, 855] spaces:[843, 848, "\n    "] text:[848, 852, "else"] open:[853, 854, "("] arguments:[854, 854] close:[854, 855, ")"]
  Command[855, 904] spaces:[855, 864, "\n        "] text:[864, 887, "add_compile_definitions"] open:[887, 888, "("] arguments:[888, 903, "GFX_NO_BIT_BLIT"] close:[903, 904, ")"]
    Argument[888, 903] text:[888, 903, "GFX_NO_BIT_BLIT"]
  Command[904, 917] spaces:[904, 909, "\n    "] text:[909, 914, "endif"] open:[915, 916, "("] arguments:[916, 916] close:[916, 917, ")"]
  BlankLine[918, 919]
  Command[919, 939] spaces:[919, 923, "    "] text:[923, 925, "if"] open:[926, 927, "("] arguments:[927, 938, "PROFILE_GFX"] close:[938, 939, ")"]
    Argument[927, 938] text:[927, 938, "PROFILE_GFX"]
  Command[939, 984] spaces:[939, 948, "\n        "] text:[948, 971, "add_compile_definitions"] open:[971, 972, "("] arguments:[972, 983, "PROFILE_GFX"] close:[983, 984, ")"]
    Argument[972, 983] text:[972, 983, "PROFILE_GFX"]
  Command[984, 1033] spaces:[984, 993, "\n        "] text:[993, 996, "set"] open:[996, 997, "("] arguments:[997, 1032, "PROFILE_GFX_C_FILES ProfileForm.cpp"] close:[1032, 1033, ")"]
    Argument[997, 1016] text:[997, 1016, "PROFILE_GFX_C_FILES"]
    Argument[1017, 1032] text:[1017, 1032, "ProfileForm.cpp"]
  Command[1033, 1080] spaces:[1033, 1042, "\n        "] text:[1042, 1045, "set"] open:[1045, 1046, "("] arguments:[1046, 1079, "PROFILE_GFX_H_FILES ProfileForm.h"] close:[1079, 1080, ")"]
    Argument[1046, 1065] text:[1046, 1065, "PROFILE_GFX_H_FILES"]
    Argument[1066, 1079] text:[1066, 1079, "ProfileForm.h"]
  Command[1080, 1093] spaces:[1080, 1085, "\n    "] text:[1085, 1090, "endif"] open:[1091, 1092, "("] arguments:[1092, 1092] close:[1092, 1093, ")"]
  BlankLine[1094, 1095]
  Command[1095, 1122] spaces:[1095, 1099, "    "] text:[1099, 1101, "if"] open:[1102, 1103, "("] arguments:[1103, 1121, "DEBUG_MODE_ENCODER"] close:[1121, 1122, ")"]
    Argument[1103, 1121] text:[1103, 1121, "DEBUG_MODE_ENCODER"]
  Command[1122, 1174] spaces:[1122, 1131, "\n        "] text:[1131, 1154, "add_compile_definitions"] open:[1154, 1155, "("] arguments:[1155, 1173, "DEBUG_MODE_ENCODER"] close:[1173, 1174, ")"]
    Argument[1155, 1173] text:[1155, 1173, "DEBUG_MODE_ENCODER"]
  Command[1174, 1187] spaces:[1174, 1179, "\n    "] text:[1179, 1184, "endif"] open:[1185, 1186, "("] arguments:[1186, 1186] close:[1186, 1187, ")"]
  BlankLine[1188, 1189]
  Command[1189, 1220] spaces:[1189, 1193, "    "] text:[1193, 1195, "if"] open:[1196, 1197, "("] arguments:[1197, 1219, "DEBUG_MODE_LARGE_FONTS"] close:[1219, 1220, ")"]
    Argument[1197, 1219] text:[1197, 1219, "DEBUG_MODE_LARGE_FONTS"]
  Command[1220, 1268] spaces:[1220, 1229, "\n        "] text:[1229, 1252, "add_compile_definitions"] open:[1252, 1253, "("] arguments:[1253, 1267, "GFX_LARGE_FONT"] close:[1267, 1268, ")"]
    Argument[1253, 1267] text:[1253, 1267, "GFX_LARGE_FONT"]
  Command[1268, 1281] spaces:[1268, 1273, "\n    "] text:[1273, 1278, "endif"] open:[1279, 1280, "("] arguments:[1280, 1280] close:[1280, 1281, ")"]
  BlankLine[1282, 1283]
  Command[1283, 1322] spaces:[1283, 1287, "    "] text:[1287, 1310, "add_compile_definitions"] open:[1310, 1311, "("] arguments:[1311, 1321, "DEBUG_MODE"] close:[1321, 1322, ")"]
    Argument[1311, 1321] text:[1311, 1321, "DEBUG_MODE"]
  Command[1322, 1352] spaces:[1322, 1327, "\n    "] text:[1327, 1329, "if"] open:[1330, 1331, "("] arguments:[1331, 1351, "DEBUG_MODE_PRESCALER"] close:[1351, 1352, ")"]
    Argument[1331, 1351] text:[1331, 1351, "DEBUG_MODE_PRESCALER"]
  Command[1352, 1406] spaces:[1352, 1361, "\n        "] text:[1361, 1384, "add_compile_definitions"] open:[1384, 1385, "("] arguments:[1385, 1405, "DEBUG_MODE_PRESCALER"] close:[1405, 1406, ")"]
    Argument[1385, 1405] text:[1385, 1405, "DEBUG_MODE_PRESCALER"]
  Command[1406, 1445] spaces:[1406, 1415, "\n        "] text:[1415, 1418, "set"] open:[1418, 1419, "("] arguments:[1419, 1444, "ARDUINO_CPU 8MHzatmega328"] close:[1444, 1445, ")"]
    Argument[1419, 1430] text:[1419, 1430, "ARDUINO_CPU"]
    Argument[1431, 1444] text:[1431, 1444, "8MHzatmega328"]
  Command[1445, 1457] spaces:[1445, 1450, "\n    "] text:[1450, 1454, "else"] open:[1455, 1456, "("] arguments:[1456, 1456] close:[1456, 1457, ")"]
  Command[1457, 1497] spaces:[1457, 1466, "\n        "] text:[1466, 1469, "set"] open:[1469, 1470, "("] arguments:[1470, 1496, "ARDUINO_CPU 16MHzatmega328"] close:[1496, 1497, ")"]
    Argument[1470, 1481] text:[1470, 1481, "ARDUINO_CPU"]
    Argument[1482, 1496] text:[1482, 1496, "16MHzatmega328"]
  Command[1497, 1510] spaces:[1497, 1502, "\n    "] text:[1502, 1507, "endif"] open:[1508, 1509, "("] arguments:[1509, 1509] close:[1509, 1510, ")"]
  Command[1510, 1518] spaces:[1510, 1511, "\n"] text:[1511, 1515, "else"] open:[1516, 1517, "("] arguments:[1517, 1517] close:[1517, 1518, ")"]
  Command[1518, 1539] spaces:[1518, 1523, "\n    "] text:[1523, 1525, "if"] open:[1526, 1527, "("] arguments:[1527, 1538, "LARGE_FONTS"] close:[1538, 1539, ")"]
    Argument[1527, 1538] text:[1527, 1538, "LARGE_FONTS"]
  Command[1539, 1587] spaces:[1539, 1548, "\n        "] text:[1548, 1571, "add_compile_definitions"] open:[1571, 1572, "("] arguments:[1572, 1586, "GFX_LARGE_FONT"] close:[1586, 1587, ")"]
    Argument[1572, 1586] text:[1572, 1586, "GFX_LARGE_FONT"]
  Command[1587, 1600] spaces:[1587, 1592, "\n    "] text:[1592, 1597, "endif"] open:[1598, 1599, "("] arguments:[1599, 1599] close:[1599, 1600, ")"]
  BlankLine[1601, 1602]
  Command[1602, 1636] spaces:[1602, 1606, "    "] text:[1606, 1609, "set"] open:[1609, 1610, "("] arguments:[1610, 1635, "ARDUINO_CPU 8MHzatmega328"] close:[1635, 1636, ")"]
    Argument[1610, 1621] text:[1610, 1621, "ARDUINO_CPU"]
    Argument[1622, 1635] text:[1622, 1635, "8MHzatmega328"]
  Command[1636, 1645] spaces:[1636, 1637, "\n"] text:[1637, 1642, "endif"] open:[1643, 1644, "("] arguments:[1644, 1644] close:[1644, 1645, ")"]
  BlankLine[1646, 1647]
  Command[1647, 1677] text:[1647, 1650, "set"] open:[1650, 1651, "("] arguments:[1651, 1676, "${PROJECT_NAME}_BOARD pro"] close:[1676, 1677, ")"]
    Argument[1651, 1672] text:[1651, 1672, "${PROJECT_NAME}_BOARD"]
    Argument[1673, 1676] text:[1673, 1676, "pro"]
  Command[1677, 1702] spaces:[1677, 1678, "\n"] text:[1678, 1685, "project"] open:[1685, 1686, "("] arguments:[1686, 1701, "${PROJECT_NAME}"] close:[1701, 1702, ")"]
    Argument[1686, 1701] text:[1686, 1701, "${PROJECT_NAME}"]
  BlankLine[1703, 1704]
  Command[1704, 1717] text:[1704, 1706, "if"] open:[1707, 1708, "("] arguments:[1708, 1716, "UART_INT"] close:[1716, 1717, ")"]
    Argument[1708, 1716] text:[1708, 1716, "UART_INT"]
  Command[1717, 1763] spaces:[1717, 1722, "\n    "] text:[1722, 1745, "add_compile_definitions"] open:[1745, 1746, "("] arguments:[1746, 1762, "SSD1306_UART_INT"] close:[1762, 1763, ")"]
    Argument[1746, 1762] text:[1746, 1762, "SSD1306_UART_INT"]
  Command[1763, 1818] spaces:[1763, 1768, "\n    "] text:[1768, 1771, "set"] open:[1771, 1772, "("] arguments:[1772, 1817, "UART_C_FILES drivers/uartint.c drivers/ring.c"] close:[1817, 1818, ")"]
    Argument[1772, 1784] text:[1772, 1784, "UART_C_FILES"]
    Argument[1785, 1802] text:[1785, 1802, "drivers/uartint.c"]
    Argument[1803, 1817] text:[1803, 1817, "drivers/ring.c"]
  Command[1818, 1873] spaces:[1818, 1823, "\n    "] text:[1823, 1826, "set"] open:[1826, 1827, "("] arguments:[1827, 1872, "UART_H_FILES drivers/uartint.h drivers/ring.h"] close:[1872, 1873, ")"]
    Argument[1827, 1839] text:[1827, 1839, "UART_H_FILES"]
    Argument[1840, 1857] text:[1840, 1857, "drivers/uartint.h"]
    Argument[1858, 1872] text:[1858, 1872, "drivers/ring.h"]
  Command[1873, 1881] spaces:[1873, 1874, "\n"] text:[1874, 1878, "else"] open:[1879, 1880, "("] arguments:[1880, 1880] close:[1880, 1881, ")"]
  Command[1881, 1918] spaces:[1881, 1886, "\n    "] text:[1886, 1889, "set"] open:[1889, 1890, "("] arguments:[1890, 1917, "UART_C_FILES drivers/uart.c"] close:[1917, 1918, ")"]
    Argument[1890, 1902] text:[1890, 1902, "UART_C_FILES"]
    Argument[1903, 1917] text:[1903, 1917, "drivers/uart.c"]
  Command[1918, 1955] spaces:[1918, 1923, "\n    "] text:[1923, 1926, "set"] open:[1926, 1927, "("] arguments:[1927, 1954, "UART_H_FILES drivers/uart.h"] close:[1954, 1955, ")"]
    Argument[1927, 1939] text:[1927, 1939, "UART_H_FILES"]
    Argument[1940, 1954] text:[1940, 1954, "drivers/uart.h"]
  Command[1955, 1964] spaces:[1955, 1956, "\n"] text:[1956, 1961, "endif"] open:[1962, 1963, "("] arguments:[1963, 1963] close:[1963, 1964, ")"]
  BlankLine[1965, 1966]
  LineComment[1966, 2042] open:[1966, 1967, "#"] text:[1967, 2042, " Define additional source and header files or default arduino sketch files\n"]
  Command[2042, 2462] text:[2042, 2045, "set"] open:[2045, 2046, "("] arguments:[2046, 2461, "${PROJECT_NAME}_SRCS\n    CookiePortioner.cpp\n    drivers/interrupt_handlers.c\n    EventTranslator.cpp\n    MainScreen.cpp\n    ${PROFILE_GFX_C_FILES}\n    tasks/AdcHandler.cpp\n    tasks/MotorControl.cpp\n    tasks/PortionControl.cpp\n    tasks/SoundHandler.cpp\n    util/HistoryBuffer.cpp\n    util/non_volatile_data.c\n    util/power_down.c\n    util/Strings.c\n    util/TimeIt.cpp\n    util/TimeIt.h\n    ${UART_C_FILES}\n    "] close:[2461, 2462, ")"]
    Argument[2046, 2066] text:[2046, 2066, "${PROJECT_NAME}_SRCS"]
    Argument[2071, 2090] text:[2071, 2090, "CookiePortioner.cpp"]
    Argument[2095, 2123] text:[2095, 2123, "drivers/interrupt_handlers.c"]
    Argument[2128, 2147] text:[2128, 2147, "EventTranslator.cpp"]
    Argument[2152, 2166] text:[2152, 2166, "MainScreen.cpp"]
    Argument[2171, 2193] text:[2171, 2193, "${PROFILE_GFX_C_FILES}"]
    Argument[2198, 2218] text:[2198, 2218, "tasks/AdcHandler.cpp"]
    Argument[2223, 2245] text:[2223, 2245, "tasks/MotorControl.cpp"]
    Argument[2250, 2274] text:[2250, 2274, "tasks/PortionControl.cpp"]
    Argument[2279, 2301] text:[2279, 2301, "tasks/SoundHandler.cpp"]
    Argument[2306, 2328] text:[2306, 2328, "util/HistoryBuffer.cpp"]
    Argument[2333, 2357] text:[2333, 2357, "util/non_volatile_data.c"]
    Argument[2362, 2379] text:[2362, 2379, "util/power_down.c"]
    Argument[2384, 2398] text:[2384, 2398, "util/Strings.c"]
    Argument[2403, 2418] text:[2403, 2418, "util/TimeIt.cpp"]
    Argument[2423, 2436] text:[2423, 2436, "util/TimeIt.h"]
    Argument[2441, 2456] text:[2441, 2456, "${UART_C_FILES}"]
  Command[2462, 2873] spaces:[2462, 2463, "\n"] text:[2463, 2466, "set"] open:[2466, 2467, "("] arguments:[2467, 2872, "${PROJECT_NAME}_HDRS\n    config.h\n    drivers/interrupt_handlers.h\n    EventTranslator.cpp\n    InputEvents.h\n    MainScreen.h\n    ${PROFILE_GFX_H_FILES}\n    pin_config.h\n    tasks/AdcHandler.h\n    tasks/MotorControl.h\n    tasks/PortionControl.h\n    tasks/SoundHandler.h\n    type_defs.h\n    util/HistoryBuffer.h\n    util/non_volatile_data.h\n    util/power_down.h\n    util/Strings.h\n    ${UART_H_FILES}\n    "] close:[2872, 2873, ")"]
    Argument[2467, 2487] text:[2467, 2487, "${PROJECT_NAME}_HDRS"]
    Argument[2492, 2500] text:[2492, 2500, "config.h"]
    Argument[2505, 2533] text:[2505, 2533, "drivers/interrupt_handlers.h"]
    Argument[2538, 2557] text:[2538, 2557, "EventTranslator.cpp"]
    Argument[2562, 2575] text:[2562, 2575, "InputEvents.h"]
    Argument[2580, 2592] text:[2580, 2592, "MainScreen.h"]
    Argument[2597, 2619] text:[2597, 2619, "${PROFILE_GFX_H_FILES}"]
    Argument[2624, 2636] text:[2624, 2636, "pin_config.h"]
    Argument[2641, 2659] text:[2641, 2659, "tasks/AdcHandler.h"]
    Argument[2664, 2684] text:[2664, 2684, "tasks/MotorControl.h"]
    Argument[2689, 2711] text:[2689, 2711, "tasks/PortionControl.h"]
    Argument[2716, 2736] text:[2716, 2736, "tasks/SoundHandler.h"]
    Argument[2741, 2752] text:[2741, 2752, "type_defs.h"]
    Argument[2757, 2777] text:[2757, 2777, "util/HistoryBuffer.h"]
    Argument[2782, 2806] text:[2782, 2806, "util/non_volatile_data.h"]
    Argument[2811, 2828] text:[2811, 2828, "util/power_down.h"]
    Argument[2833, 2847] text:[2833, 2847, "util/Strings.h"]
    Argument[2852, 2867] text:[2852, 2867, "${UART_H_FILES}"]
  BlankLine[2874, 2875]
  LineComment[2875, 2933] open:[2875, 2876, "#"] text:[2876, 2933, "## Additional static libraries to include in the target.\n"]
  LineComment[2933, 2961] open:[2933, 2934, "#"] text:[2934, 2961, " set(${PROJECT_NAME}_LIBS)\n"]
  BlankLine[2961, 2962]
````````````````````````````````



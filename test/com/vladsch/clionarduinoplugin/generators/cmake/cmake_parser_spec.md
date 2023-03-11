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
  Command[111, 128] text:[111, 118, "message"] open:[118, 119, "("] arguments:[119, 127, "\"${arg}\""] close:[127, 128, ")"]
    Argument[119, 127] open:[119, 120, "\""] text:[120, 126, "${arg}"] close:[126, 127, "\""]
  Command[129, 141] text:[129, 139, "endforeach"] open:[139, 140, "("] arguments:[140, 140] close:[140, 141, ")"]
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
  Command[116, 133] text:[116, 123, "message"] open:[123, 124, "("] arguments:[124, 132, "\"${arg}\""] close:[132, 133, ")"]
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
  Command[130, 147] text:[130, 137, "message"] open:[137, 138, "("] arguments:[138, 146, "\"${arg}\""] close:[146, 147, ")"]
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
  Command[111, 128] text:[111, 118, "message"] open:[118, 119, "("] arguments:[119, 127, "\"${arg}\""] close:[127, 128, ")"]
    Argument[119, 127] open:[119, 120, "\""] text:[120, 126, "${arg}"] close:[126, 127, "\""]
  Command[129, 141] text:[129, 139, "endforeach"] open:[139, 140, "("] arguments:[140, 140] close:[140, 141, ")"]
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
  Command[127, 144] text:[127, 134, "message"] open:[134, 135, "("] arguments:[135, 143, "\"${arg}\""] close:[143, 144, ")"]
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
  Command[15, 63] text:[15, 31, "link_directories"] open:[31, 32, "("] arguments:[32, 62, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[62, 63, ")"]
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
  Command[57, 111] text:[57, 60, "set"] open:[60, 61, "("] arguments:[61, 110, "${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii"] close:[110, 111, ")"]
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
  Command[57, 111] text:[57, 60, "set"] open:[60, 61, "("] arguments:[61, 110, "${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii"] close:[110, 111, ")"]
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
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "PROJECT_NAME tft_life"] close:[140, 141, ")"]
    Argument[119, 131] text:[119, 131, "PROJECT_NAME"]
    Argument[132, 140] text:[132, 140, "tft_life"]
  Command[183, 219] text:[183, 186, "set"] open:[186, 187, "("] arguments:[187, 218, "${CMAKE_PROJECT_NAME}_BOARD pro"] close:[218, 219, ")"]
    Argument[187, 214] text:[187, 214, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[215, 218] text:[215, 218, "pro"]
  Command[220, 250] text:[220, 223, "set"] open:[223, 224, "("] arguments:[224, 249, "ARDUINO_CPU 8MHzatmega328"] close:[249, 250, ")"]
    Argument[224, 235] text:[224, 235, "ARDUINO_CPU"]
    Argument[236, 249] text:[236, 249, "8MHzatmega328"]
  Command[252, 276] text:[252, 259, "project"] open:[259, 260, "("] arguments:[260, 275, "${PROJECT_NAME}"] close:[275, 276, ")"]
    Argument[260, 275] text:[260, 275, "${PROJECT_NAME}"]
  Command[303, 341] text:[303, 306, "set"] open:[306, 307, "("] arguments:[307, 340, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[340, 341, ")"]
    Argument[307, 327] text:[307, 327, "${PROJECT_NAME}_SRCS"]
    Argument[328, 340] text:[328, 340, "tft_life.cpp"]
  Command[390, 438] text:[390, 406, "link_directories"] open:[406, 407, "("] arguments:[407, 437, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[437, 438, ")"]
    Argument[407, 437] text:[407, 437, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  Command[492, 540] text:[492, 495, "set"] open:[495, 496, "("] arguments:[496, 539, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[539, 540, ")"]
    Argument[496, 528] text:[496, 528, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[529, 539] text:[529, 539, "avrispmkii"]
  Command[541, 599] text:[541, 544, "set"] open:[544, 545, "("] arguments:[545, 598, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[598, 599, ")"]
    Argument[545, 571] text:[545, 571, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[572, 598] text:[572, 598, "/dev/cu.usbserial-00000000"]
  Command[600, 637] text:[600, 603, "set"] open:[604, 605, "("] arguments:[605, 636, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[636, 637, ")"]
    Argument[605, 633] text:[605, 633, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[634, 636] text:[634, 636, "-v"]
  Command[669, 717] text:[669, 694, "generate_arduino_firmware"] open:[694, 695, "("] arguments:[695, 716, "${CMAKE_PROJECT_NAME}"] close:[716, 717, ")"]
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
  Command[394, 442] text:[394, 410, "link_directories"] open:[410, 411, "("] arguments:[411, 441, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[441, 442, ")"]
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



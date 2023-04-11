---
title: CMake Formatter Spec Test
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
# comment
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
#[[ comment
# inner comment
]]

# line comment
.
CMakeFile[0, 46]
  BracketComment[0, 30] open:[0, 3, "#[["] text:[3, 28, " comment\n# inner comment\n"] close:[28, 30, "]]"]
  LineEnding[30, 31]
  BlankLine[31, 32]
  LineComment[32, 46] open:[32, 33, "#"] text:[33, 46, " line comment"]
````````````````````````````````


in-line bracket comments

```````````````````````````````` example(Comments: 3) options(ast-comments,  bracket-comments)
#[[ comment # inner comment ]] # line comment

# line comment
.
#[[ comment # inner comment ]] # line comment

# line comment
.
CMakeFile[0, 61]
  BracketComment[0, 30] open:[0, 3, "#[["] text:[3, 28, " comment # inner comment "] close:[28, 30, "]]"]
  LineComment[30, 46] spaces:[30, 31, " "] open:[30, 31, " "] text:[31, 46, "# line comment\n"]
  BlankLine[46, 47]
  LineComment[47, 61] open:[47, 48, "#"] text:[48, 61, " line comment"]
````````````````````````````````


bracket comments disabled

```````````````````````````````` example(Comments: 4) options(ast-comments)
#[[ comment
# inner comment
]]

# line comment
.
#[[ comment
# inner comment
]]

# line comment
.
CMakeFile[0, 46]
  LineComment[0, 12] open:[0, 1, "#"] text:[1, 12, "[[ comment\n"]
  LineComment[12, 28] open:[12, 13, "#"] text:[13, 28, " inner comment\n"]
  UnrecognizedInput[28, 31]
  BlankLine[31, 32]
  LineComment[32, 46] open:[32, 33, "#"] text:[33, 46, " line comment"]
````````````````````````````````


not a bracket comment

```````````````````````````````` example(Comments: 5) options(ast-comments,  bracket-comments)
# [[ comment
# inner comment
]]

# line comment
.
# [[ comment
# inner comment
]]

# line comment
.
CMakeFile[0, 47]
  LineComment[0, 13] open:[0, 1, "#"] text:[1, 13, " [[ comment\n"]
  LineComment[13, 29] open:[13, 14, "#"] text:[14, 29, " inner comment\n"]
  UnrecognizedInput[29, 32]
  BlankLine[32, 33]
  LineComment[33, 47] open:[33, 34, "#"] text:[34, 47, " line comment"]
````````````````````````````````


## AST

default no eol

```````````````````````````````` example(AST: 1) options(ast-comments)
if ()

# line comment
.
if ()

# line comment
.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[5, 6]
  BlankLine[6, 7]
  LineComment[7, 21] open:[7, 8, "#"] text:[8, 21, " line comment"]
````````````````````````````````


with eol

```````````````````````````````` example(AST: 2) options(ast-comments,  ast-line-eol)
if ()

# line comment

.
if ()

# line comment

.
CMakeFile[0, 23]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[5, 6]
  BlankLine[6, 7]
  LineComment[7, 22] open:[7, 8, "#"] text:[8, 22, " line comment\n"]
  BlankLine[22, 23]
````````````````````````````````


no blanks

```````````````````````````````` example(AST: 3) options(ast-comments,  ast-line-eol)
if ()

# line comment
.
if ()

# line comment
.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[5, 6]
  BlankLine[6, 7]
  LineComment[7, 21] open:[7, 8, "#"] text:[8, 21, " line comment"]
````````````````````````````````


blanks

```````````````````````````````` example(AST: 4) options(ast-comments,  ast-blank)
if ()

# line comment
.
if ()

# line comment
.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[5, 6]
  BlankLine[6, 7]
  LineComment[7, 21] open:[7, 8, "#"] text:[8, 21, " line comment"]
````````````````````````````````


with blanks, with eol, with comments

```````````````````````````````` example(AST: 5) options(ast-comments,  ast-line-eol,  ast-blank)
if ()

# line comment
.
if ()

# line comment
.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[5, 6]
  BlankLine[6, 7]
  LineComment[7, 21] open:[7, 8, "#"] text:[8, 21, " line comment"]
````````````````````````````````


no blanks, no eol, no comments

```````````````````````````````` example AST: 6
if ()

# line comment
.
if ()

.
CMakeFile[0, 21]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
  LineEnding[5, 6]
  BlankLine[6, 7]
````````````````````````````````


no blanks, no eol, no comments, empty file

```````````````````````````````` example AST: 7
# line comment

# line comment
.
.
CMakeFile[0, 30]
  LineEnding[14, 15]
  BlankLine[15, 16]
````````````````````````````````


no blanks, no eol, no comments

```````````````````````````````` example(AST: 8) options(bracket-comments)
# line comment

#[[ gone ]]

# line comment
.
.
CMakeFile[0, 43]
  LineEnding[14, 15]
  BlankLine[15, 16]
  LineEnding[27, 28]
  BlankLine[28, 29]
````````````````````````````````


## Commands

no args

```````````````````````````````` example Commands: 1
if()
.
if()
.
CMakeFile[0, 4]
  Command[0, 4] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 3] close:[3, 4, ")"]
````````````````````````````````


```````````````````````````````` example Commands: 2
if ()
.
if ()
.
CMakeFile[0, 5]
  Command[0, 5] text:[0, 2, "if"] open:[3, 4, "("] arguments:[4, 4] close:[4, 5, ")"]
````````````````````````````````


one arg

```````````````````````````````` example Commands: 3
if(arg)
.
if(arg)
.
CMakeFile[0, 7]
  Command[0, 7] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 6, "arg"] close:[6, 7, ")"]
    Argument[3, 6] text:[3, 6, "arg"]
````````````````````````````````


one arg

```````````````````````````````` example Commands: 4
if( arg)
.
if(arg)
.
CMakeFile[0, 8]
  Command[0, 8] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 7, " arg"] close:[7, 8, ")"]
    Argument[4, 7] text:[4, 7, "arg"]
````````````````````````````````


one arg

```````````````````````````````` example Commands: 5
if(arg )
.
if(arg )
.
CMakeFile[0, 8]
  Command[0, 8] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 7, "arg "] close:[7, 8, ")"]
    Argument[3, 6] text:[3, 6, "arg"]
````````````````````````````````


one arg

```````````````````````````````` example Commands: 6
if( arg )
.
if(arg )
.
CMakeFile[0, 9]
  Command[0, 9] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 8, " arg "] close:[8, 9, ")"]
    Argument[4, 7] text:[4, 7, "arg"]
````````````````````````````````


one ref arg

```````````````````````````````` example Commands: 7
if(${ref})
.
if(${ref})
.
CMakeFile[0, 10]
  Command[0, 10] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 9, "${ref}"] close:[9, 10, ")"]
    Argument[3, 9] text:[3, 9, "${ref}"]
````````````````````````````````


one ref arg

```````````````````````````````` example Commands: 8
if(${ref}_complex)
.
if(${ref}_complex)
.
CMakeFile[0, 18]
  Command[0, 18] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 17, "${ref}_complex"] close:[17, 18, ")"]
    Argument[3, 17] text:[3, 17, "${ref}_complex"]
````````````````````````````````


one quoted ref arg

```````````````````````````````` example Commands: 9
if("${ref}_complex")
.
if("${ref}_complex")
.
CMakeFile[0, 20]
  Command[0, 20] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 19, "\"${ref}_complex\""] close:[19, 20, ")"]
    Argument[3, 19] open:[3, 4, "\""] text:[4, 18, "${ref}_complex"] close:[18, 19, "\""]
````````````````````````````````


one bracket ref arg

```````````````````````````````` example Commands: 10
if([[${ref}_complex]])
.
if([[${ref}_complex]])
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
if([[${ref}_complex
more data
]])
.
CMakeFile[0, 33]
  Command[0, 33] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 32, "[[${ref}_complex\nmore data\n]]"] close:[32, 33, ")"]
    Argument[3, 32] open:[3, 5, "[["] text:[5, 30, "${ref}_complex\nmore data\n"] close:[30, 32, "]]"]
````````````````````````````````


one unquoted legacy

```````````````````````````````` example Commands: 12
if(abc" "def)
.
if(abc" "def)
.
CMakeFile[0, 13]
  Command[0, 13] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 12, "abc\" \"def"] close:[12, 13, ")"]
    Argument[3, 12] text:[3, 12, "abc\" \"def"]
````````````````````````````````


one parenthesized legacy

```````````````````````````````` example Commands: 13
if((abc" "def))
.
if((abc" "def))
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
if(abc\;def)
.
CMakeFile[0, 12]
  Command[0, 12] text:[0, 2, "if"] open:[2, 3, "("] arguments:[3, 11, "abc\;def"] close:[11, 12, ")"]
    Argument[3, 11] text:[3, 11, "abc\;def"]
````````````````````````````````


several args

```````````````````````````````` example Commands: 15
if(abc" "def xyz /path/def)
.
if(abc" "def xyz /path/def)
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
foreach(arg
    NoSpace
    Escaped\ Space
    This;Divides;Into;Five;Arguments

    Escaped\;Semicolon
    )
  message("${arg}")

endforeach()

.
CMakeFile[0, 151]
  Command[0, 113] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 112, "arg\n    NoSpace\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    \n    Escaped\;Semicolon\n    "] close:[112, 113, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    LineEnding[11, 12]
    Argument[16, 23] text:[16, 23, "NoSpace"]
    LineEnding[23, 24]
    Argument[28, 42] text:[28, 42, "Escaped\ Space"]
    LineEnding[42, 43]
    Argument[47, 51] text:[47, 51, "This"]
    Separator[51, 52]
    Argument[52, 59] text:[52, 59, "Divides"]
    Separator[59, 60]
    Argument[60, 64] text:[60, 64, "Into"]
    Separator[64, 65]
    Argument[65, 69] text:[65, 69, "Five"]
    Separator[69, 70]
    Argument[70, 79] text:[70, 79, "Arguments"]
    LineEnding[79, 80]
    BlankLine[84, 85]
    Argument[89, 107] text:[89, 107, "Escaped\;Semicolon"]
    LineEnding[107, 108]
  LineEnding[113, 114]
  Command[114, 133] spaces:[114, 116, "  "] text:[116, 123, "message"] open:[123, 124, "("] arguments:[124, 132, "\"${arg}\""] close:[132, 133, ")"]
    Argument[124, 132] open:[124, 125, "\""] text:[125, 131, "${arg}"] close:[131, 132, "\""]
  LineEnding[133, 134]
  BlankLine[136, 137]
  Command[137, 149] text:[137, 147, "endforeach"] open:[147, 148, "("] arguments:[148, 148] close:[148, 149, ")"]
  LineEnding[149, 150]
  BlankLine[150, 151]
````````````````````````````````


several args with blanks

```````````````````````````````` example(Commands: 17) options(ast-blank)
foreach(arg
    NoSpace  # comment
    Escaped\ Space
    This;Divides;Into;Five;Arguments
    
    Escaped\;Semicolon
    )
  message("${arg}")
  
endforeach()

.
foreach(arg
    NoSpace
    Escaped\ Space
    This;Divides;Into;Five;Arguments

    Escaped\;Semicolon
    )
  message("${arg}")

endforeach()

.
CMakeFile[0, 162]
  Command[0, 124] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 123, "arg\n    NoSpace  # comment\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    \n    Escaped\;Semicolon\n    "] close:[123, 124, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    LineEnding[11, 12]
    Argument[16, 23] text:[16, 23, "NoSpace"]
    LineEnding[34, 35]
    Argument[39, 53] text:[39, 53, "Escaped\ Space"]
    LineEnding[53, 54]
    Argument[58, 62] text:[58, 62, "This"]
    Separator[62, 63]
    Argument[63, 70] text:[63, 70, "Divides"]
    Separator[70, 71]
    Argument[71, 75] text:[71, 75, "Into"]
    Separator[75, 76]
    Argument[76, 80] text:[76, 80, "Five"]
    Separator[80, 81]
    Argument[81, 90] text:[81, 90, "Arguments"]
    LineEnding[90, 91]
    BlankLine[95, 96]
    Argument[100, 118] text:[100, 118, "Escaped\;Semicolon"]
    LineEnding[118, 119]
  LineEnding[124, 125]
  Command[125, 144] spaces:[125, 127, "  "] text:[127, 134, "message"] open:[134, 135, "("] arguments:[135, 143, "\"${arg}\""] close:[143, 144, ")"]
    Argument[135, 143] open:[135, 136, "\""] text:[136, 142, "${arg}"] close:[142, 143, "\""]
  LineEnding[144, 145]
  BlankLine[147, 148]
  Command[148, 160] text:[148, 158, "endforeach"] open:[158, 159, "("] arguments:[159, 159] close:[159, 160, ")"]
  LineEnding[160, 161]
  BlankLine[161, 162]
````````````````````````````````


several args with seps, blanks, comments

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
foreach(arg
    NoSpace     # comment
    Escaped\ Space
    This;Divides;Into;Five;Arguments

    Escaped\;Semicolon
    )
  message("${arg}")

endforeach()

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
foreach(arg
    NoSpace
    Escaped\ Space
    This;Divides;Into;Five;Arguments

    Escaped\;Semicolon
    )
  message("${arg}")

endforeach()

.
CMakeFile[0, 151]
  Command[0, 113] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 112, "arg\n    NoSpace\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    \n    Escaped\;Semicolon\n    "] close:[112, 113, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    LineEnding[11, 12]
    Argument[16, 23] text:[16, 23, "NoSpace"]
    LineEnding[23, 24]
    Argument[28, 42] text:[28, 42, "Escaped\ Space"]
    LineEnding[42, 43]
    Argument[47, 51] text:[47, 51, "This"]
    Separator[51, 52]
    Argument[52, 59] text:[52, 59, "Divides"]
    Separator[59, 60]
    Argument[60, 64] text:[60, 64, "Into"]
    Separator[64, 65]
    Argument[65, 69] text:[65, 69, "Five"]
    Separator[69, 70]
    Argument[70, 79] text:[70, 79, "Arguments"]
    LineEnding[79, 80]
    BlankLine[84, 85]
    Argument[89, 107] text:[89, 107, "Escaped\;Semicolon"]
    LineEnding[107, 108]
  LineEnding[113, 114]
  Command[114, 133] spaces:[114, 116, "  "] text:[116, 123, "message"] open:[123, 124, "("] arguments:[124, 132, "\"${arg}\""] close:[132, 133, ")"]
    Argument[124, 132] open:[124, 125, "\""] text:[125, 131, "${arg}"] close:[131, 132, "\""]
  LineEnding[133, 134]
  BlankLine[136, 137]
  Command[137, 149] text:[137, 147, "endforeach"] open:[147, 148, "("] arguments:[148, 148] close:[148, 149, ")"]
  LineEnding[149, 150]
  BlankLine[150, 151]
````````````````````````````````


several args no preserve whitespace

```````````````````````````````` example(Commands: 20) options(no-preserve-whitespace)
foreach(arg
    NoSpace
    Escaped\ Space
    This;Divides;Into;Five;Arguments
    
    Escaped\;Semicolon
    )
  message("${arg}")
  
endforeach()

.
foreach ( arg
    NoSpace
    Escaped\ Space
    This;Divides;Into;Five;Arguments

    Escaped\;Semicolon 
)
message( "${arg}" )

endforeach()

.
CMakeFile[0, 151]
  Command[0, 113] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 112, "arg\n    NoSpace\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    \n    Escaped\;Semicolon\n    "] close:[112, 113, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    LineEnding[11, 12]
    Argument[16, 23] text:[16, 23, "NoSpace"]
    LineEnding[23, 24]
    Argument[28, 42] text:[28, 42, "Escaped\ Space"]
    LineEnding[42, 43]
    Argument[47, 51] text:[47, 51, "This"]
    Separator[51, 52]
    Argument[52, 59] text:[52, 59, "Divides"]
    Separator[59, 60]
    Argument[60, 64] text:[60, 64, "Into"]
    Separator[64, 65]
    Argument[65, 69] text:[65, 69, "Five"]
    Separator[69, 70]
    Argument[70, 79] text:[70, 79, "Arguments"]
    LineEnding[79, 80]
    BlankLine[84, 85]
    Argument[89, 107] text:[89, 107, "Escaped\;Semicolon"]
    LineEnding[107, 108]
  LineEnding[113, 114]
  Command[114, 133] spaces:[114, 116, "  "] text:[116, 123, "message"] open:[123, 124, "("] arguments:[124, 132, "\"${arg}\""] close:[132, 133, ")"]
    Argument[124, 132] open:[124, 125, "\""] text:[125, 131, "${arg}"] close:[131, 132, "\""]
  LineEnding[133, 134]
  BlankLine[136, 137]
  Command[137, 149] text:[137, 147, "endforeach"] open:[147, 148, "("] arguments:[148, 148] close:[148, 149, ")"]
  LineEnding[149, 150]
  BlankLine[150, 151]
````````````````````````````````


several args no preserve whitespace, collapse whitespace

```````````````````````````````` example(Commands: 21) options(no-preserve-whitespace,  collapse-whitespace)
foreach(arg
    NoSpace       # comment
    Escaped\ Space
    This;Divides;Into;Five;Arguments
    
    Escaped\;Semicolon
    )
  message("${arg}")
  
endforeach()

.
foreach ( arg
    NoSpace
    Escaped\ Space
    This;Divides;Into;Five;Arguments

    Escaped\;Semicolon 
)
message( "${arg}" )

endforeach()

.
CMakeFile[0, 167]
  Command[0, 129] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 128, "arg\n    NoSpace       # comment\n    Escaped\ Space\n    This;Divides;Into;Five;Arguments\n    \n    Escaped\;Semicolon\n    "] close:[128, 129, ")"]
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
    BlankLine[100, 101]
    Argument[105, 123] text:[105, 123, "Escaped\;Semicolon"]
    LineEnding[123, 124]
  LineEnding[129, 130]
  Command[130, 149] spaces:[130, 132, "  "] text:[132, 139, "message"] open:[139, 140, "("] arguments:[140, 148, "\"${arg}\""] close:[148, 149, ")"]
    Argument[140, 148] open:[140, 141, "\""] text:[141, 147, "${arg}"] close:[147, 148, "\""]
  LineEnding[149, 150]
  BlankLine[152, 153]
  Command[153, 165] text:[153, 163, "endforeach"] open:[163, 164, "("] arguments:[164, 164] close:[164, 165, ")"]
  LineEnding[165, 166]
  BlankLine[166, 167]
````````````````````````````````


several args no preserve whitespace, seps, line breaks, collapse whitespace

```````````````````````````````` example(Commands: 22) options(no-preserve-whitespace,  no-preserve-breaks,  collapse-whitespace,  no-preserve-seps)
foreach(arg NoSpace Escaped\ Space This;Divides;Into;Five;Arguments Escaped\;Semicolon)
  message("${arg}")
  
endforeach()

.
foreach ( arg NoSpace Escaped\ Space This Divides Into Five Arguments
Escaped\;Semicolon )
message( "${arg}" )
endforeach()
.
CMakeFile[0, 125]
  Command[0, 87] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 86, "arg NoSpace Escaped\ Space This;Divides;Into;Five;Arguments Escaped\;Semicolon"] close:[86, 87, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    Argument[12, 19] text:[12, 19, "NoSpace"]
    Argument[20, 34] text:[20, 34, "Escaped\ Space"]
    Argument[35, 39] text:[35, 39, "This"]
    Separator[39, 40]
    Argument[40, 47] text:[40, 47, "Divides"]
    Separator[47, 48]
    Argument[48, 52] text:[48, 52, "Into"]
    Separator[52, 53]
    Argument[53, 57] text:[53, 57, "Five"]
    Separator[57, 58]
    Argument[58, 67] text:[58, 67, "Arguments"]
    Argument[68, 86] text:[68, 86, "Escaped\;Semicolon"]
  LineEnding[87, 88]
  Command[88, 107] spaces:[88, 90, "  "] text:[90, 97, "message"] open:[97, 98, "("] arguments:[98, 106, "\"${arg}\""] close:[106, 107, ")"]
    Argument[98, 106] open:[98, 99, "\""] text:[99, 105, "${arg}"] close:[105, 106, "\""]
  LineEnding[107, 108]
  BlankLine[110, 111]
  Command[111, 123] text:[111, 121, "endforeach"] open:[121, 122, "("] arguments:[122, 122] close:[122, 123, ")"]
  LineEnding[123, 124]
  BlankLine[124, 125]
````````````````````````````````


several args no preserve whitespace, seps, line breaks, collapse whitespace

break sep on new line

```````````````````````````````` example(Commands: 23) options(no-preserve-whitespace,  no-preserve-breaks,  collapse-whitespace,  no-preserve-seps,  split-seps)
foreach(arg NoSpace Escaped\ Space This;Divides;Into;Five;Arguments Escaped\;Semicolon)
  message("${arg}")
  
endforeach()

.
foreach (
    arg
    NoSpace
    Escaped\ Space
    This
    Divides
    Into
    Five
    Arguments
    Escaped\;Semicolon
)
message(
    "${arg}"
)
endforeach()
.
CMakeFile[0, 125]
  Command[0, 87] text:[0, 7, "foreach"] open:[7, 8, "("] arguments:[8, 86, "arg NoSpace Escaped\ Space This;Divides;Into;Five;Arguments Escaped\;Semicolon"] close:[86, 87, ")"]
    Argument[8, 11] text:[8, 11, "arg"]
    Argument[12, 19] text:[12, 19, "NoSpace"]
    Argument[20, 34] text:[20, 34, "Escaped\ Space"]
    Argument[35, 39] text:[35, 39, "This"]
    Separator[39, 40]
    Argument[40, 47] text:[40, 47, "Divides"]
    Separator[47, 48]
    Argument[48, 52] text:[48, 52, "Into"]
    Separator[52, 53]
    Argument[53, 57] text:[53, 57, "Five"]
    Separator[57, 58]
    Argument[58, 67] text:[58, 67, "Arguments"]
    Argument[68, 86] text:[68, 86, "Escaped\;Semicolon"]
  LineEnding[87, 88]
  Command[88, 107] spaces:[88, 90, "  "] text:[90, 97, "message"] open:[97, 98, "("] arguments:[98, 106, "\"${arg}\""] close:[106, 107, ")"]
    Argument[98, 106] open:[98, 99, "\""] text:[99, 105, "${arg}"] close:[105, 106, "\""]
  LineEnding[107, 108]
  BlankLine[110, 111]
  Command[111, 123] text:[111, 121, "endforeach"] open:[121, 122, "("] arguments:[122, 122] close:[122, 123, ")"]
  LineEnding[123, 124]
  BlankLine[124, 125]
````````````````````````````````


## Commented Out Commands

one escaped semi

```````````````````````````````` example(Commented Out Commands: 1) options(commented-out)
# if(abc\;def)
.
# if(abc\;def)
.
CMakeFile[0, 14]
  CommentedOutCommand[0, 14] comment:[0, 1, "#"] text:[2, 4, "if"] open:[4, 5, "("] arguments:[5, 13, "abc\;def"] close:[13, 14, ")"]
    Argument[5, 13] text:[5, 13, "abc\;def"]
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
message([=[
This is the first line in a bracket argument with bracket length 1.
No \-escape sequences or ${variable} references are evaluated.
This is always one argument even though it contains a ; character.
The text does not end on a closing bracket of length 0 like ]].
It does end in a closing bracket of length 1.
]=])
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
message("This is a quoted argument containing multiple lines.
This is always one argument even though it contains a ; character.
Both \\-escape sequences and ${variable} references are evaluated.
The text does not end on an escaped double-quote like \".
It does end in an unescaped double quote.
")
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
"\
message("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")
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
message("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")
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
cmake_minimum_required(VERSION 2.99.99)
#[[this is a comment]] set(${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii)
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
cmake_minimum_required(VERSION 3)
#[[this is a comment]] set(${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii)
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
cmake_minimum_required(VERSION 2.99.99)
"\
message("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")
.
CMakeFile[0, 192]
  Command[0, 39] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 38, "VERSION 2.99.99"] close:[38, 39, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 38] text:[31, 38, "2.99.99"]
  LineEnding[39, 40]
  UnrecognizedInput[48, 51]
  UnrecognizedInput[40, 51]
  UnrecognizedInput[51, 98]
  UnrecognizedInput[98, 149]
  UnrecognizedInput[149, 190]
  UnrecognizedInput[190, 192]
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
cmake_minimum_required(VERSION 3)
message("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")
````````````````````````````````


Should save options in file node

```````````````````````````````` example(AutoConfig: 5) options(auto-config,  ast-comments,  ast-line-eol,  ast-blank)
cmake_minimum_required(VERSION 3)
#[[this is a comment]] set(${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii)
.
cmake_minimum_required(VERSION 3)
#[[this is a comment]] set(${CMAKE_PROJECT_NAME}_PROGRAMMER #[[]] avrispmkii)
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
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(PROJECT_NAME tft_life)

set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)

project(${PROJECT_NAME})

set(${PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set (${CMAKE_PROJECT_NAME}_AFLAGS -v)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
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
  LineEnding[182, 183]
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
  LineEnding[302, 303]
  Command[303, 341] text:[303, 306, "set"] open:[306, 307, "("] arguments:[307, 340, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[340, 341, ")"]
    Argument[307, 327] text:[307, 327, "${PROJECT_NAME}_SRCS"]
    Argument[328, 340] text:[328, 340, "tft_life.cpp"]
  LineEnding[341, 342]
  Command[342, 438] spaces:[342, 390, "#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)\n"] text:[390, 406, "link_directories"] open:[406, 407, "("] arguments:[407, 437, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[437, 438, ")"]
    Argument[407, 437] text:[407, 437, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[438, 439]
  BlankLine[439, 440]
  LineEnding[491, 492]
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
  BlankLine[668, 669]
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



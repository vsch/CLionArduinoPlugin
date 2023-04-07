package com.vladsch.clionarduinoplugin.resources

import java.util.regex.Pattern

class ArduinoConfig(boardTxt: String?, programmersTxt: String?) {
    val boardIdMap: LinkedHashMap<String, Board>
    val programmerIdMap: LinkedHashMap<String, Programmer>
    var cpuMenu: String

    init {
        val (b, c) = parseBoards(boardTxt)
        boardIdMap = b
        cpuMenu = c
        programmerIdMap = parseProgrammers(programmersTxt)
    }

    private val boardNameMap: HashMap<String, Board> by lazy {
        val map = HashMap<String, Board>()
        for ((_, value) in boardIdMap) {
            map[value.name] = value
        }
        map
    }

    private val programmerNameMap: HashMap<String, Programmer>  by lazy {
        val map = HashMap<String, Programmer>()
        for ((_, value) in programmerIdMap) {
            map[value.name] = value
        }
        map
    }

    fun getBoardById(id: String?): Board {
        return boardIdMap[id] ?: Board.NULL
    }

    fun getProgrammerById(id: String?): Programmer {
        return programmerIdMap[id] ?: Programmer.NULL
    }

    fun getBoardByName(name: String?): Board {
        return boardNameMap[name] ?: Board.NULL
    }

    fun getProgrammerByName(name: String?): Programmer {
        return programmerNameMap[name] ?: Programmer.NULL
    }

    fun getBoardCpuId(boardId: String?, cpuId: String?): String {
        return (boardIdMap[boardId] ?: Board.NULL).cpuIdById(cpuId)
    }

    fun getBoardCpuIdByName(boardId: String?, cpuName: String?): String {
        return (boardIdMap[boardId] ?: Board.NULL).cpuIdByName(cpuName)
    }

    fun getBoardCpuNameById(boardId: String?, cpuId: String?): String {
        return (boardIdMap[boardId] ?: Board.NULL).cpuNameById(cpuId)
    }

    fun getBoardCpuNameByName(boardId: String?, cpuName: String?): String {
        return (boardIdMap[boardId] ?: Board.NULL).cpuNameByName(cpuName)
    }

    companion object {
        private const val MENU_CPU = "menu.cpu"
        private const val DEFAULT_CPU_MENU = "Processor"

        val boardsTxtString: String
            get() = ResourceUtils.getResourceFileContent(ArduinoConfig::class.java, "/com/vladsch/clionarduinoplugin/config/boards.txt")

        val programmersTxtString: String
            get() = ResourceUtils.getResourceFileContent(ArduinoConfig::class.java, "/com/vladsch/clionarduinoplugin/config/programmers.txt")

        val toolchainTxtString: String
            get() = ResourceUtils.getResourceFileContent(ArduinoConfig::class.java, "/com/vladsch/clionarduinoplugin/resources/arduino-cmake/cmake/ArduinoToolchain.cmake")

        val toolchainTxtString2: String
            get() = ResourceUtils.getResourceFileContent(ArduinoConfig::class.java, "/com/vladsch/clionarduinoplugin/resources/arduino-cmake/cmake/Platform/Arduino.cmake")

        fun parseProgrammers(programmersText: String?): LinkedHashMap<String, Programmer> {
            val programmers = LinkedHashMap<String, Programmer>()
            if (programmersText != null) {
                val lines = programmersText.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val keyEntry = Pattern.compile("([^=]+)=(.*)")

                for (line in lines) {
                    val l = line.trim { it <= ' ' }
                    if (l.isEmpty() || l[0] == '#') continue
                    val matcher = keyEntry.matcher(l)
                    if (matcher.matches()) {
                        val key = matcher.group(1)
                        val value = matcher.group(2)
                        val keyParts = key.split("\\.".toRegex())

                        if (keyParts.size == 2 && keyParts[1] == "name") {
                            // have a programmer name
                            programmers.computeIfAbsent(keyParts[0]) { Programmer(it, value) }
                        }
                    }
                }
            }
            return programmers
        }

        fun parseBoards(boardText: String?): Pair<LinkedHashMap<String, Board>, String> {
            val boards = LinkedHashMap<String, Board>()
            var cpuMenu = DEFAULT_CPU_MENU

            if (boardText != null) {
                val lines = boardText.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val keyEntry = Pattern.compile("([^=]+)=(.*)")

                for (line in lines) {
                    val l = line.trim { it <= ' ' }
                    if (l.isEmpty() || l[0] == '#') continue
                    val matcher = keyEntry.matcher(l)
                    if (matcher.matches()) {
                        val key = matcher.group(1)
                        val value: String = matcher.group(2) ?: ""

                        if (key == MENU_CPU) {
                            cpuMenu = value
                        } else {
                            val keyParts = key.split("\\.".toRegex())

                            if (keyParts.size == 2 && keyParts[1] == "name") {
                                // have a board name
                                boards.computeIfAbsent(keyParts[0]) { Board(it, value, LinkedHashMap()) }
                            } else if (keyParts.size == 4 && keyParts[1] == "menu" && keyParts[2] == "cpu") {
                                // have a board cpu
                                boards[keyParts[0]]?.let { it.cpuNameMap[keyParts[3]] = value }
                            }
                        }
                    }
                }
            }
            return Pair(boards, cpuMenu)
        }
    }
}

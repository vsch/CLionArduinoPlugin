package com.vladsch.clionarduinoplugin.resources

class Board(val id: String, val name: String, val cpuNameMap: LinkedHashMap<String, String>) {
    private val nameCpuMap: HashMap<String, String> by lazy {
        val map = HashMap<String, String>()
        for ((key, value) in cpuNameMap) {
            map[value] = key
        }
        map
    }

    val isNull: Boolean
        get() = id.isEmpty()

    fun cpuIdByName(cpuName: String?): String {
        return nameCpuMap[cpuName] ?: ""
    }

    val cpuIds: List<String> = cpuNameMap.keys.toList()
    val cpuNames: List<String> = cpuNameMap.values.toList()

    fun cpuIdById(cpuId: String?): String {
        return if (cpuId != null && cpuNameMap.containsKey(cpuId)) cpuId else ""
    }

    fun cpuNameById(cpuId: String?): String {
        return cpuNameMap[cpuId] ?: ""
    }

    fun cpuNameByName(cpuName: String?): String {
        return if (cpuName != null && nameCpuMap.containsKey(cpuName)) cpuName else ""
    }

    companion object {
        val NULL = Board("", "", LinkedHashMap())
    }
}

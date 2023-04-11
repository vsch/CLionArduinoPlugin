package com.vladsch.clionarduinoplugin.generators.cmake

import com.intellij.openapi.diagnostic.Logger
import com.vladsch.clionarduinoplugin.generators.cmake.ast.*
import com.vladsch.clionarduinoplugin.generators.cmake.commands.*
import com.vladsch.clionarduinoplugin.resources.TemplateResolver
import com.vladsch.clionarduinoplugin.resources.resolveRefs
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.plugin.util.*
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import kotlin.text.ifEmpty
import kotlin.text.removePrefix
import kotlin.text.removeSuffix

/**
 * Class for creating, reading and modifying CMakeLists.txt files with specific flavour
 * provided by specialized subclasses
 *
 *
 * This class knows the how and when of manipulating the file but no knowledge of what
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class CMakeListsTxtBuilder(
    commands: Array<CMakeCommandType>,
    anchors: Array<CMakeCommandAnchor>,
    val projectNameMacro: String = PROJECT_NAME
) {

    private val myElements = ArrayList<CMakeElement>()
    private val myElementNodeMap = HashMap<CMakeElement, Node>()
    private val myCMakeCommands: MutableMap<String, CMakeCommandType>
    private val myCommands: MutableMap<String, CMakeCommandType>
    private val mySetCommands: MutableMap<String, CMakeCommandType>
    private val mySetCommandsArg0: MutableMap<String, CMakeCommandType>
    private val mySetCommandsArg0Keys: Array<String>
    private val myAnchorsMap: MutableMap<CMakeCommandType, ArrayList<CMakeCommandAnchor>>
    private val myFirstAnchors: ArrayList<CMakeCommandType>
    private val myLastAnchors: ArrayList<CMakeCommandType>
    private val myBeforeAnchorsMap: HashMap<CMakeCommandType, ArrayList<CMakeCommandType>>
    private val myAfterAnchorsMap: HashMap<CMakeCommandType, ArrayList<CMakeCommandType>>
    private var myCMakeFile: CMakeFile? = null

    var canUseUnmodifiedOriginal = true
        private set

    // values set from cmake file not modified commands
    val cMakeVariableValues = VariableExpander()
    var cMakeProjectName = ""
        private set

    var cMakeProjectNameMacro = ""
        private set

    var outputCMakeProjectNameMacro = ""
        private set

    var isWantCommented: Boolean = false

    val elements: List<CMakeElement>
        get() = myElements

    init {
        myCMakeCommands = HashMap()
        myCommands = HashMap()
        myAnchorsMap = HashMap()
        mySetCommands = HashMap()
        mySetCommandsArg0 = HashMap()
        val setCommandsArg0Keys = ArrayList<String>()
        isWantCommented = false

        appendCommands(ourCommands, setCommandsArg0Keys)
        appendCommands(commands, setCommandsArg0Keys)

        // get set command keys sorted by deepest specialization first
        setCommandsArg0Keys.sortBy { mySetCommandsArg0[it]!!.ancestors }
        setCommandsArg0Keys.reverse()

        mySetCommandsArg0Keys = setCommandsArg0Keys.toTypedArray()

        myFirstAnchors = ArrayList()
        myLastAnchors = ArrayList()
        myBeforeAnchorsMap = HashMap()
        myAfterAnchorsMap = HashMap()

        for (commandAnchor in anchors) {
            val commandType = commandAnchor.commandType
            val commandAnchorType = commandAnchor.commandAnchor
            val beforeList: ArrayList<CMakeCommandType>?
            val afterList: ArrayList<CMakeCommandType>?

            when (commandAnchor.anchorType) {
                AnchorType.FIRST -> {
                    if (myLastAnchors.contains(commandType)) throw IllegalStateException("CommandType " + commandType.name + " cannot be anchored first and last")
                    myFirstAnchors.add(commandType)
                }

                AnchorType.LAST -> {
                    if (myFirstAnchors.contains(commandType)) throw IllegalStateException("CommandType " + commandType.name + " cannot be anchored first and last")
                    myLastAnchors.add(commandType)
                }

                AnchorType.BEFORE -> {
                    beforeList = myBeforeAnchorsMap.computeIfAbsent(commandAnchorType) { ArrayList() }
                    afterList = myBeforeAnchorsMap[commandAnchorType]
                    if (afterList != null && afterList.contains(commandType)) throw IllegalStateException("CommandType " + commandType.name + " cannot be anchored before and after " + commandAnchorType.name)
                    beforeList.add(commandType)
                }

                AnchorType.AFTER -> {
                    afterList = myAfterAnchorsMap.computeIfAbsent(commandAnchorType) { ArrayList() }
                    beforeList = myBeforeAnchorsMap[commandAnchorType]
                    if (beforeList != null && beforeList.contains(commandType)) throw IllegalStateException("CommandType " + commandType.name + " cannot be anchored before and after " + commandAnchorType.name)
                    afterList.add(commandType)
                }
            }
        }

        for (commandAnchor in anchors) {
            val anchorList = myAnchorsMap.computeIfAbsent(commandAnchor.commandType) { ArrayList() }
            anchorList.add(commandAnchor)
        }
    }

    @JvmOverloads
    constructor(
        projectNameMacro: String,
        commands: Array<CMakeCommandType>,
        anchors: Array<CMakeCommandAnchor>,
        text: CharSequence,
        options: DataHolder?,
        values: Map<String, Any>? = null
    ) : this(commands, anchors, projectNameMacro) {
        val parser = CMakeParser(BasedSequence.of(text), options ?: DEFAULT_OPTIONS)
        myCMakeFile = parser.document
        loadCMakeFile(values)
    }

    @JvmOverloads
    constructor(
        projectNameMacro: String,
        commands: Array<CMakeCommandType>,
        anchors: Array<CMakeCommandAnchor>,
        cMakeFile: CMakeFile,
        values: Map<String, Any>? = null
    ) : this(commands, anchors, projectNameMacro) {
        myCMakeFile = cMakeFile
        loadCMakeFile(values)
    }

    private fun appendCommands(commands: Array<CMakeCommandType>, setCommandsArg0Keys: ArrayList<String>) {
        for (commandType in commands) {
            if ("set" == commandType.command && commandType.fixedArgs.isNotEmpty()) {
                mySetCommands[commandType.name] = commandType
                mySetCommandsArg0[commandType.fixedArgs[0]] = commandType
                setCommandsArg0Keys.add(commandType.fixedArgs[0])
            } else {
                myCommands[commandType.name] = commandType
                myCMakeCommands[commandType.command] = commandType
            }
        }
    }

    private fun loadCMakeFile(values: Map<String, Any>?) {
        val valueSet = HashMap<String, Any>()
        if (values != null) valueSet.putAll(values)
        var erroneousSetProjectNameMacro = ""
        var erroneousSetProjectName = ""

        // if cmake project var name not given, set it to the
        if (!valueSet.containsValue(PROJECT_VAR_NAME)) {
            valueSet[PROJECT_VAR_NAME] = projectNameMacro.removeSurrounding("\${", "}")
        }

        // first get the variable values
        for (node in myCMakeFile!!.children) {
            if (node is Command && node !is CommentedOutCommand) {
                if (node.command.equals("set")) {
                    val rawArgs = ArrayList<String>()

                    for (arg in node.getChildren()) {
                        if (arg is Argument) {
                            rawArgs.add(arg.text.toString())
                        }
                    }

                    // RELEASE: if inside conditional or looping commands then do not replace its macro values
                    //     by checking the parent nodes (or previous nodes) and seeing if unmatched parent conditional or loop is our parent
                    val args = rawArgs.map { cMakeVariableValues.resolve(it) }
                    var useVarName: String? = null

                    if (rawArgs.isNotEmpty() && args.size > 1 && (rawArgs[0] == "\${CMAKE_PROJECT_NAME}" && args[0].isEmpty() || rawArgs[0] == "\${PROJECT_NAME}") && args[0].isEmpty()) {
                        // probably a mistake of including ${} around variable name
                        erroneousSetProjectName = args[1]
                        erroneousSetProjectNameMacro = rawArgs[0]
                        useVarName = rawArgs[0].removePrefix("\${").removeSuffix("}")
                    }

                    if (rawArgs.isNotEmpty() && (rawArgs[0] == "${cMakeProjectNameMacro}_SRCS" || rawArgs[0] == "${cMakeProjectNameMacro}_HDRS")) {
                        // Don't expand any macros. Just leave as is
                        if (rawArgs.size > 1) cMakeVariableValues[rawArgs[0]] = rawArgs.slice(1 .. rawArgs.size - 1)
                        else cMakeVariableValues[rawArgs[0]] = null
                    } else if (rawArgs.isNotEmpty() && (rawArgs[0] == "\${PROJECT_NAME}_SRCS" || rawArgs[0] == "\${PROJECT_NAME}_HDRS"
                            || rawArgs[0] == "\${CMAKE_PROJECT_NAME}_SRCS" || rawArgs[0] == "\${CMAKE_PROJECT_NAME}_HDRS")
                    ) {
                        // this is most likely a mistake, since the project name is in cMakeProjectNameMacro
                        val replaceMacroName = cMakeProjectNameMacro + rawArgs[0].substring(rawArgs[0].length - 5)
                        if (rawArgs.size > 1) cMakeVariableValues[replaceMacroName] = rawArgs.slice(1 .. rawArgs.size - 1)
                        else cMakeVariableValues[replaceMacroName] = null
                    } else {
                        if (args.size > 1) cMakeVariableValues[useVarName ?: args[0]] = args.slice(1 .. args.size - 1)
                        else if (args.isNotEmpty()) cMakeVariableValues[useVarName ?: args[0]] = null
                    }
                } else if (node.command.equals("project")) {
                    // get the project name
                    val nameNode = node.getFirstChildAny(Argument::class.java) as Argument?
                    if (nameNode != null) {
                        val value = nameNode.text.toString()
                        if (cMakeVariableValues.hasVariableRef(value)) {
                            cMakeProjectNameMacro = value
                            cMakeProjectName = cMakeVariableValues.resolve(value)
                            if (cMakeProjectNameMacro == erroneousSetProjectNameMacro) {
                                cMakeProjectName = erroneousSetProjectName
                                // TODO: need to have ability to log errors and warnings for processed files.
                                canUseUnmodifiedOriginal = false
                            }
                        } else {
                            cMakeProjectName = value
                        }

                        // this is set by the command!!!
                        cMakeVariableValues["PROJECT_NAME"] = cMakeProjectName
                    }
                }
            }
        }

        // if blank then make sure it is not changed from the macro, maybe it will succeed
        if (cMakeProjectName.isEmpty()) cMakeProjectName = this.projectNameMacro

        // now we can resolve the commands
        for (node in myCMakeFile!!.children) {
            val element = elementFrom(node, valueSet)
            addElement(element, node)

            if (element is CMakeCommand) {
                val typeName = element.commandType.name
                valueSet[typeName] = element
            }
        }
    }

    fun elementFrom(node: Node, valueSet: Map<String, Any>?): CMakeElement {
        // either command or text
        if (node is Command) {
            // find the command or we can create a new one if we don't have it already or just make it into a text block
            var commandType: CMakeCommandType? = null
            val rawArgs = ArrayList<String>()
            val rawArgsLeadingSpaces = ArrayList<BasedSequence>()

            for (arg in node.getChildren()) {
                if (arg is Argument) {
                    rawArgs.add(arg.text.toString())
                    if (arg.previous is LineEnding) {
                        rawArgsLeadingSpaces.add(arg.leadingSpaces.prefixWithEOL())
                    } else {
                        rawArgsLeadingSpaces.add(arg.leadingSpaces)
                    }
                }
            }

            val lastChild = node.lastChild
            val addBeforeClosing =
                if (lastChild is LineEnding) node.baseSubSequence(lastChild.startOffset, node.closingMarker.startOffset)
                else BasedSequence.NULL

            val commandName = node.command.toString()
            if (commandName == "set") {
                // see if we have a matching set command
                val arg = rawArgs.firstOrNull()
                if (arg != null) {
                    val setCommand: String?

                    if (!canUseUnmodifiedOriginal && rawArgs.isNotEmpty() && rawArgs.size > 1 && (rawArgs[0] == "\${CMAKE_PROJECT_NAME}" || rawArgs[0] == "\${PROJECT_NAME}")) {
                        // erroneous set, we convert it to proper set project name
                        setCommand = cMakeProjectNameMacro.removeSurrounding("\${", "}")
                    } else {
                        setCommand = arg
                    }

                    // normalize it to PROJECT_NAME as commands expect
                    val resolvedSetCommand: String
                    if (setCommand == "\${PROJECT_NAME}_SRCS" || setCommand == "\${PROJECT_NAME}_HDRS") {
                        // do not resolve
                        resolvedSetCommand = cMakeVariableValues[setCommand].joinToString { it }
                    } else {
                        resolvedSetCommand = cMakeVariableValues.resolve(setCommand)
                    }

                    rawArgs[0] = resolvedSetCommand

                    commandType = mySetCommandsArg0[setCommand]
                    if (commandType == null) {
                        // if the name contains a macro then it won't be expanded but the setCommand is expanded
                        // need to convert it by expanding dependent names
                        for (name in mySetCommandsArg0Keys) {
                            val nameWithProjectName =
                                if (cMakeProjectNameMacro.isNotEmpty()) name.replace(PROJECT_NAME, cMakeProjectNameMacro) else name
                            val converted = replacedCommandParams(cMakeVariableValues.resolve(nameWithProjectName), valueSet)
                            if (converted.replace(projectNameMacro, cMakeProjectName) == resolvedSetCommand) {
                                // it is this
                                commandType = mySetCommandsArg0[name]
                                rawArgs[0] = name
                                break
                            } else if (converted.indexOf(CMakeCommandType.WILDCARD_ARG_MARKER) >= 0) {
                                // has wildcard match
                                val regEx = ("^\\Q" + converted.replace(projectNameMacro, cMakeProjectName)
                                    .replace(CMakeCommandType.WILDCARD_ARG_MARKER, "\\E.*?\\Q") + "\\E$").toRegex()
                                if (resolvedSetCommand.matches(regEx)) {
                                    // use generic set
                                    commandType = mySetCommandsArg0[name]
                                    break
                                }
                            }
                        }
                    }
                }
            }

            if (commandType == null) {
                commandType = myCMakeCommands[commandName]
            }

            val makeCommand: CMakeCommandBase

            // DEBUG: remove lines
            if (commandName == "made_up_command") {
                val tmp = 0
            }

            if (commandType != null) {
                makeCommand = CMakeCommand(commandType, false, addBeforeClosing)
                makeCommand.leadingSpaces = node.leadingSpaces

                var i = 0
                var j = 0

                // first process fixed args, they may need skipping or extraction of wildcards
                for (fixedArg in commandType.fixedArgs) {
                    if (fixedArg.contains(CMakeCommandType.WILDCARD_ARG_MARKER)) {
                        // extract value of wild card
                        val regEx = Pattern.compile("^\\Q" + fixedArg.replace(CMakeCommandType.WILDCARD_ARG_MARKER, "\\E(.*?)\\Q") + "\\E$")
                        val matcher = regEx.matcher(rawArgs[i++])
                        matcher.find()
                        for (g in 1 .. matcher.groupCount()) {
                            makeCommand.setArg(j++, matcher.group(g), rawArgsLeadingSpaces[g])
                        }
                    } else i++
                }

                while (i < rawArgs.size) {
                    makeCommand.setArg(j++, rawArgs[i], rawArgsLeadingSpaces[i])
                    i++
                }
            } else {
                // unknown command
                makeCommand = CMakeUnknownCommand(commandName, rawArgs, rawArgsLeadingSpaces, false, addBeforeClosing)
                makeCommand.leadingSpaces = node.leadingSpaces
            }

            if (node is CommentedOutCommand) {
                makeCommand.commentOut(true)
            }

            return makeCommand
        }

        // create a text element
        return CMakeText(node.chars.toString(), false)
    }

    fun elementOriginalText(element: CMakeElement): String {
        val node = myElementNodeMap[element] ?: return ""
        return node.chars.toString()
    }

    fun addElement(element: CMakeElement, node: Node) {
        // no add eol adjustment, assumed to be done by caller
        myElementNodeMap[element] = node
        myElements.add(element)
    }

    fun addElement(element: CMakeElement) {
        myElements.add(element)
        fixAddEOL(myElements.size - 1)
    }

    fun fixAddEOL(insertedCommandIndex: Int) {
        val command = myElements[insertedCommandIndex]
        if (insertedCommandIndex == 0) {
            command.isAddEOL = true
        } else if (insertedCommandIndex + 1 == myElements.size) {
            val prevElement = myElements[insertedCommandIndex - 1]
            command.isAddEOL = prevElement.isAddEOL
            prevElement.isAddEOL = true
        } else {
            val prevElement = myElements[insertedCommandIndex - 1]
            if (prevElement is CMakeCommandBase) {
                // steal prev command's addEOL, make it addEOL since this command now takes its place
                command.isAddEOL = prevElement.isAddEOL()
                prevElement.setAddEOL(true)
            }
        }
    }

    fun addElement(index: Int, element: CMakeElement) {
        myElements.add(index, element)
        fixAddEOL(index)
    }

    fun setElement(index: Int, element: CMakeElement) {
        if (index < myElements.size) {
            val indexElement = myElements[index]
            element.isAddEOL = indexElement.isAddEOL

            if (element is CMakeCommandBase && indexElement is CMakeCommandBase) {
                element.leadingSpaces = indexElement.leadingSpaces
            }
        } else {
            element.isAddEOL = true
        }

        myElements[index] = element
    }

    fun removeElement(index: Int) {
        if (index + 1 < myElements.size)
            myElements[index + 1].isAddEOL = myElements[index].isAddEOL
        myElements.removeAt(index)
    }

    fun removeElement(element: CMakeElement) {
        val index = myElements.indexOf(element)
        if (index == -1) {
            throw IllegalStateException("anchor element not found")
        }
        removeElement(index)
    }

    fun addElementBefore(anchor: CMakeElement, element: CMakeElement): Int {
        val index = myElements.indexOf(anchor)
        if (index == -1) {
            throw IllegalStateException("anchor element not found")
        }

        addElement(index, element)
        return index
    }

    fun addElementAfter(anchor: CMakeElement, element: CMakeElement) {
        var index = myElements.indexOf(anchor)

        if (index == -1) {
            throw IllegalStateException("anchor element not found")
        }

        index++
        addElement(index, element)
    }

    fun replaceElement(anchor: CMakeElement, element: CMakeElement) {
        val index = myElements.indexOf(anchor)
        if (index == -1) {
            throw IllegalStateException("anchor element not found")
        }

        setElement(index, element)
    }

    fun getCommandIndex(name: String): Int {
        val commandType = getCommandType(name)
        return getCommandIndex(commandType)
    }

    fun getCommandIndex(commandType: CMakeCommandType?): Int {
        return getCommandIndex(commandType, 0, myElements.size, false)
    }

    /**
     * range is [rangeStart, rangeEnd)
     */
    fun getCommandIndex(
        commandType: CMakeCommandType?,
        rangeStart: Int = 0,
        rangeEnd: Int = myElements.size,
        reverse: Boolean = false
    ): Int {
        var firstCommented = -1
        var i = reverse.ifElse(rangeEnd, rangeStart)
        val dI = reverse.ifElse(-1, 1)

        if (commandType != null) {
            while (i >= 0 && i < myElements.size && i >= rangeStart && i < rangeEnd) {
                val element = myElements[i]
                if (element is CMakeCommand) {
                    if (element.isOfType(commandType)) {
                        if (element.isCommented) {
                            if (isWantCommented && firstCommented == -1) {
                                firstCommented = i
                            }
                        } else {
                            return i
                        }
                    } else {
                        // TODO: make it work for other sub-commands
                        if (commandType == SET_PROJECT_NAME && commandType.isOfType(element.commandType)) {
                            // specialization, compare first argument to see if a match, for now just set project name
                            val setCommandMacro = outputCMakeProjectNameMacro.ifEmpty { cMakeProjectNameMacro }
                            if (element.argCount > 1 && element.arg(0).prefixWith("\${").suffixWith("}") == setCommandMacro) {
                                return i
                            }
                        }
                    }
                }
                i += dI
            }
        }
        return firstCommented
    }

    fun getCommand(name: String): CMakeCommand? {
        val index = getCommandIndex(name)
        return if (index >= 0) myElements[index] as CMakeCommand else null
    }

    fun getCommand(commandType: CMakeCommandType?): CMakeCommand? {
        val index = getCommandIndex(commandType)
        return if (index >= 0) myElements[index] as CMakeCommand else null
    }

    fun getCommands(commandType: CMakeCommandType?): List<CMakeCommand> {
        var index = -1
        val list = ArrayList<CMakeCommand>()
        while (true) {
            index = getCommandIndex(commandType, index + 1, myElements.size, false)
            if (index < 0) break
            list.add(myElements[index] as CMakeCommand)
        }
        return list
    }

    fun getCommandType(name: String): CMakeCommandType? {
        var commandType: CMakeCommandType? = mySetCommands[name]
        if (commandType == null) {
            commandType = myCommands[name]
        }
        return commandType
    }

    private fun afterAnchorIndex(range: IndexRange, commandType: CMakeCommandType?): Boolean {
        if (commandType != null) {
            val anchorIndex = getCommandIndex(commandType.name)
            if (anchorIndex >= 0 && anchorIndex >= range.afterIndex) {
                range.afterIndex = anchorIndex + 1
                return true
            }
        }
        return false
    }

    private fun beforeAnchorIndex(range: IndexRange, commandType: CMakeCommandType?) {
        if (commandType != null) {
            val anchorIndex = getCommandIndex(commandType.name)
            if (anchorIndex >= 0 && anchorIndex < range.beforeIndex) range.beforeIndex = anchorIndex
        }
    }

    private class IndexRange(val originalBefore: Int, val originalAfter: Int) {

        var beforeIndex: Int = 0
        var afterIndex: Int = 0

        val isUnmodified: Boolean
            get() = originalAfter == afterIndex && originalBefore == beforeIndex

        val isUnmodifiedBefore: Boolean
            get() = originalBefore == beforeIndex

        val isUnmodifiedAfter: Boolean
            get() = originalAfter == afterIndex

        init {

            this.beforeIndex = originalBefore
            this.afterIndex = originalAfter
        }

        override fun toString(): String {
            return "IndexRange [" +
                "" + afterIndex +
                " - " + beforeIndex +
                ']'.toString()
        }
    }

    private fun adjustIndexRange(
        command: CMakeCommand,
        range: IndexRange,
        siblings: Collection<CMakeCommandType>?,
        siblingsAnchorType: AnchorType,
        isMember: Boolean
    ) {
        if (siblings == null) return

        var isAfter: Boolean
        val isOnSelfAfter: Boolean
        var hadSelf = false

        when (siblingsAnchorType) {
            AnchorType.LAST -> if (isMember) {
                isAfter = true
                isOnSelfAfter = false
            } else {
                isAfter = false
                isOnSelfAfter = false
            }

            AnchorType.FIRST -> if (isMember) {
                isAfter = true
                isOnSelfAfter = false
            } else {
                isAfter = true
                isOnSelfAfter = true
            }

            AnchorType.AFTER, AnchorType.BEFORE -> {
                isAfter = true
                isOnSelfAfter = false
            }
        }

        for (siblingType in siblings) {
            if (siblingType === command.commandType) {
                isAfter = isOnSelfAfter
                hadSelf = true
            } else {
                if (isAfter) {
                    afterAnchorIndex(range, siblingType)
                } else {
                    beforeAnchorIndex(range, siblingType)
                }
            }
        }

        when (siblingsAnchorType) {
            AnchorType.FIRST -> if (hadSelf) {
                if (range.isUnmodified) {
                    range.beforeIndex = 0
                }
            }

            AnchorType.LAST -> if (hadSelf) {
                if (range.isUnmodified) {
                    range.afterIndex = myElements.size
                }
            }

            else -> {
            }
        }
    }

    /**
     * Place according to anchors or at the end if no anchors for this command
     *
     *
     * Heuristic, Not efficient but does the job
     *
     * @param command command to place in the file
     */
    fun addCommand(command: CMakeCommand) {
        val anchors = myAnchorsMap[command.commandType]
        // go through the list and try to satisfy conditions
        val range = IndexRange(myElements.size, 0)
        var afterHasPriority = false

        if (anchors == null) {
            // make respect firsts, lasts, and any of its own dependents
            adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false)
            adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false)
            val beforeDependents = myBeforeAnchorsMap[command.commandType]
            val afterDependents = myAfterAnchorsMap[command.commandType]
            if (beforeDependents != null) {
                // treat them as first (ie. before this node)
                adjustIndexRange(command, range, beforeDependents, AnchorType.FIRST, false)
            }
            if (afterDependents != null) {
                // treat them as last (ie. after this node)
                adjustIndexRange(command, range, afterDependents, AnchorType.LAST, false)
            }
        } else {
            for (anchor in anchors) {
                when (anchor.anchorType) {
                    AnchorType.FIRST -> adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, true)

                    AnchorType.BEFORE -> {
                        adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false)
                        adjustIndexRange(command, range, myBeforeAnchorsMap[anchor.commandAnchor], AnchorType.BEFORE, true)

                        beforeAnchorIndex(range, anchor.commandAnchor)

                        // treat any of the after of the same parent as last so this command will come before
                        adjustIndexRange(command, range, myAfterAnchorsMap[anchor.commandAnchor], AnchorType.LAST, false)
                        adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false)
                    }

                    AnchorType.AFTER -> {
                        adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false)
                        // treat any of the before of the same parent as firsts so this command will come after
                        adjustIndexRange(command, range, myBeforeAnchorsMap[anchor.commandAnchor], AnchorType.FIRST, false)

                        if (afterAnchorIndex(range, anchor.commandAnchor)) {
                            // found true anchor, use the afterIndex for placement, the before may be too low in the file
                            afterHasPriority = true
                        }

                        adjustIndexRange(command, range, myAfterAnchorsMap[anchor.commandAnchor], AnchorType.AFTER, true)
                        adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false)
                    }

                    AnchorType.LAST ->
                        //adjustIndexRange(command, range, myFirstAnchors, AnchorType.FIRST, false);
                        adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, true)
                } //adjustIndexRange(command, range, myLastAnchors, AnchorType.LAST, false);
            }
        }

        if (command.isOfType(PROJECT) && command.argCount > 0) {
            // copy its argument for generating the CMakeLists file
            outputCMakeProjectNameMacro = command.arg(0)
        }

        if (range.beforeIndex == myElements.size && range.afterIndex == 0) {
            // no anchors, goes last
            addElement(command)
        } else if (range.beforeIndex == myElements.size) {
            // no before anchor, goes after
            addElement(range.afterIndex, command)
        } else if (range.afterIndex == 0) {
            // no after anchor, goes before
            addElement(range.beforeIndex, command)
        } else {
            //            if (!afterHasPriority && range.beforeIndex < range.afterIndex) throw IllegalStateException("Invalid anchor definitions: BeforeAnchor(" + range.beforeIndex + ") < AfterAnchor(" + range.afterIndex + ") for " + command.commandType.name)
            // insert before
            addElement(if (afterHasPriority) range.afterIndex else range.beforeIndex, command)
        }
    }

    fun setCommand(name: String, args: Collection<String>): CMakeCommand? {
        return setCommand(getCommandType(name), args)
    }

    fun setOrAddCommand(name: String, vararg args: String): CMakeCommand {
        val commandType = getCommandType(name)
        return setOrAddCommand(commandType!!, Arrays.asList(*args))
    }

    fun setOrAddCommand(name: String, args: Collection<String>): CMakeCommand {
        val commandType = getCommandType(name)
        return setOrAddCommand(commandType!!, args)
    }

    fun setOrAddCommand(commandType: CMakeCommandType, vararg args: String): CMakeCommand {
        return setOrAddCommand(commandType, Arrays.asList(*args))
    }

    fun removeCommand(name: String) {
        removeCommand(getCommandType(name))
    }

    fun removeCommand(commandType: CMakeCommandType?) {
        var index: Int
        do {
            index = getCommandIndex(commandType)
            if (index != -1) {
                removeElement(index)
            }
        } while (index != -1)
    }

    /**
     * Replace, modify or insert command based on command type and command anchors information
     *
     * @param commandType command name (not CMake command name but builder specific name)
     * @param args arguments for the command
     * @return resulting command (new or modified)
     */
    fun setOrAddCommand(commandType: CMakeCommandType, args: Collection<String>?): CMakeCommand {
        val command = setCommand(commandType, args)
        if (command == null) {
            // create a new one and find where to insert it
            val newCommand = CMakeCommand(commandType, args?.toList() ?: listOf(), null, true, BasedSequence.NULL)
            addCommand(newCommand)
            return newCommand
        }
        return command
    }

    /**
     * Replace or modify a command based on command type and command anchors information
     * if the command does not exist in the builder then nothing is done and null is returned.
     *
     * RELEASE: have to not resolve its args to values if it is inside if(), else(), foreach(), while(), function()
     *     currently, expansion proceeds with the last set value
     *
     * @param commandType command name (not CMake command name but builder specific name)
     * @param args arguments for the command
     * @return resulting command (new or modified)
     */
    fun setCommand(commandType: CMakeCommandType?, args: Collection<String>?): CMakeCommand? {
        val command = getCommand(commandType)
        var newCommand = command

        if (command != null) {
            val argList = args?.toList() ?: listOf()

            if (!commandType!!.isMultiple && commandType.maxArgs == CMakeCommandType.INF_MAX_ARGS) {
                if (myElementNodeMap[command] != null) {
                    // original command, replace it with new
                    newCommand = CMakeCommand(command)
                    newCommand.commentOut(false)
                    newCommand.setArgsWithDefaults(argList, command.getArgsLeadingSpaces(argList))
                    replaceElement(command, newCommand)
                } else {
                    newCommand!!.setArgsWithDefaults(argList, null)
                }
            } else {
                if (commandType.isMultiple) {
                    if (commandType.isNoDupeArgs) {
                        // add another one after this one if the arg value is different
                        if (!command.allArgsEqual(argList)) {
                            newCommand = CMakeCommand(
                                command.commandType,
                                argList,
                                command.getArgsLeadingSpaces(argList),
                                true,
                                command.addBeforeClosing
                            )
                            newCommand.commentOut(false)
                            addElementAfter(command, newCommand)
                        }
                    } else {
                        // add another one after this one
                        newCommand = CMakeCommand(command.commandType, argList, null, true, BasedSequence.NULL)
                        addElementAfter(command, newCommand)
                    }
                } else {
                    if (command.isOfType(PROJECT) && argList.isNotEmpty()) {
                        // copy its argument for generating the CMakeLists file
                        outputCMakeProjectNameMacro = argList[0]
                    }

                    if (myElementNodeMap[command] != null) {
                        // original command, replace it with new
                        if (command.isOfType(commandType)) {
                            newCommand = CMakeCommand(command)
                            newCommand.commentOut(false)
                            newCommand.setArgsWithDefaults(argList, command.getArgsLeadingSpaces(argList))
                            replaceElement(command, newCommand)
                        } else {
                            // is a matched sub-type, need to replace completely
                            newCommand = CMakeCommand(commandType, argList, null, false, command.addBeforeClosing)
                            newCommand.commentOut(false)
                            newCommand.setArgsWithDefaults(argList, command.getArgsLeadingSpaces(argList))

                            replaceElement(command, newCommand)
                        }
                    } else {
                        newCommand!!.setArgsWithDefaults(argList, null)
                    }
                }
            }
        }
        return newCommand
    }

    fun getCMakeContents(values: Map<String, Any>?, suppressCommentedCommands: Boolean, unmodifiedOriginalText: Boolean): String {
        val sb = StringBuilder()
        val valueSet = HashMap<String, Any>()
        if (values != null) valueSet.putAll(values)

        if (outputCMakeProjectNameMacro.isEmpty()) {
            if (cMakeProjectNameMacro.isEmpty()) {
                outputCMakeProjectNameMacro = projectNameMacro
            } else {
                outputCMakeProjectNameMacro = cMakeProjectNameMacro
            }
        }

        // if cmake project var name not given, set it to the
        if (!valueSet.containsValue(PROJECT_VAR_NAME)) {
            valueSet[PROJECT_VAR_NAME] = outputCMakeProjectNameMacro.removeSurrounding("\${", "}")
        }

        for (element in myElements) {
            if (element is CMakeCommand) {
                val typeName = element.commandType.name
                if (!valueSet.containsKey(typeName)) {
                    valueSet[typeName] = element
                }
            }
        }

        var skipNextLineEnding = false
        for (element in myElements) {
            try {
                if (skipNextLineEnding) {
                    skipNextLineEnding = false

                    if (element is CMakeText && element.text == "\n") {
                        continue
                    }
                }

                val node: Node? = if (unmodifiedOriginalText) myElementNodeMap[element] else null

                if (node != null) {
                    sb.append(node.chars)
                } else {
                    element.appendTo(sb, outputCMakeProjectNameMacro, valueSet, suppressCommentedCommands)
                }

                if (suppressCommentedCommands && element is CMakeCommandBase && element.isCommented && element.isSuppressibleCommented && !element.isAddEOL) {
                    // suppress next eol
                    skipNextLineEnding = true
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        var result: String = sb.toString()
        if (result.isNotEmpty()) result = result.trimEnd() + "\n"
        return result
    }

    companion object {

        // commands can have fixed and variable arguments
        // fixed arguments can have dependency on variable arguments of other commands in the command set for a given cmake file
        // this allows fixed args to be dependent on args of other commands or values in the command set
        //
        //
        const val PROJECT_VAR_NAME = "PROJECT_NAME"
        const val PROJECT_NAME = "\${$PROJECT_VAR_NAME}"

        // @formatter:off
        val CMAKE_MINIMUM_REQUIRED = CMakeCommandType("CMAKE_MINIMUM_REQUIRED", "cmake_minimum_required", arrayOf(), 1, 2, false, false, true)
        val CMAKE_MINIMUM_REQUIRED_VERSION = CMakeCommandSubType("CMAKE_MINIMUM_REQUIRED_VERSION", CMAKE_MINIMUM_REQUIRED, arrayOf("VERSION"), 2, 2, true, false, true)
        val LINK_DIRECTORIES = CMakeCommandType("LINK_DIRECTORIES", "link_directories", arrayOf(), 1, CMakeCommandType.INF_MAX_ARGS, true, false, true)
        val ADD_SUBDIRECTORY = CMakeCommandType("ADD_SUBDIRECTORY", "add_subdirectory", arrayOf(), 1, 3, true, true, false)
        val PROJECT = CMakeCommandType("PROJECT", "project", arrayOf(), 1, 1, true, false, false, arrayOf("\${<@$PROJECT_VAR_NAME@>}"), 0)
        
        val SET = CMakeCommandType("SET", "set", arrayOf(), 1, CMakeCommandType.INF_MAX_ARGS, true, true, true)

        // Generic commands added but not verified
        val ADD_COMPILE_OPTIONS = CMakeCommandType("ADD_COMPILE_OPTIONS", "add_compile_options", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ADD_CUSTOM_COMMAND = CMakeCommandType("ADD_CUSTOM_COMMAND", "add_custom_command", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ADD_CUSTOM_TARGET = CMakeCommandType("ADD_CUSTOM_TARGET", "add_custom_target", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ADD_DEFINITIONS = CMakeCommandType("ADD_DEFINITIONS", "add_definitions", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ADD_DEPENDENCIES = CMakeCommandType("ADD_DEPENDENCIES", "add_dependencies", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ADD_EXECUTABLE = CMakeCommandType("ADD_EXECUTABLE", "add_executable", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ADD_LIBRARY = CMakeCommandType("ADD_LIBRARY", "add_library", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ADD_TEST = CMakeCommandType("ADD_TEST", "add_test", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val AUX_SOURCE_DIRECTORY = CMakeCommandType("AUX_SOURCE_DIRECTORY", "aux_source_directory", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val BREAK = CMakeCommandType("BREAK", "break", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val BUILD_COMMAND = CMakeCommandType("BUILD_COMMAND", "build_command", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val CMAKE_HOST_SYSTEM_INFORMATION = CMakeCommandType("CMAKE_HOST_SYSTEM_INFORMATION", "cmake_host_system_information", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val CMAKE_POLICY = CMakeCommandType("CMAKE_POLICY", "cmake_policy", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val CONFIGURE_FILE = CMakeCommandType("CONFIGURE_FILE", "configure_file", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val CREATE_TEST_SOURCELIST = CMakeCommandType("CREATE_TEST_SOURCELIST", "create_test_sourcelist", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val DEFINE_PROPERTY = CMakeCommandType("DEFINE_PROPERTY", "define_property", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ELSEIF = CMakeCommandType("ELSEIF", "elseif", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.PRE_UNINDENT or CMakeCommandType.POST_INDENT)
        val ELSE = CMakeCommandType("ELSE", "else", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.PRE_UNINDENT or CMakeCommandType.POST_INDENT)
        val ENABLE_LANGUAGE = CMakeCommandType("ENABLE_LANGUAGE", "enable_language", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ENABLE_TESTING = CMakeCommandType("ENABLE_TESTING", "enable_testing", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val ENDFOREACH = CMakeCommandType("ENDFOREACH", "endforeach", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.PRE_UNINDENT)
        val ENDFUNCTION = CMakeCommandType("ENDFUNCTION", "endfunction", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.PRE_UNINDENT)
        val ENDIF = CMakeCommandType("ENDIF", "endif", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.PRE_UNINDENT)
        val ENDMACRO = CMakeCommandType("ENDMACRO", "endmacro", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.PRE_UNINDENT)
        val ENDWHILE = CMakeCommandType("ENDWHILE", "endwhile", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.PRE_UNINDENT)
        val EXECUTE_PROCESS = CMakeCommandType("EXECUTE_PROCESS", "execute_process", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val EXPORT = CMakeCommandType("EXPORT", "export", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val FILE = CMakeCommandType("FILE", "file", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val FIND_FILE = CMakeCommandType("FIND_FILE", "find_file", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val FIND_LIBRARY = CMakeCommandType("FIND_LIBRARY", "find_library", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val FIND_PACKAGE = CMakeCommandType("FIND_PACKAGE", "find_package", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val FIND_PATH = CMakeCommandType("FIND_PATH", "find_path", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val FIND_PROGRAM = CMakeCommandType("FIND_PROGRAM", "find_program", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val FLTK_WRAP_UI = CMakeCommandType("FLTK_WRAP_UI", "fltk_wrap_ui", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val FOREACH = CMakeCommandType("FOREACH", "foreach", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.POST_INDENT)
        val FUNCTION = CMakeCommandType("FUNCTION", "function", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.POST_INDENT)
        val GET_CMAKE_PROPERTY = CMakeCommandType("GET_CMAKE_PROPERTY", "get_cmake_property", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val GET_DIRECTORY_PROPERTY = CMakeCommandType("GET_DIRECTORY_PROPERTY", "get_directory_property", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val GET_FILENAME_COMPONENT = CMakeCommandType("GET_FILENAME_COMPONENT", "get_filename_component", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val GET_PROPERTY = CMakeCommandType("GET_PROPERTY", "get_property", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val GET_SOURCE_FILE_PROPERTY = CMakeCommandType("GET_SOURCE_FILE_PROPERTY", "get_source_file_property", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val GET_TARGET_PROPERTY = CMakeCommandType("GET_TARGET_PROPERTY", "get_target_property", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val GET_TEST_PROPERTY = CMakeCommandType("GET_TEST_PROPERTY", "get_test_property", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val IF = CMakeCommandType("IF", "if", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.POST_INDENT)
        val INCLUDE_DIRECTORIES = CMakeCommandType("INCLUDE_DIRECTORIES", "include_directories", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val INCLUDE_EXTERNAL_MSPROJECT = CMakeCommandType("INCLUDE_EXTERNAL_MSPROJECT", "include_external_msproject", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val INCLUDE_REGULAR_EXPRESSION = CMakeCommandType("INCLUDE_REGULAR_EXPRESSION", "include_regular_expression", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val INCLUDE = CMakeCommandType("INCLUDE", "include", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val INSTALL = CMakeCommandType("INSTALL", "install", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val LIST = CMakeCommandType("LIST", "list", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val LOAD_CACHE = CMakeCommandType("LOAD_CACHE", "load_cache", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val LOAD_COMMAND = CMakeCommandType("LOAD_COMMAND", "load_command", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val MACRO = CMakeCommandType("MACRO", "macro", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.POST_INDENT)
        val MARK_AS_ADVANCED = CMakeCommandType("MARK_AS_ADVANCED", "mark_as_advanced", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val MATH = CMakeCommandType("MATH", "math", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val MESSAGE = CMakeCommandType("MESSAGE", "message", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val OPTION = CMakeCommandType("OPTION", "option", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val QT_WRAP_CPP = CMakeCommandType("QT_WRAP_CPP", "qt_wrap_cpp", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val QT_WRAP_UI = CMakeCommandType("QT_WRAP_UI", "qt_wrap_ui", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val REMOVE_DEFINITIONS = CMakeCommandType("REMOVE_DEFINITIONS", "remove_definitions", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val RETURN = CMakeCommandType("RETURN", "return", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val SEPARATE_ARGUMENTS = CMakeCommandType("SEPARATE_ARGUMENTS", "separate_arguments", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val SET_DIRECTORY_PROPERTIES = CMakeCommandType("SET_DIRECTORY_PROPERTIES", "set_directory_properties", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val SET_PROPERTY = CMakeCommandType("SET_PROPERTY", "set_property", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val SET_SOURCE_FILES_PROPERTIES = CMakeCommandType("SET_SOURCE_FILES_PROPERTIES", "set_source_files_properties", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val SET_TARGET_PROPERTIES = CMakeCommandType("SET_TARGET_PROPERTIES", "set_target_properties", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val SET_TESTS_PROPERTIES = CMakeCommandType("SET_TESTS_PROPERTIES", "set_tests_properties", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val SITE_NAME = CMakeCommandType("SITE_NAME", "site_name", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val SOURCE_GROUP = CMakeCommandType("SOURCE_GROUP", "source_group", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val STRING = CMakeCommandType("STRING", "string", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val TARGET_COMPILE_DEFINITIONS = CMakeCommandType("TARGET_COMPILE_DEFINITIONS", "target_compile_definitions", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val TARGET_COMPILE_OPTIONS = CMakeCommandType("TARGET_COMPILE_OPTIONS", "target_compile_options", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val TARGET_INCLUDE_DIRECTORIES = CMakeCommandType("TARGET_INCLUDE_DIRECTORIES", "target_include_directories", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val TARGET_LINK_LIBRARIES = CMakeCommandType("TARGET_LINK_LIBRARIES", "target_link_libraries", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val TRY_COMPILE = CMakeCommandType("TRY_COMPILE", "try_compile", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val TRY_RUN = CMakeCommandType("TRY_RUN", "try_run", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val UNSET = CMakeCommandType("UNSET", "unset", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val VARIABLE_WATCH = CMakeCommandType("VARIABLE_WATCH", "variable_watch", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false)
        val WHILE = CMakeCommandType("WHILE", "while", arrayOf(), 0, CMakeCommandType.INF_MAX_ARGS, false, true, false, null, CMakeCommandType.POST_INDENT)

        // specialized set commands
        val SET_CMAKE_TOOLCHAIN_FILE = CMakeCommandSubType("SET_CMAKE_TOOLCHAIN_FILE", SET, arrayOf("CMAKE_TOOLCHAIN_FILE"), 1, 1, true, false, true, arrayOf("\${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"))
        val SET_CMAKE_CXX_STANDARD = CMakeCommandSubType("SET_CMAKE_CXX_STANDARD", SET, arrayOf("CMAKE_CXX_STANDARD"), 1, 1, false, false, true)
        val SET_PROJECT_NAME = CMakeCommandSubType("SET_PROJECT_NAME", SET, arrayOf("<@$PROJECT_VAR_NAME@>"), 1, 1, false, false, true)
        // @formatter:on

        val ourCommands = arrayOf(
            CMAKE_MINIMUM_REQUIRED,
            CMAKE_MINIMUM_REQUIRED_VERSION,
            LINK_DIRECTORIES,
            ADD_SUBDIRECTORY,
            PROJECT,
            SET,

            SET_CMAKE_TOOLCHAIN_FILE,
            SET_CMAKE_CXX_STANDARD,
            SET_PROJECT_NAME
        )

        // @formatter:off
        val ourAllCommands = arrayOf(
            CMAKE_MINIMUM_REQUIRED, CMAKE_MINIMUM_REQUIRED_VERSION, LINK_DIRECTORIES, ADD_SUBDIRECTORY, PROJECT, SET, ADD_COMPILE_OPTIONS,
            ADD_CUSTOM_COMMAND, ADD_CUSTOM_TARGET, ADD_DEFINITIONS, ADD_DEPENDENCIES, ADD_EXECUTABLE, ADD_LIBRARY, ADD_TEST, 
            AUX_SOURCE_DIRECTORY, BREAK, BUILD_COMMAND, CMAKE_HOST_SYSTEM_INFORMATION, CMAKE_POLICY, CONFIGURE_FILE, CREATE_TEST_SOURCELIST,
            DEFINE_PROPERTY, ELSEIF, ELSE, ENABLE_LANGUAGE, ENABLE_TESTING, ENDFOREACH, ENDFUNCTION, ENDIF, ENDMACRO, ENDWHILE,
            EXECUTE_PROCESS, EXPORT, FILE, FIND_FILE, FIND_LIBRARY, FIND_PACKAGE, FIND_PATH, FIND_PROGRAM, FLTK_WRAP_UI, FOREACH, FUNCTION,
            GET_CMAKE_PROPERTY, GET_DIRECTORY_PROPERTY, GET_FILENAME_COMPONENT, GET_PROPERTY, GET_SOURCE_FILE_PROPERTY, GET_TARGET_PROPERTY,
            GET_TEST_PROPERTY, IF, INCLUDE_DIRECTORIES, INCLUDE_EXTERNAL_MSPROJECT, INCLUDE_REGULAR_EXPRESSION, INCLUDE, INSTALL, LIST,
            LOAD_CACHE, LOAD_COMMAND, MACRO, MARK_AS_ADVANCED, MATH, MESSAGE, OPTION, QT_WRAP_CPP, QT_WRAP_UI, REMOVE_DEFINITIONS, RETURN,
            SEPARATE_ARGUMENTS, SET_DIRECTORY_PROPERTIES, SET_PROPERTY, SET_SOURCE_FILES_PROPERTIES, SET_TARGET_PROPERTIES, SET_TESTS_PROPERTIES, SITE_NAME, SOURCE_GROUP,
            STRING, TARGET_COMPILE_DEFINITIONS, TARGET_COMPILE_OPTIONS, TARGET_INCLUDE_DIRECTORIES, TARGET_LINK_LIBRARIES, TRY_COMPILE,
            TRY_RUN, UNSET, VARIABLE_WATCH, WHILE, SET_CMAKE_TOOLCHAIN_FILE, SET_CMAKE_CXX_STANDARD, SET_PROJECT_NAME
        )
        // @formatter:on

        val ourIndentingCommands = ourAllCommands.filter { it.isPostIndenting || it.isPreUnIndenting }
        val ourIndentingCommandMap: Map<String, CMakeCommandType> = ourIndentingCommands.map { it.name to it }.toMap()

        private val LOG = Logger.getInstance("com.vladsch.clionarduinoplugin.generators")
        private val DEFAULT_OPTIONS = MutableDataSet()
            .set(CMakeParser.AUTO_CONFIG, true)
            .set(CMakeParser.AST_LINE_END_EOL, true)
            .set(CMakeParser.AST_COMMENTS, true)
            .set(CMakeParser.AST_BLANK_LINES, true)
            .set(CMakeParser.AST_ARGUMENT_SEPARATORS, true)
            .set(CMakeParser.AST_COMMENTED_OUT_COMMANDS, true)

        /**
         * replace other commands' argument references in the given string
         *
         * <$COMMAND_NAME$> refers to variable arg 0 of command with name COMMAND_NAME or string mapped by COMMAND_NAME
         * <$COMMAND_NAME[2]$> refers to variable arg 2 of command with name COMMAND_NAME
         * <$COMMAND_NAME[]$> invalid, always empty result
         * <$COMMAND_NAME[-1]$> first from from the end, ie. command.getArgCount() - 1
         * <$COMMAND_NAME[-3]$> third from from the end, ie. command.getArgCount() - 3
         *
         * NOTE: if Value or Command is not found or index is invalid then it is the same as the value being empty
         *
         * @param arg      string with possible command references and variable value references
         * @param valueSet map of names to values, if value is CMakeCommand then its argument value will be extracted, otherwise the argument value will be the String value of passed object
         * @return string with variables replaced
         */
        @Suppress("MemberVisibilityCanBePrivate")
        fun replacedCommandParams(arg: String, valueSet: Map<String, Any>?): String {
            val result = arg.resolveRefs(TemplateResolver.COMMAND_REF) { name, index ->
                val ref = valueSet?.get(name)
                if (ref == null) null
                else if (ref !is CMakeCommand) {
                    ref.toString() + if (index == null) "" else "[$index]"
                } else {
                    val command = ref as? CMakeCommand
                    var varIndex = index?.toIntOrNull() ?: 0

                    if (varIndex < 0) varIndex += command!!.argCount
                    if (varIndex >= 0 && varIndex < command!!.argCount) command.getArg(varIndex) else ""
                }
            }
            return result
        }
    }
}


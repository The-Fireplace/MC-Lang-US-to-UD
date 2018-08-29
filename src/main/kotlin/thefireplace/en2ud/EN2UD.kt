package thefireplace.en2ud

import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.internal.AbstractTask


open class EN2UD: Plugin<Project>{
    companion object{
        var ext:EN2UDextension? = null
    }
    override fun apply(p0: Project) {
        p0.tasks?.create("en2ud", EN2UDtask::class.java)
        ext = EN2UDextension()
        p0.extensions?.add("en2ud", ext)
    }
}

open class EN2UDextension {
    var modid = ""
    var customRegex = ""
    var copyInvalidLines = false
    var debug = false
}

open class EN2UDtask : AbstractTask() {
    companion object {
        private var regexDelimiter = "\\\\.|%([0-9]\\\$)?[A-z]|§([0-9]|[a-f]|[k-o]|r)".toRegex()
        private val possibleInputs = arrayOf("en_GB.lang", "en_US.lang", "en_CA.lang", "en_NZ.lang", "en_AU.lang", "en_7S.lang")
        private const val mcmetaDirectory = "src/main/resources/pack.mcmeta"
        private const val assetsDirectory = "src/main/resources/assets/"
        private var langDirectory = ""

        private val map = LinkedHashMap<Char, Char>()

        private fun setupMap() {
            val normal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()-_[]{};':\",.<>/?§+|= "
            val inverse = "ⱯᗺƆᗡƎℲ⅁HIՐʞꞀWNOԀΌᴚS⟘∩ᴧMX⅄Zɐqɔpǝɟᵷɥᴉɾʞꞁɯuodbɹsʇnʌʍxʎz⥝ᘔƐᔭ59Ɫ860¡@#$%^⅋*)(-‾][}{؛,:„'˙></¿§+|= "

            for((index, char) in normal.withIndex())
                map[char] = inverse[index]
        }
    }

    @TaskAction
    fun en2ud() {
        setupMap()
        //Get the lang directory
        val ext: EN2UDextension? = EN2UD.ext
        if(ext?.modid == null || ext.modid == "")
            error("No modid found!")
        //Put together the lang directory
        langDirectory = assetsDirectory + ext.modid + "/lang/"
        //Check if the user has set custom regex, and if so, make this check for it
        if(ext.customRegex.isNotEmpty())
            regexDelimiter = "($regexDelimiter|${ext.customRegex})".toRegex()
        //If at least one lang file has been loaded
        var loadedSomething = false
        //Which lang files have been loaded
        val loadedFiles = arrayOf(false, false, false, false, false, false, false)
        //The array of lang files
        val inputFiles = arrayOf(File("null"), File("null"), File("null"), File("null"), File("null"), File("null"), File("null"))
        //If the output filename should be lowercase (Used for mods using resource pack format 3)
        var lowercaseOutput = false
        //Check if pack.mcmeta exists and the format is 3
        val mcmetaFile = File(mcmetaDirectory)
        if(mcmetaFile.exists()) {
            val metaInputStream = mcmetaFile.inputStream()

            //Set the output file to be lowercase if pack_format is 3
            //This is a quick and lazy way to do it, but it should work.
            metaInputStream.bufferedReader().useLines { allLines -> allLines.forEach {
                if(it.contains("pack_format") && it.contains('3'))
                    lowercaseOutput = true
            } }

            metaInputStream.close()
        }
        //Check for english language files
        for ((i, input) in possibleInputs.withIndex()) {
            val filePath = langDirectory + if (lowercaseOutput) input.toLowerCase() else input
            inputFiles[i] = File(filePath)
            if(ext.debug)
                println("Searching for lang file: $filePath")
            if (inputFiles[i].exists()) {
                loadedFiles[i] = true
                loadedSomething = true
                if(ext.debug)
                    println("Lang file found!")
            }
        }

        if (!loadedSomething)
            error("Unable to find an English language file!")

        var outFileName = "en_UD.lang"
        if (lowercaseOutput)
            outFileName = outFileName.toLowerCase()

        val outputFile = File(langDirectory + outFileName)
        //The lines to be added to the output file
        val outputLines = mutableListOf<String>()
        //The identifiers that have already been processed. Used to prevent duplicates from different localizations
        val usedIdents = mutableListOf<String>()

        //Process all the input files
        for((i, inputFile) in inputFiles.withIndex()) {
            //Return if that file was not found
            if(!loadedFiles[i])
                continue
            val fileInputStream = inputFile.inputStream()
            val lines = mutableListOf<String>()

            fileInputStream.bufferedReader().useLines { allLines -> allLines.forEach { lines.add(it) } }

            fileInputStream.close()

            for (line in lines) {
                //Check if the line should not be translated
                if (!line.contains('=')) {
                    if (line.isBlank())
                        outputLines.add("")//Preserve empty lines
                    else if (ext.copyInvalidLines)
                        outputLines.add(line)//Add the invalid line to the new file
                    continue//Skip improperly formatted lines
                }
                //Find and separate the identifier from the value
                val langIndex = line.indexOf('=') + 1
                val ident = line.substring(0, langIndex)
                val modLine = line.substring(langIndex)
                //Split the value into the non-regex parts
                val regexSplitValue = modLine.split(regexDelimiter)
                //Reverse the values
                val reversedRegexSplitValues = regexSplitValue.reversed()
                //Find the regex parts of the line by filtering out the non-regex parts
                var delimiters = mutableListOf<String>()
                for (result in regexDelimiter.findAll(modLine))
                    delimiters.add(result.value)
                //Fix duplicate regex values so they reverse order correctly
                val modDelimiters = delimiters
                for (delim in delimiters)
                    if (delim.startsWith("%") && delim.length == 2 && delimiters.indexOf(delim) != delimiters.lastIndexOf(delim)) {
                        var delimIndex = 1
                        for ((totalIndex, modDelim) in delimiters.withIndex()) {
                            if (modDelim == delim)
                                modDelimiters[totalIndex] = modDelim[0] + Integer.toString(delimIndex++) + '$' + modDelim[1]
                        }
                    }
                delimiters = modDelimiters
                //Reverse the regex values
                val reversedDelimiters = delimiters.reversed()
                //Merge delimiters with non-delimiters
                val mergedList = mutableListOf<String>()
                if (!reversedDelimiters.isEmpty() && modLine.endsWith(reversedDelimiters[0])) {
                    for ((curIndex, element) in reversedDelimiters.withIndex()) {
                        mergedList.add(element)
                        if (curIndex >= 0 && reversedRegexSplitValues.size > curIndex + 1)
                            mergedList.add(reversedRegexSplitValues[curIndex + 1])
                    }
                } else {
                    for ((curIndex, element) in reversedRegexSplitValues.withIndex()) {
                        mergedList.add(element)
                        if (curIndex >= 0 && reversedDelimiters.size > curIndex)
                            mergedList.add(reversedDelimiters[curIndex])
                    }
                }
                //Switch the color codes back
                val recoloredMerged = mutableListOf<String>()
                recoloredMerged.addAll(mergedList)
                val colorDelimIndeces = mutableListOf<Int>()
                for ((index, delim) in mergedList.withIndex())
                    if (delim.startsWith("§") && delim.length == 2)
                        colorDelimIndeces.add(index)
                if (colorDelimIndeces.size > 0) {
                    if (!recoloredMerged[0].startsWith("§") || recoloredMerged[0].length != 2)
                        recoloredMerged.add(0, "§f")
                    val newColorDelimIndeces = mutableListOf(-1)
                    newColorDelimIndeces.addAll(colorDelimIndeces)
                    newColorDelimIndeces.removeAt(newColorDelimIndeces.lastIndex)
                    for ((indexI, index) in newColorDelimIndeces.withIndex())
                        recoloredMerged[index + 1] = mergedList[colorDelimIndeces[indexI]]
                    recoloredMerged[colorDelimIndeces.last() + 1] = "§f"
                }
                //Create the reversed value to output
                var outModLine = ""
                for (section in recoloredMerged) {
                    if (section.matches(regexDelimiter))
                        outModLine += section
                    else {
                        var outSection = ""
                        for (char in section.reversed())
                            outSection += if (map.containsKey(char))
                                map[char]
                            else {
                                println("Warning: Character not in map: $char")
                                char
                            }
                        outModLine += outSection
                    }
                }
                //Output the new line
                outputLines += ident + outModLine
                usedIdents.add(ident)
            }
        }

        outputFile.printWriter().use { out ->
            for (line in outputLines)
                out.println(line)
        }
        println("$outFileName successfully created!")
    }
}
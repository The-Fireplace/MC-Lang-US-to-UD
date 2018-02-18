import java.io.File

val map = LinkedHashMap<Char, Char>()
val regexDelimiter = "\\\\.|%([0-9]\\\$)?[A-z]|§([0-9]|[a-f]|[k-o]|r)".toRegex()

fun setupMap() {
    val normal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()-_[]{};':\",.<>/?§+| "
    val inverse = "ⱯᗺƆᗡƎℲ⅁HIՐʞꞀWNOԀΌᴚS⟘∩ᴧMX⅄Zɐqɔpǝɟᵷɥᴉɾʞꞁɯuodbɹsʇnʌʍxʎz⥝ᘔƐᔭ59Ɫ860¡@#$%^⅋*)(-‾][}{؛,:„'˙></¿§+| "

    for(char in normal)
        map.put(char, inverse[normal.indexOf(char)])
}

fun main(args: Array<String>) {
    setupMap()
    val possibleInputs = listOf("en_GB.lang", "en_US.lang", "en_CA.lang", "en_NZ.lang", "en_AU.lang", "en_7S.lang")//list possible input lang files in order of preference.
    var loadedFile = false
    var loadedSecondary = false
    var lowercaseOutput = false
    var inputFile = File("null")
    var secondaryInputFile = File("null")
    for(input in possibleInputs) {
        inputFile = File(input)
        if(inputFile.exists()){
            //TODO: Check if filename is all lowercase here.
            loadedFile = true
            println("Language file found: "+input)
            break
        }
    }

    if(!loadedFile){
        for(input in possibleInputs) {
            inputFile = File(input.toLowerCase())
            if(inputFile.exists()){
                loadedFile = true
                lowercaseOutput = true
                println("Language file found: "+input)
                break
            }
        }
    }

    if(!loadedFile){
        println("Unable to find a valid language file!")
        return
    }

    if(!lowercaseOutput){
        for(input in possibleInputs) {
            secondaryInputFile = File(input)
            if(secondaryInputFile.exists() && secondaryInputFile != inputFile){
                loadedSecondary = true
                println("Secondary language file found: "+input)
                break
            }
        }
    }else{
        for(input in possibleInputs) {
            secondaryInputFile = File(input.toLowerCase())
            if(secondaryInputFile.exists() && secondaryInputFile != inputFile){
                loadedSecondary = true
                println("Secondary language file found: "+input)
                break
            }
        }
    }

    var outFileName = "en_UD.lang"
    if(lowercaseOutput)
        outFileName = outFileName.toLowerCase()

    val outputFile = File(outFileName)
    val outputLines = mutableListOf<String>()
    val usedIdents = mutableListOf<String>()

    val fileInputStream = inputFile.inputStream()
    val lines = mutableListOf<String>()

    fileInputStream.bufferedReader().useLines { allLines -> allLines.forEach { lines.add(it) } }

    fileInputStream.close()

    for(line in lines){
        if(!line.contains('=')) {
            if(line.isEmpty())
                outputLines.add("")//Preserve empty lines
            continue//Skip empty or improperly formatted lines
        }
        //Find and separate the identifier from the value
        val langIndex = line.indexOf('=')+1
        val ident = line.substring(0, langIndex)
        val modLine = line.substring(langIndex)
        //Split the value into the non-regex parts
        val regexSplitValue = modLine.split(regexDelimiter)
        //Reverse the values
        val reversedRegexSplitValues = regexSplitValue.reversed()
        //Find the regex parts of the line by filtering out the non-regex parts
        var delimiters = mutableListOf<String>()
        for(result in regexDelimiter.findAll(modLine))
            delimiters.add(result.value)
        //Fix duplicate regex values so they reverse order correctly
        val modDelimiters = delimiters
        for(delim in delimiters)
            if(delim.startsWith("%") && delim.length == 2 && delimiters.indexOf(delim) != delimiters.lastIndexOf(delim)){
                var delimIndex = 1
                for((totalIndex, modDelim) in delimiters.withIndex()) {
                    if (modDelim == delim)
                        modDelimiters[totalIndex] = modDelim[0] + Integer.toString(delimIndex++) + '$' + modDelim[1]
                }
            }
        delimiters = modDelimiters
        //Reverse the regex values
        val reversedDelimiters = delimiters.reversed()
        //Merge delimiters with non-delimiters
        val mergedList = mutableListOf<String>()
        if(!reversedDelimiters.isEmpty() && modLine.endsWith(reversedDelimiters[0])) {
            var curIndex=0
            for (element in reversedDelimiters) {
                mergedList.add(element)
                curIndex = reversedDelimiters.subList(curIndex, reversedDelimiters.lastIndex).indexOf(element)//Make sure to not get the same index if multiple of the same element are in the list
                if(curIndex >= 0 && reversedRegexSplitValues.size > curIndex)
                    mergedList.add(reversedRegexSplitValues[curIndex])
            }
        }else{
            var curIndex=0
            for (element in reversedRegexSplitValues) {
                mergedList.add(element)
                curIndex = reversedRegexSplitValues.subList(curIndex, reversedRegexSplitValues.lastIndex).indexOf(element)//Make sure to not get the same index if multiple of the same element are in the list
                if(curIndex >= 0 && reversedDelimiters.size > curIndex)
                    mergedList.add(reversedDelimiters[curIndex])
            }
        }
        //Create the reversed value to output
        var outModLine = ""
        for(section in mergedList){
            if(section.matches(regexDelimiter))
                outModLine += section
            else{
                var outSection=""
                for(char in section.reversed())
                    outSection += if(map.containsKey(char))
                        map.get(char)
                    else{
                        println("Error: Character not in map: "+char)
                        char
                    }
                outModLine += outSection
            }
        }
        //Output the new line
        outputLines += ident+outModLine
        usedIdents.add(ident)
    }

    if(loadedSecondary){
        val fileInputStream2 = secondaryInputFile.inputStream()
        val lines2 = mutableListOf<String>()

        fileInputStream2.bufferedReader().useLines { allLines -> allLines.forEach { lines2.add(it) } }

        fileInputStream2.close()

        outputLines.add("")//Add a newline to separate the two sections

        for(line in lines2){
            if(!line.contains('=')) {
                if(line.isEmpty())
                    outputLines.add("")//Preserve empty lines
                continue//Skip empty or improperly formatted lines
            }
            //Find and separate the identifier from the value
            val langIndex = line.indexOf('=')+1
            val ident = line.substring(0, langIndex)
            if(usedIdents.contains(ident))
                continue//Skip identifiers we've already used.
            val modLine = line.substring(langIndex)
            //Split the value into the non-regex parts
            val regexSplitValue = modLine.split(regexDelimiter)
            //Reverse the values
            val reversedRegexSplitValues = regexSplitValue.reversed()
            //Find the regex parts of the line by filtering out the non-regex parts
            var delimiters = mutableListOf<String>()
            for(result in regexDelimiter.findAll(modLine))
                delimiters.add(result.value)
            //Fix duplicate regex values so they reverse order correctly
            val modDelimiters = delimiters
            for(delim in delimiters)
                if(delim.startsWith("%") && delim.length == 2 && delimiters.indexOf(delim) != delimiters.lastIndexOf(delim)){
                    var delimIndex = 1
                    for((totalIndex, modDelim) in delimiters.withIndex()) {
                        if (modDelim == delim)
                            modDelimiters[totalIndex] = modDelim[0] + Integer.toString(delimIndex++) + '$' + modDelim[1]
                    }
                }
            delimiters = modDelimiters
            //Reverse the regex values
            val reversedDelimiters = delimiters.reversed()
            //Merge delimiters with non-delimiters
            val mergedList = mutableListOf<String>()
            if(!reversedDelimiters.isEmpty() && modLine.endsWith(reversedDelimiters[0])) {
                var curIndex=0
                for (element in reversedDelimiters) {
                    mergedList.add(element)
                    curIndex = reversedDelimiters.subList(curIndex, reversedDelimiters.lastIndex).indexOf(element)//Make sure to not get the same index if multiple of the same element are in the list
                    if(curIndex >= 0 && reversedRegexSplitValues.size > curIndex)
                        mergedList.add(reversedRegexSplitValues[curIndex])
                }
            }else{
                var curIndex=0
                for (element in reversedRegexSplitValues) {
                    mergedList.add(element)
                    curIndex = reversedRegexSplitValues.subList(curIndex, reversedRegexSplitValues.lastIndex).indexOf(element)//Make sure to not get the same index if multiple of the same element are in the list
                    if(curIndex >= 0 && reversedDelimiters.size > curIndex)
                        mergedList.add(reversedDelimiters[curIndex])
                }
            }
            //Create the reversed value to output
            var outModLine = ""
            for(section in mergedList){
                if(section.matches(regexDelimiter))
                    outModLine += section
                else{
                    var outSection=""
                    for(char in section.reversed())
                        outSection += if(map.containsKey(char))
                            map.get(char)
                        else{
                            println("Error: Character not in map: "+char)
                            char
                        }
                    outModLine += outSection
                }
            }
            //Output the new line
            outputLines += ident+outModLine
            usedIdents.add(ident)
        }
    }

    outputFile.printWriter().use { out ->
        for(line in outputLines)
            out.println(line)
    }
    println(outFileName+" successfully created!")
}
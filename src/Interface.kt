import java.io.File

fun main(arg: Array<String>) {

    var wm: WordMatcher = WordMatcher()
    val cw: ConsoleWrapper = ConsoleWrapper()
    var substring: String = ""
    var matches: MutableList<String> = mutableListOf<String>()
    var c: Char

    val BACKSPACE: Char = '\u0008'
    val DEL: Char = '\u007F'
    val ENTER: Char = '\u000D'

    cw.initConsole()

    cw.updateAndDisplayState(substring, matches)
    // TODO: create while loop, add more control characters
    mainloop@for(i in 1..4) {
        c = cw.readNextChar()
        when(c) {
            ENTER->break@mainloop
            DEL->substring=substring.dropLast(1)
            else->substring+=c
        }
        matches = wm.findMatchingWords(words = null, subString = substring)
        cw.updateAndDisplayState(substring,matches)
    }
    cw.resetConsole()
}

class ConsoleWrapper() {

    private val escCode: String = "\u001B"
    private val console = System.console()
    private val reader = console.reader()

    private var cursorPosition: Int = 1

    private val maxMatches: Int = 10

    private fun switchTerminalMode(rawMode: Boolean = false): Unit {
        // switches terminal to normal (line) mode
        var cmd = arrayOf("/bin/sh", "-c", "stty sane </dev/tty")
        if (rawMode)
        // switches terminal to raw (character) mode
            cmd = arrayOf("/bin/sh", "-c", "stty raw </dev/tty")

        // execute command
        Runtime.getRuntime().exec(cmd).waitFor()
    }

    fun initConsole(): Unit {
        switchTerminalMode(rawMode = true)
        eraseScreen()
        placeCursor(1,1)
    }

    fun resetConsole(): Unit {
        eraseScreen()
        placeCursor(1,1)
        switchTerminalMode(rawMode = false)
    }

    private fun eraseScreen(): Unit {
        print("${escCode}[2J")
    }

    private fun deleteLine(R: Int?): Unit {
        if (R != null) {
            placeCursor(R, 1)
            print("${escCode}[2K")
        }
    }

    private fun placeCursor(R: Int?, C: Int?): Unit {
        if (R != null || C != null) {
            print("${escCode}[${R};${C}f")
        } else {
            print("${escCode}[;H")
        }
    }

    private fun printlnAt(R: Int, line: String): Unit {
        placeCursor(R,1)
        println(line)
    }

    fun readNextChar(): Char {
        // read input and return
        return this.reader.read().toChar()
    }

    fun updateAndDisplayState(substring: String, matches: List<String>): Unit {
        // update internal state of cursor
        this.cursorPosition = substring.length+1

        // FIXME: find a less brutal way of preparing the screen for printing
        eraseScreen()
        printlnAt(1, substring)

        for (i in matches.indices) {
            if (i>this.maxMatches)
                break
            printlnAt(i+2, matches[i])
        }
        placeCursor(R=1,C=this.cursorPosition)
    }
}

class WordMatcher() {

    var words: List<String> = loadWords()

    private fun loadWords(): List<String> {
        val file = File("data/english-nouns.txt")
        return file.readLines()
    }

    fun findMatchingWords(words: List<String>?, subString: String): MutableList<String> {
        var matchingWords: MutableList<String> = mutableListOf<String>()

        var useWords: List<String> = this.words
        if (words!=null) {
            useWords = words
        }

        var score: Float
        for (word in useWords) {
            score = scoreSubMatch(subString, word)
            if(score==1.0f) {
                matchingWords.add(word)
            }
        }
        return matchingWords
    }

    fun scoreSubMatch(sub: String, word: String): Float {
        val ls: Int = sub.length
        val lw: Int = word.length
        if (lw >= ls && word.subSequence(0, ls) == sub)
            return 1.0f
        return 0.0f
    }
}
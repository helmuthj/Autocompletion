import java.io.File

fun main(arg: Array<String>) {

    var words: List<String> = loadWords()

    var substring: String = "dr"
    var matchingWords: MutableList<String> = findMatchingWords(words,substring)

    val cw: ConsoleWrapper = ConsoleWrapper()
    cw.switchTerminalMode(rawMode = true)
    cw.updateAndDisplayState(substring,matchingWords)
    var c = cw.readNextChar()
    cw.switchTerminalMode(rawMode = false)

    //switchTerminalMode(true)
    //eraseScreen()
    //interactive(words)
    //switchTerminalMode(false)

}

class ConsoleWrapper() {

    private val escCode: String = "\u001B"
    private val console = System.console()
    private val reader = console.reader()

    private var cursorPosition: Int = 1

    fun switchTerminalMode(rawMode: Boolean = false): Unit {
        // switches terminal to normal (line) mode
        var cmd = arrayOf("/bin/sh", "-c", "stty sane </dev/tty")
        if (rawMode)
        // switches terminal to raw (character) mode
            cmd = arrayOf("/bin/sh", "-c", "stty raw </dev/tty")

        // execute command
        Runtime.getRuntime().exec(cmd).waitFor()
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
            printlnAt(i+2, matches[i])
        }
        placeCursor(R=1,C=this.cursorPosition)
    }
}

fun loadWords(): List<String>{
    val file = File("data/english-nouns.txt")
    return file.readLines()
}

fun findMatchingWords(words: List<String>, subString: String): MutableList<String> {
    var matchingWords: MutableList<String> = mutableListOf<String>()
    val l1: Int = subString.length
    var l2: Int
    for (word in words) {
        l2 = word.length
        // lazy?
        // ask for permission or ask for forgiveness?
        if (l2>=l1 && word.subSequence(0,l1)==subString) {
            matchingWords.add(word)
        }
    }
    return matchingWords
}
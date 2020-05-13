import java.io.File

// TODO: implement UP/DOWN arrow if possible to select words
// TODO: rank matched words somehow
// TODO: get larger word list

// Helper for loading a dictionary from file
private fun loadDictionary(): List<String> {
    val file = File("data/english-nouns.txt")
    // Assumes exactly one word per line
    return file.readLines()
}

fun main(arg: Array<String>) {

    val cw: ConsoleWrapper = ConsoleWrapper()
    var substring: String = ""
    val matches: MutableList<String> = mutableListOf<String>()
    val maxMatches: Int = 10
    var subTreeExists: Boolean = false
    var c: Char

    val DEL: Char = '\u007F'
    val ENTER: Char = '\u000D'

    // load dictionary and prepare Trie
    val dictionary: List<String> = loadDictionary()
    val trie: Trie = Trie()
    trie.insertDictionary(dictionary)

    // prepare console
    cw.initConsole()
    cw.updateAndDisplayState(substring, matches)

    mainloop@while(true) {
        // read a character from user
        c = cw.readNextChar()
        when(c) {
            ENTER->break@mainloop
            DEL->substring=substring.dropLast(1)
            else->substring+=c
        }

        // clear matches and replace with new ones
        matches.clear()
        if(substring.isNotEmpty()) {
            subTreeExists = trie.querySubtree(substring)
            if (subTreeExists) {
                var count: Int = 0
                for (match in trie) {
                    if(count++<=maxMatches)
                        matches.add(match!!)
                    else
                        break
                }
            }
        }
        // update display
        cw.updateAndDisplayState(substring,matches)
    }

    // reset console back to original state
    cw.resetConsole()
}

// Wraps all console operations, such as printing, reading, placing cursor, managing the console state.
class ConsoleWrapper() {

    private val escCode: String = "\u001B"
    private val console = System.console()
    private val reader = console.reader()

    private var cursorPosition: Int = 1

    // switch console to character or line mode
    private fun switchTerminalMode(rawMode: Boolean = false): Unit {
        // switches terminal to normal (line) mode
        var cmd = arrayOf("/bin/sh", "-c", "stty sane </dev/tty")
        if (rawMode)
        // switches terminal to raw (character) mode
            cmd = arrayOf("/bin/sh", "-c", "stty raw </dev/tty")

        // execute command
        Runtime.getRuntime().exec(cmd).waitFor()
    }

    // init console
    fun initConsole(): Unit {
        switchTerminalMode(rawMode = true)
        eraseScreen()
        placeCursor(1,1)
    }

    // restore original state of console
    fun resetConsole(): Unit {
        eraseScreen()
        placeCursor(1,1)
        switchTerminalMode(rawMode = false)
    }

    // clear screen
    private fun eraseScreen(): Unit {
        print("${escCode}[2J")
    }

    // delete a line from console
    private fun deleteLine(R: Int?): Unit {
        if (R != null) {
            placeCursor(R, 1)
            print("${escCode}[2K")
        }
    }

    // place cursor somewhere in the console
    private fun placeCursor(R: Int?, C: Int?): Unit {
        if (R != null || C != null) {
            print("${escCode}[${R};${C}f")
        } else {
            print("${escCode}[;H")
        }
    }

    // print line into a given row
    private fun printlnAt(R: Int, line: String): Unit {
        placeCursor(R,1)
        println(line)
    }

    // read one character from console
    fun readNextChar(): Char {
        return this.reader.read().toChar()
    }

    fun updateAndDisplayState(substring: String, matches: List<String>): Unit {
        // update internal state of cursor
        this.cursorPosition = substring.length+1

        // clear screen, print query, print matches
        eraseScreen()
        printlnAt(1, substring)
        for (i in matches.indices) {
            printlnAt(i+2, matches[i])
        }

        // place cursor behind last character of query
        placeCursor(R=1,C=this.cursorPosition)
    }
}
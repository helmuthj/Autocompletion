import java.lang.StringBuilder
import java.util.Scanner

fun main(arg: Array<String>) {
    //printSq()
    switchTerminalMode(true)
    eraseScreen()
    interactive()
    switchTerminalMode(false)
}

const val escCode: String = "\u001B"

fun switchTerminalMode(rawMode: Boolean = false): Unit {
    // switches terminal to normal (line) mode
    var cmd = arrayOf("/bin/sh", "-c", "stty sane </dev/tty")
    if (rawMode)
        // switches terminal to raw (character) mode
        cmd = arrayOf("/bin/sh", "-c", "stty raw </dev/tty")

    // execute command
    Runtime.getRuntime().exec(cmd).waitFor()
}

fun eraseScreen(): Unit {
    print("${escCode}[2J")
}

fun deleteLine(R: Int?): Unit {
    if (R != null) {
        placeCursor(R, 1)
        print("${escCode}[2K")
    }
}

fun placeCursor(R: Int?, C: Int?): Unit {
    if(R != null || C != null) {
        print("${escCode}[${R};${C}f")
    }
    else {
        print("${escCode}[;H")
    }
}

fun printSq(): Unit {
    // prints square at 1,1 cursor position

    // print N by M "x"'s
    val N: Int = 5
    val M: Int = 5
    for (i in 1..N) {
        for (j in 1..M) {
            placeCursor(i,j)
            print("x")
        }
    }
}

fun interactive(): Unit {

    val console = System.console()
    val reader = console.reader()
    var charnum: Int
    var word: String = ""

    for (i in 1..3) {
        placeCursor(1,i)
        charnum = reader.read()
        word += charnum.toChar()

        placeCursor(1,1)
        println(word)

        deleteLine(2)
        deleteLine(3)
        deleteLine(4)
        placeCursor(2,1)
        println(word+"_X")
        placeCursor(3,1)
        println(word+"_YZ")
        placeCursor(4,1)
        println(word+"_XYZ")
    }
}
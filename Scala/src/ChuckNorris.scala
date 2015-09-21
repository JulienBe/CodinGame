import math._
import scala.util._

object Solution extends App {
    val message = readLine
    var toTranslate = ""
    message.foreach(c => toTranslate += to7bits(c.toInt.toBinaryString))
    
    def to7bits(s: String): String = {
        var converted = s
        while (converted.length() < 7)
            converted = "0" + converted
        return converted
    }
    
    translateChar(toTranslate)
    
    def translateChar(s: String) {
        var previous = s.charAt(0)
        var cpt = 0
        var message = ""

        for (i <- 1 until s.length) {
            val b = s.charAt(i)
            cpt += 1

            if (b != previous) {
                message += addDataToMessage(previous, cpt)
                message += " "
                cpt = 0
            }
            previous = b
        }
        cpt += 1
        message += addDataToMessage(previous, cpt)
                    
        println(message)
    }
    
    def addDataToMessage(previous: Char, cpt: Int): String = {
        var msg = ""
        if (previous == '0')    msg += "00 "
        else                    msg += "0 "
        for (j <- 0 until cpt)  msg += "0"
        msg
    }
}

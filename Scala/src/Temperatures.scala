import math._
import scala.util._

object Solution extends App {
    val n = readInt // the number of temperatures to analyse
    
    if (n == 0) println(0)
    else processTemperatures()
    
    def processTemperatures() {
        val temps = readLine // the N temperatures expressed as integers ranging from -273 to 5526
        var closest : Int = 5527

        temps.split(" ").foreach(
            (s: String) => closest = processSingleTemp(s.toInt, closest)
        )
        println(closest)
    }
    
    def processSingleTemp(i: Int, closest: Int): Int = {
        if (Math.abs(i) < Math.abs(closest))
            return i
        else if (Math.abs(i) == Math.abs(closest) && i > 0)
            return i
        return closest
    }
}

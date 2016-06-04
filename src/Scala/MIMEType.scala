package Scala

import math._
import scala.util._

object Solution extends App {
    val n = readInt // Number of elements which make up the association table.
    val q = readInt // Number Q of file names to be analyzed.
    val associations = collection.mutable.Map.empty[String,String]
    
    for(i <- 0 until n) {
        val splitted = readLine.split(" ")
        associations += splitted(0).toLowerCase -> splitted(1)
    }
    
    for(i <- 0 until q)
        printMime(readLine)

    def printMime(line: String) {
        val splitted = line.toLowerCase.split('.')
        if (line.charAt(line.size - 1) != '.' &&
                splitted.size > 1 &&
                associations.contains(splitted(splitted.size - 1)))
            println(associations(splitted(splitted.size - 1)))
        else
            println("UNKNOWN")
    }
}

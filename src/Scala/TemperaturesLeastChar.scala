package Scala

object Solution extends App{if(readInt!=0)println(readLine.split(" ").map(_.toInt).sortWith(_>_).minBy(math.abs))else print(0)}

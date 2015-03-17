object Player {

  def main(args: Array[String]) {
    while (true) {

      val enemy1 = new Enemy(readLine(), readInt())
      val enemy2 = new Enemy(readLine(), readInt())

      if (enemy1.dist < enemy2.dist)  println(enemy1.name)
      else                            println(enemy2.name)
    }
  }

}

class Enemy(val name: String, val dist: Int)
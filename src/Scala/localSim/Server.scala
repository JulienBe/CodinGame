package Scala.localSim

abstract class Server(clients: List[Client]) {

  def start() = {
    client.foreach(sendStartData(_))
    while (!isFinished)
      act
    end  
  }
  
  def act
  def end
  
  def sendStartData(client: Client)
  def isFinished: Boolean
}

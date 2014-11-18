package TwitterClient


import akka.actor._
import akka.routing._
import java.security.MessageDigest
import common._
import scala.io.Source
import java.io._


case class SendTweet (msg: String, id : Int,  remote : ActorRef) 

    
object Client{
  
  def main(args: Array[String]){
	  println("****************************************************************************************")
	   
	   implicit val system = ActorSystem("LocalSystem")
	   val bossActor = system.actorOf(Props(new ClientMaster (args(0))), name = "ClientMaster")
	   
	   //below line for hard coded IP address
	   //val bossActor = system.actorOf(Props[ClientMaster], name = "ClientMaster")
	   
	  System.setProperty("java.net.preferIPv4Stack", "true")
	   println("****************************************************************************************")
	   bossActor  ! Message ("Connected")
   }
}

class ClientMaster (ip: String) extends Actor {
//class ClientMaster extends Actor {

	val remote = context.actorFor("akka.tcp://WorkerSystem@"+ip+":5570/user/ServerMaster")
	//below line for hard coded IP
    //val remote = context.actorSelection("akka.tcp://WorkerSystem@192.168.0.29:5570/user/ServerMaster")
	val clientworker = context.actorOf(Props[ClientWorker].withRouter(RoundRobinRouter(4)), name = "ClientWorkers")
	var counter = 0 
	var i = 0
  

  def receive = {
	  
	case Message (msg) => 
	  	println (msg)
	  		
	  	var i = 0;
	  	if (msg == "ready"){
	  	  
	  	  for ( i <- 0 until 4) {
	  	    
	  	    clientworker ! SendTweet ("ready", i, remote)
	  	    
	  	  }	  	  
	  	  
	  	}	  		 
	  	else 
	  		remote ! Message (msg)    
	        
  }
}

class ClientWorker extends Actor {  
  
  var clientid = 1
  var buffer = Array [Char] (140)
  var bufsize:Int = 140
  
 def receive = {
    
    
    case SendTweet (msg, i, remote) =>
	  
      clientid = i
	  println ("In Acknowledge")
	  println (msg)
	  if (msg == "ready"){
	    
	  	//var buffer = Array [Char] (140);	    
		//Source.fromFile("twitter.txt", bufsize).foreach{ 
		  for (buffer <- Source.fromFile("twitter.txt").getLines() ){
			remote ! TweetTransmit (clientid, buffer)
		}
	  }
      
      remote ! Message ("done")
      
  }
  
}

/*class HashGenerator { 
	
	def GetHash(countStart: Int, countEnd : Int, pattern : StringBuffer) : Outputdetail = {
    
		val md = MessageDigest.getInstance("SHA-256");
		var i = 0
		var outputBitCoin: String = ""
		var outputcount = 0
  	    
  	  	for (i <- countStart until countEnd){ 
  	    	
  	  		
  	  		val mdbytes = md.digest(("rkmalik" + i.toString()).getBytes);
  	  		val sb = new StringBuffer();
  	  		//var currentBitCoin = HexOfBuffer(mdbytes)
  	  		sb.append(HexOfBuffer(mdbytes))
  	  		
  	  		
  	  		if (sb.indexOf(pattern.toString()) == 0){
  	  		  
  	  			//println ("GetHash : Using Index of Zeros " + sb.toString())
  	  			outputBitCoin = outputBitCoin  + "\n" + ("rkmalik" + i.toString()) + "\t" + sb.toString(); 
  	  			outputcount = outputcount + 1
  	  		}
	  			
  	  	}		
		
		// Output the Payload of Bit Coins mined to 
		Outputdetail (outputcount, outputBitCoin)
  }
  
  // Definition of new Method HexOfBuffer X is used to get hex code in capital letters while 
  // x (small x is used to get the hex value in small letters
  def HexOfBuffer(buf: Array[Byte]): String = {
    buf.map("%02x" format _).mkString
  }  
}
* 
* */


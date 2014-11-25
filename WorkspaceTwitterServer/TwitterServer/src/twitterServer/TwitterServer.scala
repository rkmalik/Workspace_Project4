package twitterServer

//package server

import akka.actor._
import akka.routing._
import java.security.MessageDigest
import scala.concurrent.duration._
//import messages._
import scala.math._
import java.io._
import java.io.PrintStream
import java.io.FileOutputStream
import scala.io.Source
import common._

case class DurationInfo(totalbitcoins : Int, duration : Duration )
case class 	GenerateTweets(start : Int, nrofElements: Int)
case object MineTweets

 //case class TweetTransmit (msg: String)
 //case class Acknowledge(msg: String)

object TwitterServer{
    //System.setOut(new PrintStream(new java.io.FileOutputStream("TweetOutput.txt")))
    def main(args: Array[String]){
      //val writer = new PrintWriter(new File("TwitterOP.txt" ))

      //writer.write("Hello there!my first tweet")
      //writer.close()
  	 // val numOfZeros = args (0).toInt
      val numOfTweets=args(0).toInt
	  val pattern = new BuildPattern
	  val system = ActorSystem("WorkerSystem")
	  // val timeractor = system.actorOf(Props[TimerActor], name="TimerActor")
	  //val ServerMaster = system.actorOf(Props(new ServerMaster(pattern.getPattern(numOfZeros), timeractor)), name = "ServerMaster")
	 //original line below from bitcoin
	  //val ServerMaster = system.actorOf(Props(new ServerMaster(pattern.getPattern(numOfZeros))), name = "ServerMaster")
	   val ServerMaster = system.actorOf(Props(new ServerMaster(pattern.getPattern(numOfTweets))), name = "ServerMaster")
	  System.setProperty("java.net.preferIPv4Stack", "true")
	 /*
	  ServerMaster ! MineBitcoins
	  ServerMaster ! MineBitcoins
	  ServerMaster ! MineBitcoins
	  ServerMaster ! MineBitcoins
	  */
	  ServerMaster ! MineTweets
	  ServerMaster ! MineTweets
	  ServerMaster ! MineTweets
	  ServerMaster ! MineTweets

  }
}

class BuildPattern {
  
  def getPattern (numOfZeros : Int) : StringBuffer = {
    
    // Develop starting pattern 
	  val i = 0
	  //var pattern = new StringBuffer ()
	  var pattern = new StringBuffer ()

	  for (i <- 0 until numOfZeros) {
		  pattern.append("0".toString ())
	  }
	  pattern
  }  
}

// This class takes the number or zeros to be mined 
class ServerMaster (pattern: StringBuffer) extends Actor {
  

	var start = 0
	var timerstart: Long=System.currentTimeMillis
	var timerend : Long = 0
	var duration : Long = 0
	var nrOfElements = 20000
	var remoteElements = nrOfElements *4
	//var totalbitcoins = 0
	var totaltweets = 0
	val serverworker = context.actorOf(Props (new ServerWorker).withRouter(RoundRobinRouter(4)), name = "LocalWorkers")
	 
	def receive = {
		
	  case msg =>
	    if(msg == "Connected"){
	      println (msg)
	      //sender ! Acknowledge ("")
	      sender ! "ready"
	      
	}else
	{
	  println(msg)
	} 
	  case ServerWorkerResp (result, msg) => 		  	

			if (result> 0) {
				println ("")
				println ("Number of Bit Coins Mined by server workers  " + result)
				println(s"Server Workers output Bit Coins : '$msg'")			  
			}
			
			//totalbitcoins = totalbitcoins + result
			totaltweets = totaltweets + result
			/*
			if (totalbitcoins < 100) {			  
				
				sender ! MineBitcoinsWithinRange(start, nrOfElements, pattern)
				start = start + nrOfElements				
			} else {
			  println ("")
			  println ("Server Master Minded the Total " + totalbitcoins)
			  println ("STOP")
			}
			* 
			*/
			if(totaltweets < 100){
			  sender ! GenerateTweets(start, nrOfElements)
			  start = start + nrOfElements
			  }else{
			    
			  }
		
		case ClientToServerResp (resultcount, msg) =>
	
		  	if (resultcount> 0) {
		  		println ("")
				println ("Number of Bit Coins Mined by Remote Client Workers  " + resultcount)
				println(s"Remote Client Workers output Bit Coins : '$msg'")		  
			}

			//totalbitcoins = totalbitcoins + resultcount
			
			/*
			if (totalbitcoins < 100) {			  

				sender ! ServerToClientReq (start, remoteElements, pattern)
				start = start + remoteElements
				
			} else {
			  
			  println ("STOP")
			  timerend = System.currentTimeMillis
			  duration = timerend - timerstart 
			  //timeractor ! DurationInfo(totalbitcoins, duration)
			  println ("Total Time taken to mine " + totalbitcoins + " = " + duration + " MiliSeconds.")
			}
			*/
	/*  
		case MineBitcoins =>
			serverworker ! MineBitcoinsWithinRange(start, nrOfElements, pattern)
	        start = start + nrOfElements
		*/
		case MineTweets =>
		    serverworker ! GenerateTweets(start,nrOfElements)
		case ClientToServer(msg) =>

			println (s"Message From Client : '$msg'")

			sender ! ServerToClientReq (start, remoteElements, pattern)
			start = start + remoteElements
			
		case msg =>
		   println(msg)
			
  	}
}

/*
class TimerActor extends Actor {
	def receive=
	{
		case DurationInfo(message,duration) =>
		println(msg)
		println(duration)
	}
}*/

// This the worker actor takes the number of Zeros to be found in the given range and return the 
// output payload of the mined bit coins
class ServerWorker extends Actor {
  
	def receive = {
    	case MineBitcoinsWithinRange (start, nrOfElements, pattern) =>
    	  
    		//val hg = new HashGenerator ()

    		//var out : Outputdetail = hg.GetHash(start, start + nrOfElements, pattern)
    		
    		//sender ! ServerWorkerResp (out.result , out.bitCoins)   	
  }  
}




/*
class HashGenerator { 
	
	def GetHash(countStart: Int, countEnd : Int, pattern : StringBuffer) : Outputdetail = {
    
		val md = MessageDigest.getInstance("SHA-256");
		var i = 0
		var outputBitCoin: String = ""
		var outputcount = 0
  	    
  	  	for (i <- countStart until countEnd){  	    	
  	  		
  	  		val mdbytes = md.digest(("rkmalik" + i.toString()).getBytes);
  	  		val sb = new StringBuffer();
  	  		sb.append(HexOfBuffer(mdbytes)) 	  		
  	  		
  	  		if (sb.indexOf(pattern.toString()) == 0){
  	  		  
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
*/

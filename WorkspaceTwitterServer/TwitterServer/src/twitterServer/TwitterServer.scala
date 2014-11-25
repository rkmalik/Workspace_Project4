package twitterServer

//package server
import akka.actor._
import akka.routing._
import java.security.MessageDigest
import scala.concurrent.duration._
import scala.math._
import java.io._
import java.io.PrintStream
import java.io.FileOutputStream
import scala.io.Source
import common._
import collection.mutable
import collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.StringBuilder
import java.util.Hashtable

case class currenttweetcount(tweetcount:Int , msg:String)
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
	  val ServerMaster = system.actorOf(Props(new ServerMaster(pattern.getPattern(numOfTweets))), name = "ServerMaster")
	  System.setProperty("java.net.preferIPv4Stack", "true")
	
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


    var hashmap = new HashMap[Int,ArrayBuffer[StringBuffer]]() //{ override def default(key:Int = "-" }
	var hashint = new HashMap[Int,Int]() 
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
      
      
	  case TweetTransmit(clientid,message) =>
	  	
	    var tweetcount = 0
	    var tweetbuf = new ArrayBuffer [StringBuffer]
	    tweetbuf = hashmap.getOrElse(clientid, tweetbuf)
	    var buf = new StringBuffer (message)
	    tweetbuf += buf
	    hashmap.put(clientid, tweetbuf)
	    tweetcount = hashint.getOrElse(clientid, tweetcount)
	    hashint.put(clientid, tweetcount)
	    //hashtable.pput(clientid, )
	  
	  

	    case Message(msg) =>
	      val msgcount = 0 
	      if(msg == "Connected")
	      {
	    	   println (msg)
	    	   //sender ! Acknowledge ("")
	    	   sender ! Message ("ready")
		      if(msgcount==10)
	          {
	             println("number of tweets is 10 now")
	          }
	       }else if (msg == "done"){
	          println("got the tweets as per clientid:")
	    	   for ((k,v) <- hashmap) {
	    		   var tweetcount = 0
	    		   tweetcount = hashint.getOrElse(k, tweetcount)
	    		   println ("Total Number of Tweets for " + tweetcount + "  For client  " + k)
	    		   
	    		   var buff  = new StringBuffer 
	    		   var it = hashmap.iterator 
	    		   
	    		   it.foreach{
	    		     
	    		     println 
	    		     
	    		   }
	    		   
	   /* 		   for (buff <- it.hasNext) {
	    		     
	    		     println (it.)
	    		     it++
	    		     
	    		   }
	    		   println(s"key: " + k + ", value: " + v)*/
	    	   }
	       }
	    
	    case currenttweetcount(tweetcount,msg) =>
	      if(tweetcount == 10){
	        println("number of tweets till now:"+tweetcount)
	      }
	      
	    case ServerWorkerResp (result, msg) => 		  	

			if (result> 0) {
				println ("")
				println ("Number of Bit Coins Mined by server workers  " + result)
				println(s"Server Workers output Bit Coins : '$msg'")			  
			}
			
					
		case ClientToServerResp (resultcount, msg) =>
	
		  	if (resultcount> 0) {
		  		println ("")
				println ("Number of Bit Coins Mined by Remote Client Workers  " + resultcount)
				println(s"Remote Client Workers output Bit Coins : '$msg'")		  
			}

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


// This the worker actor takes the number of Zeros to be found in the given range and return the 
// output payload of the mined bit coins
class ServerWorker extends Actor {
  
	def receive = {
	  
	     case MineBitcoinsWithinRange (start, nrOfElements, pattern) =>
    	  
  }  
}
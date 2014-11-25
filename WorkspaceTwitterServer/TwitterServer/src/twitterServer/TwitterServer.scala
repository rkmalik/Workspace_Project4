package twitterServer

//package twitterServer

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




case class Tweeting (clientid : Int,  msg: String, clientsender: ActorRef)

object TwitterServer{
  sealed trait TraitMessage
  case object Stop extends TraitMessage
  
  	//System.setOut(new PrintStream(new java.io.FileOutputStream("TweetOutput.txt")))
	val corecount =  Runtime.getRuntime().availableProcessors()
	
    def main(args: Array[String]){
      //val numOfTweets=args(0).toInt
	  val system = ActorSystem("TwitterServer")
	  
	  println ("Total Cores in this machin is " + corecount)
	  val serverworker = system.actorOf(Props (new TwitterServerWorker /*(totaltweets, tweetdata) */).withRouter(RoundRobinRouter(corecount)), name = "TwitterSeverMaster")  
	  
	  val router = system.actorOf(Props(new TwitterRouter(serverworker)), name = "TwitterRouter")
	  System.setProperty("java.net.preferIPv4Stack", "true")
	  
	  
  }		
  
  
  class TwitterServerWorker /*(totaltweets: Int , tweetdata:ArrayBuffer [ArrayBuffer[String]])*/ extends Actor {

	def receive = {      
      
	  case Tweeting(clientid,message, sender) =>

	    // We will be handling the server side data on the later stage
	    // As of now we are just handling the numbe of tweets
	    /*var tweetcount = 0
	    var tweetbuf = new ArrayBuffer [StringBuffer]
	    tweetbuf = hashmap.getOrElse(clientid, tweetbuf)
	    var buf = new StringBuffer (message)
	    tweetbuf += buf
	    hashmap.put(clientid, tweetbuf)
	    println("clientid:" +clientid)
	    println("mesages:" +message)
	    tweetcount = hashint.getOrElse(clientid, tweetcount)
	    hashint.put(clientid, tweetcount)
	     */
	     
	    	println ("Client id : " + clientid + "  Tweet : " + message)
	  		/*tweetdata (clientid) += message
	  		if (tweetcount >= 100) {	  		  
	  		  for (i <- 0 until 100){
	  		    var it2 = 0
	  		    while (it2 <  tweetdata(i).length) {
	  		      
	  		      println ((tweetdata (i))(it2))
	  		      
	  		    }	  		    
	  		    
	  		  }  		  
	  		  //context.stop(self)   		  
	  		}*/
	  		
	  

	    /*case MessageFromServer (msg, clientsender) =>
	      val msgcount = 0 
	      println ("In Server Woker " + msg)
	      if(msg == "Connected")
	      {
	    	   println ("In Server Woker " + msg)
	    	   //sender ! Acknowledge ("")
	    	  println("sending ready")	
	    	   clientsender ! "ready"
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
	    		   
	    	   }
	       }*/
		
  	}
}

  
  class TwitterRouter (TwitterServerWorker:ActorRef) extends Actor {
		  
			var timerstart: Long=System.currentTimeMillis
			var timerend : Long = 0
			var duration : Long = 0
			var nrOfTweets= 0
			
			
			  def receive = {
			    
			  
			  case Stop =>
			    println ("Total Numbe of Tweets = " + nrOfTweets + "  " + duration + " MiliSeconds.")
			    println ("Closing the Connection")
			    context.stop(self)
			  
			    case msg : String => 
			    
			      println ("In Router " + msg)
			      sender ! "ready"
			      
			      // No futher call from the server master to client		      
			      //TwitterServerWorker ! (sender, msg)
			      //TwitterServerWorker ! TweetTransmit(clientid,message, sender)
			      
			   
			    case TweetTransmit(clientid, message) =>
			      //println (clientid + " " + message)
			      // Pass this message to the worker.
			     nrOfTweets += 1 
			     timerend = System.currentTimeMillis
			     duration = timerend - timerstart 
				  if (duration >= 30000) {
					  self ! Stop
				  } else 
					  TwitterServerWorker ! Tweeting(clientid, message, sender)
			  }
			  
			  
		  
	      }	  
	
	}

/*
class TwitterRouter (TwitterServerWorker:ActorRef) extends Actor {
	  
		var timerstart: Long=System.currentTimeMillis
		var timerend : Long = 0
		var duration : Long = 0
		var nrOfTweets= 0
		
  
		  def receive = {
		    
		  
		  case Stop =>
		    context.stop(self)
		  
		    case msg : String => 
		    
		      println ("In Router " + msg)
		      sender ! "ready"
		      
		      // No futher call from the server master to client		      
		      //TwitterServerWorker ! (sender, msg)
		      //TwitterServerWorker ! TweetTransmit(clientid,message, sender)
		      
		   
		    case TweetTransmit(clientid, message) =>
		      //println (clientid + " " + message)
		      // Pass this message to the worker.
		      nrOfTweets++
		      timerend = System.currentTimeMillis
			  duration = timerend - timerstart 
			  println ("Total Numbe of Tweet = " + nrOfTweets + "  " + duration + " MiliSeconds.")
		      TwitterServerWorker ! Tweeting(clientid, message, sender)
		  }
		  
		  
	  
      }	  
*/


package TwitterClient
import akka.actor._
import akka.routing._
import java.security.MessageDigest
import common._
import scala.io.Source
import java.io._
import scala.concurrent.duration._
import scala.collection.mutable.ArrayBuffer
import scala.util.Random



case class TweetRef (remote : ActorRef) 

    
object Client{
  
  def main(args: Array[String]){
	   
	   implicit val system = ActorSystem("TwitterClient")
	   val bossActor = system.actorOf(Props(new TwitterClientMaster (args(0), system)), name = "TwitterClientMaster")	   
	   //below line for hard coded IP address
	   //val bossActor = system.actorOf(Props[TwitterClientMaster], name = "TwitterClientMaster")	   
	  System.setProperty("java.net.preferIPv4Stack", "true")
	   println("****************************************************************************************")
	   
	    bossActor  ! "Connected"
   }
}

//class TwitterClientMaster (ip: String, system: ActorSystem) extends Actor {
//class TwitterClientMaster extends Actor {
class TwitterClientMaster (ip: String, system: ActorSystem) extends Actor {  
	val remote = context.actorFor("akka.tcp://TwitterServer@"+ip+":5570/user/TwitterRouter")
	//val TwitterClientWorker = context.actorOf(Props(new TwitterClientWorker (1)).withRouter(RoundRobinRouter(100000)), name = "TwitterClientWorkers")
	//var counter = 0 
	
	var clientcount = 10000
	var i = 0	
	val tweetclients = new ArrayBuffer[ActorRef]()
    for(i<-0 to clientcount)
  	{
  		tweetclients += system.actorOf(Props(new TwitterClientWorker (i)), name = "clientworker"+i)
  	}
	
	
	val b = System.currentTimeMillis;
	

	//system.scheduler.schedule(0 milliseconds,10 milliseconds,TwitterClientWorker(i),SendTweet ("ready", i, remote))

  def receive = {
	  
	  case msg: String =>
	 
	    // If I got the message as connected from main then send this to the Twitter Server 
	    // and wait for the ready message
	    //println (msg)
	    if (msg == "Connected") {
	     
	      	     remote !  msg
	      
	     // Else if I have got the ready message from the remote server then I will generate the threads 
	    } else if (msg == "ready") {
	      println ("received ready")
	      
	    	while(true)
			{
				var i = Random.nextInt(clientcount)
				tweetclients(i) ! TweetRef (sender)
			}
	      
	    }
	     
	        
  }
}

class TwitterClientWorker (id: Int)extends Actor {    
	var clientid = id
	//var buffer = Array [Char] (140)  
	var tweetMsg = new StringBuilder   
	var bufsize:Int = 140
	
 def receive = {       
	  
    case TweetRef (sender) =>  
	    
		  //for (tweetMsg <- Source.fromFile("twitter.txt").getLines() ){
			//sender ! TweetTransmit (clientid, tweetMsg)
		//}

      
      	sender ! TweetTransmit (clientid, "Hello Twitter")
  }  
}

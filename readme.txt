
1. This Project will require the all the Akka library Jars to be included.
2. common.jar contains all the common case class declaration used by the client and the server.
3. The following under VM arguments:-Dakka.remote.netty.tcp.port=5570(the port number can be anything depending on the availability)
4. In the client arguement we need to give the IP address of the Server

Application Properties
	
	Project : 			TwitterClient
	Main Class : 		TwitterClient.Client
	Program Argument : 	192.168.0.30
	VM Argument : 		-Dakka.remote.netty.tcp.port=5570
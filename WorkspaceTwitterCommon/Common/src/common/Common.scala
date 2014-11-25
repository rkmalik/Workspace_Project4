package common

import java.util.ArrayList

case class TweetTransmit (clientid: Int, buffer: String)
case class UserData (clientid: Int)
case class Acknowledge (msg: String)
case class 	Message (msg : String)


case class 	MineBitcoinsActor(start : Int, nrofElements: Int)
    case class 	MineBitcoinsWithinRange(start : Int, nrofElements: Int, pattern : StringBuffer)
    case class  ServerToClient (msg : String)
    case class  ClientToServer (msg : String)
    case class  ServerToClientReq (start : Int, nrofElements: Int, pattern : StringBuffer)
	case class  ClientToServerResp (result : Int, bitCoins : String)
	case class 	Outputdetail (result : Int, bitCoins : String)
	case class 	ServerWorkerResp (result : Int, bitCoins : String)
    case object MineBitcoins

    

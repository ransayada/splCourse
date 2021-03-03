package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {
        Database.getInstance().initialize("./Courses.txt");
        int port = Integer.parseInt(args[0]);
        int numOfThreads = Integer.parseInt(args[1]);
        Server.reactor(
                numOfThreads,
                port, //port
                () -> new CRSMessagingProtocol(), //protocol factory
                ()->new CRSMsgEncoderDecoder() //message encoder decoder factory
        ).serve();
    }
}

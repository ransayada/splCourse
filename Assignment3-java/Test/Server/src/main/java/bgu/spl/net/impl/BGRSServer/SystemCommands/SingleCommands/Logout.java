package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.*;

public class Logout extends ClientCommand {
    

    public Logout(){super(4);}

    @Override
    public ServerCommand execute() {
        Database db = Database.getInstance();
        boolean result = db.logOutUser(userName);
        if (result) return succAction();
        else return error();
    }
  
}

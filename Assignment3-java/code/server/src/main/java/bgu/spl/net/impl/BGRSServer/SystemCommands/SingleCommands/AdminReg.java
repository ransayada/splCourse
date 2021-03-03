package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.AdminCommand;
import bgu.spl.net.impl.BGRSServer.SystemCommands.ServerCommand;

public class AdminReg extends AdminCommand {
    String password;
    

    public AdminReg(String user, String pass) {
        super(1);
        userName = user;
        password = pass;
    }

    @Override
    public ServerCommand execute() {
        Database db = Database.getInstance();
        boolean result = db.registerNewUser(userName, password,true);
        if (result) return succAction();
        else return error();
    }
}

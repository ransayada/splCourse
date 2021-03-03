package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.ServerCommand;
import bgu.spl.net.impl.BGRSServer.SystemCommands.StudentCommand;

public class StudentReg extends StudentCommand {
    private String password;
    

    public StudentReg(String user, String pass) {
        super(2);
        userName = user;
        password = pass;
    }

    @Override
    public ServerCommand execute() {
        Database db = Database.getInstance();
        boolean result = db.registerNewUser(userName, password,false);
        if (result) return succAction();
        else return error();
    }
}

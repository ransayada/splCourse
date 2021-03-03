package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.ServerCommand;
import bgu.spl.net.impl.BGRSServer.SystemCommands.StudentCommand;

import java.util.NoSuchElementException;

public class IsRegistered extends StudentCommand {
    private int courseNumber;
   

    public IsRegistered(int courseNumber) {
        super(9);
        this.courseNumber = courseNumber;
    }

    @Override
    public ServerCommand execute() {
        Database db = Database.getInstance();
        boolean result;
        try {
            result = db.isRegisteredForCourse(userName,courseNumber);
        } catch (NoSuchElementException e) {
            return error();
        }
        ACK response = succAction();
        if(result) response.setOptionalMsg("REGISTERED");
        else  response.setOptionalMsg("NOT REGISTERED");
        return response;
    }
    
}

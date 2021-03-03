package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.ServerCommand;
import bgu.spl.net.impl.BGRSServer.SystemCommands.StudentCommand;

import java.util.NoSuchElementException;

public class MyCourses extends StudentCommand {

    

    public MyCourses() {
        super(11);
    }

    @Override
    public ServerCommand execute() {
        Database db = Database.getInstance();
        String courses;
        try {
            courses = db.getMyCourses(userName);
        } catch (NoSuchElementException e) {
            return error();
        }
        ACK response = succAction();
        response.setOptionalMsg(courses);
        return response;
    }
  
}

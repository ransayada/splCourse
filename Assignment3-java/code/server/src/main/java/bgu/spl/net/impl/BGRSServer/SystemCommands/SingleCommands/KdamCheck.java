package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.ServerCommand;
import bgu.spl.net.impl.BGRSServer.SystemCommands.StudentCommand;

import java.util.NoSuchElementException;

public class KdamCheck extends StudentCommand {
    private int courseNumber;
   

    public KdamCheck(int courseNumber) {
        super(6);
        this.courseNumber = courseNumber;
    }

    @Override
    public ServerCommand execute() {
        Database db = Database.getInstance();
        String kdam;
        try {
            kdam = db.getKdamForCourse(courseNumber);
        } catch (NoSuchElementException e) {
            return error();
        }
       ACK response = succAction();
        response.setOptionalMsg(kdam);
        return response;
    }
    
}

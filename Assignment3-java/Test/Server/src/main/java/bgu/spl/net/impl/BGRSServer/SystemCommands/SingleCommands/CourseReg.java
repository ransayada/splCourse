package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.ServerCommand;
import bgu.spl.net.impl.BGRSServer.SystemCommands.StudentCommand;

public class CourseReg extends StudentCommand {
    private int courseNumber;
    public CourseReg(int courseNumber) {
        super(5);
        this.courseNumber=courseNumber;
    }

    @Override
    public ServerCommand execute() {
        Database db = Database.getInstance();
        boolean result = db.registerToCourse(userName,courseNumber);
        if (result) return succAction();
        else return error();
    }
    
}

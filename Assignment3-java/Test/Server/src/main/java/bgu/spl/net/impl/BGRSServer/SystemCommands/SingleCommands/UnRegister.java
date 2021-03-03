package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.ServerCommand;
import bgu.spl.net.impl.BGRSServer.SystemCommands.StudentCommand;

public class UnRegister extends StudentCommand {
    private int courseNumber;

    

    public UnRegister(int courseNumber) {
        super(10);
        this.courseNumber=courseNumber;
    }

    @Override
    public ServerCommand execute() {
        Database db = Database.getInstance();
        boolean result = db.unRegisterFromCourse(userName,courseNumber);
        if (result) return succAction();
        else return error();
    }
  
}

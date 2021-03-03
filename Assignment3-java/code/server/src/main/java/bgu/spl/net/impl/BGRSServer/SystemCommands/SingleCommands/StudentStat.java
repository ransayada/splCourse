package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.DB.Database;
import bgu.spl.net.impl.BGRSServer.SystemCommands.AdminCommand;
import bgu.spl.net.impl.BGRSServer.SystemCommands.ServerCommand;

import java.util.NoSuchElementException;

public class StudentStat extends AdminCommand {
    private String studentName;

  

    public StudentStat(String student) {
        super(8);
        this.studentName = student;
    }

    @Override
    public ServerCommand execute() {
        Database db = Database.getInstance();
        String studentData;
        try {
            studentData = db.getStudentStat(studentName);
        } catch (NoSuchElementException e) {
            return error();
        }
        ACK response = succAction();
        response.setOptionalMsg(studentData);
        return response;
    }
  
}

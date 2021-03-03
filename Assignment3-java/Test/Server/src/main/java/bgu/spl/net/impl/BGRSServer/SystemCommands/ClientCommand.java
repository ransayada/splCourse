package bgu.spl.net.impl.BGRSServer.SystemCommands;
import bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands.*;


public abstract class  ClientCommand implements Commands {
    protected int opCode;
    protected String userName = null;

    public ClientCommand(int opCode){this.opCode=opCode;}

    public int getOpCode(){return opCode;}
    public String getUserName(){return userName;}
    public void setUserName(String name){ userName=name;}

    protected ERR error(){return new ERR(opCode);}
    protected ACK succAction(){return new ACK(opCode);}

    public abstract ServerCommand execute();
}

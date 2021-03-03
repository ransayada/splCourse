package bgu.spl.net.impl.BGRSServer.SystemCommands;
import bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands.*;


/**
 * ClientCommand abstract class, which implements Commands interface, will be extended by the server commands - ACK and ERR.
 * Contains opCode and userName fields, basic getter/setters, error and succAction functions to assist with sending responds to the user.
 * The most important function - execute, will be used for the Protocol to perform the different actions per command type.
 */
public abstract class  ClientCommand implements Commands {
    protected int opCode;
    protected String userName = null;

    public ClientCommand(int opCode){this.opCode=opCode;}

    public int getOpCode(){return opCode;}
    public String getUserName(){return userName;}
    public void setUserName(String name){ userName=name;}

    protected ERR error(){return new ERR(opCode);}
    protected ACK succAction(){return new ACK(opCode);}

    /**
     * Functoin will be used to execute the specific required procedure for this command by the Protocol.
     * @return ServerCommand with the result to be returned to the user.
     */
    public abstract ServerCommand execute();
}

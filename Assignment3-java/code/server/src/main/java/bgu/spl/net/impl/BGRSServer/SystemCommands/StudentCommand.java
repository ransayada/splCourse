package bgu.spl.net.impl.BGRSServer.SystemCommands;

/**
 * StudentCommand abstract class, which extends ClientCommand abstract class (which implements Commands interface). Will be exteneded by the different Student Commands
 */
public abstract class  StudentCommand extends  ClientCommand{
    public StudentCommand(int opCode){
        super(opCode);
    }
}

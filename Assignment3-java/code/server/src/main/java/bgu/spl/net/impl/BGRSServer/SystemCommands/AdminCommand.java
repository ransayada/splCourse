package bgu.spl.net.impl.BGRSServer.SystemCommands;

/**
 * AdminCommand abstract class, which extends ClientCommand abstract class (which implements Commands interface). Will be exteneded by the different admin Commands
 */
public abstract class  AdminCommand extends  ClientCommand {
    public AdminCommand(int opCode){
        super(opCode);
    }
}

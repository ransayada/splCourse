package bgu.spl.net.impl.BGRSServer.SystemCommands;

public abstract class ServerCommand implements Commands {
    protected int opCode;
    protected int messageOpcode;

    public ServerCommand(int opCode,int messageOpcode) {
        this.opCode = opCode;
        this.messageOpcode = messageOpcode;
    }

    public int getOpCode() {
        return opCode;
    }

    public abstract byte[] encode(); //TODO: consider if needed for encdec
}

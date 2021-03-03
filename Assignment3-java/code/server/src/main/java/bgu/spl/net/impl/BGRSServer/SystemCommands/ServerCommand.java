package bgu.spl.net.impl.BGRSServer.SystemCommands;

/**
 * ServerCommand abstract class, which implements Commands interface, will be extended by the server commands - ACK and ERR
 * Contains the abstract method encode() to be used by the EncoderDecoder/
 */
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

    public abstract byte[] encode();
}

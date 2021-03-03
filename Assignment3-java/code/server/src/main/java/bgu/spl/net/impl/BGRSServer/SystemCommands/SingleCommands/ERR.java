package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.SystemCommands.*;

public class ERR extends ServerCommand {

    public ERR(int messageOpcode){super(13,messageOpcode);}

    /**
     * 1) encode the ERR op
     * 2) encode the messageOpcode
     *
     * @return array of bytes for the buufer to sent to the client
     */
    @Override
    public byte[] encode() {
        int num = getOpCode();
        int num2 = messageOpcode;
        byte[] bytesArr = new byte[4];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        bytesArr[2] = (byte) ((num2 >> 8) & 0xFF);
        bytesArr[3] = (byte) (num2 & 0xFF);
        return bytesArr;
    }
}

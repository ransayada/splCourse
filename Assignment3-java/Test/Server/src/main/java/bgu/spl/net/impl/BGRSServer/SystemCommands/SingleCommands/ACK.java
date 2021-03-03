package bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands;

import bgu.spl.net.impl.BGRSServer.SystemCommands.ServerCommand;

import java.nio.charset.StandardCharsets;

public class ACK extends ServerCommand {
    private String optionalMsg=null;

    public ACK(int messageOpcode) {
        super(12, messageOpcode);
    }
    public void setOptionalMsg(String msg){optionalMsg=msg;}

      /**
     * 1) encode the ACK op
     * 2) encode the messageOpcode
     * 3) encode the optionalMsg
     * 4) encode 1 byte 0
     *
     * @return array of bytes for the buufer to sent to the client
     */
    @Override
    public byte[] encode() {
        byte[] temp;
        if(optionalMsg!=null) {
            temp = (optionalMsg + "\0").getBytes(StandardCharsets.UTF_8);
        }
        else
            temp = "\0".getBytes();
        int num = getOpCode();
        int num2 = messageOpcode;
        byte[] bytesArr = new byte[temp.length + 4];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        bytesArr[2] = (byte) ((num2 >> 8) & 0xFF);
        bytesArr[3] = (byte) (num2 & 0xFF);
        for (int i = 0; i < temp.length; i++) {
            bytesArr[i + 4] = temp[i];
        }
        return bytesArr;
    }
}

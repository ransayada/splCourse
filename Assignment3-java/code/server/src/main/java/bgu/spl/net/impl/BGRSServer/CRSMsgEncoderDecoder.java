package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGRSServer.SystemCommands.Commands;
import bgu.spl.net.impl.BGRSServer.SystemCommands.SingleCommands.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CRSMsgEncoderDecoder implements MessageEncoderDecoder<Commands> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int nextZero = 0;
    private int numberOfZeros = 0;
    private short op = 0;


    /**
     * decodeNextByte
     * first check if there are 4 bytes in the array
     * get the command op
     * using the op to decode the all command base on length and number of zeros
     *
     * @param nextByte the next byte to consider for the currently decoded
     *                 message
     * @return a decoded command
     */
    public Commands decodeNextByte(byte nextByte) {

        if (len == 1) {
            pushByte(nextByte);
            op = getOp();
            if ((op == ((short) 4)) | (op == ((short) 11))) {
                return commandToBuildD(op);
            }
        }
        else if (len >= 3) { // checker to see if there are 4 bytes of data
            if ((op == ((short) 1) | op == ((short) 2) | op == ((short) 3))) {
                if (nextByte == '\0' ) {
                    if (numberOfZeros == 0) { //hence first zero
                        numberOfZeros++;
                        pushByte(nextByte);
                    } else if (numberOfZeros == 1) { //hence this is the second zero
                        return commandToBuildA(op);
                    }
                } else {
                    pushByte(nextByte);
                }
            } else if (op == ((short) 5) | op == ((short) 6) | op == ((short) 7) | op == ((short) 9) | op == ((short) 10)) {
                pushByte(nextByte);
                return commandToBuildB(op);
            } else if (op == ((short) 8)) {
                if (nextByte == '\0') {
                    return commandToBuildC();
                } else {
                    pushByte(nextByte);
                }
            } else if (op == ((short) 12) | op == ((short) 13)) { //op 12 = ack op 13 = err
                throw new IllegalArgumentException("the op is not valid for decoding");
            } else {
                throw new IllegalArgumentException("there was problem with the decoding");
            }
        } else {
            pushByte(nextByte);
        }
        return null; //not finish yet
    }

    /**
     * pushs the bytes and memeo the indexes of the 00X0 (if there are)
     *
     * @param nextByte
     */
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    /**
     * return a short with the command op
     *
     * @return short op out of byte
     */
    private short getOp() {
        short result = (short) ((bytes[0] & 0xff) << 8);
        result += (short) (bytes[1] & 0xff);
        return result;
    }

    /**
     * commanToBuildA : AdminReg,StudentReg,Login id: 2 zero
     * commanToBuildB : CourseReg,KdamCheck,CourseStat,IsRegistered,UnRegister id: length = 4 byets
     * commanToBuildC : StudentStat id: 1 zero
     * commanToBuildD : Logout,MyCourses id: length = 2 byets
     * <p>
     * this methods return a decoded command out of buffer feed of bytes
     *
     * @param thisOp
     * @return a command specified by the op
     */
    private Commands commandToBuildA(short thisOp) {
        int indexOfFirstZero = findNextZero(nextZero);
        byte[] subArray1 = Arrays.copyOfRange(bytes, 2, indexOfFirstZero);
        String userName = new String(subArray1, StandardCharsets.UTF_8);
        byte[] subArray2 = Arrays.copyOfRange(bytes, indexOfFirstZero + 1, len);
        String passWord = new String(subArray2, StandardCharsets.UTF_8);
        len = 0;
        numberOfZeros = 0;
        op = 0;
        nextZero = 0;
        switch (thisOp) {
            case ((short) 1):
                return new AdminReg(userName, passWord);
            case ((short) 2):
                return new StudentReg(userName, passWord);
            case ((short) 3):
                return new Login(userName, passWord);
        }
        throw new IllegalArgumentException("not valid return");
    }

    private Commands commandToBuildB(short thisOp) {
        byte[] subArray = Arrays.copyOfRange(bytes, 2, 4);
        short result = (short) ((subArray[0] & 0xff) << 8);
        result += (short) (subArray[1] & 0xff);
        int num = result;
        len = 0;
        numberOfZeros = 0;
        op = 0;
        switch (thisOp) {
            case ((short) 5):
                return new CourseReg(num);
            case ((short) 6):
                return new KdamCheck(num);
            case ((short) 7):
                return new CourseStat(num);
            case ((short) 9):
                return new IsRegistered(num);
            case ((short) 10):
                return new UnRegister(num);
        }
        throw new IllegalArgumentException("not valid return ");
    }

    private Commands commandToBuildC() {
       // int indexOfFirstZero = findNextZero(nextZero);
        byte[] subArray = Arrays.copyOfRange(bytes, 2, len);
        String userName = new String(subArray, StandardCharsets.UTF_8);
        len = 0;
        op = 0;
        return new StudentStat(userName);
    }

    private Commands commandToBuildD(short thisOp) {
        len = 0;
        op = 0;
        if (thisOp == ((short) 11)) {
            return new MyCourses();
        } else {
            return new Logout();
        }
    }

    /**
     * getting the index of the next zero from the last
     *
     * @param nextZe
     * @return -1 if didnt find any
     */
    private int findNextZero(int nextZe) {
        int index = nextZe + 1;
        while (index != bytes.length) {
            if (bytes[index] == ('\0')) {
                nextZero = index;
                return index;
            }
            index++;
        }
        return (-1);
    }

    /**
     * both of the encode needed command implementation happened in their own class
     *
     * @param message the message to encode
     * @return encoded command
     */

    public byte[] encode(Commands message) {
        short op = (short) message.getOpCode();
        if (op == 12) {
            return ((ACK) message).encode();
        } else if (op == 13) {
            return ((ERR) message).encode();
        } else {
            throw new IllegalArgumentException("the system cant encode commands other than ACK & ERR");
        }
    }
}

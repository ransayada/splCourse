
#include "../include/ClientEncDec.h"

ClientEncDec::ClientEncDec(bool &shouldTerminate) : len(0), op("0"), aditionalOp("0"),
                                                    _shouldTerminate(shouldTerminate),
                                                    bytes(std::vector<char>((short) 1024)) {
}

/**
 * Encode the received @strToByt input from the user based on the specific format per each Command.
 *
 *
 * @param strToByt
 * @param len
 * @return  list of chars contains the bytes to send to the Server.
 */
char* ClientEncDec::encode(std::string &strToByt, int &len) {
    len = 0;
    char *result = new char[1024];
    //the first word in all commands is the op / if there is no space indexOfFirstSpace = npos
    int indexOfFirstSpace = strToByt.find(' ');

    if (strToByt.find(' ') != std::string::npos) { // cant compare size and int.
        //harvest the command short out of the string line
        std::string com = strToByt.substr(0, indexOfFirstSpace);
        short opt = comToOp(com);
        std::string opString = "";
        opString+= ((opt >> 8) & 0xFF);
        opString+= (opt & 0xFF);

        //add opCode to the result
        memcpy(result+len, opString.c_str(), 2);
        len=len+2;
        /*
         * op 1,2,3 input format: op: command + space + userName + space + password
         * op 1,2,3 output format: op:2 bytes + userName + \0 : 1byte + passWord + \0 : 1 byte
         */
        if (opt < 4) {
            //the second word is additional message / if there is no space indexOfFirstSpace = npos
            int indexOfSecondSpace = strToByt.find(' ', indexOfFirstSpace + 1);
            // userName parsing
            std::string userName = strToByt.substr(indexOfFirstSpace + 1, indexOfSecondSpace - indexOfFirstSpace - 1);
            //passWord parsing
            std::string password = strToByt.substr(indexOfSecondSpace + 1);

            //add first string to the result including last \0 byte
            memcpy(result+len, userName.c_str(), userName.length()+1);
            len=len+userName.length()+1;
            //add second string to the result including last \0 byte
            memcpy(result+len, password.c_str(), password.length()+1);
            len=len+password.length()+1;
            return result;
        }
            /*
             * op 5,6,7,8,9,10 input format: op: command + space + courseNumber
             * op 5,6,7,8,9,10 output format: op:2 bytes + courseNum + \0 : 1byte
             */
        else if ((opt > 4) & (opt < 11)) {
            std::string firstData = strToByt.substr(indexOfFirstSpace + 1);
            if (opt != 8) {
                int n = std::stoi(firstData);
                short art = ((short) n);
                std::string courseNumber = "";
                courseNumber+= ((art >> 8) & 0xFF);
                courseNumber+= (art & 0xFF);
                //add CourseNumber to the result
                memcpy(result+len, courseNumber.c_str(), 2);
                len=len+2;
            }
            if (opt == 8) {
                //add string to the result including last \0 byte
                memcpy(result+len, firstData.c_str(), firstData.length()+1);
                len=len+firstData.length()+1;
            }
            return result;

        }
    /*
     * op 4, 11 input format: op: command
     * op 4, 11 output format: op:2 bytes
     * there are no spaces
     */
    } else { //what happaned if there are no spaces :  npos is the -1 of std::string
        std::string com = strToByt.substr(0);
        short opt = comToOp(com);
        std::string opString = "";
        opString+= ((opt >> 8) & 0xFF);
        opString+= (opt & 0xFF);
        //add opCode to the result
        memcpy(result+len, opString.c_str(), 2);
        len=len+2;
        return  result;

    }
   return nullptr;
}

/**
 * Decoded byte by byte until received full message based on the provided format.
 *
 * @param byte of message and reference to the result string, which will be later filled with the decoded message.
 * @return string for ack : ACK op of the acknowledge command
 *                         (enter) optional message
 *               for error: Error op of the acknowledge command
 */
bool ClientEncDec::decode(char &byte, std::string &result) {
    std::string ans = "";
    //turning first 2 bytes to string op
    if (len == 1) {
        bytes[len++] = byte;
        short first = (short) ((bytes[0] & 0xff) << 8);
        first += (short) (bytes[1] & 0xff);
        op = opToCom(first);
    }
        //turning second 2 bytes to string aditionalop
    else if (len == 3) {
        bytes[len++] = byte;
        short second = (short) ((bytes[2] & 0xff) << 8);
        second += (short) (bytes[3] & 0xff);
        aditionalOp = std::to_string(second);
        // if op is err then the length of the msg is 4 bytes
        if (op == "ERROR") {
            result = op + " " + aditionalOp;
            len = 0;
            op = '0';
            aditionalOp = '0';
            return true;
        }
    }
        // byte 0 represent the end of this msg
    else if ((len >= 4) & (byte == '\0')) {
        ans += (op + " " + aditionalOp);
        if (len > 4) {
            ans = ans + "\n";
            for (int i = 4; i < len; i++) {
                ans += (bytes[i] & 0xff);
            }
        }
        if (aditionalOp == "4") { // on ack LogOut sholdTerminate equal true;
            _shouldTerminate = true;
        }
        result = ans;
        len = 0;
        op = '0';
        aditionalOp = '0';
        return true;
    } else {
        bytes[len++] = byte;
        return false;
    }
    return false;
}

//for decode, turn command string to short op
std::string ClientEncDec::opToCom(short op) {
    if (op == 12) {
        return "ACK";
    } else {
        return "ERROR";
    }
}

//for encode, turn short op to string command
short ClientEncDec::comToOp(std::string com) {
    if (com == "ADMINREG") {
        return ((short) 1);
    } else if (com == "STUDENTREG") {
        return ((short) 2);
    } else if (com == "LOGIN") {
        return ((short) 3);
    } else if (com == "LOGOUT") {
        return ((short) 4);
    } else if (com == "COURSEREG") {
        return ((short) 5);
    } else if (com == "KDAMCHECK") {
        return ((short) 6);
    } else if (com == "COURSESTAT") {
        return ((short) 7);
    } else if (com == "STUDENTSTAT") {
        return ((short) 8);
    } else if (com == "ISREGISTERED") {
        return ((short) 9);
    } else if (com == "UNREGISTER") {
        return ((short) 10);
    } else if (com == "MYCOURSES") {
        return ((short) 11);
    } else
        return -1; //Assuming as instructed it won't be used
}



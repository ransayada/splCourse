#ifndef CLIENT_CLIENTENCDEC_H
#define CLIENT_CLIENTENCDEC_H

#include <string>
#include <iostream>
#include <boost/asio.hpp>

using boost::asio::ip::tcp;


class ClientEncDec {
public:
    ClientEncDec(bool &shouldTerminate);
    //Encode messages from String to bytes
   // char*
    char* encode(std::string &strToByt, int &len);
    //Decode messages based on ACK and ERR structure, return string
    bool decode(char &byte, std::string &result);
private:
    int len;
    std::string op;
    std::string aditionalOp;
    bool &_shouldTerminate;
    std::vector<char> bytes;
    //make the command string out of the short op
    std::string opToCom(short op);
    //make the short op out of the command string
    short comToOp(std::string com);

};


#endif //CLIENT_CLIENTENCDEC_H

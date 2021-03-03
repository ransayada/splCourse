#ifndef CONNECTION_HANDLER_H_
#define CONNECTION_HANDLER_H_
                                           
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include "./ClientEncDec.h"

using boost::asio::ip::tcp;

class ConnectionHandler {
private:
	const std::string host_;
	const short port_;
	boost::asio::io_service io_service_;   // Provides core I/O functionality
	tcp::socket socket_;
	ClientEncDec _encdec;
 
public:
    ConnectionHandler(std::string host, short port, bool &shouldTerminate);
    virtual ~ConnectionHandler();
 
    // Connect to the remote machine
    bool connect();
 
    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
    bool getBytes(char bytes[], unsigned int bytesToRead);
 
	// Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
    bool sendBytes(const char bytes[], int bytesToWrite);
	
    // Read an ascii line from the server
    // Returns false in case connection closed before a newline can be read.
    bool getLine(std::string& line);
	
	// Send an ascii line from the server
    // Returns false in case connection closed before all the data is sent.
    bool sendLine(std::string& line);

    // Close down the connection properly.
    void close();
 
}; //class ConnectionHandler
 
#endif
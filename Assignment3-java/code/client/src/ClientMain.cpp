
#include <stdlib.h>
#include <string>
#include <queue>
#include "../include/Task.h"
#include "../include/ConnectionHandler.h"
#include <boost/thread/thread.hpp>
#include <boost/fiber/condition_variable.hpp>
#include <boost/fiber/mutex.hpp>


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/



int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    bool shouldTerminate = false; //use as a flag for a successful logout
    bool pendingForLogout = false; //use as a flag to to mark that the second thread is pending for logout

    ConnectionHandler connectionHandler(host, port, shouldTerminate);

    //use shared Q for pending messages from the user (throw stdin)
    std::queue<std::string> *pendingInputs = new std::queue<std::string>();
    boost::mutex mutex;

    /* Use 1 condition variable for 2 uses - mark the Q isn't empty, and that a logout was processed.
     * Decided to use 1 for readability purposes
     */
    boost::condition_variable conditionCheck;

    //Create the task and thread
    Task getKeyboard(shouldTerminate,pendingForLogout, conditionCheck, *pendingInputs, mutex);

    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        delete pendingInputs;
        return 1;
    }
    boost::thread th1(&Task::run, &getKeyboard);

    //As long as we didn't receive a successful Logout, pull a message from the Q, send it to the server and wait for a result.
    while (!shouldTerminate) {
        //lock the Q
        boost::unique_lock<boost::mutex> lock(mutex);
        //if empty - wait until second thread pushes a line
        while (pendingInputs->size() == 0) {
            conditionCheck.wait(lock);
        }
        std::string line(pendingInputs->front());
        pendingInputs->pop();
        lock.unlock();
        //Send a message to the Server
        if (!connectionHandler.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            shouldTerminate = true;
            conditionCheck.notify_all();
            break;
        }
       std::string answer;
        //Get a response from the Server
        if (!connectionHandler.getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            shouldTerminate = true;
            conditionCheck.notify_all();
            break;
        }
        std::cout << answer << std::endl;

        //In case the second thread is waiting (for logout's result), wake him up
        if(answer[4] =='4') {
            pendingForLogout = false;
            conditionCheck.notify_all();
        }
    }
    th1.interrupt();
    th1.join();
    delete pendingInputs;
    return 0;
}




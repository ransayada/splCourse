#include "../include/Task.h"

#include <iostream>

Task::Task(bool &shouldTerminate, bool &pendingForLogout, boost::condition_variable &conditionCheck,  std::queue<std::string> &inputQ, boost::mutex &mutex)
        : _shouldTerminate(
        shouldTerminate),_pendingForLogout(pendingForLogout),_conditionCheck(conditionCheck), _inputQ(inputQ), _mutex(mutex) {}

void Task::run() {
    boost::this_thread::interruption_point();
    while (!_shouldTerminate) {

            _pendingForLogout=false;
            const short bufsize = 1024;
            char buf[bufsize];
            //Push input from the user to the Q of pending messages
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            boost::unique_lock<boost::mutex> lock(_mutex);
            _inputQ.push(line);



        //in case the op is LOGOUT - wait until the main thread wakes you up after the processing
        int indexOfFirstSpace = line.find(' ');
        std::string com= line.substr(0,indexOfFirstSpace);

        //Notify the main thread in case waiting on empty Q
        _conditionCheck.notify_all();

        //In case the input request was LOGOUT - wait until the message was processed.
        if(com=="LOGOUT"){
            _pendingForLogout=true;
            while(_pendingForLogout)
                _conditionCheck.wait(lock);
        }
    }
}


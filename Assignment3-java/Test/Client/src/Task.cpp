#include "../include/Task.h"

#include <iostream>

Task::Task(bool &shouldTerminate, bool &pendingForLogout, boost::condition_variable &conditionCheck,  std::queue<std::string> &inputQ, boost::mutex &mutex)
        : _shouldTerminate(
        shouldTerminate),_pendingForLogout(pendingForLogout),_conditionCheck(conditionCheck), _inputQ(inputQ), _mutex(mutex) {}

void Task::run() {
    boost::this_thread::interruption_point();
    while (!_shouldTerminate) {
            //Ask for input
            _pendingForLogout=false;
            const short bufsize = 1024;
            char buf[bufsize];
            std::cerr << "insert request" << std::endl; //TODO:remove
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            boost::unique_lock<boost::mutex> lock(_mutex);
            _inputQ.push(line);

        //notify the main thread in case pending for an empty Q

        //in case the op is LOGOUT - wait until the main thread wakes you up after the processing
        int indexOfFirstSpace = line.find(' ');
        std::string com= line.substr(0,indexOfFirstSpace);
        _conditionCheck.notify_all();
        if(com=="LOGOUT"){
            _pendingForLogout=true;
            while(_pendingForLogout)
                _conditionCheck.wait(lock);
        }
    }
}


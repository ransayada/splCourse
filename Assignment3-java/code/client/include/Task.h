#ifndef TASK_H
#define TASK_H

#include <mutex>
#include <string>
#include <queue>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition_variable.hpp>


class Task {
public:
    Task(bool &shouldTerminate, bool &pendingForLogout, boost::condition_variable &conditionCheck, std::queue<std::string> &inputQ,
         boost::mutex &mutex);

    void run();

private:
    bool &_shouldTerminate;
    bool &_pendingForLogout;
    boost::condition_variable &_conditionCheck;
    std::queue<std::string> &_inputQ;
    boost::mutex &_mutex;
};


#endif

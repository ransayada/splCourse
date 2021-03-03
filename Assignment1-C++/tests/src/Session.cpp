#include <fstream>
#include "../include/json.hpp"
#include "../include/Session.h"
#include "../include/Agent.h"

using json = nlohmann::json;
using namespace std;

Session::Session(const string &path) : g(vector<vector<int>>()), treeType(), cycle(0), agents(
        vector<Agent *>()), pendingAgents(
        vector<Agent *>()), infectedQueue(queue<int>()) {

    //read JSON file
    ifstream inP(path);
    json inputFile;
    inP >> inputFile;

    //build initial graph
    setGraph(Graph(inputFile["graph"]));
    //build initial agent list
    for (auto &elem: inputFile["agents"]) {
        if (elem[0] == "C") {
            agents.push_back(new ContactTracer());
        } else if (elem[0] == "V") {
            agents.push_back(new Virus(elem[1]));
            g.spreadVirus(elem[1]);
        }
    }
    // initial treeType
    if (inputFile["tree"] == "C") treeType = Cycle;
    if (inputFile["tree"] == "M") treeType = MaxRank;
    if (inputFile["tree"] == "R") treeType = Root;
}

void Session::simulate() {
    bool isFinished(false);
    cycle = 0;
    while (!isFinished) {
        pendingAgents = vector<Agent *>();
        for (Agent *ag:agents) {
            ag->act(*this);
        }
        //following the action of all current agents, add pendingAgents to the main agents list.
        int size = pendingAgents.size();
        for (int i = 0; i <
                        size; i++) {
            agents.push_back(pendingAgents[i]->clone());
            if (pendingAgents[i] != nullptr) { //make sure to delete extra memory
                delete pendingAgents[i];
                pendingAgents[i] = nullptr;
            }
        }
        (cycle)++;
        if (isEndOfSess()) isFinished = (true); //at the end of each cycle, check EndOfSess flow
    }
    cycle = 0;
    createOutput();
}

TreeType Session::getTreeType() const {
    return this->treeType;
}

const int Session::getCycle() const {
    return this->cycle;
}

Graph &Session::getGraph() {
    return g;
}

int Session::dequeueInfected() {
    if (!infectedQueue.empty()) {
        int toPop = infectedQueue.front();
        infectedQueue.pop();
        return toPop;
    }
    return -1;
}


void Session::enqueueInfected(int nodeInd) {
    infectedQueue.push(nodeInd);
}

void Session::setGraph(const Graph &graph) {
    g = (graph);
}

void Session::addAgent(const Agent &agent) {
    pendingAgents.push_back(agent.clone());
}

const bool
Session::isEndOfSess() const { //for every virus agent, make sure isInfected and make sure all neighbors are infected as well
    bool isSatisfied(true);
    int size = g.getNodeStatusList().size();
    for (int i = 0; isSatisfied && i < size; i++) {
        if (g.hasVirus(i)) {
            if (!g.isInfected(i)) {     //in case a node is a carrier - session didn't finish.
                isSatisfied = false;
                break;
            }
            else { //iterate through the neighbors to make sure are infected;
                const vector<int> &neighbors = g.getEdges()[i];
                int sizeNe = neighbors.size();
                for (int i = 0; i < sizeNe; i++) {
                    if ((neighbors[i] == 1) && !g.isInfected(i)) {
                        isSatisfied = false;
                        break;
                    }
                }
            }
        }
    }
    return isSatisfied;
}
void Session::createOutput() {
    json output;
    //go through all nodes and add them if infected
    vector<int> infectedList;
    const vector<vector<int>>& edges = g.getEdges();
    int size = edges.size();
    for (int i = 0; i < size; i++) {
        if (g.isInfected(i)) infectedList.push_back(i);
    }
    //add to file
    output["infected"] = infectedList;
    output["graph"] = edges;
    ofstream outFile("./output.json");
    outFile << output;
}

//rule of 5

//destructor
void Session::clear() {
    //delete all agents on 'agents'
    int size1 = agents.size();
    for (int i = 0; i < size1; i++) {
        if (agents[i] != nullptr) {
            delete agents[i];
            agents[i] = nullptr;
        }
    }
    //delete all agents on 'pendingAgents' - should be empty at that point already
    int size2 = pendingAgents.size();
    for (int i = 0; i < size2; i++) {
        if (pendingAgents[i]) {
            delete pendingAgents[i];
            pendingAgents[i] = nullptr;
        }
    }
}

Session::~Session() {
    clear();
}


void Session::copy(const vector<Agent *> &other_agents, const vector<Agent *> &other_pendingAgents) {
    for (Agent *ag:other_agents) {
        if (ag)
            agents.push_back(ag->clone()); //didn't use addAgent as in our flow it adds to pendingAgents.
    }
    for (Agent *ag:other_pendingAgents) {
        if (ag)
            pendingAgents.push_back(ag->clone());
    }
}

//copy constructor

Session::Session(const Session &other) : g(other.g), treeType(other.treeType), cycle(other.cycle),
                                         agents(vector<Agent *>()),
                                         pendingAgents(vector<Agent *>()),
                                         infectedQueue(other.infectedQueue) {
    copy(other.agents, other.pendingAgents); //deep copy for agent lists
}

//copy assignment
Session &Session::operator=(const Session &other) {
    if (this == &other) {
        return *this;
    }
    clear();
    g = other.g;
    treeType = other.treeType;
    cycle = other.cycle;
    agents = vector<Agent *>();
    pendingAgents = vector<Agent *>();
    infectedQueue = other.infectedQueue;
    copy(other.agents, other.pendingAgents); //deep copy for agent lists

    return *this;
}

//move constructor
Session::Session(Session &&other) : g(move(other.g)), treeType(other.treeType), cycle(other.cycle),
                                    agents(move(other.agents)), pendingAgents(move(other.pendingAgents)),
                                    infectedQueue(move(other.infectedQueue)) {
}

//move assignment
Session &Session::operator=(Session &&other) {
    if (this != &other) {
        clear();
        //Steal pointers
        g = move(other.g);
        treeType = other.treeType;
        cycle = other.cycle;
        agents = move(other.agents);
        pendingAgents = move(other.pendingAgents);
        infectedQueue = move(other.infectedQueue);
    }
    return *this;
}

#include "../include/Agent.h"

using namespace std;

Agent::Agent() {};

//empty destructor
Agent::~Agent() {
}

ContactTracer::ContactTracer() : Agent() {
}

void ContactTracer::act(Session &session) {
    Graph &g1 = session.getGraph();
    int num = session.dequeueInfected(); // pop the next infected on the line
    if (num != -1) {
        Tree *t1 = g1.BFS(num, session); // makes bfs tree out of the graph
        int toIsolate = t1->traceTree(); // using the traceTree method in order to choose which node to isolate
        g1.isolateNode(toIsolate);
        delete t1;  // deleting the remains of the bfs tree
    }
}

const int ContactTracer::getIndex() const { return -1; }

Agent *ContactTracer::clone() const { return new ContactTracer(); }


Virus::Virus(int nodeInd) : Agent(), nodeInd(nodeInd) {
}

void Virus::act(Session &session) {
    Graph &g1 = session.getGraph();
    if (!g1.isInfected(getIndex())) { //if agent hasn't been infected already - infect it and add to Q
        g1.infectNode(getIndex());
        session.enqueueInfected(getIndex());
    }
    const vector<int> &neighbors(
            g1.getEdges()[getIndex()]);
    bool spread = false;
    int size = neighbors.size();
    //go over neighbors to look for a neighbor that isn't infected
    for (int i = 0; !spread & (i < size); i++) {
        //use hasVirus to make sure whether has a virus (both infected and carrier)
        if ((neighbors[i] == 1) & !g1.hasVirus(i)) {
            g1.spreadVirus(i);  //spread the virus to node i (assign as carrier)
            Agent *nextVirus = new Virus(i);
            session.addAgent(*nextVirus); //add as an agent
            delete nextVirus; //addAgent clone the nextVirus so we need to delete nextVirus
            spread = true;
        }
    }
}

const int Virus::getIndex() const { return nodeInd; }

Agent *Virus::clone() const {
    return new Virus(getIndex());
}



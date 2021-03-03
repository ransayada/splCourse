#ifndef GRAPH_H_
#define GRAPH_H_

#include <vector>
#include "Tree.h"

class Graph{
public:
    Graph(std::vector<std::vector<int>> matrix);
    void infectNode(int nodeInd); //Change nodeStatus to I
    void spreadVirus(int nodeInd); //Change nodeStatus to C
    void isolateNode(int nodeInd); //for CT tracer flow
    const bool isInfected(int nodeInd) const;
    const bool hasVirus(int nodeInd) const;
    const std::vector<std::vector<int>>& getEdges() const;
    const std::vector<char>& getNodeStatusList() const;
    Tree * BFS(int nodeInd, Session& sess) const; // bfs path on graph

private:
    std::vector<std::vector<int>> edges;
    std::vector<char> nodeStatusList;  // H=Healthy, I= infected (operating virus), C=carrier (not yet infected)
};

#endif

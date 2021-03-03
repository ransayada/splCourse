#include "../include/Tree.h"
#include "../include/Session.h"

using namespace std;

//Tree
Tree::Tree(int rootLabel) : node(rootLabel), children(vector<Tree *>()) {
}

void Tree::addChild(const Tree &child) {
    children.push_back(child.clone());
}
//for efficiency reasons as instructed in the office hours
void Tree::addChild(Tree* child){
    children.push_back(child);
}

const vector<Tree *> &
Tree::getChildren() const {
    return children;
}

const int &Tree::getLabel() const {
    return node;
}


Tree *Tree::createTree(const Session &session, int rootLabel) { // create tree is based on the type of tree we choose
    switch (session.getTreeType()) {
        case MaxRank: {
            return new MaxRankTree(rootLabel);
        }
        case Root: {
            return new RootTree(rootLabel);
        }
        case Cycle: {
            return new CycleTree(rootLabel, session.getCycle());
        }
        default:
            return 0;
    }

}


//CycleTree
CycleTree::CycleTree(int rootLabel, int currCycle) : Tree(rootLabel), currCycle(currCycle) {

}
// gives back the next node to isolate on a cycle tree
int CycleTree::traceTree() {
    Tree* tree = this;
    int cC = this->currCycle; // a down counter to know how many iterations per cycle we have
    int op = node;
    while (!tree->getChildren().empty() & (cC!=0)) { //doing the trace tree for cycle until cc is 0 or there is no lefter node
        tree = tree->getChildren()[0];
        op = tree->getLabel(); // get the label of a node to isolate
        cC--;
    }
    return op;
}


Tree *CycleTree::clone() const {
    return new CycleTree(*this);
}

//MaxRankTree
MaxRankTree::MaxRankTree(int rootLabel) : Tree(rootLabel) {

}

Tree *MaxRankTree::clone() const {
    return new MaxRankTree(*this);
}

int MaxRankTree::traceTree() { // commit BFS travel over the tree (due to code clearance decided not to add to BFS function logic)
    //  Tree *tree = this->clone();

    deque<Tree *> q;
    q.push_back(this);
    int tempMaxLabel = q[0]->getLabel();
    int tempMaxRank = this->getChildren().size();
    while (!q.empty()) {
        Tree *tempTree = q[0];
        q.pop_front();
        int size1 = tempTree->getChildren().size();
        for (int i = 0; i < size1; i++) { //loop on tempTree children
            int size2 =tempTree->getChildren()[i]->getChildren().size();
            if (size2 > tempMaxRank) // finding if the new node with the max range
            {
                tempMaxLabel = tempTree->getChildren()[i]->getLabel();
                tempMaxRank = size2;

            }
            q.push_back(tempTree->getChildren()[i]); // order the all bfs tree
        }
    }
    return tempMaxLabel;
}
RootTree::RootTree(int rootLabel) : Tree(rootLabel) {

}

int RootTree::traceTree() {
    return node;
}

Tree *RootTree::clone() const {
    return new RootTree(*this);
}


//-----------------------------------------------------Rule of 5--------------------------------------------------
//Destructor
void Tree::clear() {
    int size = children.size();
    for (int i = 0;( i < size )& !(children.empty()); i++) {
        if(children[i]!= nullptr){
            delete children[i];
            children[i] = nullptr;
        }
    }
    children.clear();
}
Tree::~Tree() {
    clear();
}
//copyConstructor
void Tree::copy(const vector<Tree *> &other_children)  {
    int size = other_children.size();
    for (int i = 0; (i < size) & !(other_children.empty()); i++) {
        if(other_children[i]) {
            Tree * t1 = other_children[i]->clone();
            children.push_back(t1);
        }
    }
}
Tree::Tree(const Tree &other) : node(other.node), children(vector<Tree *>()) {
    copy(other.children); //deep copy of children vector
}

//copy assignment
Tree &Tree::operator=(const Tree& other) {
    if (this == &other) {
        return *this;
    }
    clear();
    node = other.node;
    children= vector<Tree *>();
    copy(other.children); //deep copy of children vector
    return *this;
}

//move constructor
Tree::Tree(Tree &&other) : node(other.node), children(move(other.children)) {
}
//move assignment
Tree &Tree::operator=(Tree &&other) {
    if (this != &other) {
        if (!(children.empty())) {
            clear();
        }

    node = other.node;
    children = move(other.children);}
    return *this;

}

//-------------------------------------------------end Rule of 5--------------------------------------------------

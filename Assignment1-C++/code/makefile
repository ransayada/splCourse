#All Targets
all: cTrace

#Tool invocations
cTrace: bin/main.o bin/Session.o bin/Graph.o bin/Agent.o bin/Tree.o
		@echo 'Building target: cTrace'
		@echo 'Invoking: C++ Linker'
		g++ -o bin/cTrace bin/main.o bin/Session.o bin/Agent.o bin/Graph.o bin/Tree.o
		@echo 'Finished building target: cTrace'
		@echo ' '

#Depends on the source and header files
bin/main.o: src/main.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/main.o src/main.cpp

bin/Session.o: src/Session.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Session.o src/Session.cpp

bin/Graph.o: src/Graph.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Graph.o src/Graph.cpp

bin/Agent.o: src/Agent.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Agent.o src/Agent.cpp

bin/Tree.o: src/Tree.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Tree.o src/Tree.cpp


#Clean the build directory
clean:
		@echo 'Cleaning'
		rm -f bin/*

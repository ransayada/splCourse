CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude -lpthread -lboost_thread
LDFLAGS:=-lboost_system -lpthread -lboost_thread

#All Targets
all: BGRSclient

#Tool invocations
BGRSclient: bin/ClientMain.o bin/ClientEncDec.o bin/ConnectionHandler.o bin/Task.o
	@echo 'Building target: BGRSclient'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/BGRSclient bin/ClientMain.o bin/ConnectionHandler.o bin/ClientEncDec.o bin/Task.o $(LDFLAGS)
	@echo 'Finished building target: BGRSclient'
	@echo ' '


#Depends on the source and header files
bin/ClientMain.o: src/ClientMain.cpp
	g++ $(CFLAGS) -o bin/ClientMain.o src/ClientMain.cpp
	
bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/ClientEncDec.o: src/ClientEncDec.cpp
	g++ $(CFLAGS) -o bin/ClientEncDec.o src/ClientEncDec.cpp

bin/Task.o: src/Task.cpp
	g++ $(CFLAGS) -o bin/Task.o src/Task.cpp

#Clean the build directory
clean:
	@echo 'Cleaning'
	rm -f bin/*


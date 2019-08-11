CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: Client
	g++ -o bin/BGSclient bin/connectionHandler.o bin/BidiClient.o bin/ReadFromKeyboard.o bin/ReadFromServer.o bin/EncoderDecoder.o $(LDFLAGS)

Client: bin/connectionHandler.o bin/BidiClient.o bin/ReadFromKeyboard.o bin/ReadFromServer.o bin/EncoderDecoder.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BidiClient.o: src/BidiClient.cpp
	g++ $(CFLAGS) -o bin/BidiClient.o src/BidiClient.cpp

bin/ReadFromKeyboard.o: src/ReadFromKeyboard.cpp
	g++ $(CFLAGS) -o bin/ReadFromKeyboard.o src/ReadFromKeyboard.cpp

bin/ReadFromServer.o: src/ReadFromServer.cpp
	g++ $(CFLAGS) -o bin/ReadFromServer.o src/ReadFromServer.cpp

bin/EncoderDecoder.o: src/EncoderDecoder.cpp
	g++ $(CFLAGS) -o bin/EncoderDecoder.o src/EncoderDecoder.cpp

.PHONY: clean
clean:
	rm -f bin/*

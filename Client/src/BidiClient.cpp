//
// Created by razbik@wincs.cs.bgu.ac.il on 1/1/19.
//


#include <iostream>
#include "connectionHandler.h"
#include <thread>
#include <mutex>
#include "ReadFromKeyboard.h"
#include "ReadFromServer.h"
#include "EncoderDecoder.h"

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1] ;
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);

    EncoderDecoder encoderDecoder(connectionHandler);

    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    ReadFromKeyboard task1(connectionHandler, encoderDecoder);
    ReadFromServer task2(connectionHandler, encoderDecoder);

    std::thread th1(&ReadFromKeyboard::run, &task1);
    std::thread th2(&ReadFromServer::run, &task2);

    th1.join();
    th2.join();

    return 0;

}

//
// Created by razbik@wincs.cs.bgu.ac.il on 1/1/19.
//

#include "ReadFromServer.h"
#include <mutex>

ReadFromServer::ReadFromServer(ConnectionHandler &connectionHandler, EncoderDecoder &encoderDecoder): connectionHandler(connectionHandler), encoderDecoder(encoderDecoder) {}

ReadFromServer::~ReadFromServer() {
    connectionHandler.close();
}

void ReadFromServer::run(){
    while (!connectionHandler.isClose()){
        encoderDecoder.decode();
    }

}
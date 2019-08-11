//
// Created by razbik@wincs.cs.bgu.ac.il on 1/1/19.
//

#ifndef CLIENT_READFROMSERVER_H
#define CLIENT_READFROMSERVER_H


#include <mutex>
#include "connectionHandler.h"
#include "EncoderDecoder.h"
#include <boost/thread.hpp>

class ReadFromServer {
private:
    ConnectionHandler& connectionHandler;
    EncoderDecoder& encoderDecoder;
public:
    ReadFromServer (ConnectionHandler &connectionHandler, EncoderDecoder &encoderDecoder);
    virtual ~ReadFromServer();
    void run();
};


#endif //CLIENT_READFROMSERVER_H

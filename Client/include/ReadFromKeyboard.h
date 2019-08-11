//
// Created by razbik@wincs.cs.bgu.ac.il on 1/1/19.
//

#ifndef CLIENT_READFROMKEYBOARD_H
#define CLIENT_READFROMKEYBOARD_H


#include <mutex>
#include "connectionHandler.h"
#include "EncoderDecoder.h"
#include <boost/thread.hpp>

class ReadFromKeyboard {
private:
    ConnectionHandler& connectionHandler;
    EncoderDecoder& encoderDecoder;
public:
    ReadFromKeyboard (ConnectionHandler &connectionHandler, EncoderDecoder &encoderDecoder);
    virtual ~ReadFromKeyboard() = default;
    void run();
};



#endif //CLIENT_READFROMKEYBOARD_H

//
// Created by razbik@wincs.cs.bgu.ac.il on 1/2/19.
//

#ifndef CLIENT_ENCODERDECODER_H
#define CLIENT_ENCODERDECODER_H

#include "connectionHandler.h"

class EncoderDecoder {
private:
    ConnectionHandler& connectionHandler;
public:
    EncoderDecoder(ConnectionHandler &connectionHandler);
    virtual ~EncoderDecoder() = default;
    void encode(std:: string line);
    void decode();
    short bytesToShort(char* bytesArr);
    void shortToBytes(short num, char* bytesArr);
};


#endif //CLIENT_ENCODERDECODER_H

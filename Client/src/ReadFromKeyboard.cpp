//
// Created by razbik@wincs.cs.bgu.ac.il on 1/1/19.
//

#include "ReadFromKeyboard.h"
#include "EncoderDecoder.h"
#include "connectionHandler.h"
#include <mutex>

ReadFromKeyboard::ReadFromKeyboard(ConnectionHandler &connectionHandler, EncoderDecoder &encoderDecoder): connectionHandler(connectionHandler), encoderDecoder(encoderDecoder){}


void ReadFromKeyboard::run(){
    while (!connectionHandler.isClose()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        encoderDecoder.encode(line);
    }

}

//
// Created by razbik@wincs.cs.bgu.ac.il on 1/2/19.
//

#include "EncoderDecoder.h"



EncoderDecoder:: EncoderDecoder(ConnectionHandler& connectionHandler): connectionHandler(connectionHandler) {}

void EncoderDecoder::encode(std:: string line){
    size_t found = line.find(" ");
    std:: string command = line.substr(0,found);   // cut the command for making the opcode
    char bytesArr[2];
    if(command == "REGISTER" || command == "LOGIN"){
        if(command == "REGISTER"){shortToBytes(1, bytesArr);}
        else if(command == "LOGIN"){shortToBytes(2, bytesArr);}
        connectionHandler.sendBytes(bytesArr, 2);
        line = line.substr(found+1);  // remove the command from the line
        found = line.find(" ");
        command = line.substr(0,found);  // find the username
        connectionHandler.sendLine(command);
        line = line.substr(found+1);    // cut the username from the line , and keep only the password
        connectionHandler.sendLine(line);
    }
    else if(command == "LOGOUT"|| command == "USERLIST"){
        if(command == "LOGOUT"){
            shortToBytes(3, bytesArr);
            connectionHandler.sendBytes(bytesArr, 2);
            while (!connectionHandler.getLoggedOut()){

            }
            connectionHandler.setLoggedOut(false);
        }
        else if(command == "USERLIST"){
            shortToBytes(7, bytesArr);
            connectionHandler.sendBytes(bytesArr, 2);
        }

    }
    else if(command == "FOLLOW"){
        shortToBytes(4, bytesArr);
        connectionHandler.sendBytes(bytesArr, 2);
        char followArry[1];
        line = line.substr(found+1);    // remove the command from the line
        found = line.find(" ");
        command = line.substr(0,found);   // find the follow or unfollow char
        if(command[0] == '0'){
            followArry[0] = '\0';
        } else{
            followArry[0] = 1;
        }
        connectionHandler.sendBytes(followArry, 1);
        line = line.substr(found+1);   // cut the 0 or 1 char from the string
        found = line.find(" ");
        command = line.substr(0, found);    // keep the num of users
        int numOfUsers = std:: stoi(command);   // change the num of users from string to int
        char numOfUsersArr[2];
        shortToBytes(numOfUsers, numOfUsersArr);
        connectionHandler.sendBytes(numOfUsersArr, 2);
        line = line.substr(found +1);     // cut the num of users and keep only the username list
        while (numOfUsers > 1){        // >1 because the last name is without space after him
            found = line.find(" ");
            command = line.substr(0, found);
            connectionHandler.sendLine(command);
            line = line.substr(found+1);
            numOfUsers--;
        }
        connectionHandler.sendLine(line);   // send the last name in the list
    }
    else if (command == "POST" || command == "STAT"){
        if(command == "POST"){shortToBytes(5, bytesArr);}
        else if(command == "STAT"){shortToBytes(8, bytesArr);}
        connectionHandler.sendBytes(bytesArr, 2);
        line = line.substr(found+1);
        connectionHandler.sendLine(line);
    }
    else if (command == "PM"){
        shortToBytes(6, bytesArr);
        connectionHandler.sendBytes(bytesArr, 2);
        line = line.substr(found+1);
        found = line.find(" ");
        command = line.substr(0, found);
        connectionHandler.sendLine(command);
        line = line.substr(found+1);
        connectionHandler.sendLine(line);
    }
}

void EncoderDecoder:: decode(){
    char opcode[2];
    connectionHandler.getBytes(opcode, 2);
    short op = bytesToShort(opcode);
    if(op == 9){
        char type [1];
        connectionHandler.getBytes(type,1);
        short type1 = bytesToShort(type);
        std::string type2 ;  // keep the string "PM" or "POST"
        if (type1 == 0) {
            type2 = "PM";
        }
            else{
                type2 = "Public";
            }

        std::string postingUser;
        connectionHandler.getLine(postingUser);
        std::string content;
        connectionHandler.getLine(content);
        std::cout<<"NOTIFICATION " << type2 << " " << postingUser.substr(0,postingUser.length()-1) << " " << content.substr(0,content.length()-1) << std::endl;

    } else if(op == 10){
        char messageOpcodeArr [2];
        connectionHandler.getBytes(messageOpcodeArr,2);
        short messageOpcdoe = bytesToShort(messageOpcodeArr);
        if (messageOpcdoe == 1 || messageOpcdoe == 2 || messageOpcdoe == 5 || messageOpcdoe == 6 ){
            std::cout<<"ACK " << messageOpcdoe << std::endl;
        }
        else if (messageOpcdoe == 3){
            std::cout<<"ACK " << messageOpcdoe << std::endl;
            connectionHandler.close();
            connectionHandler.setLoggedOut(true);
        }
        else if (messageOpcdoe == 4 || messageOpcdoe == 7){
            char numOfUsersArr [2];
            connectionHandler.getBytes(numOfUsersArr,2);
            short numOfUsers = bytesToShort(numOfUsersArr);
            std::string usernames;
            for(int i=0 ; i<numOfUsers; i++){
                std::string username;
                connectionHandler.getLine(username);
                username = username.substr(0,username.size()-1);
                usernames = usernames + username +" ";
            }
            std::cout << "ACK " << messageOpcdoe << " " << numOfUsers << " " << usernames << std::endl;
        }
        else if(messageOpcdoe == 8){
            char numOfPostsArr[2];
            connectionHandler.getBytes(numOfPostsArr,2);
            short numOfPosts = bytesToShort(numOfPostsArr);
            char numOfFollowersArr[2];
            connectionHandler.getBytes(numOfFollowersArr,2);
            short numOfFollowers = bytesToShort(numOfFollowersArr);
            char numOfFollowingArr[2];
            connectionHandler.getBytes(numOfFollowingArr,2);
            short numOfFollowing = bytesToShort(numOfFollowingArr);
            std::cout<< "ACK " << messageOpcdoe << " " << numOfPosts <<" "<< numOfFollowers << " "<< numOfFollowing << std::endl;
        }
    }
    else if(op == 11){
        char messageOpcodeArr [2];
        connectionHandler.getBytes(messageOpcodeArr,2);
        short messageOpcdoe = bytesToShort(messageOpcodeArr);
        std::cout<<"ERROR " << messageOpcdoe << std::endl;
        if(messageOpcdoe==3)
            connectionHandler.setLoggedOut(true);

    }
}

short EncoderDecoder:: bytesToShort(char* bytesArr){
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void EncoderDecoder:: shortToBytes(short num, char* bytesArr){
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}
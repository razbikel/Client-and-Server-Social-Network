package bgu.spl.net.api;

import bgu.spl.net.api.bidi.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

public class EncoderDecoder<T> implements MessageEncoderDecoder<T>{

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short opcode = 0;
    private int numOfZeros = 0;
    private int lenAfterLastZero = 0;
    private String userName;
    private String password;
    private boolean follow = false;
    private short numOfUsers;
    private LinkedList<String> userNameList;
    private String content;
    private Vector<byte[]> userNameListBytes;

    public T decodeNextByte(byte nextByte) {
        T output = null;
        pushByte(nextByte);
        if(len == 2){
            opcode = bytesToShort(bytes);
        }
        if(opcode != 0){
            if(opcode == 1 || opcode == 2){
                output = this.buildRegisterOrLogin(nextByte);
            }
            else if(opcode == 3){
                output = this.buildLogout(nextByte);
            }
            else if(opcode == 4){
                output = this.buildFollowUnfollow(nextByte);
            }
            else if(opcode == 5){
                output = this.buildPost(nextByte);
            }
            else if(opcode == 6){
                output = this.buildPm(nextByte);
            }
            else if(opcode == 7){
                output = this.buildUserlist(nextByte);
            }
            else if(opcode == 8){
                output = this.buildStat(nextByte);
            }
        }
        return output;
    }

    @Override
    public byte[] encode(T message){
        byte[] output = null;
        if(message instanceof Notification){
            output = this.buildNotification(((Notification) message).getNotificationType() , ((Notification) message).getPostingUser() , ((Notification) message).getContent());

        }
        else if (message instanceof AckGeneral){
            output = buildAckGeneral(((AckGeneral) message).getMessageOpcode());

        }
        else if(message instanceof AckFollow){
            userNameListBytes = new Vector<>();
            output = this.buildAckFollowOrAckUserList((short) 4,((AckFollow) message).getUserNameList(),((AckFollow) message).getNumOfUsers());
        }
        else if(message instanceof AckStat){
            output = this.buildAckStat(((AckStat) message).getNumPosts(), ((AckStat) message).getNumFollowers(), ((AckStat) message).getNumFollowing());

        }
        else if(message instanceof AckUserList){
            userNameListBytes = new Vector<>();
            output = this.buildAckFollowOrAckUserList((short) 7,((AckUserList) message).getUserNameList(),((AckUserList) message).getNumOfUsers());
        }
        else if (message instanceof ErrorNot){
            output = this.buildErrorNot(((ErrorNot) message).getMessageOpcode());
        }
        return output;
    }


    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    private byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private T buildRegisterOrLogin(byte nextByte){
        Message output = null;
        if(nextByte == '\0'){
            numOfZeros++;
        }
        if(numOfZeros == 1){
            if (userName == null) {
                lenAfterLastZero = len;
                userName = new String(bytes, 2, len-3, StandardCharsets.UTF_8);
            }
        }
        else if(numOfZeros == 2){
            password = new String(bytes,lenAfterLastZero, len-lenAfterLastZero-1, StandardCharsets.UTF_8);
            if (opcode == 1) {
                output = new Register(userName, password);
            }
            else {
                output = new Login(userName, password);
            }
            opcode = 0;
            userName = null;
            password = null;
            len = 0;
            lenAfterLastZero = 0;
            numOfZeros = 0;
        }
        return (T) output;
    }

    private T buildLogout(byte nextByte){
        Logout logout = new Logout();
        opcode = 0;
        len = 0;
        return (T) logout;
    }

    private T buildFollowUnfollow(byte nextByte){
        FollowUnfollow followUnfollow = null;
        if(len == 3){
            if(bytes[2] == '\0'){
                follow = true;
            }
        }
        else if(len == 5){
            byte[] users = new byte[2];
            users[0] = bytes[3];
            users[1] = bytes[4];
            numOfUsers = bytesToShort(users);
        }
        else if(len > 5){
            if (nextByte == '\0'){
                numOfZeros++;
            }
            if (numOfUsers != 0){
                if(numOfZeros < numOfUsers){
                    if(numOfZeros != 0){
                        if(numOfZeros == 1){
                            userNameList = new LinkedList<>();
                            String userName = new String(bytes, 5, len-6, StandardCharsets.UTF_8);
                            userNameList.add(userName);
                            lenAfterLastZero = len;
                        }
                        else {
                            String userName = new String(bytes, lenAfterLastZero, len-lenAfterLastZero-1, StandardCharsets.UTF_8);
                            userNameList.add(userName);
                            lenAfterLastZero = len;
                        }
                    }
                }
                else {
                    if(numOfZeros == 1){
                        userNameList = new LinkedList<>();
                        String userName = new String(bytes, 5, len-6, StandardCharsets.UTF_8);
                        userNameList.add(userName);
                    }
                    else {
                        String userName = new String(bytes, lenAfterLastZero, len-lenAfterLastZero-1, StandardCharsets.UTF_8);
                        userNameList.add(userName);
                    }
                    followUnfollow = new FollowUnfollow(follow, numOfUsers, userNameList);
                    opcode = 0;
                    follow = false;
                    numOfUsers = 0;
                    userNameList = null;
                    len = 0;
                    lenAfterLastZero = 0;
                    numOfZeros = 0;
                }
            }
            else {
                userNameList = new LinkedList<>();
                followUnfollow = new FollowUnfollow(follow, numOfUsers, userNameList);
                opcode = 0;
                follow = false;
                numOfUsers = 0;
                userNameList = null;
                len = 0;
                lenAfterLastZero = 0;
                numOfZeros = 0;
            }
        }
        return (T) followUnfollow;
    }

    private T buildPost(byte nextByte){
        Post post = null;
        if(nextByte == '\0'){
            numOfZeros++;
        }
        if(numOfZeros == 1){
            content = new String(bytes, 2, len -3, StandardCharsets.UTF_8);
            post = new Post(content);
            opcode = 0;
            content = null;
            len = 0;
            lenAfterLastZero = 0;
            numOfZeros = 0;
        }
        return (T) post;
    }

    private T buildPm(byte nextByte){
        PrivateMessage pm = null;
        if(nextByte == '\0'){
            numOfZeros++;
        }
        if(numOfZeros == 1){
            if (userName == null) {
                lenAfterLastZero = len;
                userName = new String(bytes, 2, len-3, StandardCharsets.UTF_8);
            }
        }
        else if(numOfZeros == 2){
            content = new String(bytes,lenAfterLastZero, len-lenAfterLastZero-1, StandardCharsets.UTF_8);
            pm = new PrivateMessage(userName, content);
            opcode = 0;
            userName = null;
            content = null;
            len = 0;
            lenAfterLastZero = 0;
            numOfZeros = 0;
        }
        return (T) pm;
    }

    private T buildUserlist(byte nextByte){
        Userlist userlist = new Userlist();
        opcode = 0;
        len = 0;
        return (T) userlist;
    }

    private T buildStat(byte nextByte){
        Stat stat = null;
        if(nextByte == '\0'){
            numOfZeros++;
        }
        if(numOfZeros == 1){
            userName = new String(bytes, 2, len -3, StandardCharsets.UTF_8);
            stat = new Stat(userName);
            opcode = 0;
            userName = null;
            len = 0;
            lenAfterLastZero = 0;
            numOfZeros = 0;
        }
        return (T) stat;
    }

    private byte[] buildAckGeneral(short messageOpcode){
        byte[] opcode = shortToBytes((short) 10);
        byte[] messageOpcodeA = shortToBytes(messageOpcode);
        byte[] output = new byte[4];
        output[0] = opcode[0];
        output[1] = opcode[1];
        output[2] = messageOpcodeA[0];
        output[3] = messageOpcodeA[1];
        return output;
    }

    private byte[] buildNotification(char type , String postingUser , String content){
        byte[] postingUserA = postingUser.getBytes();
        byte[] contentA = content.getBytes();
        byte [] output = new byte[5 + postingUserA.length + contentA.length];
        byte[] opcode = shortToBytes((short) 9);
        output[0] = opcode[0];
        output[1] = opcode[1];
        output[2] = (byte) type;
        int i = 3;
        for (int j = 0; j < postingUserA.length; j++){
            output[i] = postingUserA[j];
            i++;
        }
        output[i] = (byte)'\0';
        i++;
        for (int j = 0; j < contentA.length; j++){
            output[i] = contentA[j];
            i++;
        }
        output[i] = (byte) '\0';
        return output;
    }

    private byte[] buildErrorNot(short messageOpcode){
        byte [] output = new byte[4];
        byte[] opcode = shortToBytes((short) 11);
        byte[] messageOpcodeA =shortToBytes(messageOpcode) ;
        output[0] = opcode[0];
        output[1] = opcode[1];
        output[2] = messageOpcodeA[0];
        output[3] = messageOpcodeA[1];
        return output;
    }

    private byte[] buildAckFollowOrAckUserList(short messageOpcode,LinkedList<String> userNameList , short numOfUsers){
        int counter = 0; // count the lenght of all the usernames in bytes
        for (int i = 0 ; i<numOfUsers ;i++){
            byte[] userName = userNameList.get(i).getBytes();
            this.userNameListBytes.add(userName);
            counter = counter+userName.length;
        }
        byte[] output = new byte[counter + numOfUsers + 6];
        byte[] opcode = buildAckGeneral(messageOpcode);
        output[0] = opcode[0];
        output[1] = opcode[1];
        output[2] = opcode[2];
        output[3] = opcode[3];
        byte[] numOfUsersA = shortToBytes(numOfUsers);
        output[4] = numOfUsersA[0];
        output[5] = numOfUsersA[1];
        int start = 6;
        for (int i = 0; i<userNameListBytes.size(); i++){
            start = pushBytes(start, output, userNameListBytes.get(i));
        }
        return output;
    }

    private int pushBytes (int start,byte[] addTo,byte[] addFrom){  // add bytes from one array to another , and return the index where is should start to push next time
        int output = start;
        for (int i = 0 ; i<addFrom.length; i++){
            addTo[output] = addFrom[i];
            output++;
        }
        addTo[output] = '\0';
        return output+1;
    }

    private byte[] buildAckStat(short numOfPosts, short numOfFollowers, short numOfFollowing){
        byte[] output = new byte[10];
        byte[] opcode = buildAckGeneral((short) 8);
        output[0] = opcode[0];
        output[1] = opcode[1];
        output[2] = opcode[2];
        output[3] = opcode[3];
        byte[] numOfPostsA = shortToBytes(numOfPosts);
        byte[] numOfFollowersA = shortToBytes(numOfFollowers);
        byte[] numOfFollwingA = shortToBytes(numOfFollowing);
        output[4] = numOfPostsA[0];
        output[5] = numOfPostsA[1];
        output[6] = numOfFollowersA[0];
        output[7] = numOfFollowersA[1];
        output[8] = numOfFollwingA[0];
        output[9] = numOfFollwingA[1];
        return output;
    }
}

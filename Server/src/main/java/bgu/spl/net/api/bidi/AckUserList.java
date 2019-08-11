package bgu.spl.net.api.bidi;

import java.util.LinkedList;

public class AckUserList extends Ack {
    private Short numOfUsers;
    private LinkedList<String> userNameList;

    public AckUserList (Short MessageOpcode, Short numOfUsers, LinkedList<String> userNameList){
        super(MessageOpcode);
        this.numOfUsers = numOfUsers;
        this.userNameList = userNameList;
    }

    public Short getNumOfUsers(){
        return this.numOfUsers;
    }

    public LinkedList<String> getUserNameList(){
        return this.userNameList;
    }
}

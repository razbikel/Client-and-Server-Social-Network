package bgu.spl.net.api.bidi;

import java.util.LinkedList;
import java.util.List;

public class FollowUnfollow implements Message {
    private boolean follow;
    private Short Opcode;
    private Short NumOfUsers;
    private LinkedList<String> userNameList;

    public FollowUnfollow(boolean follow , Short NumOfUsers , LinkedList<String> userNameList){
        this.userNameList = new LinkedList<>();
        this.Opcode = 4;
        this.follow = follow;
        this.userNameList.addAll(userNameList);
        this.NumOfUsers = NumOfUsers;
    }

    public Short getOpcode(){
        return this.Opcode;
    }

    public boolean isFollow(){
        return this.follow;
    }

    public List<String> getUserNameList(){
        return this.userNameList;
    }

    public Short getNumOfUsers(){
        return this.NumOfUsers;
    }
}

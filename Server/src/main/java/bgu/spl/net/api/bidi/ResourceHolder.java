package bgu.spl.net.api.bidi;

import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceHolder {
    private ConcurrentHashMap<String,User> registerdByUsername;  // key - username
    private LinkedList<String> usernameRegisterd;                // data structure that will keep the registered usernames by their registration's order
    private ConcurrentHashMap<Integer,User> logedInById;    // key - id
    private Vector<Message> postNpm; // keep all the posts and private messages

    public ResourceHolder(){
        this.registerdByUsername = new ConcurrentHashMap<>();
        this.usernameRegisterd = new LinkedList<>();
        this.logedInById = new ConcurrentHashMap<>();
        this.postNpm = new Vector<>();
    }


    public ConcurrentHashMap<String,User> getRegisterdByUsername(){
        return this.registerdByUsername;
    }

    public ConcurrentHashMap<Integer,User>getLogedInById(){
        return this.logedInById;
    }

    public LinkedList<String> getUsernameRegisterd(){
        return this.usernameRegisterd;
    }

    public void addRegister(User user){
        synchronized (user) {
            registerdByUsername.putIfAbsent(user.getUserName(), user);
            usernameRegisterd.add(user.getUserName());
        }
    }

    public void addLogedIn(int id,User user) {
        logedInById.putIfAbsent(id, user);
    }

    public void addPostOrPm(Message m){
        postNpm.add(m);
    }

    public Vector<Message> getPostNpm(){
        return this.postNpm;
    }

    public void logout(int connectionId){
        logedInById.remove(connectionId);
    }


}

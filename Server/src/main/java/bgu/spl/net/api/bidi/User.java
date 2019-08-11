package bgu.spl.net.api.bidi;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class User {
    private String userName;
    private String password;
    private int id;
    private ConcurrentHashMap<Integer,User> following;
    private ConcurrentHashMap<String,User> followers;
    private short numOfPosts;
    private LinkedBlockingQueue<Notification> notificationsQueue; // keep the notifications that was sent when the user logged out , and get them when logged in

    public User(String username , String password, int id){
        this.userName = username;
        this.password = password;
        this.id = id;
        this.numOfPosts = 0;
        this.following = new ConcurrentHashMap<>();
        this.followers = new ConcurrentHashMap<>();
        this.notificationsQueue = new LinkedBlockingQueue();
    }

    public String getUserName(){
        return this.userName;
    }

    public LinkedBlockingQueue<Notification> getNotificationsQueue(){
        return this.notificationsQueue;
    }

    public void addNotification(Notification n){
        this.notificationsQueue.add(n);
    }

    public int getId(){
        return this.id;
    }

    public String getPassword(){
        return this.password;
    }

    public boolean addFollowing(int connectionId,User user){
        if (this.following.containsKey(connectionId)){
            return false;
        }
        following.putIfAbsent(connectionId,user);
        return true;
    }

    public void increaseNumOfPosts(){
        this.numOfPosts++;
    }

    public short getNumOfPosts(){
        return this.numOfPosts;
    }

    public boolean addFollower(User user){
        if (this.followers.containsKey(user.getUserName())){
            return false;
        }
        followers.putIfAbsent(user.getUserName(),user);
        return true;
    }

    public boolean unfollow(int connectionId){
        if (!(this.following.containsKey(connectionId))){
            return false;
        }
        this.following.remove(connectionId);
        return true;

    }

    public boolean removeFollower(User user){
        if (!(this.followers.containsKey(user.getUserName()))){
            return false;
        }
        this.followers.remove(user.getUserName());
        return true;
    }

    public ConcurrentHashMap<Integer,User> getFollowing(){
        return this.following;
    }

    public ConcurrentHashMap<String,User> getFollowers(){
        return this.followers;
    }
}

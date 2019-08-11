package bgu.spl.net.api.bidi;


import java.util.LinkedList;
import java.util.Map;

public class BidiProtocolImpl<T> implements BidiMessagingProtocol<T> {

    private ConnectionsImpl connections;
    private int connectionId;
    private boolean shouldTerminate;
    private ResourceHolder resourceHolder;

    public BidiProtocolImpl(ResourceHolder resourceHolder){
        this.resourceHolder = resourceHolder;
        this.shouldTerminate = false;
    }

    public void start(int connectionId, Connections connections) {
        this.connections = (ConnectionsImpl) connections;
        this.connectionId = connectionId;
    }

    public void process(T message) {
        if (message instanceof Register){
            if(resourceHolder.getRegisterdByUsername().containsKey(((Register) message).getUserName()) || !(connections.getActiveClients().containsKey(connectionId))){
                ErrorNot errorNot = new ErrorNot((short) 1);
                connections.send(connectionId, errorNot);
            }
            else{
                User user = new User(((Register) message).getUserName() , ((Register) message).getPassword(), connectionId);
                resourceHolder.addRegister(user);
                AckGeneral ack = new AckGeneral((short) 1);
                connections.send(connectionId, ack);
            }
        }
        else if(message instanceof Login){
            if(!(resourceHolder.getRegisterdByUsername().containsKey(((Login) message).getUserName())) ||
                    !(resourceHolder.getRegisterdByUsername().get(((Login) message).getUserName()).getPassword().equals(((Login) message).getPassword()))||
                    resourceHolder.getLogedInById().containsKey(connectionId)){
                ErrorNot errorNot = new ErrorNot((short) 2);
                connections.send(connectionId, errorNot);
            }
            else {
                User user = resourceHolder.getRegisterdByUsername().get(((Login) message).getUserName());
                resourceHolder.addLogedIn(connectionId, user);
                AckGeneral ack = new AckGeneral((short) 2);
                connections.send(connectionId, ack);
                while (!(user.getNotificationsQueue().isEmpty())){
                    connections.send(connectionId, user.getNotificationsQueue().poll());
                }
            }
        }
        else if (message instanceof Logout){
            if(!(resourceHolder.getLogedInById().containsKey(connectionId))){
                ErrorNot errorNot = new ErrorNot((short) 3);
                connections.send(connectionId, errorNot);
            }
            else {
                synchronized (resourceHolder.getLogedInById().get(connectionId)) {
                    resourceHolder.logout(connectionId);
                    AckGeneral ack = new AckGeneral((short) 3);
                    connections.send(connectionId, ack);
                    shouldTerminate = true;
                    connections.disconnect(connectionId);
                }
            }
        }
        else if (message instanceof FollowUnfollow){
            if(!(resourceHolder.getLogedInById().containsKey(connectionId))){
                ErrorNot errorNot = new ErrorNot((short) 4);
                connections.send(connectionId, errorNot);
            }
            else{
                short counter = 0;
                LinkedList<String> successful = new LinkedList<>();
                if(((FollowUnfollow) message).isFollow()){
                    for(int i = 0; i<((FollowUnfollow) message).getUserNameList().size(); i++){
                        if(resourceHolder.getRegisterdByUsername().containsKey(((FollowUnfollow) message).getUserNameList().get(i))) {
                            int id = resourceHolder.getRegisterdByUsername().get(((FollowUnfollow) message).getUserNameList().get(i)).getId();  // id of the user that we want to follow
                            boolean followed = resourceHolder.getLogedInById().get(connectionId).addFollowing(id, resourceHolder.getRegisterdByUsername().get(((FollowUnfollow) message).getUserNameList().get(i)));
                            resourceHolder.getLogedInById().get(id).addFollower(resourceHolder.getLogedInById().get(connectionId)); // add me to the followers list of the other user
                            if (followed) {
                                successful.add(((FollowUnfollow) message).getUserNameList().get(i));
                                counter++;
                            }
                        }
                    }
                    if(counter == 0 ){
                        ErrorNot errorNot = new ErrorNot((short) 4);
                        connections.send(connectionId, errorNot);

                    }
                    else {
                        AckFollow ack = new AckFollow((short) 4, counter, successful);
                        connections.send(connectionId, ack);
                    }
                }
                else {
                    for(int i = 0; i<((FollowUnfollow) message).getUserNameList().size(); i++){
                        if(resourceHolder.getRegisterdByUsername().containsKey(((FollowUnfollow) message).getUserNameList().get(i))){
                            int id = resourceHolder.getRegisterdByUsername().get(((FollowUnfollow) message).getUserNameList().get(i)).getId();  // id of the user that we want to unfollow
                            boolean unFollowed = resourceHolder.getLogedInById().get(connectionId).unfollow(id);
                            resourceHolder.getRegisterdByUsername().get(((FollowUnfollow) message).getUserNameList().get(i)).removeFollower(resourceHolder.getLogedInById().get(connectionId));
                            if (unFollowed){
                                successful.add(((FollowUnfollow) message).getUserNameList().get(i));
                                counter++;
                            }
                        }
                    }
                    if(counter == 0 ){
                        ErrorNot errorNot = new ErrorNot((short) 4);
                        connections.send(connectionId, errorNot);
                    }
                    else {
                        AckFollow ack = new AckFollow((short) 4, counter, successful);
                        connections.send(connectionId, ack);
                    }
                }
            }
        }
        else if(message instanceof Post){
            if(!(resourceHolder.getLogedInById().containsKey(connectionId))){
                ErrorNot errorNot = new ErrorNot((short) 5);
                connections.send(connectionId, errorNot);
            }
            else {
                resourceHolder.addPostOrPm((Message)message);
                resourceHolder.getLogedInById().get(connectionId).increaseNumOfPosts();
                String temp = ((Post) message).getContent(); // copy the content
                LinkedList<String> usernameList = new LinkedList<>();  // keep all the usernames from the content
                while(temp.length()>0){
                    int indexStart = temp.indexOf("@");
                    if (indexStart != -1) {
                        int indexEnd = temp.indexOf(" ", indexStart);
                        if(indexEnd != -1) {
                            String username = temp.substring(indexStart + 1, indexEnd);
                            temp = temp.substring(indexEnd);
                            if (!(resourceHolder.getLogedInById().get(connectionId).getFollowers().containsKey(username)) && resourceHolder.getRegisterdByUsername().containsKey(username) && !(usernameList.contains(username))){
                                usernameList.add(username);
                            }
                        }
                        else {
                            String username = temp.substring(indexStart+1);
                            temp = "";
                            if (!(resourceHolder.getLogedInById().get(connectionId).getFollowers().containsKey(username)) && resourceHolder.getRegisterdByUsername().containsKey(username) && !(usernameList.contains(username))){
                                usernameList.add(username);
                            }
                        }
                    }
                    else{
                        temp = "";
                    }
                }
                AckGeneral ack = new AckGeneral((short)5);
                connections.send(connectionId,ack);
                Notification notification = new Notification('1', resourceHolder.getLogedInById().get(connectionId).getUserName(), ((Post) message).getContent());
                for(int i = 0; i < usernameList.size(); i++) {
                    synchronized (resourceHolder.getRegisterdByUsername().get(usernameList.get(i))) {
                        User user = resourceHolder.getRegisterdByUsername().get(usernameList.get(i));
                        if(resourceHolder.getLogedInById().containsKey(user.getId())) {
                            connections.send(user.getId(), notification);
                        } else{
                            user.getNotificationsQueue().add(notification);
                        }
                    }
                }
                for (Map.Entry<String, User> entry: resourceHolder.getLogedInById().get(connectionId).getFollowers().entrySet()) {
                    synchronized (resourceHolder.getRegisterdByUsername().get(entry.getKey())) {
                        User user = resourceHolder.getRegisterdByUsername().get(entry.getKey());
                        if(resourceHolder.getLogedInById().containsKey(user.getId())) {
                            connections.send(user.getId(), notification);
                        } else{
                            user.getNotificationsQueue().add(notification);
                        }
                    }
                }
            }
        }
        else if (message instanceof PrivateMessage){
            if(!(resourceHolder.getLogedInById().containsKey(connectionId)) || !(resourceHolder.getRegisterdByUsername().containsKey(((PrivateMessage) message).getUsername()))){
                ErrorNot errorNot = new ErrorNot((short) 6);
                connections.send(connectionId, errorNot);
            }
            else{
                resourceHolder.addPostOrPm((Message)message);
                Notification notification = new Notification('\0', resourceHolder.getLogedInById().get(connectionId).getUserName(), ((PrivateMessage) message).getContent());
                synchronized (resourceHolder.getRegisterdByUsername().get(((PrivateMessage) message).getUsername())){
                    User user = resourceHolder.getRegisterdByUsername().get(((PrivateMessage) message).getUsername());
                    if(resourceHolder.getLogedInById().containsKey(user.getId())) {
                        connections.send(user.getId(), notification);
                    } else{
                        user.getNotificationsQueue().add(notification);
                    }
                }
                AckGeneral ack = new AckGeneral((short)6);
                connections.send(connectionId,ack);
            }
        }
        else if (message instanceof Userlist){
            if(!(resourceHolder.getLogedInById().containsKey(connectionId))){
                ErrorNot errorNot = new ErrorNot((short) 7);
                connections.send(connectionId, errorNot);
            }
            else{
                AckUserList ack = new AckUserList((short) 7 , (short)resourceHolder.getUsernameRegisterd().size() , resourceHolder.getUsernameRegisterd());
                connections.send(connectionId,ack);
            }

        }
        else if (message instanceof Stat){
            if(!(resourceHolder.getLogedInById().containsKey(connectionId)) || !(resourceHolder.getRegisterdByUsername().containsKey(((Stat) message).getUsername()))) {
                ErrorNot errorNot = new ErrorNot((short) 8);
                connections.send(connectionId, errorNot);
            }
            else{
                synchronized (resourceHolder.getRegisterdByUsername().get(((Stat) message).getUsername())) {
                    User user = resourceHolder.getRegisterdByUsername().get(((Stat) message).getUsername());
                    AckStat ack = new AckStat((short) 8, user.getNumOfPosts(), (short) user.getFollowers().size(), (short) user.getFollowing().size());
                    connections.send(connectionId, ack);
                }
            }

        }


    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}







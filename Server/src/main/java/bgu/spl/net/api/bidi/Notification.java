package bgu.spl.net.api.bidi;

public class Notification implements Message {
    private Short Opcode;
    private char NotificationType;
    private String postingUser;
    private String content;

    public Notification(char NotificationType, String postingUser, String content){
        this.Opcode = 9;
        this.NotificationType = NotificationType;
        this.postingUser = postingUser;
        this.content = content;
    }
    public char getNotificationType(){
        return this.NotificationType;
    }

    public String getPostingUser(){
        return this.postingUser;
    }

    public String getContent(){
        return this.content;
    }

    public Short getOpcode(){
        return this.Opcode;
    }
}

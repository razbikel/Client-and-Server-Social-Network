package bgu.spl.net.api.bidi;

public class PrivateMessage implements Message {
    private Short Opcode;
    private String username;
    private String content;

    public PrivateMessage(String username, String content){
        this.username = username;
        this.content = content;
        this.Opcode = 6;
    }
    public Short getOpcode(){
        return this.Opcode;
    }
    public String getUsername(){
        return this.username;
    }
    public String getContent(){
        return this.content;
    }
}

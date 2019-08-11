package bgu.spl.net.api.bidi;

public class Post implements Message {
    private Short Opcode;
    private String content;

    public Post(String content){
        this.Opcode = 5;
        this.content = content;
    }

    public Short getOpcode(){
        return this.Opcode;
    }

    public String getContent(){
        return this.content;
    }
}

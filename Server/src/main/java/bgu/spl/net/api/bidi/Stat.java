package bgu.spl.net.api.bidi;

public class Stat implements Message {
    private Short Opcode;
    private String username;

    public Stat(String username){
        this.Opcode = 8;
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
}

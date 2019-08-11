package bgu.spl.net.api.bidi;

public class Userlist implements Message {
    private Short Opcode;

    public Userlist(){
        this.Opcode = 7;
    }
}

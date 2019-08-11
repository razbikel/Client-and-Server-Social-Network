package bgu.spl.net.api.bidi;

public class Logout implements Message {
    private Short Opcode;

    public Logout(){
        this.Opcode = 3;
    }

    public Short getOpcode(){
        return this.Opcode;
    }
}


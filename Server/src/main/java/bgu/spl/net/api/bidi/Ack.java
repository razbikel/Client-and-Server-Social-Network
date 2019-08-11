package bgu.spl.net.api.bidi;

public abstract class Ack implements Message {
    private Short Opcode;
    private Short MessageOpcode;

    public Ack(Short MessageOpcode){
        this.Opcode = 10;
        this.MessageOpcode = MessageOpcode;
    }

    public Short getMessageOpcode(){
        return this.MessageOpcode;
    }
}

package bgu.spl.net.api.bidi;

public class ErrorNot implements Message {
    private short Opcode;
    private short MessageOpcode;

    public ErrorNot(Short MessageOpcode){
        this.Opcode = 11;
        this.MessageOpcode = MessageOpcode;
    }

    public short getMessageOpcode(){
        return this.MessageOpcode;
    }

}

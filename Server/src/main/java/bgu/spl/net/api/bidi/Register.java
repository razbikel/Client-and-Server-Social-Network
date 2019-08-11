package bgu.spl.net.api.bidi;

public class Register implements Message {
    private String userName;
    private String password;
    private Short Opcode;

    public Register(String userName, String password){
        this.userName = userName;
        this.password = password;
        this.Opcode = 1;

    }
    public String getUserName(){
        return this.userName;
    }

    public String getPassword(){
        return this.password;
    }

    public Short getOpcode(){
        return this.Opcode;
    }
}

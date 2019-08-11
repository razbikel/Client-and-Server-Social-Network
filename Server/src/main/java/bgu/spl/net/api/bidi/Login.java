package bgu.spl.net.api.bidi;

public class Login implements Message {
    private String userName;
    private String password;
    private Short Opcode;

    public Login(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.Opcode = 2;
    }
    public String getUserName(){
        return this.userName;
    }
    public String getPassword(){ return this.password;}
    public Short getOpcode(){
        return this.Opcode;
    }
}

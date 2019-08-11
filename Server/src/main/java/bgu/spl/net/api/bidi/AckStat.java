package bgu.spl.net.api.bidi;

public class AckStat extends Ack {

    private Short numPosts;
    private Short numFollowers;
    private Short numFollowing;

    public AckStat(Short MessageOpcode, Short numPosts, Short numFollowers, Short numFollowing){
        super(MessageOpcode);
        this.numPosts = numPosts;
        this.numFollowers = numFollowers;
        this.numFollowing = numFollowing;
    }

    public Short getNumPosts(){
        return this.numPosts;
    }

    public Short getNumFollowers(){
        return this.numFollowers;
    }

    public Short getNumFollowing(){
        return this.numFollowing;
    }
}

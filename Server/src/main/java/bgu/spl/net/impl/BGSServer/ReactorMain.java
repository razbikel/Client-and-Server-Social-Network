package bgu.spl.net.impl.BGSServer;


import bgu.spl.net.api.EncoderDecoder;
import bgu.spl.net.api.bidi.BidiProtocolImpl;
import bgu.spl.net.api.bidi.ResourceHolder;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        ResourceHolder holder = new ResourceHolder();
        Server.reactor(
                Integer.decode(args[1]).intValue(),
                Integer.decode(args[0]).intValue(), //port
                () ->  new BidiProtocolImpl<>(holder), //protocol factory
                EncoderDecoder::new //message encoder decoder factory
        ).serve();
    }

}

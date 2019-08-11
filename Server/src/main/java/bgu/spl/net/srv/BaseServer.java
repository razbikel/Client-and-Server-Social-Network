package bgu.spl.net.srv;

import bgu.spl.net.api.EncoderDecoder;
import bgu.spl.net.api.bidi.BidiProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiProtocolImpl<T>> protocolFactory;
    private final Supplier<EncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private ConnectionsImpl connections;
    private AtomicInteger counterId;

    public BaseServer(
            int port,
            Supplier<BidiProtocolImpl<T>> protocolFactory,
            Supplier<EncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
		this.connections = new ConnectionsImpl();
		this.counterId = new AtomicInteger(0);
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                int id = counterId.getAndIncrement();

                BidiProtocolImpl<T> protocol = protocolFactory.get();

                protocol.start(id, connections);

                Socket clientSock = serverSock.accept();

                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocol);

                connections.addActiveClient(handler , id);

                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}

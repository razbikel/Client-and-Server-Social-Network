package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler> activeClients;

    public ConnectionsImpl(){
        activeClients = new ConcurrentHashMap<>();
    }

    public boolean send(int connectionId, T msg){
        boolean hasSend = false;
        if (activeClients.get(connectionId) != null){
            activeClients.get(connectionId).send(msg);
            hasSend = true;
        }
        return hasSend;
    }

    public void broadcast(T msg){
        for (Map.Entry<Integer,ConnectionHandler> entry: activeClients.entrySet()) {
            entry.getValue().send(msg);
        }
    }

    public void disconnect(int connectionId){
        activeClients.remove(connectionId);
    }

    public void addActiveClient(ConnectionHandler connectionHandler, int id){
        activeClients.putIfAbsent(id,connectionHandler);
    }

    public ConcurrentHashMap<Integer, ConnectionHandler> getActiveClients(){
        return this.activeClients;
    }
}

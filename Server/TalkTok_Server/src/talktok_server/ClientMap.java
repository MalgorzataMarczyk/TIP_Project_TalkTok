package talktok_server;

import java.util.HashMap;
import java.util.Map;

public class ClientMap {
    private static Map clientMap = new HashMap<String,ClientInfo>();
    
    public static void addObject(String key, ClientInfo info){
        clientMap.put(key, info);
        ClientThread.updateClients();
    }
    
    public static ClientInfo getObject(String key){
        return (ClientInfo)clientMap.get(key);
    }
    
    public static String getClientStatus(String key){
        ClientInfo clientInfo = ((ClientInfo)clientMap.get(key));
        if(clientInfo == null)
            return ClientInfo.NIEDOSTEPNY;
        return ((ClientInfo)clientMap.get(key)).getStatus();
    }
    
    public static void removeClient(String key){
        clientMap.remove(key);
    }
    
    public static void repleceClientInfo(String key, ClientInfo info){
        clientMap.replace(key, info);
    }
    
}

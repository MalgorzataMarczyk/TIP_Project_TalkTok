package talktok_server;

import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Lena
 */
public class Talktok_Server {

    public static ArrayList<ClientThread> clients = new ArrayList<>();
    private static ServerGUI gui;

    public static void main(String[] args) {
        
        try{  
Class.forName("com.mysql.jdbc.Driver");  
Connection con=DriverManager.getConnection(  
"jdbc:mysql://localhost:3306/tip?characterEncoding=latin1&useConfigs=maxPerformance","root","password");  
//root i password musisz ustawic tak jak masz na wlasnej bazie w mysql 
Statement stmt=con.createStatement();  
ResultSet rs=stmt.executeQuery("select * from users;");  
while(rs.next())  
System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));  
con.close();  
}catch(Exception e){ System.out.println(e);}
        
        int portNumber = 3003;
        boolean listening = true;
        int count = 0;

        gui = new ServerGUI();
        gui.setVisible(true);

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);

            while (listening) {
                ClientThread clientThread = new ClientThread(serverSocket.accept(), count);
                clientThread.start();
                count++;
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void refreshClients() {
        String [] clientList = new String[clients.size()];
        int count = 0;
        for (ClientThread c : clients) {
            clientList[count] = c.getHostname();
            count++;
        }
        gui.setList(clientList);
    }

    public static void connectClient(ClientThread clientThread) {
        clients.add(clientThread);
        refreshClients();
        gui.setOutputArea(clients.get(clients.size() - 1).getHostname() + " connected");
    }

    public static void disconnectClient(int id) {
        gui.setOutputArea(clients.get(clients.size() - 1).getHostname() + " disconnected");
        int i = 0;
        for (ClientThread ct : clients) {
            if (ct.id == id) {
                Talktok_Server.clients.remove(i);
                Talktok_Server.refreshClients();
                break;
            }
            i++;
        }
    }
    
    public static void updateGUI(String message) {
    	gui.setOutputArea(message);
    }
}

package talktok_server;


public class ClientInfo {
    private String IPaddress;
    private String Status;
    private String name;
    
    public static String DOSTEPNY = "1";
    public static String NIEDOSTEPNY = "0";
    
    public ClientInfo(String IPadderss, String Status, String name) {
        this.IPaddress = IPadderss;
        this.Status = Status;
        this.name = name;
    }
    
    public String getIPaddress(){
        return IPaddress;
    }
    
    public String getStatus(){
        return Status;
    }
    
    
}

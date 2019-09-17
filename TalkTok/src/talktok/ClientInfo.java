package talktok;

public class ClientInfo {
    private String IPaddress;
    private String Status;
    
    public static String DOSTEPNY = "1";
    public static String NIEDOSTEPNY = "0";
    
    public ClientInfo(String IPadderss, String Status) {
        this.IPaddress = IPadderss;
        this.Status = Status;
    }
    
    public String getIPaddress(){
        return IPaddress;
    }
    
    public String getStatus(){
        return Status;
    }

}

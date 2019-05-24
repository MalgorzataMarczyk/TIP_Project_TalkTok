/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * FXML Controller class
 *
 * @author Lena
 */
public class activeCallController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    private boolean Mic=true;
    
    public int port_server = 8888;
    public String ip_server = "127.0.0.1";
    public int port_my;
    public String my_ip = "127.0.0.1";
    
    
    
        RecorderThread r = new RecorderThread();
        PlayerThread p = new PlayerThread();
    
    public static AudioFormat getAudioFormat(){
    
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channel = 2;
        boolean singed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channel, singed, bigEndian);
        
    }
    
    TargetDataLine audio_in;
     SourceDataLine data_out;
    
    DatagramSocket datagramSocket;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            
                   InetAddress inet = InetAddress.getByName(ip_server);
                   init_audio();
                   init_player();
                   init_recorder(inet,port_server);
                   
                   
               } catch (Exception ex) {
                   Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
               
               }
    }    
    
    
    @FXML
    private void EndCallButtonAction(ActionEvent event) {
    r.end();
    p.end();
    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    stage.close();
    }
    
    @FXML
    private void MicButtonAction(ActionEvent event) {
        System.out.println("mic " + Mic);
        if(Mic){
        MainController.calling = false;
        Mic = false;
        }else {
          MainController.calling = true;
          Mic = true;}
    }
    
    
     public void init_audio() throws LineUnavailableException, UnknownHostException, SocketException{
    
    AudioFormat format = getAudioFormat();
    
        DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
        DataLine.Info info_in = new DataLine.Info(TargetDataLine.class, format);
    
    
    
   if (!AudioSystem.isLineSupported(info_in)) {
                System.out.println("Line for in not supported");
                System.exit(0);
            }
            if(!AudioSystem.isLineSupported(info_out)){
                System.out.println("Line for out not supported");
                System.exit(0);
            }
        
        
        audio_in = (TargetDataLine)AudioSystem.getLine(info_in);
        audio_in.open(format);
        audio_in.start();
        
        data_out = (SourceDataLine)AudioSystem.getLine(info_out);
        data_out.open(format);
        data_out.start();
        
        
        /*
         InetAddress inet = InetAddress.getByName(ip_server);
        r.audio_in = audio_in;
        r.out = new DatagramSocket(); 
        r.server_ip = inet;
        r.server_port = port_server;
        
        
        MainController.calling = true;
        MainController.Exit = false;
        r.start();
       */
        MainController.calling = true;
        MainController.Exit = false;
    }
     
     public void init_player(){
     
        try {
            datagramSocket = new DatagramSocket(0);
            p.in = datagramSocket;
            p.data_out = data_out;
            p.data_out = data_out;
            ////my_ip = InetAddress.getLocalHost().getHostAddress();
            port_my = datagramSocket.getLocalPort();
            System.out.println("my ip: "+my_ip+"  fdsfs: "+datagramSocket.getLocalPort());
            ///p.start();
        } catch (Exception ex) {
            Logger.getLogger(activeCallController.class.getName()).log(Level.SEVERE, null, ex);
        } 
            
            
     }
     
     public void init_recorder(InetAddress ia,int port){
        r.out = datagramSocket;
        r.audio_in = audio_in;
        r.server_ip = ia;
        r.server_port = port;
        r.start();
     }
     
    
}

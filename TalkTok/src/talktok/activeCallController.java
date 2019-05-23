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
import javax.sound.sampled.TargetDataLine;
import static talktok.MainController.calling;

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
    
    
        RecorderThread r = new RecorderThread();
    
    public static AudioFormat getAudioFormat(){
    
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channel = 2;
        boolean singed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channel, singed, bigEndian);
        
    }
    
    TargetDataLine audio_in;
    
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
                   init_audio();
               } catch (Exception ex) {
                   Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
               
               }
    }    
    
    
    @FXML
    private void EndCallButtonAction(ActionEvent event) {
    r.end();
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
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    
    if(!AudioSystem.isLineSupported(info)){
      System.out.println("Error!!");
        System.exit(0);
    }
        audio_in = (TargetDataLine)AudioSystem.getLine(info);
        audio_in.open(format);
        audio_in.start();
        InetAddress inet = InetAddress.getByName(ip_server);
        r.audio_in = audio_in;
        r.out = new DatagramSocket(); 
        r.server_ip = inet;
        r.server_port = port_server;
        calling = true;
        r.start();
        
    }
    
}

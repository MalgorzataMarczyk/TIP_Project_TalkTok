/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
public class MainController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    public int port_server = 8888;
    public String ip_server = "127.0.0.1";
    public static boolean calling = false;
    
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
        // TODO
    }    

    @FXML
    private void AddFriendButtonAction(ActionEvent event) {
      
         try {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("xml/addFriend.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
         ///stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
       
    }
        
    }
    
    @FXML
    private void HistoryButtonAction(ActionEvent event) throws Exception {
        
      
       
       Parent MainParent = FXMLLoader.load(getClass().getResource("xml/history.fxml"));
       Scene sceneMain = new Scene(MainParent);
     
       Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
       
       
        window.setScene(sceneMain);
        window.show();
        
        
       
    }
    
    
    
    @FXML
    private void MakeCallButtonAction(ActionEvent event) {
           try {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("xml/calling.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
         ///stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        //////////////////////////////////
               try {
                   init_audio();
               } catch (Exception ex) {
                   Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
               
               }
        
       
        
    } catch (IOException e) {
       
    }
    }
    
    @FXML
    private void quitButtonAction(ActionEvent event) {
          Platform.exit();
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
        RecorderThread r = new RecorderThread();
        InetAddress inet = InetAddress.getByName(ip_server);
        r.audio_in = audio_in;
        r.out = new DatagramSocket(); 
        r.server_ip = inet;
        r.server_port = port_server;
        calling = true;
        r.start();
    }
    
    
    
    
}

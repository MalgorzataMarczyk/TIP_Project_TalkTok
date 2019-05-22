/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok_server;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Lena
 */
public class FXMLDocumentController implements Initializable {
    
    
    public int port = 8888;
    
    public static AudioFormat getAudioFormat(){
    
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channel = 2;
        boolean singed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channel, singed, bigEndian);
        
    }
    
    public SourceDataLine data_out;
    
    
    public void init_audio() throws LineUnavailableException, SocketException{
    
    AudioFormat format = getAudioFormat();
    DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
        if(!AudioSystem.isLineSupported(info_out)){
        System.out.println("Error!!");
        System.exit(0);
        }
        data_out = (SourceDataLine)AudioSystem.getLine(info_out);
        data_out.open(format);
        data_out.start();
        player_thread p = new player_thread();
        p.in = new DatagramSocket(port);
        p.data_out = data_out;
        TalkTok_Server.calling = true;
        p.start();
    }
    
    
    
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        try {
            init_audio();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

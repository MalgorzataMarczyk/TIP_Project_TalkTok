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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import static talktok.Call.inCall;
import static talktok.Call.endCallStatusSended;
import static talktok.TalkTok.client;

/**
 * FXML Controller class
 *
 * @author Lena
 */
public class activeCallController implements Initializable {

   String inCallWith;
   boolean StatuSended = false;
  
    private boolean Mic=true;
 
      @FXML
    private Label receiverUserNameLabel;
    @FXML
    private Text timeText;
    int mins = 0, secs = 0, millis = 0;
    Timeline timeline;
    private String WhoCalled;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        inCallWith = client.inCallWith;
        WhoCalled="me";
        receiverUserNameLabel.setText("Call with: "+inCallWith);
       try {
           client.changeUserStatus(client.myName, "0");
       } catch (IOException ex) {
           Logger.getLogger(activeCallController.class.getName()).log(Level.SEVERE, null, ex);
       }
        
        //System.out.println("My name is : "+client.myName);
        
        timeText.setText("00:00:000"); 
	timeline = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
            	change(timeText);
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(false);
                timeline.play();
        
        
    }    
    
    
    @FXML
    private void EndCallButtonAction(ActionEvent event) throws IOException {
       
        String time=timeText.getText();
        client.changeUserStatus(client.myName, "1");
        client.sendCallEndToUser(inCallWith,WhoCalled,time);
        client.endingCall();
        

    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    stage.close();
    }
   
    @FXML
    private void MicButtonAction(ActionEvent event) {
            client.Mic();
    }
    
    void change(Text text) {
		if(millis == 1000) {
			secs++;
			millis = 0;
		}
		if(secs == 60) {
			mins++;
			secs = 0;
		}
		text.setText((((mins/10) == 0) ? "0" : "") + mins + ":"
		 + (((secs/10) == 0) ? "0" : "") + secs + ":" 
			+ (((millis/10) == 0) ? "00" : (((millis/100) == 0) ? "0" : "")) + millis++);
                if(!inCall){
                    if(!endCallStatusSended)
                    {
                        endCallStatusSended = true;
                        try {
                            client.changeUserStatus(client.myName, "1");
                        } catch (IOException ex) {
                            Logger.getLogger(activeCallController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    Stage stage = (Stage) timeText.getScene().getWindow();
                    stage.close();
                    }
    }

   
    
    
     
    
}

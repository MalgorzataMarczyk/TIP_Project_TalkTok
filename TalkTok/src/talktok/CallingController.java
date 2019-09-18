/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import static talktok.TalkTok.client;

/**
 * FXML Controller class
 *
 * @author Lena
 */
public class CallingController implements Initializable {
AudioClip ac;
URL path;
Call call;
String inCallWith;
int isCallExist = client.isCallExist;

 @FXML
    private Label CallingUserNameLabel;


     public void playSound(String name){
    
        path = getClass().getResource(name);
        ac  = new AudioClip(path.toString());
        ac.setCycleCount(1); ///jak jest indefinite aby byla petla to sie psuje dunno why, 
                             ///wiec mozna po prostu dac 20 bo w sumie i tak po pewnym czasie sie rozłaczy
        ac.play();
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
      inCallWith = client.inCallWith;
     //potem powycinam dzwieki teraz mi sie nie chce  
      playSound("sounds/cat_party.mp3");
      CallingUserNameLabel.setText(inCallWith);
        
    }    
    
    @FXML
    private void AnswerButtonAction(ActionEvent event) throws IOException {
           
       ac.stop();
       
        client.callACK(inCallWith);
        String destination = client.callingIP;
        client.startCallWith(destination);        
        Parent MainParent = FXMLLoader.load(getClass().getResource("xml/activeCallRereiver.fxml"));
        Scene sceneMain = new Scene(MainParent);
     
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
       
        window.setScene(sceneMain);
        window.show();

        

    }
    
    
    @FXML
    private void DeclineButtonAction(ActionEvent event) {
          ac.stop();
          client.endingCall();
          //System.out.println("todo");
          
    }
    
    
    
}

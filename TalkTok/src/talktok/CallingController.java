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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
private double xOffset = 0;
private double yOffset = 0;

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
        System.out.println("Destination IP: "+destination);
        client.startCallWith(destination);        
        Parent MainParent = FXMLLoader.load(getClass().getResource("xml/activeCallRereiver.fxml"));
        Scene sceneMain = new Scene(MainParent);
        

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        ///window.initStyle(StageStyle.UNDECORATED);
        window.setScene(sceneMain);
        window.show();

        sceneMain.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        sceneMain.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                window.setX(event.getScreenX() - xOffset);
                window.setY(event.getScreenY() - yOffset);
            }
        });
        
    }
    
    
    @FXML
    private void DeclineButtonAction(ActionEvent event) throws IOException {
          ac.stop();
          
          client.sendCallEndToUser(inCallWith,"none","none");
          Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
          stage.close();
          
          
    }
    
  
}

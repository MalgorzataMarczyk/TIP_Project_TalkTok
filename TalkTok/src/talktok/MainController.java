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
    
    
    public static boolean calling = false;
   
   
    
    
    
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
               
        
       
        
    } catch (IOException e) {
       
    }
    }
    
    @FXML
    private void quitButtonAction(ActionEvent event) {
          Platform.exit();
    }
    
    
    
    
    
    
}

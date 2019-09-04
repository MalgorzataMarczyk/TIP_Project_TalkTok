/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import static talktok.TalkTok.client;

/**
 * FXML Controller class
 *
 * @author Lena
 */
public class MainController implements Initializable {

    /**
     * Initializes the controller class.
     */
     @FXML
    private Label labelUserName;

    @FXML
    private Label labelUserDescription;
    
      @FXML
    private TextField textUserDescription;

    @FXML
    private Button okDesButton;
    
    @FXML
    private Button buttonLabelDesButton;
    
    String [] serverData;
    
    public String userName;
    
    public static boolean calling = false;
   
    public static volatile boolean Exit = false;
   
    public void uploadDataFromServer(){
        serverData = client.getServerData();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textUserDescription.setVisible(false);
        okDesButton.setVisible(false);
        uploadDataFromServer();
        /*for(String data : serverData){
            System.out.println(data);
        }*/
        userName = serverData[0];
        labelUserName.setText(userName);
        
        if(serverData[3] == null){
            labelUserDescription.setText("Dodaj opis");
        }
        else
            labelUserDescription.setText(serverData[3]);
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
    
    
    public void openCall(){
    
        try {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("xml/activeCall.fxml"));
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
    void DescriptionButtonAction(ActionEvent event) {
        labelUserDescription.setVisible(false);
        textUserDescription.setVisible(true);
        okDesButton.setVisible(true);

    }
    
    @FXML
    void SetDescriptionAction(ActionEvent event) {
        textUserDescription.setVisible(false);
        okDesButton.setVisible(false);
        serverData[3] = textUserDescription.getText();
        labelUserDescription.setText(textUserDescription.getText());
        labelUserDescription.setVisible(true);
        //Send update data to server and save it in to database
        client.UpdateData(serverData);
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
        client.disconnect();
          Platform.exit();
    }
    
    
    
    
    
    
}

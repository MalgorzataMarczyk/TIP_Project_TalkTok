/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author Lena
 */
public class RegisterController implements Initializable {
    
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    ObservableList list = FXCollections.observableArrayList();
    @FXML
    private ChoiceBox<String> genders;
    
    
    
    private void loadData(){
    
    list.removeAll(list);
    String f = "Kobieta";
    ///String m = "Mężczyzna";
    String q = "Inna";
    list.addAll(f,q);
    
    genders.getItems().addAll(list);
        
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws Exception {
        
      
       
        
       
    }
    
    @FXML
    private void quitButtonAction(ActionEvent event){
     Platform.exit();
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        loadData();
        
    }   
}

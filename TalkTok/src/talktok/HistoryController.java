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
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Lena
 */
public class HistoryController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void BackButtonAction(ActionEvent event) throws IOException {
        
         
       Parent MainParent = FXMLLoader.load(getClass().getResource("xml/main.fxml"));
       Scene sceneMain = new Scene(MainParent);
       sceneMain.getStylesheets().add(getClass().getResource("css/main.css").toExternalForm());
       Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
       
       
        window.setScene(sceneMain);
        window.show();
        
    }

    @FXML
    private void quitButtonAction(ActionEvent event) {
        Platform.exit();
    }
    
}

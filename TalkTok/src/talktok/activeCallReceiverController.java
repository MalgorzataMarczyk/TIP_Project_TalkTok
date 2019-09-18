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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import static talktok.TalkTok.client;

/**
 *
 * @author Gosia
 */
public class activeCallReceiverController implements Initializable{
    private boolean Mic=true;
    @FXML
    private Label callingUserNameLabel;
    String inCallWith;
    Call call;
    
     @Override
    public void initialize(URL url, ResourceBundle rb) {
         inCallWith = client.inCallWith;
         callingUserNameLabel.setText("Call with: "+inCallWith);
    }    
    
    
    @FXML
    private void EndCallButtonAction(ActionEvent event) throws IOException {
        
        client.sendCallEndToUser(inCallWith);
        client.endingCall();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void MicButtonAction(ActionEvent event) {
        ///nie dziala :(
        
       
            client.Mic();
        
        
    }
}

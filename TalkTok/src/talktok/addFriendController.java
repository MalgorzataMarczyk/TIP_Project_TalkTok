/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import static talktok.MainController.userName;

/**
 * FXML Controller class
 *
 * @author Lena
 */
public class addFriendController implements Initializable {

   @FXML
    private TextField UsernameTextField;
    @FXML
    private TextField AliasTextField;
    
    String me;
    String friend;
    String alias;
    
    public boolean dataState = true;
    public boolean userAdded = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
     @FXML
    private void AddButtonAction(ActionEvent event) throws Exception {
    
        try{
            dataState = true;
            check();
            
        }catch(Exception e){
            dataState = false;
        }
        
        
        
        int errorMessage = 0;
          if(dataState){
              errorMessage = sendContactToDatabase(me, friend, alias);
              userAdded = errorMessage == 1;           
          }
          else
              userAdded = false;  
          
          
          
          if(userAdded){
          JOptionPane.showMessageDialog(null, "Dodano do kontaktów!");
           UsernameTextField.clear();
          AliasTextField.clear();
            }
      else
      {
          if(errorMessage==2)
            JOptionPane.showMessageDialog(null, "Użytkownik o podanym loginie nie istnieje.");
          else if(errorMessage==3)
            JOptionPane.showMessageDialog(null, "Masz już użytkownika w kontaktach.");
      }
          
         
        
    }
    
    
    
    public void check()throws Exception {
        
        
        
        if(UsernameTextField.getText() == null || UsernameTextField.getText().trim().isEmpty() || AliasTextField.getText() == null || AliasTextField.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Wypełnij wszystkie okna.");
            throw new Exception("User login is empty!");
            
        }
        else if(UsernameTextField.getText().equalsIgnoreCase(userName)){
         JOptionPane.showMessageDialog(null, "Nie możesz dodać samego siebie!");
            throw new Exception("Cant add yourself!");
        } else{
             me = userName;
             friend = UsernameTextField.getText();
             alias = AliasTextField.getText();
        }
         
    }
    
    public int sendContactToDatabase(String me, String friend, String alias) throws ClassNotFoundException, SQLException{
 
        String [] userData = new String[3];
        userData[0] = me;
        userData[1] = friend;
        userData[2] = alias;
        return TalkTok.client.ContactSendData(userData);      
        
    }
    
}
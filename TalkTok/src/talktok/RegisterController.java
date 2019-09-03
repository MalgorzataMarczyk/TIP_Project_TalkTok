/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import java.sql.*; 

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
    
    @FXML
    private PasswordField passwordTextField;
    
     @FXML
    private PasswordField confirmPasswordTextField;

    @FXML
    private TextField loginTextField;
    
    @FXML
    private TextField emailTextField;
    
    @FXML
    private CheckBox acceptCheckBox;
    
     @FXML
    private DatePicker dateBirthDatePicker;
    
    
    public String userLogin;
    public String userPassword;
    public String userEmail;
    public LocalDate userBirthDate;
    public String userGender;
    public boolean dataState = true;
    public boolean userAdded = false;
    
    
    private void loadData(){
    
    list.removeAll(list);
    String f = "Kobieta";
    ///String m = "Mężczyzna";
    String q = "Inna";
    list.addAll(f,q);
    
    genders.getItems().addAll(list);
        
    }
    
    public void checkoutLogin()throws Exception {
        if(loginTextField.getText() == null || loginTextField.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Login nie może być pusty!");
            throw new Exception("User login is empty!");
            
        }
        else{
             userLogin = loginTextField.getText();
        }
         
    }
    
    public Boolean comparePasswords(){
        String passwordString = passwordTextField.getText();
        String confPasswordString = confirmPasswordTextField.getText();
        if(passwordString.compareTo(confPasswordString) == 0){
            return true;
        }
        else
            return false;
        
    }
    
    public void checkoutPasswords() throws Exception{
       if(passwordTextField.getText() == null || passwordTextField.getText().trim().isEmpty())
      {
           JOptionPane.showMessageDialog(null, "Hasło nie może być puste!");
           throw new Exception("User password is empty!");
      }
       else if(confirmPasswordTextField.getText() == null || confirmPasswordTextField.getText().trim().isEmpty())
      {
           JOptionPane.showMessageDialog(null, "Powtórz hasło");
           throw new Exception("Confirm password!");
      }
       else if (!comparePasswords()){
           JOptionPane.showMessageDialog(null, "Hasła muszą być takie same!");
           throw new Exception("Passwords must be the same!");
       }
       else{
          userPassword = passwordTextField.getText(); 
       }
    }
    
    public void checkoutEmail() throws Exception{
        if(emailTextField.getText() == null || emailTextField.getText().trim().isEmpty() ){
            JOptionPane.showMessageDialog(null, "Prosze podać email!");
            throw new Exception("User email is empty!");
        }
        else{
            userEmail = emailTextField.getText();
        }
    }
    
    public void checkoutAcceptCheckBox() throws Exception{
        if(!acceptCheckBox.isSelected()){
            JOptionPane.showMessageDialog(null, "Aby się zarejestrować trzeba zaakceptować regulamin!");
            throw new Exception("You must accept statute!");
        }
    }
    
    public void checkoutBirthDate(){
        if(dateBirthDatePicker.getValue() == null){
            userBirthDate = LocalDate.now();
        }
        else{
            userBirthDate = dateBirthDatePicker.getValue();
        }
    }
    
    public int sendUserToDatabase(String usLogin, String usPassword, String usEmail, LocalDate usBirthDate, String usGender ) throws ClassNotFoundException, SQLException{
 
        String [] userData = new String[5];
        userData[0] = usLogin;
        userData[1] = usEmail;
        userData[2] = usPassword;
        userData[3] = usBirthDate.toString();
        userData[4] = usGender;
        return TalkTok.client.RegisterSendData(userData);      
        
    }
        
    @FXML
    private void handleButtonAction(ActionEvent event) throws Exception {
      
      try{
            dataState = true;
            checkoutLogin();
            checkoutPasswords();
            checkoutEmail();
            checkoutBirthDate();
            userGender = genders.getValue();
            checkoutAcceptCheckBox();
        }catch(Exception e){
            dataState = false;
        }
      
      
      //try{
      int errorMessage = 0;
          if(dataState){
              errorMessage = sendUserToDatabase(userLogin, userPassword, userEmail, userBirthDate, userGender);
              userAdded = errorMessage == 1;           
          }
          else
              userAdded = false;    
        
      if(userAdded){
          JOptionPane.showMessageDialog(null, "Zostałaś poprawnie dodana!");
          Parent MainParent = FXMLLoader.load(getClass().getResource("xml/login.fxml"));
          Scene sceneMain = new Scene(MainParent);
          Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
          window.setScene(sceneMain);
          window.show();
      }
      else
      {
          if(errorMessage==2)
            JOptionPane.showMessageDialog(null, "Użytkownik o podanym loginie istnieje już w bazie danych");
          else if(errorMessage==3)
            JOptionPane.showMessageDialog(null, "Użytkownik o podanym mailu istnieje już w bazie danych");
          else
            JOptionPane.showMessageDialog(null, "Konto nie zostało dodane. Spróbuj ponownie.");
      }
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

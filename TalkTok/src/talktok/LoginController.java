/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import static talktok.TalkTok.client;

/**
 *
 * @author Lena
 */
public class LoginController implements Initializable {
    
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    public String userLogin;
    public String userPassword;
    
    @FXML
    private Label label;
    
    @FXML
    private PasswordField passwordPasswordFile;

    @FXML
    private TextField LoginTextField;
    
    public void checkoutLogin() throws Exception{
        if(LoginTextField.getText() == null || LoginTextField.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, "Prosze podać login!");
            throw new Exception("User login is empty!");
        }
        else
            userLogin = LoginTextField.getText();
    }
    
    public void checkoutPassword() throws Exception{
        if(passwordPasswordFile.getText() == null || passwordPasswordFile.getText().trim().isEmpty() ){
            JOptionPane.showMessageDialog(null, "Prosze podać hsało!");
            throw new Exception("User password is empty!");
        }
        else
            userPassword = passwordPasswordFile.getText();
    }
    
    public int sendLoginDataToServer(String usLogin, String usPassword) throws UnknownHostException{
        String [] userData = new String[2];
        userData[0] = usLogin;
        userData[1] = usPassword;
        return client.Login(userData);
    }
    

    
    
    @FXML
    private void LoginButtonAction(ActionEvent event) throws Exception {
        //Sprawdzenie poprawnosci uzupelnionych pol oraz wywolanie funkcji logowania//
        boolean dateState = true;
        boolean userLog;
        try{
            checkoutLogin();
            checkoutPassword();
            
        }catch(Exception e){
            dateState = false;
        }
        
        int errorMessage = 0;
        if(dateState){
            errorMessage = sendLoginDataToServer(userLogin, userPassword);
            userLog = errorMessage == 1;
        }
        else
            userLog = false;
        
        if(userLog){
            //Jeśli poprawnie przeszło

       Parent MainParent = FXMLLoader.load(getClass().getResource("xml/main.fxml"));
       Scene sceneMain = new Scene(MainParent);
       sceneMain.getStylesheets().add(getClass().getResource("css/main.css").toExternalForm());
       Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
       
        window.setScene(sceneMain);
        window.show();
         ///przesuwanie ekranem na mainie
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
        else
            if(errorMessage==4)
            JOptionPane.showMessageDialog(null, "Błędne hasło!");
            else
            JOptionPane.showMessageDialog(null, "Błąd logowania. Sprawdź login i spróbuj ponownie.");
   
    }
    
    
    
    @FXML
    private void RegisterButtonAction(ActionEvent event) throws Exception {
        
      
       
       Parent MainParent = FXMLLoader.load(getClass().getResource("xml/register.fxml"));
       Scene sceneMain = new Scene(MainParent);
     
       Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
       
       
        window.setScene(sceneMain);
        window.show();
        
        
        ///przesuwanie ekranem na mainie
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
    private void quitButtonAction(ActionEvent event){
     Platform.exit();
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
}

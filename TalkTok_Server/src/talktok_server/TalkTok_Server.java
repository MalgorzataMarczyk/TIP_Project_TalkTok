/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok_server;

import java.net.ServerSocket;
import java.sql.Connection;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Lena
 */
public class TalkTok_Server extends Application {
    
    public static boolean calling = false;
    
    
      ServerSocket serversocket = null;
    public static ArrayList<client> arr_client = new ArrayList<>();
    public static Connection connect = null; 
    
    
    
    @Override
    public void start(Stage stage) throws Exception {
        
        ////laczenie sie z baza danych//////
        
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

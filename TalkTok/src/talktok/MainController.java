/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.activation.MimetypesFileTypeMap;
import javax.swing.ImageIcon;
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
    private ImageView userImage;

    @FXML
    private Label labelUserDescription;
    
      @FXML
    private TextField textUserDescription;

    @FXML
    private Button okDesButton;
    
    @FXML
    private Button buttonLabelDesButton;
        
    @FXML
    private Pane PaneContacs;
    
   
     @FXML
          ListView<Contact> lv; ///////listview
    
    String [] serverData;
    
    public static String userName;
    
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
        Image icon = client.getServerImage();
        if(icon != null)
            userImage.setImage(icon);

        
        
        //////asasa
       
          ObservableList<Contact> ContactObservableList;
       
        ContactObservableList = FXCollections.observableArrayList();
        
        ContactObservableList.addAll(
        new Contact("kasia","kasia",1),
        new Contact("ka1","ewa",0),
        new Contact("ka2a","karolina",1),
        new Contact("ka3a","asia",0),
        new Contact("1a","ania",1)
        );
        
        lv.setItems(ContactObservableList);
        
        
        lv.setCellFactory(new Callback<ListView<Contact>, ListCell<Contact>>() {
             @Override
            public ListCell<Contact> call(ListView<Contact> param) {
        return new XCell();
            }
            });
        
        
        
       /*  lv.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new XCell();
            }
        });*/
       // PaneContacs.getChildren().add(lv);
        
        
        
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
    

    @FXML
    void mouseClickOnUserImage(MouseEvent event) throws IOException {
        if(event.getClickCount() == 2 && !event.isConsumed()){
            event.consume();
            //fliechooser
            FileChooser fc = new FileChooser();
            ExtensionFilter filter = new ExtensionFilter("*.Images", "jpg","gif","png");
            fc.setSelectedExtensionFilter(filter);
            File selectedFile = fc.showOpenDialog(null);
            if(selectedFile != null){
                InputStream in = new FileInputStream(selectedFile.getAbsolutePath());
                byte[] abyte = new byte[in.available()];
                in.read(abyte);
                userImage.setImage(new Image(new ByteArrayInputStream(abyte)));
                client.UpdateImage(abyte);
            }else
                 JOptionPane.showMessageDialog(null, "Nie wybrano zdjęcia");
        }
    }
    
    
    ///////////noideawhatimdoing
     static class XCell extends ListCell<Contact> {
        HBox hbox = new HBox();
       
        Label FXusername = new Label();
        Label FXalias = new Label();
        Label FXstatus = new Label();
        Pane pane = new Pane();
        Button buttonC = new Button("Call"); ///zadzwoń
        Button buttonR = new Button("Bye"); ///usun ze znajomych
        Contact lastItem;

        public XCell() {
            super();
            hbox.getChildren().addAll(FXalias,FXstatus, pane, buttonC,buttonR);
            HBox.setHgrow(pane, Priority.ALWAYS);
            buttonC.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println(lastItem.getUsername() + " : " + event);
                }
            });
            
            buttonR.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println(lastItem.getUsername() + " : " + event);
                }
            });
            
        }

        @Override
        protected void updateItem(Contact item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty || item == null ) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                ///label.setText(item!=null ? item : "<null>");
                 
                FXstatus.setText(String.valueOf(lastItem.getStatus()));
                FXalias.setText(lastItem.getAlias());
                
                setGraphic(hbox);
            }
        }
    }
    
    
}

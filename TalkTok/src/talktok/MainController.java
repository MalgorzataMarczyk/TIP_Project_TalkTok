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
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.ContentDisplay;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static talktok.HistoryController.StoryList;
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
    private Label labelFriends;
    
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
    
    Timer timer;
    
    public static String userName;
    
    public static boolean calling = false;
   
    public static volatile boolean Exit = false;
   
    public static LinkedHashSet <Contact> ContactList = new LinkedHashSet<Contact>(); ////Contact, username
    
    public void uploadDataFromServer(){
        serverData = client.getServerData();
    }
    
     private double xOffset = 0;
    private double yOffset = 0;
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

       // RefreshContactList();
        
        //System.out.println(ContactList.size());
        //////jeśli lista kontaktow nie jest pusta labelFriends.setVisible(false);
        StoryList = client.HistoryList; 
        System.out.println("winows created");
        
        client.windowsController = this;
        client.updateFriendList();
        
          TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                 client.updateFriendList();
            }
        };

        timer = new Timer("MyTimer");//create a new Timer

        timer.scheduleAtFixedRate(timerTask, new Date(), 3000);
        
    }  
   
    
    public void RefreshContactList()
    {
         Platform.runLater(() -> {
           //////////dodawanie zawartości z serwera
        ContactList = client.ContactList;
         if (!ContactList.isEmpty()){labelFriends.setVisible(false);}
      
          ObservableList<Contact> ContactObservableList;
       
        ContactObservableList = FXCollections.observableArrayList();

        ContactObservableList.addAll(ContactList);
        //System.out.println("size:"+ContactObservableList.size());        
        
        lv.getItems().clear();
        lv.setItems(ContactObservableList);
       
        
        lv.setCellFactory(new Callback<ListView<Contact>, ListCell<Contact>>() {
             @Override
            public ListCell<Contact> call(ListView<Contact> param) {
        return new XCell();
            }
            });
         });
                 
    }

    @FXML
    private void AddFriendButtonAction(ActionEvent event) {
      
         try {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("xml/addFriend.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
         stage.initStyle(StageStyle.UNDECORATED);
         
         
         ///przesuwanie ekranem na mainie
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        
        ////
         
         
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
         stage.initStyle(StageStyle.UNDECORATED);
         
         
             ///przesuwanie ekranem na mainie
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        
        ////
         
         
         
         
        stage.setScene(scene);
        stage.show();
        
        
        
        
    } catch (IOException e) {
       
    }
    }
    
    
    @FXML
    private void HistoryButtonAction(ActionEvent event) throws Exception {
        
      
       
       Parent MainParent = FXMLLoader.load(getClass().getResource("xml/history.fxml"));
       Scene sceneMain = new Scene(MainParent);
      sceneMain.getStylesheets().add(getClass().getResource("css/story.css").toExternalForm());
       Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
       
       
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
        
        ////
       
       
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
    private void quitButtonAction(ActionEvent event) throws IOException {
        timer.cancel();
        client.deleteUserFromClientMap(serverData[0]);
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
       
        Label FXopis = new Label();
        Label FXalias = new Label();
        Label FXstatus = new Label();
        Pane pane = new Pane();
        Button buttonC = new Button(); ///zadzwoń
        //Button buttonR = new Button(); ///usun ze znajomych
        Contact lastItem;

        public XCell(){
            super();
            buttonC.setId("call");
            //buttonR.setId("remove");
            
            Platform.runLater(() -> {
    InputStream input = getClass().getResourceAsStream("images/call-icon.png");
    //set the size for image
    Image image = new Image(input, 18, 18, true, true);
    ImageView imageView = new ImageView(image);            
    buttonC.setGraphic(imageView);
});

            FXalias.setFont(new Font("Arial", 22));
            FXopis.setTranslateX(30);
            FXstatus.setFont(new Font("Arial", 9));
            FXstatus.setTranslateX(60);
            FXstatus.setTranslateY(8);
            FXopis.setTranslateY(5);
            hbox.getChildren().addAll(FXalias,FXopis, FXstatus, pane, buttonC);
            HBox.setHgrow(pane, Priority.ALWAYS);
            buttonC.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //System.out.println(lastItem.getUsername() + " : " + event);
                    
                         try {
                             client.getUserStatus(lastItem.getUsername());
                             Thread.sleep(500);
                            if (client.userStatus != null && client.userStatus != 0){
                                //dzwoni do innego klienta//
                                client.sendMyNameToServer(userName); //wysyłanie userName aby server mógł wyszukać moj IP
                                Thread.sleep(500);
                                client.askServerForIP(lastItem.getUsername());
                                client.inCallWith = lastItem.getUsername();
                                Thread.sleep(500);
                                System.out.println("Call user IP: "+client.getCallUserIP());

                                client.startCall(client.getCallUserIP());
                            }else{
                                JOptionPane.showMessageDialog(null, "Użytkownik nie jest dostępny");
                            }
                            
                        } catch (IOException e) {

                                                } catch (InterruptedException ex) {
                                    //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                                }

                    
                    
                   
                }
            });
            
//            buttonR.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    System.out.println(lastItem.getUsername() + " : " + event);
//                }
//            });
            
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
                 
                
                FXalias.setText(lastItem.getAlias());
                //System.out.println(lastItem.getStatus());
                if(lastItem.getStatus().compareTo("0") == 0){
                    FXstatus.setText("NDSTP");
                }
                else{
                    FXstatus.setText("DSTP");
                }
                
                FXopis.setText(lastItem.getOpis());
                setGraphic(hbox);
            }
        }
    }
    
    
}

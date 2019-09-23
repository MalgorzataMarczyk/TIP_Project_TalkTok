/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import static talktok.TalkTok.client;

/**
 * FXML Controller class
 *
 * @author Lena
 */
public class HistoryController implements Initializable {

     @FXML
    private Pane paneStories;
    
   
private double xOffset = 0;
private double yOffset = 0;
     @FXML
          ListView<Record> listStories; ///////listview
     
      public static LinkedHashSet <Record> StoryList = new LinkedHashSet<Record>(); ////Contact, username
    
     
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        ///client.RequestHistory();
        //////////dodawanie zawartości z serwera
        //client.waitHistory();
       // StoryList = client.HistoryList;
        
       
        System.out.println(StoryList.size());
        
         ObservableList<Record> ContactObservableList;
       
        ContactObservableList = FXCollections.observableArrayList();
        
        ContactObservableList.addAll(StoryList);
        
        
        listStories.getItems().clear();
        listStories.setItems(ContactObservableList);
       
        
        listStories.setCellFactory(new Callback<ListView<Record>, ListCell<Record>>() {
             @Override
            public ListCell<Record> call(ListView<Record> param) {
        return new XCell();
            }
            });
        
    }    

    @FXML
    private void BackButtonAction(ActionEvent event) throws IOException {
        
         
       Parent MainParent = FXMLLoader.load(getClass().getResource("xml/main.fxml"));
       Scene sceneMain = new Scene(MainParent);
       sceneMain.getStylesheets().add(getClass().getResource("css/main.css").toExternalForm());
       Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
       
       
       
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

       
       
        window.setScene(sceneMain);
        window.show();
        
        
        
        
        
    }

    @FXML
    private void quitButtonAction(ActionEvent event) {
        client.disconnect();
        Platform.exit();
    }
    
    
    
     ///////////noideawhatimdoing
     static class XCell extends ListCell<Record> {
        HBox hbox = new HBox();
       
        Label FXcaller = new Label();
        Label FXanswer = new Label();
        Label FXtime = new Label();
        Label FXdate = new Label();
        Pane pane = new Pane();
        
        Record lastItem;

        public XCell() {
            super();
           FXcaller.setFont(new Font("Arial", 18));
           FXanswer.setFont(new Font("Arial", 18));
            FXcaller.setPrefWidth(60);
            FXanswer.setPrefWidth(60);
            
           FXtime.setFont(new Font("Arial", 12));
           FXdate.setFont(new Font("Arial", 50));
           FXtime.setTranslateY(25);
            
            hbox.getChildren().addAll(FXdate,FXcaller,FXtime,FXanswer, pane);
            HBox.setHgrow(pane, Priority.ALWAYS);
           hbox.setAlignment(Pos.CENTER);
            
            
        }

        @Override
        protected void updateItem(Record item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty || item == null ) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                ///label.setText(item!=null ? item : "<null>");
                 
                FXcaller.setText(lastItem.getCaller()+ " ");
             
                FXtime.setText(lastItem.getTime() + " : " + lastItem.getDate());
                FXdate.setText(" ");
                FXtime.setAlignment(Pos.CENTER);
                
                
                FXanswer.setText(lastItem.getAnswer());
                FXanswer.setAlignment(Pos.CENTER_RIGHT);
                setGraphic(hbox);
            }
        }
    }
    
    
}

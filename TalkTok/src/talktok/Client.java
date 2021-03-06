package talktok;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Client {

private double xOffset = 0;
private double yOffset = 0;
	/* Socket related global variables. */
	private String hostname;
	private int portNumber;
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
        private int ack;
        public String [] serverData;
        public byte [] imageByte;
        public MainController windowsController = null;
        

	/* Integer commands for communication with the server. */
	private final int CONNECT = 0;
	private final int DISCONNECT = 1;
	private final int UPDATE = 2;
	private final int CALL = 3;
	private final int END_CALL = 4;
	private final int ERROR = 5;
	private final int MIC_ON  = 6;
	private final int MIC_OFF = 7;
        private final int ADD_FRIEND = 8;
        private final int REGISTER = 9;
         private final int LOGIN = 10;
         private final int UPDATEDATA = 11;
         private final int UPDATEIMG = 12;
         private final int GETHISTORY = 13;
         private static final int ASK_IP = 14;
         private static final int GET_IP = 15;
         private static final int CALL_INFORM = 16;
         private static final int GET_USER_IP_BY_NAME = 17;
         private static final int CALL_ACK = 18;
         private static final int CALLING_USER_ACK = 19;
         private static final int SERVER_GET_FRIENDS = 30;
         private static final int GET_USER_STATUS = 40;
         private static final int USER_STATUS = 41;
         private static final int CHANGE_USER_STATUS = 42;
         private static final int DELETE_USER_FROM_MAP = 50;
         private final int SERVERMESSAGE = 99;
         private final int SERVERDATA = 98;
         private final int SERVERSENTIMG = 97;
          private final int SERVERSENCONTACTS = 96;
      

      LinkedHashSet <Contact> ContactList = new LinkedHashSet<Contact>();
      LinkedHashSet <Record> HistoryList = new LinkedHashSet<Record>();
	/* User interface associated with the individual client. */
	///private static ClientGUI gui;

	/*
	 * Thread for listening to commands from server. Boolean indicates whether
	 * the thread should continue running.
	 */
	private ListenerThread listener;
	private boolean listening = true;
        
        private String callUserIP;
        public String callingIP;
        public Integer userStatus = 0;
        public String myName;
        

	/* Variables related to a voice call that is being made. */
	Call call;
	String inCallWith;

	/*
	 * Constructor for Client. Takes a String hostname and integer portnumber as
	 * parameters
	 */
	public Client(String hostname, int portNumber) {
		this.hostname = hostname; //<- adres serwera
		this.portNumber = portNumber;  ////port serwera
	}

        public Client() {
       
        }

	/*
	 * Starts the Client object. Initializes a new Socket to connect to server.
	 * Initializes input/output streams. Starts a new Listener thread.
	 */
	public void start() {
		try {
			socket = new Socket(hostname, portNumber);

			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			//System.out.println("The given hostname is not connected as a server");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		listener = new ListenerThread();
		listener.start();
	}

	/*
	 * Sends the server the command stating that this client has disconnected.
	 * Closes all input/output streams. Closes the socket. Indicates that the
	 * listener thread can terminate by setting listening to false.
	 */
	public void disconnect() {
		try {
			outputStream.writeInt(DISCONNECT);
			outputStream.close();
			inputStream.close();
			socket.close();
			listening = false;
		} catch (Exception e) {
			System.out.println(e);
		}
	}

        
        
        
	/* Sends the parameter message to the server to be sent to other clients. */
	public void sendMessage(String message, String destination) {
		try {
			//outputStream.writeInt(PRIVATE_MESSAGE);
			//outputStream.writeObject(destination);
			//outputStream.writeObject(message);
		} catch (Exception e) {
		}
	}

	/*
	 * Signals to the server to notify the destination client that a call is
	 * being made. Starts a new Call instance to initialize threads for
	 * concurrent voice communication.
	 */
	public void startCall(String destination) {
		try {
			outputStream.writeInt(CALL);
			outputStream.writeObject(destination);
                       	Thread.sleep(500);

			call = new Call(0, 3001, 3002, destination);

		} catch (Exception e) {
		}

	}
        
        public void startCallWith(String destination) {
		try {
			outputStream.writeInt(CALL);
			outputStream.writeObject(destination);
                        Thread.sleep(500);

			call = new Call(1, 3001, 3002, destination);

		} catch (Exception e) {
		}

	}

	/*
	 * Inner class extending the Thread class. Used to listen for incoming
	 * commands and messages from the server. Runs until it has been told to
	 * stop listening. If the command is: - CONNECT: updates GUI to say which
	 * client has connected. - DISCONNECT: updates GUI to say which client has
	 * disconnected. - UPDATE: reads in a list of connected clients. Updates GUI
	 * list of clients. - MESSAGE: reads in message. Updates GUI with message. -
	 * CALL: reads in callee information. Starts a new Call. Updates GUI with
	 * call information. - END_CALL: ends the call. Updates GUI with termination
	 * information. - ERROR: displays error in GUI.
	 */
	class ListenerThread extends Thread {

		public void run() {
                    
			while (listening) {
				try {
					int command = inputStream.readInt();
                                        //System.out.println(command);
					if (command == CONNECT) {
						String hostname = (String) inputStream.readObject();
						//System.out.println(hostname + " connected");
					} else if (command == DISCONNECT) {
						String hostname = (String) inputStream.readObject();
						//System.out.println(hostname + " disconnected");
					} else if (command == UPDATE) {
						String[] list = (String[]) inputStream.readObject();
						//System.out.println("Update: " +list.length);
					} else if (command == ADD_FRIEND) {
						//String message = (String) inputStream.readObject();
						//System.out.println(message);
					} else if(command == GETHISTORY){
                                        
                                            String [] RecordDataArray = new String[4];
                                            
                                            
                                            
                                          try
                                                   {
                                                for (;;)
                                                   {
                                                         RecordDataArray =  (String []) inputStream.readObject();
                                                          HistoryList.add( new Record(RecordDataArray[0],RecordDataArray[1],RecordDataArray[2],RecordDataArray[3]));
                                                            }
                                                             }
                                                          catch (SocketTimeoutException exc)
                                                             {
                                                                
                                                                        // you got the timeout
                                                           }
                                                          catch (EOFException exc)
                                                          {
                                                             //System.out.println("koniec " + RecordDataArray[0]);
                                                            }
                                            
                                        }
                                        else if (command == CALL) {
						String sender = (String) inputStream.readObject();
    
					} else if (command == END_CALL) {
						String sender = (String) inputStream.readObject();
						call.endCall();
                                                //System.out.println(sender + " has ended the call");
					} else if (command == ERROR) {
						String message = (String) inputStream.readObject();
						//System.out.println(message);	
					} else if (command == MIC_ON) {
						String sender = (String) inputStream.readObject();
						//System.out.println("on");
						call.hearing = true; ////////do tego momentu dziala dobrze, wiemy ze sie wyciszyl
					}else if (command == MIC_OFF) {
                                                String sender = (String) inputStream.readObject();
						//System.out.println("off");
						call.hearing = false;
					}else if (command == REGISTER) {
						
					}else if (command == LOGIN) {

					}
                                        else if(command == SERVERMESSAGE){
                                            ack = (int)inputStream.readObject();
                                            //System.out.println(ack);
                                        }
                                        else if (command == SERVERDATA){
                                            serverData = (String[]) inputStream.readObject();
                                           
                                            
                                        }
                                        else if (command == SERVERSENTIMG){
                                            //System.out.print("odbieram");
                                            imageByte = (byte[]) inputStream.readObject();
                                            //System.out.print("odebral");
                                           
                                             
                                            
                                        } else if(command==SERVERSENCONTACTS){
                                            getContactsFromServer();
                                        }else if(command == GET_IP){
                                            callUserIP = (String)inputStream.readObject();    
                                        }else if(command == CALL_INFORM){
                                            String[] callData = new String[2];
                                            callData = (String[]) inputStream.readObject();
                                            callingIP = callData[0];
                                            inCallWith = callData[1];
                                           // System.out.println("Calling FROM IP: " + callingIP);
                                            Platform.runLater(()->{
                                                try {
                                                    FXMLLoader fxmlLoader = new FXMLLoader();
                                                    fxmlLoader.setLocation(getClass().getResource("xml/calling.fxml"));
                                                    
                                                    Scene scene = new Scene(fxmlLoader.load());
                                                    
                                                    Stage stage = new Stage();
                                                    stage.initStyle(StageStyle.UNDECORATED);
                                                    stage.setScene(scene);
                                                    stage.show();
                                                    
                                                    
                                                    
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
                                                    
                                                    
                                                    
                                                } catch (IOException ex) {
                                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            });
                                        }else if( command == CALLING_USER_ACK){
                                            inCallWith = (String) inputStream.readObject();
                                            Platform.runLater(()->{
                                                try {
                                                    FXMLLoader fxmlLoader = new FXMLLoader();
                                                    fxmlLoader.setLocation(getClass().getResource("xml/activeCall.fxml"));
                                                    
                                                    Scene scene = new Scene(fxmlLoader.load());
                                                    
                                                    Stage stage = new Stage();
                                                    stage.initStyle(StageStyle.UNDECORATED);
                                                    stage.setScene(scene);
                                                    stage.show();
                                                   
                                                    
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
                                                    
                                                    
                                                    
                                                } catch (IOException ex) {
                                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            });
                                        }else if (command == USER_STATUS){
                                            String usSt = (String) inputStream.readObject();
                                            userStatus = Integer.parseInt(usSt);
                                        }else if (command == CHANGE_USER_STATUS){
                                            changeUserStatus(myName,"1");
                                        }
                                        else{
                                           // System.out.println("czytam");
                                        }
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				} 
			}
		} 
	}

	/*
	 * Ends all threads for voice communication. Signals to the server that the
	 * call has been terminated.
	 */
	public void endCall() {
		call.endCall();

		try {
			outputStream.writeInt(END_CALL);
			outputStream.writeObject(inCallWith);
		} catch (Exception e) {

		}
	}
        
        public void endingCall(){
            call.endCall();
        }
        
        public void callACK(String inCallUserName) throws IOException{
            outputStream.writeInt(CALL_ACK);
            outputStream.writeObject(inCallUserName);
        }
        
        public void sendCallEndToUser (String inCallUserName,String WhoCalled, String time) throws IOException{
            outputStream.writeInt(20);
           
            outputStream.writeObject(inCallUserName);
             outputStream.writeObject(WhoCalled);
             outputStream.writeObject(time);
        }

	public void sendRecording(String recipient, byte[] arr) {
		try {
			outputStream.writeInt(MIC_ON);
			outputStream.writeObject(recipient);
			outputStream.writeObject(arr);
		} catch (Exception e) {

		}
	}

	public void Mic() {
            try {
            if(call.capturing){
            call.capturing = false;
            outputStream.writeInt(MIC_OFF);
            outputStream.writeObject(inCallWith);
            //System.out.println("send");
        }else {
          call.capturing = true;
             outputStream.writeInt(MIC_ON);
             outputStream.writeObject(inCallWith);
            }
             } catch (Exception e) {

		}
        }

        public int Login(String [] userData){ 
             ack = 0;
             myName = userData[0];
            try {
                
                outputStream.writeInt(LOGIN); ///wysyłamy do serwera co chcemy zrobić
                outputStream.writeObject(userData);
                
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            int i =0;
            while(ack == 0 && i <50 ){
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                i++;                
            }          
            return ack;
        }
        
        public int RegisterSendData(String [] userData){
            ack = 0;
            try {
                outputStream.writeInt(REGISTER);
                outputStream.writeObject(userData);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Czekanie na ack z serwera
            int i =0;
            while(ack == 0 && i <50 ){
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                i++;                
            }          
            return ack;
        }
        
        public String [] getServerData(){
            while(serverData == null){
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return serverData;
        }
        
        public void askServerForIP(String callUserName) throws IOException{
            outputStream.writeInt(ASK_IP);
            outputStream.writeObject(callUserName);
            
        }
        
        public void sendMyNameToServer(String myName) throws IOException{
            outputStream.writeInt(GET_USER_IP_BY_NAME);
            outputStream.writeObject(myName);
        }
        
        public String getCallUserIP (){
            return callUserIP;
        }
        
        public Image getServerImage(){
            int i =0;
            while(imageByte == null && i < 10){
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                i++;
            }
            if(imageByte != null)
            {
                return new Image(new ByteArrayInputStream(imageByte));
            }
            return null;
        }
        
        public void UpdateData(String[] updateData){
            try{
                outputStream.writeInt(UPDATEDATA);
                outputStream.writeObject(updateData);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void UpdateImage (byte[] imageArray ){
            try{
                
                outputStream.writeInt(UPDATEIMG);
                outputStream.writeObject(imageArray);          
            
            }catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void updateFriendList(){
            try {
               // System.out.println("send request to server get friend");
                outputStream.writeInt(SERVER_GET_FRIENDS);
                outputStream.writeObject("send");
               // System.out.println("sended request to server get friend");
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        
        public int ContactSendData(String [] userData){
            ack = 0;
            
            try {
                outputStream.writeInt(ADD_FRIEND);
                outputStream.writeObject(userData);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Czekanie na ack z serwera
            int i =0;
            while(ack == 0 && i <50 ){
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                i++;                
            }          
            return ack;
        }

        public int HistorytSendData(String [] userData){
            ack = 0;
            
            try {
                outputStream.writeInt(13);
                outputStream.writeObject(userData);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Czekanie na ack z serwera
            int i =0;
            while(ack == 0 && i <50 ){
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                i++;                
            }          
            return ack;
        
            
        }
        
        public void getUserStatus(String userName) throws IOException{
           outputStream.writeInt( GET_USER_STATUS);
           outputStream.writeObject(userName);
        }
              
        private void getContactsFromServer(){
            //System.out.println("start getting contact from server");
            ArrayList <String[] > list = null;
            try { 
                    list = (ArrayList <String[] > ) inputStream.readObject();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(list != null)
            {
               // System.out.println(list.size());
                ContactList.clear();
                Iterator< String[]> it = list.iterator();
                while(it.hasNext())
                    ContactList.add(new Contact(it.next()));
            }
            
           // System.out.println("finish getting contact from server");
            
            if(windowsController != null)
                windowsController.RefreshContactList();

        }
        
        public void deleteUserFromClientMap(String userName) throws IOException{
            outputStream.writeInt(DELETE_USER_FROM_MAP);
            outputStream.writeObject(userName);
        }
        
        public void changeUserStatus(String userName, String status) throws IOException{
            
            String[] arrString = {userName, status};
            outputStream.writeInt(CHANGE_USER_STATUS);
            outputStream.writeObject(arrString);
         
        }
        
        
        
}

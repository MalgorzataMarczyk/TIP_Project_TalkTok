package talktok;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client {

	/* Socket related global variables. */
	private String hostname;
	private int portNumber;
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

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

	/* User interface associated with the individual client. */
	///private static ClientGUI gui;

	/*
	 * Thread for listening to commands from server. Boolean indicates whether
	 * the thread should continue running.
	 */
	private ListenerThread listener;
	private boolean listening = true;

	/* Variables related to a voice call that is being made. */
	Call call;
	String inCallWith;

	/*
	 * Constructor for Client. Takes a String hostname and integer portnumber as
	 * parameters
	 */
	public Client(String hostname, int portNumber) {
		this.hostname = "127.0.0.1";///hostname; <- adres serwera
		this.portNumber = portNumber;  ////port serwera
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
			System.out.println("The given hostname is not connected as a server");
			
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

			inCallWith = destination;

			Thread.sleep(500);

			call = new Call(0, 3001, 3002, destination);

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
					if (command == CONNECT) {
						String hostname = (String) inputStream.readObject();
						System.out.println(hostname + " connected");
					} else if (command == DISCONNECT) {
						String hostname = (String) inputStream.readObject();
						System.out.println(hostname + " disconnected");
					} else if (command == UPDATE) {
						String[] list = (String[]) inputStream.readObject();
						System.out.println(list);
					} else if (command == ADD_FRIEND) {
						//String message = (String) inputStream.readObject();
						//System.out.println(message);
					} 
                                        else if (command == CALL) {
						String sender = (String) inputStream.readObject();
						inCallWith = sender;

						call = new Call(1, 3001, 3002, sender);

						System.out.println(sender
								+ " has started a call with you");
						
                                                Platform.runLater(new Runnable(){
                                                    @Override
                                                    public void run() {
                                                        
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
                                                    } });
                                                
                                                
                                                
                                                
					} else if (command == END_CALL) {
						String sender = (String) inputStream.readObject();
						call.endCall();
						System.out.println(sender + " has ended the call");
						
					} else if (command == ERROR) {
						String message = (String) inputStream.readObject();
						System.out.println(message);
						
					} else if (command == MIC_ON) {
						String sender = (String) inputStream.readObject();
						System.out.println("on");
						call.hearing = true; ////////do tego momentu dziala dobrze, wiemy ze sie wyciszyl
					}else if (command == MIC_OFF) {
                                                String sender = (String) inputStream.readObject();
						System.out.println("off");
						call.hearing = false;
					}else if (command == REGISTER) {
						//String sender = (String) inputStream.readObject();
						//String message = (String) inputStream.readObject();
						//System.out.println(sender + ": " + message);
					}else if (command == LOGIN) {
						/*  */
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
            System.out.println("send");
        }else {
          call.capturing = true;
             outputStream.writeInt(MIC_ON);
             outputStream.writeObject(inCallWith);
            }
             } catch (Exception e) {

		}
        }
        
        public void Login(){ /////najlepiej chyba żeby przyjmował dane w tablicy?
            try {
                System.out.println("loguje sie");
                outputStream.writeInt(LOGIN); ///wysyłamy do serwera co chcemy zrobić
                /*albo wysyłamy credentiale na raz albo po kolei
                outputStream.writeObject(credentials);
                */
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void Register(){
            try {
                outputStream.writeInt(REGISTER);
                
                
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
}

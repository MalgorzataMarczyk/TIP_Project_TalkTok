package talktok_server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class ClientThread extends Thread {

	private Socket socket;
	private String hostname;
        private ClientInfo clientInfo;
        
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private static final int CONNECT = 0;
	private static final int DISCONNECT = 1;
	private static final int UPDATE = 2;
	private static final int CALL = 3;
	private static final int END_CALL = 4;
	private static final int ERROR = 5;
	private static final int MIC_ON  = 6;
        private static final int MIC_OFF = 7;
        private static final int ADD_FRIEND = 8;
        private static final int REGISTER = 9;
        private static final int LOGIN = 10;
        private static final int UPDATEDATA = 11;
        private static final int UPDATEIMG = 12;
        private static final int SERVERSENTIMG = 97;
        private static final int SENDHISTORY=13;
        private static final int ASK_IP = 14;
        private static final int SEND_IP = 15;
        private static final int CALL_INFORM = 16;
        private static final int GET_USER_IP_BY_NAME = 17;
        private static final int CALL_ACK = 18;
        private static final int SEND_END_CALL = 20;
        private static final int SERVER_GET_FRIENDS = 30;
        private static final int GET_USER_STATUS = 40;
        private static final int USER_STATUS = 41;
        
	boolean listening;
	int id;
        public String[] userDataArray = new String[5];
        public Vector<String> userContactVector = new Vector<String>();
        
        private static final int USER_NAME = 0;
        public String [] userData;
        public File imgFile;
        public String TrueUsername = null;
        public String userIP;
        public String nameForIP;
        
	public ClientThread(Socket socket, int num) {
		this.socket = socket;
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.out.println(e);
		}
		this.hostname = socket.getInetAddress().getHostName();
                this.clientInfo = null;
		listening = true;
		this.id = num;

                        
	}

	public void run() {
		Talktok_Server.connectClient(this);
		broadcastMessage(CONNECT, hostname);
		updateClients();
		while (listening) {
                    
			try {
				int command = inputStream.readInt();
                                    
                                Talktok_Server.updateGUI(command + " is a command");
				if (command == DISCONNECT) {
					Talktok_Server.disconnectClient(id);
					broadcastMessage(DISCONNECT, hostname);
					updateClients();
					listening = false;
					break;
				} else if (command == ADD_FRIEND) {
                                      String [] updateData = (String[]) inputStream.readObject();
                                      AddContact(updateData);
                                      sendContactList();
                                        
				} else if (command == CALL) {
                                
					String destination = (String) inputStream.readObject();
					setUpCall(destination);				
				} else if (command == END_CALL) {
					String destination = (String) inputStream.readObject();
					endCall(destination);
					Talktok_Server.updateGUI(hostname + " terminated a call with "
							+ destination);
				} else if (command == MIC_ON) {
						String destination = (String) inputStream.readObject();
                                                unmuteMic(destination);
						Talktok_Server.updateGUI(hostname + " turn on mic");
				}else if (command == MIC_OFF) {
						String destination = (String) inputStream.readObject();
                                                muteMic(destination);
						Talktok_Server.updateGUI(hostname + " turn off mic");
				}else if (command == REGISTER) {
					readRegisterData();
				}else if (command == LOGIN) {
                                        readLoginData();
				}else if (command == UPDATEDATA){
                                    userDataArray = (String []) inputStream.readObject();
                                    UpdateData(userDataArray);
                                }else if (command == UPDATEIMG){
                                    UpdateImage((byte[]) inputStream.readObject());
                                } else if(command == SENDHISTORY){
                                    sendHistoryData();
                                }else if(command == ASK_IP){
                                    String callUserName = (String) inputStream.readObject();
                                    getCallUserIP(callUserName);
                                }else if(command == GET_USER_IP_BY_NAME){
                                    nameForIP = (String) inputStream.readObject();
                                    System.out.println("Name for calling IP: "+nameForIP);
                                    ClientInfo callCielnt = ClientMap.getObject(nameForIP);
                                    userIP = callCielnt.getIPaddress();
                                    //System.out.println(userIP);
                                }else if(command == CALL_ACK){
                                    String inCallUserName = (String) inputStream.readObject();
                                    
                                    callingToUser(inCallUserName, 19, TrueUsername);
                                }else if(command == SEND_END_CALL){
                                    
                                 
                                    String inCallUserName = (String) inputStream.readObject();
                                    String UserWhichCalled = (String) inputStream.readObject();
                                    String Time = (String) inputStream.readObject();
                                    if(UserWhichCalled.equals("me")){
                                        UserWhichCalled=TrueUsername;}
                                    
                                    if (!Time.equals("none")){
                                        System.out.println("koniec rozmowy" + UserWhichCalled + "-" + inCallUserName + " : " + Time);
                                     /////wpychamy do bazy historie bo wiemy ze trwało połączenie
                                     insertStory(UserWhichCalled,inCallUserName,Time);
                                     
                                    
                                    }
                                    else {System.out.println("Odrzuciło");}
                                    
                                    
                                    
                                    
                                    
                                    callingToUser(inCallUserName, END_CALL, inCallUserName);
                                }
                                else if(command == SERVER_GET_FRIENDS){
                                    System.out.println(SERVER_GET_FRIENDS);
                                    String temp = (String)inputStream.readObject();
                                    
                                    sendContactList();

                                }else if(command == GET_USER_STATUS){
                                    String userName = (String) inputStream.readObject();
                                    String userStatus = ClientMap.getClientStatus(userName);
                                    outputStream.writeInt(USER_STATUS);
                                    outputStream.writeObject(userStatus);
                                }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static void updateClients() {
		String clientList[] = new String[Talktok_Server.clients.size()];
		int count = 0;
		for (ClientThread c : Talktok_Server.clients) {
			clientList[count] = c.getHostname();
			count++;
		}
		broadcastMessage(UPDATE, clientList);
	}

	public static void broadcastMessage(int type, Object message) {
		for (ClientThread c : Talktok_Server.clients) {
			try {
				c.outputStream.writeInt(type);
				c.outputStream.writeObject(message);
			} catch (IOException e) {
			}
		}
	}
        public static void callingToUser(String targetUser, int type, Object message) {
		for (ClientThread c : Talktok_Server.clients) {
                    if(c.userData[USER_NAME].equals(targetUser))
			try {
				c.outputStream.writeInt(type);
				c.outputStream.writeObject(message);
			} catch (IOException e) {
			}
		}
	}

	private void setUpCall(String destination) {
		for (ClientThread c : Talktok_Server.clients) {
			if (c.getHostname().equals(destination)) {
				try {
					if (c == this) {
						c.outputStream.writeInt(ERROR);
						outputStream
								.writeObject("Cannot initiate a voice call with yourself");
					} else {
						c.outputStream.writeInt(CALL);
						c.outputStream.writeObject(hostname);
						Talktok_Server.updateGUI(hostname + " initiated a call with "
								+ destination);
					}
				} catch (IOException ex) {
					System.out.println(ex);
				}
			}
		}
	}

	private void endCall(String destination) {
		for (ClientThread c : Talktok_Server.clients) {
			if (c.getHostname().equals(destination)) {
				try {
                                    
					c.outputStream.writeInt(END_CALL);
					c.outputStream.writeObject(hostname);
				} catch (Exception e) {

				}
			}
		}
	}
        
        private void getCallUserIP(String callUserName) throws IOException{
            //Talktok_Server.updateGUI("getCallUserIP");
            ClientInfo callCielnt = ClientMap.getObject(callUserName);
            String callUserIP = callCielnt.getIPaddress();
            outputStream.writeInt(SEND_IP);
            outputStream.writeObject(callUserIP);
            Talktok_Server.updateGUI("Call inform for: " + callUserName);
            //callingToUser(callUserName, SEND_IP,userIP );
            String[] callData = new String[2];
            callData[0] = userIP;
            callData[1] = nameForIP;
            callingToUser(callUserName, CALL_INFORM, callData);
            
        }
	
        private void muteMic(String destination) {
		for (ClientThread c : Talktok_Server.clients) {
			if (c.getHostname().equals(destination)) {
				try {
					c.outputStream.writeInt(MIC_OFF);
					c.outputStream.writeObject(hostname);
				} catch (Exception e) {

				}
			}
		}
	}
        
        private void unmuteMic(String destination) {
		for (ClientThread c : Talktok_Server.clients) {
			if (c.getHostname().equals(destination)) {
				try {
					c.outputStream.writeInt(MIC_ON);
					c.outputStream.writeObject(hostname);
				} catch (Exception e) {

				}
			}
		}
	}
        
	private void sendIndividualMessage(String recipient, int type, Object obj) {
		for (ClientThread c : Talktok_Server.clients) {
			if(c.getHostname().equals(recipient)) {
				try {
					c.outputStream.writeInt(type);
					c.outputStream.writeObject(hostname);
					c.outputStream.writeObject(obj);
				} catch (Exception e) {
					
				}
			}
		}
	}

        private void readRegisterData() throws IOException, ClassNotFoundException{
            String [] userData;
            userData = (String[]) inputStream.readObject();
            //Zwrotny message to cliena. -1 error, 0 - po stronie klienta nie otrzymalem jeszcze odpowiedzi
            //1 - wszytko ok, 2 - uzytkownik o takim loginie istnieje, 3 - uzytkownik o takim mailu istnieje
            int message=-1;
         
            Class.forName("com.mysql.jdbc.Driver");  
            try{
                Connection con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/tip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","password");  
                Statement stmt=con.createStatement();
                //Sprawdzanie czy dany uzytkownik istnieje juz w bazie - login//
                ResultSet select = stmt.executeQuery("SELECT COUNT(username) AS usercount FROM users WHERE username = '" + userData[0] + "';");
                select.next();
                boolean userCount = select.getInt("usercount") == 0;
                
                //Sprawdzanie czy dany uzytkownik istnieje juz w bazie - email//
                ResultSet selectEmail = stmt.executeQuery("SELECT COUNT(email) AS useremail FROM users WHERE email = '" + userData[1] + "';");
                selectEmail.next();
                boolean userMailCount = selectEmail.getInt("useremail") == 0;
                if(userCount && userMailCount)
                {
                    ////generowanie salta ldla użytkownika
                   String salt = BCrypt.gensalt();
                   ////Hashowanie hasła saltem 
                   String hashed = BCrypt.hashpw(userData[2], salt);
                   
                    int rs=stmt.executeUpdate("INSERT INTO users (username, email, password, salt, birthdate, join_date, last_online_date)\n" +
                "VALUES"+ "('" + userData[0] +"','" + userData[1] +"','"+ hashed +"','" + salt + "','" + userData[3] + "',CURDATE(),CURDATE());");  
                    message =1;
                }
                else{
                    if(userCount)
                        message = 3;
                    else
                        message = 2;
                }
                con.close();       
                
            
            } catch (SQLException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
             
            Talktok_Server.updateGUI(hostname + " response for code 99: " + message);
            outputStream.writeInt(99);
            outputStream.writeObject(message);
            //here sonoo is database name, root is username and password  
 
           
        }
        
        private void readLoginData() throws IOException, ClassNotFoundException{
            
            userData = (String[]) inputStream.readObject();
             TrueUsername = userData[0];
            //Zwrotny message to cliena. -1 error, 0 - po stronie klienta nie otrzymalem jeszcze odpowiedzi
            //1 - wszytko ok, 4 - błedne hasło
            int message=-1;
            Class.forName("com.mysql.jdbc.Driver");
            byte[] Imagebuffer = null; 
            try{
                Connection con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/tip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","password");  
                Statement stmt=con.createStatement();
                ResultSet qrUserData = stmt.executeQuery("SELECT password, salt FROM users WHERE username = '" + userData[0] + "';");
                qrUserData.next();
                
                String password = qrUserData.getString("password");
                String salt = qrUserData.getString("salt");    
                
                //hashujemy otrzymane przez użytkownika hasło
                String hashed = BCrypt.hashpw(userData[1], salt);
                
                //porównujemy z tym w bazie
                if (password.compareTo(hashed)==0){
                    message =1;}
                else
                    message=4;
                qrUserData = stmt.executeQuery("SELECT username, email, status, description, last_online_date FROM users WHERE username = '" + userData[0] + "';");
                
                try{
                while(qrUserData.next()){
                    String un = qrUserData.getString("username");
                    String ue = qrUserData.getString("email");
                    String ust = qrUserData.getString("status");
                    String udes = qrUserData.getString("description");
                    Date udt = qrUserData.getDate("last_online_date");
                    userDataArray[0] = un;
                    userDataArray[1] = ue;
                    userDataArray[2] = ust;
                    userDataArray[3] = udes;
                    userDataArray[4] = udt.toString();
                }
               
                ResultSet resultImg = stmt.executeQuery("SELECT photo FROM users WHERE username = '" + userData[0] + "';");
                InputStream imageStream = null;
                while(resultImg.next())
                {
                    imageStream = resultImg.getBinaryStream(1);
                }
                if(imageStream != null)
                {
                    Imagebuffer = new byte[imageStream.available()];
                    imageStream.read(Imagebuffer);
                }

                        
                }catch (SQLException e ) {
                    
                }finally {
                    con.close();
                }
                
            }catch(SQLException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);  
            }
            Talktok_Server.updateGUI(hostname + " response for code 99: " + message);
            outputStream.writeInt(99);
            outputStream.writeObject(message);
                       
            outputStream.writeInt(98);
            outputStream.writeObject(userDataArray);
            
            if(Imagebuffer != null){
                outputStream.writeInt(97);
                outputStream.writeObject(Imagebuffer); 
            }
            
            ClientMap.addObject(
                    userDataArray[USER_NAME],
                    new ClientInfo(
                            socket.getInetAddress().getHostAddress(),
                            ClientInfo.DOSTEPNY,
                            userDataArray[USER_NAME]
                    ));
            sendContactList();
            sendHistoryData();
            
        }
        
        public void sendContactList(){
            try{
            ////////////ściągamy z bazy w pętli kontakty
            Connection con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/tip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","password");  
                Statement stmt=con.createStatement();
                ResultSet resultContact = stmt.executeQuery("SELECT username, alias, photo, description, status FROM contact_list cl join users u on cl.friend_id=u.user_id where cl.owner_id = (select user_id from users where username = '" + TrueUsername + "');");
               
                
                outputStream.writeInt(96);
                System.out.println("List Sended");
                while(resultContact.next())
                {
                    
                    String [] HistoryDataArray = new String[4];
                    String contactName = resultContact.getString("username");
                    HistoryDataArray[USER_NAME] = contactName;
                    HistoryDataArray[1] = resultContact.getString("alias");
                    HistoryDataArray[2] = resultContact.getString("description");
                    HistoryDataArray[3] = ClientMap.getClientStatus(contactName);
               
                    userContactVector.add(contactName);
               
                    System.out.println(contactName);
                    outputStream.writeObject(HistoryDataArray);
                }
            con.close();
            }catch(Exception e){Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, e);  }
        }
        
	public String getHostname() {
		return hostname;
	}
        
        public void UpdateData(String[] updateData) throws ClassNotFoundException{
            Class.forName("com.mysql.jdbc.Driver");  
            try{
                Connection con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/tip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","password");  
                Statement stmt=con.createStatement();
                PreparedStatement update = con.prepareStatement("UPDATE users SET status = ?, description = ?, last_online_date = CURRENT_DATE WHERE username = '" + updateData[0] + "';");
                int status = Integer.parseInt(updateData[2]);
                update.setInt(1,status);
                update.setString(2, updateData[3]);
                update.executeUpdate();
                update.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void UpdateImage(byte[] ImageByte) throws ClassNotFoundException, FileNotFoundException {
            Class.forName("com.mysql.jdbc.Driver");
            // FileInputStream inputStream = new FileInputStream(imageFile);
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "password");
                Statement stmt = con.createStatement();
                //System.out.println(userData[0]);
                PreparedStatement update = con.prepareStatement("UPDATE users SET photo = ? WHERE username = '" + userData[0] + "';");
                update.setBytes(1, ImageByte);
                update.executeUpdate();
                update.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ////[0] - dodający, [1] - dodawany, [2] - alias
        //// message 1 - sukces, message 2 - użytkownik nie istnieje, message 3 - już masz go w znajomych
        public void AddContact(String[] updateData) throws ClassNotFoundException{
            
           
             int message=-1;
            Class.forName("com.mysql.jdbc.Driver");  
            try{
                 Connection con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/tip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","password");  
               
                 System.out.println(updateData[0]+ " - " +  updateData[1] + " - " +  updateData[2]);
                 
                 
                 Statement stmt=con.createStatement();
                //Sprawdzanie czy dany uzytkownik istnieje w bazie //
                ResultSet selectUser = stmt.executeQuery("SELECT COUNT(username) AS usercount FROM users WHERE username = '" + updateData[1] + "';");
                selectUser.next();
                boolean UserExists = selectUser.getInt("usercount") > 0;  ///true jeśli istnieje użytkownik ktorego chcemy dodać
                
                //Sprawdzanie czy użytkownika mamy już na swojej liscie kontaktów //
                ResultSet selectContact = stmt.executeQuery("SELECT COUNT(friend_id) AS contactcount FROM contact_list WHERE friend_id = (Select user_id from users where username='"+ updateData[1] +"') and owner_id = (Select user_id from users where username='"+updateData[0]+"');");
                selectContact.next();
                boolean ContactExists = selectContact.getInt("contactcount") > 0; ///true jeśli mamy juz taki kontakt na liście
                
                if(UserExists && !ContactExists) ///istnieje użytkownik i nie mamy go w kontaktach
                {
                   
                    int rs=stmt.executeUpdate("insert into contact_list(owner_id, friend_id, alias) values((Select user_id from users where username='"+updateData[0]+"'),(Select user_id from users where username='"+ updateData[1] +"'),'"+ updateData[2] +"');");  
                    message =1; ///sukces
                }
                else{
                    if(!UserExists)
                        message = 2;
                    else
                        message = 3;
                }
                System.out.println("contacs: " + message);

                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            userContactVector.add(updateData[1]);
            try {
                outputStream.writeInt(99);
                outputStream.writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        
    private void sendHistoryData() {
        
        try{
            ////////////ściągamy z bazy w pętli historie
            Connection con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/tip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","password");  
             Statement stmt=con.createStatement();
             Statement stmt2=con.createStatement();
             
    
                   
             
             ResultSet resultMyId = stmt.executeQuery("select user_id from users where username='" + TrueUsername + "';");
              String myId =null;
             if (resultMyId.next()) {
             myId = resultMyId.getString("user_id"); ///id użytkownika 
             }
             
             ResultSet resultHistory = stmt.executeQuery("select caller_id, receiver_id, time_start, time_end from call_history where (select user_id from users where username= '" + TrueUsername + "')=receiver_id or (select user_id from users where username='" + TrueUsername + "')=caller_id order by time_end DESC;");
             
             
            
                 outputStream.writeInt(13);
                while(resultHistory.next())
                {
                   String [] ContactDataArray = new String[4];
                   String caller = resultHistory.getString("caller_id");
                   String receiver = resultHistory.getString("receiver_id");
                   String time_start = resultHistory.getString("time_start");
                   String time_end = resultHistory.getString("time_end");
                   
                   
                   if(caller.equals(myId))///ja dzwoniłam do kogoś
                   {caller="Ja";
                   ResultSet resultFriendId = stmt2.executeQuery("select alias from users u join contact_list cl on cl.friend_id=u.user_id where cl.owner_id='" + myId + "' and friend_id='" + receiver + "';");
                   if (resultFriendId.next()) {
                   receiver = resultFriendId.getString("alias");}
                   
                    } else if(receiver.equals(myId)){ ////ktoś dzwonił do mnie
                   receiver="Ja";
                   ResultSet resultFriendId2 = stmt2.executeQuery("select alias from users u join contact_list cl on cl.friend_id=u.user_id where cl.owner_id='" + myId + "' and friend_id='" + caller + "';");
                   if (resultFriendId2.next()) {
                   caller = resultFriendId2.getString("alias");
                   }
                   
                   }
                   
                String data = time_start;
                
                
                
                
               ContactDataArray[0] = caller;
               ContactDataArray[1] = receiver;
               ContactDataArray[2] = data;
               ContactDataArray[3] = time_end;
               System.out.println(ContactDataArray[0]+"_" +ContactDataArray[1]+"_" +ContactDataArray[2]+"_" + ContactDataArray[3]);
               outputStream.writeObject(ContactDataArray);
                }
            
            
            

            
            con.close();
            }catch(Exception e){Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, e);  }
        
        
        
    }

    private void insertStory(String caller, String receiver, String time){
         Connection con;  
            try {
                con = DriverManager.getConnection( "jdbc:mysql://localhost:3306/tip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","password");
            

            Statement stmt=con.createStatement();
            long millis=System.currentTimeMillis();  
            java.sql.Date timestamp=new java.sql.Date(millis); 

        System.out.println(timestamp);
             int rs=stmt.executeUpdate("insert into call_history(caller_id, receiver_id, time_start, time_end, status) values((select user_id from users where username='"+caller+"'),(select user_id from users where username='"+receiver+"'),'"+timestamp+"','"+time+"',\"0\");");  
                  
            } catch (SQLException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
}

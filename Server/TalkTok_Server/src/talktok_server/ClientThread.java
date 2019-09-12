package talktok_server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import org.mindrot.jbcrypt.BCrypt;

public class ClientThread extends Thread {

	private Socket socket;
	private String hostname;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
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
        private final int SERVERSENTIMG = 97;
	boolean listening;
	int id;
        public String[] userDataArray = new String[5];
        public String [] userData;
        public File imgFile;

	public ClientThread(Socket socket, int num) {
		this.socket = socket;
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.out.println(e);
		}
		this.hostname = socket.getInetAddress().getHostName();
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
                                }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateClients() {
		String clientList[] = new String[Talktok_Server.clients.size()];
		int count = 0;
		for (ClientThread c : Talktok_Server.clients) {
			clientList[count] = c.getHostname();
			count++;
		}
		broadcastMessage(UPDATE, clientList);
	}

	private void broadcastMessage(int type, Object message) {
		for (ClientThread c : Talktok_Server.clients) {
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
            String TrueUsername = userData[0];
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
                    if (stmt != null) { con.close(); }
                }
                
            }catch(SQLException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);  
            }
            Talktok_Server.updateGUI(hostname + " response for code 99: " + message);
            outputStream.writeInt(99);
            outputStream.writeObject(message);
            
//            for(String data : userDataArray){
//            System.out.println(data);
//        }
            
            outputStream.writeInt(98);
            outputStream.writeObject(userDataArray);
            
                
            try{
            ////////////ściągamy z bazy w pętli kontakty
            Connection con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/tip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","password");  
                Statement stmt=con.createStatement();
             ResultSet resultContact = stmt.executeQuery("SELECT username, alias, photo, description, status FROM contact_list cl join users u on cl.owner_id=u.user_id where username = '" + TrueUsername + "';");
                
                 outputStream.writeInt(96);
                while(resultContact.next())
                {
                   String [] ContactDataArray = new String[4];
               ContactDataArray[0] = resultContact.getString("username");
               ContactDataArray[1] = resultContact.getString("alias");
               ContactDataArray[2] = resultContact.getString("description");
               ContactDataArray[3] = resultContact.getString("status");
               System.out.println(ContactDataArray[0]);
               outputStream.writeObject(ContactDataArray);
                }
            
            
            

            
            con.close();
            }catch(Exception e){Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, e);  }
            
            
            if(Imagebuffer != null){
                outputStream.writeInt(97);
                outputStream.writeObject(Imagebuffer); 
            }
            
            
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

            
            try {
                outputStream.writeInt(99);
                outputStream.writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }

}

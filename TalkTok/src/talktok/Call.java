package talktok;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Call {

	/* Audio variables */
	public volatile boolean capturing = false;
	static volatile boolean inCall = false;
        public volatile boolean hearing = false;
	String inCallWith;
	AudioFormat audioFormat;
	TargetDataLine mic;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;

	public Call(int initiator, int port1, int port2, String partner) {
		inCall = true;
		capturing = true;
                hearing = true;
		if (initiator == 0) {
			new ReceiveThread(port1).start();
			new SendThread(partner, port2).start();
		} else if (initiator == 1) {
			new ReceiveThread(port2).start();
			new SendThread(partner, port1).start();
		}
                
                
                
	}

	class ReceiveThread extends Thread {

		DatagramSocket ds;

		public ReceiveThread(int port) {
			try {
				ds = new DatagramSocket(port);

				AudioFormat audioFormat = getAudioFormat();
				
				
				DataLine.Info dataLineInfo = new DataLine.Info(
						SourceDataLine.class, audioFormat);
				sourceDataLine = (SourceDataLine) AudioSystem
						.getLine(dataLineInfo);			
				sourceDataLine.open(audioFormat);
			} catch (SocketException ex) {
				
			} catch (LineUnavailableException e) {
				
			}
		}

		public void run() {
			try {
				sourceDataLine.start();

				byte[] temp = new byte[5000];
				while (inCall) {
                                           while (inCall && hearing){
                                    ////////////psuje sie jak wyciszamy mikrofon
					DatagramPacket packet = new DatagramPacket(temp,
							temp.length);
					ds.receive(packet);
					byte[] input = packet.getData();
                                       
					sourceDataLine.write(input, 0, input.length);
				}
                               }
                                System.out.println("wychodzimy");
				sourceDataLine.drain();
				sourceDataLine.close();

				ds.close();

			} catch (Exception e) {
			}
		}
	}

	class SendThread extends Thread {

		DatagramSocket ds;
		String destination;
		int destPort;

		public SendThread(String destination, int port) {
			try {
				this.ds = new DatagramSocket();

				audioFormat = getAudioFormat();
				DataLine.Info dataLineInfo = new DataLine.Info(
						TargetDataLine.class, audioFormat);
				mic = (TargetDataLine) AudioSystem
						.getLine(dataLineInfo);
				mic.open(audioFormat);
			} catch (SocketException ex) {
				Logger.getLogger(Client.class.getName()).log(Level.SEVERE,
						null, ex);
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.destination = destination;
			this.destPort = port;
		}

		public void run() {
			try {
				mic.start();

				InetAddress receiverAddress = InetAddress
						.getByName(destination);

				byte temp[] = new byte[10000];
				while (inCall) {
                                    while (capturing && inCall){
					int cnt = mic.read(temp, 0, temp.length);
					if (cnt > 0) {
						DatagramPacket packet = new DatagramPacket(temp, cnt,
								receiverAddress, destPort);
						ds.send(packet);
					}
				}
                                }
				ds.close();
				mic.close();
			} catch (Exception e) {
			}
		}
	}

	private AudioFormat getAudioFormat() {
		float sampleRate = 8000.0F;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;

		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

	public void endCall() {
		capturing = false;
                hearing = false;
		inCall = false;
	}

         
        
}

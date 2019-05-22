/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Lena
 */
public class RecorderThread extends Thread{
    
     public DatagramSocket out;
    public TargetDataLine audio_in = null;
    byte[] buffer = new byte[512];
    public InetAddress server_ip;
    public int server_port;
    
    @Override
    public void run(){
    int i=0;
    System.out.println("Start");
        while(MainController.calling){
        
            audio_in.read(buffer,0,buffer.length); 
            DatagramPacket data = new DatagramPacket(buffer,buffer.length,server_ip,server_port); ///wysylanie
            System.out.println("send #"+i++);
        try {
            out.send(data);
        } catch (IOException ex) {
            Logger.getLogger(RecorderThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
        
    
    }
    
}

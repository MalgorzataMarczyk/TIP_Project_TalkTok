/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Lena
 */
public class PlayerThread extends Thread{
    
    public DatagramSocket in;
    public SourceDataLine data_out;
    byte[] buffer = new byte[512];
    
    @Override
    public void run(){
    DatagramPacket DP_in = new DatagramPacket(buffer, buffer.length);
    int i=0;
    
        System.out.println("wchodze"); 
    while(!MainController.Exit){
        
        System.out.println("wchodze");    
        
        try {
            in.receive(DP_in);
            buffer = DP_in.getData();
            data_out.write(buffer, 0, buffer.length);
            System.out.println("#"+i++);
        } catch (IOException ex) {
            Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    
    
    
    }
    
    data_out.drain();
    data_out.close();
    
    System.out.println("Stop");
    
    
    }
    
    
    public void end(){
        MainController.Exit = true;
    }
    
    
    
}

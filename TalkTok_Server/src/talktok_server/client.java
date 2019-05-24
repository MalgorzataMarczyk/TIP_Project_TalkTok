/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok_server;

import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Lena
 */
public class client {
    public Socket socket = null;
    public int ID;
    ObjectOutputStream dout = null;
    public int int_status;

    public client(Socket sk,int id) {
        this.socket = sk;
        this.ID = id;
    }
    
}

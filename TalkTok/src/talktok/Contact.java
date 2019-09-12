/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

import java.io.Serializable;

/**
 *
 * @author Lena
 */
public class Contact  implements Serializable {

    public String getStatus() {
       return status;
    }

    public String getAlias() {
        return alias;
    }
    
     public String getUsername() {   ///z racji ze to sie w sumie i tak nie wyswietla to mozna trzymac userID a nie username? nie wiem co lepsze
        return username;
    }  
     
    String alias;
    String username;
    String opis;
    ////image ale nie wiem jak 
    String status; ////0-offline, 1-online?
    
    Contact(String username, String alias, String opis, String status){
    this.alias = alias;
    this.username = username;
    this.opis = opis;
    this.status=status;
    }

    public String getOpis() {
        return opis;
    }
    
   
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talktok;

/**
 *
 * @author Lena
 */
public class Contact {

    public int getStatus() {
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
    ////image ale nie wiem jak 
    int status; ////0-offline, 1-online?
    
    Contact(String username, String alias, int status){
    this.alias = alias;
    this.username = username;
    this.status=status;
    }
    
   
}

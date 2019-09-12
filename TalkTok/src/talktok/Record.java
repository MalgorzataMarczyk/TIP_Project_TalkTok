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
public class Record {
    
    
    
     public String getCaller() {   ///z racji ze to sie w sumie i tak nie wyswietla to mozna trzymac userID a nie username? nie wiem co lepsze
        return caller;
    }  
     
    
    String caller;
    String answer;
    ////data
    String time; ////0-offline, 1-online?
    
    Record(String caller, String answer, String time){
    this.caller = caller;
    this.answer = answer;
    this.time=time;
    }
    
    
}

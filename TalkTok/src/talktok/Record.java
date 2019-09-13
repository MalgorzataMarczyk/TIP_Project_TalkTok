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
    
    
    
     public String getCaller() {   
        return caller;
    }  
      public String getAnswer() {   
        return answer;
    }  
       public String getTime() {   
           return time;
    }  
        public String getDate() {   
        return date;
    }  
    
    String caller;
    String answer;
    String time; 
    String date;
    
    Record(String caller, String answer, String time, String date){
    this.caller = caller;
    this.answer = answer;
    this.time=time;
    this.date=date;
    }
    
    
}

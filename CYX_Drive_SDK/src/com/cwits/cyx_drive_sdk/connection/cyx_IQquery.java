package com.cwits.cyx_drive_sdk.connection;

import org.jivesoftware.smack.packet.IQ;

public class cyx_IQquery extends IQ{
   private String Jsonobj;


    public String getElementName() {  
        return "forward";  
    }  
  
    public String getNamespace() {  
        return "com:cwits:cyxmobilegate";  
    }  
  
    public String getBody() {  
     StringBuilder sb = new StringBuilder(); 
    if(null!=Jsonobj){
    sb.append(Jsonobj);
    }
        return sb.toString();  
    }  
  
   

public String getJsonobj() {
      return Jsonobj;
        }


     public void setJsonobj(String Jsonobj) {
           this.Jsonobj = Jsonobj;
        }

  @Override  
    public String getChildElementXML() {  
        StringBuilder sb = new StringBuilder();  
        sb.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">").append(getBody()).append("</").append(getElementName()).append(">");  
        return sb.toString();  
    }  
}

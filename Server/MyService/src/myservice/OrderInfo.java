/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myservice;

/**
 *
 * @author LeoneYi
 */
public class OrderInfo {
    String orderID;
    String srcAdr;
    String dstAdr;
    String userName;
    String recivUserName;
    String createTime;
    String deadlineTime;
    String finishTime;
    String weight;
    int orderState;
    String orderFee;
    String noteInfo;
    int orderType;
    
   public OrderInfo(String srcAdr,String dstAdr,String userName,String createTime,String deadlineTime,String weight,String orderFee,String noteInfo,String orderType){
       this.orderID="";
       this.srcAdr=srcAdr;
       this.dstAdr=dstAdr;
       this.userName=userName;
       this.recivUserName="";
       this.createTime=createTime;
       this.deadlineTime=deadlineTime;
       this.finishTime="";
       this.weight=weight;
       this.orderState=1;
       this.orderFee=orderFee;
       this.noteInfo=noteInfo;
       this.orderType=Integer.valueOf(orderType);
       
   }
   public void setOrderState(int state){
       this.orderState=state;
   }
   public void setOrderID(String ID){
       this.orderID=ID;
   }
   public String getOrderID(){
       return this.orderID;
   }
   public String getSrcAdr(){
       return this.srcAdr;
   }
   public String getDstAdr(){
       return this.dstAdr;
   }
   public String getUserName(){
       return this.userName;
   }
   public String getRecivUserName(){
       return this.userName;
   }
   public String getCreateTime(){
       return this.createTime;
   }
   public String getDeadlineTime(){
       return this.deadlineTime;
   }
   public String getFinishTime(){
       return this.finishTime;
   }
   public String getWeight(){
       return this.weight;
   }
   public String getOrderState(){
       return String.valueOf(this.orderState);
   }
   public String getOrderFee(){
       return this.orderFee;
   }
   public String getNoteInfo(){
       return this.noteInfo;
   }
   public String getOrderType(){
       return String.valueOf(this.orderType);
   }
    
}

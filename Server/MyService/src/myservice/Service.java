/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myservice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;

/**
 *
 * @author LeoneYi
 */
public class Service {
     public final int port = 2346;
    ServerSocket serverSocket = null;
  
    public Service(){
        //输出服务器的IP地址
        try {
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("local host:"+addr);
            serverSocket = new ServerSocket(port);
            System.out.println("0k");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
   
     public void startService(){

    try {
        Socket socket = null;
        System.out.println("waiting...");
        //等待连接，每建立一个连接，就新建一个线程
        while(true){                
            socket = serverSocket.accept();//等待一个客户端的连接，在连接之前，此方法是阻塞的
            System.out.println("connect to"+socket.getInetAddress()+":"+socket.getLocalPort());
            new ConnectThread(socket).start();
        }

    } catch (IOException e) {
        // TODO Auto-generated catch block
        System.out.println("IOException");
        e.printStackTrace();
    }
}

//向客户端发送信息
class ConnectThread extends Thread{
    Socket socket = null;

    public ConnectThread(Socket socket){    
        super();
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            while(true){
                
                //这里接收客户端发来的消息
                String msgRecv = dis.readUTF();
                System.out.println("msg from client:"+msgRecv);
                //dos.writeUTF("received:"+msgRecv);
                //这里对消息进行处理并返回消息给客户端
                String[]msgCheck=msgRecv.split("\\|");
                String msgReturn="-1";
                switch(msgCheck[0]){
                    case "loginCheck":
                        msgReturn=loginCheck(msgRecv);
                        break;
                    case "regCheck":
                        msgReturn=regCheck(msgRecv);
                        break;
                    case "userInfoChange":
                        msgReturn=userInfoChangeCheck(msgRecv);
                        break;
                    case"userInfoGet":
                        msgReturn=SqlServer.userInfoGet(msgCheck[1]);
                        break;
                    case"orderAdd":
                        msgReturn=orderAdd(msgRecv);
                        break;
                    case"myOrderInfoGet":
                        msgReturn=SqlServer.myOrderInfoReturn(msgCheck[1]);
                        break;
                    case"onlineOrderInfoGet":
                        msgReturn=SqlServer.onlineOrderInfoReturn(msgCheck[1]);
                        break;
                    case"acOrderInfoGet":
                        msgReturn=SqlServer.acOrderInfoReturn(msgCheck[1]);
                        break;
                    case "myOrderInfoChange":
                        msgReturn=SqlServer.myOrderInfoChange(msgCheck[1]);
                        break;
                    case "onlineOrderInfoChange":
                        msgReturn=SqlServer.onlineOrderInfoChange(msgCheck[1]+"|"+msgCheck[2]);
                        break;
                    case "acOrderInfoChange":
                        msgReturn=SqlServer.acOrderInfoChange(msgCheck[1]);
                        break;
                            
                    default:
                        break;
                }
                System.out.println("已向客户端发送"+msgReturn);
                dos.writeUTF(msgReturn);
                dos.flush();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public String loginCheck(String msg){
        String[]loginInfo=msg.split("\\|");
//        for(int i=0;i<loginInfo.length;i++){
//            System.out.println(loginInfo[i]);
//        }
        UserInfo userInfo=new UserInfo(loginInfo[1],loginInfo[2]);
        return SqlServer.loginCheck(userInfo);
        
    }
      public String regCheck(String msg){
        String[]regInfo=msg.split("\\|");
//       
        UserInfo userInfo=new UserInfo(regInfo[1],regInfo[2]);
        return SqlServer.regCheck(userInfo);
        
    }
      public String userInfoChangeCheck(String msg){
          String[]userInfoChange=msg.split("\\|");
          
          UserInfo userInfo=new UserInfo(userInfoChange[1],"",userInfoChange[2],userInfoChange[3]);
          
          return SqlServer.userInfoChangeCheck(userInfo);
      }
      public  Date adjustDateByHour(Date d ,Integer num, int  type) {
	Calendar Cal= Calendar.getInstance();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	Cal.setTime(d);  
	if(type==0){
		Cal.add(Calendar.HOUR_OF_DAY,-num);
	   // System.out.println("date:"+df.format(Cal.getTime()));
		
	}else
	{
		Cal.add(Calendar.HOUR_OF_DAY,num);
		//System.out.println("date:"+df.format(Cal.getTime()));
	}
	return Cal.getTime();
}
      public String orderAdd(String msg){
          String []str=msg.split("\\|");
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
          String Date=df.format(new Date());
          //String []str1=Date.split(" ");
          //String []str2=str1[1].split(":");
          //int i=(Integer.parseInt(str2[0])+Integer.parseInt(str[4]));
          
          //str2[0]=String.valueOf(i);
          String newTime=df.format(this.adjustDateByHour(new Date(),Integer.parseInt(str[4]) , 1));
          //String newTime=str1[0]+" "+str2[0]+":"+str2[1]+":"+str2[2];
          OrderInfo orderInfo=new OrderInfo(str[1],str[2],str[3],Date,newTime,str[5],str[6],str[7],str[8]);
          return SqlServer.orderAdd(orderInfo);
      }
    
}
    
}

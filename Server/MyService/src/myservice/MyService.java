/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myservice;
import java.lang.String;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author LeoneYi
 */
public class MyService {

    /**
     * @param args the command line arguments
     */

//    static public void ServerReceviedByTcp() {  
//    // 声明一个ServerSocket对象  
//    ServerSocket serverSocket = null;  
//    try {  
//        // 创建一个ServerSocket对象，并让这个Socket在1989端口监听  
//        serverSocket = new ServerSocket(1989);  
//        // 调用ServerSocket的accept()方法，接受客户端所发送的请求，  
//        // 如果客户端没有发送数据，那么该线程就停滞不继续  
//        while(true){
//            Socket socket = serverSocket.accept();  
//        // 从Socket当中得到InputStream对象  
//        InputStream inputStream = socket.getInputStream();  
//        byte buffer[] = new byte[1024 * 4];  
//        int temp = 0;  
//        // 从InputStream当中读取客户端所发送的数据  
//        while ((temp = inputStream.read(buffer)) != -1) {  
//            System.out.println(new String(buffer, 0, temp));  
//        } 
//        }
//      //  serverSocket.close();  
//    } catch (IOException e) {  
//        e.printStackTrace();  
//    }  
//}  
  public static void main(String[] args) {
        // TODO code application logic here
    Service service=new Service();
    service.startService();      
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author LeoneYi
 */
public class SqlServer {
     static public  Connection getConn(){//获取数据库连接
        String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";//SQL数据库引擎
        String dbURL="jdbc:sqlserver://localhost:1433;DatabaseName=AndroidPro";//数据源  ！！！！注意若出现加载或者连接数据库失败一般是这里出现问题
        String Name="sa";
        String Pwd="123456";
        Connection conn=null;
        try{
            Class.forName(driverName);
            conn=DriverManager.getConnection(dbURL,Name,Pwd);
            System.out.println("连接数据库成功");   
        }
        catch(Exception e){   
            e.printStackTrace();     
            System.out.println("数据库连接失败");
        }
        return conn;
    }
     static public String loginCheck(UserInfo userInfo){
          Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
           try{
                     sql="select userName,passWord from  UserLoginInfo where userName='"+userInfo.getUserName()+"'";
                     pstmt = (PreparedStatement)conn.prepareStatement(sql);
                     rs = pstmt.executeQuery();
                     if(!rs.next()){//检测用户名,如果结果集不为空，则next()返回true;
                        System.out.println("检查失败，不存在的用户名，返回0，禁止登录");
                        rs.close();
                        pstmt.close();
                        conn.close();
                        
                        return "0";
                        }
                     else{
                          if(rs.getString("passWord").equals(userInfo.getPassWord())){//检测密码，如果正确，接下来返回1
                             
                              
                              System.out.println("检查成功，密码正确，返回1，准许登录");
                              rs.close();
                              pstmt.close();
                              conn.close();                       
                              return "1";
                        }
                          else{
                              System.out.println("检查成功，密码错误，返回0，禁止登录");
                              rs.close();
                              pstmt.close();
                              conn.close();    
                              return "0";
                          }
                     }
                     
                    
                 }
         catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 }  
     }
     static public String regCheck(UserInfo userInfo){
          Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
           try{
                     sql="select userName from  UserLoginInfo where userName='"+userInfo.getUserName()+"'";
                     pstmt = (PreparedStatement)conn.prepareStatement(sql);
                     rs = pstmt.executeQuery();
                     if(!rs.next()){//检测用户名,如果结果集不为空，则next()返回true;
                        System.out.println("检查成功，用户名未注册，返回1，注册成功，尝试向数据库写入信息");
                            sql="insert into UserLoginInfo (userName,passWord) values(?,?)";
                          pstmt = (PreparedStatement) conn.prepareStatement(sql);
                          pstmt.setString(1, userInfo.getUserName());
                          pstmt.setString(2, userInfo.getPassWord());
                         
                          int i=pstmt.executeUpdate();//i是数据库中受影响的行数
                          
                          pstmt.close();
                          rs.close();
                          conn.close();
                        
                        return "1";
                        }
                     else{//用户名已存在
                          System.out.println("检查失败，用户名已注册，返回0，注册失败");
                          pstmt.close();
                          rs.close();
                          conn.close();
                          return "0";
                     }
                     
                    
                 }
         catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 }  
     }
      static public String userInfoChangeCheck(UserInfo userInfo){
           Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          try{
              System.out.println("尝试向数据库写入更改后的用户信息，返回1");
              sql="update UserLoginInfo set phoneNum='"+userInfo.getPhoneNum()+"'"+",defAdr='"+userInfo.getDefAdr()+"'"+" where userName='"+userInfo.getUserName()+"'";
              pstmt = (PreparedStatement) conn.prepareStatement(sql);
              int i=pstmt.executeUpdate();//i是数据库中受影响的行数
              pstmt.close();
              conn.close();
              return "1";
      
             }
           catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 } 
      }
      static public String userInfoGet(String userName){//检索数据库UserInfo表，向客户端发送数据
           Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          try{
              sql="select phoneNum,defAdr from  UserLoginInfo where userName='"+userName+"'";
                     pstmt = (PreparedStatement)conn.prepareStatement(sql);
                     
                     rs = pstmt.executeQuery();
                     rs.next();
                     String returnMsg="userInfoReturn|";
                     returnMsg+=rs.getString("phoneNum");
                     returnMsg+="|";
                     returnMsg+=rs.getString("defAdr");
                     pstmt.close();
                     conn.close();
                     return returnMsg;
                     
                     
              
          }
          catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 }  
          
          
      }
      static public String orderAdd(OrderInfo orderInfo){
            Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          try{
               sql="select count(orderID) from AndroidOrderInfo";
              pstmt = (PreparedStatement)conn.prepareStatement(sql);
              rs = pstmt.executeQuery();
              rs.next();
              int newID_Int=1+rs.getInt(1);
              orderInfo.setOrderID(String.valueOf(newID_Int));//获取并设置新的orderID
              
              System.out.println("尝试向数据库写入更改后的用户信息，返回1");
              sql="insert AndroidOrderInfo(orderID,srcAdr,dstAdr,userName,recivUserName,createTime,deadlineTime,finishTime,weight,orderState,orderFee,noteInfo,orderType) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
               pstmt = (PreparedStatement) conn.prepareStatement(sql);
               pstmt.setString(1, orderInfo.getOrderID());
               pstmt.setString(2, orderInfo.getSrcAdr());
               pstmt.setString(3, orderInfo.getDstAdr());
               pstmt.setString(4, orderInfo.getUserName());
               pstmt.setString(5, "");
               pstmt.setString(6, orderInfo.getCreateTime());
               pstmt.setString(7, orderInfo.getDeadlineTime());
               pstmt.setString(8, orderInfo.getFinishTime());
               pstmt.setString(9, orderInfo.getWeight());
               pstmt.setString(10, orderInfo.getOrderState());
               pstmt.setString(11, orderInfo.getOrderFee());
               pstmt.setString(12, orderInfo.getNoteInfo());
               pstmt.setString(13, orderInfo.getOrderType());
               int i=pstmt.executeUpdate();//i是数据库中受影响的行数
               pstmt.close();
               conn.close();
               return "1";
      
             }
           catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 } 
          
      }
      static public String getPhoneNum(String userName){
 Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          try{
              sql="select phoneNum from  UserLoginInfo where userName='"+userName+"'";
                     pstmt = (PreparedStatement)conn.prepareStatement(sql);
                     
                     rs = pstmt.executeQuery();
                     rs.next();
                     String returnMsg=rs.getString("phoneNum");
                     
                     pstmt.close();
                     conn.close();
                     return returnMsg;
                     
                     
              
          }
          catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 }  
                    
      }
      static public String myOrderInfoReturn(String userName){
          Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          try{
              sql="select AndroidOrderInfo.*,UserLoginInfo.phoneNum from AndroidOrderInfo,UserLoginInfo where AndroidOrderInfo.userName='"+userName+"'"+" and UserLoginInfo.userName='"+userName+"'";
              pstmt = (PreparedStatement)conn.prepareStatement(sql);
              rs = pstmt.executeQuery();
             // rs.next();
              String msgReturn="myOrderInfoReturn";
              while(rs.next()){
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderID");
                  msgReturn+="|";
                  msgReturn+=rs.getString("srcAdr");
                  msgReturn+="|";
                  msgReturn+=rs.getString("dstAdr");
                  msgReturn+="|";
                  msgReturn+=rs.getString("userName");
                  msgReturn+="|";
                  msgReturn+=rs.getString("recivUserName");
                  msgReturn+="|";
                  msgReturn+=rs.getString("createTime");
                  msgReturn+="|";
                  msgReturn+=rs.getString("deadlineTime");
                  msgReturn+="|";
                  if(!rs.getString("finishTime").isEmpty())
                    msgReturn+=rs.getString("finishTime");
                  else
                      msgReturn+="unfinished";
                  msgReturn+="|";
                  msgReturn+=rs.getString("weight");
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderState");
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderFee");
                  msgReturn+="|";
                   if(!rs.getString("noteInfo").isEmpty())
                    msgReturn+=rs.getString("noteInfo");
                  else
                      msgReturn+="No more information!";
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderType");
                  msgReturn+="|";
                  msgReturn+=rs.getString("phoneNum");
              }
              pstmt.close();
              conn.close();
              return msgReturn;
      
             }
           catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 } 
          
         
      }
      static public String onlineOrderInfoReturn(String userName){
          Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          try{
              sql="select AndroidOrderInfo.*,UserLoginInfo.phoneNum from AndroidOrderInfo,UserLoginInfo where AndroidOrderInfo.userName!='"+userName+"'"+" and UserLoginInfo.userName!='"+userName+"'and AndroidOrderInfo.userName=UserLoginInfo.userName and AndroidOrderInfo.recivUserName='' ";
              pstmt = (PreparedStatement)conn.prepareStatement(sql); 
              rs = pstmt.executeQuery();
             // rs.next();
              String msgReturn="onlineOrderInfoReturn";
              while(rs.next()){
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderID");
                  msgReturn+="|";
                  msgReturn+=rs.getString("srcAdr");
                  msgReturn+="|";
                  msgReturn+=rs.getString("dstAdr");
                  msgReturn+="|";
                  msgReturn+=rs.getString("userName");
                  msgReturn+="|";
                  msgReturn+=rs.getString("recivUserName");
                  msgReturn+="|";
                  msgReturn+=rs.getString("createTime");
                  msgReturn+="|";
                  msgReturn+=rs.getString("deadlineTime");
                  msgReturn+="|";
                  if(!rs.getString("finishTime").isEmpty())
                    msgReturn+=rs.getString("finishTime");
                  else
                      msgReturn+="unfinished";
                  msgReturn+="|";
                  msgReturn+=rs.getString("weight");
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderState");
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderFee");
                  msgReturn+="|";
                   if(!rs.getString("noteInfo").isEmpty())
                    msgReturn+=rs.getString("noteInfo");
                  else
                      msgReturn+="No more information!";
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderType");
                  msgReturn+="|";
                  msgReturn+=rs.getString("phoneNum");
              }
              pstmt.close();
              conn.close();
              return msgReturn;
      
             }
           catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 } 
          
         
      }
      static public String acOrderInfoReturn(String userName){
          Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          try{
              sql="select AndroidOrderInfo.*,UserLoginInfo.phoneNum from AndroidOrderInfo,UserLoginInfo where AndroidOrderInfo.recivUserName='"+userName+"'"+" and UserLoginInfo.userName=(select distinct AndroidOrderInfo.userName from AndroidOrderInfo where AndroidOrderInfo.userName ='"+userName+"')";
              pstmt = (PreparedStatement)conn.prepareStatement(sql);
              rs = pstmt.executeQuery();
             // rs.next();
              String msgReturn="acOrderInfoReturn";
              while(rs.next()){
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderID");
                  msgReturn+="|";
                  msgReturn+=rs.getString("srcAdr");
                  msgReturn+="|";
                  msgReturn+=rs.getString("dstAdr");
                  msgReturn+="|";
                  msgReturn+=rs.getString("userName");
                  msgReturn+="|";
                  msgReturn+=rs.getString("recivUserName");
                  msgReturn+="|";
                  msgReturn+=rs.getString("createTime");
                  msgReturn+="|";
                  msgReturn+=rs.getString("deadlineTime");
                  msgReturn+="|";
                  if(!rs.getString("finishTime").isEmpty())
                    msgReturn+=rs.getString("finishTime");
                  else
                      msgReturn+="unfinished";
                  msgReturn+="|";
                  msgReturn+=rs.getString("weight");
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderState");
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderFee");
                  msgReturn+="|";
                   if(!rs.getString("noteInfo").isEmpty())
                    msgReturn+=rs.getString("noteInfo");
                  else
                      msgReturn+="No more information!";
                  msgReturn+="|";
                  msgReturn+=rs.getString("orderType");
                  msgReturn+="|";
                  msgReturn+=rs.getString("phoneNum");
              }
              pstmt.close();
              conn.close();
              return msgReturn;
      
             }
           catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 } 
          
         
      }
      static public String myOrderInfoChange(String orderID){
          Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          try{
              System.out.println("尝试向数据库写入更改后的订单信息，返回1");
               SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String Date=df.format(new Date());
              sql="update AndroidOrderInfo set orderState='3',finishTime='"+Date+"'where orderID='"+orderID+"'";
              pstmt = (PreparedStatement) conn.prepareStatement(sql);
              int i=pstmt.executeUpdate();//i是数据库中受影响的行数
              pstmt.close();
              conn.close();
              return "1";
      
             }
           catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 } 
      }
      static public String onlineOrderInfoChange(String info){
          Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          String[]msg=info.split("\\|");
          try{
              System.out.println("尝试向数据库写入更改后的订单信息，返回1");
              sql="update AndroidOrderInfo set orderState='2',recivUserName='"+msg[0]+"' where orderID='"+msg[1]+"'";
           //   System.out.println(msg[1]+"|"+msg[2]);
              pstmt = (PreparedStatement) conn.prepareStatement(sql);
              int i=pstmt.executeUpdate();//i是数据库中受影响的行数
              pstmt.close();
              conn.close();
              return "1";
      
             }
           catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 } 
      }
      static public String acOrderInfoChange(String orderID){
          Connection conn = SqlServer.getConn();
          String sql;
          PreparedStatement pstmt;
          ResultSet rs;
          try{
              System.out.println("尝试向数据库写入更改后的订单信息，返回1");
              sql="update AndroidOrderInfo set orderState='1',recivUserName='' where orderID='"+orderID+"'";
              pstmt = (PreparedStatement) conn.prepareStatement(sql);
              int i=pstmt.executeUpdate();//i是数据库中受影响的行数
              pstmt.close();
              conn.close();
              return "1";
      
             }
           catch (SQLException ee) {
                     ee.printStackTrace();
                     //发生异常，返回-1；
                     return "-1";
                 } 
      }
          
        
    
}

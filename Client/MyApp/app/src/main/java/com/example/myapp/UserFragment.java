package com.example.myapp;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserFragment extends Fragment {
    //成员变量
    private String userName;
    //IP地址和端口号
    public static String IP_ADDRESS = "10.0.2.2";
    public static int PORT = 2346;

    //handler
    Handler handler = null;
    Handler handlerGet=null;
    Socket soc = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    String messageRecv = null;

    //界面控件
    EditText fg_et_userName;
    EditTextWithDel fg_et_phoneNum;
    EditTextWithDel fg_et_defAdr;
    Button fg_btn_submit;
    Button fg_btn_cancel;

    //构造函数
    public UserFragment(){
    }
    public void setUserName(String uN){
        userName=uN;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fg_user,container,false);
        //在这里初始化界面控件
        fg_et_userName=(EditText)view.findViewById(R.id.fg_user_et_userName);
        fg_et_phoneNum=(EditTextWithDel)view.findViewById(R.id.fg_user_et_phoneNum);
        fg_et_defAdr=(EditTextWithDel)view.findViewById(R.id.fg_user_et_defAdr);
        fg_btn_submit=(Button)view.findViewById(R.id.fg_user_btn_submit);
        fg_btn_cancel=(Button)view.findViewById(R.id.fg_user_btn_cancel);
        fg_et_userName.setText(userName);
        fg_et_userName.setEnabled(false);
        new ConnectionThreadGet("userInfoGet|"+userName).start();
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //在这里为界面控件添加监听事件
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        fg_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getContext(),"SUBMIT",Toast.LENGTH_SHORT).show();
                //new AddFragment.ConnectionThread("Hello").start();

                new ConnectionThread(getUserUIInfo()).start();


            }
        });
        fg_btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Toast.makeText(getContext(),"SUBMIT",Toast.LENGTH_SHORT).show();
                //new AddFragment.ConnectionThread("Hello").start();
                fg_et_phoneNum.setText("");
                fg_et_defAdr.setText("");

            }

            }
        );
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                 Bundle b = msg.getData();  //获取消息中的Bundle对象
                 String str = b.getString("userInfo");  //获取键为data的字符串的值
                // info.append(str);
                userInfoChangeCheck(Integer.parseInt(str));
                fg_et_phoneNum.setText("1111111");

            }
        };
        handlerGet = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();  //获取消息中的Bundle对象
                String str = b.getString("userInfo");  //获取键为data的字符串的值
                String[]userInfo=str.split("\\|");

                   // Toast.makeText(getContext(),userInfo[1],Toast.LENGTH_SHORT).show();
                    fg_et_phoneNum.setText(userInfo[1]);
                    fg_et_defAdr.setText(userInfo[2]);



            }
        };

    }
    //新建一个子线程，实现socket通信
    class ConnectionThread extends Thread {//此子线程发送消息后会等待服务器返回信息
        String message = null;//将要发送给服务器的消息

        public ConnectionThread(String msg) {//创建子进程时，会通过此构造函数将信息传进来
            message = msg;//
            // Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

        }

        @Override
        public void run() {
            if (soc == null) {
                try {
                    //Log.d("socket","new socket");
                    soc = new Socket(IP_ADDRESS, PORT);
                    //获取socket的输入输出流
                    dis = new DataInputStream(soc.getInputStream());
                    dos = new DataOutputStream(soc.getOutputStream());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                dos.writeUTF(message);//将消息发送给服务器
                dos.flush();//将内存未写完的内容输出，清空内存缓存区
                messageRecv = dis.readUTF();//等待服务器返回消息，直到收到数据为止会阻塞，停止向下运行。

                //用Handler向主进程（UI进程）发送消息以更新界面
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putString("userInfo", messageRecv);//将从服务器收到的消息写进去。
                msg.setData(b);
                handler.sendMessage(msg);//通过handler向主线程发送消息。

                //可能要对socket和dos和dis进行关闭处理，若要处理，代码写在下面

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    class ConnectionThreadGet extends Thread {//此子线程发送消息后会等待服务器返回信息
        String message = null;//将要发送给服务器的消息

        public ConnectionThreadGet(String msg) {//创建子进程时，会通过此构造函数将信息传进来
            message = msg;//
            // Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

        }

        @Override
        public void run() {
            if (soc == null) {
                try {
                    //Log.d("socket","new socket");
                    soc = new Socket(IP_ADDRESS, PORT);
                    //获取socket的输入输出流
                    dis = new DataInputStream(soc.getInputStream());
                    dos = new DataOutputStream(soc.getOutputStream());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                dos.writeUTF(message);//将消息发送给服务器
                dos.flush();//将内存未写完的内容输出，清空内存缓存区
                messageRecv = dis.readUTF();//等待服务器返回消息，直到收到数据为止会阻塞，停止向下运行。

                //用Handler向主进程（UI进程）发送消息以更新界面
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putString("userInfo", messageRecv);//将从服务器收到的消息写进去。
                msg.setData(b);
                handlerGet.sendMessage(msg);//通过handler向主线程发送消息。

                //可能要对socket和dos和dis进行关闭处理，若要处理，代码写在下面

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private String getUserUIInfo(){
        String msg="userInfoChange|";
        msg+=fg_et_userName.getText().toString();
        msg+="|";
        msg+=fg_et_phoneNum.getText().toString();
        msg+="|";
        msg+=fg_et_defAdr.getText().toString();
        return msg;
    }
    private void userInfoChangeCheck(int checkState){

        if(checkState==1){
            Toast.makeText(getContext(),"Submit successfully",Toast.LENGTH_SHORT).show();
        }
        if (checkState==0){
            Toast.makeText(getContext(),"Failure to submit",Toast.LENGTH_SHORT).show();
            fg_btn_cancel.performClick();
        }
    }

}

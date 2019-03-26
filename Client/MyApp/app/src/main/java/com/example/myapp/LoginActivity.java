package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {
    //界面控件
    Button btnLogin;
    Button btnRegister;
    EditTextWithDel txtUsername;
    EditTextWithDel txtPassword;

    //保存状态信息
    int loginCheckState=0; //0为未通过，1为通过

    //IP地址和端口号
    public static String IP_ADDRESS = "10.0.2.2";
    public static int PORT = 2346;

    //handler
    Handler handler = null;
    Socket soc = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    String messageRecv = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //实例化界面控件
        btnLogin=(Button)findViewById(R.id.btn_login);
        btnRegister=(Button)findViewById(R.id.btn_register);
        txtUsername=(EditTextWithDel)findViewById(R.id.usernameLoginEdit);
        txtPassword=(EditTextWithDel)findViewById(R.id.passwordLoginEdit);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {//初始化handler用来处理消息


                //调用这个函数
                super.handleMessage(msg);
                // Bundle b = msg.getData();  //获取消息中的Bundle对象
                // String str = b.getString("data");  //获取键为data的字符串的值
                // info.append(str);
                //在这里添加一些操作
                Bundle b = msg.getData();  //获取消息中的Bundle对象
                String str = b.getString("loginCheck");  //获取键为data的字符串的值
                loginCheckState=Integer.parseInt(str);//
                loginCheck();
            }
        };



        //实例化局部变量

        //为界面控件添加匿名响应事件
        btnLogin.setOnClickListener(new View.OnClickListener() {//登录按钮
            @Override
            public void onClick(View v) {
                //做登录信息检查
                if(!editTextCheck())//如果输入框为空，就直接返回，不连接服务器。
                    return;

                new ConnectionThread(getLoginUIInfo()).start();

            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {//注册按钮
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
                //finish();
            }
        });



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
                b.putString("loginCheck", messageRecv);//将从服务器收到的消息写进去。
                msg.setData(b);
                handler.sendMessage(msg);//通过handler向主线程发送消息。

                //可能要对socket和dos和dis进行关闭处理，若要处理，代码写在下面

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    private String getLoginUIInfo(){
        String msg="loginCheck|";
        msg+=txtUsername.getText().toString();
        msg+="|";
        msg+=txtPassword.getText().toString();
        return msg;
    }
    private void loginCheck(){


        if(loginCheckState==1) {//登录检查通过
            //启动应用主功能界面，并传递一些数据
            Intent FuncIntent = new Intent(LoginActivity.this, FuncActivity.class);
            FuncIntent.putExtra("userName",txtUsername.getText().toString());
            startActivity(FuncIntent);
            //finish();
        }
        else if (loginCheckState==0){//登录检查未通过
            Toast.makeText(LoginActivity.this,"ERROR:Incorrect username or password",Toast.LENGTH_SHORT).show();
            txtPassword.setText("");

        }
        else if(loginCheckState==-1){//意外情况发生
            Toast.makeText(LoginActivity.this,"ERROR:UNKNOWN",Toast.LENGTH_SHORT).show();
            txtPassword.setText("");
        }

    }
    private boolean editTextCheck(){//检查输入框，不能为空
        if(txtUsername.getText().toString().isEmpty()||txtPassword.getText().toString().isEmpty()){
            Toast.makeText(LoginActivity.this,"ERROR:User name and password cannot be empty",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }
}

package com.example.myapp;

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

public class RegisterActivity extends AppCompatActivity {

    //UI控件
    Button btnSubmitReg;
    Button btnCancelReg;
    EditTextWithDel txtUsernameReg;
    EditTextWithDel txtPasswordReg;
    EditTextWithDel txtPasswordAgainReg;
    //保存状态信息
    int regCheckState = 0; //0为未通过，1为通过

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
        setContentView(R.layout.activity_register);
        //实例化界面控件
        btnSubmitReg = (Button) findViewById(R.id.btn_submit);
        btnCancelReg = (Button) findViewById(R.id.btn_cancel);
        txtUsernameReg = (EditTextWithDel) findViewById(R.id.usernameRegEdit);
        txtPasswordReg = (EditTextWithDel) findViewById(R.id.passwordRegEdit);
        txtPasswordAgainReg = (EditTextWithDel) findViewById(R.id.passwordRegEditAgain);

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
                String str = b.getString("regCheck");  //获取键为data的字符串的值
                regCheckState = Integer.parseInt(str);//
                regCheck();
            }
        };
        //实例化局部变量

        //为界面控件添加匿名响应事件
        btnSubmitReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextCheck())//如果输入框为空或两次密码不相同，就直接返回，不连接服务器。
                    return;
                new ConnectionThread(getRegUIInfo()).start();

            }

        });
        btnCancelReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginIntent);
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
                b.putString("regCheck", messageRecv);//将从服务器收到的消息写进去。
                msg.setData(b);
                handler.sendMessage(msg);//通过handler向主线程发送消息。

                //可能要对socket和dos和dis进行关闭处理，若要处理，代码写在下面
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    private String getRegUIInfo(){
        String msg="regCheck|";
        msg+=txtUsernameReg.getText().toString();
        msg+="|";
        msg+=txtPasswordReg.getText().toString();
        return msg;
    }
    private void regCheck(){


        if(regCheckState==1) {//注册检查通过
            //启动应用主功能界面，并传递一些数据
            Intent FuncIntent = new Intent(RegisterActivity.this, FuncActivity.class);
            FuncIntent.putExtra("userName",txtUsernameReg.getText().toString());
            startActivity(FuncIntent);
            //finish();
        }
        else if (regCheckState==0){//注册检查未通过
            Toast.makeText(RegisterActivity.this,"ERROR:Username already exists",Toast.LENGTH_SHORT).show();
            txtPasswordReg.setText("");
            txtPasswordAgainReg.setText("");

        }
        else if(regCheckState==-1){//意外情况发生
            Toast.makeText(RegisterActivity.this,"ERROR:UNKNOWN",Toast.LENGTH_SHORT).show();
            txtPasswordReg.setText("");
            txtPasswordAgainReg.setText("");
        }

    }
    private boolean editTextCheck(){//检查输入框，不能为空
        if(txtUsernameReg.getText().toString().isEmpty()||txtPasswordReg.getText().toString().isEmpty()||txtPasswordAgainReg.getText().toString().isEmpty()){
            Toast.makeText(RegisterActivity.this,"ERROR:User name and password cannot be empty",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!txtPasswordReg.getText().toString().equals(txtPasswordAgainReg.getText().toString())){
            Toast.makeText(RegisterActivity.this,"ERROR:Two password entries are different",Toast.LENGTH_SHORT).show();
            txtPasswordReg.setText("");
            txtPasswordAgainReg.setText("");
            return false;
        }
        else
            return true;

    }
}

package com.example.myapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;

public class AddFragment extends Fragment {
    //成员变量
    private String userName;
    private String orderType;
    private String weight;
    private String timeLimit;
    //界面控件
    Button btn_add_submit;
    Button btn_add_cancel;
    RadioGroup rbg;
    EditTextWithDel fg_et_srcAdr;
    EditTextWithDel fg_et_phoneNum;
    EditTextWithDel fg_et_dstAdr;
    EditTextWithDel fg_et_noteInfo;
    EditTextWithDel fg_et_orderFee;
    Spinner fg_sp_timeLimit;
    Spinner fg_sp_weight;

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





    //构造函数
    public AddFragment() {

    }
    public void setUserName(String uN){
        userName=uN;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_add, container, false);

        //在这里初始化界面控件
        btn_add_submit = (Button) view.findViewById(R.id.fg_add_btn_submit);
        btn_add_cancel=(Button)view.findViewById(R.id.fg_add_btn_cancel);
        rbg=(RadioGroup)view.findViewById(R.id.fg_add_rbg);
        /*RadioButton btn_get=(RadioButton)view.findViewById(R.id.fg_add_rbtn_get);
        btn_get.performClick();*/
        fg_et_srcAdr=(EditTextWithDel)view.findViewById(R.id.fg_add_et_srcAdr);
        fg_et_phoneNum=(EditTextWithDel)view.findViewById(R.id.fg_add_et_phoneNum);
        fg_et_dstAdr=(EditTextWithDel)view.findViewById(R.id.fg_add_et_dstAdr);
        fg_et_noteInfo=(EditTextWithDel)view.findViewById(R.id.fg_add_et_noteInfo);
        fg_et_orderFee=(EditTextWithDel)view.findViewById(R.id.fg_add_et_orderFee);
        fg_sp_weight=(Spinner)view.findViewById(R.id.fg_add_sp_weight);
        fg_sp_timeLimit=(Spinner)view.findViewById(R.id.fg_add_sp_timeLimit);


        new ConnectionThreadGet("userInfoGet|"+userName).start();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //在这里为界面控件添加监听事件
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        btn_add_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getContext(),"SUBMIT",Toast.LENGTH_SHORT).show();
                if(fg_et_phoneNum.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Error:Telephone number should not be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(fg_et_srcAdr.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Error:User address should not be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(fg_et_dstAdr.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Error:Destination address should not be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(fg_et_orderFee.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Error:Order fee should not be empty",Toast.LENGTH_SHORT).show();
                    return;

                }
                else if(orderType==null){
                    Toast.makeText(getContext(),"Error:Order type should not be empty,please choose get or send!",Toast.LENGTH_SHORT).show();
                    return;
                }

                String msgSend="orderAdd";
                msgSend+="|";
                msgSend+=fg_et_srcAdr.getText().toString();
                msgSend+="|";
                msgSend+=fg_et_dstAdr.getText().toString();
                msgSend+="|";
                msgSend+=userName;
                msgSend+="|";
                msgSend+=timeLimit;
                msgSend+="|";
                msgSend+=weight;
                msgSend+="|";
                msgSend+=fg_et_orderFee.getText().toString();
                msgSend+="|";
                msgSend+=fg_et_noteInfo.getText().toString();
                msgSend+="|";
                msgSend+=orderType;


                //new ConnectionThread("orderAdd|srcAdr|dstAdr|admin|5|0.5kg|3|this is note|1").start();
                new ConnectionThread(msgSend).start();
            }
        });
        btn_add_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });
        rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rbtn=(RadioButton)getView().findViewById(checkedId);
                if(rbtn.getText().toString().equals("Get something")){
                    orderType="1";
                    //Toast.makeText(getContext(),"1",Toast.LENGTH_SHORT).show();
                }
                else if(rbtn.getText().toString().equals("Send something")){
                    orderType="2";
                    //Toast.makeText(getContext(),"2",Toast.LENGTH_SHORT).show();
                }

            }
        });
        fg_sp_weight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weight = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getContext(),weight,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fg_sp_timeLimit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String []str=parent.getItemAtPosition(position).toString().split(" ");
                timeLimit=str[1];
               // Toast.makeText(getContext(),timeLimit,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();  //获取消息中的Bundle对象
                String str = b.getString("data");  //获取键为data的字符串的值
                orderAddCheck(str);
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
                fg_et_srcAdr.setText(userInfo[2]);
                fg_et_phoneNum.setText(userInfo[1]);



            }
        };
    }

    //新建一个子线程，实现socket通信
    class ConnectionThread extends Thread {
        String message = null;

        public ConnectionThread(String msg) {
            message = msg;
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
                dos.writeUTF(message);
                dos.flush();
                messageRecv = dis.readUTF();//如果没有收到数据，会阻塞
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putString("data", messageRecv);
                msg.setData(b);
                handler.sendMessage(msg);
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
    public void orderAddCheck(String state){
        if(state.equals("1")){
            Toast.makeText(getContext(),"Successful order creation!",Toast.LENGTH_SHORT).show();
            clearAll();
        }
        else {
            Toast.makeText(getContext(),"Error:Unknown",Toast.LENGTH_SHORT).show();
            clearAll();
        }

    }
    public void clearAll(){
        this.fg_et_dstAdr.setText("");
        this.fg_et_noteInfo.setText("");
        this.fg_et_orderFee.setText("");
        this.fg_sp_timeLimit.setSelection(0);
        this.fg_sp_weight.setSelection(0);

    }
}




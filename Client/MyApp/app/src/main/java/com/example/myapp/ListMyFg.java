package com.example.myapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.myapp.MyRecyclerViewAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListMyFg extends Fragment {
    //成员变量
    public String userName;
    private RecyclerView rvRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private List<OrderInfo> list;

    //界面控件
    RecyclerView rvMy;
    //Button btnCR;
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
    int getState;

    public void setUserName(String uN){
        this.userName=uN;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_list_my, container, false);
        //在这里初始化成员变量

        list = new ArrayList<>();
        /*list.add(new OrderInfo("10","srcAdr","dstAdr","admin","admin123","CTime","DTime","FTime","1~2kg","1","5","note1","1"));
        list.add(new OrderInfo("12","srcAdr","dstAdr","admin","admin123","CTime","DTime","FTime","1~2kg","1","5","note2","1"));
        list.add(new OrderInfo("13","srcAdr","dstAdr","admin","admin123","CTime","DTime","FTime","1~2kg","1","5","note2","1"));
        list.add(new OrderInfo("14 ","srcAdr","dstAdr","admin","admin123","CTime","DTime","FTime","1~2kg","1","5","note2","1"));*/
        //在这里初始化界面控件
        rvMy=(RecyclerView)view.findViewById(R.id.rv_my);
        rvMy.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));//控制布局为LinearLayout或者是GridView或者是瀑布流布局
        adapter = new MyRecyclerViewAdapter(list,getContext());
        rvMy.setAdapter(adapter);
        adapter.setOnItemClickListener(MyItemClickListener);
        //btnCR=(Button)view.findViewById(R.id.item_my_btn_confirm) ;
        new ConnectionThreadGet("myOrderInfoGet|"+userName).start();
        return view;
    }
    public void setList(String msgReciv){
        String []s=msgReciv.split("\\|");
        if(!s[0].equals("myOrderInfoReturn"))
            return;
        for(int i=1;i<s.length;i+=14){
            OrderInfo orderItem=new OrderInfo(s[i],s[i+1],s[i+2],s[i+3],s[i+4],s[i+5],s[i+6],s[i+7],s[i+8],s[i+9],s[i+10],s[i+11],s[i+12],s[i+13]);

            adapter.addData(orderItem);
        }
    }
    public void addItem(int position){
       // list.add()
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //在这里为界面控件添加监听事件
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();  //获取消息中的Bundle对象
                String str = b.getString("userInfo");  //获取键为data的字符串的值
                // info.append(str);
                //userInfoChangeCheck(Integer.parseInt(str));
                //fg_et_phoneNum.setText("1111111");
                if(str.equals("1")){
                    Toast.makeText(getContext(),"Confirm receipt successfully",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getContext(),"Error:Unknown",Toast.LENGTH_SHORT).show();
                }

            }
        };
        handlerGet = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();  //获取消息中的Bundle对象
                String str = b.getString("userInfo");  //获取键为data的字符串的值
                // String[]orderInfo=str.split("\\|");
                setList(str);
                //getState=1;

                // Toast.makeText(getContext(),userInfo[1],Toast.LENGTH_SHORT).show();
                // fg_et_phoneNum.setText(userInfo[1]);
                // fg_et_defAdr.setText(userInfo[2]);



            }
        };


    }
    /**
     * item＋item里的控件点击监听事件
     */
    private MyRecyclerViewAdapter.OnItemClickListener MyItemClickListener = new MyRecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, MyRecyclerViewAdapter.ViewName viewName, String orderInfo) {
            //viewName可区分item及item内部控件
            switch (v.getId()){
                case R.id.item_my_btn_confirm:
                   // Toast.makeText(getContext(),"该Item包含的信息是"+orderInfo,Toast.LENGTH_SHORT).show();
                    String []str= orderInfo.split("\\|");
                    //adapter.removeData(Integer.valueOf(str[1]));
                    if( list.get(Integer.valueOf(str[1])).getOrderState().equals("3")){
                        Toast.makeText(getContext(),"Already confirm!",Toast.LENGTH_SHORT).show();
                        return;

                    }
                    new ConnectionThread("myOrderInfoChange|"+str[0]).start();
                    list.get(Integer.valueOf(str[1])).setOrderState(2);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String Date=df.format(new Date());
                    list.get(Integer.valueOf(str[1])).setFinishTime(Date);
                    adapter.notifyDataSetChanged();
                    //list.clear();
                    //new ConnectionThreadGet("myOrderInfoGet|"+userName).start();
                    break;
                /*case R.id.btn_refuse:
                    Toast.makeText(MainActivity.this,"你点击了拒绝按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;*/
                default:
                    /*Toast.makeText(MainActivity.this,"你点击了item按钮"+(position+1),Toast.LENGTH_SHORT).show();*/
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };
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
    public String getStateStr(String state){
        if(state.equals("1")){
            return "Waiting for reception";
        }
        else if(state.equals("2")){
            return "Be receipted by someone";
        }
        else if(state.equals("3")){
            return "Finished";
        }
        else return "Unknown";
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


}

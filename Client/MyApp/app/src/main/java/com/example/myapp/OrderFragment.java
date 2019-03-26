package com.example.myapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class OrderFragment extends Fragment implements View.OnClickListener{
    //成员变量
    String userName;
    //界面控件
    private TextView text_myOrder;
    private TextView text_onlineOrder;
    private TextView text_acOrder;
    private FrameLayout ly_content;

    //Fragment object
    private ListMyFg fg_my;
    private ListOnlineFg fg_online;
    private ListAcceptedFg fg_ac;
    private FragmentManager fManager;

    //构造函数
    public OrderFragment(){

    }
    public void setUserName(String userName){
        this.userName=userName;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fg_order,container,false);
        //获得Fragment管理对象
        fManager=getFragmentManager();
        //在这里初始化界面控件

        text_myOrder = (TextView)view.findViewById(R.id.fg_txt_tap_my);
        text_onlineOrder = (TextView) view.findViewById(R.id.fg_txt_tap_online);
        text_acOrder = (TextView) view.findViewById(R.id.fg_txt_tap_accept);
        ly_content = (FrameLayout)view. findViewById(R.id.fg_order_ly_cotent);

        //text_myOrder.performClick();

        return view;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //在这里为界面控件添加监听事件
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        text_myOrder.setOnClickListener(this);
        text_onlineOrder.setOnClickListener(this);
        text_acOrder.setOnClickListener(this);

    }


    private void setSelected(){//重置所有文本的选中状态
        text_acOrder.setSelected(false);
        text_onlineOrder.setSelected(false);
        text_myOrder.setSelected(false);

    }
    private void hideAllFragment(FragmentTransaction fragmentTransaction){ //隐藏所有Fragment
        if(fg_my !=null)fragmentTransaction.hide(fg_my);
        if(fg_online!=null)fragmentTransaction.hide(fg_online);
        if(fg_ac !=null)fragmentTransaction.hide(fg_ac);

    }

    @Override
    public void onClick(View v){//为当前活动添加点击回调事件
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (v.getId()){
            case R.id.fg_txt_tap_my:
                setSelected();
                text_myOrder.setSelected(true);
                if(true){
                    fg_my =new ListMyFg();
                    fg_my.setUserName(userName);
                    fTransaction.add(R.id.fg_order_ly_cotent, fg_my);

                }
                else{
                    fTransaction.show(fg_my);

                    //Toast.makeText(this,"展示order",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fg_txt_tap_online:
                setSelected();
                text_onlineOrder.setSelected(true);
                if(true){
                    fg_online=new ListOnlineFg();
                    fg_online.setUserName(userName);
                   // fg_add.setUserName(userName);
                    fTransaction.add(R.id.fg_order_ly_cotent,fg_online);

                }
                else{
                    fTransaction.show(fg_online);

                }
                break;
            case R.id.fg_txt_tap_accept:
                setSelected();
                text_acOrder.setSelected(true);
                if(true){
                    fg_ac =new ListAcceptedFg();
                    fg_ac.setRecivUserName(userName);
                    fTransaction.add(R.id.fg_order_ly_cotent, fg_ac);

                }
                else{
                    fTransaction.show(fg_ac);

                }
                break;
        }
        fTransaction.commit();

    }
}
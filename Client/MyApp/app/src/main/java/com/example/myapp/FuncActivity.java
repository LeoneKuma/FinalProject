package com.example.myapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FuncActivity extends AppCompatActivity implements View.OnClickListener{
    //界面控件
    private TextView text_order;
    private TextView text_add;
    private TextView text_user;
    private FrameLayout ly_content;

    //Fragment object
    private OrderFragment fg_order;
    private AddFragment fg_add;
    private UserFragment fg_user;
    private FragmentManager fManager;

    //其他成员变量
    String userName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉顶部标题
        getSupportActionBar().hide();
        setContentView(R.layout.activity_func);
        //获得Fragment管理对象
        fManager=getSupportFragmentManager();
        //实例化界面控件
        bindViews();
        //实例化其他成员变量
        userName=getIntent().getStringExtra("userName");
        Toast.makeText(this, "Welcome! "+userName, Toast.LENGTH_SHORT).show();
        //添加响应事件
        addListener();
        //模拟一次点击，默认跳转到Order;
        text_order.performClick();





    }
    private void bindViews() {//绑定界面控件
        text_order = (TextView) findViewById(R.id.tap_order);
        text_add = (TextView) findViewById(R.id.tap_add);
        text_user = (TextView) findViewById(R.id.tap_user);
        ly_content = (FrameLayout) findViewById(R.id.ly_content);
        //设定UserFragment的userName编辑框内的内容，并设定为不可编辑（不可选中）

    }
    private void addListener(){ //添加响应事件
        text_order.setOnClickListener(this);
        text_add.setOnClickListener(this);
        text_user.setOnClickListener(this);

    }
    private void setSelected(){//重置所有文本的选中状态
        text_user.setSelected(false);
        text_add.setSelected(false);
        text_order.setSelected(false);

    }
    private void hideAllFragment(FragmentTransaction fragmentTransaction){ //隐藏所有Fragment
        if(fg_order!=null)fragmentTransaction.hide(fg_order);
        if(fg_add!=null)fragmentTransaction.hide(fg_add);
        if(fg_user!=null)fragmentTransaction.hide(fg_user);

    }

    @Override
    public void onClick(View v){//为当前活动添加点击回调事件
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (v.getId()){
            case R.id.tap_order:
                setSelected();
                text_order.setSelected(true);
                if(fg_order==null){
                    fg_order=new OrderFragment();
                    fg_order.setUserName(userName);
                    fTransaction.add(R.id.ly_content,fg_order);

                }
                else{
                    fTransaction.show(fg_order);

                    //Toast.makeText(this,"展示order",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tap_add:
                setSelected();
                text_add.setSelected(true);
                if(fg_add==null){
                    fg_add=new AddFragment();
                    fg_add.setUserName(userName);
                    fTransaction.add(R.id.ly_content,fg_add);

                }
                else{
                    fTransaction.show(fg_add);

                }
                break;
            case R.id.tap_user:
                setSelected();
                text_user.setSelected(true);
                if(fg_user==null){
                    fg_user=new UserFragment();
                    fg_user.setUserName(userName);
                    fTransaction.add(R.id.ly_content,fg_user);

                }
                else{
                    fTransaction.show(fg_user);

                }
                break;
        }
        fTransaction.commit();

    }

}

package com.example.myapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class OnlineRecyclerViewAdapter extends RecyclerView.Adapter<OnlineRecyclerViewAdapter.OnlineViewHolder>implements View.OnClickListener {

    private List<OrderInfo> list;//数据源
    private Context context; //上下文

    public OnlineRecyclerViewAdapter(List<OrderInfo>list,Context context){
        this.list=list;
        this.context=context;

    }

    @Override
    public OnlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_online,parent,false);
        return new OnlineViewHolder(view);

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
    //绑定
    @Override
    public void onBindViewHolder(OnlineRecyclerViewAdapter.OnlineViewHolder holder, int position) {
        //在向列表中添加一个item时，会执行下面代码设置item的内容。
        OrderInfo orderInfo=list.get(position);//获取一个订单信息
        // Log.d("MYTEST",orderInfo.getOrderID());
        holder.tvOrderID.setText("Order ID: "+orderInfo.getOrderID());
        holder.tvWeight.setText(" Weight: "+orderInfo.getWeight());
        holder.tvCTime.setText("Create time: "+orderInfo.getCreateTime());
        holder.tvFTime.setText("Finish time: "+orderInfo.getFinishTime());
        holder.tvSrcAdr.setText("User address: "+orderInfo.getSrcAdr());
        holder.tvDstAdr.setText("Destination address: "+orderInfo.getDstAdr());
        holder.tvOrderFee.setText("Order fee: "+orderInfo.getOrderFee());
        holder.tvNote.setText("Note:"+orderInfo.getNoteInfo());

        holder.btnGet.setTag(orderInfo.getOrderID()+"|"+String.valueOf(position));//加入这个item时，为这个按钮添加备注信息：这个item的对应ID以及它在列表中的位置。




    }
    //有多少个item？
    @Override
    public int getItemCount() {
        return list.size();
    }
    public void removeData(int position){
        list.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    public void addData(OrderInfo orderInfo){
        list.add(orderInfo);
        notifyDataSetChanged();
    }


    //创建MyViewHolder继承RecyclerView.ViewHolder
    public class OnlineViewHolder extends RecyclerView.ViewHolder{
        //一个列表项所包含的控件
        private Button btnGet;
        private TextView tvOrderID,tvWeight,tvCTime,tvFTime, tvSrcAdr,tvDstAdr,tvOrderFee,tvNote;
        public OnlineViewHolder(View itemView) {
            super(itemView);

            //在这里初始化item控件
            tvOrderID=itemView.findViewById(R.id.item_online_orderID);
            tvWeight=itemView.findViewById(R.id.item_online_weight);
            tvCTime=itemView.findViewById(R.id.item_online_cTime);
            tvFTime=itemView.findViewById(R.id.item_online_dTime);
            tvSrcAdr =itemView.findViewById(R.id.item_online_srcAdr);
            tvDstAdr=itemView.findViewById(R.id.item_online_dstAdr);
            tvOrderFee=itemView.findViewById(R.id.item_online_orderFee);
            btnGet =itemView.findViewById(R.id.item_online_btn_get);
            tvNote=itemView.findViewById(R.id.item_online_note);

            //在这里为item控件添加响应事件
            btnGet.setOnClickListener(OnlineRecyclerViewAdapter.this);


        }

    }
    //item里面有多个控件可以点击（item+item内部控件）
    public enum ViewName {
        ITEM,
        PRACTISE
    } //自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener  {
        void onItemClick(View v, OnlineRecyclerViewAdapter.ViewName viewName, String itemInfo);
        void onItemLongClick(View v);
    }

    private OnlineRecyclerViewAdapter.OnItemClickListener mOnItemClickListener;//声明自定义的接口

    //定义方法并传给外面的使用者
    public void setOnItemClickListener(OnlineRecyclerViewAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener  = listener;
    }

    @Override
    public void onClick(View v) {
        String itemInfo = (String) v.getTag();      //getTag()获取数据
        if (mOnItemClickListener != null) {
            switch (v.getId()){
                case R.id.rv_online   :
                    mOnItemClickListener.onItemClick(v, OnlineRecyclerViewAdapter.ViewName.PRACTISE, itemInfo);
                    break;
                default:
                    mOnItemClickListener.onItemClick(v, OnlineRecyclerViewAdapter.ViewName.ITEM, itemInfo);
                    break;
            }
        }
    }
}

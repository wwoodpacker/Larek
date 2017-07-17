package org.firebirdsql.larek;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by nazar.humeniuk on 6/24/17.
 */

public class OrderAdapter extends BaseAdapter {
    private ArrayList<OrderItem> orderItems= new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public FragmentManager fragmentManager;
    public boolean isOrder;
    String name,occupation;
    public OrderAdapter(Context context,boolean isOrder){
        mContext=context;
        this.isOrder=isOrder;
    }
    public void add(OrderItem orderItem){
        orderItems.add(orderItem);
    }
    public void upDatelist(ArrayList<OrderItem> orderItems){
        this.orderItems=orderItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public OrderItem getItem(int position) {
        return orderItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater lInflater = (LayoutInflater)mContext.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE);
        convertView= lInflater.inflate(
                R.layout.order_item,null);
        final TextView order_item= (TextView)convertView.findViewById(R.id.order_item_text);
        ImageView btn_delete=(ImageView)convertView.findViewById(R.id.delete);
        if (isOrder){
            btn_delete.setVisibility(View.INVISIBLE);
        }else{
            btn_delete.setVisibility(View.VISIBLE);
        }
        order_item.setText(orderItems.get(position).getOrderItemName()+" "
                +String.valueOf(orderItems.get(position).getSi_count())+" шт "
                +String.valueOf(orderItems.get(position).getIi_price())+"$ "
                +String.valueOf(orderItems.get(position).getSi_total())+"$");

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_order.totalPrice-=orderItems.get(position).getSi_total();
                Fragment_order.showTotalPrice(Fragment_order.totalPrice);
                orderItems.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

}


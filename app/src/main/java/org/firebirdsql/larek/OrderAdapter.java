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
    private ArrayList<String> orderItems= new ArrayList<String>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public FragmentManager fragmentManager;
    public boolean isFromMenu;
    String name,occupation;
    public OrderAdapter(Context context){
        mContext=context;
    }
    public void add(String orderItem){
        orderItems.add(orderItem);
    }
    public void upDatelist(ArrayList<String> orderItems){
        this.orderItems=orderItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public Object getItem(int position) {
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
        order_item.setText(orderItems.get(position));

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment_order.totalPrice-=Double.valueOf(orderItems.get(position).substring(orderItems.get(position).indexOf("$")+2,orderItems.get(position).length()-2));
                Fragment_order.showTotalPrice(Fragment_order.totalPrice);
                orderItems.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

}


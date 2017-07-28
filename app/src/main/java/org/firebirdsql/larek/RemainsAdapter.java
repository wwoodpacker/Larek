package org.firebirdsql.larek;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nazar.humeniuk on 7/28/17.
 */

public class RemainsAdapter extends BaseAdapter {
    private ArrayList<Remains> remainsArrayList=new ArrayList<Remains>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public FragmentManager fragmentManager;
    String name,occupation;
    public RemainsAdapter(Context context,FragmentManager fm){
        mContext=context;
        fragmentManager=fm;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void add(Remains remains){
        remainsArrayList.add(remains);
    }

    @Override
    public int getCount() {
        return remainsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return remainsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView = (LinearLayout) mLayoutInflater.inflate(
                R.layout.remains_item, parent, false);

        TextView remain_name= (TextView)itemView.findViewById(R.id.textView1);
        TextView remain_prev= (TextView)itemView.findViewById(R.id.textView2);
        TextView remain_now= (TextView)itemView.findViewById(R.id.textView3);
        TextView remain_price= (TextView)itemView.findViewById(R.id.textView4);
        final Remains remains= this.remainsArrayList.get(position);
        remain_name.setText(remains.getName());
        remain_now.setText(String.valueOf(remains.getNow_ost()));
        remain_prev.setText(String.valueOf(remains.getPrev_ost()));
        remain_price.setText(String.valueOf(remains.getPrice()));
        return itemView;
    }
}

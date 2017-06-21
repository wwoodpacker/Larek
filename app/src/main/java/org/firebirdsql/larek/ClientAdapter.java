package org.firebirdsql.larek;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by nazar.humeniuk on 14.06.17.
 */

public class ClientAdapter extends BaseAdapter {
    private ArrayList<Client> clientsList=new ArrayList<Client>();
    private ArrayList<Client> clientsListSearch=new ArrayList<Client>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public FragmentManager fragmentManager;
    public boolean isFromMenu;
    String name,occupation;
    public ClientAdapter(Context context,FragmentManager fm,boolean isFromMenu){
        mContext=context;
        fragmentManager=fm;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isFromMenu=isFromMenu;
    }
    public void add(Client client){
        clientsList.add(client);
        clientsListSearch.add(client);
    }
    public void upDatelist(ArrayList<Client> clientsList){
        this.clientsList=clientsList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return clientsList.size();
    }

    @Override
    public Object getItem(int position) {
        return clientsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout itemView = (RelativeLayout) mLayoutInflater.inflate(
                R.layout.client_item, parent, false);

        TextView client_name= (TextView)itemView.findViewById(R.id.client_name);
        TextView client_occupation= (TextView)itemView.findViewById(R.id.client_occupation);
        ImageView btn_buy=(ImageView)itemView.findViewById(R.id.buy);
        ImageView btn_orders=(ImageView)itemView.findViewById(R.id.orders);
        if (isFromMenu){
            btn_buy.setVisibility(View.VISIBLE);
            btn_orders.setVisibility(View.VISIBLE);
        }else{
            btn_buy.setVisibility(View.INVISIBLE);
            btn_orders.setVisibility(View.INVISIBLE);
        }
        final Client mClient= this.clientsList.get(position);
        final String name=mClient.getSurname()+" "+mClient.getName()+" "+mClient.getPatronimic();
        final String occupation=mClient.getOccupation();
        if (mClient.getStatus()==0){
            client_name.setTextColor(mContext.getResources().getColor(R.color.red));
            client_occupation.setTextColor(mContext.getResources().getColor(R.color.red));
            client_name.setText(name);
            client_occupation.setText(occupation);
        }else{
            client_name.setTextColor(mContext.getResources().getColor(R.color.colorText));
            client_occupation.setTextColor(mContext.getResources().getColor(R.color.colorText));
            client_name.setText(name);
            client_occupation.setText(occupation);
        }
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_order fragmentOrder = Fragment_order.newInstance(true,name,occupation);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentOrder);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        if (!isFromMenu) {
            client_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment_order fragmentOrder = Fragment_order.newInstance(true,name,occupation);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frgmCont, fragmentOrder);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
        return itemView;
    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        clientsList.clear();
        if (charText.length() == 0) {
            clientsList.addAll(clientsListSearch);
        }
        else
        {
            for (Client wp : clientsListSearch)
            {
                if (wp.getSurname().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    clientsList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    //sort
    public void sorting(){
        Collator russianCollator = Collator.getInstance(new Locale("ru", "RU"));

        Client tmp;
        for (int i = 0; i < clientsList.size(); i++) {
            for (int j = i + 1; j < clientsList.size(); j++)
                if (russianCollator.compare(clientsList.get(i).getSurname(), clientsList.get(j).getSurname()) > 0) {
                    tmp = clientsList.get(i);
                    clientsList.set(i,clientsList.get(j));
                    clientsList.set(j,tmp);
                }
        }
        notifyDataSetChanged();
    }
}

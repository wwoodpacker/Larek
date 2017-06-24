package org.firebirdsql.larek;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nazar.humeniuk on 17.06.17.
 */

public class Fragment_order extends Fragment implements AsyncResponse{
    public Button btnAddClient,btnCancel,btnOplata,btn_back;
    public ProgressBar progressBar;
    private List<View> allBtnSales;
    public LinearLayout layoutSalesItems;
    public  LinearLayout row=null;
    public boolean isClient;
    public String nameClient,clientOccupation;
    public TextView textClientName;
    public TextView textClientOccupation;
    public static TextView textTotalPrice;
    public ArrayList<ProductSI> productsSI;
    public ArrayList<ProductII> productsII;
    public OrderAdapter orderAdapter;
    public static double totalPrice=0;
    int kil=0;
    public static Fragment_order fragment_order;


    public static Fragment_order newInstance(boolean isClient,String nameClient,String clientOccupation){
        Fragment_order fragmentOrder=new Fragment_order();
        Bundle args = new Bundle();
        args.putBoolean("isClient", isClient);
        args.putString("clientName", nameClient);
        args.putString("clientOccupation", clientOccupation);
        fragmentOrder.setArguments(args);
        return fragmentOrder;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_order,container,false);
        totalPrice=0;
        ProductSITask productSITask = new ProductSITask(getContext());
        productSITask.delegate=this;
        productSITask.execute();
        ProductIITask productIITask = new ProductIITask(getContext());
        productIITask.delegate=this;
        productIITask.execute();

        progressBar=(ProgressBar)view.findViewById(R.id.process);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        isClient=getArguments().getBoolean("isClient");
        nameClient=getArguments().getString("clientName");
        clientOccupation=getArguments().getString("clientOccupation");
        //textviews
        textClientName=(TextView)view.findViewById(R.id.textClientName);
        textClientOccupation=(TextView)view.findViewById(R.id.textClientOccupation);
        textTotalPrice=(TextView)view.findViewById(R.id.textFullPrice);

        ListView listView=(ListView)view.findViewById(R.id.listOrder);
        orderAdapter = new OrderAdapter(getContext());
        listView.setAdapter(orderAdapter);

        //buttons
        btn_back=(Button)view.findViewById(R.id.btn_back);
        btnAddClient=(Button)view.findViewById(R.id.btnAddClient);
        btnCancel=(Button)view.findViewById(R.id.btnCancel);
        btnOplata=(Button)view.findViewById(R.id.btnOplata);
        allBtnSales = new ArrayList<View>();
        if (isClient){
            btnAddClient.setVisibility(View.INVISIBLE);
            textClientOccupation.setVisibility(View.VISIBLE);
            textClientName.setVisibility(View.VISIBLE);
            textClientName.setText(nameClient);
            textClientOccupation.setText(clientOccupation);
        }else{
           btnAddClient.setVisibility(View.VISIBLE);
            textClientName.setVisibility(View.INVISIBLE);
            textClientOccupation.setVisibility(View.INVISIBLE);
        }

        layoutSalesItems = (LinearLayout)view.findViewById(R.id.layoutSalesItems);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment
                Fragment_menu fragmentMenu=new Fragment_menu();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentMenu);
                fragmentTransaction.commit();
            }
        });
        btnAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_client fragmentClient=Fragment_client.newInstance(false,"Выберите Клиента");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentClient);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment
                Fragment_menu fragmentMenu=new Fragment_menu();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentMenu);
                fragmentTransaction.commit();
            }
        });

        btnOplata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClient){

                }else{
                    Toast.makeText(getContext(),"Выберите клиента!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public void displayProducts(ArrayList<ProductSI> products){
        for (int i=0;i<products.size();i++) {
            if (i % 5 != 0 && i != 0) {
                Button btnTag = new Button(getContext());
                btnTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText(products.get(i).getName());
                btnTag.setId(i);
                btnTag.setOnClickListener(myOnlyhandler);
                row.addView(btnTag);
            } else {
                row = new LinearLayout(getContext());
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                Button btnTag = new Button(getContext());
                btnTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText(products.get(i).getName());
                btnTag.setId(i);
                btnTag.setOnClickListener(myOnlyhandler);
                row.addView(btnTag);
                layoutSalesItems.addView(row);
            }
        }
    }
    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            //Log.e("click",productsSI.get(v.getId()).getProductII());
            int pos=v.getId();
            String name=productsSI.get(pos).getName();
            int count=productsSI.get(pos).getCount();
            int productII=productsSI.get(pos).getProductII();
            double price1=0,price2=0;
            for (int i=0;i<productsII.size();i++){
                if (productsII.get(i).getId()==productII){
                    price1=productsII.get(i).getPrice();
                }
            }
            price2=price1*count;
            String orderItem=name+" "+String.valueOf(count)+" шт "+String.valueOf(price1)+"$ "+String.valueOf(price2)+"$";
            totalPrice+=price2;
            showTotalPrice(totalPrice);
            orderAdapter.add(orderItem);
            orderAdapter.notifyDataSetChanged();
        }
    };
    public static void showTotalPrice(double total){
        textTotalPrice.setText("Итого "+String.valueOf(total)+"$");
    }
    @Override
    public void processFinish(ArrayList<String> output) {

    }

    @Override
    public void processProductSI(ArrayList<ProductSI> output) {
            productsSI=output;
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
            displayProducts(output);
    }

    @Override
    public void processProductII(ArrayList<ProductII> output) {
        productsII=output;
    }


}

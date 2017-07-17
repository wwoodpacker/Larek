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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public int empID;
    public TextView textClientName;
    public TextView textClientOccupation;
    public static TextView textTotalPrice;
    public ArrayList<ProductSI> productsSI;
    public ArrayList<ProductII> productsII;
    public ArrayList<OrderItem> orderItems;
    public OrderAdapter orderAdapter;
    public static double totalPrice=0;
    int kil=0;
    ScrollView sv;
    public static Fragment_order fragment_order;


    public static Fragment_order newInstance(boolean isClient,String nameClient,String clientOccupation,int empID){
        Fragment_order fragmentOrder=new Fragment_order();
        Bundle args = new Bundle();
        args.putBoolean("isClient", isClient);
        args.putString("clientName", nameClient);
        args.putString("clientOccupation", clientOccupation);
        args.putInt("empID", empID);
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
        orderItems=new ArrayList<>();
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
        empID=getArguments().getInt("empID");
        //textviews
        textClientName=(TextView)view.findViewById(R.id.textClientName);
        textClientOccupation=(TextView)view.findViewById(R.id.textClientOccupation);
        textTotalPrice=(TextView)view.findViewById(R.id.textFullPrice);

        ListView listView=(ListView)view.findViewById(R.id.listOrder);
        orderAdapter = new OrderAdapter(getContext(),false);
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
                    Order order= new Order();
                    order.setEmpName(nameClient);
                    order.setEmpOccupation(clientOccupation);
                    order.setSeller(Integer.valueOf(GlobalVariables.getInstance().getID()));
                    order.setSoldtime(getSoldTime());
                    order.setEmployees(empID);
                    order.setTotal(totalPrice);
                    orderItems.clear();
                    for(int i=0 ; i<orderAdapter.getCount() ; i++){
                        OrderItem obj = orderAdapter.getItem(i);
                        orderItems.add(obj);
                    }
                    FragmentApplyOrder fragmentApplyOrder=FragmentApplyOrder.newInstance(order,orderItems);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frgmCont, fragmentApplyOrder);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }else{
                    Toast.makeText(getContext(),"Выберите клиента!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
public String getSoldTime(){
    Calendar calander = Calendar.getInstance();
    String cDay = String.valueOf(calander.get(Calendar.DAY_OF_MONTH));
    String cMonth = String.valueOf(calander.get(Calendar.MONTH) + 1);
    String cYear = String.valueOf(calander.get(Calendar.YEAR));
    Date date = new Date();
    date.setHours(date.getHours());
    SimpleDateFormat simpDate;
    simpDate = new SimpleDateFormat("kk:mm:ss");
    String cHour = String.valueOf(calander.get(Calendar.HOUR));
    String cMinute = String.valueOf(calander.get(Calendar.MINUTE));
    String cSecond = String.valueOf(calander.get(Calendar.SECOND));
    return cDay+"."+cMonth+"."+cYear+" "+simpDate.format(date);//cHour+":"+cMinute+":"+cSecond;
}
    public void displayProducts(ArrayList<ProductSI> products){
        for (int i=0;i<products.size();i++) {
            if (i % 3 != 0 && i != 0) {
                Button btnTag = new Button(getContext());
                RelativeLayout.LayoutParams p= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                btnTag.setLayoutParams(p);
                btnTag.getLayoutParams().height=300;
                btnTag.getLayoutParams().width=300;
                btnTag.setText(products.get(i).getName());
                btnTag.setId(i);
                btnTag.setOnClickListener(myOnlyhandler);
                row.addView(btnTag);
            } else {
                row = new LinearLayout(getContext());
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                Button btnTag = new Button(getContext());
                btnTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                btnTag.getLayoutParams().height=300;
                btnTag.getLayoutParams().width=300;
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
            OrderItem orderItem =new OrderItem();
            orderItem.setOrderItemName(productsSI.get(pos).getName());
            orderItem.setSi_id(productsSI.get(pos).getId());
            orderItem.setSi_count(productsSI.get(pos).getCount());
            orderItem.setSi_price(price1);
            orderItem.setSi_total(price2);
            orderItem.setIi_id(productsSI.get(pos).getProductII());
            orderItem.setIi_count(productsSI.get(pos).getCount());
            orderItem.setIi_price(price1);
            orderItem.setIi_total(price2);
            orderItems.add(orderItem);
            totalPrice+=price2;
            showTotalPrice(totalPrice);
            orderAdapter.add(orderItem);
            orderAdapter.notifyDataSetChanged();
        }
    };
    public static void showTotalPrice(double total){
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        textTotalPrice.setText("Итого "+String.valueOf(df.format(total))+"$");
    }
    @Override
    public void processFinish(ArrayList<String> output) {

    }

    @Override
    public void processProductSI(ArrayList<ProductSI> output) {
            productsSI=output;

    }

    @Override
    public void processProductII(ArrayList<ProductII> output) {
        productsII=output;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
        displayProducts(productsSI);
    }


}

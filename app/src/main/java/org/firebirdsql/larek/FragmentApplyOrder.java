package org.firebirdsql.larek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by nazar.humeniuk on 6/26/17.
 */

public class FragmentApplyOrder extends Fragment {
    public Order order;
    public ArrayList<OrderItem> orderItems;
    public TextView textClientName;
    public TextView textClientOccupation;
    public TextView textTotalPrice;
    public Button btnEditOrder,btnOrder;
    public static FragmentApplyOrder newInstance(Order order, ArrayList<OrderItem> orderItems){
        FragmentApplyOrder fragmentApplyOrder=new FragmentApplyOrder();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ORDER", order);
        bundle.putSerializable("ORDERITEMS", orderItems);
        fragmentApplyOrder.setArguments(bundle);

        return fragmentApplyOrder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_apply_order,container,false);
        order=(Order)getArguments().getSerializable("ORDER");
        orderItems=(ArrayList<OrderItem>) getArguments().getSerializable("ORDERITEMS");
        Button btn_back=(Button)view.findViewById(R.id.btn_back);
        //textviews
        textClientName=(TextView)view.findViewById(R.id.textClientName);
        textClientOccupation=(TextView)view.findViewById(R.id.textClientOccupation);


        textTotalPrice=(TextView)view.findViewById(R.id.textFullPrice);
        textClientName.setText(order.getEmpName());
        textClientOccupation.setText(order.getEmpOccupation());
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        textTotalPrice.setText("К оплате "+String.valueOf(df.format(order.getTotal()))+" $");
        //buttons
        btnEditOrder=(Button)view.findViewById(R.id.btnEditOrder);
        btnOrder=(Button)view.findViewById(R.id.btnOplata);
        ListView listView=(ListView)view.findViewById(R.id.listOrder);
        OrderAdapter orderAdapter = new OrderAdapter(getContext(),true);
        listView.setAdapter(orderAdapter);
        orderAdapter.upDatelist(orderItems);
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
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyOrderTask applyOrderTask =new ApplyOrderTask(getContext(),order,orderItems);
                applyOrderTask.execute();
            }
        });
        btnEditOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        return view;
    }

    class ApplyOrderTask extends AsyncTask<Void,Void,Void> {
        ProgressDialog pd;
        Context context;
        boolean result;
        Order order;
        ArrayList<OrderItem> orderItems;
        private DBhelperFirebird dBhelperFirebird;
        public ApplyOrderTask(Context _context,Order _order,ArrayList<OrderItem> _orderItems){
            context=_context;
            dBhelperFirebird=new DBhelperFirebird();
            order=_order;
            orderItems=_orderItems;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Connection connection = dBhelperFirebird.getConnection();
                Statement statement = null;
                statement = connection.createStatement();
                String sql = "INSERT INTO LAREK_ORDER (SOLDTIME, SELLER, EMPLOYEES, TOTAL, LAREK_DEP) VALUES (\'" + order.getSoldtime() + "\'," + order.getSeller() + " , " + order.getEmployees() + "," + order.getTotal() + ", \'"+GlobalVariables.getInstance().getLarekDep()+"\' )";
                statement.execute(sql);
                statement.close();
                //=====
                String sqlString="SELECT ID FROM \"LAREK_ORDER\" WHERE SOLDTIME=\'"+order.getSoldtime()+"\'";
                int idOrder=0;
                PreparedStatement StatementRSFind=null;
                ResultSet RSFind=null;
                boolean rsReady = false;
                StatementRSFind = connection.prepareStatement(sqlString);
                RSFind = StatementRSFind.executeQuery();
                rsReady = RSFind.next();
                int ii = 0;
                if (rsReady) {
                    boolean done = false;
                    while (!done) {
                        ii++;
                        idOrder=(Integer) RSFind.getObject("ID");
                        done = !RSFind.next();
                    } //End while loop
                    RSFind.close();
                    }
                StatementRSFind.close();
                //======
                Statement statement2 = null;
                statement2 = connection.createStatement();
                for (int i=0;i<orderItems.size();i++) {
                    String sSql = "INSERT INTO LAREK_ORDER_ITEM (" +
                            " \"ORDER\", " +
                            "PRODUCT_SI_ID, " +
                            "PRODUCT_SI_COUNT, " +
                            "PRODUCT_SI_PRICE, " +
                            "PRODUCT_SI_TOTAL, " +
                            "PRODUCT_II_ID, " +
                            "PRODUCT_II_COUNT, " +
                            "PRODUCT_II_PRICE, " +
                            "PRODUCT_II_TOTAL) " +
                            "VALUES (" + idOrder + ","
                            + orderItems.get(i).getSi_id() + "," +
                            +orderItems.get(i).getSi_count() + "," +
                            +orderItems.get(i).getSi_price() + "," +
                            +orderItems.get(i).getSi_total() + "," +
                            +orderItems.get(i).getIi_id() + "," +
                            +orderItems.get(i).getIi_count() + "," +
                            +orderItems.get(i).getIi_price() + "," +
                            +orderItems.get(i).getIi_total() + " )";
                    statement2.execute(sSql);
                }
                statement2.close();
                connection.close();
                 }catch(Exception ex)
                     {
                    Log.e("FirebirdExample", ex.getMessage());
                    return null;
                     }
                return null;
        }



        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.setMessage("Оплата...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            Toast.makeText(getContext(),"Продано",Toast.LENGTH_SHORT).show();
            //Fragment
            Fragment_menu fragmentMenu=new Fragment_menu();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frgmCont, fragmentMenu);
            fragmentTransaction.commit();
        }
    }
}

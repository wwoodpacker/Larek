package org.firebirdsql.larek;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nazar.humeniuk on 6/26/17.
 */

public class FragmentApplyOrder extends Fragment implements ConnectionReceiver.ConnectivityReceiverListener{
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
        MenuActivity.readQuery();
        order=(Order)getArguments().getSerializable("ORDER");
        orderItems=(ArrayList<OrderItem>) getArguments().getSerializable("ORDERITEMS");
        getArguments().remove("ORDER");
        getArguments().remove("ORDERITEMS");
        Button btn_back=(Button)view.findViewById(R.id.btn_back);
        //textviews
        textClientName=(TextView)view.findViewById(R.id.textClientName);
        textClientOccupation=(TextView)view.findViewById(R.id.textClientOccupation);


        textTotalPrice=(TextView)view.findViewById(R.id.textFullPrice);
        textClientName.setText(order.getEmpName().replace(" ","\n"));
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternet(isConnected);
    }
    private boolean checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        showInternet(isConnected);
        Log.e("connetion",String.valueOf(isConnected));
        return isConnected;
    }

    @Override
    public void onResume() {
        super.onResume();
        LarekApplication.getInstance().setConnectivityListener(this);
    }
    public void showInternet(boolean isConnected){
        if (isConnected)
            SnackbarManager.show(
                    Snackbar.with(getContext()) // context
                            .text("Онлайн режим") // text to display
                            .actionLabel("Скрыть")
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)// action button label
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    Toast.makeText(getContext(),"Перейдите в меню что-бы синхронизировать покупки!",Toast.LENGTH_SHORT).show();
                                }
                            })
                    , getActivity());
        else
            SnackbarManager.show(
                    Snackbar.with(getContext()) // context
                            .text("Оффлайн режим") // text to display
                            .actionLabel("Скрыть")
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {

                                }
                            }) // action button's ActionClickListener
                    ,getActivity());
    }

    class ApplyOrderTask extends AsyncTask<Void,Void,Void> {
        ProgressDialog pd;
        Context context;
        boolean result;
        Order order;
        ArrayList<OrderItem> orderItems;
        private DBhelperFirebird dBhelperFirebird;
        private DBhelperSqllite dBhelperSqllite;
        public ApplyOrderTask(Context _context,Order _order,ArrayList<OrderItem> _orderItems){
            context=_context;
            dBhelperFirebird=new DBhelperFirebird();
            dBhelperSqllite=new DBhelperSqllite(context);
            order=_order;
            orderItems=_orderItems;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Random rand = new Random();
            int idOrder = rand.nextInt(1000000) + 1;;
            String sql="";

            try {
                Connection connection = dBhelperFirebird.getConnection();
                sql = "INSERT INTO LAREK_ORDER (SOLDTIME, SELLER, EMPLOYEES, TOTAL, LAREK_DEP) VALUES (\'" + order.getSoldtime() + "\'," + order.getSeller() + " , " + order.getEmployees() + "," + order.getTotal() + ", \'" + GlobalVariables.getInstance().getLarekDep() + "\' )";
                    Statement statement = null;
                    statement = connection.createStatement();
                    statement.execute(sql);
                    statement.close();
                    //=====
                    String sqlString = "SELECT ID FROM \"LAREK_ORDER\" WHERE SOLDTIME=\'" + order.getSoldtime() + "\'";
                    PreparedStatement StatementRSFind = null;
                    ResultSet RSFind = null;
                    boolean rsReady = false;
                    StatementRSFind = connection.prepareStatement(sqlString);
                    RSFind = StatementRSFind.executeQuery();
                    rsReady = RSFind.next();
                    int ii = 0;
                    if (rsReady) {
                        boolean done = false;
                        while (!done) {
                            ii++;
                            idOrder = (Integer) RSFind.getObject("ID");
                            done = !RSFind.next();
                        } //End while loop
                        RSFind.close();
                        SQLiteDatabase database = dBhelperSqllite.getWritableDatabase();
                        ContentValues cvI = new ContentValues();
                        cvI.put("ID",idOrder);
                        cvI.put("SOLDTIME",order.getSoldtime());
                        cvI.put("SELLER",order.getSeller());
                        cvI.put("EMPLOYEES",order.getEmployees());
                        cvI.put("TOTAL",order.getTotal());
                        cvI.put("LAREK_DEP",GlobalVariables.getInstance().getLarekDep());
                        database.insert("LAREK_ORDER", null, cvI);
                        database.close();
                    }
                    StatementRSFind.close();

            }catch (Exception e) {
                String ssql = "INSERT INTO LAREK_ORDER (ID,SOLDTIME, SELLER, EMPLOYEES, TOTAL, LAREK_DEP) VALUES ("+idOrder+",\'" + order.getSoldtime() + "\'," + order.getSeller() + " , " + order.getEmployees() + "," + order.getTotal() + ", \'" + GlobalVariables.getInstance().getLarekDep() + "\' )";
                SQLiteDatabase database = dBhelperSqllite.getWritableDatabase();
                ContentValues cvI = new ContentValues();
                cvI.put("ID",idOrder);
                cvI.put("SOLDTIME",order.getSoldtime());
                cvI.put("SELLER",order.getSeller());
                cvI.put("EMPLOYEES",order.getEmployees());
                cvI.put("TOTAL",order.getTotal());
                cvI.put("LAREK_DEP",GlobalVariables.getInstance().getLarekDep());
                database.insert("LAREK_ORDER", null, cvI);
                database.close();
                writeQueryToFile(ssql);
            }
                //======
            try {
                Connection connection = dBhelperFirebird.getConnection();
                Statement statement2 = null;
                statement2 = connection.createStatement();
                SQLiteDatabase database3 = dBhelperSqllite.getWritableDatabase();
                for (int i=0;i<orderItems.size();i++) {
                    ContentValues cvII = new ContentValues();
                    cvII.put("\"ORDER\"",idOrder);
                    cvII.put("PRODUCT_SI_ID",orderItems.get(i).getSi_id());
                    cvII.put("PRODUCT_SI_COUNT",orderItems.get(i).getSi_count());
                    cvII.put("PRODUCT_SI_PRICE",orderItems.get(i).getSi_price());
                    cvII.put("PRODUCT_SI_TOTAL",orderItems.get(i).getSi_total());
                    cvII.put("PRODUCT_II_ID",orderItems.get(i).getIi_id());
                    cvII.put("PRODUCT_II_COUNT",orderItems.get(i).getIi_count());
                    cvII.put("PRODUCT_II_PRICE",orderItems.get(i).getIi_price());
                    cvII.put("PRODUCT_II_TOTAL",orderItems.get(i).getIi_total());
                    database3.insert("LAREK_ORDER_ITEM", null, cvII);
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
                    database3.close();
                    statement2.close();
                    connection.close();
                 }catch(Exception ex)
                     {
                         SQLiteDatabase database2 = dBhelperSqllite.getWritableDatabase();
                         for (int i=0;i<orderItems.size();i++) {
                             ContentValues cvII = new ContentValues();
                             cvII.put("\"ORDER\"",idOrder);
                             cvII.put("PRODUCT_SI_ID",orderItems.get(i).getSi_id());
                             cvII.put("PRODUCT_SI_COUNT",orderItems.get(i).getSi_count());
                             cvII.put("PRODUCT_SI_PRICE",orderItems.get(i).getSi_price());
                             cvII.put("PRODUCT_SI_TOTAL",orderItems.get(i).getSi_total());
                             cvII.put("PRODUCT_II_ID",orderItems.get(i).getIi_id());
                             cvII.put("PRODUCT_II_COUNT",orderItems.get(i).getIi_count());
                             cvII.put("PRODUCT_II_PRICE",orderItems.get(i).getIi_price());
                             cvII.put("PRODUCT_II_TOTAL",orderItems.get(i).getIi_total());
                             database2.insert("LAREK_ORDER_ITEM", null, cvII);
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
                                     "VALUES ("+ idOrder + ","
                                     + orderItems.get(i).getSi_id() + "," +
                                     + orderItems.get(i).getSi_count()+ "," +
                                     + orderItems.get(i).getSi_price()+ "," +
                                     + orderItems.get(i).getSi_total()+ "," +
                                     + orderItems.get(i).getIi_id()+ "," +
                                     + orderItems.get(i).getIi_count()+ "," +
                                     + orderItems.get(i).getIi_price()+ "," +
                                     + orderItems.get(i).getIi_total()+ " )";
                             writeQueryToFile(sSql);
                         }
                         database2.close();
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
        public void writeQueryToFile(String query){
            try {
                OutputStreamWriter myOutWriter=new OutputStreamWriter(getContext().openFileOutput("Query.txt",Context.MODE_APPEND));
                myOutWriter.append(query);
                myOutWriter.append("\n");
                myOutWriter.close();
            }catch (Exception e){
                Log.e("WriteQuery",e.getMessage());
            }
        }
    }
}

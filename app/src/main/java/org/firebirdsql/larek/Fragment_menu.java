package org.firebirdsql.larek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import static org.firebirdsql.larek.MenuActivity.mSyncData;

/**
 * Created by nazar.humeniuk on 14.06.17.
 */

public class Fragment_menu extends Fragment implements ConnectionReceiver.ConnectivityReceiverListener{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu,
                container, false);
        checkConnection();
        MenuActivity.readQuery();
        Button btn_client=(Button)view.findViewById(R.id.btn_client);
        Button btn_larek=(Button)view.findViewById(R.id.btn_larek);
        Button btn_report=(Button)view.findViewById(R.id.btn_report);
        btn_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_client fragmentClient=Fragment_client.newInstance(true,"Клиенты");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentClient);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btn_larek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_order fragmentOrder = Fragment_order.newInstance(false,"1","1",0);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentOrder);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_report fragmentReport = new Fragment_report();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentReport);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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
                            .actionLabel("Синхронизировать")
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)// action button label
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    SyncDataBases syncDataBases=new SyncDataBases(getContext());
                                    syncDataBases.execute();
                                }
                            })
                    , getActivity());
        else
            SnackbarManager.show(
                    Snackbar.with(getContext()) // context
                            .text("Оффлай режым") // text to display
                            .actionLabel("Скрыть")
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                }
                            })
                    ,getActivity());
    }
    public class SyncDataBases extends AsyncTask<Void,Void,Void>{
        ProgressDialog pd;
        Context context;
        private DBhelperFirebird dBhelperFirebird;
        public SyncDataBases(Context _context){
            context=_context;
            dBhelperFirebird=new DBhelperFirebird();
        }
        @Override
        protected Void doInBackground(Void... avoid) {
            try {
                Connection connection = dBhelperFirebird.getConnection();
                Statement statement = null;
                statement = connection.createStatement();
                InputStream instream = context.openFileInput("Query.txt");
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line=null;
                    while (( line = buffreader.readLine()) != null) {
                        statement.execute(line);
                        Log.e("Line",line);
                    }
                }
                statement.close();
                connection.close();
            }catch (Exception e){
                Log.e("Sync",e.getMessage());
                return null;
            }
            return null;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.setMessage("Синхронизация...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            MenuActivity.clearTheFile();
        }
    }

}

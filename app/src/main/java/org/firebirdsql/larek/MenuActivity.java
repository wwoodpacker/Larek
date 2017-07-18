package org.firebirdsql.larek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Created by nazar.humeniuk on 09.06.17.
 */

public class MenuActivity extends FragmentActivity {
    public Fragment_menu fragmentMenu;
    public TextView user_name,mPadName;
    public static TextView mSyncData;
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mContext=this;
        //Toolbar
        Intent intent = getIntent();
        String Name = intent.getStringExtra("name");
        user_name=(TextView)findViewById(R.id.user_name);
        mPadName=(TextView)findViewById(R.id.pad_name);
        mSyncData=(TextView)findViewById(R.id.sync_data);
        user_name.setText(Name);
        mPadName.setText(GlobalVariables.getInstance().getPadName());
        readQuery();
        //Fragment
        fragmentMenu=new Fragment_menu();
        //FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frgmCont, fragmentMenu);
        fragmentTransaction.commit();

    }

    public static void readQuery(){
        try {
            int kil=0;
            InputStream instream = mContext.openFileInput("Query.txt");
            ArrayList<String> res=new ArrayList<>();
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line=null;
                while (( line = buffreader.readLine()) != null) {
                    res.add(line);
                    if (line.contains("LAREK_ORDER_ITEM")) kil++;
                    Log.e("Line",line);
                }
                GlobalVariables.getInstance().setQuerys(res);
                if (!res.isEmpty()){
                    mSyncData.setText("Не синхронизировано "+String.valueOf(kil)+" продуктов.");
                }else {
                    mSyncData.setText("Нет данных для синхронизации");
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void clearTheFile() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(mContext.getFilesDir(), "Query.txt"));
            fos.write("".getBytes());
            fos.close();
            GlobalVariables.getInstance().setQuerys(null);
            mSyncData.setText("Нет данных для синхронизации");
        }catch (Exception e){
            Log.e("WriteQuery",e.getMessage());
        }
    }
}

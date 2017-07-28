package org.firebirdsql.larek;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity implements AsyncResponse,ConnectionReceiver.ConnectivityReceiverListener{
    public static final String APP_PREFERENCES = "mysettings";
    public DBhelperSqllite dBhelperSqllite;
    public ArrayList<String> usersAndPass;
    public boolean isFirstSelection=false;
    public int spinnerPos=0;
    public SharedPreferences mSettings;
    public String[] usersArray,passArray,idArray;
    public Pad mPad;
    //UI elements
    private Spinner spinner;
    private EditText signInpass;
    private Button btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //Database variebles
        checkConnection();
        GlobalVariables.getInstance().setUsersArraybaseURL("jdbc:firebirdsql:185.59.141.162/3050:i:\\#DB#\\larek.fdb");
        GlobalVariables.getInstance().setLogin("ANDROID_LAREK");
        GlobalVariables.getInstance().setPassword("SBVU3*()#M|SBJ89s84ur<W($m-2-2");
        GlobalVariables.getInstance().setDriverName("org.firebirdsql.jdbc.FBDriver");
        //Login
        mPad=new Pad();
        getDeviceInfo();
        //Api
        dBhelperSqllite=new DBhelperSqllite(getApplicationContext());
        if (mSettings.contains("isFirst")){
            if(mSettings.getBoolean("isFirst",true)){
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putBoolean("isFirst", false);
                editor.apply();
                //SQLiteDatabase database = dbHelper.getWritableDatabase();
            }
            else
            {

            }
        }else{
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean("isFirst", true);
            editor.apply();
        }

        if(mSettings.contains("LAREK_DEP")) {
            mPad.setLarek_dep(mSettings.getString("LAREK_DEP", ""));
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        signInpass = (EditText)findViewById(R.id.editPass);
        btnSignIn=(Button)findViewById(R.id.btn_sign_in);

        usersAndPass=new ArrayList<>();
        //API offline or online
        if(checkConnection()){
            FirstConnectDB firstConnectDB= new FirstConnectDB(this);
            firstConnectDB.delegate=this;
            firstConnectDB.execute();
        }else {
            try {
                SQLiteDatabase db = dBhelperSqllite.getWritableDatabase();
                String delSql="delete   from Larek_authorization_list\n" +
                        "where    rowid not in\n" +
                        "         (\n" +
                        "         select  min(rowid)\n" +
                        "         from    Larek_authorization_list\n" +
                        "         group by\n" +
                        "                 Name\n" +
                        "         ,       ID\n" +
                        "         )";
                Cursor c=db.rawQuery(delSql,null);
                c.moveToFirst();
                c.close();
                String sSql = "SELECT ID, Name, Password FROM Larek_authorization_list WHERE Larek_Dep = \'" + mPad.getLarek_dep() + "\'";
                Cursor cursor = db.rawQuery(sSql, null);
                ArrayList<String> res = new ArrayList<>();
                while (cursor.moveToNext()) {
                    String id = String.valueOf(cursor.getInt(cursor.getColumnIndex("ID"))); //String.valueOf((Integer) RSFind.getObject("ID"));
                    String name = cursor.getString(cursor.getColumnIndex("Name")); //(String) RSFind.getObject("Name");
                    String pass = cursor.getString(cursor.getColumnIndex("Password")); //(String) RSFind.getObject("Password");
                    res.add(id);
                    res.add(name);
                    res.add(pass);
                }
                cursor.close();
                String findPadSql = "SELECT LAREK_DEP, NAME FROM LAREK_PAD WHERE MAC = \'"+mPad.getMac()+"\'";
                Cursor cursor2 = db.rawQuery(findPadSql, null);
                cursor2.moveToFirst();
                mPad.setLarek_dep(cursor2.getString(cursor2.getColumnIndex("LAREK_DEP")));
                mPad.setName(cursor2.getString(cursor2.getColumnIndex("NAME")));
                cursor2.close();
                processFinishOffline(res);
            }catch (Exception e){
                Log.e("Sqllite",e.getMessage());
            }
        }
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                GlobalVariables.getInstance().setName(usersArray[spinnerPos]);
                GlobalVariables.getInstance().setID(idArray[spinnerPos]);
                GlobalVariables.getInstance().setPadName(mPad.getName());
                GlobalVariables.getInstance().setLarekDep(mPad.getLarek_dep());

                    String tmp_pass = md5(signInpass.getText().toString());
                    if (tmp_pass.equals(passArray[spinnerPos])) {
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        intent.putExtra("name", usersArray[spinnerPos]);
                        startActivity(intent);
                        //finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Wait administrator", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LarekApplication.getInstance().setConnectivityListener(this);
    }
    private boolean checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        showInternet(isConnected);
        Log.e("connetion",String.valueOf(isConnected));
        return isConnected;
    }
    public void getDeviceInfo(){
        String macAddress=getMacAddr();
        Log.i("TAG", "SERIAL: " + Build.SERIAL);
        Log.i("TAG","MODEL: " + Build.MODEL);
        Log.i("TAG","MAC: " + macAddress);
        mPad.setModel(Build.MODEL);
        mPad.setSerial(Build.SERIAL);
        mPad.setMac(macAddress);

    }
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public void processFinishOffline(ArrayList<String> output) {
        try {
            usersArray = new String[output.size() / 3];
            passArray = new String[output.size() / 3];
            idArray = new String[output.size() / 3];
            int k = 0;
            for (int i = 0; i < output.size(); i += 3) {
                for (int j = 0; j < 3; j++) {
                    idArray[k] = output.get(i);
                    usersArray[k] = output.get(i + 1);
                    passArray[k] = output.get(i + 2);
                }
                k++;
            }
            // адаптер

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, usersArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            // заголовок
            spinner.setPrompt("Title");
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    if (isFirstSelection) {
                        spinnerPos = position;
                    }
                    isFirstSelection = true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }catch (Exception e){
            e.getMessage();
        }

    }

    @Override
    public void processFinish(ArrayList<String> output) {
        try {
            usersArray = new String[output.size() / 3];
            passArray = new String[output.size() / 3];
            idArray = new String[output.size() / 3];
            int k = 0;
            for (int i = 0; i < output.size(); i += 3) {
                for (int j = 0; j < 3; j++) {
                    idArray[k] = output.get(i);
                    usersArray[k] = output.get(i + 1);
                    passArray[k] = output.get(i + 2);
                }
                k++;
            }
            // адаптер

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, usersArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            // заголовок
            spinner.setPrompt("Title");
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    if (isFirstSelection) {
                        spinnerPos = position;
                    }
                    isFirstSelection = true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }catch (Exception e){
            e.getMessage();
        }

    }

    @Override
    public void processProductSI(ArrayList<ProductSI> output) {

    }

    @Override
    public void processProductII(ArrayList<ProductII> output) {

    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternet(isConnected);
    }
    public void showInternet(boolean isConnected){
        if (isConnected)
            SnackbarManager.show(
                    Snackbar.with(getApplicationContext()) // context
                            .text("Онлайн режим") // text to display
                            .actionLabel("Скрыть") // action button label
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {

                                }
                            }) // action button's ActionClickListener
                    , this);
        else
            SnackbarManager.show(
                    Snackbar.with(getApplicationContext()) // context
                            .text("Оффлайн режим") // text to display
                            .actionLabel("Скрыть")
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {

                                }
                            }) // action button's ActionClickListener
                    ,this);
    }

    class FirstConnectDB extends AsyncTask<Void,Void,ArrayList<String>>{
        ProgressDialog pd;
        Context context;
        ArrayList<String> result;
        public AsyncResponse delegate = null;
        private DBhelperFirebird dBhelperFirebird;
        public FirstConnectDB(Context _context){
            context=_context;
            dBhelperFirebird=new DBhelperFirebird();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            result=new ArrayList<>();
            try{
                String findPadSql = "SELECT \"MAC\", \"LAREK_DEP\", \"NAME\" FROM \"LAREK_PAD\" WHERE MAC = \'"+mPad.getMac()+"\'";
                SQLiteDatabase database = dBhelperSqllite.getWritableDatabase();
                ContentValues cvI = new ContentValues();
                ResultSet RSFind2=null;
                boolean rsReady2 = false;
                PreparedStatement StatementRSFind2 = dBhelperFirebird.getPreparedStatement(findPadSql);
                RSFind2 = StatementRSFind2.executeQuery();
                rsReady2 = RSFind2.next();
                int i2 = 0;
                if (rsReady2)
                {
                    boolean done2=false;
                    while (!done2)
                    {
                        i2++;
                        String larek_dep =(String) RSFind2.getObject("LAREK_DEP");
                        String name =(String) RSFind2.getObject("NAME");
                        String mac =(String) RSFind2.getObject("MAC");
                        cvI.put("LAREK_DEP",larek_dep);
                        cvI.put("NAME",name);
                        cvI.put("MAC",mac);
                        database.insert("LAREK_PAD", null, cvI);
                        mPad.setLarek_dep(larek_dep);
                        mPad.setName(name);
                        done2 = !RSFind2.next();
                    }
                    RSFind2.close();
                    database.close();;
                }
                if (i2==0){
                    String createPadSql="INSERT INTO LAREK_PAD\n" +
                            "(LAREK_DEP, MAC, MODEL,SERIAL)\n" +
                            "VALUES" +
                            "('000_main',\'"+mPad.getMac()+"\', \'"+mPad.getModel()+"\', \'"+mPad.getSerial()+"\');";
                    Statement statement2 = null;
                    mPad.setLarek_dep("000_main");
                    statement2 = dBhelperFirebird.getConnection().createStatement();
                    statement2.execute(createPadSql);
                    statement2.close();
                }
            }catch(Exception ex)
            {

                Log.e("FirebirdExample", ex.getMessage());
                return null;
            }
            try {
                String sSql = "SELECT ID,\"Name\",\"Password\" FROM \"Larek_authorization_list\" WHERE \"Larek_Dep\" = \'" + mPad.getLarek_dep() + "\'";
                SQLiteDatabase database = dBhelperSqllite.getWritableDatabase();
                ContentValues cvII = new ContentValues();
                ResultSet RSFind = null;
                boolean rsReady = false;
                PreparedStatement StatementRSFind = dBhelperFirebird.getPreparedStatement(sSql);
                RSFind = StatementRSFind.executeQuery();
                rsReady = RSFind.next();
                int i = 0;
                if (rsReady) {
                    boolean done = false;
                    while (!done) {
                        i++;
                        String id = String.valueOf((Integer) RSFind.getObject("ID"));
                        String name = (String) RSFind.getObject("Name");
                        String pass = (String) RSFind.getObject("Password");
                        result.add(id);
                        result.add(name);
                        result.add(pass);
                        cvII.put("ID",(Integer) RSFind.getObject("ID"));
                        cvII.put("Name",name);
                        cvII.put("Password",pass);
                        cvII.put("Larek_Dep",mPad.getLarek_dep());
                        database.insert("Larek_authorization_list", null, cvII);
                        Log.e("SQL Response", name);
                        Log.e("SQL Response", pass);
                        done = !RSFind.next();
                    }
                    RSFind.close();
                    database.close();
                }
            }catch (Exception ex) {
            }

            return result;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.setMessage("Загрузка...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            pd.dismiss();

                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("LAREK_DEP", mPad.getLarek_dep());
                editor.apply();

            delegate.processFinish(strings);
        }
    }
}

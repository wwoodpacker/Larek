package org.firebirdsql.larek;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends Activity implements AsyncResponse{
    public ArrayList<String> usersAndPass;
    public boolean isFirstSelection=false;
    public int spinnerPos=0;
    public String[] usersArray,passArray;
    //UI elements
    private Spinner spinner;
    private EditText signInpass;
    private Button btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Database variebles
        GlobalVariables.getInstance().setUsersArraybaseURL("jdbc:firebirdsql:185.59.141.162/3050:i:\\#DB#\\larek.fdb");
        GlobalVariables.getInstance().setLogin("ANDROID_LAREK");
        GlobalVariables.getInstance().setPassword("SBVU3*()#M|SBJ89s84ur<W($m-2-2");
        GlobalVariables.getInstance().setDriverName("org.firebirdsql.jdbc.FBDriver");
        //Login
        spinner = (Spinner) findViewById(R.id.spinner);
        signInpass = (EditText)findViewById(R.id.editPass);
        btnSignIn=(Button)findViewById(R.id.btn_sign_in);

        usersAndPass=new ArrayList<>();
        FirstConnectDB firstConnectDB= new FirstConnectDB(this);
        firstConnectDB.delegate=this;
        firstConnectDB.execute();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.getInstance().setName(usersArray[spinnerPos]);
                String tmp_pass=md5(signInpass.getText().toString());
                if (tmp_pass.equals(passArray[spinnerPos])){
                    Intent intent=new Intent(MainActivity.this,MenuActivity.class);
                    intent.putExtra("name",usersArray[spinnerPos]);
                    startActivity(intent);
                    //finish();
                }else {
                    Toast.makeText(MainActivity.this,"Wrong password",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void processFinish(ArrayList<String> output) {
        usersArray=new String[output.size()/2];
        passArray=new String[output.size()/2];
        int k=0,k2=0;
        for (int i=0;i<output.size();i++){
            if(i%2==0){
                usersArray[k++]=output.get(i);    
            }else passArray[k2++]=output.get(i);
            
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
                if (isFirstSelection){
                    spinnerPos=position;
                }
                isFirstSelection=true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


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

    class FirstConnectDB extends AsyncTask<Void,Void,ArrayList<String>>{
        ProgressDialog pd;
        Context context;
        ArrayList<String> result;
        public AsyncResponse delegate = null;

        public FirstConnectDB(Context _context){
            context=_context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            result=new ArrayList<>();
            try
            {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectNetwork()
                        .build());

                //aktivate logging over adb (optional)
                System.setProperty("FBAdbLog", "true");
                //register Driver
                Class.forName(GlobalVariables.getInstance().getDriverName());
                //Get connection
                String sCon = GlobalVariables.getInstance().getUsersArraybaseURL();
                Connection con = DriverManager.getConnection(sCon, GlobalVariables.getInstance().getLogin(),
                                                                    GlobalVariables.getInstance().getPassword());
                //Query (get Table Count)
                String sSql = "SELECT \"Name\",\"Password\" FROM \"Larek_authorization_list\"";
                Statement stmt = con.createStatement();
                //ResultSet rs = stmt.executeQuery(sSql);
                ResultSet RSFind=null;
                boolean rsReady = false;
                PreparedStatement StatementRSFind = con.prepareStatement(sSql);
                RSFind = StatementRSFind.executeQuery();
                rsReady = RSFind.next();
                int i = 0;
                if (rsReady)
                {
                    boolean done=false;
                    while (!done)
                    {
                        i++;
                        String name = (String) RSFind.getObject("Name");
                        String pass = (String) RSFind.getObject("Password");
                        result.add(name);
                        result.add(pass);
                        Log.e("SQL Response",name);
                        Log.e("SQL Response",pass);
                        done = !RSFind.next();
                    }

                    RSFind.close();
                }
            }catch(Exception ex)
            {

                Log.e("FirebirdExample", ex.getMessage());
                return null;
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
            delegate.processFinish(strings);
        }
    }
}

package org.firebirdsql.larek;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by nazar.humeniuk on 14.06.17.
 */

public class ClientListTask extends AsyncTask<Void,Void,ArrayList<String>> {
    public ClientAdapter clientAdapter;
    public ArrayList<Client> clientsarray;
    public Context context;
    MenuActivity menuActivity;
    ArrayList<String> result;
    public AsyncResponse delegate = null;

    public ClientListTask(ClientAdapter _clientadapter, Context context){
        clientAdapter=_clientadapter;
        this.context=context;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        clientsarray = new ArrayList<Client>();
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

            String sSql = "SELECT ID,\"Surname\",\"Name\",\"Patronimic\",\"Occupation\",\"Larek_Dep\",\"Status\" FROM \"Larek_Employees\"";
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
                    Client client=new Client();
                    client.setID("1");
                    client.setSurname((String)RSFind.getObject("Surname"));
                    client.setName((String)RSFind.getObject("Name"));
                    client.setPatronimic((String)RSFind.getObject("Patronimic"));
                    client.setOccupation((String)RSFind.getObject("Occupation"));
                    client.setLarek_Dep((String)RSFind.getObject("Larek_dep"));
                    client.setStatus("1");
                    clientAdapter.add(client);
                    /*String name = (String) RSFind.getObject("Name");
                    String pass = (String) RSFind.getObject("Password");
                    result.add(name);
                    result.add(pass);
                    Log.e("SQL Response",name);;*/
                    Log.e("SQL Response","done");

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

    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        clientAdapter.notifyDataSetChanged();
        delegate.processFinish(strings);
    }
}

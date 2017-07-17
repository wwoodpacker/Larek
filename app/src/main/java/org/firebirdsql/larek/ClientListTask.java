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
    private DBhelperFirebird dBhelperFirebird;

    public ClientListTask(ClientAdapter _clientadapter, Context context){
        clientAdapter=_clientadapter;
        this.context=context;
        dBhelperFirebird=new DBhelperFirebird();
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        clientsarray = new ArrayList<Client>();
        result=new ArrayList<>();
        String sSql="";
        sSql = "SELECT ID,\"Surname\",\"Name\",\"Patronimic\",\"Occupation\",\"Larek_Dep\",\"Status\" FROM \"Larek_Employees\" WHERE \"Larek_Dep\" = \'"+GlobalVariables.getInstance().getLarekDep()+"\'";
        try
        {

            ResultSet RSFind=null;
            boolean rsReady = false;
            PreparedStatement StatementRSFind = dBhelperFirebird.getPreparedStatement(sSql);
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
                    client.setID((Integer)RSFind.getObject("ID"));
                    client.setSurname((String)RSFind.getObject("Surname"));
                    client.setName((String)RSFind.getObject("Name"));
                    client.setPatronimic((String)RSFind.getObject("Patronimic"));
                    client.setOccupation((String)RSFind.getObject("Occupation"));
                    client.setLarek_Dep((String)RSFind.getObject("Larek_dep"));
                    client.setStatus((Integer)RSFind.getObject("Status"));
                    clientAdapter.add(client);
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

package org.firebirdsql.larek;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by nazar.humeniuk on 7/28/17.
 */

public class RemainsTask extends AsyncTask<Void,Void,ArrayList<String>> {
    public RemainsAdapter remainsAdapter;
    public ArrayList<Remains> remainsArrayList;
    public Context context;
    ArrayList<String> result;
    public RemainsResponse delegate = null;
    private DBhelperFirebird dBhelperFirebird;

    public RemainsTask(RemainsAdapter _remainsAdapter, Context context) {
        remainsAdapter = _remainsAdapter;
        this.context = context;
        dBhelperFirebird = new DBhelperFirebird();
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        remainsArrayList = new ArrayList<Remains>();
        result = new ArrayList<>();
        String sSql = "";
        sSql = "EXECUTE PROCEDURE PROC_OSTATKI (\'"+GlobalVariables.getInstance().getLarekDep()+"\')";
        try {

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
                    Remains remains = new Remains();
                    remains.setName((String) RSFind.getObject("NAME"));
                    remains.setNow_ost((Integer) RSFind.getObject("PREV_OST"));
                    remains.setPrev_ost((Integer) RSFind.getObject("NOW_OST"));
                    remains.setPrice((Double) RSFind.getObject("PRICE"));
                    remainsAdapter.add(remains);
                    Log.e("SQL Response", "done");
                    done = !RSFind.next();
                }
                RSFind.close();
                StatementRSFind.close();
            }
        } catch (Exception ex) {

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
        remainsAdapter.notifyDataSetChanged();
        delegate.processFinish(strings);
    }
}
package org.firebirdsql.larek;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by nazar.humeniuk on 6/24/17.
 */

public class ProductIITask extends AsyncTask<Void,Void,ArrayList<ProductII>> {
    public Context context;
    ArrayList<ProductII> result;
    public AsyncResponse delegate = null;
    private DBhelperFirebird dBhelperFirebird;

    public ProductIITask(Context context){
        this.context=context;
        dBhelperFirebird=new DBhelperFirebird();
    }

    @Override
    protected ArrayList<ProductII> doInBackground(Void... params) {
        result=new ArrayList<>();
        try
        {
            String sSql = "SELECT ID,\"NAME\",\"LAREK_DEP\",\"PRICE\" FROM \"LAREK_PRODUCT_II\"";
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
                    ProductII productII=new ProductII();
                    productII.setId((Integer)RSFind.getObject("ID"));
                    productII.setName((String)RSFind.getObject("NAME"));
                    productII.setLarekDep((String) RSFind.getObject("LAREK_DEP"));
                    productII.setPrice((Double)RSFind.getObject("PRICE"));
                    result.add(productII);
                    done = !RSFind.next();
                }

                RSFind.close();
            }
        }catch(Exception ex)
        {

            Log.e("ProductII", ex.getMessage());
            return null;
        }

        return result;
    }

    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(ArrayList<ProductII> productIIs) {
        delegate.processProductII(productIIs);
    }
}


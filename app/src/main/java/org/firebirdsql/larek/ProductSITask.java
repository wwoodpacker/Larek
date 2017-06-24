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

public class ProductSITask extends AsyncTask<Void,Void,ArrayList<ProductSI>> {
    public Context context;
    ArrayList<ProductSI> result;
    public AsyncResponse delegate = null;
    private DBhelperFirebird dBhelperFirebird;

    public ProductSITask(Context context){
        this.context=context;
        dBhelperFirebird=new DBhelperFirebird();
    }

    @Override
    protected ArrayList<ProductSI> doInBackground(Void... params) {
        result=new ArrayList<>();
        try
        {
            String sSql = "SELECT ID,\"NAME\",\"LAREK_PRODUCT_II\",\"COUNT_II\" FROM \"LAREK_PRODUCT_SI\"";
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
                    ProductSI productSI=new ProductSI();
                    productSI.setId((Integer)RSFind.getObject("ID"));
                    productSI.setName((String)RSFind.getObject("NAME"));
                    productSI.setCount((Integer)RSFind.getObject("COUNT_II"));
                    productSI.setProductII((Integer)RSFind.getObject("LAREK_PRODUCT_II"));
                    result.add(productSI);
                    Log.e("SQL Response","done");
                    done = !RSFind.next();
                }

                RSFind.close();
            }
        }catch(Exception ex)
        {

            Log.e("ProductSI", ex.getMessage());
            return null;
        }

        return result;
    }

    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(ArrayList<ProductSI> productSIs) {
        delegate.processProductSI(productSIs);
    }
}

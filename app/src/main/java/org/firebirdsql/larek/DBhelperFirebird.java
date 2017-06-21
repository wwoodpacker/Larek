package org.firebirdsql.larek;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by nazar.humeniuk on 6/21/17.
 */

public class DBhelperFirebird {
    public Connection con;
    public DBhelperFirebird(){
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
            con = DriverManager.getConnection(sCon, GlobalVariables.getInstance().getLogin(),
                    GlobalVariables.getInstance().getPassword());
        }catch(Exception ex)
        {
            Log.e("FirebirdExample", ex.getMessage());
        }
    }

    public Connection getConnection(){
        return con;
    }
    public PreparedStatement getPreparedStatement(String sql){
        try {
            return con.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

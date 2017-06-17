package org.firebirdsql.larek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;


/**
 * Created by nazar.humeniuk on 09.06.17.
 */

public class MenuActivity extends FragmentActivity {
    public Fragment_menu fragmentMenu;
    public TextView user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //Toolbar
        Intent intent = getIntent();
        String Name = intent.getStringExtra("name");
        user_name=(TextView)findViewById(R.id.user_name);
        user_name.setText(Name);
        //Fragment
        fragmentMenu=new Fragment_menu();
        //FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frgmCont, fragmentMenu);
        fragmentTransaction.commit();

    }


}

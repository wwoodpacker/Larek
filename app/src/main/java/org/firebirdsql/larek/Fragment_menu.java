package org.firebirdsql.larek;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by nazar.humeniuk on 14.06.17.
 */

public class Fragment_menu extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu,
                container, false);
        Button btn_client=(Button)view.findViewById(R.id.btn_client);
        Button btn_larek=(Button)view.findViewById(R.id.btn_larek);
        Button btn_report=(Button)view.findViewById(R.id.btn_report);
        btn_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_client fragmentClient=Fragment_client.newInstance(true,"Клиенты");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentClient);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btn_larek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_order fragmentOrder = Fragment_order.newInstance(false,"1","1",0);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentOrder);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_report fragmentReport = new Fragment_report();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentReport);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

}

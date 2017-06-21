package org.firebirdsql.larek;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nazar.humeniuk on 17.06.17.
 */

public class Fragment_order extends Fragment {
    public Button btnAddClient,btnCancel,btnOplata,btn_back;
    private List<View> allBtnSales;
    public LinearLayout layoutSalesItems;
    public  LinearLayout row=null;
    int kil=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_order,container,false);
        btn_back=(Button)view.findViewById(R.id.btn_back);
        btnAddClient=(Button)view.findViewById(R.id.btnAddClient);
        btnCancel=(Button)view.findViewById(R.id.btnCancel);
        btnOplata=(Button)view.findViewById(R.id.btnOplata);
        allBtnSales = new ArrayList<View>();

        btnAddClient.setVisibility(View.VISIBLE);
        layoutSalesItems = (LinearLayout)view.findViewById(R.id.layoutSalesItems);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment
                Fragment_menu fragmentMenu=new Fragment_menu();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentMenu);
                fragmentTransaction.commit();
            }
        });
        btnAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (kil%7!=0&&kil!=0){
                    Button btnTag = new Button(getContext());
                    btnTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    btnTag.setText("Button " + kil);
                    row.addView(btnTag);
                }else{
                    row = new LinearLayout(getContext());
                    row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    Button btnTag = new Button(getContext());
                    btnTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    btnTag.setText("Button " + kil);
                    row.addView(btnTag);
                    layoutSalesItems.addView(row);
                }
                kil++;

            }
        });
        return view;
    }
}

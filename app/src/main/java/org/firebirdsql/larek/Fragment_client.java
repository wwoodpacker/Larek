package org.firebirdsql.larek;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by nazar.humeniuk on 14.06.17.
 */

public class Fragment_client extends Fragment implements AsyncResponse{
    public ListView listView;
    public ClientAdapter clientAdapter;
    public ProgressBar progressBar;
    public ImageView btnSearch,btnSort;
    public static Fragment_client fragment_client;
    public boolean isFromMenu;
    public String title;

    public static Fragment_client newInstance(boolean isFromMenu,String title){
        Fragment_client fragmentClient=new Fragment_client();
        Bundle args = new Bundle();
        args.putBoolean("isFromMenu", isFromMenu);
        args.putString("title", title);
        fragmentClient.setArguments(args);
        return fragmentClient;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client,null);
        isFromMenu=getArguments().getBoolean("isFromMenu");
        title=getArguments().getString("title");
        TextView textTitle= (TextView)view.findViewById(R.id.textTitle);
        textTitle.setText(title);

        listView=(ListView)view.findViewById(R.id.client_list);
        progressBar=(ProgressBar)view.findViewById(R.id.process);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        btnSearch=(ImageView)view.findViewById(R.id.btn_search);
        btnSort=(ImageView)view.findViewById(R.id.btn_sort);

        Button btn_back=(Button)view.findViewById(R.id.btn_back);

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
        clientAdapter = new ClientAdapter(getContext(),getFragmentManager(),isFromMenu);
        listView.setAdapter(clientAdapter);
        ClientListTask clientListTask=new ClientListTask(clientAdapter,getContext());
        clientListTask.delegate=this;
        clientListTask.execute();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Поиск клиентов");
                // Set an EditText view to get user input
                final EditText input = new EditText(getContext());
                alert.setView(input);
                alert.setPositiveButton("Поиск", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String result = input.getText().toString();
                        clientAdapter.filter(result);
                    }
                });
                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
            }
        });

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientAdapter.sorting();
                Toast.makeText(getContext(),"Сортировка по алфавиту",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void processFinish(ArrayList<String> output) {
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void processProductSI(ArrayList<ProductSI> output) {

    }

    @Override
    public void processProductII(ArrayList<ProductII> output) {

    }
}

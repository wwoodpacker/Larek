package org.firebirdsql.larek;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.ArrayList;

/**
 * Created by nazar.humeniuk on 14.06.17.
 */

public class Fragment_client extends Fragment implements AsyncResponse,ConnectionReceiver.ConnectivityReceiverListener{
    public ListView listView;
    public ClientAdapter clientAdapter;
    public ProgressBar progressBar;
    public ImageView btnSearch,btnSort;
    public static Fragment_client fragment_client;
    public boolean isFromMenu;
    public String title;
    private DBhelperSqllite dBhelperSqllite;

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
        MenuActivity.readQuery();
        dBhelperSqllite=new DBhelperSqllite(getContext());
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
        if(checkConnection()){
            ClientListTask clientListTask=new ClientListTask(clientAdapter,getContext());
            clientListTask.delegate=this;
            clientListTask.execute();
        }else {
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
            readClients();
        }

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
    public void readClients(){
        try {
            SQLiteDatabase db = dBhelperSqllite.getWritableDatabase();
            String delSql="delete   from Larek_Employees\n" +
                    "where    rowid not in\n" +
                    "         (\n" +
                    "         select  min(rowid)\n" +
                    "         from    Larek_Employees\n" +
                    "         group by\n" +
                    "                 Surname\n" +
                    "         ,       ID\n" +
                    "         )";
            Cursor c=db.rawQuery(delSql,null);
            c.moveToFirst();
            c.close();
            String sSql = "SELECT ID,Surname,Name,Patronimic,Occupation,Larek_Dep,Status FROM Larek_Employees";
            Cursor cursor = db.rawQuery(sSql, null);
            while (cursor.moveToNext()) {
                Client client =new Client();
                client.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                client.setSurname(cursor.getString(cursor.getColumnIndex("Surname")));
                client.setName(cursor.getString(cursor.getColumnIndex("Name")));
                client.setPatronimic(cursor.getString(cursor.getColumnIndex("Patronimic")));
                client.setOccupation(cursor.getString(cursor.getColumnIndex("Occupation")));
                client.setLarek_Dep(cursor.getString(cursor.getColumnIndex("Larek_Dep")));
                clientAdapter.add(client);
            }
            clientAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            Log.e("Sqllite",e.getMessage());
        }
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternet(isConnected);
    }
    private boolean checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        showInternet(isConnected);
        Log.e("connetion",String.valueOf(isConnected));
        return isConnected;
    }

    @Override
    public void onResume() {
        super.onResume();
        LarekApplication.getInstance().setConnectivityListener(this);
    }
    public void showInternet(boolean isConnected){
        if (isConnected)
            SnackbarManager.show(
                    Snackbar.with(getContext())
                            .text("Онлайн режим")
                            .actionLabel("Скрыть")
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)// action button label
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    Toast.makeText(getContext(),"Перейдите в меню что-бы синхронизировать покупки!",Toast.LENGTH_SHORT).show();
                                }
                            })
                    , getActivity());
        else
            SnackbarManager.show(
                    Snackbar.with(getContext()) // context
                            .text("Оффлайн режим") // text to display
                            .actionLabel("Скрыть")
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    MenuActivity.clearTheFile();
                                }
                            }) // action button's ActionClickListener
                    ,getActivity());
    }

}

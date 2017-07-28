package org.firebirdsql.larek;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by nazar.humeniuk on 7/28/17.
 */

public class FragmentRemains extends Fragment implements RemainsResponse{
    public ListView listView;
    public ProgressBar progressBar;
    private RemainsAdapter remainsAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_remains,container,false);
        Button btn_back=(Button)view.findViewById(R.id.btn_back);
        listView=(ListView)view.findViewById(R.id.listremains);
        progressBar=(ProgressBar)view.findViewById(R.id.process);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        remainsAdapter=new RemainsAdapter(getContext(),getFragmentManager());
        listView.setAdapter(remainsAdapter);
        RemainsTask remainsTask = new RemainsTask(remainsAdapter,getContext());
        remainsTask.delegate=this;
        remainsTask.execute();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment
                Fragment_report fragmentReport=new Fragment_report();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frgmCont, fragmentReport);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    @Override
    public void processFinish(ArrayList<String> output) {
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }
}

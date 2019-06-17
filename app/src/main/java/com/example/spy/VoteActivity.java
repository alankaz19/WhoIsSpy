package com.example.spy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class VoteActivity extends AppCompatActivity {
    private RecyclerView rv;
    private ArrayList<String> data;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        initRv();

    }

    private void initRv(){
        rv = findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        data = new ArrayList<>();



        RvAdapter adapter = new RvAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);
    }



    public class RvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private class TextViewHolder extends RecyclerView.ViewHolder{
            TextView player;

            TextViewHolder(View view) {
                super(view);
                player = view.findViewById(R.id.tv_item);
            }
        }



//        public RvAdapter(ArrayList data, Context context){
//            VoteActivity.data = data;
//            this.context = context;
//
//        }public RvAdapter(ArrayList data){
//            this.data = data;
//        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_rv,viewGroup,false);
            return new TextViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if(viewHolder instanceof TextViewHolder) {
                ((TextViewHolder)viewHolder).player.setText(data.get(i));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

}

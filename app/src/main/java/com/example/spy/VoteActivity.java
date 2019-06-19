package com.example.spy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class VoteActivity extends AppCompatActivity {

    private int spyNum;
    private int civilianNum;
    private int whiteBoardNum;

    //recyclerView
    private RecyclerView rv;
    private Context context;

    //元件
    private Button btnFinish;
    private Button btnRiddle;
    private TextView banner;


    private String[] playerNames;
    private Player[] players;
    private String[] riddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        init();
        initRv();
        btnRiddle.setVisibility(View.GONE);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VoteActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


        btnRiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View riddleView = LayoutInflater.from(VoteActivity.this).inflate(R.layout.answer, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
                final AlertDialog dialog = builder.setView(riddleView).setCancelable(false).create();

                TextView riddle1 = riddleView.findViewById(R.id.tv_riddle1);
                TextView riddle2 = riddleView.findViewById(R.id.tv_riddle2);
                Button cancel = riddleView.findViewById(R.id.btn_exit);

                riddle1.setText(riddle[1]);
                riddle2.setText(riddle[0]);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

    private void initRv(){
        rv = findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);

        RvAdapter adapter = new RvAdapter(players);
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);
    }

    public class RvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private Context context;
        private Player[] players;

        private class TextViewHolder extends RecyclerView.ViewHolder{
            TextView player;
            LinearLayout llItem;
            String[]names = MainActivity.playerGenerator.getPlayerNames();
            TextViewHolder(View view) {
                super(view);
                player = view.findViewById(R.id.tv_item);
                llItem = view.findViewById(R.id.ll_item);
                View voteDialog = LayoutInflater.from(VoteActivity.this).inflate(R.layout.vote_dialog, null);

                final TextView voteHint = voteDialog.findViewById(R.id.tv_voting);
                final Button cancel = voteDialog.findViewById(R.id.btn_cancel);
                final Button kill = voteDialog.findViewById(R.id.btn_kill);

                AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
                final AlertDialog dialog = builder.setView(voteDialog).setCancelable(false).create();
                llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        voteHint.setText("確定要殺死 " + names[getLayoutPosition()] +" 嗎？");
                        dialog.show();

                        //取消殺人
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        //殺人
                        kill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                players[getLayoutPosition()].setDead(true);
                                if(players[getLayoutPosition()].getIdentity().equals("spy")){
                                    TextViewHolder.this.player.setText(playerNames[getLayoutPosition()]  + "\n\n" + "臥底");
                                    TextViewHolder.this.player.setTextColor(Color.parseColor("#DF5A5A"));
                                    TextViewHolder.this.player.setEnabled(false);
                                    spyNum--;
                                }else if(players[getLayoutPosition()].getIdentity().equals("whiteBoard")) {
                                    TextViewHolder.this.player.setText(playerNames[getLayoutPosition()]  + "\n\n" + "QQ 白板");
                                    TextViewHolder.this.player.setTextColor(Color.parseColor("#318EFD"));
                                    TextViewHolder.this.player.setEnabled(false);
                                    whiteBoardNum--;
                                }else{
                                    TextViewHolder.this.player.setText(playerNames[getLayoutPosition()]  + "\n\n" + "平民");
                                    TextViewHolder.this.player.setTextColor(Color.parseColor("#81D4FA"));
                                    TextViewHolder.this.player.setEnabled(false);
                                    civilianNum--;
                                }

                                if(spyNum  == 0 && civilianNum > 0) {
                                    banner.setText("平民勝利");
                                    banner.setTextSize(24);
                                    banner.setTextColor(Color.parseColor("#81D4FA"));
                                    btnRiddle.setVisibility(View.VISIBLE);
                                }else if(civilianNum == 0 && spyNum > 0) {
                                    Log.d("kill",civilianNum+"");
                                    banner.setText("臥底勝利");
                                    banner.setTextSize(24);
                                    banner.setTextColor(Color.parseColor("#DF5A5A"));
                                    btnRiddle.setVisibility(View.VISIBLE);
                                }

                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        }



        public RvAdapter(Player[] players, Context context){
            this.players = players;
            this.context = context;

        }public RvAdapter(Player[] players){
            this.players = players;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_rv,viewGroup,false);
            return new TextViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if(viewHolder instanceof TextViewHolder) {
                ((TextViewHolder)viewHolder).player.setText(players[i].getName());
            }
        }

        @Override
        public int getItemCount() {
            return players.length;
        }
    }

    private void init(){
        spyNum = MainActivity.playerGenerator.getSpyNum();
        whiteBoardNum = MainActivity.playerGenerator.getWhiteBoardNum();
        civilianNum = MainActivity.playerGenerator.getPlayerNum() - spyNum;

        players = MainActivity.playerGenerator.getPlayers();
        playerNames = MainActivity.playerGenerator.getPlayerNames();

        btnFinish = this.findViewById(R.id.btn_finish);
        btnRiddle = this.findViewById(R.id.btn_riddle);

        banner = this.findViewById(R.id.tv_banner);

        //取得謎底
        Bundle bundle = getIntent().getExtras();
        String[] custom_riddles = bundle.getStringArray("RIDDLE");
        riddle = custom_riddles;

        //取得玩家資料
        players = MainActivity.playerGenerator.getPlayers();
        playerNames = new String[players.length];
        for (int i = 0 ; i< players.length ; i++){
            playerNames[i] = players[i].getName();
        }
    }
}

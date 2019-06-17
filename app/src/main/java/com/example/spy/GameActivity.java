package com.example.spy;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private TextView playerName;
    private TextView riddle;
    private TextView riddleHint;
    private TextView clickHint;
    private LinearLayout llRiddle;




    public static final String CUSTOM_RIDDLE = "CUSTOM_RIDDLE";


    private int clickCount;
    private int playerCount;
    private boolean showRiddle;

    private Player[] players;
    private String[] currentRiddle;

    private boolean hasCustomRiddle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //介面初始化
        initView();

        //接收MainActivity傳來的資料  設定謎語
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
           final String[] custom_riddles = bundle.getStringArray("RIDDLE");
           hasCustomRiddle =true;
           currentRiddle = custom_riddles;
           setCurrentRiddle(currentRiddle);
        }else if(!hasCustomRiddle){
            String[] riddle = {"漢堡","掛包"};
            setCurrentRiddle(riddle);
        }


        //取得玩家資料
        players = MainActivity.playerGenerator.getPlayers();

        //

        //實現畫面雙擊
        LinearLayout llGame = findViewById(R.id.ll_game);

        llGame.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Toast.makeText(GameActivity.this,"請再按一次",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoubleClick(View view) {



                clickCount++;
                if(clickCount % 2 == 1) {
                    showRiddle = true;
                }else{
                    showRiddle = false;
                    playerCount++;
                }

                if(playerCount  +1 == players.length+1 ) {
                    //跳轉頁面
                    Intent intent = new Intent(GameActivity.this,VoteActivity.class);
                    startActivity(intent);

                }else{
                    String playerIdentity = players[playerCount].getIdentity();
                    displayRiddle(showRiddle, playerIdentity);
                }



            }
        }));

    }



    private void initView() {
        playerName = this.findViewById(R.id.tv_playerName);
        riddle = this.findViewById(R.id.tv_riddle);
        riddleHint = this.findViewById(R.id.tv_riddleHint);
        clickHint = this.findViewById(R.id.tv_clickHint);
        llRiddle = this.findViewById(R.id.ll_riddle);

    }



    private void displayRiddle(boolean showRiddle, String playerType) {
        //設定玩家名稱
        String currentPlayerName = players[playerCount].getName();
        playerName.setText(currentPlayerName);

        //依照身分顯示玩家謎底
        if(showRiddle){
            llRiddle.setVisibility(View.VISIBLE);
            if(players[playerCount].getIdentity() == "spy"){
                riddle.setText(currentRiddle[0]);
                riddleHint.setText(R.string.riddleHint);
                riddle.setTextColor(Color.parseColor("#FFFFFF"));
            }else if(players[playerCount].getIdentity()== "whiteBoard"){
                riddleHint.setText("哈哈你是白板");
                riddle.setText("QQ");
                riddle.setTextColor(Color.parseColor("#318EFD"));
            }else{
                riddle.setText(currentRiddle[1]);
                riddleHint.setText(R.string.riddleHint);
                riddle.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }else{
            llRiddle.setVisibility(View.GONE);
        }

        //顯示雙擊提示
        if(showRiddle){
            clickHint.setText(R.string.clickHint2);
        }else{
            clickHint.setText(R.string.clickHint1);
        }
    }

    private void setCurrentRiddle(String[] riddles) {
        this.currentRiddle = riddles;
    }
}

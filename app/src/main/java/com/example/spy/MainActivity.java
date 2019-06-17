package com.example.spy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static int MAX_PLAYER = 15;
    private static int MIN_PLAYER = 5;
    private static int MIN_SPY = 1;


    private TextView tvPlayerNum;
    private TextView tvSpyNum;
    private TextView tvCivilianNum;

    private Switch swChangeRiddle;
    private Switch swChangeNickName;
    private Switch swHasWhiteBoard;

    private SeekBar seekBar;

    private Button btnMinus;
    private Button btnPlus;
    private Button btnStart;

    public static  PlayerGenerator playerGenerator;

    private String[] customRiddle;
    private String[] riddle;
    private String[] playerNames;

    private int playerNum;
    private int spyNum;
    private int civilianNum;
    private int whiteBoardNum;

    private int maxSpy;

    private SharedPreferences sp;

    private boolean hasWhiteBoard;
    private boolean changeNickName;
    private boolean hasCustomRiddle;

    private OkHttpClient client;

    //API 控制
    NetworkController networkController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();

        init();
        getRiddle();



        tvSpyNum.setText(Integer.toString(spyNum));
        tvPlayerNum.setText(Integer.toString(playerNum));
        tvCivilianNum.setText(Integer.toString(civilianNum));
        seekBar.setProgress(playerNum);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress <= MAX_PLAYER && progress >= MIN_PLAYER) {
                    playerNum = progress;
                }

                if(playerNum > 9) {
                    spyNum = maxSpy = 3;
                    tvSpyNum.setText(Integer.toString((spyNum)));
                }else{
                    maxSpy = 2;
                    if(spyNum > maxSpy){
                        spyNum = maxSpy;
                    }
                    tvSpyNum.setText(Integer.toString(spyNum));
                }

                updateCivilian();

                tvPlayerNum.setText(Integer.toString(playerNum));
                tvCivilianNum.setText(Integer.toString(civilianNum));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(spyNum < maxSpy) {
                   spyNum++;
               }
               updateCivilian();
               tvSpyNum.setText(Integer.toString(spyNum));
               tvCivilianNum.setText(Integer.toString(civilianNum));

            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spyNum > MIN_SPY) {
                    spyNum--;
                }
                updateCivilian();
                tvSpyNum.setText(Integer.toString(spyNum));
                tvCivilianNum.setText(Integer.toString(civilianNum));
            }
        });

        swHasWhiteBoard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    whiteBoardNum = 1;
                    updateCivilian();
                    tvCivilianNum.setText(Integer.toString(civilianNum));
                    hasWhiteBoard = true;
                }else{
                    whiteBoardNum = 0;
                    updateCivilian();
                    tvCivilianNum.setText(Integer.toString((civilianNum)));
                    hasWhiteBoard = false;
                }

            }
        });

        swChangeNickName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    changeNickName = true;
                    playerNames = new String[playerNum];
                    initPlayerName(playerNames);
                }
                changeNickName = false;
            }
        });



        swChangeRiddle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    final View riddleView = LayoutInflater.from(MainActivity.this).inflate(R.layout.riddle, null);

                    final EditText customRiddle1 = riddleView.findViewById(R.id.ed_riddle1);
                    final EditText customRiddle2 = riddleView.findViewById(R.id.ed_riddle2);
                    Button customButton = riddleView.findViewById(R.id.btn_set);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final AlertDialog dialog = builder.setView(riddleView).create();

                    customButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!customRiddle1.getText().toString().equals("") && !customRiddle2.getText().toString().equals("") ) {
                                riddle[0] = customRiddle1.getText().toString();
                                riddle[1] = customRiddle2.getText().toString();
                                hasCustomRiddle = true;
                                Log.d("test",riddle[0] +" " +riddle[1] );
                            }
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }else{
                    getRiddle();
                    hasCustomRiddle = false;
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //傳送謎底
                Bundle bundle =  new Bundle();
                if(hasCustomRiddle){
                    bundle.putStringArray("RIDDLE",riddle);
                    Log.d("test",riddle[0] +" " +riddle[1] );
                }else{
                    bundle.putStringArray("RIDDLE",riddle);
                    Log.d("test",riddle[0] +" " +riddle[1] );
                }

                //建立玩家資料
                if(changeNickName){
                    playerGenerator = new PlayerGenerator(playerNum, spyNum, whiteBoardNum, playerNames);
                }else{
                    playerGenerator = new PlayerGenerator(playerNum, spyNum, whiteBoardNum);
                }

                //跳轉頁面
                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                //保存玩家數據
                sp.edit().putInt("PLAYER_NUM",playerNum)
                        .putInt("SPY_NUM",spyNum)
                        .putInt("WHITE_BOARD_NUM",whiteBoardNum)
                        .apply();
            }
        });

    }




    private void init() {
        sp = getSharedPreferences("DATA",MODE_PRIVATE);
        playerNum = sp.getInt("PLAYER_NUM", 5);
        spyNum = maxSpy = sp.getInt("SPY_NUM", 1);
        whiteBoardNum = sp.getInt("WHITEBOARD_NUM",0);
        civilianNum = playerNum - spyNum;

        tvPlayerNum = this.findViewById(R.id.tv_playerNum);
        tvSpyNum = this.findViewById(R.id.tv_spyNum);
        tvCivilianNum = this.findViewById(R.id.tv_civilianNum);

        swChangeNickName = this.findViewById(R.id.sw_changeNickName);
        swChangeRiddle = this.findViewById(R.id.sw_changeRiddle);
        swHasWhiteBoard = this.findViewById(R.id.sw_hasWhiteBoard);

        seekBar = this.findViewById(R.id.seekBar);

        btnMinus = this.findViewById(R.id.btn_minus);
        btnPlus = this.findViewById(R.id.btn_plus);
        btnStart = this.findViewById(R.id.btn_start);

        riddle = new String[2];


    }

    private void updateCivilian(){
        civilianNum = playerNum - spyNum - whiteBoardNum;
    }

    private void initPlayerName(String[] playerNames) {
        for(int i = 0 ; i < playerNames.length ; i++) {
            playerNames[i] = "玩家 " + i;
        }
    }

    private void getRiddle() {
        final ProgressDialog dialog =  ProgressDialog.show(MainActivity.this,"Loading","loading");
        FormBody formBody = new FormBody.Builder()
                .add("command", "getPuzzle")
                .build();

        Request request = new Request.Builder()
                .url("https://script.google.com/macros/s/AKfycbx139voITd8knGT5xlVBZESmPxGM61jEQoPcSTz-0y3kKc-MKJr/exec")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                Log.d("dialog","failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();

                try {
                    JSONArray jsonArray = new JSONArray(str);
                    int r = (int)(Math.random() * jsonArray.length());
                    JSONObject jsonObject = jsonArray.getJSONObject(r);
                    riddle[0] = jsonObject.getString("p1");
                    riddle[1] = jsonObject.getString("p2");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });




    }
}

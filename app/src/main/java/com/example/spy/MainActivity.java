package com.example.spy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private static int MAX_PLAYER = 15;
    private static int MIN_PLAYER = 5;
    private static int MIN_SPY = 1;

    //玩家生成器
    public static PlayerGenerator playerGenerator;
    public static String[] playerNames;

    //各個身分人數
    private TextView tvPlayerNum;
    private TextView tvSpyNum;
    private TextView tvCivilianNum;

    //三個開關宣告與布林值
    private Switch swChangeRiddle;
    private Switch swChangeNickName;
    private Switch swHasWhiteBoard;

    private boolean changeNickName;
    private boolean hasCustomRiddle;
    private boolean hasWhiteBoard;



    //拉條與按鈕
    private SeekBar seekBar;

    private Button btnMinus;
    private Button btnPlus;
    private Button btnStart;


    private String[] riddle;

    private int playerNum;
    private int spyNum;
    private int civilianNum;
    private int whiteBoardNum;
    private int maxSpy;

    //儲存資料
    private SharedPreferences sp;

    //客戶端連線
    private OkHttpClient client;

    private RecyclerView rv;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();

        init();
        initPlayerNames();

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

        //自定義暱稱
        swChangeNickName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                initPlayerNames();
                //attribute
                Context context = MainActivity.this;
                final View nickNameView = LayoutInflater.from(MainActivity.this).inflate(R.layout.nick_name, null);


                //initRv
                rv = nickNameView.findViewById(R.id.nick_name_rv);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                MyRvAdapter adapter = new MyRvAdapter(playerNames,context);
                rv.setLayoutManager(layoutManager);
                rv.setAdapter(adapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog dialog = builder.setView(nickNameView).create();
                Button nickNameBtn = nickNameView.findViewById(R.id.btn_nick_name);

                if(isChecked){
                    changeNickName = true;
                    dialog.show();

                    nickNameBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }else{
                    changeNickName = false;
                }
            }
        });


        //自定義謎底
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
                                riddle[1] = customRiddle1.getText().toString();
                                riddle[0] = customRiddle2.getText().toString();
                                hasCustomRiddle = true;
                                Log.d("test",riddle[0] +" " +riddle[1] );
                            }
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }else{
                    hasCustomRiddle = false;
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //建立玩家資料
                if(changeNickName){
                    playerGenerator = new PlayerGenerator(playerNum, spyNum, whiteBoardNum, playerNames);
                }else{
                    playerGenerator = new PlayerGenerator(playerNum, spyNum, whiteBoardNum);
                    Log.d("test","success");
                }


                //保存玩家數據
                sp.edit().putInt("PLAYER_NUM",playerNum)
                        .putInt("SPY_NUM",spyNum)
                        .putInt("WHITE_BOARD_NUM",whiteBoardNum)
                        .apply();

                //連線取得謎底
                getRiddle();
            }
        });

    }




    private void init() {
        //從記憶體取出上次資料
        sp = getSharedPreferences("DATA",MODE_PRIVATE);
        playerNum = sp.getInt("PLAYER_NUM", 5);
        spyNum = maxSpy = sp.getInt("SPY_NUM", 1);
        whiteBoardNum = sp.getInt("WHITEBOARD_NUM",0);
        civilianNum = playerNum - spyNum;

        //連結各個元件
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

    private void changePlayerName(String[] playerNames) {
        for(int i = 0 ; i < playerNames.length ; i++) {
            playerGenerator.getPlayers()[i].setName(playerNames[i]);
            playerGenerator.getPlayerNames()[i]  = playerNames[i];
        }
    }

    private void getRiddle() {
        NetworkController.getInstance().postRiddle(new NetworkController.NetworkControllerCallback(MainActivity.this) {
            @Override
            public void onSuccess(JSONObject responseJson) {
                String str = responseJson.toString();
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    JSONObject jsonObject2 = jsonObject.getJSONObject("msg");
                    JSONArray jsonArray = jsonObject2.getJSONArray("puzzles");
                    int r = (int)(Math.random() * jsonArray.length());
                    JSONObject puzzle = jsonArray.getJSONObject(r);

                    Toast.makeText(MainActivity.this,puzzle.toString(),Toast.LENGTH_SHORT);
                    if(!hasCustomRiddle){
                        riddle[0] = puzzle.getString("p1");
                        riddle[1] = puzzle.getString("p2");
                    }

                    //傳送謎底
                    Bundle bundle =  new Bundle();
                    if(hasCustomRiddle){
                        bundle.putStringArray("RIDDLE",riddle);
                        Log.d("riddle",riddle[0] +" " +riddle[1] );
                    }else{
                        bundle.putStringArray("RIDDLE",riddle);
                        Log.d("riddle",riddle[0] +" " +riddle[1] );
                    }

                    //跳轉頁面
                    Intent intent = new Intent(MainActivity.this,GameActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errMsg) {
                Log.d("test",errMsg);
            }

            @Override
            public void onComplete() {

            }
        }.enableLoadingDialog().showErrorToast());

    }

    private void initPlayerNames() {
        playerNames = new String[playerNum];
        for(int i = 0; i < playerNames.length ; i++) {
            playerNames[i] = "玩家 " +(i + 1);
        }

    }

    public class MyRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private class MyTextViewHolder extends RecyclerView.ViewHolder {
            EditText etNickName;
            TextWatcher textWatcher;

            public MyTextViewHolder(@NonNull View itemView) {
                super(itemView);
                etNickName = itemView.findViewById(R.id.et_nickName);//利用FindById 找到要使用的item
                textWatcher = new TextWatcher() { //監聽文字輸入

                    String origin;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        int position = getLayoutPosition();
                        origin = playerNames[position];
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int position = getLayoutPosition();
                        if(!s.toString().equals("")){
                            playerNames[position] = s.toString();
                            Toast.makeText(MainActivity.this, "請輸入暱稱", Toast.LENGTH_SHORT);
                        }else{
                            Toast.makeText(MainActivity.this, "請輸入暱稱", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int position = getLayoutPosition();
                        if(s.toString().equals("玩") || s.toString().equals(origin.charAt(0))){
                            playerNames[position] = origin + " " + (getLayoutPosition() + 1);
                        }
                    }
                };
                etNickName.addTextChangedListener(textWatcher);
            }
        }

        private String[] playerNames;
        private Context context;

        public MyRvAdapter(String[] playerNames, Context context) { //依照使用的資料型態創造建構子 傳入Context參數
            this.playerNames = playerNames;
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nickname_item, viewGroup, false);
            return new MainActivity.MyRvAdapter.MyTextViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof MainActivity.MyRvAdapter.MyTextViewHolder) {
                ((MainActivity.MyRvAdapter.MyTextViewHolder) viewHolder).etNickName.setText(playerNames[i]);
            }
        }

        @Override
        public int getItemCount() {
            return playerNames.length;
        }
    }

}

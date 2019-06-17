package com.example.spy;

import android.util.Log;

public class PlayerGenerator {
    private int playerNum;
    private int spyNum;
    private int whiteBoardNum;


    private Player[] players;
    private String[] playerNames;


    public PlayerGenerator(int playerNum, int spyNum ,int whiteBoardNum) {
        this.playerNum = playerNum;
        this.spyNum = spyNum;
        this.whiteBoardNum = whiteBoardNum;

        players = new Player[playerNum];
        generatePlayerData();
        randomIdentity(players);
    }

    public PlayerGenerator(int playerNum, int spyNum ,int whiteBoardNum, String[] playerNames) {
        this.playerNum = playerNum;
        this.spyNum = spyNum;
        this.whiteBoardNum = whiteBoardNum;

        players = new Player[playerNum];
        generatePlayerData(playerNames);
        randomIdentity(players);
    }

    private void swapIdentity(Player[] players, int i , int j) {
        String temp = players[i].getIdentity();
        players[i].setIdentity(players[j].getIdentity());
        players[j].setIdentity(temp);
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public String[] getPlayerNames() {
        return this.playerNames;
    }

    private void generatePlayerData() {
        for(int i = 0; i < players.length ; i++) {
            players[i] = new Player(i);
            players[i].setName("玩家 " +(i + 1));
        }
    }
    private void generatePlayerData(String[] playerNames) {
        for(int i = 0; i < players.length ; i++) {
            players[i] = new Player(i);
            players[i].setName(playerNames[i]);
        }
    }

    private void randomIdentity(Player[] players) {
        for(int i  = 0; i < players.length ; i++) {
            this.players[i].setIdentity("civilian");
            if(spyNum > 0) {
                spyNum--;
                this.players[i].setIdentity("spy");
            }else if(whiteBoardNum > 0){
                whiteBoardNum--;
                this.players[i].setIdentity("whiteBoard");
            }



        }

        for(int j = 0; j < players.length ; j++) {
            int random = (int)(Math.random() * players.length);
            swapIdentity(players,j,random);
        }


    }


}

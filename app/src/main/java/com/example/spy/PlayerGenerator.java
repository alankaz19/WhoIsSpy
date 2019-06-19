package com.example.spy;


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
        playerNames = new String[playerNum];
        generatePlayerData();
        randomIdentity(players);
    }

    public PlayerGenerator(int playerNum, int spyNum ,int whiteBoardNum, String[] playerNames) {
        this.playerNum = playerNum;
        this.spyNum = spyNum;
        this.whiteBoardNum = whiteBoardNum;

        this.players = new Player[playerNum];
        this.playerNames = playerNames;
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
        for(int i = 0; i < players.length ; i++) {
            this.playerNames[i] = players[i].getName();
        }
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
        int tempSpyNum = spyNum;
        int tempWhiteBoardNum = whiteBoardNum;
        for(int i  = 0; i < players.length ; i++) {
            this.players[i].setIdentity("civilian");
            if (tempSpyNum > 0) {
                tempSpyNum--;
                this.players[i].setIdentity("spy");
            } else if (tempWhiteBoardNum > 0) {
                tempWhiteBoardNum--;
                this.players[i].setIdentity("whiteBoard");
            }
        }

        for(int j = 0; j < players.length ; j++) {
            int random = (int)(Math.random() * players.length);
            swapIdentity(players,j,random);
        }

    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getSpyNum() {
        return spyNum;
    }

    public int getWhiteBoardNum() {
        return whiteBoardNum;
    }
}

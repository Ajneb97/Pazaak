package pz.ajneb97.model.game;

import org.bukkit.entity.Player;
import pz.ajneb97.Pazaak;
import pz.ajneb97.tasks.GameTask;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {
    private GamePlayer player1;
    private GamePlayer player2;
    private GamePlayer playerTurn;
    private GamePlayer startingRoundPlayer;
    private double bet;
    private GameStatus gameStatus;
    private int turnTime;
    private GameTask gameTask;
    private boolean inventoryBlocked;
    private long millisStart;

    public Game(Player player1, Player player2){
        this.player1 = new GamePlayer(player1);
        this.player2 = new GamePlayer(player2);
    }

    public void generatePlayerStart(){
        player1.reset();
        player2.reset();
        if(startingRoundPlayer == null){
            int num = new Random().nextInt(2);
            if(num == 1){
                startingRoundPlayer = player1;
            }else{
                startingRoundPlayer = player2;
            }
        }else{
            if(startingRoundPlayer == player1){
                startingRoundPlayer = player2;
            }else{
                startingRoundPlayer = player1;
            }
        }
        playerTurn = startingRoundPlayer;
    }

    public void changeTurn(){
        if(playerTurn == player1 && !player2.isOnStand()){
            playerTurn = player2;
        }else if(playerTurn == player2 && !player1.isOnStand()){
            playerTurn = player1;
        }
    }

    public GamePlayer getPlayer1() {
        return player1;
    }

    public void setPlayer1(GamePlayer player1) {
        this.player1 = player1;
    }

    public GamePlayer getPlayer2() {
        return player2;
    }

    public void setPlayer2(GamePlayer player2) {
        this.player2 = player2;
    }

    public GamePlayer getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(GamePlayer playerTurn) {
        this.playerTurn = playerTurn;
    }

    public GamePlayer getStartingRoundPlayer() {
        return startingRoundPlayer;
    }

    public void setStartingRoundPlayer(GamePlayer startingRoundPlayer) {
        this.startingRoundPlayer = startingRoundPlayer;
    }

    public double getBet() {
        return bet;
    }

    public void setBet(double bet) {
        this.bet = bet;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getTurnTime() {
        return turnTime;
    }

    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    public GamePlayer getGamePlayer(Player player){
        if(player1 != null && player1.getPlayer().equals(player)){
            return player1;
        }
        if(player2 != null && player2.getPlayer().equals(player)){
            return player2;
        }
        return null;
    }

    public List<GamePlayer> getGamePlayers(){
        return Arrays.asList(player1,player2);
    }

    public void startCooldownTask(Pazaak plugin, int time){
        stopCooldownTask();
        gameTask = new GameTask(plugin,this,time);
        gameTask.start();
    }

    public void stopCooldownTask(){
        if(gameTask != null){
            gameTask.stop();
            gameTask = null;
        }
    }

    public GamePlayer getOpponent(Player player){
        if(player1.getPlayer().equals(player)){
           return player2;
        }
        return player1;
    }

    public boolean isInventoryBlocked() {
        return inventoryBlocked;
    }

    public void setInventoryBlocked(boolean inventoryBlocked) {
        this.inventoryBlocked = inventoryBlocked;
    }

    public GameTask getGameTask() {
        return gameTask;
    }

    public long getMillisStart() {
        return millisStart;
    }

    public void setMillisStart(long millisStart) {
        this.millisStart = millisStart;
    }
}

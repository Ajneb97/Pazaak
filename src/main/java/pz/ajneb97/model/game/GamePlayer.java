package pz.ajneb97.model.game;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class GamePlayer {
    private Player player;
    private ArrayList<String> cards;
    private int winRounds;
    private ArrayList<String> bonusCards;
    private boolean onStand;
    private boolean bonusCardPlayed;
    private boolean changingInventory;

    private Inventory topInventoryCopy;

    public GamePlayer(Player player){
        this.player = player;

        cards = new ArrayList<>();
        bonusCards = new ArrayList<>();
        Random r = new Random();
        for(int i=0;i<=3;i++){
            int num = r.nextInt(6)+1;
            int sign = r.nextInt(2)+1;
            if(sign == 1){
                bonusCards.add("+"+num);
            }else{
                bonusCards.add("-"+num);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getWinRounds() {
        return winRounds;
    }

    public void setWinRounds(int winRounds) {
        this.winRounds = winRounds;
    }

    public ArrayList<String> getBonusCards() {
        return bonusCards;
    }

    public void setBonusCards(ArrayList<String> bonusCards) {
        this.bonusCards = bonusCards;
    }

    public boolean isOnStand() {
        return onStand;
    }

    public void setOnStand(boolean onStand) {
        this.onStand = onStand;
    }

    public boolean isBonusCardPlayed() {
        return bonusCardPlayed;
    }

    public void setBonusCardPlayed(boolean bonusCardPlayed) {
        this.bonusCardPlayed = bonusCardPlayed;
    }

    public void reset(){
        this.onStand = false;
        this.bonusCardPlayed = false;
        this.cards = new ArrayList<>();
    }

    public boolean isChangingInventory() {
        return changingInventory;
    }

    public void setChangingInventory(boolean changingInventory) {
        this.changingInventory = changingInventory;
    }

    public ArrayList<String> getCards() {
        return cards;
    }

    public void setCards(ArrayList<String> cards) {
        this.cards = cards;
    }

    public int getPoints(){
        int points = 0;
        for(String card : cards){
            points = points+Integer.parseInt(card);
        }
        return points;
    }

    public void addNormalCard(int cardNumber){
        cards.add(cardNumber+"");
    }

    public boolean useBonusCard(int bonusCardPos){
        String bonusCard = bonusCards.get(bonusCardPos);
        cards.add(bonusCard);
        if(getPoints() < 0){
            cards.remove(cards.size()-1);
            return false;
        }

        bonusCards.set(bonusCardPos,null);
        bonusCardPlayed = true;
        return true;
    }

    public boolean tableFull(){
        return cards.size() == 9;
    }

    public Inventory getTopInventoryCopy() {
        return topInventoryCopy;
    }

    public void setTopInventoryCopy(Inventory topInventoryCopy) {
        this.topInventoryCopy = topInventoryCopy;
    }
}

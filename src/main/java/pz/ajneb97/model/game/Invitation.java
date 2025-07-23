package pz.ajneb97.model.game;

import org.bukkit.entity.Player;
import pz.ajneb97.Pazaak;
import pz.ajneb97.tasks.InvitationExpirationTask;

public class Invitation {
    private Player sender;
    private Player receiver;
    private double bet;
    private int expireTime;
    private InvitationExpirationTask expirationTask;

    public Invitation(Player sender,Player receiver,double bet,int expireTime){
        this.sender = sender;
        this.receiver = receiver;
        this.bet = bet;
        this.expireTime = expireTime;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public double getBet() {
        return bet;
    }

    public void setBet(double bet) {
        this.bet = bet;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public void startExpirationTask(Pazaak plugin){
        expirationTask = new InvitationExpirationTask(plugin,this);
        expirationTask.start();
    }

    public void stopExpirationTask(){
        expirationTask.stop();
    }
}

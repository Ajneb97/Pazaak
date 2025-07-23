package pz.ajneb97.managers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pz.ajneb97.Pazaak;
import pz.ajneb97.configs.MainConfigManager;
import pz.ajneb97.model.game.Invitation;

import java.util.ArrayList;

public class InvitationManager {

    private Pazaak plugin;

    // Sender can send only 1 invitation
    // Receiver can receive more than 1 invitation
    private ArrayList<Invitation> invitations;

    public InvitationManager(Pazaak plugin){
        this.plugin = plugin;
        invitations = new ArrayList<>();
    }

    public ArrayList<Invitation> getInvitations() {
        return invitations;
    }

    public Invitation getInvitationBySender(Player player){
        for(Invitation i : invitations){
            if(i.getSender().equals(player)){
                return i;
            }
        }
        return null;
    }

    public ArrayList<Invitation> getInvitationsByReceiver(Player player){
        ArrayList<Invitation> inv = new ArrayList<>();
        for(Invitation i : invitations){
            if(i.getReceiver().equals(player)){
                inv.add(i);
            }
        }
        return inv;
    }

    public Invitation getInvitation(Player sender,Player receiver){
        for(Invitation i : invitations){
            if(i.getSender().equals(sender) && i.getReceiver().equals(receiver)){
                return i;
            }
        }
        return null;
    }

    public void removeInvitation(Invitation invitation){
        invitations.removeIf(i -> {
            if(i.equals(invitation)){
                i.stopExpirationTask();
                return true;
            }
            return false;
        });
    }

    public void invitePlayer(Player player1, Player player2, double bet){
        FileConfiguration messages = plugin.getMessagesConfig();
        MessagesManager msgManager = plugin.getMessagesManager();

        // World check
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        if(mainConfigManager.getDisabledWorlds().contains(player1.getLocation().getWorld().getName())){
            msgManager.sendMessage(player1,messages.getString("worldDisabled"),true);
            return;
        }

        // Same player
        if(player1.equals(player2)){
            msgManager.sendMessage(player1,messages.getString("selfInvitation"),true);
            return;
        }

        // Bet check
        if(bet != 0){
            Economy vault = plugin.getDependencyManager().getVault();
            if(vault != null && vault.getBalance(player1) < bet){
                msgManager.sendMessage(player1,messages.getString("moneyError"),true);
                return;
            }
        }

        // Already playing
        GameManager gameManager = plugin.getGameManager();
        if(gameManager.getGameByPlayer(player1) != null){
            msgManager.sendMessage(player1,messages.getString("alreadyPlaying"),true);
            return;
        }
        if(gameManager.getGameByPlayer(player2) != null){
            msgManager.sendMessage(player1,messages.getString("alreadyPlayingOther"),true);
            return;
        }

        // Distance to player
        if(!isValidDistance(player1,player2)){
            msgManager.sendMessage(player1,messages.getString("distanceError"),true);
            return;
        }

        // Already has sent an invitation to someone
        Invitation invitation = getInvitationBySender(player1);
        if(invitation != null){
            msgManager.sendMessage(player1,messages.getString("alreadyInvited")
                    .replace("%player%",invitation.getReceiver().getName()),true);
            return;
        }

        // Create invitation and start expiration cooldown
        int expireTime = mainConfigManager.getInvitationExpireTime();
        invitation = new Invitation(player1,player2,bet,expireTime);
        invitations.add(invitation);
        invitation.startExpirationTask(plugin);

        // Messages
        String msgSender;
        String msgReceiver;
        if(bet == 0){
            msgSender = messages.getString("playerInvite").replace("%player%",player2.getName());
            msgReceiver = messages.getString("invitationReceived").replace("%player%",player1.getName());
        }else{
            msgSender = messages.getString("playerInviteBet").replace("%player%",player2.getName())
                    .replace("%bet%",bet+"");
            msgReceiver = messages.getString("invitationReceivedWithBet").replace("%player%",player1.getName())
                    .replace("%bet%",bet+"");
        }
        msgManager.sendMessage(player1,msgSender,true);
        msgManager.sendMessage(player2,msgReceiver,true);
    }

    // player1 = receiver, who is accepting the invitation
    // player2 = sender, who sent the invitation
    public void acceptInvitation(Player player1, Player player2){
        FileConfiguration messages = plugin.getMessagesConfig();
        MessagesManager msgManager = plugin.getMessagesManager();

        // World check
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        if(mainConfigManager.getDisabledWorlds().contains(player1.getLocation().getWorld().getName())){
            msgManager.sendMessage(player1,messages.getString("worldDisabled"),true);
            return;
        }

        // No invitation received
        Invitation invitationReceived = getInvitation(player2,player1);
        if(invitationReceived == null){
            msgManager.sendMessage(player1,messages.getString("noInvitation")
                    .replace("%player%",player2.getName()),true);
            return;
        }

        // Already playing
        GameManager gameManager = plugin.getGameManager();
        if(gameManager.getGameByPlayer(player1) != null){
            msgManager.sendMessage(player1,messages.getString("alreadyPlaying"),true);
            return;
        }

        // Distance to player
        if(!isValidDistance(player1,player2)){
            msgManager.sendMessage(player1,messages.getString("distanceError"),true);
            return;
        }

        // Bet check for receiver (and sender again)
        double bet = invitationReceived.getBet();
        if(bet != 0){
            Economy vault = plugin.getDependencyManager().getVault();
            if(vault != null){
                if(vault.getBalance(player1) < bet){
                    msgManager.sendMessage(player1,messages.getString("moneyErrorInvitationReceiver")
                            .replace("%money%",bet+""),true);
                    return;
                }
                if(vault.getBalance(player2) < bet){
                    msgManager.sendMessage(player1,messages.getString("moneyErrorInvitationSender")
                            .replace("%player%",player2.getName()),true);
                    return;
                }

                // Remove money
                vault.withdrawPlayer(player1,bet);
                vault.withdrawPlayer(player2,bet);

            }
        }

        removeInvitation(invitationReceived);

        plugin.getGameManager().startStartingPhase(player1,player2,bet);
    }

    // When receiver doesn't accept.
    public void rejectInvitation(Invitation invitation){
        FileConfiguration messages = plugin.getMessagesConfig();
        MessagesManager msgManager = plugin.getMessagesManager();

        msgManager.sendMessage(invitation.getSender(),messages.getString("invitationRejected")
                .replace("%player%",invitation.getReceiver().getName()),true);

        removeInvitation(invitation);
    }

    // When receiver leaves the server.
    public void manageInvitations(Player player){
        // Reject received invitations
        ArrayList<Invitation> allInvitations = getInvitationsByReceiver(player);
        for(Invitation i : allInvitations){
            rejectInvitation(i);
        }

        // Cancel sent invitation
        Invitation i = getInvitationBySender(player);
        if(i != null){
            removeInvitation(i);
        }
    }

    private boolean isValidDistance(Player player1, Player player2){
        double maxDistance = plugin.getConfigsManager().getMainConfigManager().getInvitationMaxDistance();
        Location l1 = player1.getLocation();
        Location l2 = player2.getLocation();
        if(!l1.getWorld().equals(l2.getWorld())){
            return false;
        }

        return l1.distance(l2) <= maxDistance;
    }
}

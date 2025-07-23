package pz.ajneb97.managers;

import org.bukkit.entity.Player;
import pz.ajneb97.Pazaak;
import pz.ajneb97.model.data.PlayerData;
import pz.ajneb97.utils.OtherUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private Pazaak plugin;
    private Map<UUID, PlayerData> players;
    private Map<String,UUID> playerNames;
    private PlayerDataBackupManager playerDataBackupManager;

    public PlayerDataManager(Pazaak plugin){
        this.plugin = plugin;
        this.playerNames = new HashMap<>();
        this.playerDataBackupManager = new PlayerDataBackupManager(plugin);
    }

    public Map<UUID,PlayerData> getPlayers() {
        return players;
    }

    public void setPlayers(Map<UUID,PlayerData> players) {
        this.players = players;
        for(Map.Entry<UUID, PlayerData> entry : players.entrySet()){
            playerNames.put(entry.getValue().getName(),entry.getKey());
        }
    }

    public void addPlayer(PlayerData p){
        players.put(p.getUuid(),p);
        playerNames.put(p.getName(), p.getUuid());
    }

    public PlayerData getPlayer(Player player, boolean create){
        PlayerData playerData = players.get(player.getUniqueId());
        if(playerData == null && create){
            playerData = new PlayerData(player.getUniqueId(),player.getName());
            addPlayer(playerData);
        }
        return playerData;
    }

    private void updatePlayerName(String oldName,String newName,UUID uuid){
        playerNames.remove(oldName);
        playerNames.put(newName,uuid);
    }

    public PlayerData getPlayerByUUID(UUID uuid){
        return players.get(uuid);
    }

    private UUID getPlayerUUID(String name){
        return playerNames.get(name);
    }

    public PlayerData getPlayerByName(String name){
        UUID uuid = getPlayerUUID(name);
        return players.get(uuid);
    }

    public void removePlayerByUUID(UUID uuid){
        players.remove(uuid);
    }

    public void setJoinPlayerData(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData != null){
            if(playerData.getName() == null || !playerData.getName().equals(player.getName())){
                updatePlayerName(playerData.getName(),player.getName(),player.getUniqueId());
                playerData.setName(player.getName());
                playerData.setModified(true);
            }
        }

        playerDataBackupManager.restorePlayerDataBackup(player,true);
    }

    public int getWins(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData != null){
            return playerData.getWins();
        }
        return 0;
    }

    public int getLoses(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData != null){
            return playerData.getLoses();
        }
        return 0;
    }

    public String getTimePlayed(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData != null){
            return OtherUtils.getTimeFormat2(playerData.getMillisPlayed()/1000,plugin.getMessagesManager());
        }
        return OtherUtils.getTimeFormat2(0,plugin.getMessagesManager());
    }

    public void endGame(Player player,boolean win,long millisPlayed){
        PlayerData playerData = getPlayer(player,true);
        if(win){
            playerData.setWins(playerData.getWins()+1);
        }else{
            playerData.setLoses(playerData.getLoses()+1);
        }
        playerData.setMillisPlayed(playerData.getMillisPlayed()+millisPlayed);
        playerData.setModified(true);
    }

    public PlayerDataBackupManager getPlayerDataBackupManager() {
        return playerDataBackupManager;
    }
}

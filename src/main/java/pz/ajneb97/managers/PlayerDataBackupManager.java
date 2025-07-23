package pz.ajneb97.managers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pz.ajneb97.Pazaak;
import pz.ajneb97.model.data.PlayerDataBackup;
import java.util.Map;
import java.util.UUID;

public class PlayerDataBackupManager {
    private Pazaak plugin;
    private Map<UUID, PlayerDataBackup> players;

    public PlayerDataBackupManager(Pazaak plugin){
        this.plugin = plugin;
    }

    public Map<UUID, PlayerDataBackup> getPlayers() {
        return players;
    }

    public void setPlayers(Map<UUID, PlayerDataBackup> players) {
        this.players = players;
    }

    public PlayerDataBackup getPlayer(Player player){
        return players.get(player.getUniqueId());
    }

    public void restorePlayerDataBackup(Player player, boolean async){
        PlayerDataBackup playerDataBackup = getPlayer(player);
        if(playerDataBackup == null){
            return;
        }

        player.getInventory().setContents(playerDataBackup.getInventory());

        players.remove(player.getUniqueId());

        if(async){
            new BukkitRunnable(){
                @Override
                public void run() {
                    plugin.getConfigsManager().getPlayersConfigManager().saveBackupConfig(player.getUniqueId().toString(),null);
                }
            }.runTaskAsynchronously(plugin);
        }else{
            plugin.getConfigsManager().getPlayersConfigManager().saveBackupConfig(player.getUniqueId().toString(),null);
        }
    }

    public void createPlayerDataBackup(Player player){
        PlayerDataBackup playerDataBackup = new PlayerDataBackup(player.getInventory().getContents().clone());
        players.put(player.getUniqueId(),playerDataBackup);
        new BukkitRunnable(){
            @Override
            public void run() {
                plugin.getConfigsManager().getPlayersConfigManager().saveBackupConfig(player.getUniqueId().toString(),playerDataBackup);
            }
        }.runTaskAsynchronously(plugin);
    }
}

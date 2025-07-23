package pz.ajneb97.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pz.ajneb97.Pazaak;
import pz.ajneb97.configs.model.CommonConfig;
import pz.ajneb97.model.data.PlayerData;
import pz.ajneb97.model.data.PlayerDataBackup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayersConfigManager extends DataFolderConfigManager{


    public PlayersConfigManager(Pazaak plugin, String folderName) {
        super(plugin, folderName);
    }

    @Override
    public void createFiles() {

    }

    @Override
    public void loadConfigs(){
        Map<UUID, PlayerData> players = new HashMap<>();
        Map<UUID, PlayerDataBackup> playersBackup = new HashMap<>();

        ArrayList<CommonConfig> configFiles = getConfigs();
        for(CommonConfig commonConfig : configFiles){
            FileConfiguration config = commonConfig.getConfig();
            String uuidString = commonConfig.getPath().replace(".yml", "");
            String name = config.getString("name");
            int wins = config.getInt("wins");
            int loses = config.getInt("loses");
            long millisPlayed = config.getLong("millis_played");

            PlayerDataBackup backup = null;
            if(config.contains("backup")){
                ItemStack[] inventory = new ItemStack[41];
                if(config.contains("backup.inventory")){
                    for(String key : config.getConfigurationSection("backup.inventory").getKeys(false)){
                        inventory[Integer.parseInt(key)] = config.getItemStack("backup.inventory."+key);
                    }
                }

                backup = new PlayerDataBackup(inventory);
            }

            UUID uuid = UUID.fromString(uuidString);
            PlayerData playerData = new PlayerData(uuid,name);
            playerData.setWins(wins);
            playerData.setLoses(loses);
            playerData.setMillisPlayed(millisPlayed);

            if(backup != null){
                playersBackup.put(uuid,backup);
            }

            players.put(uuid,playerData);
        }

        plugin.getPlayerDataManager().setPlayers(players);
        plugin.getPlayerDataManager().getPlayerDataBackupManager().setPlayers(playersBackup);
    }

    public void saveConfig(PlayerData playerData){
        String playerName = playerData.getName();
        CommonConfig playerConfig = getConfigFile(playerData.getUuid()+".yml");
        FileConfiguration config = playerConfig.getConfig();

        config.set("name", playerName);
        config.set("wins",playerData.getWins());
        config.set("loses",playerData.getLoses());
        config.set("millis_played",playerData.getMillisPlayed());

        playerConfig.saveConfig();
    }

    public void saveBackupConfig(String uuid,PlayerDataBackup playerDataBackup){
        CommonConfig playerConfig = getConfigFile(uuid+".yml");
        FileConfiguration config = playerConfig.getConfig();

        config.set("backup",null);
        if(playerDataBackup != null){
            ItemStack[] items = playerDataBackup.getInventory();
            for(int i=0;i<items.length;i++){
                config.set("backup.inventory."+i,items[i]);
            }
        }

        playerConfig.saveConfig();
    }

    @Override
    public void saveConfigs(){
        Map<UUID, PlayerData> players = plugin.getPlayerDataManager().getPlayers();
        for(Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
            PlayerData playerData = entry.getValue();
            if(playerData.isModified()){
                saveConfig(playerData);
            }
            playerData.setModified(false);
        }
    }

}

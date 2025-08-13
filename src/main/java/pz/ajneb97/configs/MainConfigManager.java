package pz.ajneb97.configs;


import org.bukkit.configuration.file.FileConfiguration;
import pz.ajneb97.Pazaak;
import pz.ajneb97.configs.model.CommonConfig;
import pz.ajneb97.configs.model.SoundConfig;
import pz.ajneb97.configs.model.SoundsConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainConfigManager {

    private Pazaak plugin;
    private CommonConfig configFile;
    private boolean updateNotify;
    private int invitationExpireTime;
    private double invitationMaxDistance;
    private boolean automaticStand;
    private int timeInEachTurn;
    private List<String> disabledWorlds;
    private int playerDataSave;
    private SoundsConfig soundsConfig;
    private boolean betSystemEnabled;

    public MainConfigManager(Pazaak plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("config.yml",plugin,null,false);
        configFile.registerConfig();
        checkUpdate();
    }


    public void configure() {
        FileConfiguration config = configFile.getConfig();
        updateNotify = config.getBoolean("update_notify");
        invitationExpireTime = config.getInt("invitation_expire_time");
        invitationMaxDistance = config.getDouble("invitation_max_distance");
        automaticStand = config.getBoolean("automatic_stand");
        timeInEachTurn = config.getInt("time_in_each_turn");
        disabledWorlds = config.getStringList("disabled_worlds");
        playerDataSave = config.getInt("player_data_save");
        soundsConfig = new SoundsConfig(
                configureSound(config,"sounds.end_turn"),
                configureSound(config,"sounds.generate_card"),
                configureSound(config,"sounds.lose_round"),
                configureSound(config,"sounds.win_round"),
                configureSound(config,"sounds.tie_round"),
                configureSound(config,"sounds.lose_game"),
                configureSound(config,"sounds.win_game"),
                configureSound(config,"sounds.add_card"),
                configureSound(config,"sounds.stand")
        );
        betSystemEnabled = config.getBoolean("bet_system.enabled");
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    private SoundConfig configureSound(FileConfiguration config, String path){
        boolean enabled = config.getBoolean(path+".enabled");
        String soundString = config.getString(path+".sound");
        String[] soundStringSep = soundString.split(";");
        return new SoundConfig(enabled,
                soundStringSep[0],Float.parseFloat(soundStringSep[1]),Float.parseFloat(soundStringSep[2])
        );
    }

    public void checkUpdate(){
        Path pathConfig = Paths.get(configFile.getRoute());
        try{
            String text = new String(Files.readAllBytes(pathConfig));
            FileConfiguration config = getConfig();
            if(!text.contains("bet_system")){
                config.set("bet_system.enabled",false);
                configFile.saveConfig();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public CommonConfig getConfigFile() {
        return configFile;
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }

    public Pazaak getPlugin() {
        return plugin;
    }

    public boolean isUpdateNotify() {
        return updateNotify;
    }

    public int getInvitationExpireTime() {
        return invitationExpireTime;
    }

    public double getInvitationMaxDistance() {
        return invitationMaxDistance;
    }

    public boolean isAutomaticStand() {
        return automaticStand;
    }

    public int getTimeInEachTurn() {
        return timeInEachTurn;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public SoundsConfig getSoundsConfig() {
        return soundsConfig;
    }

    public int getPlayerDataSave() {
        return playerDataSave;
    }

    public boolean isBetSystemEnabled() {
        return betSystemEnabled;
    }
}

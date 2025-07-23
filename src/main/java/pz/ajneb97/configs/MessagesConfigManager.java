package pz.ajneb97.configs;

import org.bukkit.configuration.file.FileConfiguration;
import pz.ajneb97.Pazaak;
import pz.ajneb97.configs.model.CommonConfig;
import pz.ajneb97.managers.MessagesManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessagesConfigManager {

    private Pazaak plugin;
    private CommonConfig configFile;

    public MessagesConfigManager(Pazaak plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("messages.yml",plugin,null,false);
        configFile.registerConfig();
        checkUpdates();
    }

    public void configure() {
        FileConfiguration config = configFile.getConfig();

        MessagesManager messagesManager = new MessagesManager();
        messagesManager.setPrefix(config.getString("prefix"));
        messagesManager.setTimeSeconds(config.getString("seconds"));
        messagesManager.setTimeMinutes(config.getString("minutes"));
        messagesManager.setTimeHours(config.getString("hours"));
        messagesManager.setTimeDays(config.getString("days"));

        plugin.setMessagesManager(messagesManager);
    }

    public CommonConfig getConfigFile() {
        return configFile;
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    public void checkUpdates(){
        Path pathConfig = Paths.get(configFile.getRoute());
        try{
            String text = new String(Files.readAllBytes(pathConfig));
            FileConfiguration config = configFile.getConfig();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }
}

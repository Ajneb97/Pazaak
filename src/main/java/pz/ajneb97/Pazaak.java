package pz.ajneb97;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pz.ajneb97.api.ExpansionPazaak;
import pz.ajneb97.api.PazaakAPI;
import pz.ajneb97.commands.MainCommand;
import pz.ajneb97.configs.ConfigsManager;
import pz.ajneb97.listeners.PlayerListener;
import pz.ajneb97.managers.*;
import pz.ajneb97.model.internal.UpdateCheckerResult;
import pz.ajneb97.tasks.PlayerDataSaveTask;
import pz.ajneb97.utils.ServerVersion;
import pz.ajneb97.versions.NMSItemManager;
import pz.ajneb97.versions.NMSManager;

public class Pazaak extends JavaPlugin {

    PluginDescriptionFile pdfFile = getDescription();
    public String version = pdfFile.getVersion();
    public static String prefix;
    public static ServerVersion serverVersion;

    private ConfigsManager configsManager;
    private MessagesManager messagesManager;
    private PlayerDataManager playerDataManager;
    private CommonItemManager commonItemManager;
    private InventoryManager inventoryManager;
    private DependencyManager dependencyManager;
    private GameManager gameManager;
    private InvitationManager invitationManager;
    private NMSItemManager nmsItemManager;
    private NMSManager nmsManager;
    private PlayerDataSaveTask playerDataSaveTask;
    private UpdateCheckerManager updateCheckerManager;

    public void onEnable(){
        setVersion();
        setPrefix();
        this.playerDataManager = new PlayerDataManager(this);
        this.commonItemManager = new CommonItemManager(this);
        this.gameManager = new GameManager(this);
        this.invitationManager = new InvitationManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.nmsItemManager = new NMSItemManager(this);
        this.nmsManager = new NMSManager(this);
        this.dependencyManager = new DependencyManager(this);

        this.configsManager = new ConfigsManager(this);
        this.configsManager.configure();

        registerEvents();
        registerCommands();

        reloadPlayerDataSaveTask();

        PazaakAPI api = new PazaakAPI(this);
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new ExpansionPazaak(this).register();
        }

        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&eHas been enabled! &fVersion: "+version));
        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&eThanks for using my plugin!   &f~Ajneb97"));

        updateCheckerManager = new UpdateCheckerManager(version);
        updateMessage(updateCheckerManager.check());
    }

    public void onDisable(){
        configsManager.getPlayersConfigManager().saveConfigs();
        gameManager.endAllGames();
        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&eHas been disabled! &fVersion: "+version));
    }
    public void registerCommands(){
        this.getCommand("pazaak").setExecutor(new MainCommand(this));
    }

    public void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
    }

    public void setPrefix(){
        prefix = MessagesManager.getColoredMessage("&3[&9Pazaak&3] ");
    }

    public void setVersion(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        switch(bukkitVersion){
            case "1.20.5":
            case "1.20.6":
                serverVersion = ServerVersion.v1_20_R4;
                break;
            case "1.21":
            case "1.21.1":
                serverVersion = ServerVersion.v1_21_R1;
                break;
            case "1.21.2":
            case "1.21.3":
                serverVersion = ServerVersion.v1_21_R2;
                break;
            case "1.21.4":
                serverVersion = ServerVersion.v1_21_R3;
                break;
            case "1.21.5":
                serverVersion = ServerVersion.v1_21_R4;
                break;
            case "1.21.6":
            case "1.21.7":
            case "1.21.8":
                serverVersion = ServerVersion.v1_21_R5;
                break;
            default:
                try{
                    serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
                }catch(Exception e){
                    serverVersion = ServerVersion.v1_21_R5;
                }
        }
    }

    public void reloadPlayerDataSaveTask() {
        if(playerDataSaveTask != null) {
            playerDataSaveTask.end();
        }
        playerDataSaveTask = new PlayerDataSaveTask(this);
        playerDataSaveTask.start(configsManager.getMainConfigManager().getPlayerDataSave());
    }

    public ConfigsManager getConfigsManager() {
        return configsManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void setMessagesManager(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    public FileConfiguration getMessagesConfig(){
        return configsManager.getMessagesConfigManager().getConfigFile().getConfig();
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public CommonItemManager getCommonItemManager() {
        return commonItemManager;
    }

    public NMSItemManager getNmsItemManager() {
        return nmsItemManager;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public InvitationManager getInvitationManager() {
        return invitationManager;
    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }

    public UpdateCheckerManager getUpdateCheckerManager() {
        return updateCheckerManager;
    }

    public void updateMessage(UpdateCheckerResult result){
        if(!result.isError()){
            String latestVersion = result.getLatestVersion();
            if(latestVersion != null){
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cThere is a new version available. &e(&7"+latestVersion+"&e)"));
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cYou can download it at: &fhttps://www.spigotmc.org/resources/57428/"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+" &cError while checking update."));
        }

    }
}

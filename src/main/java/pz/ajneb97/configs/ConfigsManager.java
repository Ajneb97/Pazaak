package pz.ajneb97.configs;


import pz.ajneb97.Pazaak;

public class ConfigsManager {

	private Pazaak plugin;
	private MainConfigManager mainConfigManager;
	private MessagesConfigManager messagesConfigManager;
	private InventoryConfigManager inventoryConfigManager;
	private PlayersConfigManager playersConfigManager;
	
	public ConfigsManager(Pazaak plugin) {
		this.plugin = plugin;
		this.mainConfigManager = new MainConfigManager(plugin);
		this.messagesConfigManager = new MessagesConfigManager(plugin);
		this.inventoryConfigManager = new InventoryConfigManager(plugin);
		this.playersConfigManager = new PlayersConfigManager(plugin,"players");
	}
	
	public void configure() {
		mainConfigManager.configure();
		messagesConfigManager.configure();
		inventoryConfigManager.configure();
		playersConfigManager.configure();
	}

	public MainConfigManager getMainConfigManager() {
		return mainConfigManager;
	}

	public MessagesConfigManager getMessagesConfigManager() {
		return messagesConfigManager;
	}

	public PlayersConfigManager getPlayersConfigManager() {
		return playersConfigManager;
	}

	public InventoryConfigManager getInventoryConfigManager() {
		return inventoryConfigManager;
	}

	public boolean reload(){
		if(!messagesConfigManager.reloadConfig()){
			return false;
		}
		if(!mainConfigManager.reloadConfig()){
			return false;
		}
		if(!inventoryConfigManager.reloadConfig()){
			return false;
		}

		plugin.reloadPlayerDataSaveTask();

		return true;
	}
}

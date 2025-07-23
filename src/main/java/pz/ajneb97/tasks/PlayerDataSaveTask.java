package pz.ajneb97.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import pz.ajneb97.Pazaak;

public class PlayerDataSaveTask {

	private Pazaak plugin;
	private boolean end;
	public PlayerDataSaveTask(Pazaak plugin) {
		this.plugin = plugin;
		this.end = false;
	}
	
	public void end() {
		end = true;
	}
	
	public void start(int seconds) {
		long ticks = seconds* 20L;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(end) {
					this.cancel();
				}else {
					execute();
				}
			}
			
		}.runTaskTimerAsynchronously(plugin, 0L, ticks);
	}
	
	public void execute() {
		plugin.getConfigsManager().getPlayersConfigManager().saveConfigs();
	}
}

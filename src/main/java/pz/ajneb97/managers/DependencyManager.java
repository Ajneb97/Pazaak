package pz.ajneb97.managers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import pz.ajneb97.Pazaak;

public class DependencyManager {

    private Pazaak plugin;

    private boolean isPlaceholderAPI;
    private boolean isPaper;
    private Economy vault;

    public DependencyManager(Pazaak plugin){
        this.plugin = plugin;

        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            isPlaceholderAPI = true;
        }
        try{
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        }catch(Exception e){

        }
        setupVault();
    }

    public boolean isPlaceholderAPI() {
        return isPlaceholderAPI;
    }

    public boolean isPaper() {
        return isPaper;
    }

    private void setupVault() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        vault = rsp.getProvider();
    }

    public Economy getVault() {
        return vault;
    }
}

package pz.ajneb97.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import pz.ajneb97.Pazaak;
import pz.ajneb97.model.game.Invitation;

public class InvitationExpirationTask {
    private Pazaak plugin;
    private Invitation invitation;
    private boolean stop;

    public InvitationExpirationTask(Pazaak plugin, Invitation invitation){
        this.plugin = plugin;
        this.invitation = invitation;
    }

    public void stop(){
        this.stop = true;
    }

    public void start(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(stop){
                    this.cancel();
                }else{
                    execute();
                }
            }
        }.runTaskTimer(plugin,0L,20L);
    }

    public void execute(){
        invitation.setExpireTime(invitation.getExpireTime()-1);
        if(invitation.getExpireTime() <= 0){
            plugin.getInvitationManager().rejectInvitation(invitation);
        }
    }
}

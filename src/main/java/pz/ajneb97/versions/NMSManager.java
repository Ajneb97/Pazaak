package pz.ajneb97.versions;

import org.bukkit.entity.Player;
import pz.ajneb97.Pazaak;
import pz.ajneb97.managers.MessagesManager;
import pz.ajneb97.utils.ServerVersion;
import pz.ajneb97.versions.classes.*;

import java.lang.reflect.InvocationTargetException;

public class NMSManager {

    private Version versionClasses;
    private ServerVersion serverVersion;
    private Pazaak plugin;

    public NMSManager(Pazaak plugin) {
        this.plugin = plugin;
        this.versionClasses = new Version();
        this.serverVersion = Pazaak.serverVersion;

        if(serverVersionGreaterEqualThan(ServerVersion.v1_19_R3)){
            return;
        }

        try {
            versionClasses.addClass(NMSClassType.CONTAINER,new NMSClassContainer(serverVersion));
            versionClasses.addClass(NMSClassType.CONTAINERS,new NMSClassContainers(serverVersion));
            versionClasses.addClass(NMSClassType.PACKET,new NMSClassPacket(serverVersion));
            versionClasses.addClass(NMSClassType.PACKET_PLAY_OUT_OPEN_WINDOW,new NMSClassPacketPlayOutOpenWindow(serverVersion));
            versionClasses.addClass(NMSClassType.CRAFT_PLAYER,new NMSClassCraftPlayer(serverVersion));
            versionClasses.addClass(NMSClassType.CRAFT_CHAT_MESSAGE,new NMSClassCraftChatMessage(serverVersion));
            versionClasses.addClass(NMSClassType.I_CHAT_BASE_COMPONENT,new NMSClassIChatBaseComponent(serverVersion));
            versionClasses.addClass(NMSClassType.ENTITY_PLAYER,new NMSClassEntityPlayer(serverVersion,versionClasses));
            versionClasses.addClass(NMSClassType.PLAYER_CONNECTION,new NMSClassPlayerConnection(serverVersion,versionClasses));
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    // 1.16.5 - 1.19.3
    public void setOpenInventoryTitle(Player player, String title){
        NMSClassPlayerConnection nmsClassPlayerConnection = (NMSClassPlayerConnection) versionClasses.getClass(NMSClassType.PLAYER_CONNECTION);
        NMSClassContainer nmsClassContainer = (NMSClassContainer) versionClasses.getClass(NMSClassType.CONTAINER);
        NMSClassContainers nmsClassContainers = (NMSClassContainers) versionClasses.getClass(NMSClassType.CONTAINERS);
        NMSClassCraftPlayer nmsClassCraftPlayer = (NMSClassCraftPlayer) versionClasses.getClass(NMSClassType.CRAFT_PLAYER);
        NMSClassEntityPlayer nmsClassEntityPlayer = (NMSClassEntityPlayer) versionClasses.getClass(NMSClassType.ENTITY_PLAYER);
        NMSClassCraftChatMessage nmsClassCraftChatMessage = (NMSClassCraftChatMessage) versionClasses.getClass(NMSClassType.CRAFT_CHAT_MESSAGE);
        NMSClassIChatBaseComponent nmsClassIChatBaseComponent = (NMSClassIChatBaseComponent) versionClasses.getClass(NMSClassType.I_CHAT_BASE_COMPONENT);
        NMSClassPacketPlayOutOpenWindow nmsClassPacketPlayOutOpenWindow = (NMSClassPacketPlayOutOpenWindow) versionClasses.getClass(NMSClassType.PACKET_PLAY_OUT_OPEN_WINDOW);

        try {
            Object craftPlayer = nmsClassCraftPlayer.getClassReference().cast(player);
            Object entityPlayer = nmsClassCraftPlayer.getGetHandle().invoke(craftPlayer);
            Object invTitle = nmsClassCraftChatMessage.getFromStringOrNull().invoke(null, title);
            Object activeContainer = nmsClassEntityPlayer.getActiveContainer().get(entityPlayer);
            int windowId = (int)nmsClassContainer.getWindowId().get(activeContainer);
            Object generic9x6 = nmsClassContainers.getGeneric9x6().get(null);

            Object packet = nmsClassPacketPlayOutOpenWindow.getClassReference().getConstructor(
                int.class,nmsClassContainers.getClassReference(),nmsClassIChatBaseComponent.getClassReference()
            ).newInstance(
                windowId,generic9x6,invTitle
            );

            Object connection = nmsClassEntityPlayer.getPlayerConnection().get(entityPlayer);
            nmsClassPlayerConnection.getSendPacket().invoke(connection,packet);

            if(!serverVersionGreaterEqualThan(ServerVersion.v1_17_R1)){
                nmsClassEntityPlayer.getUpdateInventory().invoke(entityPlayer,activeContainer);
            }else{
                player.updateInventory();
            }


        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private boolean serverVersionGreaterEqualThan(ServerVersion version){
        return serverVersion.ordinal() >= version.ordinal();
    }
}

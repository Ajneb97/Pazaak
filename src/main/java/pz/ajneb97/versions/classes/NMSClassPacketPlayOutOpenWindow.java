package pz.ajneb97.versions.classes;

import pz.ajneb97.utils.ServerVersion;

public class NMSClassPacketPlayOutOpenWindow extends NMSClass{

    public NMSClassPacketPlayOutOpenWindow(ServerVersion serverVersion) throws ClassNotFoundException {
        if(serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_17_R1)){
            classReference = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutOpenWindow");
        }else{
            classReference = Class.forName("net.minecraft.server."+serverVersion+".PacketPlayOutOpenWindow");
        }
    }

}

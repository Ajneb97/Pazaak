package pz.ajneb97.versions.classes;


import pz.ajneb97.utils.ServerVersion;

public class NMSClassPacket extends NMSClass{

    public NMSClassPacket(ServerVersion serverVersion) throws ClassNotFoundException, NoSuchMethodException {
        if(serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_17_R1)){
            classReference = Class.forName("net.minecraft.network.protocol.Packet");
        }else{
            classReference = Class.forName("net.minecraft.server."+serverVersion+".Packet");
        }
    }

}

package pz.ajneb97.versions.classes;

import pz.ajneb97.utils.ServerVersion;

public class NMSClassIChatBaseComponent extends NMSClass{

    public NMSClassIChatBaseComponent(ServerVersion serverVersion) throws ClassNotFoundException, NoSuchMethodException {
        if(serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_17_R1)){
            classReference = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
        }else{
            classReference = Class.forName("net.minecraft.server."+serverVersion+".IChatBaseComponent");
        }
    }
}

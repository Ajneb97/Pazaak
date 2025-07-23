package pz.ajneb97.versions.classes;

import pz.ajneb97.utils.ServerVersion;
import pz.ajneb97.versions.NMSClassType;
import pz.ajneb97.versions.Version;

import java.lang.reflect.Method;

public class NMSClassPlayerConnection extends NMSClass{

    private Method sendPacket;

    public NMSClassPlayerConnection(ServerVersion serverVersion, Version versionClasses) throws ClassNotFoundException, NoSuchMethodException {
        if(serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_17_R1)){
            classReference = Class.forName("net.minecraft.server.network.PlayerConnection");

            switch(serverVersion){
                case v1_17_R1:
                    sendPacket = classReference.getMethod("sendPacket",versionClasses.getClass(NMSClassType.PACKET).getClassReference());
                    break;
                case v1_18_R1:
                case v1_18_R2:
                case v1_19_R1:
                case v1_19_R2:
                    sendPacket = classReference.getMethod("a",versionClasses.getClass(NMSClassType.PACKET).getClassReference());
                    break;
            }
        }else{
            classReference = Class.forName("net.minecraft.server."+serverVersion+".PlayerConnection");
            sendPacket = classReference.getMethod("sendPacket",versionClasses.getClass(NMSClassType.PACKET).getClassReference());
        }
    }

    public Method getSendPacket() {
        return sendPacket;
    }
}

package pz.ajneb97.versions.classes;


import pz.ajneb97.utils.ServerVersion;

import java.lang.reflect.Field;

public class NMSClassContainers extends NMSClass{
    private Field generic9x6;

    public NMSClassContainers(ServerVersion serverVersion) throws ClassNotFoundException, NoSuchFieldException {
        if(serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_17_R1)){
            classReference = Class.forName("net.minecraft.world.inventory.Containers");
            generic9x6 = classReference.getField("f");
        }else{
            classReference = Class.forName("net.minecraft.server."+serverVersion+".Containers");
            generic9x6 = classReference.getField("GENERIC_9X6");
        }
    }

    public Field getGeneric9x6() {
        return generic9x6;
    }
}

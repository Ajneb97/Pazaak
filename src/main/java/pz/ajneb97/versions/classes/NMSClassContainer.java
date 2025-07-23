package pz.ajneb97.versions.classes;


import pz.ajneb97.utils.ServerVersion;

import java.lang.reflect.Field;

public class NMSClassContainer extends NMSClass{
    private Field windowId;

    public NMSClassContainer(ServerVersion serverVersion) throws ClassNotFoundException, NoSuchFieldException {
        if(serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_17_R1)){
            classReference = Class.forName("net.minecraft.world.inventory.Container");
            windowId = classReference.getField("j");
        }else{
            classReference = Class.forName("net.minecraft.server."+serverVersion+".Container");
            windowId = classReference.getField("windowId");
        }
    }

    public Field getWindowId() {
        return windowId;
    }
}

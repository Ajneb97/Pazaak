package pz.ajneb97.versions.classes;

import pz.ajneb97.utils.ServerVersion;

import java.lang.reflect.Method;

public class NMSClassCraftPlayer extends NMSClass{

    private Method getHandle;

    public NMSClassCraftPlayer(ServerVersion serverVersion) throws ClassNotFoundException, NoSuchMethodException {
        classReference = Class.forName("org.bukkit.craftbukkit."+serverVersion+".entity.CraftPlayer");
        getHandle = classReference.getMethod("getHandle");
    }

    public Method getGetHandle() {
        return getHandle;
    }
}

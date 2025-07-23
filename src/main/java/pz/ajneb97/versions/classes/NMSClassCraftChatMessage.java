package pz.ajneb97.versions.classes;

import pz.ajneb97.utils.ServerVersion;

import java.lang.reflect.Method;

public class NMSClassCraftChatMessage extends NMSClass{

    private Method fromStringOrNull;
    public NMSClassCraftChatMessage(ServerVersion serverVersion) throws ClassNotFoundException, NoSuchMethodException {
        classReference = Class.forName("org.bukkit.craftbukkit."+serverVersion+".util.CraftChatMessage");
        fromStringOrNull = classReference.getMethod("fromStringOrNull",String.class);
    }

    public Method getFromStringOrNull() {
        return fromStringOrNull;
    }
}

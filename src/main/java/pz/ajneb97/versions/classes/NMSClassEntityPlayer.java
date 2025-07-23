package pz.ajneb97.versions.classes;


import pz.ajneb97.utils.ServerVersion;
import pz.ajneb97.versions.NMSClassType;
import pz.ajneb97.versions.Version;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSClassEntityPlayer extends NMSClass{
    private Field playerConnection;
    private Field activeContainer;
    private Method updateInventory;

    public NMSClassEntityPlayer(ServerVersion serverVersion,Version versionClasses) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
        if(serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_17_R1)){
            classReference = Class.forName("net.minecraft.server.level.EntityPlayer");
            playerConnection = classReference.getField("b");

            switch(serverVersion){
                case v1_17_R1:
                    activeContainer = classReference.getField("bV");
                    break;
                case v1_18_R1:
                    activeContainer = classReference.getField("bW");
                    break;
                case v1_18_R2:
                    activeContainer = classReference.getField("bV");
                    break;
                case v1_19_R1:
                case v1_19_R2:
                    activeContainer = classReference.getField("bU");
                    break;
            }
        }else{
            classReference = Class.forName("net.minecraft.server."+serverVersion+".EntityPlayer");
            playerConnection = classReference.getField("playerConnection");
            activeContainer = classReference.getField("activeContainer");
            updateInventory = classReference.getMethod("updateInventory",versionClasses.getClass(NMSClassType.CONTAINER).getClassReference());
        }
    }

    public Field getPlayerConnection() {
        return playerConnection;
    }

    public Field getActiveContainer() {
        return activeContainer;
    }

    public Method getUpdateInventory() {
        return updateInventory;
    }
}

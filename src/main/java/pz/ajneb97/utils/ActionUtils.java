package pz.ajneb97.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pz.ajneb97.configs.model.SoundConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActionUtils {
    public static void playSoundFromConfig(Player player, SoundConfig soundConfig){
        if(!soundConfig.isEnabled()){
            return;
        }

        try {
            Sound sound = getSoundByName(soundConfig.getSound());
            player.playSound(player.getLocation(), sound, soundConfig.getVolume(), soundConfig.getPitch());
        }catch(Exception e ) {
            // Try from resource pack
            player.playSound(player.getLocation(), soundConfig.getSound(), soundConfig.getVolume(), soundConfig.getPitch());
        }
    }

    private static Sound getSoundByName(String name){
        try {
            Class<?> soundTypeClass = Class.forName("org.bukkit.Sound");
            Method valueOf = soundTypeClass.getMethod("valueOf", String.class);
            return (Sound) valueOf.invoke(null,name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

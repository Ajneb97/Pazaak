package pz.ajneb97.configs.model;


public class SoundConfig {
    private boolean enabled;
    private String sound;
    private float volume;
    private float pitch;

    public SoundConfig(boolean enabled, String sound, float volume, float pitch) {
        this.enabled = enabled;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}

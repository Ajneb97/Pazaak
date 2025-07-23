package pz.ajneb97.configs.model;

public class SoundsConfig {
    private SoundConfig endTurnSound;
    private SoundConfig generateCardSound;
    private SoundConfig loseRoundSound;
    private SoundConfig winRoundSound;
    private SoundConfig tieRoundSound;
    private SoundConfig loseGameSound;
    private SoundConfig winGameSound;
    private SoundConfig addCardSound;
    private SoundConfig standSound;

    public SoundsConfig(SoundConfig endTurnSound, SoundConfig generateCardSound, SoundConfig loseRoundSound,
                        SoundConfig winRoundSound, SoundConfig tieRoundSound, SoundConfig loseGameSound,
                        SoundConfig winGameSound, SoundConfig addCardSound, SoundConfig standSound) {
        this.endTurnSound = endTurnSound;
        this.generateCardSound = generateCardSound;
        this.loseRoundSound = loseRoundSound;
        this.winRoundSound = winRoundSound;
        this.loseGameSound = loseGameSound;
        this.winGameSound = winGameSound;
        this.tieRoundSound = tieRoundSound;
        this.addCardSound = addCardSound;
        this.standSound = standSound;
    }

    public SoundConfig getEndTurnSound() {
        return endTurnSound;
    }

    public void setEndTurnSound(SoundConfig endTurnSound) {
        this.endTurnSound = endTurnSound;
    }

    public SoundConfig getGenerateCardSound() {
        return generateCardSound;
    }

    public void setGenerateCardSound(SoundConfig generateCardSound) {
        this.generateCardSound = generateCardSound;
    }

    public SoundConfig getLoseRoundSound() {
        return loseRoundSound;
    }

    public void setLoseRoundSound(SoundConfig loseRoundSound) {
        this.loseRoundSound = loseRoundSound;
    }

    public SoundConfig getWinRoundSound() {
        return winRoundSound;
    }

    public void setWinRoundSound(SoundConfig winRoundSound) {
        this.winRoundSound = winRoundSound;
    }

    public SoundConfig getTieRoundSound() {
        return tieRoundSound;
    }

    public void setTieRoundSound(SoundConfig tieRoundSound) {
        this.tieRoundSound = tieRoundSound;
    }

    public SoundConfig getLoseGameSound() {
        return loseGameSound;
    }

    public void setLoseGameSound(SoundConfig loseGameSound) {
        this.loseGameSound = loseGameSound;
    }

    public SoundConfig getWinGameSound() {
        return winGameSound;
    }

    public void setWinGameSound(SoundConfig winGameSound) {
        this.winGameSound = winGameSound;
    }

    public SoundConfig getAddCardSound() {
        return addCardSound;
    }

    public void setAddCardSound(SoundConfig addCardSound) {
        this.addCardSound = addCardSound;
    }

    public SoundConfig getStandSound() {
        return standSound;
    }

    public void setStandSound(SoundConfig standSound) {
        this.standSound = standSound;
    }
}

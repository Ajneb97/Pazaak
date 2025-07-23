package pz.ajneb97.model.inventory;

import pz.ajneb97.model.items.CommonItem;

import java.util.Map;

public class CustomItems {
    private CommonItem standRemainingSpace;
    private CommonItem totalPoints;
    private Map<String,CommonItem> normalCards;
    private Map<String,CommonItem> bonusCards;
    private CommonItem unknownBonusCard;

    public CustomItems(CommonItem standRemainingSpace, CommonItem totalPoints,
                       Map<String, CommonItem> normalCards, Map<String, CommonItem> bonusCards, CommonItem unknownBonusCard) {
        this.standRemainingSpace = standRemainingSpace;
        this.totalPoints = totalPoints;
        this.normalCards = normalCards;
        this.bonusCards = bonusCards;
        this.unknownBonusCard = unknownBonusCard;
    }

    public CommonItem getStandRemainingSpace() {
        return standRemainingSpace;
    }

    public void setStandRemainingSpace(CommonItem standRemainingSpace) {
        this.standRemainingSpace = standRemainingSpace;
    }

    public CommonItem getTotalPoints() {
        return totalPoints;
    }

    public CommonItem getNormalCard(String id){
        if(normalCards.containsKey(id)){
            return normalCards.get(id);
        }
        return null;
    }

    public CommonItem getBonusCard(String id){
        if(bonusCards.containsKey(id)){
            return bonusCards.get(id);
        }
        return null;
    }

    public CommonItem getUnknownBonusCard() {
        return unknownBonusCard;
    }

    public void setUnknownBonusCard(CommonItem unknownBonusCard) {
        this.unknownBonusCard = unknownBonusCard;
    }
}

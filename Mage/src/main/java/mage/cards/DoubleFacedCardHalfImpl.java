package mage.cards;

import mage.MageInt;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.constants.*;
import mage.game.Game;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author JayDi85
 */
public class DoubleFacedCardHalfImpl extends CardImpl implements DoubleFacedCardHalf {

    private DoubleFacedCard parentCard;
    private boolean isFront = false;

    public DoubleFacedCardHalfImpl(
            UUID ownerId, CardSetInfo setInfo,
            SuperType[] cardSuperTypes, CardType[] cardTypes, SubType[] cardSubTypes,
            String costs, SpellAbilityType spellAbilityType
    ) {
        this(
                ownerId, setInfo,
                cardSuperTypes, cardTypes, cardSubTypes,
                costs, spellAbilityType, ""
        );
    }

    public DoubleFacedCardHalfImpl(
            UUID ownerId, CardSetInfo setInfo,
            SuperType[] cardSuperTypes, CardType[] cardTypes, SubType[] cardSubTypes,
            String costs, SpellAbilityType spellAbilityType, String colors
    ) {
        super(ownerId, setInfo, cardTypes, costs, spellAbilityType);
        this.supertype.addAll(Arrays.asList(cardSuperTypes));
        this.subtype.addAll(Arrays.asList(cardSubTypes));
        this.color.addColor(new ObjectColor(colors));
    }

    public DoubleFacedCardHalfImpl(final DoubleFacedCardHalfImpl card) {
        super(card);
        this.parentCard = card.parentCard;
        this.isFront = card.isFront;
    }

    @Override
    public UUID getOwnerId() {
        return parentCard.getOwnerId();
    }

    @Override
    public String getExpansionSetCode() {
        // TODO: own set code?
        return parentCard.getExpansionSetCode();
    }

    @Override
    public String getCardNumber() {
        // TODO: own card number?
        return parentCard.getCardNumber();
    }

    @Override
    public boolean moveToZone(Zone toZone, Ability source, Game game, boolean flag, List<UUID> appliedEffects) {
        return parentCard.moveToZone(toZone, source, game, flag, appliedEffects);
    }

    @Override
    public boolean moveToExile(UUID exileId, String name, Ability source, Game game, List<UUID> appliedEffects) {
        return parentCard.moveToExile(exileId, name, source, game, appliedEffects);
    }

    @Override
    public boolean removeFromZone(Game game, Zone fromZone, Ability source) {
        return parentCard.removeFromZone(game, fromZone, source);
    }

    @Override
    public DoubleFacedCard getMainCard() {
        return parentCard;
    }

    @Override
    public void setZone(Zone zone, Game game) {
        game.setZone(parentCard.getId(), zone);
        game.setZone(parentCard.getLeftHalfCard().getId(), zone);
        game.setZone(parentCard.getRightHalfCard().getId(), zone);
    }

    @Override
    public DoubleFacedCardHalfImpl copy() {
        return new DoubleFacedCardHalfImpl(this);
    }

    @Override
    public void setParentCard(DoubleFacedCard card) {
        this.parentCard = card;
    }

    @Override
    public DoubleFacedCard getParentCard() {
        return this.parentCard;
    }

    @Override
    public void setPT(MageInt power, MageInt toughness) {
        this.power = power;
        this.toughness = toughness;
    }

    @Override
    public String getIdName() {
        // id must send to main card (popup card hint in game logs)
        return getName() + " [" + parentCard.getId().toString().substring(0, 3) + ']';
    }

    @Override
    public void setIsFront(boolean isFront) {
        this.isFront = isFront;
    }

    @Override
    public boolean isFront() {
        return isFront;
    }

    @Override
    public Card getSecondCardFace() {
        return this.getMainCard().getSecondCardFace();
    }

    @Override
    public boolean isTransformable() {
        return parentCard.isTransformable();
    }
}

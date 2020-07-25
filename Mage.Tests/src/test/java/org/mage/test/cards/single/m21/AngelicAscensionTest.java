package org.mage.test.cards.single.m21;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

public class AngelicAscensionTest extends CardTestPlayerBase {


    // Exile target creature or planeswalker. Its controller creates a 4/4 white Angel creature token with flying.

    @Test
    public void exileCreatureOpponent(){
        addCard(Zone.HAND, playerA, "Angelic Ascension");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Angelic Ascension", "Grizzly Bears");
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();
        setStrictChooseMode(true);
        assertAllCommandsUsed();
        assertExileCount(playerB, 1);
        assertPermanentCount(playerB,  "Angel", 1);
        assertPowerToughness(playerB, "Angel", 4, 4);
    }

    @Test
    public void exileOwnCreature(){
        addCard(Zone.HAND, playerA, "Angelic Ascension");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Angelic Ascension", "Grizzly Bears");
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();
        assertAllCommandsUsed();
        assertExileCount(playerA, 1);
        assertPermanentCount(playerA,  "Angel", 1);
        assertPowerToughness(playerA, "Angel", 4, 4);
    }
}

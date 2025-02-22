package mage.cards.v;

import mage.MageInt;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.DiesSourceTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.Cost;
import mage.abilities.costs.common.TapTargetCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.combat.CanAttackAsThoughItDidntHaveDefenderSourceEffect;
import mage.abilities.effects.common.continuous.BoostSourceEffect;
import mage.abilities.keyword.DefenderAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.predicate.permanent.TappedPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.game.stack.StackAbility;
import mage.target.common.TargetControlledPermanent;
import mage.watchers.Watcher;

import java.util.*;

/**
 * @author L_J
 */
public final class VodalianWarMachine extends CardImpl {

    private static final FilterControlledPermanent filter = new FilterControlledPermanent("untapped Merfolk you control");

    static {
        filter.add(TappedPredicate.UNTAPPED);
        filter.add(SubType.MERFOLK.getPredicate());
    }

    public VodalianWarMachine(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{U}{U}");
        this.subtype.add(SubType.WALL);
        this.power = new MageInt(0);
        this.toughness = new MageInt(4);

        // Defender
        this.addAbility(DefenderAbility.getInstance());

        // Tap an untapped Merfolk you control: Vodalian War Machine can attack this turn as though it didn't have defender.
        Ability ability = new SimpleActivatedAbility(new CanAttackAsThoughItDidntHaveDefenderSourceEffect(Duration.EndOfTurn), new TapTargetCost(new TargetControlledPermanent(1, 1, filter, true)));
        this.addAbility(ability);

        // Tap an untapped Merfolk you control: Vodalian War Machine gets +2/+1 until end of turn.
        this.addAbility(new SimpleActivatedAbility(new BoostSourceEffect(2, 1, Duration.EndOfTurn), new TapTargetCost(new TargetControlledPermanent(1, 1, filter, true))));

        // When Vodalian War Machine dies, destroy all Merfolk tapped this turn to pay for its abilities.
        this.addAbility(new DiesSourceTriggeredAbility(new VodalianWarMachineEffect()), new VodalianWarMachineWatcher());
    }

    private VodalianWarMachine(final VodalianWarMachine card) {
        super(card);
    }

    @Override
    public VodalianWarMachine copy() {
        return new VodalianWarMachine(this);
    }
}

class VodalianWarMachineEffect extends OneShotEffect {

    private static final FilterPermanent filter = new FilterPermanent("Merfolk tapped this turn to pay for its abilities");

    static {
        filter.add(SubType.MERFOLK.getPredicate());
    }

    VodalianWarMachineEffect() {
        super(Outcome.Detriment);
        staticText = "destroy all " + filter.getMessage();
    }

    private VodalianWarMachineEffect(final VodalianWarMachineEffect effect) {
        super(effect);
    }

    @Override
    public VodalianWarMachineEffect copy() {
        return new VodalianWarMachineEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourcePermanent = game.getPermanentOrLKIBattlefield(source.getSourceId());
        if (sourcePermanent != null) {
            VodalianWarMachineWatcher watcher = game.getState().getWatcher(VodalianWarMachineWatcher.class);
            if (watcher != null && watcher.getTappedMerfolkIds(sourcePermanent, game) != null) {
                for (Permanent permanent : game.getBattlefield().getActivePermanents(filter, source.getControllerId(), source, game)) {
                    if (watcher.getTappedMerfolkIds(sourcePermanent, game).contains(new MageObjectReference(permanent, game))) {
                        permanent.destroy(source, game, false);
                    }
                }
                return true;
            }
        }
        return false;
    }

}

class VodalianWarMachineWatcher extends Watcher {

    private final Map<MageObjectReference, Set<MageObjectReference>> tappedMerfolkIds = new HashMap<>();

    VodalianWarMachineWatcher() {
        super(WatcherScope.GAME);
    }

    Set<MageObjectReference> getTappedMerfolkIds(Permanent permanent, Game game) {
        return tappedMerfolkIds.get(new MageObjectReference(permanent, game));
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.ACTIVATED_ABILITY) {
            if (event.getSourceId() != null) {
                Permanent sourcePermanent = game.getPermanentOrLKIBattlefield(event.getSourceId());
                if (sourcePermanent != null) {
                    StackAbility stackAbility = (StackAbility) game.getStack().getStackObject(event.getSourceId());
                    if (stackAbility != null) {
                        Ability ability = stackAbility.getStackAbility();
                        if (ability != null) {
                            for (Cost cost : ability.getCosts()) {
                                if (cost instanceof TapTargetCost && cost.isPaid()) {
                                    TapTargetCost tapCost = (TapTargetCost) cost;
                                    if (tapCost.getTarget().isChosen(game)) {
                                        MageObjectReference mor = new MageObjectReference(sourcePermanent.getId(), sourcePermanent.getZoneChangeCounter(game), game);
                                        Set<MageObjectReference> toAdd;
                                        if (tappedMerfolkIds.get(mor) == null) {
                                            toAdd = new HashSet<>();
                                        } else {
                                            toAdd = tappedMerfolkIds.get(mor);
                                        }
                                        for (UUID targetId : tapCost.getTarget().getTargets()) {
                                            toAdd.add(new MageObjectReference(targetId, game));
                                        }
                                        tappedMerfolkIds.put(mor, toAdd);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        tappedMerfolkIds.clear();
    }
}

package agent.strategy;

import java.util.List;
import java.util.Random;

import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Etat;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import jdk.nashorn.internal.codegen.CompilerConstants;

/**
 * Strategie qui renvoit une action aleatoire avec probabilite epsilon, une
 * action gloutonne (qui suit la politique de l'agent) sinon Cette classe a
 * acces a un RLAgent par l'intermediaire de sa classe mere.
 *
 * @author lmatignon
 *
 */
public class StrategyGreedy extends StrategyExploration {
    //TODO
    //...

    private Random rand = new Random();
    private double epsilon = 0.0;

    public StrategyGreedy(RLAgent agent, double epsilon) {
        super(agent);
        //TODO
        this.epsilon = epsilon;

        //...
    }

    /**
     * @return action selectionnee par la strategie d'exploration
     */
    @Override
    public Action getAction(Etat _e) {
        //VOTRE CODE
        List< Action> la = agent.getActionsLegales(_e);
        //Si on ne peut plus bouger alors on est sur un état absorbant
        if (agent.getActionsLegales(_e).isEmpty()) {
            return null;
        }
        //Soit on retourne la politique optimale
        //Soit on retourne une valeur aléatoire de la liste d'action possible
        la = agent.getPolitique(_e);
        if (this.epsilon > this.rand.nextDouble()) {
            la = agent.getActionsLegales(_e);
        }
        return la.get(ThreadLocalRandom.current().nextInt(0, la.size()));

        //getAction renvoi null si _e absorbant
    }

    public void setEpsilon(double epsilon) {
        //VOTRE CODE
        this.epsilon = epsilon;
    }

}

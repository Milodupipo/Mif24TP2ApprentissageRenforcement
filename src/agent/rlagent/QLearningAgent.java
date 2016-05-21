package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent {
    //TODO

    /**
     *
     * @param alpha
     * @param gamma
     * @param Environnement
     */
    Map< Etat, Map< Action, Double>> QValeur = new HashMap<Etat, Map< Action, Double>>();
    Map< Etat, Vector< Double>> QValeurv2 = new HashMap<Etat, Vector< Double>>();
    //Un vAction pour avoir les actions dispos
    //Un vEtat poru les etats dispos
    //Un Vec < Vec< > > pour sauvegarder les données en rapport avec les etats et actions
    Vector< Vector< Double>> QValeurv3 = new Vector< Vector< Double>>();
    Vector< Etat> vEtat = new Vector< Etat>();
    Vector< Action> vAction = new Vector< Action>();

    public QLearningAgent(double alpha, double gamma, Environnement _env) {
        super(alpha, gamma, _env);
        final int nbEtat = 12;
        final int nbAction = 4;

        for (int i = 0; i < nbEtat; i++) {
            QValeurv3.add(new Vector< Double>());
            for (int j = 0; j < nbAction; j++) {
                double random = ThreadLocalRandom.current().nextInt(0, 100 + 1);
                QValeurv3.get(i).add(random);
            }
        }
        Etat e = env.getEtatCourant();
    }

    void mettreAjourVec(Etat e, Action a) {
        boolean pasTrouver = true;
        //Etat maj
        if (vEtat.isEmpty()) {
            vEtat.add(e);
        } else {
            for (int i = 0; i < vEtat.size(); i++) {
                if (vEtat.get(i).equals(e)) {
                    pasTrouver = false;
                    break;
                }
            }
            if (pasTrouver) {
                vEtat.add(e);
            }
        }
        pasTrouver = true;
        //Action maj
        if (vAction.isEmpty()) {
            vAction.add(a);
        } else {
            for (int i = 0; i < vAction.size(); i++) {
                if (vAction.get(i).equals(a)) {
                    pasTrouver = false;
                    break;
                }
            }
            if (pasTrouver) {
                vAction.add(a);
            }
        }
    }

    /**
     * renvoi la (les) action(s) de plus forte(s) valeur(s) dans l'etat e
     *
     * renvoi liste vide si aucunes actions possibles dans l'etat
     */
    @Override
    public List<Action> getPolitique(Etat e) {
        //TODO
        List< Action> la = this.getActionsLegales(e);
        //Ajout de l'action au vector d'action si on ne la jamais faite
        /*for (Action a : la) {
         mettreAjourVec(e, a);
         }*/
        //Retourne les actions qui ont la plus forte valeur
        for (int i = 0; i < vEtat.size(); i++) {
            if (vEtat.get(i).equals(e)) {
                double indiceMin = 0;
                double min = QValeurv3.get(i).get(0);
                Vector< Double> vtmp = QValeurv3.get(i);
                for (int j = 1; j < vtmp.size(); j++) {
                    if (QValeurv3.get(i).get(j) > min) {
                        la.remove((int) indiceMin);
                        vtmp.remove((int) indiceMin);
                        min = vtmp.get(0);
                        indiceMin = 0;
                        j = 0;
                    } else if (vtmp.get(j) < min) {
                        min = vtmp.get(j);
                        indiceMin = j;
                        j = -1;
                    }
                }
                break;
            }
        }
        return la;
    }

    /**
     * @return la valeur d'un etat
     */
    @Override
    public double getValeur(Etat e) {
        //TODO
        int numE = 0;
        double max = 0;
        for (int i = 0; i < vEtat.size(); i++) {
            if (vEtat.get(i).equals(e)) {
                numE = i;
                break;
            }
        }
        for (int i = 0; i < QValeurv3.get(numE).size(); i++) {
            if (max < QValeurv3.get(numE).get(i)) {
                max = QValeurv3.get(numE).get(i);
            }
        }
        return max;
    }

    /**
     *
     * @param e
     * @param a
     * @return Q valeur du couple (e,a)
     */
    @Override
    public double getQValeur(Etat e, Action a) {
        //TODO
        /*for (int i = 0; i < vEtat.size(); i++) {
         if (vEtat.get(i) == e) {
         QValeurv3.get(i).
         }
         }*/
        int numE = 0, numA = 0;
        for (int i = 0; i < vEtat.size(); i++) {
            if (vEtat.get(i).equals(e)) {
                numE = i;
                break;
            }
        }
        for (int i = 0; i < vAction.size(); i++) {
            if (vAction.get(i).equals(a)) {
                numA = i;
                break;
            }
        }
        if (!QValeurv3.isEmpty()) {
            return QValeurv3.get(numE).get(numA);
        }
        return 0.0;
    }

    /**
     * setter sur Q-valeur
     */
    @Override
    public void setQValeur(Etat e, Action a, double d) {
        //TODO
        /*for (Entry entreEtat : QValeur.entrySet()) {
         Map< Action, Double > map = (Map< Action, Double >)entreEtat;
         for (Entry entreAction : map.entrySet()) {
                
         }
         }*/
        if (d < vmin) {
            vmin = d;
        } else if (d > vmax) {
            vmax = d;
        }
        //QValeur.get(e).put(a, d);
        //mise a jour vmin et vmax pour affichage gradient de couleur
        //...
        int numE = 0, numA = 0;
        for (int i = 0; i < vEtat.size(); i++) {
            if (vEtat.get(i).equals(e)) {
                numE = i;
                break;
            }
        }
        for (int i = 0; i < vAction.size(); i++) {
            if (vAction.get(i).equals(a)) {
                numA = i;
                break;
            }
        }
        QValeurv3.get(numE).set(numA, d);
        this.notifyObs();
    }

    /**
     *
     * mise a jour de la Q-valeur du couple (e,a) apres chaque interaction
     * <etat e,action a, etatsuivant esuivant, recompense reward>
     * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement
     * apres avoir realise une action.
     *
     * @param e
     * @param a
     * @param esuivant
     * @param reward
     */
    @Override
    public void endStep(Etat e, Action a, Etat esuivant, double reward) {
        //TODO
        mettreAjourVec(e, a);
        double proba = 1 - this.alpha;
        //Valeur actuelle
        double valeur = getQValeur(e, a);
        double valeurS = getValeur(esuivant);
        double resultat = proba * valeur + this.alpha * (reward + this.getGamma() * valeurS);
        int numE = 0, numA = 0;
        for (int i = 0; i < vEtat.size(); i++) {
            if (vEtat.get(i).equals(e)) {
                numE = i;
                break;
            }
        }
        for (int i = 0; i < vAction.size(); i++) {
            if (vAction.get(i).equals(a)) {
                numA = i;
                break;
            }
        }
        QValeurv3.get(numE).set(numA, resultat);
        this.env.setEtatCourant(esuivant);
    }

    @Override
    public Action getAction(Etat e) {
        this.actionChoisie = this.stratExplorationCourante.getAction(e);
        return this.actionChoisie;
    }

    /**
     * reinitialise les Q valeurs
     */
    @Override
    public void reset() {
        super.reset();
        this.episodeNb = 0;
        //TODO
        final int nbEtat = 12;
        final int nbAction = 4;
        for (int i = 0; i < QValeurv3.size(); i++) {
            QValeurv3.get(i).clear();
        }
        vEtat.clear();
        vAction.clear();
        for (int i = 0; i < nbEtat; i++) {
            QValeurv3.add(new Vector< Double>());
            for (int j = 0; j < nbAction; j++) {
                double random = ThreadLocalRandom.current().nextInt(0, 100 + 1);
                QValeurv3.get(i).add(random);
            }
        }
        this.notifyObs();
    }

}

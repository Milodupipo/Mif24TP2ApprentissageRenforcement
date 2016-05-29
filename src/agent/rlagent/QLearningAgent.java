package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
import java.util.LinkedList;
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
    protected Map< Etat, Map< Action, Double>> QValeur = new HashMap<Etat, Map< Action, Double>>();

    public QLearningAgent(double alpha, double gamma, Environnement _env) {
        super(alpha, gamma, _env);
    }

    void agrandirMap(Etat e, Action a) {
        boolean pasTrouver = true;
        //pour QValeur
        List< Action> la = this.getActionsLegales(e);
        try {
            QValeur.get(e).get(la.get(0));
        } catch (Exception ex) {
            QValeur.put(e, new HashMap<Action, Double>());
            for (int i = 0; i < la.size(); i++) {
                QValeur.get(e).put(la.get(i), (double) ThreadLocalRandom.current().nextInt(10, 100));
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
        //Recherche les valeurs Double de la map pour l'état courant
        Vector<Double> vd = new Vector<Double>();
        Map<Action, Double> mtmp = (Map<Action, Double>) QValeur.get(e);
        List<Action> la = new LinkedList<Action>();
        try {
            for (Entry action : mtmp.entrySet()) {
                la.add((Action) action.getKey());
                vd.add((Double) action.getValue());
            }
        } catch (Exception ex) {
            la = this.getActionsLegales(e);
            return la;
        }
        //On cherche les meilleures actions possible en fonction de leur valeur
        //Puis on retourne la liste de argmax(valeur)
        int indiceMin = 0;
        if (!vd.isEmpty()) {
            double min = vd.get(0);
            for (int i = 1; i < vd.size(); i++) {
                if (vd.get(i) > min) {
                    la.remove(indiceMin);
                    vd.remove(indiceMin);
                    min = vd.get(0);
                    indiceMin = 0;
                    i = 0;
                } else if (vd.get(i) < min) {
                    min = vd.get(i);
                    indiceMin = i;
                    i = -1;
                }
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
        double max = 0;
        Map<Action, Double> mtmp = QValeur.get(e);
        try {
            for (Entry action : mtmp.entrySet()) {
                if (max < (Double) action.getValue()) {
                    max = (Double) action.getValue();
                }
            }
        } catch (Exception ex) {
            return 0.0;
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
        try {
            return QValeur.get(e).get(a);
        } catch (Exception ex) {
            return 0.0;
        }
    }

    /**
     * setter sur Q-valeur
     */
    @Override
    public void setQValeur(Etat e, Action a, double d) {
        //TODO
        //On actualise la valeur dans la map et on recherche vmin et vmax
        QValeur.get(e).replace(a, d);
        double min = d, max = 0;
        for (Entry map : QValeur.entrySet()) {
            Map<Action, Double> mtmp = (Map<Action, Double>) map.getValue();
            for (Entry eAct : mtmp.entrySet()) {
                if ((Double) eAct.getValue() < min) {
                    min = (Double) eAct.getValue();
                }
                if ((Double) eAct.getValue() > max) {
                    max = (Double) eAct.getValue();
                }
            }
        }
        vmin = min;
        vmax = max;
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
        //On ajoute l'etat à la map si nous ne somme jamais tombé dessus avant
        agrandirMap(e, a);
        double proba = 1 - this.alpha;
        double valeur = getQValeur(e, a);
        double valeurS = getValeur(esuivant);
        double resultat = proba * valeur + this.alpha * (reward + this.getGamma() * valeurS);
        //On acutalise la map et les vmin/vmax
        this.setQValeur(e, a, resultat);
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
        QValeur.clear();

        this.notifyObs();
    }

}

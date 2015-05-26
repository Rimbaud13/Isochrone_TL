 /**
 * Classe représentant un arbre d'arrêts constituant les chemins les plus courts pour un arrêt.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.timetable;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;


public final class FastestPathTree {

    private final Stop m_startingStop;
    private final Map<Stop, Integer> m_arrivalTime;
    private final Map<Stop, Stop> m_predecessor;

    /**
     * Constructeur de la classe qui construit l'arbre des chemins les plus
     * courts pour un arrêt passé en agrument ainsi que deux Map. Le premier
     * associe un heure d'arrivée à chaque arrêt pour lequel il y a un chemin le
     * plus court. Le deuxième associe l'arrêt précédent à un arrêt pour lequel
     * il y un chemin le plus court.
     * 
     * @param startingStop
     *            L'arrêt de départ.
     * @param arrivalTime
     *            Une Map associant une heure d'arrivée à chaque arrêt d'arrivée
     *            possible.
     * @param predecessor
     *            Une Map associant l'arrêt qui précéde un arrêt d'arrivée dans
     *            le chemin le plus court pour y arriver.
     * @throws IllegalArgumentException
     *             Si tous les arrêts de la table des prédécesseurs ainsi que
     *             l'arrêt de départ ne sont pas contenu dans la table des
     *             heures d'arrivées.
     */
    public FastestPathTree(Stop startingStop, Map<Stop, Integer> arrivalTime,
            Map<Stop, Stop> predecessor) {
        for (Stop i : arrivalTime.keySet()) {
            if (!predecessor.containsKey(i) && i != startingStop) {
                throw new IllegalArgumentException(
                        "Erreur : Clés des maps invalides !");
            }
        }

        m_startingStop = startingStop;
        m_arrivalTime = new HashMap<>(arrivalTime);
        m_predecessor = new HashMap<>(predecessor);
    }

    /**
     * Retourne l'arrêt de départ du chemin le plus court.
     * 
     * @return L'arrêt de départ du chemin le plus court.
     */
    public Stop startingStop() {
        return m_startingStop;
    }

    /**
     * Retourne l'heure de départ pour faire le chemin le plus court.
     * 
     * @return L'heure de départ pour faire le chemin le plus court.
     */
    public int startingTime() {
        return m_arrivalTime.get(m_startingStop);
    }

    /**
     * Retourne un Set contenant tous les arrêts pour lesquels il existe un
     * chemin le plus court pour ce point de départ.
     * 
     * @return Un set contenant touts les arrêts pour lesquel il existe un
     *         chemin le plus court.
     */
    public Set<Stop> stops() {
        return unmodifiableSet(m_arrivalTime.keySet());
    }

    /**
     * Retourne l'heure d'arrivée à l'arrêt passé en argument, en prenant le
     * chemin le plus court depuis l'arrêt de départ. Retourne infini, si
     * l'arrêt passé en paramètre n'est pas dans la liste des arrêts qui ont un
     * chemin le plus court.
     * 
     * @param stop
     *            L'arrêt pour lequel on shouaite savoir l'heure d'arrivée en
     *            emprutant le chemin le plus court.
     * @return L'heure d'arrivée à l'arrêt en emprutant le chemin le plus court.
     */
    public int arrivalTime(Stop stop) {
        if (!m_arrivalTime.containsKey(stop))
            return SecondsPastMidnight.INFINITE;
        else
            return m_arrivalTime.get(stop);
    }

    /**
     * Retourne une List contenant tous les arrêts en partant de celui de départ
     * pour arriver à l'arrêt passé en paramètre, en emprutant le chemin le plus
     * court (l'ordre dans la liste est : arrêt de départ jusqu'à l'arrêt
     * d'arrivée (tous deux y compris)).
     * 
     * @param stop
     *            L'arrêt d'arrivée pour lequel on shouaite avoir la List des
     *            arrêts.
     * @return La List de touts les arrêts en partant de l'arrêt de départ
     *         jusqu'à l'arrêt d'arrivée (tous deux y compris) en empruntant le
     *         chemin le plus court.
     */
    public List<Stop> pathTo(Stop stop) {
        if (!m_arrivalTime.containsKey(stop)) {
            throw new IllegalArgumentException(
                    "Error : Cet arrêt n'a pas de chemin le plus court associé !");
        }

        ArrayList<Stop> path = new ArrayList<>();
        path.add(stop);
        boolean done = false;
        if (stop == m_startingStop) {
            done = true;
        }

        Stop tmp = stop;
        Stop predecessorTmp = null;
        while (!done) {
            predecessorTmp = m_predecessor.get(tmp);
            path.add(predecessorTmp);
            tmp = predecessorTmp;
            if (tmp == m_startingStop) {
                done = true;
            }
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Bâtisseur de FastestPathTree, facilite l'instanciation de celle-ci.
     * 
     * @author Justinien Bouron (236608)
     * @author Nicolas Roussel (238333)
     */
    public static final class Builder {

        private final Stop m_builderStartingStop;
        private final Map<Stop, Integer> m_builderArrivalTime;
        private final Map<Stop, Stop> m_builderPredecessor;

        /**
         * Constructeur du bâtisseur associé à FastestPathTree.
         * 
         * @param startingStop
         *            L'arrêt de départ.
         * @param startingTime
         *            L'heure de départ.
         * @throws IllegalArgumentException
         *             Si l'heure de départ est négative.
         */
        public Builder(Stop startingStop, int startingTime) {
            if (startingTime < 0) {
                throw new IllegalArgumentException(
                        "Error : Heure de départ négative !");
            }

            m_builderStartingStop = startingStop;
            m_builderArrivalTime = new HashMap<>();
            m_builderPredecessor = new HashMap<>();
            m_builderArrivalTime.put(m_builderStartingStop, startingTime);
        }

        /**
         * Ajoute au bâtisseur un arrêt d'arrivée, son heure d'arrivée, et
         * l'arrêt qui précède l'arrivée à cet arrêt. Ceux-ci sont tous passé en
         * argument.
         * 
         * @param stop
         *            L'arrêt d'arrivée.
         * @param time
         *            L'heure d'arrivée.
         * @param predecessor
         *            L'arrêt qui précéde l'arrêt d'arrivée.
         * @return La même instance d'arrivée après y avoir ajouté l'arrêt,
         *         l'heure et le prédécesseur.
         * @throws IllegalArgumentException
         *             Si l'heure d'arrivée est inférieure à l'heure de départ.
         *             Si l'arrêt passé en argument est l'arrêt de départ (il
         *             est impossible de lui ajouter un prédécesseur ou de
         *             redéfinir l'heure de départ.
         */
        public Builder setArrivalTime(Stop stop, int time, Stop predecessor) {
            if (stop == m_builderStartingStop) {
                throw new IllegalArgumentException(
                        "Error : Impossible de redéfinir l'heure de départ ou d'y ajouter un prédécesseur !");
            }

            if (time < m_builderArrivalTime.get(predecessor)) {
                throw new IllegalArgumentException(
                        "Error : Heure d'arrivée antérieure à l'heure de départ !");
            }

            m_builderArrivalTime.put(stop, time);
            m_builderPredecessor.put(stop, predecessor);
            return this;
        }

        /**
         * Retourne l'heure de première arrivée à l'arrêt passé en argument.
         * 
         * @param stop
         *            L'arrêt dont on shouaite savoir l'heure de première
         *            arrivée.
         * @return L'heure de première arrivée.
         */
        public int arrivalTime(Stop stop) {
            if (!m_builderArrivalTime.containsKey(stop)) {
                return SecondsPastMidnight.INFINITE;
            } else {
                return m_builderArrivalTime.get(stop);
            }
        }

        /**
         * Construit le FastestPathTree auquel se rattache ce bâtisseur.
         * 
         * @return L'instance de FastestPathTree auquel se rattache ce
         *         bâtisseur.
         */
        public FastestPathTree build() {
            return new FastestPathTree(m_builderStartingStop,
                    m_builderArrivalTime, m_builderPredecessor);
        }
    }
}

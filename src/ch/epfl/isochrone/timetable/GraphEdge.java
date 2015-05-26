/**
 * Classe permettant la représentation d'un arc de graphe.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.timetable;

import java.util.Set;
import java.util.HashSet;

final class GraphEdge {

    private final Stop m_destination;
    private final int m_walkingTime;
    private final Set<Integer> m_packedTrips;

    /**
     * Construit une instance de la classe GraphEdge.
     * 
     * @param destination
     *            Représente la destination de l'arc du graphe.
     * @param walkingTime
     *            Temps nécéssaire pour arriver a destination a pied. Vaut -1
     *            quand c'est impossible (trop loin).
     * @param packedTrips
     *            Collection regroupant tous les trajets de cet arc.
     * @throws IllegalArgumentException
     *             Si le temps de marche est inférieur à -1.
     */

    public GraphEdge(Stop destination, int walkingTime, Set<Integer> packedTrips) {
        if (walkingTime < -1)
            throw new IllegalArgumentException(
                    "Erreur : temps de marche invalide.");

        m_destination = destination;
        m_walkingTime = walkingTime;
        m_packedTrips = new HashSet<>(packedTrips);
    }

    /**
     * Permet l'encodage dans un entier du trajet. Cet entier comporte dans les
     * 6 chiffres de poids fort l'heure de départ et dans les 4 autres chiffres
     * le temps de trajet.
     * 
     * @param departureTime
     *            Représente l'heure de départ en direction de la destination.
     * @param arrivalTime
     *            L'heure d'arrivé à la destination.
     * @return L'entier résultant de l'encodage du trajet.
     * @throws IllegalArgumentException
     *             Si l'heure de départ est invalide ou si la durée du trajet
     *             est invalide.
     */
    public static int packTrip(int departureTime, int arrivalTime) {
        if (!(0 <= departureTime && departureTime <= 107999)
                || !(0 <= arrivalTime - departureTime && arrivalTime
                - departureTime <= 9999))
            throw new IllegalArgumentException("Erreur : Temps invalides.");

        return departureTime * 10000 + (arrivalTime - departureTime);
    }

    /**
     * Permet l'extraction de l'heure de départ d'un trajet donné sous sa forme
     * encodée.
     * 
     * @param packedTrip
     *            Le trajet sous sa forme encodée.
     * @return L'heure de départ de ce trajet.
     */
    public static int unpackTripDepartureTime(int packedTrip) {
        return packedTrip / 10000;
    }

    /**
     * Extrait la durée d'un trajet donné sous sa forme encodée.
     * 
     * @param packedTrip
     *            Le trajet sous sa forme encodée.
     * @return La durée de ce trajet.
     */
    public static int unpackTripDuration(int packedTrip) {
        return packedTrip % 10000;
    }

    /**
     * Extrait l'heure d'arrivée d'un trajet donné sous sa forme encodée.
     * 
     * @param packedTrip
     *            Le trajet sous sa forme encodée.
     * @return L'heure d'arrivée de ce trajet
     */
    public static int unpackTripArrivalTime(int packedTrip) {
        return unpackTripDepartureTime(packedTrip)
                + unpackTripDuration(packedTrip);
    }

    /**
     * Un Getter renvoyant la destination de l'instance de GraphEdge sur
     * laquelle on appelle cette méthode.
     * 
     * @return Destination de l'arc.
     */
    public Stop destination() {
        return m_destination;
    }

    /**
     * Permet de connaitre l'heure d'arrivée à destination la plus petite en
     * fonction de l'heure de départ donnée.
     * 
     * @param departureTime
     *            L'heure de départ.
     * @return La première heure d'arrivée possible
     */
    public int earliestArrivalTime(int departureTime) {

        int lastDeparture = -1;

        for (Integer i : m_packedTrips) { // recherche de la dernière heure de départ en TL
            if (unpackTripDepartureTime(i) > lastDeparture)
                lastDeparture = unpackTripDepartureTime(i);
        }

        if (departureTime > lastDeparture && m_walkingTime == -1)
            return SecondsPastMidnight.INFINITE;
        else {
            int arrivalTimeWalk = departureTime + m_walkingTime;
            int arrivalTimeTL = SecondsPastMidnight.INFINITE;

            for (Integer i : m_packedTrips) {// recherche de l'heure d'arrivée
                if (unpackTripDepartureTime(i) >= departureTime
                        && unpackTripArrivalTime(i) < arrivalTimeTL)
                    arrivalTimeTL = unpackTripArrivalTime(i);
            }

            if (m_walkingTime == -1)
                return arrivalTimeTL;
            else {
                if (arrivalTimeWalk <= arrivalTimeTL)
                    return arrivalTimeWalk;
                else
                    return arrivalTimeTL;
            }
        }

    }

    /**
     * Bâtisseur de la classe GraphEdge.
     * 
     * @author Justinien Bouron (236608)
     * @author Nicolas Roussel (238333)
     */
    public static final class Builder {

        private int m_builderWalkingTime;
        private final Stop m_builderDestination;
        private final Set<Integer> m_builderPackedTrips;

        /**
         * Constructeur du builder de la classe GraphEdge.
         * 
         * @param destination
         *            Destination de la future instance de la classe GraphEdge.
         */
        public Builder(Stop destination) {
            m_builderDestination = destination;
            m_builderWalkingTime = -1;
            m_builderPackedTrips = new HashSet<>();
        }

        /**
         * Un Setter permettant de modifier la valeur de la variable walkingTime
         * représentant la durée du trajet a pied. Cette valeur peut être -1
         * signifiant alors qu'il n'est pas possible de faire le trajet a pied.
         * 
         * @param newWalkingTime
         *            La nouvelle valeur de la variable walkingTime.
         * @return Une addresse sur le builder lui-même permettant un appel en
         *         chaine des méthodes du Builder.
         * @throws IllegalArgumentException
         *             Si la nouvelle valeur de walkingTime est invalide c'est à
         *             dire inférieur à -1.
         */
        public GraphEdge.Builder setWalkingTime(int newWalkingTime) {
            if (newWalkingTime < -1)
                throw new IllegalArgumentException(
                        "Erreur : walkingTime invalide.");

            m_builderWalkingTime = newWalkingTime;
            return this;
        }

        /**
         * Permet d'ajouter un trajet a la future instance de GraphEdge.
         * 
         * @param departureTime
         *            Représente l'heure de départ de ce trajet.
         * @param arrivalTime
         *            L'heure d'arrivée de ce trajet.
         * @return Une addresse sur le builder lui-même permettant un appel en
         *         chaine des méthodes du Builder.
         * @throws IllegalArgumentException
         *             Si la méthode packTrip renvoit une exception, voir la
         *             méthode packTrip pour plus de détail.
         */
        public GraphEdge.Builder addTrip(int departureTime, int arrivalTime) {
            m_builderPackedTrips.add(packTrip(departureTime, arrivalTime));
            return this;
        }

        /**
         * Permet la création de l'instance de la classe GraphEdge associée à ce
         * builder.
         * 
         * @return L'instance elle-même.
         */
        public GraphEdge build() {
            return new GraphEdge(m_builderDestination, m_builderWalkingTime,
                    m_builderPackedTrips);
        }
    }

}

/**
 * Classe représentant le graphe de l'horaire.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.timetable;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import static java.util.Collections.unmodifiableSet;
import static java.util.Collections.unmodifiableMap;

public final class Graph {

    private final Set<Stop> m_stops;
    private final Map<Stop, List<GraphEdge>> m_outgoingEdges;
    
    private Graph(Set<Stop> stops, Map<Stop, List<GraphEdge>> outgoingEdges) {
        assert validateBuilderSet(stops, outgoingEdges);
        m_stops = stops;
        m_outgoingEdges = outgoingEdges;
    }

    private boolean validateBuilderSet(Set<Stop> stops,
            Map<Stop, List<GraphEdge>> outgoingEdges) {
        for(Stop i : outgoingEdges.keySet()){
            if(!stops.contains(i))
                return false;
        }
        for(List<GraphEdge> i : outgoingEdges.values()){
            for(GraphEdge j : i){
                if(!stops.contains(j.destination()))
                    return false;
            }
        }

        return true;
    }

    /**
     * Méthode retournant une instance de la classe FastestPathTree et
     * représentant l'arborescence des chemins les plus court pour un arrêt et
     * une heure de départ donnés (Utilise l'algorithme de Dijkstra).
     * 
     * @param startingStop
     *            L'arrêt de départ.
     * @param departureTime
     *            L'heure de départ.
     * @return Instance de la classe FastestPathTree représentant l'arborescence
     *         décrite plus haut.
     * @throws IllegalArgumentException
     *             Si l'heure de départ est négative ou si le graphe ne
     *             continent pas l'arrêt de départ.
     */
    public FastestPathTree fastestPaths(Stop startingStop, int departureTime) {
        if (departureTime < 0) {
            throw new IllegalArgumentException(
                    "Error : Heure de départ négative !");
        }
        if (!(m_stops.contains(startingStop))) {
            throw new IllegalArgumentException(
                    "Error : Le graphe ne contient pas l'arrêt de départ !");
        }

        FastestPathTree.Builder pathBuilder = new FastestPathTree.Builder(
                startingStop, departureTime);

        for (Stop i : m_stops) {// On met toutes les heures d'arrivées à
            // Infinite
            if (i != startingStop) {
                pathBuilder.setArrivalTime(i, SecondsPastMidnight.INFINITE,
                        startingStop);
            }
        }

        HashSet<Stop> V = new HashSet<>(m_stops);

        int minArrival = SecondsPastMidnight.INFINITE;
        Stop stopToVisit = null;

        while (!V.isEmpty()) {
            minArrival = SecondsPastMidnight.INFINITE;
            stopToVisit = null;
            for (Stop i : V) {// recherche de l'arrêt à visiter
                if (pathBuilder.arrivalTime(i) <= minArrival) {
                    minArrival = pathBuilder.arrivalTime(i);
                    stopToVisit = i;
                }
            }

            V.remove(stopToVisit);

            if (pathBuilder.arrivalTime(stopToVisit) == SecondsPastMidnight.INFINITE) {// Si
                // il
                // n'y
                // a
                // plus
                // de
                // voisins
                // à
                // visiter
                break;
            }
            
            if(m_outgoingEdges.get(stopToVisit) != null){
                for (GraphEdge j : m_outgoingEdges.get(stopToVisit)) {// On
                    // considére
                    // les voisins
                    // de l'arrêt
                    // qu'on
                    // visite
                    if (j.earliestArrivalTime(pathBuilder.arrivalTime(stopToVisit)) < pathBuilder
                            .arrivalTime(j.destination())
                            && V.contains(j.destination())) {
    
                        pathBuilder.setArrivalTime(j.destination(), j
                                .earliestArrivalTime(pathBuilder
                                        .arrivalTime(stopToVisit)), stopToVisit);
                    }
                }
            }

        }

        return pathBuilder.build();
    }

    /**
     * Bâtisseur de Graph, facilite l'instanciation de celle-ci.
     * 
     * @author Justinien Bouron (236608)
     * @author Nicolas Roussel (238333)
     * 
     */
    public static final class Builder {

        private final Set<Stop> m_builderStops;
        private final Map<Stop, Map<Stop, GraphEdge.Builder>> m_builderGraphBuilders;

        /**
         * Construit le bâtisseur de Graph grâce aux arrêts qui lui sont passés
         * en argument.
         * 
         * @param stops
         */
        public Builder(Set<Stop> stops) {
            m_builderStops = new HashSet<>(stops);
            //m_builderOutgoingEdges = new HashMap<>();
            m_builderGraphBuilders = new HashMap<>();

        }

        /**
         * Ajoute un arc (pour un transport en TL) au Graph auquel se bâtisseur
         * se rattache, en lui passant en argument : l'arrêt de départ,
         * d'arrivée, l'heure de départet l'heure d'arrivée.
         * 
         * @param fromStop
         *            L'arrêt de départ.
         * @param toStop
         *            L'arrêt d'arrivée.
         * @param departureTime
         *            L'heure de départ.
         * @param arrivalTime
         *            L'heure d'arrivée.
         * @return Cette instance du bâtisseur après y avoir ajouté l'arc.
         * @throws IllegalArgumentException
         *             Si la liste d'arrêts du Graph ne contient pas l'arrêt de
         *             départ ou d'arrivée. Si l'heure d'arrivée est inférieure
         *             à l'heure de départ. Si les heures sont négatives.
         */
        public Builder addTripEdge(Stop fromStop, Stop toStop,
                int departureTime, int arrivalTime) {
            if (!m_builderStops.contains(fromStop)
                    || !m_builderStops.contains(toStop)) {
                throw new IllegalArgumentException(
                        "Error: Les arrêts ne se trouvent pas dans cette instance de Builder !");
            }   

            if (departureTime < 0 || arrivalTime < 0) {
                throw new IllegalArgumentException(
                        "Error: Heure(s) négative(s) !");
            }

            if (departureTime > arrivalTime) {
                throw new IllegalArgumentException(
                        "Error: Heure d'arrivée antérieure à l'heure d'arrivée !");
            }

            this.getBuilder(fromStop, toStop).addTrip(departureTime,
                    arrivalTime);
            return this;
        }

        /**
         * Ajoute tout les arcs (marche à pied) au Graph pour tout les arrêts de
         * ce bâtisseur, en lui passant en argument le temps maximum de marche
         * ainsi que la vitesse de marche.
         * 
         * @param maxWalkingTime
         *            Le temps maximum de marche.
         * @param walkingSpeed
         *            La vitesse de marche.
         * @return La même instance de ce builder après y avoir ajouté tout les
         *         arcs.
         * @throws IllegalArgumentException
         *             Si le temps maximum de marche est négatif. Si la vitesse
         *             est négative.
         */
        public Builder addAllWalkEdges(int maxWalkingTime, double walkingSpeed) {
            if (maxWalkingTime < 0) {
                throw new IllegalArgumentException(
                        "Error: Temps maximum de marche négatif !");
            }

            if (walkingSpeed <= 0) {
                throw new IllegalArgumentException(
                        "Error: Vitesse de marche nulle ou négative !");
            }

            double maxDistance = ((double) maxWalkingTime) * walkingSpeed;
            double distanceTo = 0.0;
            ArrayList<Stop> stopList = new ArrayList<>(m_builderStops);

            for (int i = 0; i < stopList.size(); i++) {
                for (int j = i + 1; j < stopList.size(); j++) {
                    distanceTo = Math.round(stopList.get(i).position()
                            .distanceTo(stopList.get(j).position()));
                    if (distanceTo <= maxDistance) {
                        int walkTime = ((int) (Math.round(distanceTo / walkingSpeed)));

                        this.getBuilder(stopList.get(i), stopList.get(j))
                        .setWalkingTime(walkTime);

                        this.getBuilder(stopList.get(j), stopList.get(i))
                        .setWalkingTime(walkTime);
                    }
                }
            }
            return this;
        }

        /**
         * Construit le Graph auquel est associé ce builder.
         * 
         * @return L'instance de Graph auquel se rattache ce builder.
         */
        public Graph build() {
            List<GraphEdge> arc = new ArrayList<GraphEdge>();
            Map<Stop, GraphEdge.Builder> tmp;
            Map<Stop, List<GraphEdge>> builderOutgoingEdges = new HashMap<>();
            for (Stop i : m_builderGraphBuilders.keySet()) {
                tmp = m_builderGraphBuilders.get(i);
                for (Stop j : tmp.keySet()) {// construction des Builders
                    arc.add(tmp.get(j).build());
                }
                builderOutgoingEdges.put(i, new ArrayList<>(arc));
                arc.clear();
            }

            return new Graph(unmodifiableSet(m_builderStops), unmodifiableMap(builderOutgoingEdges));
        }

        private GraphEdge.Builder getBuilder(Stop fromStop, Stop toStop) {
            if (!m_builderGraphBuilders.containsKey(fromStop)) {
                // Si il n'est pas dans la map
                HashMap<Stop, GraphEdge.Builder> tmp = new HashMap<>();
                tmp.put(toStop, new GraphEdge.Builder(toStop));
                m_builderGraphBuilders.put(fromStop, tmp);
            } else if (m_builderGraphBuilders.containsKey(fromStop)
                        && !m_builderGraphBuilders.get(fromStop).containsKey(toStop)){
                    // Si il est dans la map mais pas la map de map            
                m_builderGraphBuilders.get(fromStop).put(toStop,
                        new GraphEdge.Builder(toStop));
            }

            return m_builderGraphBuilders.get(fromStop).get(toStop);
        }
    }
}
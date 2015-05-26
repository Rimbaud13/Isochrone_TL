/**
 * Classe représentant un horaire.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.timetable;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import static java.util.Collections.unmodifiableSet;

public final class TimeTable {

    private final Set<Stop> m_stops;
    private final Collection<Service> m_services;

    /**
     * Constructeur de la classe Timetable qui instancie une TimeTable grâce aux
     * arrêts et services qui lui sont passé en arguement.
     * 
     * @param stops
     *            Un ensemble d'arrêts de classe Stop.
     * @param services
     *            Un ensemble de services de classe Service.
     */
    public TimeTable(Set<Stop> stops, Collection<Service> services) {
        m_stops = unmodifiableSet(new HashSet<>(stops));
        m_services = new HashSet<>(services);
    }

    /**
     * Retourne l'ensemble des arrêts de l'horaire.
     * 
     * @return L'ensemble des arrêts de l'horaire.
     */
    public Set<Stop> stops() {
        return m_stops;
    }

    /**
     * Retourne l'ensemble des services actifs pour un certain jour qui est
     * passé en argument.
     * 
     * @param date
     *            Le jour pour lequel on souhait avoir l'ensemble des services
     *            actifs.
     * @return L'ensemble des services actifs pour le jour passé en argument.
     */
    public Set<Service> servicesForDate(Date date) {
        Set<Service> tmp = new HashSet<>();
        for (Service service : m_services) {
            if (service.isOperatingOn(date)) {
                tmp.add(service);
            }
        }

        return tmp;
    }

    /**
     * Bâtisseur de la classe TimbeTable.
     * 
     * @author Justinien Bouron (236608)
     * @author Nicolas Roussel (238333)
     */
    public static final class Builder {

        private final Set<Stop> m_builderStops;
        private final Collection<Service> m_builderServices;

        public Builder() {
            m_builderStops = new HashSet<>();
            m_builderServices = new HashSet<>();
        }

        /**
         * Retourne la même instance de ce builder après y avoir ajouté à la
         * liste d'arrêts, l'arrêt passé en argument.
         * 
         * @param newStop
         *            L'arrêt que l'on shouaite ajouter à l'horaire auquel se
         *            rattache ce builder.
         * @return La même instance de ce builder après y avoir ajouté l'arrêt.
         */
        public Builder addStop(Stop newStop) {
            m_builderStops.add(newStop);
            return this;
        }

        /**
         * Retourne la même instance de ce builder après y avoir ajouté à la
         * liste de services, le service passé en argument.
         * 
         * @param newService
         *            Le service que l'on shouaite ajouter à l'horaire auquel se
         *            rattache ce builder.
         * @return La même instance de ce builder après y avoir ajouté le
         *         service.
         */
        public Builder addService(Service newService) {
            m_builderServices.add(newService);
            return this;
        }

        /**
         * Retourne l'instance TimeTable auquel ce Builder se rattache.
         * 
         * @return L'instance de TimeTable auquel ce Builder se rattache.
         */
        public TimeTable build() {
            return new TimeTable(m_builderStops, m_builderServices);
        }
    }
}

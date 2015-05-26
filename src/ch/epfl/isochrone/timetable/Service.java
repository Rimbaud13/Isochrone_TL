/**
 * Classe représentant un service.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.timetable;

import java.util.Set;
import java.util.HashSet;

public final class Service {

    private final String m_name;
    private final Date m_startingDate;
    private final Date m_endingDate;
    private final Set<Date.DayOfWeek> m_opertatingDays;
    private final Set<Date> m_excludedDates;
    private final Set<Date> m_includedDates;

    /**
     * Constructuer d'une instance de la classe Service qui est définine par son
     * nom, sa plage de validité, des dates excluses (exceptions à la plage de
     * validité) et des dates incluses (exceptions à la plage de validité).
     * 
     * @param name
     *            Le nom du service.
     * @param startingDate
     *            La date de début de la plage de validité du service.
     * @param endingDate
     *            La date de fin de la plage de validité du service.
     * @param operatingDays
     *            Les jours de la semaine pendant lesquels le service est actif.
     * @param excludedDates
     *            Les jours contenus dans la plage de validité mais où le
     *            service n'est pas actif (exceptions à la plage de validité).
     * @param includedDates
     *            Les jours qui ne sont pas contenus dans la plage de validité
     *            mais où le service est tout de même actif.
     * @throws IllegalArgumentException
     *             Si la plage de validité est invalide (date de fin précède la
     *             date de début). Si une des dates excluses n'est pas contenue
     *             dans la plage de validité. Si les dates excluses et incluses
     *             (exceptions à la plage de validité) ont des dates communes.
     */
    public Service(String name, Date startingDate, Date endingDate,
            Set<Date.DayOfWeek> operatingDays, Set<Date> excludedDates,
            Set<Date> includedDates) {

        if (endingDate.compareTo(startingDate) == -1) {
            throw new IllegalArgumentException(
                    "Error : Dates de début et de fin invalides !");
        }

        for (Date date : excludedDates) {
            if (date.compareTo(endingDate) == 1
                    || date.compareTo(startingDate) == -1) {
                throw new IllegalArgumentException(
                        "Error : Dates excluses invalides !");
            }

            if (includedDates.contains(date)) {
                throw new IllegalArgumentException(
                        "Error : Dates incluses et excluses ont des dates en commun !");
            }
        }

        for (Date date : includedDates) {
            if (date.compareTo(endingDate) == 1
                    || date.compareTo(startingDate) == -1) {
                throw new IllegalArgumentException(
                        " Error : Dates excluses invalides !");
            }
        }

        m_name = name;
        m_startingDate = startingDate;
        m_endingDate = endingDate;
        m_opertatingDays = new HashSet<>(operatingDays);
        m_excludedDates = new HashSet<>(excludedDates);
        m_includedDates = new HashSet<>(includedDates);
    }

    /**
     * Retourne le nom du service.
     * 
     * @return Le nom du service.
     */
    public String name() {
        return m_name;
    }

    /**
     * Retournes vrai si le jour passé en paramètre est un jour actif dans le
     * service. C'est-à-dire si le jour est dans la plage de validité sans être
     * dans les jour exclus, ou si il fait partie des jours inclus.
     * 
     * @param date
     *            Le jour dont on désire savoir si le service est actif ou non.
     * @return Vrai si le jour est un jour actif du service, et faux sinon.
     */
    public boolean isOperatingOn(Date date) {
        return (!(m_excludedDates.contains(date))
                && !(date.compareTo(m_endingDate) == 1)
                && !(date.compareTo(m_startingDate) == -1) 
                && m_opertatingDays.contains(date.dayOfWeek()))
                || m_includedDates.contains(date);
    }

    /**
     * Retourne une String contenant le nom du service.
     * 
     * @return Le nom du service dans une String.
     */
    public String toString() {
        return m_name;
    }

    /**
     * Bâtisseur de la classe Service.
     * 
     * @author Justinien Bouron (236608)
     * @author Nicolas Roussel (238333)
     */
    public static final class Builder {

        private final String m_builderName;
        private final Date m_builderStartingDate;
        private final Date m_builderEndingDate;
        private final Set<Date.DayOfWeek> m_builderOpertatingDays;
        private final Set<Date> m_builderExcludedDates;
        private final Set<Date> m_builderIncludedDates;

        /**
         * Constructeur de la classe Builder auquel on passe en argument le nom
         * et la plage de validité (dates de début et fin) du service duquel il
         * facilite l'instanciation.
         * 
         * @param name
         *            Le nom du service auquel ce Builder se rattache.
         * @param startingDate
         *            La date de début du service auquel ce Builder se rattache.
         * @param endingDate
         *            La date de fin du service auquel auquel ce Builder se
         *            rattache.
         * @throws IllegalArgumentException
         *             Si la plage de validité est invalide (date de fin du
         *             service précède celle de début).
         */
        public Builder(String name, Date startingDate, Date endingDate) {
            if (startingDate.compareTo(endingDate) == 1) {
                throw new IllegalArgumentException(
                        "Date de fin du service précède la date de début !");
            }

            m_builderName = name;
            m_builderStartingDate = startingDate;
            m_builderEndingDate = endingDate;
            m_builderOpertatingDays = new HashSet<>();
            m_builderExcludedDates = new HashSet<>();
            m_builderIncludedDates = new HashSet<>();
        }

        /**
         * Retourne le nom du service auquel ce Builder se rattache.
         * 
         * @return Le nom du service auquel ce Builder se rattache.
         */
        public String name() {
            return m_builderName;
        }

        /**
         * Retourne la même instance Builder après avoir ajouté à la liste des
         * jours de circulation du service auquel ce Builder se rattache, le
         * jour passé en argument.
         * 
         * @param day
         *            Le jour que l'on shouaite ajouter à la liste des jours de
         *            circualtion du service auquel ce Builder se rattache.
         * @return La même instance de ce Builder après y avoir ajouté le jour
         *         de circualtion.
         */
        public Builder addOperatingDay(Date.DayOfWeek day) {
            m_builderOpertatingDays.add(day);
            return this;
        }

        /**
         * Retourne la même instance Builder après avoir ajouté à la liste des
         * dates exceptionnelles à exclure du service auquel ce Builder se
         * rattache, la date passée en argument.
         * 
         * @param date
         *            La date à ajouter à la liste des dates à exclure du
         *            service auquel ce Builder se rattache.
         * @return La même instance de ce Builder après y avoir ajouté la date à
         *         exclure du service auquel ce Builder se rattache.
         * @throws IllegalArgumentException
         *             Si la date n'est pas dans la plage de validité du service
         *             auquel ce Builder se rattache ou si la date fait partie
         *             des dates à inclure de ce même service.
         */
        public Builder addExcludedDate(Date date) {
            if (m_builderIncludedDates.contains(date)) {
                throw new IllegalArgumentException(
                        "Erreur : La date est contenue dans les dates à inclure !");
            }

            if (date.compareTo(m_builderEndingDate) == 1
                    || date.compareTo(m_builderStartingDate) == -1) {
                throw new IllegalArgumentException(
                        "Erreur : La date n'est pas dans la plage de validité !");
            }

            m_builderExcludedDates.add(date);
            return this;
        }

        /**
         * Retourne la même instance Builder après avoir ajouté à la liste des
         * dates exceptionnelles à inclure du service auquel ce Builder se
         * rattache, la date passée en argument.
         * 
         * @param date
         *            La date à ajouter à la liste des dates à inclure du
         *            service auquel ce Builder se rattache.
         * @return La même instance de ce Builder après y avoir ajouté la date à
         *         inclure du service auquel ce Builder se rattache.
         * @throws IllegalArgumentException
         *             Si la date n'est pas dans la plage de validité du service
         *             auquel ce Builder se rattache ou si la date fait partie
         *             des dates à exclure de ce même service.
         */
        public Builder addIncludedDate(Date date) {
            if (date.compareTo(m_builderEndingDate) == 1
                    || date.compareTo(m_builderStartingDate) == -1) {
                throw new IllegalArgumentException(
                        "Erreur : La date n'est pas dans la plage de validité !");
            }
            if (m_builderExcludedDates.contains(date)) {
                throw new IllegalArgumentException(
                        "Erreur : La date est contenue dans les dates à exclure !");
            }

            m_builderIncludedDates.add(date);
            return this;
        }

        /**
         * Retourne le service auquel se rattache ce Builder.
         * 
         * @return Le service auquel se rattache ce Builder.
         */
        public Service build() {
            return new Service(m_builderName, m_builderStartingDate,
                    m_builderEndingDate, m_builderOpertatingDays,
                    m_builderExcludedDates, m_builderIncludedDates);
        }
    }

}

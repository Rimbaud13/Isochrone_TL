/**
 * Classe représentant un arrêt dans l'espace.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;

public final class Stop{

    private final String m_name;
    private final PointWGS84 m_position;

    /**
     * Construit un arrêt dans l'espace avec un nom et une position.
     * 
     * @param name
     *            Le nom que l'on associe à l'arrêt.
     * @param position
     *            La postion que l'on associe à l'arrêt.
     */
    public Stop(String name, PointWGS84 position) {
        m_name = name;
        m_position = position;
    }

    /**
     * Retourne le nom de l'arrêt.
     * 
     * @return Le nom de l'arrêt.
     */
    public String name() {
        return m_name;
    }

    /**
     * Retourne la position de l'arrêt.
     * 
     * @return La position de l'arrêt.
     */
    public PointWGS84 position() {
        return m_position;
    }

    /**
     * Retourne un String contenant le nom de l'arrêt.
     * 
     * @return Un string contenant le nom de l'arrêt.
     */
    @Override
    public String toString() {
        return m_name;
    }
}

/**
 * Classe représentant un point dans le système de coordonnées OSM.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.geo;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.sinh;
import static java.lang.Math.pow;
import static java.lang.Math.round;

public final class PointOSM {

    private final int m_zoom;
    private final double m_coordX;
    private final double m_coordY;

    /**
     * Construit un point dans le systèem OSM.
     * 
     * @param zoom
     *            Le niveau de zoom utilisé dans le système de coordonnées.
     * @param x
     *            La valeur de la coordonnée X dans le système de coorodonnées.
     * @param y
     *            La valeur de la coordonnée Y dans le système de coorodonnées.
     * @throws IllegalArgumentException
     *             Si le zoom est négatif.
     */
    public PointOSM(int zoom, double x, double y) {
        if (zoom < 0)
            throw new IllegalArgumentException(
                    " Error : Zoom invalide car négatif !");

        m_zoom = zoom;
        int maxXY = maxXY(m_zoom);

        if (!(0 <= x && x <= maxXY))
            throw new IllegalArgumentException(
                    "Error : Coordonée X, invalide, doit être dans l'intervalle [0;"
                            + maxXY + "]!");

        if (!(0 <= y && y <= maxXY))
            throw new IllegalArgumentException(
                    "Error : Coordonée Y, invalide, doit être dans l'intervalle [0;"
                            + maxXY + "]!");

        m_coordX = x;
        m_coordY = y;
    }

    /**
     * Retourne la taille de l'image de la carte pour le niveau de zoom donné.
     * 
     * @param zoom
     *            Le niveau de zoom utilisé.
     * @return La taille de l'image de la carte.
     * @throws IllegalArgumentException
     *             Si le zoom est négatif.
     */
    public static int maxXY(int zoom) throws IllegalArgumentException {
        if (zoom < 0)
            throw new IllegalArgumentException(
                    "Error : Zoom invalide car négatif !");

        return (int) pow(2, zoom + 8);
    }

    /**
     * Retourne la valeur de la coordonnée X.
     * 
     * @return La valeur de la coordonnée X.
     */
    public double x() {
        return m_coordX;
    }

    /**
     * Retourne la valeur de la coordonnée Y.
     * 
     * @return La valeur de la coordonnée Y.
     */
    public double y() {
        return m_coordY;
    }

    /**
     * Retourne l'entier le plus proche de la valeur de la coordonnée X.
     * 
     * @return L'entier le plus proche de la valeur de la coordonnée X.
     */
    public int roundedX() {
        return (int) round(m_coordX);
    }

    /**
     * Retourne l'entier le plus proche de la valeur de la coordonnée Y.
     * 
     * @return L'entier le plus proche de la valeur de la coordonnée Y.
     */
    public int roundedY() {
        return (int) round(m_coordY);
    }

    /**
     * Retourne la valeur du zoom utilisé dans le système OSM.
     * 
     * @return La valeur du zoom utilisé dans le système OSM.
     */
    public int zoom() {
        return m_zoom;
    }

    /**
     * Retourne un nouveau point dans le système OSM en fonction du nouveau zoom
     * utilisé.
     * 
     * @param newZoom
     *            Le nouveau zoom désiré.
     * @return Le nouveau point dans le système avec son nouveau zoom.
     * @throws IllegalArgumentException
     *             Si la valeur du zoom est négative.
     */
    public PointOSM atZoom(int newZoom) {
        if (newZoom < 0)
            throw new IllegalArgumentException(
                    "Error : Zoom invalide car négatif !");
        double tmp = pow(2, newZoom - m_zoom);
        double newX = m_coordX * tmp;
        double newY = m_coordY * tmp;

        return new PointOSM(newZoom, newX, newY);
    }

    /**
     * Retourne le point dans le système de coordonnées WGS 84.
     * 
     * @return Un nouveau point dans le système WGS 84.
     */
    public PointWGS84 toWGS84() {
        double s = pow(2, m_zoom + 8);
        double longitude = ((2 * PI) / s) * m_coordX - PI;
        double latitude = atan(sinh(PI - ((2 * PI) / s) * m_coordY));

        return new PointWGS84(longitude, latitude);
    }

    /**
     * Retourne un string comportant respectivement, le zoom, la valeur X, la
     * valeur Y du point demandé.
     * 
     * @return Un string comportant respectivement, le zoom, la valeur X, la
     *         valeur Y du point demandé.
     */
    @Override
    public String toString() {
        return "(" + m_zoom + ", " + m_coordX + ", " + m_coordY + ")";
    }
}

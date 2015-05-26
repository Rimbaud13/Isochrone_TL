/**
 * Classe représentant un point dans le système de coordonnées WGS 84.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.geo;

import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.sqrt;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static ch.epfl.isochrone.math.Math.haversin;
import static ch.epfl.isochrone.math.Math.asinh;

public final class PointWGS84 {

    private final double m_longitude;
    private final double m_latitude;

    /**
     * Construit un point dans le système de coordonnées WGS 84.
     * 
     * @param longitude
     *            La longitude du point dans le système WGS 84.
     * @param latitude
     *            La latitude du point dans le système WGS 84.
     * @throws IllegalArgumentException
     *             Si la valeur de la latitude ou longitude ne sont pas
     *             conformes au système WGS 84.
     */
    public PointWGS84(double longitude, double latitude) {
        if (!(-1 * PI <= longitude && longitude <= PI)
                || !(-1 * PI / 2 <= latitude && latitude <= PI / 2)) {
            throw new IllegalArgumentException(
                    "Error : Longitude ou latitude invalides !");
        }

        m_longitude = longitude;
        m_latitude = latitude;
    }

    /**
     * Retourne la longitude du point dans le système WGS 84.
     * 
     * @return La longitude du point dans le système WGS 84.
     */
    public double longitude() {
        return m_longitude;
    }

    /**
     * Retourne la latitude du point dans le système WGS 84.
     * 
     * @return La latitude du point dans le système WGS 84.
     */
    public double latitude() {
        return m_latitude;
    }

    /**
     * Retourne la distance en mètres du point à un autre point passé en
     * argument.
     * 
     * @param that
     *            Le point à partir du quel on veut mesurer la distance.
     * @return La distance mètres séparant les deux points.
     */
    public double distanceTo(PointWGS84 that) {
        return 2
                * 6378137
                * asin(sqrt(haversin(m_latitude - that.latitude())
                        + cos(m_latitude) * cos(that.latitude())
                        * haversin(m_longitude - that.longitude())));
    }

    /**
     * Retourne le point dans le système de coordonnées OSM selon le zoom passé
     * en argument.
     * 
     * @param zoom
     *            Le zoom utilisé dans le système OSM.
     * @return Le point dans le système OSM.
     * @throws IllegalArgumentException
     *             Si le zoom passé en argument est négatif.
     */
    public PointOSM toOSM(int zoom) {
        if (zoom < 0)
            throw new IllegalArgumentException(
                    "Error : Zoom invalide car négatif !");

        double s = pow(2, zoom + 8);
        double a = s / (2 * PI);
        double x = a * (m_longitude + PI);
        double y = a * (PI - asinh(tan(m_latitude)));

        return new PointOSM(zoom, x, y);
    }

    /**
     * Retourne un string contenant respectivement la longitude et latitude du
     * point.
     * 
     * @return Un string contenant respectivement la longitude et latitude du
     *         point.
     */
    @Override
    public String toString() {
        return "(" + toDegrees(m_longitude) + ",   " + toDegrees(m_latitude) + ")";
    }
}
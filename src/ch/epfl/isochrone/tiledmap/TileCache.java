/**
 * Classe représentant un cache de Tile.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.tiledmap;

import java.util.LinkedHashMap;
import java.util.Map;

import static ch.epfl.isochrone.geo.PointOSM.maxXY;

public final class TileCache {

    private final static int MAX_SIZE = 100;

    @SuppressWarnings("serial")
    private LinkedHashMap<Long, Tile> m_cache = new LinkedHashMap<Long, Tile>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Tile> e) {
            return size() > MAX_SIZE;
        }
    };

    /**
     * Constructeur de la classe TileCache.
     */
    public TileCache() {
    }

    /**
     * Permet d'ajouter une Tuile au cache de tuile.
     * 
     * @param zoom
     *            Le zoom de la tuile.
     * @param x
     *            La coordonée X de la tuile.
     * @param y
     *            La coordonée Y de la tuile.
     * @param tile
     *            La Tuile elle-même.
     * @throws IllegalArgumentException
     *             Lève l'exception si le zoom est négatif ou si les coordonnées
     *             x ou y sont hors de leur intervalle possible pour le zoom
     *             donné.
     */
    public void put(int zoom, int x, int y, Tile tile) {
        long pack = getPackedTriplet(zoom, x, y);
        m_cache.put(pack, tile);
    }

    /**
     * Permet de récupérer la tuile du cache correspondant aux coordonées et au
     * niveau de zoom donnés.
     * 
     * @param zoom
     *            Le niveau de zoom de la tuile.
     * @param x
     *            La coordonée X de la tuile.
     * @param y
     *            La coordonée Y de la tuile.
     * @throws IllegalArgumentException
     *             Lève l'exception si le zoom est négatif ou si les coordonnées
     *             x ou y sont hors de leur intervalle possible pour le zoom
     *             donné.
     * @return La tuile ci celle-ci est contenue dans le cache de tuile, NULL
     *         sinon.
     */
    public Tile get(int zoom, int x, int y) {
        long pack = getPackedTriplet(zoom, x, y);
        if (m_cache.containsKey(pack)) {
            return m_cache.get(pack);
        } else
            return null;
    }

    private static long getPackedTriplet(int zoom, int x, int y) {
        if (zoom < 0)
            throw new IllegalArgumentException(
                    " Error : Zoom invalide car négatif !");
        int maxXY = maxXY(zoom);

        if (!(0 <= x && x <= maxXY))
            throw new IllegalArgumentException(
                    "Error : Coordonée X, invalide, doit être dans l'intervalle [0;"
                            + maxXY + "]!");

        if (!(0 <= y && y <= maxXY))
            throw new IllegalArgumentException(
                    "Error : Coordonée Y, invalide, doit être dans l'intervalle [0;"
                            + maxXY + "]!");
        
        return (long) (x * Math.pow(10, 9) + y * 100 + zoom);
    }
}

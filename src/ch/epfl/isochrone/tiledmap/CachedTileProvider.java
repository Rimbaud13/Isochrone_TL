/**
 * Classe représentant un fournisseur de tuiles permettant un accès plus rapide aux Tiles par rapport aux autres fournisseurs.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.tiledmap;

public final class CachedTileProvider implements TileProvider {

    private final TileCache m_tileCache;
    private final TileProvider m_tileProvider;

    /**
     * Constructeur de la classe CachedTileProvider.
     * 
     * @param tileProvider
     *            Le fournisseur que l'on veut tansformer.
     */
    public CachedTileProvider(TileProvider tileProvider) {
        m_tileCache = new TileCache();
        m_tileProvider = tileProvider;
    }

    @Override
    /**
     * Redefinition de la méthode tileAt de l'interface TileProvider. Si la tuile correspondant a ces coordonées est contenue dans le cache de tuile cette méthode renvoit la tuile correspondante, sinon elle l'ajoute au cache de tuile avant de la renvoyer.
     * @param zoom  Le niveau de zoom.
     * @param x La coordonnée X. 
     * @param y La coordonée Y.
     * @return  La tuile qui contient la position correspondant aux coordonées données.
     */
    public Tile tileAt(int zoom, int x, int y) {
        Tile returnTile = m_tileCache.get(zoom, x, y);
        if (returnTile != null)
            return returnTile;
        else {
            returnTile = m_tileProvider.tileAt(zoom, x, y);
            m_tileCache.put(zoom, x, y, returnTile);
            return returnTile;
        }
    }

}

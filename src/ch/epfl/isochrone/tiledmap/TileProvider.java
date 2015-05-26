/**
 * Interface décrivant un fournisseur de tuiles.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.tiledmap;

public interface TileProvider {
    
    /**
     * Revoit la tuile en fonction des coordonées et au niveau de zoom donnés.
     * @param zoom  Le niveau de zoom.
     * @param x La coordonnée X.
     * @param y La coordonée Y.
     * @return  La tuile qui contient la position correspondant aux coordonées données.
     */
    public Tile tileAt(int zoom, int x, int y);
    
}

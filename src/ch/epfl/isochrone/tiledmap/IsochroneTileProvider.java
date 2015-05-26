/**
 * Classe représentant un fournisseur de tuile isochrone.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.tiledmap;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Stop;

public final class IsochroneTileProvider implements TileProvider {

    private final FastestPathTree m_pathTree;
    private final ColorTable m_colorTable;
    private final double m_walkingSpeed;

    /**
     * Constructeur de la classe IsochroneTileProvider.
     * @param pathTree  Arbre des plus couts chemin à l'aide du quel ce fournisseur créera les tuiles isochrones.
     * @param colorTable    Instance de ColorTable dont on utilisera les couleurs pour créer les tuile isochrones.
     * @param walkingSpeed  Vitesse de marche.
     * @throws IllegalArgumentException Lève l'exception si la vitesse de marche est inférieure à zéro.
     */
    public IsochroneTileProvider(FastestPathTree pathTree,
            ColorTable colorTable, double walkingSpeed) {
        if(walkingSpeed < 0){
            throw new IllegalArgumentException("Error : La vitesse de marche ne peut pas être ngative !");
        }
        m_pathTree = pathTree;
        m_colorTable = colorTable;
        m_walkingSpeed = walkingSpeed;
    }

    /**
     * Redéfinition de la méthode tileAt de l'interface TileProvider. Cette méthode renvoit maintenant la tuile isochrone en fonctions des coordonées données en paramètres.
     * @param zoom Le niveau de zoom de la tuile.
     * @param x    La coordonée X de la tuile.
     * @param y    La coordonée Y de la tuile.
     * @return  La tuile isochrone correspondant aux coordonées données en paramètres.
     */
    public Tile tileAt(int zoom, int x, int y) {
        
        //Création d'une BufferedImage avec les dimesions d'une tile
        BufferedImage i = new BufferedImage(256, 256,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = i.createGraphics();

        // Remplit avec la couleur de fond
        g.setColor(m_colorTable.getColor(m_colorTable.getNumberOfColors() - 1));
        g.fillRect(0, 0, i.getWidth(), i.getHeight());
        
        //Si il y a plus d'une couleur on dessine couche par couche.
        if (m_colorTable.getNumberOfColors() > 1){
            
            //Initialisation de variables
            int arrivalTime = 0;
            int interval = m_colorTable.getInterval();
            int timeLeft = 0;
            int startingTime = m_pathTree.startingTime();
            
            //Coordonées OSM du centre du système de coordonées de g (Graphics2D)
            int originTileX = x * 256;
            int originTileY = y * 256;
            
            //Construction de deux points OSM à 1 pixel d'écart pour créer l'échelle.
            PointOSM point1 = new PointOSM(zoom, originTileX, originTileY);
            PointOSM point2 = new PointOSM(zoom, originTileX + 1, originTileY);
            PointOSM point3 = new PointOSM(zoom, originTileX, originTileY + 1);
            
            //Création d'une échelle METRES : PIXELS pour le zoom donné
            Double distanceOnePixelX = point1.toWGS84().distanceTo(point2.toWGS84());
            Double distanceOnePixelY = point1.toWGS84().distanceTo(point3.toWGS84());
            
            //Boucle qui dessine tout les cercles couche par couche
            for (int j = m_colorTable.getNumberOfColors() - 2; j >= 0; j--) {
                g.setColor(m_colorTable.getColor(j)); //séléction de la couleur
                for (Stop s : m_pathTree.stops()) { //On dessine pout tout les points
                    arrivalTime = m_pathTree.arrivalTime(s); //calcul du temps d'arrivée
                    if (arrivalTime - startingTime <= (j + 1) * interval) { // on dessine que si le temps d'arrivée est dans l'intervalle de la couleur
                        PointOSM sOSM = s.position().toOSM(zoom);                        
                        timeLeft = (j + 1) * interval - (arrivalTime - startingTime); //calcul du temps restant
                        double walkRadius = m_walkingSpeed * timeLeft; //calcul de la distance que l'on peut encore parcourir à pied
                        int pixelRadiusX = (int) Math.round(walkRadius / distanceOnePixelX); //conversion de la distance (mètres) en pixels pour X
                        int pixelRadiusY = (int) Math.round(walkRadius / distanceOnePixelY); //conversion de la distance (mètres) en pixels pour Y
                        
                        g.fill(new Ellipse2D.Double(sOSM.x() - pixelRadiusX - originTileX, 
                                sOSM.y() -pixelRadiusY - originTileY, pixelRadiusX * 2, pixelRadiusY*2)); //on dessine le cercle
                    }
                }
            }
        }
        g.dispose();
        return new Tile(i, zoom, x, y);
    }

}

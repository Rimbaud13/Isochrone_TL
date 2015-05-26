/**
 * Classe représentant un fournisseur de tuiles OSM.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public final class OSMTileProvider implements TileProvider {

    private final String m_baseServer;

    /**
     * Constructeur de la classe OSMTileProvider.
     * 
     * @param baseServer
     *            l'adresse de base du serveur de Tiles.
     */
    public OSMTileProvider(String baseServer) {
        m_baseServer = baseServer;
    }
    
    /**
     * Autre constructeur de la classe OSMTileProvider.
     * @param url Url de base du serveur de tuile.
     */
    public OSMTileProvider(URL url){
        this(url.toString());
    }

    
    /**
     * Redefinition de la méthode tileAt de l'interface TileProvider. Cette redéfinition permet d'obtenir la tuile à l'aide du server de tuile.
     * @param zoom Le niveau de zoom de la tuile.
     * @param x    La coordonée X de la tuile.
     * @param y    La coordonée Y de la tuile.
     * @return     La tuile correspondant aux coordonées donnés en paramètre.
     */
    @Override
    public Tile tileAt(int zoom, int x, int y) {
        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append(m_baseServer).append(zoom).append("/")
                .append(x).append("/").append(y).append(".png");
        BufferedImage image = null;
        
        try {
            URL url = new URL(urlStringBuilder.toString());
            image = ImageIO.read(url);

        } catch (IOException e) { // MalformedURLException est une sous-classe
                                  // de IOException, les deux exceptions sont
                                  // donc gérées dans ce catch
            try {
                image = ImageIO.read(getClass().getResource(
                        "/images/error-tile.png"));
            } catch (IOException j) {
                // Ce cas n'existe pas.
            }
        }

        return new Tile(image, zoom, x, y);
    }

}

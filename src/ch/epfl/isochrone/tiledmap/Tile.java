/**
 * Classe représentant une tuile.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.tiledmap;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import static ch.epfl.isochrone.geo.PointOSM.maxXY;

public final class Tile {

    private final int m_zoom;
    @SuppressWarnings("unused")
    private final int m_posX;
    @SuppressWarnings("unused")
    private final int m_posY;
    private final BufferedImage m_image;

    /**
     * Constructeur de la tuile correspondant au coordonées et au niveau de zoom
     * donnés.
     * 
     * @param image
     *            L'image de la tuile sous forme d'une instance de la classe
     *            BufferedImage.
     * @param zoom
     *            Le niveau de zoom de la tuile.
     * @param posX
     *            La coordonée X de la tuile.
     * @param posY
     *            La coordonée Y de la tuile.
     * @throws IllegalArgumentException
     *             Lève l'exception si le zoom est négatif ou si les coordonnées
     *             posX ou posY sont hors de leur intervalle possible pour le zoom
     *             donné.
     */
    public Tile(BufferedImage image, int zoom, int posX, int posY) {
        if (zoom < 0)
            throw new IllegalArgumentException(
                    " Error : Zoom invalide car négatif !");
        m_zoom = zoom;
        int maxXY = maxXY(m_zoom);

        if (!(0 <= posX && posX <= maxXY))
            throw new IllegalArgumentException(
                    "Error : Coordonée X, invalide, doit être dans l'intervalle [0;"
                            + maxXY + "]!");

        if (!(0 <= posY && posY <= maxXY))
            throw new IllegalArgumentException(
                    "Error : Coordonée Y, invalide, doit être dans l'intervalle [0;"
                            + maxXY + "]!");
        m_posX = posX;
        m_posY = posY;

        BufferedImage copy = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = copy.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        m_image = copy;
    }

    /**
     * Méthode permettant d'obtenir l'image de l'instance de Tile actuelle sous
     * la forme d'une instance de la classe BufferedImage.
     * 
     * @return Copie de l'instance de BufferedImage contenue dans cette tuile.
     */
    public BufferedImage getTileImage() {
        BufferedImage copy = new BufferedImage(m_image.getWidth(),
                m_image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = copy.getGraphics();
        g.drawImage(m_image, 0, 0, null);
        g.dispose();
        return copy;
    }
}

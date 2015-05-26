/**
 * Classe représentant un composant Swing capable d'afficher une carte en tuiles.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JComponent;

import ch.epfl.isochrone.tiledmap.TileProvider;
import static ch.epfl.isochrone.geo.PointOSM.maxXY;

/**
 * @author Nicolas
 * 
 */
@SuppressWarnings("serial")
public final class TiledMapComponent extends JComponent {

    private int m_zoom;
    private List<TileProvider> m_providers;

    /**
     * Construint un composant Swing qui affiche une carte en tuiles selon le
     * zoom passé en argument
     * 
     * @param zoom
     *            Le zoom auquel on souhaite afficher la carte en tuiles.
     */
    public TiledMapComponent(int zoom) {
        if (!(0 <= zoom && zoom <= 19))
            throw new IllegalArgumentException("Erreur : zoom invalide !");
        m_zoom = zoom;
        m_providers = new ArrayList<>();
    }

    /**
     * Retourne une Dimension qui a la taille « idéale » du composant. Cette
     * taille est simplement celle de la carte du monde au niveau de zoom
     * courant.
     */
    public Dimension getPreferredSize() {
        return new Dimension(maxXY(m_zoom), maxXY(m_zoom));
    }

    /**
     * Retourne le zoom actuel de la carte en tuiles.
     * 
     * @return Le zoom actuel de la carte en tuiles.
     */
    public int zoom() {
        return m_zoom;
    }

    /**
     * Méthode qui change le zoom auquel on veut voir les tuiles.
     * 
     * @param newZoom
     *            Le nouveau zoom que l'on souhaite avoir.
     * 
     * @throws IllegalArgumentException
     *             Si le nouveau zoom n'est pas compris dans l'intervalle [0;
     *             19].
     */
    public void setZoom(int newZoom) {
        if (!(0 <= newZoom && newZoom <= 19))
            throw new IllegalArgumentException("Erreur : zoom invalide !");
        m_zoom = newZoom;
        repaint();
    }

    /**
     * Change les providers de tuiles qui doivent être affichés. La nouvelle
     * liste de providers doit être une list complète et triée, l'affichage se
     * fera ainsi : on affiche la tuile du premier provider de la liste, puis on
     * affiche par-dessus celle-ci la tuile du deuxième provider de la liste, et
     * ainsi de suite jusqu'au dernier provider de la liste.
     * 
     * @param providers
     *            La liste de providers complète et triée dont le composant doit
     *            afficher les tuiles.
     */
    public void setProviders(List<TileProvider> providers) {
        m_providers = new ArrayList<>(providers);
        repaint();
    }

    /**
     * Cette méthode est appelée par Swing chaque fois que le composant doit
     * être redessiné, et donc dessine ce que le composant doit afficher.
     * 
     * @param g0
     *            Le contexte graphique grâce auquel on dessine.
     */
    public void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        Rectangle rect = getVisibleRect();
        int begX, begY, endX, endY;
        begX = (int) rect.getX() / 256;
        begY = (int) (rect.getY() / 256);
        endX = (int) ((rect.getX() + rect.getWidth()) / 256);
        endY = (int) ((rect.getY() + rect.getHeight()) / 256);
        int posX, posY;
        for (int y = begY; y <= endY; y++) {
            for (int x = begX; x <= endX; x++) {
                posX = 256 * x;
                posY = 256 * y;
                for (int i = 0; i < m_providers.size(); i++) {
                    g.drawImage(m_providers.get(i).tileAt(m_zoom, x, y)
                            .getTileImage(), null, posX, posY);
                }
            }
        }
    }
}

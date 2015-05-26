/**
 * Classe représentant un transformateur de fournisseur de tuile.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;

public abstract class FilteringTileProvider implements TileProvider {

    private final TileProvider m_provider;

    /**
     * Constructeur de la classe FilteringTileProvider, qui est un
     * transformateur de TileProvider.
     * 
     * @param provider
     *            Instance de la classe provider a transformer.
     */
    public FilteringTileProvider(TileProvider provider) {
        m_provider = provider;
    }

    /**
     * Méthode abstraite permettant de transformer une couleur donnée sous forme
     * d'un int contenant le canal alpha, le canal rouge, vert, et bleu.
     * 
     * @param argb
     *            La couleur a transformer.
     * @return La nouvelle couleur obtenue après transformation.
     */
    abstract public int transformARGB(int argb);

    /**
     * Redéfinition de la méthode tileAt qui va renvoyer une Tile, du
     * fournisseur de tuile, ayant subit une transformation des couleurs de tous
     * ses pixels via la méthode transformArgb.
     * 
     * @param zoom
     *            Le niveau de zoom.
     * @param x
     *            La coordonnée X.
     * @param y
     *            La coordonée Y.
     * @return La tuile du fournisseur de tuile, transformée.
     */
    @Override
    public Tile tileAt(int zoom, int x, int y) {
        Tile t = m_provider.tileAt(zoom, x, y);
        BufferedImage tImage = t.getTileImage();
        for (int coord_y = 0; coord_y < 256; coord_y++) {
            for (int coord_x = 0; coord_x < 256; coord_x++) {
                tImage.setRGB(coord_x, coord_y,
                        transformARGB(tImage.getRGB(coord_x, coord_y)));
            }
        }
        return new Tile(tImage, zoom, x, y);
    }

}

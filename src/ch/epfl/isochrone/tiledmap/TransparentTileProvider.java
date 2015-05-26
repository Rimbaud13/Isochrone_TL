/**
 * Classe transformant un fournisseur de tuile dans le but de rendre les tuiles plus ou moins transparentes.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.tiledmap;

public final class TransparentTileProvider extends FilteringTileProvider {

    private final double m_opacity;

    /**
     * Constructeur de la classe TransparentTileProvider.
     * 
     * @param opacity
     *            Opacité des tuiles renvoyées par tileAt.
     * @param provider
     *            Instance de TileProvider a transformer.
     * @throws IllegalArgumentException
     *             Lève l'exception si l'opacité passée en argument n'est pas
     *             dans l'intervalle [0;1].
     */
    public TransparentTileProvider(double opacity, TileProvider provider) {
        super(provider);
        if (!(0. <= opacity && opacity <= 1.))
            throw new IllegalArgumentException("Erreur : opacité invalide !");
        m_opacity = opacity;
    }

    /**
     * Redéfinition de la méthode transformARGB de la classe
     * FilteringTileProvider. Cette méthode prend en paramètre un int
     * représentant une couleur et va lui appliquer l'opacité donnée dans le
     * constructeur de cette instance.
     * 
     * @param argb
     *            Couleur a transformer.
     * @return La couleur transformée sous forme d'un int.
     */
    @Override
    public int transformARGB(int argb) {
        return ((argb << 8) >>> 8) | ((int) Math.round(m_opacity * 255) << 24);
    }
}

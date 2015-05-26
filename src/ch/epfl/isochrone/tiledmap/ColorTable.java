/**
 * Classe représentant un cache de Tile.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.tiledmap;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public final class ColorTable {

    private final int m_interval;
    private final List<Color> m_colors;

    /**
     * Constructeur de la classe ColorTable
     * 
     * @param interval
     *            Représente l'interval de durée entre 2 couches de couleur.
     * @param colors
     *            Liste contenant toutes les couleurs utilisée pour afficher la
     *            carte isochrone.
     * @throws IllegalArgumentException
     *             Lève l'exception si la liste de couleurs est vide ou si
     *             l'intervalle entre deux couleurs est un temps négatif ou nul.
     */
    public ColorTable(int interval, List<Color> colors) {
        if (colors.isEmpty()) {
            throw new IllegalArgumentException(
                    "Error : La liste des couleurs est vide !");
        }
        if (interval <= 0) {
            throw new IllegalArgumentException(
                    "Error : L'intervalle de temps entre deux couleurs ne peut être nul ou inférieur à zéro");
        }
        m_interval = interval;
        m_colors = new ArrayList<>(colors);
    }

    /**
     * Méthode permettant de connaitre la durée de l'interval de temps séparant
     * 2 couleurs différentes.
     * 
     * @return L'intervalle de temps séparant 2 couleurs.
     */
    public int getInterval() {
        return m_interval;
    }

    /**
     * Méthode permettant d'obtenir le nombre total de couleur différentes que
     * contient l'instance actuelle.
     * 
     * @return Le nombre de couleurs différentes contenues dans la liste
     *         m_colors.
     */
    public int getNumberOfColors() {
        return m_colors.size();
    }

    /**
     * Méthode permettant d'obtenir la couleur correspondant à l'indice de la
     * tranche donnée en paramètre. L'indice est compris entre 0 et le nombre de
     * couleurs moins un.
     * 
     * @param tranche
     *            Indice de la tranche, c'est à dire l'indice de la liste
     *            contenant la couleur que l'on veut obtenir.
     * @throws IllegalArgumentException
     *             L'exception est levée si l'indice de la tranche demandée
     *             n'est pas pas dans l'intervalle [0, nombre de couleurs -1].
     * @return La couleur correspondant à cette tranche.
     */
    public Color getColor(int tranche) {
        if (!(0 <= tranche && tranche <= m_colors.size() - 1))
            throw new IllegalArgumentException(
                    "Error : Index de tranche invalide !");
        return m_colors.get(tranche);
    }
}

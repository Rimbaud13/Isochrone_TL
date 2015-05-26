/**
 * Classe permettant la représentation de l'heure en secondes après minuits.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.timetable;

public final class SecondsPastMidnight {

    /**
     * Constante définissant un temps infini en secondes.
     */
    public static final int INFINITE = 200000;

    private SecondsPastMidnight() {
    }

    /**
     * Convertis une heure donnée en heures, minutes et secondes en secondes
     * uniquement.
     * 
     * @param hours
     *            Le nombre de heures.
     * @param minutes
     *            Le nombre de minutes.
     * @param seconds
     *            Le nombre de secondes.
     * @return La conversion de l'heure passée en paramètre en secondes.
     * @throws IllegalArgumentException
     *             Si un des paramètres est invalide, c'est-à-dire si l'un des
     *             paramètres ne fais pas partie d'un certain intervalle.
     */
    public static int fromHMS(int hours, int minutes, int seconds) {
        if (!(0 <= seconds && seconds < 60))
            throw new IllegalArgumentException("Secondes invalides !");

        if (!(0 <= minutes && minutes < 60))
            throw new IllegalArgumentException("Minutes invalides !");

        if (!(0 <= hours && hours < 30))
            throw new IllegalArgumentException("Heures invalides !");

        return 3600 * hours + 60 * minutes + seconds;
    }

    /**
     * Retourne en secondes l'heure donnée en java.util.Date.
     * 
     * @param date
     *            L'heure en java.util.date.
     * @return Le nombre de secondes correspondant à l'heure passée en paramètre
     *         en java.util.Dat.
     */
    @SuppressWarnings("deprecation")
    public static int fromJavaDate(java.util.Date date) {
        int sec = date.getSeconds();
        if (sec >= 60)
            sec = 59;
        return fromHMS(date.getHours(), date.getMinutes(), sec);
    }

    /**
     * Retourne le nombre d'heures (en entier) contenues dans un temps donné en
     * secondes.
     * 
     * @param spm
     *            Le nombre de secondes.
     * @return Le nombre d'heures contenues dans le nombre de secondes passé en
     *         paramètre.
     * @throws IllegalArgumentException
     *             Si le nombre de secondes passé en paramètre est invalide
     *             (inférieur à zéro, ou supérieur à 30 heures).
     */
    public static int hours(int spm) {
        if (spm < 0 || spm > 107999) // 107999 = 29h59m59s
            throw new IllegalArgumentException("Heure invalide !");

        return spm / 3600; // 1 hour = 3600 seconds
    }

    /**
     * Retourne le nombre de minutes (en entier) après une conversion d'un
     * nombre de secondes en heures, mintues, secondes.
     * 
     * @param spm
     *            Le nombre de secondes.
     * @return Le nombre de minutes après la conversion.
     * @throws IllegalArgumentException
     *             Si le nombre de secondes passé en paramètre est invalide
     *             (inférieur à zéro, ou supérieur à 30 heures).
     */
    public static int minutes(int spm) {
        if (spm < 0 || spm > 107999) // 107999 = 29h59m59s
            throw new IllegalArgumentException("Heure invalide !");

        return ((spm - hours(spm) * 3600) / 60);
    }

    /**
     * Retourne le nombre de secondes (en entier) après une conversion d'un
     * nombre de secondes en heures, mintues, secondes. * @param spm Le nombre
     * de secondes.
     * 
     * @return Le nombre de secondes après la conversion.
     * @throws IllegalArgumentException
     *             Si le nombre de secondes passé en paramètre est invalide
     *             (inférieur à zéro, ou supérieur à 30 heures).
     */
    public static int seconds(int spm) {
        if (spm < 0 || spm > 107999) // 107999s = 29h59m59s
            throw new IllegalArgumentException("Heure invalide !");

        return spm - hours(spm) * 3600 - minutes(spm) * 60;
    }

    /**
     * Convertis un nombre de secondes en une String de type : "hh:mm:ss".
     * 
     * @param spm
     *            Le nombre de secondes à convertir.
     * @return Une string de type : "hh:mm:ss" selon le nombre de secondes
     *         passés en paramètre.
     */
    public static String toString(int spm) {
        if (spm < 0 || spm > 107999) // 107999s = 29h59m59s
            throw new IllegalArgumentException("Heure invalide !");

        return String.format("%02d:%02d:%02d", hours(spm), minutes(spm),
                seconds(spm));
    }
}

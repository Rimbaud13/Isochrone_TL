/**
 * Classe premettant la représentation des Dates.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.timetable;

import static ch.epfl.isochrone.math.Math.divF;
import static ch.epfl.isochrone.math.Math.modF;

public final class Date implements Comparable<Date> {

    private final int m_day;
    private final Month m_month;
    private final int m_year;

    /**
     * Enumeration représentant les jours de la semaine.
     * 
     * @author Justinien Bouron (236608)
     * @author Nicolas Roussel (238333)
     */
    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    /**
     * Enumeration représentant les mois.
     * 
     * @author Justinien Bouron (236608)
     * @author Nicolas Roussel (238333)
     */
    public enum Month {
        JANUARY(31), FEBRUARY(29), // e_maxDays dans FEBRUARY ne sera pas utile
        // car il dépends de l'année.
        MARCH(31), APRIL(30), MAY(31), JUNE(30), JULY(31), AUGUST(31), SEPTEMBER(
                30), OCTOBER(31), NOVEMBER(30), DECEMBER(31);

        private final int e_maxDays;

        private Month(int maxDays) {
            e_maxDays = maxDays;
        }

        /**
         * Retourne le nombre maximal de jours pour le Month (retourne 29 dans
         * le cas de février).
         * 
         * @return Le nombre maximal de jours poue le Month (retourne 29 dans le
         *         cas de février)
         */
        public int getMaxDays() {
            return e_maxDays;
        }
    }

    /**
     * Constructeur principal de la classe Date. C'est celui qui sera appellé
     * par les autres constructeurs surchargés.
     * 
     * @param day
     *            Le jour de la Date que l'on veut créer. De type Int.
     * @param month
     *            Le mois de la Date que l'on veut créer. De type Month.
     * @param year
     *            L'année de la Date que l'on veut créer. De type Int.
     * @throws IllegalArgumentException
     *             L'exception est levée si le jours est invalide (négatif ou
     *             supérieur au nombre de jours dans ce mois).
     */
    public Date(int day, Month month, int year) {
        if (!(1 <= day && day <= daysInMonth(month, year)))
            throw new IllegalArgumentException("Jour invalide !");

        m_day = day;
        m_month = month;
        m_year = year;
    }

    /**
     * Premier constructeur surchargé. La différence avec le constructeur
     * principal est qu'il prend en paramètre un int pour le mois de la Date que
     * l'on veut construire.
     * 
     * @param day
     *            Le jour de la Date que l'on veut créer. De type Int.
     * @param month
     *            Le mois de la Date que l'on veut créer. De type int.
     * @param year
     *            L'année de la Date que l'on veut créer. De type Int.
     * @throws IllegalArgumentException
     *             Si le nombre du jour ou mois est invalide.
     */
    public Date(int day, int month, int year) {
        this(day, intToMonth(month), year);
    }

    /**
     * Deuxième constructeur surchargé. Construit une instance de Classe Date a
     * partir d'une instance de Classe java.util.Date.
     * 
     * @param date
     *            Instance de classe java.util.Date.
     */
    @SuppressWarnings("deprecation")
    public Date(java.util.Date date) {
        this(date.getDate(), date.getMonth() + 1, date.getYear() + 1900);
    }

    /**
     * Renvoit le jour de la Date.
     * 
     * @return Le jour de la Date.
     */
    public int day() {
        return m_day;
    }

    /**
     * Renvoit le mois de la Date.
     * 
     * @return Le mois de la Date.
     */
    public Month month() {
        return m_month;
    }

    /**
     * Renvoit l'année de la Date.
     * 
     * @return L'année de la Date.
     */
    public int year() {
        return m_year;
    }

    /**
     * Renvoit le jour de la semaine correspondant a cette Date sous forme d'une
     * enumeration.
     * 
     * @return Enumeration du jour de la semaine correspondant a cette date.
     */
    public DayOfWeek dayOfWeek() {
        return DayOfWeek.values()[modF(fixed() - 1, 7)];
    }

    /**
     * Retourne la Date correspondante a cette Date au quel on ajoute un nombre
     * de jours donné en paramètre.
     * 
     * @param daysDiff
     *            Nombre de jours depuis cette Date.
     * @return Instance de la Classe Date distante de daysDiff jours a
     *         l'instance courante.
     */
    public Date relative(int daysDiff) {
        return fixedToDate(dateToFixed(m_day, m_month, m_year) + daysDiff);
    }

    /**
     * Renvoit le mois de la date sous forme d'un int.
     * 
     * @return L'indice du mois de la Date.
     */
    public int intMonth() {
        return monthToInt(m_month);
    }

    /**
     * Retourne une instance java.util.Date représentant la même date que
     * l'instance courante.
     * 
     * @return Date sous une instance java.util.Date.
     */
    @SuppressWarnings("deprecation")
    public java.util.Date toJavaDate() {
        return new java.util.Date(m_year - 1900, monthToInt(m_month) - 1, m_day);
    }

    /**
     * Retourne une string représentant cette date, sous la forme suivante:
     * "année-mois-jour".
     */
    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", m_year, monthToInt(m_month),
                m_day);
    }

    /**
     * Redefinition de la méthode Equals() de la classe Object.
     */
    @Override
    public boolean equals(Object that) {
        if(that == null){
            return false;
        }
        else{
            return (that.getClass() == this.getClass())
                     && this.fixed() == ((Date)that).fixed();
        }
    }

    /**
     * Compare l'instance courante avec une instance de Date donnée en
     * paramètre.
     * 
     * @param that
     *            L'instance avec laquelle on réalise la comparaison.
     * @return Retourne -1 si l'instance courante est strictement inférieure a
     *         la date passée en argument. Retourne 1 si elle est strictement
     *         supérieure. Retourne 0 si les deux dates sont les mêmes.
     */
    public int compareTo(Date that) {
        return Integer.compare(this.fixed(), that.fixed());
    }

    /**
     * Redefinition de la méthode hashCode() de Object.
     */
    @Override
    public int hashCode() {
        return this.fixed();
    }

    /**
     * Appelle la méthode dateToFixed() et revoit la valeur retournée par cette
     * dernière.
     * 
     * @return La valeur retournée par la méthode dateToFixed().
     */
    private int fixed() {
        return dateToFixed(m_day, m_month, m_year);
    }

    /**
     * Retourne le numéro du mois donné en paramètre. Cette méthode fait
     * simplement l'inverse de la méthode intToMonth. C'est a dire que l'on
     * donne un mois et la méthode retourne un Int correspondant au nombre de ce
     * mois dans la notation standard des dates.
     * 
     * @param m
     *            Le mois.
     * @return Le nombre associé au mois donné en paramètre.
     */
    private static int monthToInt(Month m) {
        return m.ordinal() + 1;
    }

    /**
     * Indique si l'année donné est bissextile.
     * 
     * @param y
     *            L'année dont on veut savoir la bissextilité.
     * @return True si l'année est bissextile, False sinon.
     */
    private static boolean isLeapYear(int y) {
        return ((y % 4 == 0) && !(y % 100 == 0)) || (y % 400 == 0);
    }

    /**
     * Renvoit le mois associé au nombre donné en paramètre. La numérotation est
     * telle que le mois de Janvier correspond au nombre 1, Février au nombre 2,
     * .... (voir enumeration Month).
     * 
     * @param m
     *            Le numéro du mois.
     * @return Le mois correspondant.
     * @throws IllegalArgumentException
     *             Si l'entier passé en argument ne fait pas partie de
     *             l'intervalle [1;12].
     */
    private static Month intToMonth(int m) {
        if (m < 1 || m > 12) {
            throw new IllegalArgumentException(
                    "Error : L'entier ne fait pas partie de l'intervalle [1;12].");
        }

        return Month.values()[m - 1];
    }

    /**
     * Renvoit le nombre de jours que contient un mois sous forme de int. Prend
     * également en paramètre l'année en raison de la variance du nombre de
     * jours du mois de Février selon si l'année est bissextile ou non.
     * 
     * @param m
     *            Le mois dont on veut savoir le nombre de jours.
     * @param y
     *            L'année correspondante.
     * @return Le nombre maximal de jours de ce mois durant cette année.
     */
    private static int daysInMonth(Month m, int y) {
        if (m == Month.FEBRUARY) {
            if (isLeapYear(y))
                return 29;
            else
                return 28;
        } else
            return m.getMaxDays();
    }

    /**
     * Revoit une instance de la classe Date correspondant au nombre donnée en
     * paramètre. Ce nombre est le nombre de jours écoulés depuis le 01/01/01 et
     * cette Date. Cette méthode est l'inverse de la méthode précédente. Plus
     * précisément elle utilise l'inverse de la fonction g pour calculer cette
     * Date.
     * 
     * @param n
     *            Le nombre de jours écoulés depuis le 01/01/01 et cette Date.
     * @return Une instance de la classe Date correspondant a ce nombre.
     */
    private static Date fixedToDate(int n) {
        int d0 = n - 1;
        int n400 = divF(d0, 146097);
        int d1 = modF(d0, 146097);
        int n100 = divF(d1, 36524);
        int d2 = modF(d1, 36524);
        int n4 = divF(d2, 1461);
        int d3 = modF(d2, 1461);
        int n1 = divF(d3, 365);
        int y0 = 400 * n400 + 100 * n100 + 4 * n4 + n1;

        int y = 0;
        if (n100 == 4 || n1 == 4)
            y = y0;
        else
            y = y0 + 1;

        int p = n - dateToFixed(1, Month.JANUARY, y);
        int c = 0;
        if (n < dateToFixed(1, Month.MARCH, y))
            c = 0;
        else if (n >= dateToFixed(1, Month.MARCH, y) && isLeapYear(y))
            c = 1;
        else
            c = 2;

        int m = divF((12 * (p + c) + 373), 367);

        int d = n - dateToFixed(1, intToMonth(m), y) + 1;

        return new Date(d, intToMonth(m), y);
    }

    /**
     * Renvoit, sous forme de Int, le nombre de jours écoulés entre le 01/01/01
     * et la date passée en paramètre, a l'aide de la formule g pésentée dans
     * l'Etape 2. Ce nombre est alors utile pour réaliser des calculs avec des
     * dates.
     * 
     * @param d
     *            Le jours de la date.
     * @param m
     *            Le mois de la date.
     * @param y
     *            L'année de la date.
     * @return Le nombre de jours entre le 01/01/01 et cette date sous forme de
     *         Int.
     */
    private static int dateToFixed(int d, Month m, int y) {
        int y0 = y - 1;

        int c = 0;
        if (monthToInt(m) <= 2)
            c = 0;
        else if (monthToInt(m) > 2 && isLeapYear(y))
            c = -1;
        else
            c = -2;

        int g = 365 * y0 + divF(y0, 4) - divF(y0, 100) + divF(y0, 400)
                + divF((367 * monthToInt(m) - 362), 12) + c + d;
        return g;
    }
}

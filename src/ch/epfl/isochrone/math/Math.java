/**
 * Classe regroupant des formules mathématiques.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */

package ch.epfl.isochrone.math;

import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.sin;
import static java.lang.Integer.signum;

public final class Math {

    private Math() {
    }

    /**
     * Retourne le sinus hyperbolique inverse de la valeur passée en argument.
     * 
     * @param x
     *            La valeur dont on souhaite le sinus hyperbolique inverse.
     * @return Le sinus hyperboloqie inverse de la valeur passée en argument.
     */
    public static double asinh(double x) {
        return log(x + sqrt(1 + x * x));
    }

    /**
     * Retourne le haversin de la valeur passée en argument.
     * 
     * @param x
     *            La valeur dont on souhaite le haversin.
     * @return Le haversin de la valeur passée en argument.
     */
    public static double haversin(double x) {
        return pow(sin(x / 2), 2);
    }

    /**
     * Retourne le quotient d'une division par défaut.
     * 
     * @param n
     *            Le dividende de la division.
     * @param d
     *            Le diviseur de la division.
     * @return Le quotient de la division.
     */
    public static int divF(int n, int d) {
        int qt = n / d;
        int rt = n % d;
        int I;
        if (signum(rt) == -1 * signum(d))
            I = 1;
        else
            I = 0;
        return qt - I;
    }

    /**
     * Retourne le reste d'une division par défaut.
     * 
     * @param n
     *            Le dividende de la division.
     * @param d
     *            Le diviseur de la division.
     * @return Le reste de la division.
     */
    public static int modF(int n, int d) {
        int rt = n % d;
        int I;
        if (signum(rt) == -1 * signum(d))
            I = 1;
        else
            I = 0;
        return rt + I * d;
    }
}

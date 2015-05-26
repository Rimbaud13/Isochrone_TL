/**
 * Classe principale contenant la méthode main.
 * 
 * @author Justinien Bouron (236608)
 * @author Nicolas Roussel (238333)
 */


package ch.epfl.isochrone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Graph;
import ch.epfl.isochrone.timetable.SecondsPastMidnight;
import ch.epfl.isochrone.timetable.Stop;
import ch.epfl.isochrone.timetable.TimeTable;
import ch.epfl.isochrone.timetable.TimeTableReader;

public class TimeTableSearch {

    public static void main(String[] args) throws IOException {

        //Récupération de tout les arguments passés au main.
        String arret = args[0];
        String date = args[1];
        String heure = args[2];

        TimeTableReader reader = new TimeTableReader("/time-table/");

        TimeTable timetable = reader.readTimeTable();

        String[] champsDate;
        champsDate = date.split("-");

        Date dateDep = new Date(Integer.parseInt(champsDate[2]),
                Integer.parseInt(champsDate[1]),
                Integer.parseInt(champsDate[0]));

        Graph graph = reader.readGraphForServices(timetable.stops(),
                timetable.servicesForDate(dateDep), 300, 1.25);

        Stop depart = null;

        for (Stop i : timetable.stops()) {
            if (i.name().equals(arret)) {
                depart = i;
                break;
            }
        }

        String[] champsHeure;
        champsHeure = heure.split(":");

        int heureDep = SecondsPastMidnight.fromHMS(
                Integer.parseInt(champsHeure[0]),
                Integer.parseInt(champsHeure[1]),
                Integer.parseInt(champsHeure[2]));

        //Création du FastestPathTree pour l'heure de départ et l'arrêt de départ
        FastestPathTree fastestPath = graph.fastestPaths(depart, heureDep);

        //Création des String à afficher
        ArrayList<String> list = new ArrayList<>();
        for (Stop i : fastestPath.stops()) {
            if(fastestPath.arrivalTime(i) < SecondsPastMidnight.INFINITE){
                list.add(i.name()
                        + " : "
                        + SecondsPastMidnight.toString(fastestPath
                                .arrivalTime(i)) + "\n    via : " + fastestPath.pathTo(i));
            }                               
        }

        //Triage par ordre alphabétique
        Collections.sort(list);

        //Affichage
        for (String i : list)
        {
            System.out.println(i);
        }

    }
}

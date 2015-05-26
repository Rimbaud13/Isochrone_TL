/**
 * Classe permettant la lecture de fichiers pour la création des horaires et du graphe.
 * 
 * @author Nicolas Roussel (238333)
 * @author Justinien Bouron (236608)
 */

package ch.epfl.isochrone.timetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.PI;

import ch.epfl.isochrone.geo.PointWGS84;

public final class TimeTableReader {

    private final String m_baseResourceName;

    /**
     * Constructeur de la classe TimeTableReader qui lira les fichiers
     * 
     * @param baseResourceName<
     *            préfixe des fichiers qui sera placé avant le nom du fichier.
     */
    public TimeTableReader(String baseResourceName) {
        m_baseResourceName = baseResourceName;
    }

    /**
     * Méthode permettant la lecture des fichiers stops.csv et calendar.csv
     * ainsi que la création des services et des stops décris dans ces fichiers.
     * 
     * @return Instance de la classe TimeTable possedant les services et les
     *         stops donnés dans les fichiers.
     * @throws IOException
     *             Si le BufferedReader rencontre une IOException.
     */
    public TimeTable readTimeTable() throws IOException {
        try {
            TimeTable.Builder timetable = new TimeTable.Builder();
            // ajout des services
            for (Service i : ReadServices()) {
                timetable.addService(i);
            }
            // ajout de tous les arrets :
            for (Stop i : ReadStops()) {
                timetable.addStop(i);
            }
            // création de la TimeTable :
            return timetable.build();
        } catch (IOException e) {
            throw e;
        }
    }

    private Set<Stop> ReadStops() throws IOException {
        try {
            String fileName = m_baseResourceName + "stops.csv";
            InputStream inStream = getClass().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, StandardCharsets.UTF_8));
            String[] champs;
            String line;
            Set<Stop> stops = new HashSet<>();
            double longitude, latitude;

            while ((line = reader.readLine()) != null) {
                champs = line.split(";");
                latitude = (Double.parseDouble(champs[1].substring(0)) / 180.0)
                        * PI;
                longitude = (Double.parseDouble(champs[2].substring(0)) / 180.0)
                        * PI;
                stops.add(new Stop(champs[0], new PointWGS84(longitude,
                        latitude)));
            }
            reader.close();
            return stops;
        } catch (IOException e) {
            throw e;
        }
    }

    private Set<Service> ReadServices() throws IOException {
        try {
            String fileName = m_baseResourceName + "calendar.csv";
            InputStream inStream = getClass().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, StandardCharsets.UTF_8));
            String[] champs;
            String line;
            Map<String, Service.Builder> services = new HashMap<>();
            Date startingDate;
            Date endingDate;

            while ((line = reader.readLine()) != null) {
                // création du builder de service avec les dates de debut et de
                // fin
                champs = line.split(";");
                startingDate = new Date(Integer.parseInt(champs[8].substring(6,
                        8)), Integer.parseInt(champs[8].substring(4, 6)),
                        Integer.parseInt(champs[8].substring(0, 4)));
                endingDate = new Date(Integer.parseInt(champs[9]
                        .substring(6, 8)), Integer.parseInt(champs[9]
                                .substring(4, 6)), Integer.parseInt(champs[9]
                                        .substring(0, 4)));

                services.put(champs[0], new Service.Builder(champs[0],
                        startingDate, endingDate));
                // ajout des jours de services
                for (int i = 1; i <= 7; i++) {
                    if (Integer.parseInt(champs[i].substring(0, 1)) == 1) {
                        services.get(champs[0]).addOperatingDay(
                                Date.DayOfWeek.values()[i - 1]);
                    }
                }
            }

            // lecture des exceptions dans le fichier calendar_dates.csv
            fileName = m_baseResourceName + "calendar_dates.csv";
            inStream = getClass().getResourceAsStream(fileName);
            reader = new BufferedReader(new InputStreamReader(inStream,
                    StandardCharsets.UTF_8));
            Date exceptionDate;

            while ((line = reader.readLine()) != null) {
                champs = line.split(";");
                exceptionDate = new Date(Integer.parseInt(champs[1].substring(
                        6, 8)), Integer.parseInt(champs[1].substring(4, 6)),
                        Integer.parseInt(champs[1].substring(0, 4)));
                if (Integer.parseInt(champs[2].substring(0)) == 1)
                    services.get(champs[0]).addIncludedDate(exceptionDate);
                else
                    services.get(champs[0]).addExcludedDate(exceptionDate);
            }

            // création du Set des services :
            Set<Service> servicesSet = new HashSet<>();
            for (Service.Builder i : services.values()) {
                servicesSet.add(i.build());
            }
            reader.close();
            return servicesSet;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Méthode permettant la création d'une instance de la classe Graph
     * possèdant les trajets contenus dans stop_times.csv et appartenant a l'un
     * des services donnés en argument.
     * 
     * @param stops
     *            Ensemble des arrêts du Graphe.
     * @param services
     *            Ensemble des services utilisés pour générer tous les trajets
     *            du Graphe.
     * @param walkingTime
     *            Temps de marche maximum, il sera utile pour générer tous les
     *            trajets qu'il est possible de faire a pied.
     * @param walkingSpeed
     *            Vitesse de marche durant les trajets a pied.
     * @return Instance de la classe Graph auquel on a ajouté tous les trajets.
     * @throws IOException
     *             Si le BufferedReader rencontre une IOException.
     */
    public Graph readGraphForServices(Set<Stop> stops, Set<Service> services,
            int walkingTime, double walkingSpeed) throws IOException {
        try {
            String fileName = m_baseResourceName + "stop_times.csv";
            InputStream inStream = getClass().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, StandardCharsets.UTF_8));
            String[] champs;
            String line;

            Graph.Builder graph = new Graph.Builder(stops);
            Stop depart = null, arrivee = null;

            HashMap<String, Stop> mapStringStop = new HashMap<>();
            for (Stop i : stops) {
                mapStringStop.put(i.name(), i);
            }

            HashMap<String, Service> mapStringService = new HashMap<>();
            for (Service i : services) {
                mapStringService.put(i.name(), i);
            }

            while ((line = reader.readLine()) != null) {
                champs = line.split(";");
                if (services.contains(mapStringService.get(champs[0]))) {
                    if (mapStringStop.containsKey(champs[1])
                            && mapStringStop.containsKey(champs[3])) {
                        depart = mapStringStop.get(champs[1]);
                        arrivee = mapStringStop.get(champs[3]);
                        graph.addTripEdge(depart, arrivee,
                                Integer.parseInt(champs[2]),
                                Integer.parseInt(champs[4]));
                    }

                }
            }

            reader.close();
            graph.addAllWalkEdges(walkingTime, walkingSpeed);
            return graph.build();
        } catch (IOException e) {
            throw e;
        }
    }
}

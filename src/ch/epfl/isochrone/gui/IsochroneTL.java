package ch.epfl.isochrone.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SeparatorUI;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.tiledmap.CachedTileProvider;
import ch.epfl.isochrone.tiledmap.ColorTable;
import ch.epfl.isochrone.tiledmap.FilteringTileProvider;
import ch.epfl.isochrone.tiledmap.IsochroneTileProvider;
import ch.epfl.isochrone.tiledmap.LineProvider;
import ch.epfl.isochrone.tiledmap.OSMTileProvider;
import ch.epfl.isochrone.tiledmap.TileProvider;
import ch.epfl.isochrone.tiledmap.TransparentTileProvider;
import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Graph;
import ch.epfl.isochrone.timetable.Stop;
import ch.epfl.isochrone.timetable.TimeTable;
import ch.epfl.isochrone.timetable.TimeTableReader;
import ch.epfl.isochrone.timetable.Date.Month;
import ch.epfl.isochrone.timetable.SecondsPastMidnight;

public final class IsochroneTL {
    private static final String OSM_TILE_URL = "http://b.tile.openstreetmap.org/";
    private static final int INITIAL_ZOOM = 11;
    private static final PointWGS84 INITIAL_POSITION = new PointWGS84(
            Math.toRadians(6.476), Math.toRadians(46.613));
    private static final String INITIAL_STARTING_STOP_NAME = "Lausanne-Gare";
    private static final int INITIAL_DEPARTURE_TIME = SecondsPastMidnight
            .fromHMS(6, 8, 0);
    private static final Date INITIAL_DATE = new Date(1, Month.OCTOBER, 2013);
    private static final int WALKING_TIME = 5 * 60;
    private static final double WALKING_SPEED = 1.25;
    private Stop m_selectedStop;
    private Point m_positionBPoint;
    private Point m_positionSouris;
    private Boolean m_isPressed = false;
    private Date m_selectedDate;
    private int m_selectedTime;
    private Graph m_graph;
    private TimeTable m_timetable;
    private TimeTableReader m_reader;
    private FastestPathTree m_fastestPath;
    private TileProvider m_bgTileProvider;
    private TileProvider m_fgTileProvider;
    private LineProvider m_lineProvider;
    private ColorTable m_colorTable;
    private TiledMapComponent m_tiledMapComponent;
    private int m_zoom;

    /* Variables pour bonus */
    private Stop m_destination = null;
    private final JTextArea m_pathText = new JTextArea();
    private final JScrollPane m_pathArea = new JScrollPane(m_pathText);
    private static final String LINE_JUMP = System.getProperty("line.separator");

    /**
     * Constructeur de la classe IsochroneTL.
     * 
     * @throws IOException
     *             Dans le cas où les fichiers contenus dans /time-table/
     *             n'aurais pas pu être lus.
     */
    public IsochroneTL() throws IOException {

        Color color0 = new Color(0.0f, 0.0f, 0.0f);
        Color color1 = new Color(0.0f, 0.0f, 0.5f);
        Color color2 = new Color(0.0f, 0.0f, 1.0f);
        Color color3 = new Color(0.0f, 0.5f, 0.5f);
        Color color4 = new Color(0.0f, 1.0f, 0.0f);
        Color color5 = new Color(0.5f, 1.0f, 0.0f);
        Color color6 = new Color(1.0f, 1.0f, 0.0f);
        Color color7 = new Color(1.0f, 0.5f, 0.0f);
        Color color8 = new Color(1.0f, 0.0f, 0.0f);

        ArrayList<Color> colorList = new ArrayList<>(0);
        colorList.add(color8);
        colorList.add(color7);
        colorList.add(color6);
        colorList.add(color5);
        colorList.add(color4);
        colorList.add(color3);
        colorList.add(color2);
        colorList.add(color1);
        colorList.add(color0);

        m_colorTable = new ColorTable(300, colorList);

        m_reader = new TimeTableReader("/time-table/");
        m_timetable = m_reader.readTimeTable();

        m_graph = m_reader.readGraphForServices(m_timetable.stops(),
                m_timetable.servicesForDate(INITIAL_DATE), WALKING_TIME,
                WALKING_SPEED);

        String startingStopName = INITIAL_STARTING_STOP_NAME;

        for (Stop i : m_timetable.stops()) {
            if (i.name().equals(startingStopName)) {
                m_selectedStop = i;
                break;
            }
        }
        m_destination = m_selectedStop;

        m_zoom = INITIAL_ZOOM;
        m_selectedTime = INITIAL_DEPARTURE_TIME;
        m_selectedDate = INITIAL_DATE;

        // Création du FastestPathTree pour l'heure de départ et l'arrêt de
        // départ
        m_fastestPath = m_graph.fastestPaths(m_selectedStop,
                INITIAL_DEPARTURE_TIME);

        m_bgTileProvider = new CachedTileProvider(new OSMTileProvider(new URL(
                OSM_TILE_URL)));
        IsochroneTileProvider isoTileProvider = new IsochroneTileProvider(
                m_fastestPath, m_colorTable, WALKING_SPEED);
        FilteringTileProvider filteredTileProvider = new TransparentTileProvider(
                0.5, isoTileProvider);

        m_fgTileProvider = new CachedTileProvider(filteredTileProvider);
        m_tiledMapComponent = new TiledMapComponent(INITIAL_ZOOM);
        ArrayList<TileProvider> tileProviders = new ArrayList<>();
        tileProviders.add(m_bgTileProvider);
        tileProviders.add(m_fgTileProvider);
        
        /* bonus */
        m_pathText.setEditable(false);          
        m_lineProvider = new LineProvider(new ArrayList<>(m_fastestPath.pathTo(m_destination)));
        tileProviders.add(m_lineProvider);
        
        m_tiledMapComponent.setProviders(tileProviders);
        
        changeItineraryText();
        updateLine();
    }

    private void updateStop(Stop newSelectedStop) {
        if (!newSelectedStop.equals(m_selectedStop)) {
            m_selectedStop = newSelectedStop;
            updateFastestPath();
        }
    }

    private void updateGraph(Date newDate) throws IOException {
        m_graph = m_reader.readGraphForServices(m_timetable.stops(),
                m_timetable.servicesForDate(newDate), WALKING_TIME,
                WALKING_SPEED);
        updateFastestPath();
    }

    private void updateDeparture(Date date, int newTime) throws IOException {
        if (date.relative(-1).equals(m_selectedDate)
                && SecondsPastMidnight.hours(newTime) < 4
                && SecondsPastMidnight.fromHMS(24, 0, 0) + newTime == m_selectedTime) {
            return;
        } else if (date.equals(m_selectedDate)
                && SecondsPastMidnight.hours(newTime) >= 4
                && newTime == m_selectedTime) {
            return;
        } else if (date.relative(-1).equals(m_selectedDate)
                && SecondsPastMidnight.hours(newTime) < 4) {
            m_selectedTime = SecondsPastMidnight.fromHMS(24, 0, 0) + newTime;
            updateFastestPath();
        } else if (date.equals(m_selectedDate)
                && SecondsPastMidnight.hours(newTime) >= 4) {
            m_selectedTime = newTime;
            updateFastestPath();
        } else if (!date.equals(m_selectedDate)
                && SecondsPastMidnight.hours(newTime) >= 4) {
            m_selectedDate = date;
            m_selectedTime = newTime;
            updateGraph(m_selectedDate);
        } else if (!date.relative(-1).equals(m_selectedDate)
                && SecondsPastMidnight.hours(newTime) < 4) {
            m_selectedDate = date.relative(-1);
            m_selectedTime = SecondsPastMidnight.fromHMS(24, 0, 0) + newTime;
            updateGraph(m_selectedDate);
        } else {
            return;
        }
    }

    private void updateFastestPath() {
        m_fastestPath = m_graph.fastestPaths(m_selectedStop, m_selectedTime);
        updateIsochrone();
    }

    private void updateIsochrone() {

        IsochroneTileProvider isoTileProvider = new IsochroneTileProvider(
                m_fastestPath, m_colorTable, WALKING_SPEED);
        FilteringTileProvider filteredTileProvider = new TransparentTileProvider(
                0.5, isoTileProvider);
        m_fgTileProvider = new CachedTileProvider(filteredTileProvider);
        
        m_lineProvider = new LineProvider(new ArrayList<>(m_fastestPath.pathTo(m_destination)));

        ArrayList<TileProvider> providers = new ArrayList<>();
        providers.add(m_bgTileProvider);
        providers.add(m_fgTileProvider);
        providers.add(m_lineProvider);
        m_tiledMapComponent.setProviders(providers);
    }

    private JComponent createCenterPanel() {
        final JViewport viewPort = new JViewport();
        viewPort.setView(m_tiledMapComponent);
        PointOSM startingPosOSM = INITIAL_POSITION.toOSM(m_tiledMapComponent
                .zoom());
        viewPort.setViewPosition(new Point(startingPosOSM.roundedX(),
                startingPosOSM.roundedY()));

        final JPanel copyrightPanel = createCopyrightPanel();

        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 300));

        layeredPane.add(viewPort, new Integer(0));
        layeredPane.add(copyrightPanel, new Integer(1));

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final Rectangle newBounds = layeredPane.getBounds();
                viewPort.setBounds(newBounds);
                copyrightPanel.setBounds(newBounds);

                viewPort.revalidate();
                copyrightPanel.revalidate();
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(layeredPane, BorderLayout.CENTER);
        layeredPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && !m_isPressed) {
                    m_positionBPoint = viewPort.getViewPosition();
                    m_positionSouris = e.getPoint();
                    m_isPressed = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1)
                    m_isPressed = false;
            }
        });

        layeredPane.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (m_isPressed)
                    viewPort.setViewPosition(new Point(m_positionBPoint.x
                            - e.getX() + m_positionSouris.x, m_positionBPoint.y
                            - e.getY() + m_positionSouris.y));
            }

            @Override
            public void mouseMoved(MouseEvent arg0) {
                return;
            }

        });

        layeredPane.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent arg0) {
                int rotation = arg0.getWheelRotation();
                m_zoom = m_tiledMapComponent.zoom() - rotation;
                if (m_zoom > 19) {
                    m_zoom = 19;
                }
                if (m_zoom < 10) {
                    m_zoom = 10;
                }

                Point zoomPos = viewPort.getViewPosition();
                Point mousePos = arg0.getPoint();

                PointOSM p = new PointOSM(m_tiledMapComponent.zoom(), zoomPos.x
                        + mousePos.x, zoomPos.y + mousePos.y);
                p = p.atZoom(m_zoom);
                Point newPos = new Point(p.roundedX(), p.roundedY());

                m_tiledMapComponent.setZoom(m_zoom);
                viewPort.setViewPosition(new Point(newPos.x - mousePos.x,
                        newPos.y - mousePos.y));
            }
        });
        return centerPanel;
    }

    private JPanel createCopyrightPanel() {
        Icon tlIcon = new ImageIcon(getClass().getResource(
                "/images/tl-logo.png"));
        String copyrightText = "Données horaires 2013. Source : Transports publics de la région lausannoise / Carte : © contributeurs d'OpenStreetMap";
        JLabel copyrightLabel = new JLabel(copyrightText, tlIcon,
                SwingConstants.CENTER);
        copyrightLabel.setOpaque(true);
        copyrightLabel.setForeground(new Color(1f, 1f, 1f, 0.6f));
        copyrightLabel.setBackground(new Color(0f, 0f, 0f, 0.4f));
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0));

        JPanel copyrightPanel = new JPanel(new BorderLayout());
        copyrightPanel.add(copyrightLabel, BorderLayout.PAGE_END);
        copyrightPanel.setOpaque(false);
        return copyrightPanel;
    }

    /* méthodes du bonus */

    private JPanel createPathPanel(){
        JPanel pathPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Itinéraire : ");
        pathPanel.add(title, BorderLayout.PAGE_START);
        pathPanel.add(createDestinationPanel(), BorderLayout.PAGE_START);
        pathPanel.add(m_pathArea, BorderLayout.CENTER);
        return pathPanel;
    }

    private JPanel createDestinationPanel() {
        JLabel destStop = new JLabel("Destination");

        Vector<Stop> stops = new Vector<>(m_timetable.stops());
        Collections.sort(stops, new Comparator<Stop>() {
            public int compare(Stop a, Stop b) {
                return a.name().compareTo(b.name());
            }
        });
        JComboBox<Stop> stopBox = new JComboBox<>(stops);
        stopBox.setSelectedItem(m_selectedStop);
        stopBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                @SuppressWarnings("unchecked")
                Stop newSelectedStop = (Stop) ((JComboBox<Stop>) arg0
                        .getSource()).getSelectedItem();
                updateDestinationStop(newSelectedStop);
                m_pathText.setText(m_fastestPath.toString());
                changeItineraryText();
            }
        });
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.CENTER);

        JPanel destPanel = new JPanel(layout);

        destPanel.add(destStop);
        destPanel.add(stopBox);
        return destPanel;
    }

    private void updateDestinationStop(Stop s) {
        if (!(m_destination.equals(s))) {
            m_destination = s;
            updateLine();
        }
        
    }
    
    private void changeItineraryText(){
        ArrayList<Stop> path = new ArrayList<>(m_fastestPath.pathTo(m_destination));
        String newTxt = "";
        int time;
        if(m_fastestPath.arrivalTime(path.get(path.size() - 1)) == SecondsPastMidnight.INFINITE){
            m_pathText.setText("Impossible d'effectuer l'itinéraire.");
        }
        else{    
            for (int i = 0; i < path.size(); i ++){
                time = m_fastestPath.arrivalTime(path.get(i));
                if (SecondsPastMidnight.hours(time) >= 24){
                    time -= 24*3600;
                }
                newTxt +=  SecondsPastMidnight.toString(time) + "  " + path.get(i).name() + LINE_JUMP;
            }
            m_pathText.setText(newTxt);
            updateLine();
        }
    }
    
    private void updateLine(){
        m_lineProvider.setPath(new ArrayList<>(m_fastestPath.pathTo(m_destination)));
        updateIsochrone();
    }

    /* *** */

    @SuppressWarnings("deprecation")
    private JPanel createUpperPanel() {

        JLabel startStop = new JLabel("Départ");

        Vector<Stop> stops = new Vector<>(m_timetable.stops());
        Collections.sort(stops, new Comparator<Stop>() {
            public int compare(Stop a, Stop b) {
                return a.name().compareTo(b.name());
            }
        });
        JComboBox<Stop> stopBox = new JComboBox<>(stops);
        stopBox.setSelectedItem(m_selectedStop);
        stopBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                @SuppressWarnings("unchecked")
                Stop newSelectedStop = (Stop) ((JComboBox<Stop>) arg0
                        .getSource()).getSelectedItem();
                updateStop(newSelectedStop);
                changeItineraryText();
            }
        });

        JSeparator separator = new JSeparator();
        JLabel timeStart = new JLabel("Date et heure");

        SpinnerDateModel spinnerDateModel = new SpinnerDateModel();

        java.util.Date startingDate = INITIAL_DATE.toJavaDate();
        startingDate
                .setHours(SecondsPastMidnight.hours(INITIAL_DEPARTURE_TIME));
        startingDate.setMinutes(SecondsPastMidnight
                .minutes(INITIAL_DEPARTURE_TIME));
        startingDate.setSeconds(SecondsPastMidnight
                .seconds(INITIAL_DEPARTURE_TIME));
        spinnerDateModel.setValue(startingDate);

        spinnerDateModel.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                java.util.Date newDate = ((SpinnerDateModel) e.getSource())
                        .getDate();
                try {
                    updateDeparture(new Date(newDate),
                            SecondsPastMidnight.fromJavaDate(newDate));
                    changeItineraryText();
                } catch (IOException e1) {
                    System.out
                            .println("Error : Problème à la lecture de l'horaire !");
                }
            }
        });

        JSpinner timeSpinner = new JSpinner(spinnerDateModel);

        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.CENTER);

        JPanel upperPanel = new JPanel(layout);

        upperPanel.add(startStop);
        upperPanel.add(stopBox);
        upperPanel.add(separator);
        upperPanel.add(timeStart);
        upperPanel.add(timeSpinner);

        return upperPanel;
    }

    private void start() {
        JFrame frame = new JFrame("Isochrone TL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
        frame.getContentPane().add(createUpperPanel(), BorderLayout.PAGE_START);
        frame.getContentPane().add(createPathPanel(), BorderLayout.LINE_START);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Main du projet, se contente seulement de créer une instance de la classe
     * IsochroneTL et d'appeller sa méthode start().
     * 
     * @param args
     *            Argument lors de l'appel du main par la console, n'est pas
     *            utilisé dans ce projet.
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new IsochroneTL().start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
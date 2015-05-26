package ch.epfl.isochrone.tiledmap;

import ch.epfl.isochrone.timetable.Stop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LineProvider implements TileProvider{
    private ArrayList<Stop> m_path;
    private final Font m_font = new Font("Courier", Font.BOLD, 12);
    
    public LineProvider(ArrayList<Stop> path){
        m_path = new ArrayList<>(path);
    }
    
    public void setPath(ArrayList<Stop> newPath){
        m_path = new ArrayList<>(newPath);
    }
    
    @Override
    public Tile tileAt(int zoom, int x, int y) {
        BufferedImage image = new BufferedImage(256, 256,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        int x1, y1, x2, y2;
        
        Stop curr, next;
        for (int i = 0; i < m_path.size(); i++){
            curr = m_path.get(i);
            if (i < m_path.size() -1){
                next = m_path.get(i+1);
            }
            else{
                next = curr;
            }
            x1 = (int)curr.position().toOSM(zoom).x() - x*256;
            y1 = (int)curr.position().toOSM(zoom).y() - y*256;
            x2 = (int)next.position().toOSM(zoom).x() - x*256;
            y2 = (int)next.position().toOSM(zoom).y() - y*256;
            g.setColor(new Color(0, 0, 0));
            g.setStroke(new BasicStroke(4));
            g.draw(new Line2D.Float(x1, y1, x2, y2));
            if (zoom >= 13){
                int width = g.getFontMetrics().stringWidth(m_path.get(i).name()) + 8;
                int height = 10 + 8;
                //x1, y1, width, height, arcWidth, arcHeight
                g.setColor(new Color(127, 127, 127));
                g.fillRoundRect(x1 - 4, y1 - height + 4, width, height, 4, 4);
                g.setFont(m_font);
                g.setColor(Color.red);          
                g.drawString(m_path.get(i).name(), x1, y1);
            }
        }
        return new Tile(image, zoom, x, y);
    }
    
}

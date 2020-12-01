package me.abdullah.graph.main;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Visualization {

    private Map<Integer, Integer> classification = new HashMap<>();

    private Font font;

    private Main main;
    private Iterator<Integer> points;
    private Iterator<Integer> current;
    private int currentPoint;
    private int currentTextPoint;
    public Visualization(Main main){
        this.main = main;
        this.points = main.getConnections().keySet().iterator();
        this.currentPoint = points.next();
        this.currentTextPoint = currentPoint;
        main.selection = currentPoint;
        this.current = main.getConnections().get(currentPoint).iterator();

        for (int i = 0; i < main.getPoints().size(); i++) {
            classification.put(i, 0);
        }

        this.font = new Font("Montserrat", Font.PLAIN, 30);
    }

    private int ticks = 0;
    public int visualize(Graphics2D g){
        g.setColor(Color.white);
        g.setFont(font);
        for (Integer a : classification.keySet()) {
            if(currentPoint == a || currentTextPoint == a) g.setColor(Color.orange);
            g.drawString(Integer.toString(classification.get(a)), (int)main.getPoints().get(a).getCenterX(), (int)(main.getPoints().get(a).getCenterY() - Main.RADIUS * 4));
            g.setColor(Color.white);
        }

        if(ticks < 500) {
            ticks++;
            return -1;
        }
        ticks = 0;

        if(current.hasNext()){
            int b = current.next();
            currentTextPoint = b;
            classification.put(b, 1-classification.get(currentPoint));
        }else{
            if(!points.hasNext()){
                main.vis = false;
            }else {
                currentPoint = points.next();
                currentTextPoint = currentPoint;
                current = main.getConnections().get(currentPoint).iterator();
                main.selection = currentPoint;
                System.out.println(main.selection);
            }
        }

        if(main.vis) return -1;
        else main.selection = -1;

        if(isBipartite()){
            return 1;
        }else return 0;
    }

    private boolean isBipartite(){
        for (Integer a : main.getConnections().keySet()) {
            for (Integer b : main.getConnections().get(a)) {
                if(classification.get(a) == classification.get(b)) return true;
            }
        }
        return false;
    }
}

package me.abdullah.graph.listeners;

import me.abdullah.graph.main.Main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class MouseListener extends MouseAdapter {

    private Main main;
    public MouseListener(Main main){
        this.main = main;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(main.vis) return;

        for (int i = 0; i < main.getPoints().size(); i++) {
            Ellipse2D.Double point = main.getPoints().get(i);
            if(point.contains(e.getX(), e.getY())){
                if(main.selection == i) main.selection = -1;
                else if(main.selection != -1) {
                    main.addConnection(main.selection, i);
                    main.s = "";
                    main.selection = -1;
                }else main.selection = i;
                return;
            }
        }

        MouseMotionListener.movable = new Ellipse2D.Double(e.getX() - Main.RADIUS/2, e.getY() - Main.RADIUS/2, Main.RADIUS, Main.RADIUS);
        main.addPoint(MouseMotionListener.movable);
        main.s = "";
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(main.vis) return;

        MouseMotionListener.movable = null;
    }
}

package me.abdullah.graph.listeners;

import me.abdullah.graph.main.Main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;

public class MouseMotionListener extends MouseMotionAdapter {

    public static Ellipse2D.Double movable = null;

    @Override
    public void mouseDragged(MouseEvent e) {
        if(movable != null){
            movable.x = e.getX() - Main.RADIUS/2;
            movable.y = e.getY() - Main.RADIUS/2;
        }
    }
}

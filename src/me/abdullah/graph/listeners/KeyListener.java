package me.abdullah.graph.listeners;

import me.abdullah.graph.main.Main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyListener extends KeyAdapter {

    private Main main;
    public KeyListener(Main main){
        this.main = main;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()){
            main.undo();
            main.s = "";
        }
        else if(e.getKeyCode() == KeyEvent.VK_SPACE){
            main.s = "";
            main.startVisualization();
        }
    }
}

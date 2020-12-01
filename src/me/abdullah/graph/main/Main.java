package me.abdullah.graph.main;

import me.abdullah.graph.listeners.KeyListener;
import me.abdullah.graph.listeners.MouseListener;
import me.abdullah.graph.listeners.MouseMotionListener;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main extends Canvas implements Runnable {

    public static final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    public static final double UNIT = WIDTH*HEIGHT*1E-6;

    public static final int RADIUS = (int)Math.round(Main.UNIT*8);

    private boolean running = false;

    private Thread thread;

    public Main(){
        this.addMouseListener(new MouseListener(this));
        this.addMouseMotionListener(new MouseMotionListener());
        this.addKeyListener(new KeyListener(this));

        new Window(WIDTH, HEIGHT, "Bipartite Graph Visualizer", this);
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running){
            long now = System.nanoTime();
            if(1.0/((now-lastTime)/1000000000.0) > 500) continue;
            lastTime = now;
            if(running) render();
            frames++;
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }

    private List<Ellipse2D.Double> points = new CopyOnWriteArrayList<>();
    private List<Line2D.Double> lines = new CopyOnWriteArrayList<>();

    private Map<Integer, Set<Integer>> connections = new ConcurrentHashMap<>();

    private LinkedList<Object> undoStack = new LinkedList<>();

    public int selection = -1;

    private final Stroke stroke = new BasicStroke((int)Math.round(Main.UNIT));

    private Visualization visualization;
    public boolean vis = false;
    public String s = "";
    private Font font = new Font("Montserrat", Font.PLAIN, 30);

    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.gray);
        g.setStroke(stroke);
        for (Line2D.Double line : lines) {
            g.draw(line);
        }

        g.setColor(Color.white);
        for (Ellipse2D.Double point : points) {
            if(selection != -1)
                if(point == points.get(selection)) g.setColor(Color.orange);
            g.fill(point);
            g.setColor(Color.white);
        }

        if(vis){
            int id = visualization.visualize(g);
            if(id == 1) s = "This graph is Bipartite/Two Colorable";
            else if(id == 0) s = "This graph is Not Bipartite/Two Colorable";
        }

        g.setFont(font);
        g.drawString(s, Main.WIDTH/2 - g.getFontMetrics().stringWidth(s)/2, (int)Main.UNIT*500);

        g.dispose();
        bs.show();
    }

    public void addPoint(Ellipse2D.Double point){
        points.add(point);
        connections.put(points.size()-1, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        undoStack.push(point);
    }

    public void removePoint(Ellipse2D.Double point){
        connections.remove(points.indexOf(point));
        points.remove(point);
    }

    public void addConnection(int p1, int p2){
        if(connections.get(p1).contains(p2)) return;

        connections.get(p1).add(p2);
        connections.get(p2).add(p1);
        lines.add(new Line2D.Double(points.get(p1).getCenterX(), points.get(p1).getCenterY(), points.get(p2).getCenterX(), points.get(p2).getCenterY()));

        undoStack.push(p1 + " " + p2);
    }

    public void removeConnection(int p1, int p2){
        connections.get(p1).remove(p2);
    }

    public void undo(){
        if(undoStack.isEmpty()) return;

        Object obj = undoStack.pop();
        if(obj instanceof Ellipse2D.Double){
            removePoint((Ellipse2D.Double)obj);
        }else if(obj instanceof String){
            String[] s = ((String)obj).split(" ");
            int p1 = Integer.parseInt(s[0]);
            int p2 = Integer.parseInt(s[1]);
            removeConnection(p1, p2);
            removeConnection(p2, p1);
            lines.remove(lines.size()-1);
        }
    }

    public void startVisualization(){
        visualization = new Visualization(this);
        vis = true;
    }

    public List<Ellipse2D.Double> getPoints(){
        return points;
    }

    public Map<Integer, Set<Integer>> getConnections(){
        return connections;
    }

    public static void main(String[] args){
        new Main();
    }
}

/*
 */
package engine;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JRootPane;

public class Window extends JFrame
{

    private final Canvas canvas;
    private Graphics g;
    private BufferStrategy bs;
    
    public Window()
    {
        this(300, 300);
    }

    public Window(double width, double height)
    {
        this((int) width, (int) height);
    }

    public Window(int width, int height)
    {
        super();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(width, height);
        this.setLocationRelativeTo(null);
        this.canvas = new Canvas();
        
        this.add(this.canvas);
        this.setVisible(true);
    }

    public synchronized Graphics getDrawGraphics()
    {
        this.bs = this.canvas.getBufferStrategy();
        if (this.bs == null)
        {
            this.canvas.createBufferStrategy(3);
            return null;
        }
        this.g = this.bs.getDrawGraphics();

        return this.g;
    }

    public synchronized void finalizeDraw() throws NullPointerException
    {
        this.bs.show();
        this.g.dispose();
    }

    public Canvas getCanvas()
    {
        return this.canvas;
    }
    
    @Override
    public int getWidth()
    {
        return ((JRootPane)this.getComponent(0)).getComponent(0).getWidth();
    }
    
    @Override
    public int getHeight()
    {
        return ((JRootPane)this.getComponent(0)).getComponent(0).getHeight();
    }
    
    public static void main(String[] args)
    {
    }
}

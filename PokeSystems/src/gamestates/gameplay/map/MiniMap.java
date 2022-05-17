package gamestates.gameplay.map;

import engine.Engine;
import math.Vector2;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class MiniMap
{

    private BufferedImage mapImage;
    private Engine engine;

    public MiniMap(BufferedImage tileImg, Engine engine)
    {
        // Creates Map Image
        this.mapImage = tileImg;
        this.engine = engine;
    }

    public void render(Graphics g)
    {
        g.drawImage(this.mapImage, 0, 0, this.engine.getWindow().getWidth(), this.engine.getWindow().getHeight(), null);
    }

    public void update(double delta)
    {

    }

    public void setMapImage(BufferedImage img)
    {
        this.mapImage = img;
    }
}

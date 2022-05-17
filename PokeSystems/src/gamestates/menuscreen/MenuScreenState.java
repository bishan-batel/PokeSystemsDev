/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.menuscreen;

import engine.Config;
import engine.Engine;
import engine.Resource;
import gamestates.GameState;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author schro
 */
public class MenuScreenState extends GameState
{

    private static final BufferedImage[] BACKGROUND_IMAGES;

    static
    {
        BufferedImage[] img;
        try
        {
            img = new BufferedImage[]
            {
                ImageIO.read(new File("assets/textures/icons/menu1.png")),
                ImageIO.read(new File("assets/textures/icons/menu2.png"))
            };
        } catch (IOException ex)
        {
            img = new BufferedImage[]
            {
                null
            };
            System.err.println("Failed to load menu screen");
        }
        BACKGROUND_IMAGES = img;
    }

    public MenuScreenState(Engine engine)
    {
        super(engine);
    }

    @Override
    public void setup()
    {
        System.out.println("MenuScreenState setup");
    }

    @Override
    public void render()
    {
        Graphics g = this.win.getDrawGraphics();
        if (g == null)
        {
            return;
        }

        double index = (System.currentTimeMillis() * 2E-3) % BACKGROUND_IMAGES.length;
        g.drawImage(BACKGROUND_IMAGES[(int) index], 0, 0, this.win.getWidth(), this.win.getHeight(), null);

        if (this.engine.isCurrentKey(Config.KeyBind.KEY_NEXT))
        {
            this.engine.getConsole().execute("gamestate_set 1");
        }
        this.win.finalizeDraw();
    }

    @Override
    public void update(double delta)
    {
    }

    @Override
    public void onClose()
    {
    }
}

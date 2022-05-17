/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import gamestates.GameState;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author schro
 */
public class CreditState extends GameState
{

    BufferedImage creditImg;

    public CreditState(Engine engine)
    {
        super(engine);
    }

    private long startTime;

    @Override
    public synchronized void setup()
    {
        startTime = System.currentTimeMillis();
        try
        {
            creditImg = ImageIO.read(new File("assets/credits.png"));
        } catch (Exception e)
        {
            System.out.println("couldnt find it lol");
        }
    }

    @Override
    public synchronized void render()
    {
        Graphics g = this.engine.getWindow().getDrawGraphics();
        Window win = this.engine.getWindow();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, win.getWidth(), win.getHeight());

        double ypos = 3E-2 * -(System.currentTimeMillis() - startTime);
        double width = this.engine.getWindow().getWidth();
        double height = (win.getWidth() / creditImg.getWidth()) * creditImg.getHeight();
        g.drawImage(creditImg, 0, (int) ypos, (int) width, (int) height, null);

        this.engine.getWindow().finalizeDraw();
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

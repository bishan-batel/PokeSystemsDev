/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.gameplay.map.tile;

import engine.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author schro
 */
public class JuanTile implements Tile, CustomScaleTile
{

    private static final float SCALE = 32f;
    private static BufferedImage TEXTURE;

    static
    {
        new Thread(() ->
        {
            BufferedImage img = null;
            try
            {
                img = ImageIO.read(new File("assets/textures/prop/juan.png"));
            } catch (IOException ex)
            {
                System.err.println("Failed to load juan");
            }
            TEXTURE = img;
        }).start();
    }

    @Override
    public BufferedImage getTexture()
    {
        return TEXTURE;
    }

    @Override
    public float getScaleX()
    {
        return JuanTile.SCALE;
    }

    @Override
    public float getScaleY()
    {
        return JuanTile.SCALE;
    }
}

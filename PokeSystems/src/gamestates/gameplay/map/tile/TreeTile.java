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
public class TreeTile implements Tile, CustomScaleTile, CollidableTile
{
    private static final float SCALE = 2.3f;
    private static BufferedImage TEXTURE;

    static
    {
        new Thread(() ->
        {
            BufferedImage img = null;
            try
            {
                img = ImageIO.read(new File("assets/textures/grass_tileset.png"));
                img = Resource.getImageFromTileSet(img, 64, 64, 3, 0);
            } catch (IOException ex)
            {
                System.err.println("Failed to load tree prop image");
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
        return TreeTile.SCALE;
    }

    @Override
    public float getScaleY()
    {
        return TreeTile.SCALE;
    }
}

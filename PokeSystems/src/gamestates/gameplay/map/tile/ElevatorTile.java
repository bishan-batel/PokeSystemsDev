/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.gameplay.map.tile;

import gamestates.gameplay.map.Map;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author schro
 */
public class ElevatorTile implements Tile, CustomScaleTile, CollidableTile
{

    private static final float SCALE = 4f;
    private static BufferedImage TEXTURE;

    static
    {
        new Thread(() ->
        {
            BufferedImage img = null;
            try
            {
                img = ImageIO.read(new File("assets/textures/prop/building/elevator.png"));
            } catch (IOException ex)
            {
                System.err.println("Failed to load elevator");
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
        return TEXTURE.getWidth()/Map.TILE_SIZE*ElevatorTile.SCALE;
    }

    @Override
    public float getScaleY()
    {
        return TEXTURE.getHeight()/Map.TILE_SIZE*ElevatorTile.SCALE;
    }
}

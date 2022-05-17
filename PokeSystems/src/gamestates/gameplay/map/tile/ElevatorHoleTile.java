/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.gameplay.map.tile;

/**
 *
 * @author schro
 */
import engine.Resource;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ElevatorHoleTile implements Tile
{

    private static final BufferedImage TEXTURE;

    static
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("assets/textures/elevator_hole.png"));
        } catch (IOException ex)
        {
            System.err.println("Failed to load tall elevator hole floor tile");
        }
        TEXTURE = img;
    }

    @Override
    public BufferedImage getTexture()
    {
        return ElevatorHoleTile.TEXTURE;
    }
}

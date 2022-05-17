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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class VoidTile implements Tile, CollidableTile
{

    private static final BufferedImage TEXTURE;

    static
    {
        BufferedImage img;
        try
        {
            img = ImageIO.read(new File("assets/textures/empty .png"));
        } catch (IOException ex)
        {
            img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            img.setRGB(0, 0, new Color(0, 0, 0).getRGB());
            System.err.println("Failed to load void tile");
        }
        TEXTURE = img;
    }

    @Override
    public BufferedImage getTexture()
    {
//        return null;
        return TEXTURE;
    }
}

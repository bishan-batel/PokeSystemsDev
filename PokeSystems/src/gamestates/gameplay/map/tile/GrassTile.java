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

public class GrassTile implements Tile
{

    private static final BufferedImage TEXTURE;
    private static BufferedImage[] textures = null;
    private byte index;

    static
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("assets/textures/grass_tileset.png"));
        } catch (IOException ex)
        {
            System.err.println("Failed to load grass tile");
        }
        TEXTURE = img;
    }

    public GrassTile()
    {
        if (textures == null)
        {
            textures = new BufferedImage[]
            {
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 0, 3),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 0, 4),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 0, 5)
            };
        }

        this.index = (byte) (Math.random() * (GrassTile.textures.length - 0.8));
    }

    @Override
    public BufferedImage getTexture()
    {
        return textures[this.index];
    }
}

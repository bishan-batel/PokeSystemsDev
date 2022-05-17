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

public class LungTile implements Tile, CollidableTile
{

    private static final BufferedImage TEXTURE;
    private static BufferedImage[] textures = null;
    private byte index;

    static
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("assets/textures/lung.png"));
        } catch (IOException ex)
        {
            System.err.println("Failed to load Flesh tile");
        }
        TEXTURE = img;
    }

    public LungTile()
    {
        if (LungTile.textures == null)
        {
            LungTile.textures = new BufferedImage[]
            {
                Resource.getImageFromTileSet(LungTile.TEXTURE, 16, 16, 0, 0),
                Resource.getImageFromTileSet(LungTile.TEXTURE, 16, 16, 0, 1),
                Resource.getImageFromTileSet(LungTile.TEXTURE, 16, 16, 0, 2)
            };
        }

        this.index = (byte) (Math.random() * (LungTile.textures.length - 0.8));
    }

    @Override
    public BufferedImage getTexture()
    {
        return LungTile.textures[this.index];
    }
}

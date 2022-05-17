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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PathTile implements Tile
{

    private static final BufferedImage TEXTURE;
    private static BufferedImage[] textures = null;
    private int index;

    static
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("assets/textures/grass_tileset.png"));
        } catch (IOException ex)
        {
            System.err.println("Failed to load path til");
        }
        TEXTURE = img;
    }

    public PathTile()
    {
        if (textures == null)
        {
            textures = new BufferedImage[]
            {
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 6, 0),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 7, 0),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 8, 0),
            };
        }

        this.index = (int) (Math.random() * PathTile.textures.length );
    }

    @Override
    public BufferedImage getTexture()
    {
        return textures[this.index];
    }
}

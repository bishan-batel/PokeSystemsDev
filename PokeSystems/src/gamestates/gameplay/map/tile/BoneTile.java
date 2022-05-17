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
import gamestates.gameplay.map.Map;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BoneTile implements Tile, CollidableTile, CustomTextureTile
{

    private static final BufferedImage TEXTURE;
    private static BufferedImage[] textures = null;
    private byte index;

    static
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("assets/textures/bone.png"));
        } catch (IOException ex)
        {
            System.err.println("Failed to load bone field tile");
        }
        TEXTURE = img;
    }

    public BoneTile()
    {
        if (textures == null)
        {
            textures = new BufferedImage[]
            {
                // Single
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 0, 0),
                // Straight with edge
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 0, 1),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 1, 1),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 0, 2),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 1, 2),
                // Corner
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 0, 3),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 1, 3),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 0, 4),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 1, 4),
                // Straight
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 0, 5),
                Resource.getImageFromTileSet(TEXTURE, 16, 16, 1, 5),
            };
        }
    }

    @Override
    public BufferedImage getTexture()
    {
        return textures[this.index];
    }

    @Override
    public void updateTexture(Map map, int x, int y)
    {
        boolean left = map.getTileAt(x - 1, y) instanceof BoneTile;
        boolean right = map.getTileAt(x + 1, y) instanceof BoneTile;
        boolean up = map.getTileAt(x, y - 1) instanceof BoneTile;
        boolean down = map.getTileAt(x, y + 1) instanceof BoneTile;

        if (up)
        {
            this.index = (byte) (4 + (left ? 4 : right ? 3 : down ? 6 : 4));
        } else if (down)
        {
            this.index = (byte) (4 + (left ? 2 : right ? 1 : 3));
        } else if (left)
        {
            this.index = (byte) (4 + (right ? 5 : 2)); // Left & right
        } else if (right)
        {
            this.index = 1;
        }
    }
}

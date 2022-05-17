/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.gameplay.map.prop;

import engine.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BoogerProp extends Prop
{
    private static final BufferedImage[] TEXTURES;
    private byte index = 0;

    static
    {
        BufferedImage img;
        BufferedImage[] sprites = null;
        try
        {
            img = ImageIO.read(new File("assets/textures/prop/booger.png"));
            sprites = new BufferedImage[]
            {
                Resource.getImageFromTileSet(img, 16, 16, 0, 0),
                Resource.getImageFromTileSet(img, 16, 16, 0, 1),
                Resource.getImageFromTileSet(img, 16, 16, 0, 2),
                Resource.getImageFromTileSet(img, 16, 16, 0, 3)
            };
        } catch (IOException ex)
        {
            System.err.println("Failed to load booger prop image");
        }
        TEXTURES = sprites;
    }

    @Override
    public void constructor()
    {
        this.index = (byte) (Math.random() * TEXTURES.length);
    }

    // my headphones died wait
    @Override
    public BufferedImage getTexture()
    {
        return TEXTURES[this.index];
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.gameplay.map.prop;

import engine.Resource;
import gamestates.gameplay.dialogue.LecternDialogueData;
import gamestates.gameplay.map.Map;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import math.Vector2;
import org.json.JSONException;

public class NPCProp extends Prop implements InteractableProp, CustomScaleProp, DynamicProp
{

    private static final BufferedImage[][] TEXTURES;
    private byte index = 0;
    private short colorIndex = 0;

    static
    {
        TEXTURES = new BufferedImage[8][4];
        for (int i = 0; i < TEXTURES.length; i++)
        {
            try
            {
                BufferedImage texture = ImageIO.read(new File("assets/textures/prop/npc/" + i + ".png"));
                for (int j = 0; j < TEXTURES[0].length; j++)
                {
                    TEXTURES[i][j] = Resource.getImageFromTileSet(texture, 32, 32, 0, j);
                }
            } catch (IOException ex)
            {
            }
        }
    }

    @Override
    public void constructor()
    {

        this.index = 0;
        try
        {
            this.colorIndex = (short) this.config.getInt("color");
        } catch (JSONException e)
        {
            this.colorIndex = 0;
        }
    }

    @Override
    public BufferedImage getTexture()
    {
        return TEXTURES[this.colorIndex][this.index];
    }

    @Override
    public void update(double delta, Map map)
    {
        Vector2 pos = new Vector2(this.x, this.y);
        pos.sub(this.map.getPlayer().getPos());
        
        
        double angle = (Math.PI + Math.atan2(pos.y, pos.x)) / Math.PI * 180;
        if (angle < 45 || angle > 315)
        {
            this.index = 3;
        } else if (angle > 45 && angle < 135)
        {
            this.index = 2;
        } else if (angle > 135 && angle < 225)
        {
            this.index = 1;
        } else
        {
            this.index = 0;
        }
    }

    @Override
    public void interact()
    {
        LecternProp prop = new LecternProp();
        prop.construct(map, x, y, config);
        prop.interact();
    }

    @Override
    public float getScaleX()
    {
        return 1.5f;
    }

    @Override
    public float getScaleY()
    {
        return 1.5f;
    }

}

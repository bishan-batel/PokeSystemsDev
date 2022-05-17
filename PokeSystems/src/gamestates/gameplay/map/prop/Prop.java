package gamestates.gameplay.map.prop;

import gamestates.gameplay.map.Map;
import java.awt.image.BufferedImage;
import org.json.JSONObject;

public abstract class Prop
{
    protected Map map;
    protected int x, y;
    protected JSONObject config;

    public final void construct(Map map, int x, int y, JSONObject config)
    {
        this.map = map;
        this.x = x;
        this.y = y;
        this.config = config;
        this.constructor();
    }
    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public abstract void constructor();
    public abstract BufferedImage getTexture();
}

package gamestates;

import engine.Engine;
import engine.Window;

public abstract class GameState
{
    protected Window win;
    protected Engine engine;
    public GameState(Engine engine)
    {
        this.engine = engine;
        this.win = this.engine.getWindow();
    }
    
    public abstract void setup();
    public abstract void render();
    public abstract void update(double delta);
    public abstract void onClose();
}

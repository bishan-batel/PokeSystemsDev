package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DefaultKeyListener implements KeyListener
{
    private final Engine engine;
    public DefaultKeyListener(Engine engine)
    {
        this.engine = engine;
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        this.engine.setKeyState(e.getKeyCode(), true);
        this.engine.setCurrentKeyPressed(e.getKeyCode());
        // Console Toggle
        if (e.getKeyCode() == Config.KeyBind.KEY_CONSOLE.getKeyCode())
        {
            this.engine.getConsole().setVisible(!this.engine.getConsole().isVisible());
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        this.engine.setKeyState(e.getKeyCode(), false);
    }
    
    @Override
    @Deprecated
    public void keyTyped(KeyEvent e)
    {
    }
}

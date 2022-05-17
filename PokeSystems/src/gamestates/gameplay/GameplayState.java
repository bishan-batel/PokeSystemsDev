package gamestates.gameplay;

import engine.Config;
import engine.Engine;
import gamestates.GameState;
import gamestates.gameplay.dialogue.Dialogue;
import gamestates.gameplay.dialogue.battle.Pokedex;
import gamestates.gameplay.dialogue.battle.PokemonBattle;
import gamestates.gameplay.map.Map;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

public class GameplayState extends GameState implements KeyListener {
    private Dialogue dialogue = null;
    private Map map;

    public GameplayState(Engine engine) {
        super(engine);
    }

    @Override
    public void setup() {
        try {
            this.dialogue = null;
            this.engine.getWindow().getCanvas().addKeyListener(this);
            this.map = new Map(Config.GAME_MAP_FILE, this); // Sends Map File
        } catch (FileNotFoundException | JSONException ex) {
            System.err.println("Error loading map " + Config.GAME_MAP_FILE + ", resetting to menustate");
            this.engine.setGameState((byte) 0); // Defaults to Menu Screen
        }
        // Defaults to Menu Screen
    }

    @Override
    public synchronized void render() {
        Graphics g = this.engine.getWindow().getDrawGraphics();
        if (g == null)
            return;

        if (this.engine.isKeyPressed(77)) {
            this.map.getMiniMap().render(g);
        } else {
            this.map.render(g);
        }

        if (this.dialogue == null) {
        } else {
            this.dialogue.render(g);
        }
        this.engine.getWindow().finalizeDraw();
    }

    @Override
    public synchronized void update(double delta) {
        if (this.dialogue == null) {
            this.map.update(delta);
        } else {
            if (this.dialogue.dispose) {
                this.dialogue = null;
            }
        }
    }

    @SuppressWarnings("empty-statement")
    public void requestDialogue(Dialogue dObj) {
        new Thread(() -> {
            try {
                wait((long) 1E1);
            } catch (Exception ex) {
            }
            this.dialogue = dObj;
        }).start();
    }

    @Override
    public void onClose() {
        this.engine.getWindow().getCanvas().removeKeyListener(this);
    }

    public Engine getEngine() {
        return this.engine;
    }

    public Map getMap() {
        return this.map;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (this.dialogue != null) {
            this.dialogue.keyPressed(e);
        } else if (e.getKeyCode() == Config.KeyBind.POKEDEX.getKeyCode()) {
            this.requestDialogue(new Pokedex(this.map));
        }
    }

    @Deprecated
    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Deprecated
    @Override
    public void keyTyped(KeyEvent e) {

    }
}

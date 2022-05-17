/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.awt.Font;
import java.awt.FontFormatException; import java.io.File;
import java.io.IOException;

public final class Config {

    // Dialogue Config
    public static final Font DIALOGUE_FONT;

    // Player Config
    public static boolean PLAYER_FLUID_CONTROL;
    public static boolean PLAYER_SPIN;
    public static float PLAYER_INTERACTION_RATE = 0.01f;
    public static float PlAYER_INTERACTION_DISTANCE = 1.5f;
    public static float PLAYER_SPEED = 0.05f;

    @Deprecated
    public static final String SOUND_FOLDER = "assets/sounds/";

    // Pokemon Config
    public static final String POKEMON_FOLDER = "assets/pokemon/";
    // Map Config
    public static final String MAPS_FOLDER = "assets/map/";
    public static final String DEFAULT_MAP_PATH = "default/skin/outside";
    public static boolean MAP_CHAOS;
    public static String GAME_MAP_FILE = Config.DEFAULT_MAP_PATH;

    // Keybinding
    public enum KeyBind {
        KEY_FORWARD(87), // w
        KEY_LEFT(65), // a
        KEY_RIGHT(68), // s
        KEY_BACK(83), // d
        KEY_MAP(77), // m
        KEY_INTERACT(69), // e
        KEY_CONSOLE(121), // f10
        KEY_NEXT(32), // space 
        KEY_NO(78), // n 
        KEY_YES(89), // y 
        POKEDEX(82); // i

        protected int keyCode;

        KeyBind(int n) {
            keyCode = n;
        }

        public int getKeyCode() {
            return this.keyCode;
        }

        public char getKeyChar() {
            return (char) this.keyCode;
        }
    }

    // Loading
    static {
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("assets/font/old_mono.ttf"));
        } catch (FontFormatException | IOException ex) {
            font = new Font("", Font.PLAIN, 1);
            ex.printStackTrace();
        }
        DIALOGUE_FONT = font;
    }

    // Private Constructor
    private Config() {
    }
}

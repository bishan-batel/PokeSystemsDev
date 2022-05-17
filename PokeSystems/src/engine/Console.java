package engine;

import gamestates.gameplay.GameplayState;
import gamestates.gameplay.dialogue.battle.PokemonBattle;
import gamestates.gameplay.map.Map;
import gamestates.gameplay.map.prop.DynamicProp;
import gamestates.gameplay.map.prop.InteractableProp;
import gamestates.gameplay.map.prop.Prop;
import gamestates.gameplay.map.tile.Tile;
import gamestates.gameplay.pokemon.Pokemon;
import gamestates.gameplay.pokemon.PokemonSpecies;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import org.json.JSONException;
import org.json.JSONObject;

public class Console extends JFrame implements ActionListener {
    private static final Font FONT = Config.DIALOGUE_FONT.deriveFont((float) 20);
    private static final Color BACKGROUND = new Color(21, 21, 21);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private final JLabel[] log = new JLabel[5];
    private final JTextField field;
    private final Box box;
    private final Engine engine;

    public Console(Engine engine) {
        super("Console");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        this.engine = engine;

        // Gui
        this.box = Box.createVerticalBox();

        for (int i = 0; i < log.length; i++) {
            log[i] = new JLabel("<html>null");
            log[i].setFont(FONT);
            log[i].setForeground(TEXT_COLOR);
            log[i].setBackground(BACKGROUND);
            this.box.add(log[i]);
        }
        this.field = new JTextField(30);
        this.field.setFont(FONT);
        this.field.addActionListener(this);
        this.field.setForeground(TEXT_COLOR);
        this.field.setBackground(BACKGROUND);

        this.box.add(this.field);

        this.getContentPane().setBackground(BACKGROUND);
        this.add(this.box);

        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
        this.pack();
    }

    private String gamestate_set(String[] cmd) {
        String output;
        try {
            this.engine.setGameState(Byte.parseByte(cmd[1]));
            output = "Set Gamestate to " + cmd[1];
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            output = "[Error] Invalid command syntax or input";
        }
        return output;
    }

    private String toggle_player_spin() {
        Config.PLAYER_SPIN = !Config.PLAYER_SPIN;
        return "Player spin toggled to " + Config.PLAYER_SPIN;
    }

    private String toggle_map_chaos() {
        Config.MAP_CHAOS = !Config.MAP_CHAOS;
        return "Map Chaos toggled to " + Config.MAP_CHAOS;
    }

    private String map(String cmd) {
        String output;
        try {
            // Deletes "map " from rest of command for filepath
            String filePath = cmd.substring(4, cmd.length());
            Config.GAME_MAP_FILE = filePath;
            output = "MapFile changed to assets/" + filePath;
        } catch (Exception e) {
            output = "[Error] Invalid command syntax or file";
        }
        return output;
    }

    private String reload() {
        try {
            var gameplay = (GameplayState) this.engine.gameStates[this.engine.getGameState()];
            gameplay.getMap().reload(Config.GAME_MAP_FILE);
            return "Reload Successfull";
        } catch (FileNotFoundException | JSONException ex) {
            return "Failed to reload";
        }
    }

    private String set_player_speed(String[] cmd) {
        String output;
        try {
            Config.PLAYER_SPEED = (float) Double.parseDouble(cmd[1]);
            output = "Player Speed set to " + cmd[1];
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            output = "[Error] Invalid command syntax or input";
        }
        return output;
    }

    private String set_player_interaction(String[] cmd) {
        String output;
        try {
            Config.PLAYER_INTERACTION_RATE = (float) Double.parseDouble(cmd[1]);
            output = "Player interaction rate set to " + cmd[1];
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            output = "[Error] Invalid command syntax or input";
        }
        return output;
    }

    private String reload_map_texture() {
        ((GameplayState) this.engine.gameStates[this.engine.getGameState()]).getMap().reloadMapTexture();
        return "Reloaded Map Texture successfully";
    }

    private String toggle_fluid_control() {
        Config.PLAYER_FLUID_CONTROL = !Config.PLAYER_FLUID_CONTROL;
        return "Toggled Fluid Control to " + Config.PLAYER_FLUID_CONTROL;
    }

    private String set_tile(String[] cmd) {
        try {
            String tileClassName = "gamestates.gameplay.map.tile." + cmd[1] + "Tile";

            // its probaly fine
            @SuppressWarnings("unchecked")
            Tile tile = (Tile) Class.forName(tileClassName).newInstance();

            int x = Integer.parseInt(cmd[2]);
            int y = Integer.parseInt(cmd[3]);
            ((GameplayState) this.engine.gameStates[1]).getMap().setTileAt(x, y, tile);
            this.reload_map_texture();
            return "Set tile " + tileClassName + " at " + x + ", " + y;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NumberFormatException e) {
            return "Error setting prop";
        }
    }

    private String add_prop(String rawCmd) {
        try {
            JSONObject propJSON = new JSONObject(rawCmd.replaceFirst("add_prop", ""));
            Map map = ((GameplayState) this.engine.gameStates[this.engine.getGameState()]).getMap();
            int x = propJSON.getInt("x");
            int y = propJSON.getInt("y");
            String propClassName = "gamestates.gameplay.map.prop." + propJSON.getString("type") + "Prop";
            Prop prop = (Prop) Class.forName(propClassName).newInstance();
            prop.construct(map, x, y, propJSON.getJSONObject("config"));

            if (prop instanceof DynamicProp) {
                map.getDynamicPropList().add((DynamicProp) prop);
            }
            if (prop instanceof InteractableProp) {
                map.getInteractablePropList().add((InteractableProp) prop);
            }
            map.getProps().add(prop);
            return "Prop " + propClassName + " added";
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | JSONException e) {
            return "Unable to add prop";
        }
    }

    private String delete_prop(String[] cmd) {
        Map map = ((GameplayState) this.engine.gameStates[this.engine.getGameState()]).getMap();
        try {
            ArrayList<Prop> props = map.getProps();
            for (int i = 0; i < props.size(); i++) {
                Prop prop = props.get(i);
                if (prop.getX() == Integer.parseInt(cmd[1]) && prop.getY() == Integer.parseInt(cmd[2])) {
                    props.remove(prop);
                    map.getDynamicPropList().remove(prop);
                    map.getInteractablePropList().remove(prop);
                    return "Deleted Prop Successfully";
                }
            }
        } catch (NumberFormatException e) {
            return "Invalid Syntax";
        }
        return "Unable to find prop";
    }

    private String load(String rawCmd) {
        String output = this.map(rawCmd);
        output += this.reload();
        return output;
    }

    private String request_battle(String rawcmd) {
        try {
            String type = rawcmd.substring(15);
            GameplayState state = ((GameplayState) this.engine.gameStates[this.engine.getGameState()]);
            state.requestDialogue(
                    new PokemonBattle(state.getMap(), new PokemonSpecies[] { PokemonSpecies.valueOf(type) }));
            return "Requested Battle";
        } catch (Exception e) {
            return "Failed to request battle";
        }
    }

    private String remove_pokemon(String[] cmd) {
        try {
            String type = cmd[1];
            Map map = ((GameplayState) this.engine.gameStates[this.engine.getGameState()]).getMap();
            ArrayList<Pokemon> pokemon = map.getPlayer().getPokemon();
            for (int i = 0; i < pokemon.size(); i++) {
                if (pokemon.get(i).getSpecies() == PokemonSpecies.valueOf(type)) {
                    pokemon.remove(pokemon.get(i));
                    return "Removed Succesfully";
                }
            }
            return "Failed to find Pokemon";
        } catch (Exception e) {
            return "Error occured while removing";
        }
    }

    private String add_pokemon(String[] cmd) {
        try {
            String type = cmd[1];
            Map map = ((GameplayState) this.engine.gameStates[this.engine.getGameState()]).getMap();
            map.getPlayer().getPokemon().add(new Pokemon(PokemonSpecies.valueOf(type)));
            return "Added Pokemon";
        } catch (Exception e) {
            return "Error occured while adding";
        }
    }

    private String unlock_pokedex() {
        ArrayList<Pokemon> plist = ((GameplayState) this.engine.gameStates[this.engine.getGameState()]).getMap()
                .getPlayer().getPokemon();
        plist.removeAll(plist);

        for (PokemonSpecies ps : PokemonSpecies.values()) {
            plist.add(new Pokemon(ps));
        }
        return "Unlocked Pokedex";
    }

    private String interpretCommand(String rawCmd) {
        try {
            String cmd[] = rawCmd.split(" "); // Seperates command by spaces
            switch (cmd[0]) // Checks for every possible command and executes if found
            {
                case "gamestate_set":
                    return this.gamestate_set(cmd);
                case "toggle_player_spin":
                    return this.toggle_player_spin();
                case "map":
                    return this.map(rawCmd);
                case "reload":
                    return this.reload();
                case "set_player_speed":
                    return this.set_player_speed(cmd);
                case "set_player_interaction":
                    return this.set_player_interaction(cmd);
                case "toggle_map_chaos":
                    return this.toggle_map_chaos();
                case "reload_map_texture":
                    return this.reload_map_texture();
                case "toggle_fluid_control":
                    return this.toggle_fluid_control();
                case "set_tile":
                    return this.set_tile(cmd);
                case "add_prop":
                    return this.add_prop(rawCmd);
                case "delete_prop":
                    return this.delete_prop(cmd);
                case "lde":
                    return this.load(rawCmd);
                case "request_battle":
                    return this.request_battle(rawCmd);
                case "remove_pokemon":
                    return this.remove_pokemon(cmd);
                case "add_pokemon":
                    return this.add_pokemon(cmd);
                case "unlock_pokedex":
                    return this.unlock_pokedex();
                default:
                    return "Command Not Found \"" + rawCmd + '"';
            }
        } catch (Exception e) {
            return "Error issuing command";
        }
    }

    public void execute(String command) {
        for (int i = 0; i < this.log.length - 1; i++) {
            log[i].setText(log[i + 1].getText());
        }
        log[this.log.length - 1].setText(
                "<html><p style=\\\"width:" + this.getWidth() / 2 + "px\\\">" + this.interpretCommand(command));
        this.field.setText("");
        this.validate();
        this.repaint();

        this.pack();
        this.setSize(350, this.getHeight());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.execute(this.field.getText());
    }

}

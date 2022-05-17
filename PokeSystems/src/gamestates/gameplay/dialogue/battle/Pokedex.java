/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.gameplay.dialogue.battle;

import engine.Config;
import engine.Resource;
import engine.Window;
import gamestates.gameplay.dialogue.Dialogue;
import gamestates.gameplay.dialogue.LecternDialogue;
import gamestates.gameplay.map.Map;
import gamestates.gameplay.map.player.Player;
import gamestates.gameplay.pokemon.Pokemon;
import gamestates.gameplay.pokemon.PokemonAttack;
import gamestates.gameplay.pokemon.PokemonSpecies;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Pokedex extends Dialogue
{

    public static final BufferedImage POKEDEX_TEXTURE, DEAD_TEXTURE;
    private final Map map;
    private final Player player;
    private ArrayList<Pokemon> pokemon;
    private final long startTime;
    private BufferedImage texture;
    private BufferedImage overviewTexture, infoTexture;

    static
    {
        BufferedImage img = null;
        BufferedImage img2 = null;
        try
        {
            img = ImageIO.read(new File("assets/textures/icons/pokedex.png"));
            img2 = ImageIO.read(new File("assets/textures/icons/dead.png"));
        } catch (IOException e)
        {
            System.err.println("Failed to load pokedex texture");
        }
        POKEDEX_TEXTURE = img;
        DEAD_TEXTURE = img2;
    }

    public Pokedex(Map map)
    {
        this(map, map.getPlayer());
    }

    public Pokedex(Map map, Player player)
    {
        this.map = map;
        this.player = player;
        this.pokemon = player.getPokemon();
        this.startTime = System.currentTimeMillis();
        this.texture = new BufferedImage(
                this.map.getEngine().getWindow().getWidth(),
                this.map.getEngine().getWindow().getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        this.overviewTexture = new BufferedImage(
                LecternDialogue.DIALOGUE_BOX_TEXTURE.getWidth(),
                LecternDialogue.DIALOGUE_BOX_TEXTURE.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        this.infoTexture = new BufferedImage(
                LecternDialogue.DIALOGUE_BOX_TEXTURE.getWidth(),
                LecternDialogue.DIALOGUE_BOX_TEXTURE.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
    }

    private short pokemonIndex = 0;
    private short attackIndex = 0;

    private synchronized void renderInfo(Graphics gr)
    {
        Pokemon poke = this.pokemon.get(pokemonIndex);
        PokemonSpecies species = poke.getSpecies();

        Graphics g = this.infoTexture.getGraphics();

        g.drawImage(LecternDialogue.DIALOGUE_BOX_TEXTURE, 0, 0, null);

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 50));
        g.setColor(Color.BLACK);
        Resource.drawStringMultiLine(g,
                species.getDescription(),
                1300, 115, 160
        );
    }

    private synchronized void renderOverview(Graphics gr)
    {
        Pokemon poke = this.pokemon.get(pokemonIndex);
        PokemonSpecies species = poke.getSpecies();

        Graphics g = this.overviewTexture.getGraphics();

        g.drawImage(LecternDialogue.DIALOGUE_BOX_TEXTURE, 0, 0, null);
        g.drawImage(species.getIcon(), 140, 160, 250, 250, null);

        if (poke.getHealth() <= 0)
        {
            g.drawImage(DEAD_TEXTURE, 140, 160, 250, 250, null);
        }

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 50));

        float p = poke.getHealth() / species.getMaxHealth();
        g.setColor(new Color((int) (255 * (1 - p)), (int) (255 * p), 0));
        Resource.drawStringMultiLine(g, poke.getHealth() + "/" + species.getMaxHealth() + " Health", 1000, 120, 440);

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 80));
        g.setColor(Color.BLACK);
        Resource.drawStringMultiLine(g, species.getName(), 1000, 450, 180);

        g.setColor(Color.BLUE);
        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 50));
        Resource.drawStringMultiLine(g,
                "(" + (this.pokemonIndex + 1) + '/' + this.pokemon.size() + ") [A & D]",
                1000, 450, 230
        );

        g.setColor(Color.BLACK);
        this.attackIndex = (short) Resource.constrain(this.attackIndex, 0, species.getAttacks().length - 1);
        PokemonAttack attack = species.getAttacks()[this.attackIndex];

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 50));
        Resource.drawStringMultiLine(g, attack.getName(), 1000, 450, 300);

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 50));

        if (attack.getDamageForEnemy() != 0)
        {
            g.setColor(Color.RED);
            Resource.drawStringMultiLine(g, attack.getDamageForEnemy() + " DMG", 1000, 450, 350);
        } else
        {
            g.setColor(new Color(255, 51, 204));
            Resource.drawStringMultiLine(g, attack.getHealForSelf() + " HEAL", 1000, 450, 350);
        }

        g.setColor(Color.BLUE);
        Resource.drawStringMultiLine(g,
                "(" + (this.attackIndex + 1) + '/' + species.getAttacks().length + ") [W & S]",
                1000, 450, 400
        );
    }

    @Override
    public void render(Graphics gr)
    {
        Window win = this.map.getEngine().getWindow();

        Graphics g = this.texture.getGraphics();
        // Background Cull
        g.setColor(new Color(0, 0, 0, 2));
        g.fillRect(0, 0, win.getWidth(), win.getHeight());

        // Self Box
        float xOff = win.getWidth() * 0.00f;
        float yOff = win.getHeight() * 0.6f;
        float xSize = win.getWidth() * 0.6f;
        float ySize = win.getHeight() * 0.4f;
        this.renderOverview(g);
        this.renderInfo(g);
        g.drawImage(this.overviewTexture, (int) xOff, (int) (yOff - ySize * 0.66f), (int) xSize, (int) ySize, null);
        g.drawImage(this.infoTexture, (int) xOff, (int) yOff, (int) xSize, (int) ySize, null);

        xOff = win.getWidth() * 0.1f;
        yOff = win.getHeight() * 0f;
        xSize = win.getWidth() * 0.9f;
        ySize = win.getHeight() * 0.3f;

        g.drawImage(POKEDEX_TEXTURE, (int) xOff, (int) yOff, (int) xSize, (int) ySize, null);
        gr.drawImage(this.texture, 0, 0, win.getWidth(), win.getHeight(), null);
    }

    @Override
    public synchronized void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == Config.KeyBind.KEY_LEFT.getKeyCode())
        {
            this.pokemonIndex = (short) Resource.constrain(this.pokemonIndex - 1, 0, this.pokemon.size() - 1);
        } else if (e.getKeyCode() == Config.KeyBind.KEY_RIGHT.getKeyCode())
        {
            this.pokemonIndex = (short) Resource.constrain(this.pokemonIndex + 1, 0, this.pokemon.size() - 1);
        } else if (e.getKeyCode() == Config.KeyBind.KEY_FORWARD.getKeyCode())
        {
            this.attackIndex--;
        } else if (e.getKeyCode() == Config.KeyBind.KEY_BACK.getKeyCode())
        {
            this.attackIndex++;
        } else if (e.getKeyCode() == Config.KeyBind.POKEDEX.getKeyCode())
        {
            this.dispose = true;
        }
    }
}

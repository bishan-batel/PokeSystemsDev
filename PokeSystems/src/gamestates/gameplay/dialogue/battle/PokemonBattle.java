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
import static gamestates.gameplay.dialogue.battle.Pokedex.DEAD_TEXTURE;
import gamestates.gameplay.map.Map;
import gamestates.gameplay.map.player.Player;
import gamestates.gameplay.pokemon.Pokemon;
import gamestates.gameplay.pokemon.PokemonAttack;
import gamestates.gameplay.pokemon.PokemonSpecies;
import gamestates.gameplay.pokemon.PokemonType;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PokemonBattle extends Dialogue
{

    private final Map map;
    private final Player player;
    private ArrayList<Pokemon> pokemon;
    private Pokemon enemy;
    private BufferedImage texture, selfTexture, otherTexture, dialogueTexture;

    public PokemonBattle(Map map, PokemonSpecies[] species)
    {
        try
        {
            this.enemy = new Pokemon(species[(int) (Math.random() * species.length)]);
        } catch (Exception e)
        {
            this.enemy = new Pokemon(PokemonSpecies.values()[(int) (Math.random() * PokemonSpecies.values().length)]);
        }

        this.map = map;
        this.player = map.getPlayer();
        this.pokemon = player.getPokemon();
        this.texture = new BufferedImage(
                this.map.getEngine().getWindow().getWidth(),
                this.map.getEngine().getWindow().getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        this.selfTexture = new BufferedImage(
                LecternDialogue.DIALOGUE_BOX_TEXTURE.getWidth(),
                LecternDialogue.DIALOGUE_BOX_TEXTURE.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        this.otherTexture = new BufferedImage(
                LecternDialogue.DIALOGUE_BOX_TEXTURE.getWidth(),
                LecternDialogue.DIALOGUE_BOX_TEXTURE.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        this.renderSelfTexture();
        this.renderOtherTexture();
    }

    private synchronized void renderOtherTexture()
    {
        PokemonSpecies species = this.enemy.getSpecies();
        Graphics g = this.otherTexture.getGraphics();
        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 80));
        g.setColor(Color.BLACK);

        g.drawImage(LecternDialogue.DIALOGUE_BOX_TEXTURE, 0, 0, null);
        g.drawImage(species.getIcon(), 140, 160, 250, 250, null);

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 80));
        g.setColor(Color.BLACK);
        Resource.drawStringMultiLine(g, species.getName(), 1000, 450, 180);

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 50));
        float p = this.enemy.getHealth() / species.getMaxHealth();
        g.setColor(new Color((int) (255 * (1 - p)), (int) (255 * p), 0));
        Resource.drawStringMultiLine(g, this.enemy.getHealth() + "/" + species.getMaxHealth() + " Health", 1000, 120, 440);
    }

    private short pokemonIndex = 0;
    private short attackIndex = 0;

    private synchronized void renderSelfTexture()
    {
        Pokemon poke = this.pokemon.get(pokemonIndex);
        PokemonSpecies species = poke.getSpecies();

        Graphics g = this.selfTexture.getGraphics();

        g.drawImage(LecternDialogue.DIALOGUE_BOX_TEXTURE, 0, 0, null);
        g.drawImage(species.getIcon(), 140, 160, 250, 250, null);

        if (poke.getHealth() <= 0)
        {
            g.drawImage(DEAD_TEXTURE, 140, 160, 250, 250, null);
        }

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 80));
        g.setColor(Color.BLACK);
        Resource.drawStringMultiLine(g, species.getName(), 1000, 450, 180);

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 50));
        float p = poke.getHealth() / species.getMaxHealth();
        g.setColor(new Color((int) (255 * (1 - p)), (int) (255 * p), 0));
        Resource.drawStringMultiLine(g, poke.getHealth() + "/" + species.getMaxHealth() + " Health", 1000, 120, 440);

        g.setColor(Color.BLACK);
        Resource.drawStringMultiLine(g,
                "(" + (this.pokemonIndex + 1) + '/' + this.pokemon.size() + ") [A & D]",
                1000, 450, 230
        );

        this.attackIndex = (short) Resource.constrain(this.attackIndex, 0, species.getAttacks().length - 1);
        PokemonAttack attack = species.getAttacks()[this.attackIndex];

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 50));
        Resource.drawStringMultiLine(g, attack.getName(), 1000, 450, 300);

        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) 50));
        Resource.drawStringMultiLine(g,
                "(" + (this.attackIndex + 1) + '/' + species.getAttacks().length + ") [W & S]",
                1000, 450, 400
        );

        if (attack.getDamageForEnemy() != 0)
        {
            g.setColor(Color.RED);
            Resource.drawStringMultiLine(g, attack.getDamageForEnemy() + " DMG", 1000, 450, 350);
        } else
        {
            g.setColor(new Color(255, 51, 204));
            Resource.drawStringMultiLine(g, attack.getHealForSelf() + " HEAL", 1000, 450, 350);
        }
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

        g.drawImage(this.selfTexture, (int) xOff, (int) yOff, (int) xSize, (int) ySize, null);

        xOff = win.getWidth() * 0.5f;
        yOff = win.getHeight() * 0.1f;
        xSize = win.getWidth() * 0.6f;
        ySize = win.getHeight() * 0.4f;

        g.drawImage(this.otherTexture, (int) xOff, (int) yOff, (int) xSize, (int) ySize, null);

        gr.drawImage(this.texture, 0, 0, null);

        int alive = 0;
        for (int i = 0; i < pokemon.size(); i++)
        {
            if (pokemon.get(i).getHealth() > 0 && pokemon.get(i).getSpecies().getPokemonType() != PokemonType.ITEM)
            {
                alive++;
            }
        }
        if ((this.enemy.getHealth() <= 0 || alive == 0) && !this.dispose)
        {
            this.pokemon.add(new Pokemon(enemy.getSpecies()));
            this.dispose = true;
        }
    }

    private boolean turn = false;

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
        } else if (e.getKeyCode() == Config.KeyBind.KEY_NEXT.getKeyCode() && turn == false)
        {
            Pokemon p = this.pokemon.get(this.pokemonIndex);
            if (p.getHealth() <= 0 || p.getSpecies().getPokemonType() == PokemonType.ITEM)
            {
                return;
            }
            p.attackPokemon(p.getSpecies().getAttacks()[this.attackIndex], this.enemy);
            this.renderOtherTexture();
            if (enemy.getHealth() <= 0)
            {
                return;
            }
            int index = (int) (Math.min(Math.random(), 0.999f) * this.enemy.getSpecies().getAttacks().length);
            this.enemy.attackPokemon(enemy.getSpecies().getAttacks()[index], p);
        }
        this.renderSelfTexture();
    }
}

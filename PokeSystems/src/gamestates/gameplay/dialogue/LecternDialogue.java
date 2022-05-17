package gamestates.gameplay.dialogue;

import engine.Config;
import engine.Resource;
import engine.Window;
import gamestates.gameplay.map.Map;
import gamestates.gameplay.pokemon.PokemonSpecies;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LecternDialogue extends Dialogue
{

    private static final float FONT_SIZE = 64;
    public static final BufferedImage DIALOGUE_BOX_TEXTURE;
    public static final double SPEED = 4E1;
    private Map map;
    private BufferedImage texture;
    private LecternDialogueData[] dialogueList;
    private short dialogueIndex = 0;
    private long startTime;

    static
    {
        BufferedImage img;
        try
        {
            img = ImageIO.read(new File("assets/textures/icons/dialogue_box.png"));
        } catch (IOException ex)
        {
            System.err.println("Failed to load dialogue box texture");
            img = null;
        }
        DIALOGUE_BOX_TEXTURE = img;
    }

    public LecternDialogue(LecternDialogueData[] texts, Map map)
    {

        this.map = map;
        this.startTime = System.currentTimeMillis();
        this.loadImage();
        // Rendering
        this.dialogueList = texts;
    }

    private synchronized void loadImage()
    {
        this.texture = new BufferedImage(
                DIALOGUE_BOX_TEXTURE.getWidth(),
                DIALOGUE_BOX_TEXTURE.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = this.texture.getGraphics();
        g.drawImage(DIALOGUE_BOX_TEXTURE, 0, 0,
                this.texture.getWidth(),
                this.texture.getHeight(), null);
        g.dispose();
    }

    private synchronized void drawString()
    {
        if (this.dialogueIndex >= this.dialogueList.length)
        {
            return;
        }
        String str = this.dialogueList[this.dialogueIndex].getText();
        double animationIndex = (System.currentTimeMillis() - this.startTime) / LecternDialogue.SPEED;
        str = str.substring(0, (int) Math.min(animationIndex, str.length()));
        Graphics g = this.texture.getGraphics();
        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) FONT_SIZE));
        g.setColor(Color.BLACK);
        Resource.drawStringMultiLine(g, str, (int) (this.texture.getWidth() - 360), 120, 120 + (int) FONT_SIZE);
        g.dispose();
    }

    private synchronized void drawBoolean()
    {
        Graphics g = this.texture.getGraphics();
        float x = this.texture.getWidth() * 0.8f;
        float y = this.texture.getHeight() * 0.7f;
        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) FONT_SIZE));
        g.setColor(Color.BLACK);
        g.drawString(
                Config.KeyBind.KEY_YES.getKeyChar() + "/" + Config.KeyBind.KEY_NO.getKeyChar(),
                (int) x, (int) y);
        g.dispose();
    }

    private synchronized void drawQuery()
    {
        Graphics g = this.texture.getGraphics();
        float x = this.texture.getWidth() * 0.8f;
        float y = this.texture.getHeight() * 0.7f;
        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) FONT_SIZE));
        g.setColor(Color.BLACK);
        g.drawString(
                Config.KeyBind.KEY_YES.getKeyChar() + "/" + Config.KeyBind.KEY_NO.getKeyChar(),
                (int) x, (int) y);
        g.dispose();
    }

    private synchronized void drawNormal()
    {
        Graphics g = this.texture.getGraphics();
        float x = this.texture.getWidth() * 0.75f;
        float y = this.texture.getHeight() * 0.7f;
        g.setFont(Config.DIALOGUE_FONT.deriveFont((float) FONT_SIZE));
        g.setColor(Color.BLACK);
        g.drawString("[SPACE]", (int) x, (int) y);
        g.dispose();
    }

    private synchronized void drawIcon()
    {
        switch (this.dialogueList[this.dialogueIndex].getType())
        {
            case BOOLEAN:
                this.drawBoolean();
                break;
            case QUERY:
                this.drawQuery();
                break;
            case NORMAL:
            default:
                this.drawNormal();
        }
    }

    private synchronized void loadTexture()
    {
        this.loadImage();
        this.drawString();
        this.drawIcon();
    }

    @Override
    public synchronized void render(Graphics g)
    {
        if (this.dispose)
        {
            return;
        }
        Window win = this.map.getEngine().getWindow();
        float xOff = win.getWidth() * 0.1f;
        float yOff = win.getHeight() * 0.5f;
        float xSize = win.getWidth() * 0.8f;
        float ySize = win.getHeight() * 0.5f;
        this.loadTexture();
        g.drawImage(texture, (int) xOff, (int) yOff, (int) xSize, (int) ySize, null);
    }

    public synchronized void step()
    {
        this.dispose = this.dialogueIndex >= this.dialogueList.length;
        this.dialogueList[this.dialogueIndex].execute();
        this.startTime = System.currentTimeMillis();
        this.dialogueIndex++;
        this.dispose = this.dialogueIndex >= this.dialogueList.length;
    }

    public synchronized void queryStep()
    {
        this.dispose = this.dialogueIndex >= this.dialogueList.length;

        LecternDialogueData ldd = this.dialogueList[this.dialogueIndex];

        final String[] invalidAndQuery = ldd.getRawText().split(DialogueType.DATA_SPLIT)[1].split(";", 2);
        final String[] args = invalidAndQuery[0].split(",");
        final String invalidMsg = invalidAndQuery[1];

        int completed = 0;

        for (String arg : args)
        {
            if (arg.startsWith(DialogueType.QUERY_POKEMON_REQUIRED))
            {
                PokemonSpecies species = PokemonSpecies.valueOf(arg.replace(DialogueType.QUERY_POKEMON_REQUIRED, ""));
                boolean found = false;
                for (int i = 0; i < this.map.getPlayer().getPokemon().size(); i++)
                {
                    if (this.map.getPlayer().getPokemon().get(i).getSpecies() == species)
                    {
                        found = true;
                    }
                }
                if (found)
                {
                    completed++;
                }
            } else
            {
                completed++;
            }
        }

        if (completed != args.length)
        {
            this.map.getGameplayState().requestDialogue(new LecternDialogue(new LecternDialogueData[]
            {
                new LecternDialogueData(this.map, invalidMsg, new String[0])
            }, this.map));
            this.dispose = true;
            return;
        }
        ldd.execute();
        this.startTime = System.currentTimeMillis();
        this.dialogueIndex++;

        this.dispose = this.dialogueIndex >= this.dialogueList.length;
    }

    public BufferedImage getTexture()
    {
        return this.texture;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int keyCode = e.getKeyCode();

        if (Math.min((System.currentTimeMillis() - this.startTime) / 0.5E2, this.dialogueList[this.dialogueIndex].getText().length()) != this.dialogueList[this.dialogueIndex].getText().length())
        {
            this.startTime = 0;
            return;
        }

        // Checks with each dialogue type
        switch (this.dialogueList[this.dialogueIndex].getType())
        {
            case BOOLEAN:
                if (keyCode == Config.KeyBind.KEY_NO.getKeyCode())
                {
                    this.dispose = true;
                } else if (keyCode == Config.KeyBind.KEY_YES.getKeyCode())
                {
                    this.step();
                }
                break;
            case QUERY:
                if (keyCode == Config.KeyBind.KEY_NO.getKeyCode())
                {
                    this.dispose = true;
                } else if (keyCode == Config.KeyBind.KEY_YES.getKeyCode())
                {
                    this.queryStep();
                }
                break;
            case NORMAL:
            default:
                if (keyCode == Config.KeyBind.KEY_NEXT.getKeyCode())
                {
                    this.step();
                }
        }
    }
}

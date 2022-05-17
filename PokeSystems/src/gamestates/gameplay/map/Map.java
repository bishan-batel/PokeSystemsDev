package gamestates.gameplay.map;

import engine.Config;
import engine.Engine;
import engine.Resource;
import math.Vector2;
import engine.Window;
import gamestates.gameplay.GameplayState;
import gamestates.gameplay.map.player.Player;
import gamestates.gameplay.map.prop.CustomScaleProp;
import gamestates.gameplay.map.prop.DynamicProp;
import gamestates.gameplay.map.prop.InteractableProp;
import gamestates.gameplay.map.prop.Prop;
import gamestates.gameplay.map.tile.CustomScaleTile;
import gamestates.gameplay.map.tile.CustomTextureTile;
import gamestates.gameplay.map.tile.Tile;
import gamestates.gameplay.map.tile.VoidTile;
import gamestates.gameplay.pokemon.PokemonSpecies;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Map
{

    public static final BufferedImage NOTIFY_TEXTURE;
    public static final short TILE_SIZE = 64;

    // Access
    private Engine engine;
    private MiniMap miniMap;
    private GameplayState gameplayState;

    // Map Data
    private BufferedImage baseImage, baseTexture;
    private JSONObject baseJSONData;

    // Tile and Prop
    private PokemonSpecies[] availableSpecies;
    private Tile[][] tiles;
    private ArrayList<Prop> propList;
    private ArrayList<DynamicProp> dynamicPropList;
    private ArrayList<InteractableProp> interactablePropList;
    private Prop interactableHighlight = null;
    private Player player;

    static
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("assets/textures/icons/notify.png"));
        } catch (IOException e)
        {
            System.err.println("Failed to load notify icon");
        }
        NOTIFY_TEXTURE = img;
    }

    public Map(String pathRaw, GameplayState gameplayState) throws FileNotFoundException, JSONException
    {
        this.reload(pathRaw, gameplayState);
    }

    public void reload(String pathRaw) throws FileNotFoundException, JSONException
    {
        Player p = this.player;
        this.reload(pathRaw, this.gameplayState);
        Vector2 pos = this.player.getPos();
        p.setPos(pos);
        this.player = p;
    }

    private void reload(String pathRaw, GameplayState gameplayState) throws FileNotFoundException, JSONException
    {
        final String path = Config.MAPS_FOLDER + pathRaw;
        System.err.println("\n\n<-- Attempting Load of " + path + " Map -->\n");
        this.engine = gameplayState.getEngine();
        this.gameplayState = gameplayState;
        // Initilizes base and prop image in case of error
        this.baseImage = null;

        // String path for base and prop image
        String baseImagePath = path + "/tile.png";

        // Loads each file individually
        try
        {
            this.baseImage = ImageIO.read(new File(baseImagePath));
            System.out.println("Read " + baseImagePath + " succesfully"); 
        } catch (IOException ioe)
        {
            System.err.println("Failed to load base texture image for " + path);
        }

        if (this.baseImage == null)
        {
            throw new FileNotFoundException();
        }
        // gets JSON data
        this.baseJSONData = Resource.readJSONFile(Config.MAPS_FOLDER + "map_interpret_data.json");

        // Loads tiles in map from base image
        System.out.println("Begin load of tiles [" + path + "]");
        this.loadTiles();
        System.out.println("Begin load of props [" + path + "]");
        this.loadProps(path);

        System.out.println("Retrieving player data & construction [" + path + "]");
        try
        {
            JSONObject mapData = Resource.readJSONFile(path + "/data.json");
            this.player = new Player(mapData.getInt("player-x"), mapData.getInt("player-y"), this);

            try
            {
                JSONArray availableJSON = mapData.getJSONArray("available");
                this.availableSpecies = new PokemonSpecies[availableJSON.length()];
                for (int i = 0; i < this.availableSpecies.length; i++)
                {
                    this.availableSpecies[i] = PokemonSpecies.valueOf(availableJSON.getString(i));
                }
            } catch (JSONException e)
            {
                System.err.println("Failed to load species");
                this.availableSpecies = null;
            }
        } catch (JSONException e)
        {
            System.err.println("Failed to get player-x or player-y");
            this.player = new Player(5, 5, this);
        }

        // Constructs tilemap
        System.out.println("Creating base tile texture [" + path + "]");
        this.baseTexture = this.createMapImage(this.engine.getWindow());

        System.out.println("Setting up Minimap [" + path + "]");
        this.miniMap = new MiniMap(this.baseTexture, this.engine);
    }

    // Loading Tiles
    private void loadTiles()
    {
        this.tiles = new Tile[this.baseImage.getWidth()][this.baseImage.getHeight()];
        for (int x = 0; x < this.tiles.length; x++)
        {
            for (int y = 0; y < this.tiles[x].length; y++)
            {
                this.tiles[x][y] = this.interpretTile(this.baseImage.getRGB(x, y));
            }
        }

        for (int x = 0; x < this.tiles.length; x++)
        {
            for (int y = 0; y < this.tiles[x].length; y++)
            {
                Tile tile = this.tiles[x][y];
                if (tile instanceof CustomTextureTile)
                {
                    ((CustomTextureTile) tile).updateTexture(this, x, y);
                }
            }
        }
    }

    private Tile interpretTile(int rgb)
    {
        // Im  want to die
        String tileClassName = "";
        try
        {
            JSONArray blockKey = this.baseJSONData.getJSONArray("block key");
            for (int i = 0; i < blockKey.length(); i++)
            {
                JSONArray hexData = blockKey.getJSONArray(i);
                String hex = hexData.getString(0);
                int decode = Color.decode(hex).getRGB();
                if (decode == rgb)
                {
                    String tileName = hexData.getString(1) + "Tile";
                    tileClassName = "gamestates.gameplay.map.tile." + tileName;
                    Tile tile = (Tile) Class.forName(tileClassName).newInstance();
                    return tile;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NumberFormatException | JSONException ex)
        {
            System.err.println("Failed to interpret tile [" + tileClassName + "]");
        }
        return new VoidTile();
    }

    private Prop interpretProp(File file)
    {
        String tileClassName = "";
        try
        {
            JSONObject propJSON = Resource.readJSONFile(file.getPath());
            int x = propJSON.getInt("x");
            int y = propJSON.getInt("y");
            tileClassName = "gamestates.gameplay.map.prop." + propJSON.getString("type") + "Prop";
            Prop prop = (Prop) Class.forName(tileClassName).newInstance();
            prop.construct(this, x, y, propJSON.getJSONObject("config"));
            return prop;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | JSONException e)
        {
            System.err.println("Failed to interpret prop [" + tileClassName + "]");
        }
        return null;
    }

    private void loadProps(String path)
    {
        File folder = new File(path + "/props");
        File[] files = folder.listFiles();

        this.dynamicPropList = new ArrayList<>();
        this.propList = new ArrayList<>();
        this.interactablePropList = new ArrayList<>();

        if (files == null)
        {
            return;
        }

        ArrayList<File> fileList = Resource.getAllJSONFilesFromList(files);

        for (File file : fileList)
        {
            Prop prop = this.interpretProp(file);
            if (prop != null)
            {
                if (prop instanceof DynamicProp)
                {
                    this.dynamicPropList.add((DynamicProp) prop);
                } 
                if (prop instanceof InteractableProp)
                {
                    this.interactablePropList.add((InteractableProp) prop);
                }
                this.propList.add(prop);
            }
        }

    }

    // Prop Updating
    public void update(double delta)
    {
        if (Config.MAP_CHAOS)
        {
            this.chaos();
        }

        this.updateProps(delta);
        this.player.update(delta);
        if (this.interactableHighlight != null && this.engine.isKeyPressed(Config.KeyBind.KEY_INTERACT))
        {
            ((InteractableProp) this.interactableHighlight).interact();
            this.interactableHighlight = null;
        }
    }

    private void updateProps(double delta)
    {
        for (int i = 0; i < this.dynamicPropList.size(); i++)
        {
            this.dynamicPropList.get(i).update(delta, this);
        }
    }

    // Dynamic Rendering
    public void render(Graphics g)
    {
        // Screen Objects
        int xOff = (int) -(this.player.getPos().x * TILE_SIZE);
        int yOff = (int) -(this.player.getPos().y * TILE_SIZE);
        // Centers
        xOff += this.player.getSize().x / 2;
        yOff += this.player.getSize().y / 2;
        xOff += this.engine.getWindow().getWidth() / 2 - TILE_SIZE;
        yOff += this.engine.getWindow().getHeight() / 2 - TILE_SIZE;

        // Constrains
        xOff = (int) Math.min(xOff, 0);
        xOff = (int) Math.max(
                -(this.baseTexture.getWidth() - this.engine.getWindow().getWidth()),
                xOff);

        yOff = (int) Math.min(yOff, 0);
        yOff = (int) Math.max(
                -(this.baseTexture.getHeight() - this.engine.getWindow().getHeight()),
                yOff);

        g.drawImage(this.baseTexture, xOff, yOff, null);
        this.renderProps(g, xOff, yOff);
        this.renderInteractionHighlight(g, xOff, yOff);
        this.renderPlayer(g, xOff, yOff);
        // kishan dont forget to not die k thanks
    }

    private void renderInteractionHighlight(Graphics g, int xOff, int yOff)
    {
        if (this.interactableHighlight == null)
        {
            return;
        }
        double xPos = TILE_SIZE * this.interactableHighlight.getX() + TILE_SIZE / 4;
        double yPos = TILE_SIZE * this.interactableHighlight.getY() - TILE_SIZE / 4;
        yPos -= Math.abs(Math.sin(System.currentTimeMillis() * 4E-3) * TILE_SIZE / 8);
        g.drawImage(Map.NOTIFY_TEXTURE, xOff + (int) xPos, yOff + (int) yPos, TILE_SIZE / 2, TILE_SIZE / 2, null);
    }

    private void renderPlayer(Graphics g, int xOff, int yOff)
    {
        // Offset
        double xPos = xOff + (TILE_SIZE * this.player.getPos().x);
        double yPos = yOff + (TILE_SIZE * this.player.getPos().y);
        xPos -= this.player.getSize().x / 2;
        yPos -= this.player.getSize().y / 2;
        g.drawImage(this.player.getTexture(), (int) xPos, (int) yPos,
                (int) this.player.getSize().x, (int) this.player.getSize().y, null);
    }

    private void renderProps(Graphics g, int xOff, int yOff)
    {
        for (int i = 0; i < this.propList.size(); i++)
        {
            // Determines offset based on prop
            Prop prop = this.propList.get(i);
            double xPos = TILE_SIZE * prop.getX();
            double yPos = TILE_SIZE * prop.getY();
            double xSize = TILE_SIZE;
            double ySize = TILE_SIZE;
            if (prop instanceof CustomScaleProp)
            {
                CustomScaleProp csp = (CustomScaleProp) prop;
                xSize *= csp.getScaleX();
                ySize *= csp.getScaleY();
                xPos -= xSize / 4;
                yPos -= ySize / 4;
            }
            g.drawImage(prop.getTexture(), xOff + (int) xPos, yOff + (int) yPos, (int) xSize, (int) ySize, null);
        }
    }

    // Loading Map Image
    public BufferedImage createMapImage(Window win)
    {
        BufferedImage img = new BufferedImage(
                Map.TILE_SIZE * this.tiles.length,
                Map.TILE_SIZE * this.tiles[0].length,
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics g = img.getGraphics();

        ArrayList<CustomScaleTile> csTiles = new ArrayList<>();
        ArrayList<Vector2> csTilesPos = new ArrayList<>();
        for (int x = 0; x < tiles.length; x++)
        {
            for (int y = 0; y < tiles[0].length; y++)
            {
                Tile tile = this.tiles[x][y];
                double xPos = Map.TILE_SIZE * x;
                double yPos = Map.TILE_SIZE * y;
                if (tile instanceof CustomScaleTile)
                {
                    csTiles.add((CustomScaleTile) tile);
                    csTilesPos.add(new Vector2(x, y));
                } else
                {
                    g.drawImage(tile.getTexture(), (int) xPos, (int) yPos, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }
        for (int i = 0; i < csTiles.size(); i++)
        {
            CustomScaleTile tile = csTiles.get(i);
            Vector2 vec = csTilesPos.get(i);
            double xPos = Map.TILE_SIZE * vec.x;
            double yPos = Map.TILE_SIZE * vec.y;
            double scaleX = tile.getScaleX() * TILE_SIZE;
            double scaleY = tile.getScaleY() * TILE_SIZE;
            xPos -= scaleX / 4;
            yPos -= scaleY / 4;
            g.drawImage(((Tile) tile).getTexture(), (int) xPos, (int) yPos, (int) scaleX, (int) scaleY, null);
        }
        g.dispose();
        return img;
    }

    private void chaos()
    {
        Vector2 rngIndex1 = new Vector2(Math.random() * this.tiles.length,
                Math.random() * this.tiles[0].length);
        Vector2 rngIndex2 = new Vector2(Math.random() * this.tiles.length,
                Math.random() * this.tiles[0].length);
        Tile t1 = this.tiles[(int) rngIndex1.x][(int) rngIndex1.y];
        Tile t2 = this.tiles[(int) rngIndex2.x][(int) rngIndex2.y];
        this.tiles[(int) rngIndex2.x][(int) rngIndex2.y] = t1;
        this.tiles[(int) rngIndex1.x][(int) rngIndex1.y] = t2;
    }

    public void reloadMapTexture()
    {
        this.baseTexture = this.createMapImage(this.engine.getWindow());
        this.miniMap.setMapImage(this.baseTexture);
    }

    // Setters and Getters
    public BufferedImage getBaseTexture()
    {
        return this.baseTexture;
    }

    public ArrayList<InteractableProp> getInteractableProps()
    {
        return this.interactablePropList;
    }

    public void setInteractableHighlight(Prop p)
    {
        this.interactableHighlight = p;
    }

    public void setTileAt(int x, int y, Tile t)
    {
        try
        {
            this.tiles[x][y] = t;
        } catch (IndexOutOfBoundsException e)
        {
        }
    }

    public Tile getTileAt(int x, int y)
    {
        try
        {
            return this.tiles[x][y];
        } catch (IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public ArrayList<Prop> getProps()
    {
        return this.propList;
    }

    public Tile[][] getTiles()
    {
        return tiles;
    }

    public void setTiles(Tile[][] tiles)
    {
        this.tiles = tiles;
    }

    public JSONObject getBaseJSONData()
    {
        return baseJSONData;
    }

    public int getOffsetX()
    {
        double xPos = TILE_SIZE * this.player.getPos().x;

        return (int) xPos;
    }

    public int getOffsetY()
    {
        double yPos = TILE_SIZE * this.player.getPos().y;
        return (int) yPos;
    }

    public MiniMap getMiniMap()
    {
        return this.miniMap;
    }

    public Engine getEngine()
    {
        return this.engine;
    }

    public GameplayState getGameplayState()
    {
        return this.gameplayState;
    }

    public PokemonSpecies[] getAvailableSpecies()
    {
        return this.availableSpecies;
    }

    public ArrayList<DynamicProp> getDynamicPropList()
    {
        return this.dynamicPropList;
    }

    public ArrayList<InteractableProp> getInteractablePropList()
    {
        return this.interactablePropList;
    }

    // Static
}

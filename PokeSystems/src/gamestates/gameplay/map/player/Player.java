package gamestates.gameplay.map.player;

import engine.Config;
import engine.Resource;
import gamestates.gameplay.dialogue.battle.PokemonBattle;
import math.Vector2;
import gamestates.gameplay.map.Map;
import gamestates.gameplay.map.prop.InteractableProp;
import gamestates.gameplay.map.prop.Prop;
import gamestates.gameplay.map.tile.CollidableTile;
import gamestates.gameplay.map.tile.PokemonTile;
import gamestates.gameplay.map.tile.Tile;
import gamestates.gameplay.pokemon.Pokemon;
import gamestates.gameplay.pokemon.PokemonSpecies;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player
{

   // Render/Texture
   private static final float SCALE_MULTIPLIER = 1.5f;
   public static final byte WALK_ANIMATION_SPEED = 4;
   private static final BufferedImage[][] TEXTURES = new BufferedImage[3][4];
   private float animationWalkIndex = 0, animationDir = 0;

   // Vector Properties
   private Vector2 pos, prevPos, size;

   // Pokemon
   private ArrayList<Pokemon> pokemonList;

   // Map / Access to outer direcories
   private Map map;

   static
   {
      new Thread(() ->
      {
	 BufferedImage img = null;
	 try
	 {
	    img = ImageIO.read(new File("assets/textures/hazmat.png"));
	 }
	 catch (IOException ex)
	 {
	    System.err.println("Failed to load player spritesheet");
	 }

	 for (int i = 0; i < TEXTURES.length; i++)
	 {
	    for (int j = 0; j < TEXTURES[0].length; j++)
	    {
	       TEXTURES[i][j] = Resource.getImageFromTileSet(img, 32, 32, i, j);
	    }
	 }
      }).start();
   }

   public Player(int x, int y, Map map)
   {
      this.pos = new Vector2(x, y);
      this.prevPos = this.pos;
      this.map = map;
      this.size = new Vector2(1, 1);
      this.size.mult(Map.TILE_SIZE * Player.SCALE_MULTIPLIER);

      this.pokemonList = new ArrayList<>();
      this.pokemonList.add(new Pokemon(PokemonSpecies.SCALPAL));
   }

   public void update(double delta)
   {
      this.checkForPokemonTile();
      if (Config.PLAYER_FLUID_CONTROL)
      {
	 this.fluidController(delta);
      }
      else
      {
	 this.joystickController(delta);
      }
      if (Config.PLAYER_SPIN)
      {
	 this.animationDir = (this.animationDir + 0.1f) % Player.TEXTURES[0].length;
      }
   }

   // Movement
   private void joystickController(double delta)
   {
      Vector2 dir = new Vector2(0, 0);

      if (this.map.getEngine().isCurrentKeyPressed(Config.KeyBind.KEY_FORWARD))
      {
	 // y- Walk (Forward)
	 this.animationDir = 0;
	 dir.y = -1;
      }
      else if (this.map.getEngine().isCurrentKeyPressed(Config.KeyBind.KEY_LEFT))
      {
	 // x- Walk (Left)
	 this.animationDir = 1;
	 dir.x = -1;
      }
      else if (this.map.getEngine().isCurrentKeyPressed(Config.KeyBind.KEY_RIGHT))
      {
	 // x+ Walk (Right)
	 this.animationDir = 3;
	 dir.x = 1;
      }
      else if (this.map.getEngine().isCurrentKeyPressed(Config.KeyBind.KEY_BACK))
      {
	 // y+ Walk (Backwards)
	 this.animationDir = 2;
	 dir.y = 1;
      }

      // Does not update animation or walk if dir vector is 0
      if (dir.getMag() == 0)
      {
	 if ((int) this.animationWalkIndex != 0)
	 {
	    this.animationWalkIndex += Config.PLAYER_SPEED * Player.WALK_ANIMATION_SPEED * delta;
	    this.animationWalkIndex %= Player.TEXTURES.length;
	 }
	 return;
      }

      // Animation
      this.animationWalkIndex += Config.PLAYER_SPEED * Player.WALK_ANIMATION_SPEED * delta;
      this.animationWalkIndex %= Player.TEXTURES.length;

      // Movement
      dir.setMag(Config.PLAYER_SPEED * delta); // Limits max player speed

      // Creates position after movement
      Vector2 newPos = this.pos.copy();
      newPos.add(dir);

      // Limits position between 0 and map width & height
      newPos.x = Resource.constrain(newPos.x, 0, this.map.getTiles().length - 2);
      newPos.y = Resource.constrain(newPos.y, 0, this.map.getTiles()[0].length - 2);

      // Creates temporary vector that aligns with the center of the player
      Vector2 centerPos = newPos.copy();
      centerPos.add(new Vector2(Player.SCALE_MULTIPLIER / 4, Player.SCALE_MULTIPLIER / 4));

      // Checks if the center of the player after movement will hit a collidable tile
      if (!(this.getTileAtPos(centerPos) instanceof CollidableTile))
      {
	 // If tile hit is not collidable, assigns movement (without offset size scale) to player
	 this.pos = newPos;
      }
      // what do I put here again
   }

   @Deprecated
   private void fluidController(double delta)
   {
      Vector2 dir = new Vector2(0, 0);
      if (this.map.getEngine().isKeyPressed(Config.KeyBind.KEY_FORWARD))
      {
	 // y- Walk (Forward)
	 this.animationDir = 3;
	 dir.y--;
      }
      if (this.map.getEngine().isKeyPressed(Config.KeyBind.KEY_LEFT))
      {
	 // x- Walk (Left)
	 this.animationDir = 1;
	 dir.x--;
      }
      if (this.map.getEngine().isKeyPressed(Config.KeyBind.KEY_RIGHT))
      {
	 // x+ Walk (Right)
	 this.animationDir = 2;
	 dir.x++;
      }
      if (this.map.getEngine().isKeyPressed(Config.KeyBind.KEY_BACK))
      {
	 // y+ Walk (Backwards)
	 this.animationDir = 0;
	 dir.y++;
      }
      if (dir.getMag() == 0)
      {
	 return;
      }

      // Animation
      this.animationWalkIndex += Config.PLAYER_SPEED * Player.WALK_ANIMATION_SPEED * delta;
      this.animationWalkIndex %= Player.TEXTURES.length;

      // Movement
      dir.setMag(Config.PLAYER_SPEED * delta);

      Vector2 newPos = this.pos.copy();
      newPos.add(dir);

      newPos.x = Resource.constrain(newPos.x, 0, this.map.getTiles().length - 2);
      newPos.y = Resource.constrain(newPos.y, 0, this.map.getTiles()[0].length - 2);

      // Checks for Collision with Collidable Tiles
      if (!(this.getTileAtPos(newPos) instanceof CollidableTile))
      {
	 this.pos = newPos;
      }
      // what do I put here again
   }

   private Tile getTileAtPos(Vector2 v)
   {
      return this.map.getTileAt(
	 (int) Math.round(v.x - SCALE_MULTIPLIER / 2),
	 (int) Math.round(v.y - SCALE_MULTIPLIER / 4)
      );
   }

   private void checkForPokemonTile()
   {
      if (!this.prevPos.equals(this.pos)) // If moved
      {
	 if (this.getTileAtPos(this.pos) instanceof PokemonTile) // Execute chance if on PokemonTile
	 {
	    if (Math.random() < Config.PLAYER_INTERACTION_RATE)
	    {
	       // Engage Battle
	       // smells like carbon monoxide up in here
	       this.map.getGameplayState().requestDialogue(new PokemonBattle(this.map, this.map.getAvailableSpecies()));
	    }
	 }
	 else
	 {
	    this.checkForInteractable();
	 }
      }
      this.prevPos = pos.copy();
   }

   private void checkForInteractable()
   {
      this.map.setInteractableHighlight(null);
      for (InteractableProp iprop : this.map.getInteractableProps())
      {
	 Prop prop = (Prop) iprop;
	 Vector2 propPos = new Vector2(prop.getX(), prop.getY());
	 Vector2 diff = this.pos.copy();
	 diff.sub(propPos);
	 if (diff.getMag() < Config.PlAYER_INTERACTION_DISTANCE)
	 {
	    this.map.setInteractableHighlight(prop);
	 }
      }
   }

   // Seters and getters
   public ArrayList<Pokemon> getPokemon()
   {
      return this.pokemonList;
   }

   public BufferedImage getTexture()
   {
      return Player.TEXTURES[(int) this.animationWalkIndex][(int) this.animationDir];
   }

   public void setPos(Vector2 v)
   {
      this.pos = v.copy();
   }

   public Vector2 getPos()
   {
      return this.pos;
   }

   public Vector2 getSize()
   {
      return this.size;
   }
}

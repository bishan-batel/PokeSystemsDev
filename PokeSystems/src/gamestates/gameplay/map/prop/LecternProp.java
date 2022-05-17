/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.gameplay.map.prop;

import engine.Resource;
import gamestates.gameplay.dialogue.LecternDialogue;
import gamestates.gameplay.dialogue.LecternDialogueData;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LecternProp extends Prop implements InteractableProp
{

    private static final BufferedImage[] TEXTURES;
    private byte index = 0;
    private LecternDialogueData[] dialogueData;

    static
    {
        BufferedImage img;
        BufferedImage[] sprites = null;
        try
        {
            img = ImageIO.read(new File("assets/textures/prop/lectern.png"));
            sprites = new BufferedImage[]
            {
                Resource.getImageFromTileSet(img, 16, 16, 0, 0),
                Resource.getImageFromTileSet(img, 16, 16, 0, 1),
                Resource.getImageFromTileSet(img, 16, 16, 0, 2),
                Resource.getImageFromTileSet(img, 16, 16, 0, 3),
                null
            };
        } catch (IOException ex)
        {
            System.err.println("Failed to load lectern prop image");
        }
        TEXTURES = sprites;

    }

    @Override
    public void constructor()
    {
        try
        {
            this.index = (byte) this.config.getInt("direction");
        } catch (JSONException e)
        {
            this.index = 0;
        }
        try
        {
            JSONArray info = this.config.getJSONArray("info");

            this.dialogueData = new LecternDialogueData[info.length()];

            for (int i = 0; i < info.length(); i++)
            {
                // Gets JSON from cell
                JSONObject dialogueJSON = info.getJSONObject(i);

                // Gets command array from json and converts to string array
                JSONArray commandsJSON = dialogueJSON.getJSONArray("commands");
                String[] commands = new String[commandsJSON.length()];
                for (int j = 0; j < commandsJSON.length(); j++)
                {
                    commands[j] = commandsJSON.getString(j);
                }
                // Uses command list and text from cell and creates lectern dialogue data
                this.dialogueData[i] = new LecternDialogueData(this.map, dialogueJSON.getString("text"), commands);
            }
        } catch (JSONException e)
        {
            System.err.println("Failed to load text assets");
            this.dialogueData = new LecternDialogueData[]
            {
                new LecternDialogueData(this.map, "Failed to load text assets", new String[0])
            };
        }
    }

    @Override
    public BufferedImage getTexture()
    {
        return TEXTURES[this.index];
    }

    @Override
    public void interact()
    {
        this.map.getGameplayState().requestDialogue(new LecternDialogue(this.dialogueData, this.map));
    }
}

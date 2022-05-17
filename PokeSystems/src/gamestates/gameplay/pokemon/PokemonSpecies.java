package gamestates.gameplay.pokemon;

import engine.Config;
import engine.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public enum PokemonSpecies
{
    // PokemonSpecies List
    // Normal
    SCALPAL("Scalpal", "normal/"),
    SILVER_KEY("Silver Key", "normal/"),
    // Integumentary
    SKIN_TISSUE("Skin Tissue", "integumentary/"),
    HAIR_FOLLICLE("Hair Follicle", "integumentary/"),
    // Immune
    T_CELL("T Cell", "immune/"),
    B_CELL("B Cell", "immune/"),
    // Circulatory
    VEIN("Vein", "circulatory/"),
    ARTERY("Artery", "circulatory/"),
    // Respitory
    ALVEOLI("Alveoli", "respiratory/"),
    LUNG_TISSUE("Lung Tissue", "respiratory/"),
    // Lympnatic
    LYMPH_NODE("Lymph Node", "lympnatic/"),
    // Skeletal
    BONE_MARROW("Bone Marrow", "skeletal/"),
    BONE_TISSUE("Bone Tissue", "skeletal/"),
    // Digestive
    STOMACH_ACID("Stomach Acid", "digestive/"),
    SMALL_INTESTINE_TISSUE("Small Intestine Tissue", "digestive/"),
    // Muscular
    SKELETAL_MUSCLE_TISSUE("Skeletal Muscle Tissue", "muscular/"),
    SMOOTH_MUSCLE_TISSUE("Smooth Muscle Tissue", "muscular/"),
    CARDIAC_MUSCLE_TISSUE("Cardiac Muscle Tissue", "muscular/"),
    // Nervous
    BRAIN_TISSUE("Brain Tissue", "nervous/"),
    PERIPHERAL_NERVE("Peripheral Nerve", "nervous/"),
    // Reproductive
    EPIDIDYMIS_TISSUE("Epididymis Tissue", "reproductive/"),
    TESTES_TISSUE("Testes Tissue", "reproductive/"),
    // Endocrine
    ADRENAL_GLAND_TISSUE("Adrenal Gland Tissue", "endocrine/"),
    HYPOTHALAMUS_TISSUE("Hypothalamus Tissue", "endocrine/"),
    //Excretory
    KIDNEY_TISSUE("Kidney Tissue", "excretory/"),
    BLADDER_TISSUE("Bladder Tissue", "excretory/");
    // Interpetation / File loading
    private PokemonType type;
    private int maxHealth;
    private String name, description;
    private BufferedImage icon;
    private PokemonAttack[] attacks;

    private PokemonSpecies(String name, String rawPath)
    {
        System.err.println("Attempting load of " + name + " at folder " + rawPath);
        this.name = name;
        // File Loading
        final String path = Config.POKEMON_FOLDER + rawPath;
        String jsonFilePath = path + name + ".json";
        String iconFilePath = path + name + ".png";
        try
        {
            JSONObject pokemonData = Resource.readJSONFile(jsonFilePath); // Attempts to get JSON File and Data

            // JSON Properties
            this.type = PokemonType.getPokemonTypeFromName(pokemonData.getString("type"));
            this.maxHealth = pokemonData.getInt("max_health");
            this.description = pokemonData.getString("description").replace("\t", "");

            System.out.println("Load successfull for " + name + " type, health, and description");

            JSONArray attackJSON = pokemonData.getJSONArray("attacks");
            this.attacks = new PokemonAttack[attackJSON.length()];
            for (int i = 0; i < this.attacks.length; i++)
            {
                JSONObject attackObj = attackJSON.getJSONObject(i);
                String attackName = attackObj.getString("attack_name");
                int dmg = attackObj.getInt("attack_damage");
                this.attacks[i] = new PokemonAttack(attackName, dmg);
                System.out.println("Loaded attack (" + attackName + "[DMG:" + dmg + "] )");
            }

            // Image
            this.icon = ImageIO.read(new File(iconFilePath));
        } catch (JSONException | IOException e)
        {
            // Defaults
            this.name = "Error loading JSON";
            this.maxHealth = 0;
            this.type = PokemonType.NORMAL;
            this.attacks = new PokemonAttack[]
            {
                new PokemonAttack("[Error Loading Attack]", 0)
            };
            this.icon = null;
            System.err.println("Failed to load species " + name);
        }
    }

    public PokemonAttack[] getAttacks()
    {
        return this.attacks;
    }

    public String getDescription()
    {
        return this.description;
    }

    public PokemonType getPokemonType()
    {
        return this.type;
    }

    public int getMaxHealth()
    {
        return this.maxHealth;
    }

    public BufferedImage getIcon()
    {
        return this.icon;
    }

    public String getName()
    {
        return this.name;
    }

    public static void main(String[] args)
    {
    }
}

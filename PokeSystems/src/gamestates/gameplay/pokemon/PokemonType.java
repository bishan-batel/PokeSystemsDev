package gamestates.gameplay.pokemon;

public enum PokemonType
{
    NORMAL("normal"),
    ITEM("item"),
    CIRCULATORY("circulatory"),
    DIGESTIVE("digestive"),
    ENDOCRINE("endocrine"),
    EXCRETORY("excretory"),
    IMMUNE("immune"),
    INTEGUMENTARY("integumentary"),
    LYMPNATIC("lympnatic"),
    MUSCULAR("muscular"),
    NERVOUS("nervous"),
    REPRODUCTIVE("reproductive"),
    RESPITORY("respiratory"),
    SKELETAL("skeletal");

    public final String TYPE_NAME;

    private PokemonType(String typename)
    {
        this.TYPE_NAME = typename;
    }

    public String getTypeNameCapitilized()
    {
        String firstLetter = this.TYPE_NAME.charAt(0) + "";
        return TYPE_NAME.replaceFirst(firstLetter, firstLetter.toUpperCase()); // Capitlizes First Letter
    }

    public static PokemonType getPokemonTypeFromName(String typename)
    {
        for (PokemonType type : PokemonType.values())
        {
            // Checks to see if typename is equal to any of existing pokemon typenames
            if (type.TYPE_NAME.equals(typename))
            {
                // Returns if found
                return type;
            }
        }
        // Defaults to 'normal'
        return PokemonType.NORMAL;
    }
}

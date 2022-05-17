package gamestates.gameplay.pokemon;

public class Pokemon
{
    private final PokemonSpecies SPECIES;
    private int health;

    public Pokemon(PokemonSpecies species)
    {
        this.SPECIES = species;
        this.health = this.SPECIES.getMaxHealth();
    }

    public void attackPokemon(PokemonAttack attack, Pokemon other)
    {
        this.heal(attack.getHealForSelf()); // Heals self
        other.damage(attack.getDamageForEnemy()); // Damages other
    }

    // Health Management
    public void heal(int dmg)
    {
        this.health += dmg;
        this.health = (int) Math.min(this.health, this.SPECIES.getMaxHealth());
    }

    public void damage(int dmg)
    {
        this.health -= dmg;
        this.health = (int) Math.max(this.health, 0);
    }

    // Setters and Getters
    public int getHealth()
    {
        return this.health;
    }

    public PokemonSpecies getSpecies()
    {
        return this.SPECIES;
    }
}

package gamestates.gameplay.pokemon;

public final class PokemonAttack
{

    private final String ATTACK_NAME;
    private final int ATTACK_DAMAGE;

    protected PokemonAttack(String name, int dmg)
    {
        this.ATTACK_NAME = name;
        this.ATTACK_DAMAGE = dmg;
    }

    public String getName()
    {
        return this.ATTACK_NAME;
    }

    public int getHealForSelf()
    {
        return -Math.min(0, this.ATTACK_DAMAGE);
    }

    public int getDamageForEnemy()
    {
        return Math.max(this.ATTACK_DAMAGE, 0);
    }
}

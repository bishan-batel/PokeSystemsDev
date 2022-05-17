/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestates.gameplay.dialogue;

/**
 *
 * @author schro
 */
public enum DialogueType
{
    NORMAL("[normal]"),
    BOOLEAN("[boolean]"),
    QUERY("[query]");
    public static final String DATA_SPLIT = "data*";
    public static final String QUERY_POKEMON_REQUIRED = "pr";
    
    public final String KEY;

    private DialogueType(String key)
    {
        this.KEY = key;
    }
}

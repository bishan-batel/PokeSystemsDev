package gamestates.gameplay.dialogue;

import gamestates.gameplay.map.Map;

public class LecternDialogueData
{

    private String[] commands;
    private String text;
    private Map map;
    
    public LecternDialogueData(Map map, String text, String[] commands)
    {
        this.map = map;
        this.text = text;
        this.commands = commands;
    }

    public void execute()
    {
        for (String cmd : this.commands)
        {
            this.map.getEngine().getConsole().execute(cmd);
        }
    }

    public DialogueType getType()
    {
        for (DialogueType type : DialogueType.values())
        {
            if (this.text.startsWith(type.KEY))
            {
                return type;
            }
        }
        return DialogueType.NORMAL;
    }

    public String getRawText()
    {
        return this.text;
    }
    public String getText()
    {
        return this.text.replace(this.getType().KEY, "").split(DialogueType.DATA_SPLIT)[0];
    }
}

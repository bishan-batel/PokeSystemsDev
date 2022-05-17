
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PokemonCreator extends JFrame implements ActionListener
{

    private static final Font FONT = new Font("Consolas", Font.BOLD, 16);
    private JTextField nameField, healthField;
    private AttackField[] attackFields = new AttackField[4];
    private JTextArea descriptionArea;
    private JComboBox<String> typeCombo;
    private JButton button;
    private String[] types =
    {
        "circulatory",
        "digestive",
        "endocrine",
        "excretory",
        "muscular",
        "immune",
        "integumentary",
        "lympnatic",
        "nervous",
        "reproductive",
        "respitory",
        "skeletal",
        "normal",
        "item"
    };
    private Box box;

    public PokemonCreator()
    {
        super("Pokemon JSON Creator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.box = Box.createVerticalBox();

        this.nameField = new JTextField(10);
        this.nameField.setFont(FONT);
        JLabel label = new JLabel("Name: ");
        label.setFont(FONT);
        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(this.nameField);
        this.box.add(panel);

        this.typeCombo = new JComboBox<>(types);
        this.box.add(this.typeCombo);
        this.healthField = new JTextField(10);
        this.healthField.setFont(FONT);
        label = new JLabel("Health: ");
        label.setFont(FONT);
        panel = new JPanel();
        panel.add(label);
        panel.add(this.healthField);
        this.box.add(panel);

        label = new JLabel("Attacks", JLabel.CENTER);
        label.setFont(FONT);
        this.box.add(label);
        for (int i = 0; i < attackFields.length; i++)
        {
            attackFields[i] = new AttackField();
            this.box.add(this.attackFields[i]);
        }

        this.descriptionArea = new JTextArea("Description goes here", 5, 0);
        this.descriptionArea.setBackground(new Color(21, 21, 21));
        this.descriptionArea.setForeground(new Color(244, 244, 244));
        this.descriptionArea.setFont(FONT);
        this.box.add(this.descriptionArea);

        this.button = new JButton("Generated JSON File");
        this.button.setFont(FONT);
        this.button.addActionListener(this);
        this.box.add(this.button);

        this.add(this.box);
        this.setVisible(true);
        this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            JSONObject jsonFile = new JSONObject();
            jsonFile.put("name", this.nameField.getText());
            jsonFile.put("max_health", Double.parseDouble(this.healthField.getText()));
            jsonFile.put("type", (String) this.types[this.typeCombo.getSelectedIndex()]);
            JSONArray attackArray = new JSONArray();
            for (AttackField attackField : this.attackFields)
            {
                if (!attackField.isBlank())
                {
                    JSONObject attackObj = new JSONObject();
                    attackObj.put("attack_name", attackField.getText());
                    attackObj.put("attack_damage", attackField.getDamage());
                    attackArray.put(attackObj);
                }
            }
            jsonFile.put("attacks", attackArray);
            jsonFile.put("description", this.descriptionArea.getText().replace("\n", " ").replace("  ", " "));

            try (FileWriter printer = new FileWriter(this.nameField.getText() + ".json"))
            {
                printer.write(jsonFile.toString());
                printer.flush();
            }
        } catch (IOException | NumberFormatException | JSONException ex)
        {
        }

    }

    private class AttackField extends JPanel
    {

        private JTextField attackNameField, damageField;

        public AttackField()
        {
            super();
            this.attackNameField = new JTextField(10);
            this.attackNameField.setFont(PokemonCreator.FONT);

            JLabel label = new JLabel("Name:");
            label.setFont(PokemonCreator.FONT);
            this.add(label);
            this.add(this.attackNameField);

            this.damageField = new JTextField(3);
            this.damageField.setFont(PokemonCreator.FONT);
            label = new JLabel("Damage:");
            label.setFont(PokemonCreator.FONT);
            this.add(label);
            this.add(this.damageField);
        }

        public boolean isBlank()
        {
            return this.getText().isEmpty();
        }

        public String getText()
        {
            String txt = this.attackNameField.getText();
            return txt;
        }

        public double getDamage() throws NumberFormatException
        {
            return Double.parseDouble(this.damageField.getText());
        }
    }

    public static void main(String[] args)
    {
        new PokemonCreator();
    }
}

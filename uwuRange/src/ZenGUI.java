import org.dreambot.api.script.AbstractScript;
import org.dreambot.core.Instance;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

/**
 * This class creates a generic re-usable script settings GUI.
 * It will also save and load the input data for each OSRS account between use.
 *
 * @author Zenarchist
 * @version 1.0
 */
public class ZenGUI implements ActionListener, FocusListener, ChangeListener {
    // Main class variables
    private boolean START_BUTTON_PRESSED = false;
    private boolean CANCEL_BUTTON_PRESSED = false;
    private final int WIDTH = 420;
    private final int HEIGHT = 250;
    private Preferences SAVE;
    // GUI components
    private JFrame frame;
    private JTabbedPane tabbedPane;
    // JPanels (Panel ID = order in which it was added)
    private ArrayList<JPanel> TAB_PANELS = new ArrayList<>();
    // Input components
    private HashMap<String, JTextField> INPUT_TEXTFIELDS = new HashMap<>();
    private HashMap<String, JSpinner> INPUT_SPINNERS = new HashMap<>();
    private HashMap<String, JCheckBox> INPUT_CHECKBOX = new HashMap<>();
    private HashMap<String, JSlider> INPUT_SLIDER = new HashMap<>();
    private HashMap<String, JComboBox> INPUT_COMBO = new HashMap<>();
    // Raw input collections
    private HashMap<String, String> SETTINGS_TEXT = new HashMap<>();
    private HashMap<String, Integer> SETTINGS_INT = new HashMap<>();
    private HashMap<String, Boolean> SETTINGS_BOOL = new HashMap<>();
    // Colors
    //private Color COL_FOREGROUND = Color.white;
    private Color COL_FOREGROUND = new Color(0, 246, 255, 255);
    private Color COL_HIGHLIGHT = new Color(230, 40, 255);
    private Color COL_BACKGROUND = new Color(254, 0, 241, 255);
    private Color COL_FRAME_BACKGROUND = new Color(177, 0, 197);
    // Default Font
    private Font DEFAULT_FONT = new Font("Courier New", Font.BOLD, 14);
    // Script helper class
    private AbstractScript script;
    // Location of frame icon image
    private String ICON_URL = "https://i.imgur.com/DxhuwFC.png";

    // This creates a ZenGUI with the given Script class (for access to AbstractScript)
    public ZenGUI(AbstractScript script) {
        this.script = script;
        this.SAVE = Preferences.userNodeForPackage(ZenGUI.class);
        // Setup JFrame
        setupThemeColors();
        frame = new JFrame(script.getManifest().name() + " GUI");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.setBackground(COL_FRAME_BACKGROUND);
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COL_FRAME_BACKGROUND);
        tabbedPane.setForeground(COL_FOREGROUND);
        tabbedPane.setFont(DEFAULT_FONT);
    }

    // Sets up the custom UI look & feel colors for the Tabbed Pane to match DB's color theme
    private void setupThemeColors() {
        UIManager.put("TabbedPane.borderColor", COL_FRAME_BACKGROUND);
        UIManager.put("TabbedPane.darkShadow", COL_FRAME_BACKGROUND);
        UIManager.put("TabbedPane.light", COL_FRAME_BACKGROUND);
        UIManager.put("TabbedPane.highlight", COL_FRAME_BACKGROUND);
        UIManager.put("TabbedPane.focus", COL_FRAME_BACKGROUND);
        UIManager.put("TabbedPane.selected", COL_FRAME_BACKGROUND);
        UIManager.put("TabbedPane.unselectedBackground", COL_FRAME_BACKGROUND);
        UIManager.put("TabbedPane.selectHighlight", COL_FRAME_BACKGROUND);
        UIManager.put("TabbedPane.tabAreaBackground", COL_FRAME_BACKGROUND);
        UIManager.put("TabbedPane.borderHightlightColor", Color.white);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
    }

    // Adds a panel to the main tabbed pane
    public void addPanel(String title, String tooltip) {
        JPanel newPanel = new JPanel();
        newPanel.setName(title);
        newPanel.setBackground(COL_FRAME_BACKGROUND);
        newPanel.setLayout(new GridLayout(0, 2));
        newPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab(title, null, newPanel, tooltip);
        TAB_PANELS.add(newPanel);
    }

    // Short-hand for adding panel inputs
    public void addStringInput(int panelID, String title, String key, String defaultValue) {
        addStringInput(panelID, title, key, null, defaultValue);
    }

    // Adds a String input to the GUI
    public void addStringInput(int panelID, String title, String key, String tooltip, String defaultValue) {
        JLabel label = getLabel(title);
        JTextField field = new JTextField();
        // Setup text field
        field.setName(key);
        field.setToolTipText(tooltip);
        field.setText(defaultValue);
        field.setBackground(COL_BACKGROUND);
        field.setForeground(COL_FOREGROUND);
        field.setCaretColor(COL_HIGHLIGHT);
        field.setSelectionColor(COL_HIGHLIGHT);
        field.setFont(DEFAULT_FONT);
        field.setColumns(50);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.addActionListener(this);
        field.addFocusListener(this);
        // Add the new components to the content pane
        JPanel p = TAB_PANELS.get(panelID);
        if(p == null) {
            script.log("[GUI] Error: Panel ID is null - " + panelID);
            return;
        }

        p.add(label);
        p.add(field);
        INPUT_TEXTFIELDS.put(key, field);
    }

    // Short-hand for adding panel inputs
    public void addIntegerInput(int panelID, String title, String key, int defaultValue, int min, int max) {
        addIntegerInput(panelID, title, key, null, defaultValue, min, max);
    }

    // Adds an Integer input to the GUI
    public void addIntegerInput(int panelID, String title, String key, String tooltip, int defaultValue, int min, int max) {
        JLabel label = getLabel(title);
        SpinnerNumberModel snm = new SpinnerNumberModel(defaultValue, min, max, 1);
        JSpinner spinner = new JSpinner(snm);
        // Setup the spinner
        spinner.setName(key);
        spinner.setToolTipText(tooltip);
        spinner.setValue(defaultValue);
        spinner.setFont(DEFAULT_FONT);
        // Search for and set the JSpinner's textfield paramters
        for(int i = 0; i < spinner.getEditor().getComponents().length; i++) {
            Component c = spinner.getEditor().getComponent(i);
            if(c instanceof JTextField) {
                c.setForeground(COL_FOREGROUND);
                c.setBackground(COL_BACKGROUND);
                ((JTextField) c).setCaretColor(COL_HIGHLIGHT);
                ((JTextField) c).setSelectionColor(COL_HIGHLIGHT);
                ((JTextField) c).setColumns(4);
                ((JTextField) c).setHorizontalAlignment(JTextField.CENTER);
                c.addFocusListener(this);
            }
        }
        // Add the new components to the content pane
        JPanel p = TAB_PANELS.get(panelID);
        if(p == null) {
            script.log("[GUI] Error: Panel ID is null - " + panelID);
            return;
        }

        p.add(label);
        p.add(spinner);
        INPUT_SPINNERS.put(key, spinner);
    }

    // Short-hand for adding panel inputs
    public void addBooleanInput(int panelID, String title, String key, boolean defaultValue) {
        addBooleanInput(panelID, title, key, null, defaultValue);
    }

    // Adds an Boolean input to the GUI
    public void addBooleanInput(int panelID, String title, String key, String tooltip, boolean defaultValue) {
        JLabel label = getLabel(title);
        JCheckBox checkbox = new JCheckBox();
        // Setup the checkbox
        checkbox.setName(key);
        checkbox.setToolTipText(tooltip);
        checkbox.setSelected(defaultValue);
        checkbox.setForeground(COL_FOREGROUND);
        checkbox.setBackground(COL_FRAME_BACKGROUND);
        // Add the new components to the content pane
        JPanel p = TAB_PANELS.get(panelID);
        if(p == null) {
            script.log("[GUI] Error: Panel ID is null - " + panelID);
            return;
        }

        p.add(label);
        p.add(checkbox);
        INPUT_CHECKBOX.put(key, checkbox);
    }

    // Short-hand for adding panel inputs
    public void addSliderInput(int panelID, String title, String key, int defaultValue, int min, int max, int majorTicks, int minorTicks) {
        addSliderInput(panelID, title, key, null, defaultValue, min, max, majorTicks, minorTicks);
    }

    // Adds a Slider input to the GUI
    public void addSliderInput(int panelID, String title, String key, String tooltip, int defaultValue, int min, int max, int majorTicks, int minorTicks) {
        JLabel label = getLabel(title);
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, defaultValue);
        // Setup the checkbox
        slider.setName(key);
        slider.setForeground(COL_FOREGROUND);
        slider.setBackground(COL_FRAME_BACKGROUND);
        slider.setMajorTickSpacing(majorTicks);
        slider.setMinorTickSpacing(minorTicks);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        slider.setToolTipText("Value: " + slider.getValue() + (slider.getName().equals("antibanrate") ? "%" : ""));
        // Add the new components to the content pane
        JPanel p = TAB_PANELS.get(panelID);
        if(p == null) {
            script.log("[GUI] Error: Panel ID is null - " + panelID);
            return;
        }

        p.add(label);
        p.add(slider);
        INPUT_SLIDER.put(key, slider);
    }

    // Short-hand for adding panel inputs
    public void addComboInput(int panelID, String title, String key, int defaultValue, String... list) {
        addComboInput(panelID, title, key,  null, defaultValue, list);
    }

    // Adds a Slider input to the GUI
    public void addComboInput(int panelID, String title, String key, String tooltip, int defaultValue, String... list) {
        JLabel label = getLabel(title);
        JComboBox combo = new JComboBox(list);
        // Setup the checkbox
        combo.setName(key);
        combo.setToolTipText(tooltip);
        combo.setEditable(false);
        combo.setSelectedIndex(defaultValue);
        combo.setForeground(COL_FOREGROUND);
        combo.setBackground(COL_BACKGROUND);
        // Add the new components to the content pane
        JPanel p = TAB_PANELS.get(panelID);
        if(p == null) {
            script.log("[GUI] Error: Panel ID is null - " + panelID);
            return;
        }

        p.add(label);
        p.add(combo);
        INPUT_COMBO.put(key, combo);
    }

    // Returns a label with the given text
    private JLabel getLabel(String text) {
        JLabel label = new JLabel();
        JTextField field = new JTextField();
        // Setup label
        label.setText(" " + text + ": ");
        label.setForeground(COL_FOREGROUND);
        label.setFont(DEFAULT_FONT);
        return label;
    }

    // Builds and shows the GUI to the user
    public boolean show() {
        // Enable mouse & keyboard input to the game while the GUI is open
        Instance.getInstance().setMouseInputEnabled(true);
        Instance.getInstance().setKeyboardInputEnabled(true);
        loadPreferences();
        // Add START & CANCEL buttons to each tab
        for(JPanel p : TAB_PANELS) {
            JButton startBtn = new JButton("START");
            JButton cancelBtn = new JButton("CANCEL");
            startBtn.setBackground(COL_BACKGROUND);
            startBtn.setForeground(COL_FOREGROUND);
            startBtn.setFont(DEFAULT_FONT);
            startBtn.addActionListener(this);
            cancelBtn.setBackground(COL_BACKGROUND);
            cancelBtn.setForeground(COL_FOREGROUND);
            cancelBtn.setFont(DEFAULT_FONT);
            cancelBtn.addActionListener(this);
            p.add(startBtn);
            p.add(cancelBtn);
        }
        frame.setContentPane(tabbedPane);
        frame.pack();
        frame.setVisible(true);
        frame.toFront();
        getFrameIcon();
        // This loop waits for the frame to close before allowing the script to start
        while(!finished()) { script.sleep(10); }
        if(CANCEL_BUTTON_PRESSED) {
            script.stop();
            return false;
        } else
            printSettings();

        // Script has started - disable input
        Instance.getInstance().setMouseInputEnabled(false);
        Instance.getInstance().setKeyboardInputEnabled(false);
        return true;
    }

    // This method prints the input settings to console (for debugging purposes)
    private void printSettings() {
        String print = "";
        for(String k : SETTINGS_TEXT.keySet()) {
            String value = SETTINGS_TEXT.get(k);
            if(!value.equals(""))
                print = print + k + "=" + value + ",";
        }
        for(String k : SETTINGS_INT.keySet()) {
            int value = SETTINGS_INT.get(k);
            print = print + k + "=" + value + ",";
        }
        for(String k : SETTINGS_BOOL.keySet()) {
            boolean value = SETTINGS_BOOL.get(k);
            print = print + k + "=" + value + ",";
        }

        script.log("[GUI] " + print.substring(0, print.length()-1));
    }

    // This method returns true if the user has clicked START or CANCEL
    public boolean finished() {
        return START_BUTTON_PRESSED || CANCEL_BUTTON_PRESSED;
    }

    // Returns the user input for the given String variable key
    public String getString(String key) {
        return SETTINGS_TEXT == null ? "null" : SETTINGS_TEXT.get(key);
    }

    // Returns the user input for the given Integer variable key
    public int getInt(String key) {
        return SETTINGS_INT == null ? -1 : SETTINGS_INT.get(key);
    }

    // Returns the user input for the given Boolean variable key
    public boolean getBoolean(String key) {
        return SETTINGS_BOOL == null ? false : SETTINGS_BOOL.get(key);
    }

    // This method stores the input values into a raw datatype collection
    private void storeSettings() {
        // Store raw values from UI elements and save application preferences
        for(String k : INPUT_TEXTFIELDS.keySet()) {
            SETTINGS_TEXT.put(k, INPUT_TEXTFIELDS.get(k).getText());
            SAVE.put(script.getLocalPlayer().getName() + "-" + k, INPUT_TEXTFIELDS.get(k).getText());
        }
        for(String k : INPUT_SPINNERS.keySet()) {
            SETTINGS_INT.put(k, (int) INPUT_SPINNERS.get(k).getValue());
            SAVE.putInt(script.getLocalPlayer().getName() + "-" + k, (int) INPUT_SPINNERS.get(k).getValue());
        }
        for(String k : INPUT_CHECKBOX.keySet()) { // Store as string because you can't use pref.getBoolean(key, null) == null as booleans don't allow for null values
            SETTINGS_BOOL.put(k, INPUT_CHECKBOX.get(k).isSelected());
            SAVE.put(script.getLocalPlayer().getName() + "-" + k, INPUT_CHECKBOX.get(k).isSelected() == true ? "true" : "false");
        }
        for(String k : INPUT_SLIDER.keySet()) {
            SETTINGS_INT.put(k, INPUT_SLIDER.get(k).getValue());
            SAVE.putInt(script.getLocalPlayer().getName() + "-" + k, INPUT_SLIDER.get(k).getValue());
        }
        for(String k : INPUT_COMBO.keySet()) {
            SETTINGS_INT.put(k, INPUT_COMBO.get(k).getSelectedIndex());
            SAVE.putInt(script.getLocalPlayer().getName() + "-" + k, INPUT_COMBO.get(k).getSelectedIndex());
        }
        // Clean up UI components to save RAM
        INPUT_TEXTFIELDS.clear();
        INPUT_SPINNERS.clear();
        INPUT_CHECKBOX.clear();
        INPUT_SLIDER.clear();
        INPUT_COMBO.clear();
        INPUT_TEXTFIELDS = null;
        INPUT_SPINNERS = null;
        INPUT_CHECKBOX = null;
        INPUT_SLIDER = null;
        INPUT_COMBO = null;
    }

    // Loads any saved data from the system to display into the UI
    private void loadPreferences() {
        // First check text fields
        for(String s : INPUT_TEXTFIELDS.keySet()) {
            if (INPUT_TEXTFIELDS.get(s) != null && SAVE.get(script.getLocalPlayer().getName() + "-" + s, null) != null) {
                JTextField txt = INPUT_TEXTFIELDS.get(s);
                txt.setText(SAVE.get(script.getLocalPlayer().getName() + "-" + s, null));
            }
        }

        // Next check spinners
        for(String s : INPUT_SPINNERS.keySet()) {
            if (INPUT_SPINNERS.get(s) != null && SAVE.getInt(script.getLocalPlayer().getName() + "-" + s, -1) != -1) {
                JSpinner spinner = INPUT_SPINNERS.get(s);
                spinner.setValue(SAVE.getInt(script.getLocalPlayer().getName() + "-" + s, -1));
            }
        }

        // Next check checkboxes
        for(String s : INPUT_CHECKBOX.keySet()) {
            if (INPUT_CHECKBOX.get(s) != null && SAVE.get(script.getLocalPlayer().getName() + "-" + s, null) != null) {
                JCheckBox box = INPUT_CHECKBOX.get(s);
                box.setSelected(SAVE.get(script.getLocalPlayer().getName() + "-" + s, null).equals("true"));
            }
        }

        // Next check sliders
        for(String s : INPUT_SLIDER.keySet()) {
            if(INPUT_SLIDER.get(s) != null && SAVE.getInt(script.getLocalPlayer().getName() + "-" + s, -1) != -1) {
                JSlider slider = INPUT_SLIDER.get(s);
                slider.setValue(SAVE.getInt(script.getLocalPlayer().getName() + "-" + s, -1));
            }
        }

        // Next check combo boxes
        for(String s : INPUT_COMBO.keySet()) {
            if (INPUT_COMBO.get(s) != null && SAVE.getInt(script.getLocalPlayer().getName() + "-" + s, -1) != -1) {
                JComboBox box = INPUT_COMBO.get(s);
                box.setSelectedIndex(SAVE.getInt(script.getLocalPlayer().getName() + "-" + s, -1));
            }
        }
    }

    // Finds the given input component and sets its default value
    // This can only be used by scripts before they call show(), as show() stalls script until START or CANCEL is pressed
    public void setValue(String key, Object value) {
        JComponent c = null;

        // First check text fields
        for(String s : INPUT_TEXTFIELDS.keySet())
            if(s.equals(key) && INPUT_TEXTFIELDS.get(s) != null)
                c = INPUT_TEXTFIELDS.get(s);

        // Next check spinners
        if(c == null) {
            for(String s : INPUT_SPINNERS.keySet())
                if(s.equals(key) && INPUT_SPINNERS.get(s) != null)
                    c = INPUT_SPINNERS.get(s);
        }

        // Next check checkboxes
        if(c == null) {
            for(String s : INPUT_CHECKBOX.keySet())
                if(s.equals(key) && INPUT_CHECKBOX.get(s) != null)
                    c = INPUT_CHECKBOX.get(s);
        }

        // Next check sliders
        if(c == null) {
            for(String s : INPUT_SLIDER.keySet())
                if(s.equals(key) && INPUT_SLIDER.get(s) != null)
                    c = INPUT_SLIDER.get(s);
        }

        // Next check combo boxes
        if(c == null) {
            for(String s : INPUT_COMBO.keySet())
                if(s.equals(key) && INPUT_COMBO.get(s) != null)
                    c = INPUT_COMBO.get(s);
        }

        // If the component was found, set the tooltip
        if(c != null) {
            if(c instanceof JTextField) {
                ((JTextField)c).setText(value.toString());
            } else
            if(c instanceof JSpinner) {
                ((JSpinner)c).setValue(value);
            } else
            if(c instanceof JCheckBox) {
                ((JCheckBox)c).setSelected((Boolean)value);
            } else
            if(c instanceof JSlider) {
                ((JSlider)c).setValue((int)value);
            } else
            if(c instanceof JComboBox) {
                ((JComboBox)c).setSelectedIndex((int)value);
            }
        }
    }

    // Finds the given input component and sets whether or not it is enabled
    public void setEnabled(String key, boolean enabled) {
        JComponent c = null;

        // First check text fields
        for(String s : INPUT_TEXTFIELDS.keySet())
            if(s.equals(key) && INPUT_TEXTFIELDS.get(s) != null)
                c = INPUT_TEXTFIELDS.get(s);

        // Next check spinners
        if(c == null) {
            for(String s : INPUT_SPINNERS.keySet())
                if(s.equals(key) && INPUT_SPINNERS.get(s) != null)
                    c = INPUT_SPINNERS.get(s);
        }

        // Next check checkboxes
        if(c == null) {
            for(String s : INPUT_CHECKBOX.keySet())
                if(s.equals(key) && INPUT_CHECKBOX.get(s) != null)
                    c = INPUT_CHECKBOX.get(s);
        }

        // Next check sliders
        if(c == null) {
            for(String s : INPUT_SLIDER.keySet())
                if(s.equals(key) && INPUT_SLIDER.get(s) != null)
                    c = INPUT_SLIDER.get(s);
        }

        // Next check combo boxes
        if(c == null) {
            for(String s : INPUT_COMBO.keySet())
                if(s.equals(key) && INPUT_COMBO.get(s) != null)
                    c = INPUT_COMBO.get(s);
        }

        // If the component was found, set the enabled flag
        if(c != null)
            c.setEnabled(enabled);
    }

    // Finds the given input component and sets the tooltip text
    public void setTooltip(String key, String tip) {
        JComponent c = null;

        // First check text fields
        for(String s : INPUT_TEXTFIELDS.keySet())
            if(s.equals(key) && INPUT_TEXTFIELDS.get(s) != null)
                c = INPUT_TEXTFIELDS.get(s);

        // Next check spinners
        if(c == null) {
            for(String s : INPUT_SPINNERS.keySet())
                if(s.equals(key) && INPUT_SPINNERS.get(s) != null)
                    c = INPUT_SPINNERS.get(s);
        }

        // Next check checkboxes
        if(c == null) {
            for(String s : INPUT_CHECKBOX.keySet())
                if(s.equals(key) && INPUT_CHECKBOX.get(s) != null)
                    c = INPUT_CHECKBOX.get(s);
        }

        // Next check sliders
        if(c == null) {
            for(String s : INPUT_SLIDER.keySet())
                if(s.equals(key) && INPUT_SLIDER.get(s) != null)
                    c = INPUT_SLIDER.get(s);
        }

        // Next check sliders
        if(c == null) {
            for(String s : INPUT_COMBO.keySet())
                if(s.equals(key) && INPUT_COMBO.get(s) != null)
                    c = INPUT_COMBO.get(s);
        }

        // If the component was found, set the tooltip
        if(c != null)
            c.setToolTipText(tip);
    }

    @Override
    // Handle component interactions like button presses
    public void actionPerformed(ActionEvent actionEvent) {
        Object obj = actionEvent.getSource();
        if(obj == null)
            return;

        // If object is a UI element, handle it here
        if(obj instanceof JButton) {
            String src = ((JButton)obj).getText();
            if(src.equals("START")) {
                // Close frame and dispose of UI elements
                frame.setVisible(false);
                storeSettings();
                frame.dispose();
                START_BUTTON_PRESSED = true;
            } else
            if(src.equals("CANCEL")) {
                // Close frame, dispose of UI elements and set cancel flag to true (stops script)
                frame.setVisible(false);
                frame.dispose();
                CANCEL_BUTTON_PRESSED = true;
            }
        }
    }

    @Override
    // When a textfield gains focus, select all text to make script input easier
    public void focusGained(FocusEvent focusEvent) {
        final Component c = focusEvent.getComponent();
        if (c instanceof JFormattedTextField) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((JFormattedTextField) c).selectAll();
                }
            });
        } else if (c instanceof JTextField) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((JTextField) c).selectAll();
                }
            });
        }
    }

    @Override
    public void focusLost(FocusEvent focusEvent) {
    }

    @Override
    // Set the tooltip of the JSlider when the value changes
    public void stateChanged(ChangeEvent changeEvent) {
        if(changeEvent.getSource() instanceof JSlider) {
            JSlider slider = (JSlider)changeEvent.getSource();
            slider.setToolTipText("Value: " + slider.getValue());
        }
    }

    // This method sets the JFrame icon to one downloaded from the web
    private void getFrameIcon() {
        Image image = null;
        try {
            // Gets the GUI frame icon from a Dreambot forum avatar image
            URL url = new URL(ICON_URL);
            image = ImageIO.read(url);
            image = getScaledImage(image, 256, 256);
        } catch (IOException e) { }

        // If the image is successfully grabbed and resized, set it as the frame icon
        if(image != null) {
            ImageIcon icon = new ImageIcon(image);
            if(icon != null)
                frame.setIconImage(icon.getImage());
        }
    }

    // This method returns a resized image (used to turn avatar into better icon resolution)
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    // This method sets the Frame icon
    public void setIconImage(String URL) {
        ICON_URL = URL;
    }
}
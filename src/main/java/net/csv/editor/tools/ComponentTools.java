package net.csv.editor.tools;

import java.awt.*;

public class ComponentTools {


    public static void centerForm(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        int x = (int) ((screenSize.getWidth() - windowSize.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - windowSize.getHeight()) / 2);
        window.setLocation(x, y);
    }

    public static void setGrayColor(Component component) {
        Color backgroundColor = new Color(50, 50, 50);
        component.setBackground(backgroundColor);
        Color foregroundColor = new Color(175, 175, 175);
        component.setForeground(foregroundColor);
    }

    public static void setGrayColor(Component... components) {
        for (Component component : components) {
            Color backgroundColor = new Color(50, 50, 50);
            component.setBackground(backgroundColor);
            Color foregroundColor = new Color(175, 175, 175);
            component.setForeground(foregroundColor);
        }
    }


}

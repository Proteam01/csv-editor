package net.csv.editor;

import net.csv.editor.tools.ComponentTools;
import net.csv.editor.view.PrincipalFrm;


/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args ) {
        PrincipalFrm principalFrm = new PrincipalFrm();
        principalFrm.setSize(700,500);
        ComponentTools.centerForm(principalFrm);
        principalFrm.setVisible(true);

    }
}

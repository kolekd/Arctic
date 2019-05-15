package cz.danik.arctic.graphics;

import javax.swing.*;

public class ArcticFrame extends JFrame {

    public ArcticFrame() {
        initUI();
    }

    private void initUI() {
        add(new Board());

        setResizable(false);
        pack();

        setTitle("Arctic");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}

package cz.danik.arctic;

import cz.danik.arctic.graphics.ArcticFrame;

import javax.swing.*;
import java.awt.*;

public class ArcticApplication {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new ArcticFrame();
            frame.setVisible(true);
        });
    }

}

package com.company;

import com.company.board.Board;

import javax.swing.*;
import java.awt.*;

public class Arctic extends JFrame {

    public Arctic() {
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

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new Arctic();
            frame.setVisible(true);
        });
    }

}

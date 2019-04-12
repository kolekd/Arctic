package com.company.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;

public class Board extends JPanel implements KeyListener, ActionListener {

    public static final int TILE_SIZE = 32;

    public static final int BOARD_HEIGHT = TILE_SIZE * 20;
    public static final int BOARD_WIDTH = TILE_SIZE * 13;

    private boolean inGame = true;

    private int posX;
    private int posY;

    private Image player;
    private Image wall;

    public Board(){
        addKeyListener(this);

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);

        loadImages();
        initGame();
    }

    private void loadImages() {
        ImageIcon pIcon = new ImageIcon("src/resources/player_tile.png");
        player = pIcon.getImage();

        ImageIcon wIcon = new ImageIcon("src/resources/wall_tile.png");
        wall = wIcon.getImage();
    }

    public void initGame() {
        posX = (BOARD_WIDTH / 2) - (TILE_SIZE / 2);
        posY = BOARD_HEIGHT - (TILE_SIZE * 4);
    }

    public void doDrawing(Graphics graphics) {

        graphics.drawImage(player, posX, posY, this);

        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void paintComponent (Graphics graphics){
        super.paintComponent(graphics);

        doDrawing(graphics);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT && posX > 0) {
            posX -= TILE_SIZE;
        }

        if (key == KeyEvent.VK_RIGHT && posX < BOARD_WIDTH - TILE_SIZE) {
            posX += TILE_SIZE;
        }

        repaint();
    }
}


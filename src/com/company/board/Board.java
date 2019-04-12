package com.company.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;

public class Board extends JPanel implements ActionListener {

    public static final int BOARD_HEIGHT = 400;
    public static final int BOARD_WIDTH = (BOARD_HEIGHT / 4) * 3;

    public static final int TILE_SIZE = BOARD_HEIGHT / 10;

    private int posX;
    private int posY;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Image player;
    private Image wall;

    public Board(){

        addKeyListener(new TAdapter());

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
        posX = BOARD_WIDTH / 2;
        posY = BOARD_HEIGHT - TILE_SIZE;
    }

    public void move() {

        if (leftDirection) {
            posX -= TILE_SIZE;
        }

        if (rightDirection) {
            posX += TILE_SIZE;
        }

        if (upDirection) {
            posY -= TILE_SIZE;
        }

        if (downDirection) {
            posY += TILE_SIZE;
        }
    }

    public void doDrawing(Graphics graphics) {
        graphics.drawImage(player, posX,posY, this);
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void paintComponent (Graphics graphics){
        doDrawing(graphics);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if (key == KeyEvent.VK_RIGHT) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if (key == KeyEvent.VK_UP) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if (key == KeyEvent.VK_DOWN) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }

}

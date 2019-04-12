package com.company.board;

import com.company.logic.RandomDecision;
import com.company.logic.WallLine;
import com.company.logic.WallPlacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Board extends JPanel implements KeyListener, ActionListener {

    private static final int TILE_SIZE = 32;

    private static final int BOARD_HEIGHT = TILE_SIZE * 20;
    private static final int BOARD_WIDTH = TILE_SIZE * 13;
    private static final int MAX_TILES_IN_A_ROW = BOARD_WIDTH/TILE_SIZE;


    private static final int DELAY = 200;

    private static boolean gameRunning;
    private static int TICK_COUNT;

    private int posX;
    private int posY;

    private List<WallLine> listOfWallLists = new ArrayList<>();

    private Image player;
    private Image wall;

    Random random;
    Timer timer;

    public Board() {
        addKeyListener(this);

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);

        loadImages();
        initGame();
    }

    private void initGame() {
        posX = (BOARD_WIDTH / 2) - (TILE_SIZE / 2);
        posY = BOARD_HEIGHT - (TILE_SIZE * 4);

        random = new Random();

        TICK_COUNT = 0;
        gameRunning = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void loadImages() {
        ImageIcon pIcon = new ImageIcon("src/resources/player_tile.png");
        player = pIcon.getImage();

        ImageIcon wIcon = new ImageIcon("src/resources/wall_tile.png");
        wall = wIcon.getImage();
    }

    private void generateWall() {

        List<WallPlacer> wallPlacerList = new ArrayList<>();
        WallLine wallLine = new WallLine(wallPlacerList, 0);

        for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
            if(RandomDecision.get()) {
                wallPlacerList.add(new WallPlacer(true));
            } else {
                wallPlacerList.add(new WallPlacer(false));
            }
        }

        listOfWallLists.add(wallLine);
    }

    private void doDrawing(Graphics graphics) {

        if(gameRunning) {
            graphics.drawImage(player, posX, posY, this);

            for (WallLine wallLine : listOfWallLists) {
                for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
                    if(wallLine.getWalls().get(i).isWillBePlaced()) {
                        graphics.drawImage(wall, (i * TILE_SIZE), wallLine.getPosY(), this);
                    }
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {
            gameOver(graphics);
        }
    }

    public void gameOver(Graphics g) {
        String msg = "Game Over";
        Font font = new Font("Helvetica", Font.BOLD, 18);
        FontMetrics metr = getFontMetrics(font);

        g.setColor(Color.black);
        g.setFont(font);
        g.drawString(msg, (BOARD_WIDTH - metr.stringWidth(msg)) / 2, BOARD_HEIGHT / 2);
    }

    @Override
    public void paintComponent (Graphics graphics){
        super.paintComponent(graphics);

        doDrawing(graphics);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TICK_COUNT++;

        Iterator<WallLine> iterator = listOfWallLists.iterator();
        while (iterator.hasNext()) {
            WallLine currentWallLine = iterator.next();

            for (int i = 0; i < currentWallLine.getWalls().size(); i++) {
                if(posY == currentWallLine.getPosY() + TILE_SIZE &&
                   currentWallLine.getWalls().get(posX / TILE_SIZE).isWillBePlaced()) {
                    
                    gameRunning = false;
                    break;
                }
            }

            if (currentWallLine.getPosY() + TILE_SIZE > BOARD_HEIGHT) {
                iterator.remove();
            } else {
                currentWallLine.setPosY(currentWallLine.getPosY() + TILE_SIZE);
            }
        }

        if(TICK_COUNT == 8) {
            generateWall();
            TICK_COUNT = 0;
        }

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


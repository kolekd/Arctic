package com.company.board;

import com.company.util.RandomDecision;
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

    private static final int INITIAL_SPEED_INCREASE_FREQUENCY = 30;
    private static final int INITIAL_SPEED_INCREASE_VALUE = 10;
    public static final int INITIAL_WALL_GENERATION_FREQUENCY = 24;

    private static int SPEED_INCREASE_VALUE = INITIAL_SPEED_INCREASE_VALUE;
    private static int SPEED_INCREASE_FREQUENCY = INITIAL_SPEED_INCREASE_FREQUENCY;
    private static int WALL_GENERATION_FREQUENCY = INITIAL_WALL_GENERATION_FREQUENCY;
    private static int DELAY = 70;

    private static boolean gameRunning;

    private static int SCORE_COUNT;
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
        setBackground(new Color(238,238,238));
//        setBackground(new Color(0,0,0));

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
                wallPlacerList.add(new WallPlacer(true, (i * TILE_SIZE)));
            } else {
                wallPlacerList.add(new WallPlacer(false, 0));
            }
        }

        listOfWallLists.add(wallLine);
    }

    private void doDrawing(Graphics graphics) {

        if(gameRunning) {
            graphics.drawImage(player, posX, posY, this);

            for (WallLine wallLine : listOfWallLists) {
                for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
                    if(wallLine.getWalls().get(i).isPlaced()) {
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
        String score = "Score: " + SCORE_COUNT;
                Font font = new Font("Helvetica", Font.BOLD, 18);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.black);
        g.setFont(font);
        g.drawString(msg, (BOARD_WIDTH - metrics.stringWidth(msg)) / 2, BOARD_HEIGHT / 2);
        g.drawString(score, (BOARD_WIDTH - metrics.stringWidth(score)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE);
    }

    @Override
    public void paintComponent (Graphics graphics){
        super.paintComponent(graphics);

        doDrawing(graphics);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Score:  " + SCORE_COUNT + "  Delay: " + timer.getDelay() + "  SI Frequency: " +
                            SPEED_INCREASE_FREQUENCY + "  SI Value: " + SPEED_INCREASE_VALUE);
        TICK_COUNT++;
        SCORE_COUNT++;

        Iterator<WallLine> iterator = listOfWallLists.iterator();
        while (iterator.hasNext()) {
            WallLine currentWallLine = iterator.next();

            for (int i = 0; i < currentWallLine.getWalls().size(); i++) {
                if(posY == currentWallLine.getPosY() + TILE_SIZE &&
                   currentWallLine.getWalls().get(posX / TILE_SIZE).isPlaced()) {

                    timer.stop();
                    gameRunning = false;
                    break;
                }
            }

            if (currentWallLine.getPosY() + TILE_SIZE > BOARD_HEIGHT) {
                iterator.remove();
            } else {
                currentWallLine.setPosY(currentWallLine.getPosY() + TILE_SIZE/4);
            }
        }

        if(SCORE_COUNT % WALL_GENERATION_FREQUENCY == 0) {
            generateWall();
        }

//        updateSpeedChangeFrequency();

        if(TICK_COUNT > SPEED_INCREASE_FREQUENCY && timer.getDelay() > 20) {
            timer.setDelay(timer.getDelay() - SPEED_INCREASE_VALUE);
            SPEED_INCREASE_FREQUENCY += INITIAL_SPEED_INCREASE_FREQUENCY;
            TICK_COUNT = 0;
        }

        if(SCORE_COUNT % INITIAL_SPEED_INCREASE_FREQUENCY * 2 == 0 &&
           SCORE_COUNT > INITIAL_SPEED_INCREASE_FREQUENCY * 15 && SPEED_INCREASE_VALUE > 1) {
            SPEED_INCREASE_VALUE--;
        }

        repaint();
    }

    public boolean isThereAWallHere(int coords) {
        for(WallLine wallLine : listOfWallLists) {
            for(WallPlacer wall : wallLine.getWalls()) {
                if (coords == wall.getPosX() &&
                        posY < wallLine.getPosY() + TILE_SIZE &&
                        posY > wallLine.getPosY() - TILE_SIZE){
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT && posX > 0 && !isThereAWallHere(posX - TILE_SIZE)) {
            posX -= TILE_SIZE;
        }

        if (key == KeyEvent.VK_RIGHT && posX < BOARD_WIDTH - TILE_SIZE && !isThereAWallHere(posX + TILE_SIZE)) {
            posX += TILE_SIZE;
        }

        if (key == KeyEvent.VK_DOWN) {
            if(timer.isRunning()) {
                timer.stop();
            } else {
                timer.start();
            }
        }

        repaint();

    }


    @Override
    public void keyReleased(KeyEvent e) {
    }
}


package com.company.board;

import com.company.logic.Logic;
import com.company.model.WallLine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.company.logic.Constants.*;

public class Board extends JPanel implements KeyListener, ActionListener {

    // Kind of ez - val=10, freq=20, wgen=24, eachTickTileGoDownBy=TILE_SIZE / 4

    // Nice & smooth - init_delay=50, min_delay=15, val=10, freq=20, wgen=48, eachTickTileGoDownBy=TILE_SIZE / 8

    private Image player;
    private Image playerBuffed;
    private Image wall;
    private Image powerUp;

    private Font font;
    private Font slimFont;
    private FontMetrics metrics;
    private FontMetrics slimMetrics;

    private Logic logic;

    public Board() {
        addKeyListener(this);

        font = new Font("Helvetica", Font.BOLD, 18);
        slimFont = new Font("Helvetica", Font.PLAIN,16);
        slimMetrics = getFontMetrics(slimFont);
        metrics = getFontMetrics(font);

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        setBackground(new Color(238,238,238));
        setFont(font);

        loadImages();
        logic = new Logic();
    }

    private void launch() {
        removeAll();
        logic.initGame(this);
        revalidate();
        repaint();
    }

    private void loadImages() {
        ImageIcon pIcon = new ImageIcon("src/resources/player_tile.png");
        player = pIcon.getImage();

        ImageIcon pbIcon = new ImageIcon("src/resources/player_tile_buffed.png");
        playerBuffed = pbIcon.getImage();

        ImageIcon wIcon = new ImageIcon("src/resources/wall_tile.png");
        wall = wIcon.getImage();

        ImageIcon pwIcon = new ImageIcon("src/resources/power_up.png");
        powerUp = pwIcon.getImage();
    }

    private void doDrawing(Graphics graphics) {
        if(logic.gameRunning) {
            if(logic.player_buffed) {
                graphics.drawImage(playerBuffed, logic.player_posX, logic.player_posY, this);
            } else {
                graphics.drawImage(player, logic.player_posX, logic.player_posY, this);
            }

            for (WallLine wallLine : logic.listOfWallLines) {
                for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
                    if(wallLine.getWalls().size() < 2 && wallLine.getWalls().get(0).isPowerUp()) {
                        if(wallLine.getWalls().get(0).isPlaced()) {
                            graphics.drawImage(powerUp, (wallLine.getWalls().get(0).getPosX()), wallLine.getPosY(), this);
                            break;
                        }
                    } else if(wallLine.getWalls().get(i).isPlaced()) {
                        graphics.drawImage(wall, (i * TILE_SIZE), wallLine.getPosY(), this);
                    }
                }
            }

            graphics.drawString(String.valueOf(logic.SCORE_COUNT), (TILE_SIZE / 4), (BOARD_HEIGHT) - 6);


            Toolkit.getDefaultToolkit().sync();

        } else if (logic.gameJustLaunched) {
            gameStart(graphics);
        } else {
            gameOver(graphics);
        }
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over";
        String score = "Score: " + logic.SCORE_COUNT;
        String restartMsg = "Press <space> to restart.";

        drawWords(g, msg, font, (BOARD_WIDTH - metrics.stringWidth(msg)) / 2, BOARD_HEIGHT / 2);
        drawWords(g, score, font, (BOARD_WIDTH - metrics.stringWidth(score)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE);
        drawWords(g, restartMsg, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(restartMsg)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE * 2);
    }

    private void gameStart(Graphics g) {
        String title = "Arctic";
        String startMsg = "Press <space> to start.";

        g.setColor(Color.black);
        drawWords(g, title, font, (BOARD_WIDTH - metrics.stringWidth(title)) / 2, (BOARD_HEIGHT / 2));
        drawWords(g, startMsg, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(startMsg)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE * 2);
    }

    private void drawWords(Graphics g, String words, Font font, int x, int y) {
        g.setFont(font);
        g.drawString(words, x, y);
    }

    @Override
    public void paintComponent (Graphics graphics){
        super.paintComponent(graphics);

        doDrawing(graphics);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(logic.debugReport());

        logic.tickAction();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT && logic.player_posX > 0 && logic.noWallThere(logic.player_posX - TILE_SIZE)) {
            logic.player_posX -= TILE_SIZE;
        }

        if (key == KeyEvent.VK_RIGHT && logic.player_posX < BOARD_WIDTH - TILE_SIZE && logic.noWallThere(logic.player_posX + TILE_SIZE)) {
            logic.player_posX += TILE_SIZE;
        }

        if (logic.gameJustLaunched  && key == KeyEvent.VK_SPACE || !logic.gameRunning && key == KeyEvent.VK_SPACE) {
            launch();
        }

//        if (key == KeyEvent.VK_DOWN) {
//            if(logic.timer.isRunning()) {
//                logic.timer.stop();
//            } else {
//                logic.timer.start();
//            }
//        }
//
//          TIME STOP BUTTON

        repaint();

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}


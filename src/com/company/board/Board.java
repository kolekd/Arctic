package com.company.board;

import com.company.logic.Logic;
import com.company.model.*;

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
    private Image playerBreakerBuff;
    private Image playerShooterBuff;
    private Image wall;
    private Image movingWall;
    private Image powerUpBreaker;
    private Image powerUpShooter;
    private Image projectile;

    private Font font;
    private Font slimFont;
    private Font hitFont;
    private FontMetrics metrics;
    private FontMetrics slimMetrics;

    private Logic logic;

    public Board() {
        addKeyListener(this);

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        setBackground(new Color(238,238,238));
        setFont(font);

        font = new Font("Helvetica", Font.BOLD, 18);
        slimFont = new Font("Helvetica", Font.PLAIN,16);
        hitFont = new Font("Helvetica", Font.BOLD, 13);
        slimMetrics = getFontMetrics(slimFont);
        metrics = getFontMetrics(font);

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
        ImageIcon playerIcon = new ImageIcon("src/resources/player_tile.png");
        player = playerIcon.getImage();

        ImageIcon playerBreakerBuffIcon = new ImageIcon("src/resources/player_tile_buffed_breaker.png");
        playerBreakerBuff = playerBreakerBuffIcon.getImage();

        ImageIcon playerShooterBuffIcon = new ImageIcon("src/resources/player_tile_buffed_shooter.png");
        playerShooterBuff = playerShooterBuffIcon.getImage();

        ImageIcon wallIcon = new ImageIcon("src/resources/wall_tile.png");
        wall = wallIcon.getImage();

        ImageIcon movingWallIcon = new ImageIcon("src/resources/moving_wall_tile.png");
        movingWall = movingWallIcon.getImage();

        ImageIcon breakerPowerUpIcon = new ImageIcon("src/resources/power_up_breaker.png");
        powerUpBreaker = breakerPowerUpIcon.getImage();

        ImageIcon shooterPowerUpIcon = new ImageIcon("src/resources/power_up_shooter.png");
        powerUpShooter = shooterPowerUpIcon.getImage();

        ImageIcon projectileIcon = new ImageIcon("src/resources/projectile.png");
        projectile = projectileIcon.getImage();
    }

    private void drawPlayer(Graphics g) {
        if(logic.playerBuff.equals("breaker")) {
            g.drawImage(playerBreakerBuff, logic.playerPosX, logic.playerPosY, this);
        } else if(logic.playerBuff.equals("shooter")) {
            g.drawImage(playerShooterBuff, logic.playerPosX, logic.playerPosY, this);
        } else {
            g.drawImage(player, logic.playerPosX, logic.playerPosY, this);
        }
    }

    private void doDrawing(Graphics graphics) {
        if(logic.gameRunning) {
            drawPlayer(graphics);

            for (Projectile currentProjectile : logic.projectileList) {
                graphics.drawImage(projectile, currentProjectile.getPosX(), currentProjectile.getPosY(), this);
            }

            for (WallLine wallLine : logic.listOfWallLists) {
                for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
                    Wall currentWall = wallLine.getWalls().get(0);
                    if(wallLine.getWalls().size() < 2) {
                        if(currentWall instanceof PowerUp) {
                            if(currentWall.isPlaced()) {
                                if(((PowerUp)currentWall).isShooter()) {
                                    graphics.drawImage(powerUpShooter, (currentWall.getPosX()), wallLine.getPosY(), this);
                                } else {
                                    graphics.drawImage(powerUpBreaker, (currentWall.getPosX()), wallLine.getPosY(), this);
                                }
                                break;
                            }
                        }
                    } else {
                        Wall wallOnI = wallLine.getWalls().get(i);
                        if(wallOnI.isPlaced()) {
                            if (wallOnI instanceof MovingWall) {
                                graphics.drawImage(movingWall, (wallOnI.getPosX()), wallLine.getPosY(), this);
                            } else {
                                graphics.drawImage(wall, (i * TILE_SIZE), wallLine.getPosY(), this);
                            }
                        } else if(wallOnI.getJustDestroyed().equals("breaker")) {
                            drawWords(graphics, "1000", hitFont, wallOnI.getPosX() + (TILE_SIZE /4) - 7, wallLine.getPosY() + (TILE_SIZE /2));
                        } else if(wallOnI.getJustDestroyed().equals("shooter")) {
                            drawWords(graphics, "200", hitFont, wallOnI.getPosX() + (TILE_SIZE /4) - 3, wallLine.getPosY() + (TILE_SIZE /2));
                        }
                    }
                }
            }

            drawWords(graphics, String.valueOf(logic.SCORE_COUNT), font, (TILE_SIZE / 4), (BOARD_HEIGHT) - 6);

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
        String restartMsg = "Press <R> to restart.";

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
        logic.tickAction();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        //  Left: move left if there's no wall or if at board border
        if (key == KeyEvent.VK_LEFT && logic.playerPosX > 0 && logic.noWallThere(logic.playerPosX - TILE_SIZE)) {
            logic.playerPosX -= TILE_SIZE;
        }

        //  Right: move right if there's no wall or if at board border
        if (key == KeyEvent.VK_RIGHT && logic.playerPosX < BOARD_WIDTH - TILE_SIZE && logic.noWallThere(logic.playerPosX + TILE_SIZE)) {
            logic.playerPosX += TILE_SIZE;
        }

        //  SPACE: launch the game at app start; launch projectiles when buffed; DEBUG MODE - launch projectiles
        if (key == KeyEvent.VK_SPACE) {
            if (logic.gameJustLaunched) {
                launch();
            } else if(logic.playerBuff.equals("shooter") || DEBUG_MODE) {
                logic.projectilesWillBeLaunched = true;
            }
        }

        // R: restart
        if (key == KeyEvent.VK_R && !logic.gameRunning) {
            launch();
        }

        //  Down: DEBUG MODE - time freeze
        if (key == KeyEvent.VK_DOWN && DEBUG_MODE) {
            if(logic.timer.isRunning()) {
                logic.timer.stop();
            } else {
                logic.timer.start();
            }
        }

        //  B: DEBUG MODE - gain Breaker power-up
        if (key == KeyEvent.VK_B && DEBUG_MODE) {
            logic.playerBuff = "breaker";
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}


package cz.danik.arctic.graphics;

import cz.danik.arctic.logic.Logic;
import cz.danik.arctic.model.Tile;
import cz.danik.arctic.model.wall.Wall;
import cz.danik.arctic.model.PowerUp;
import cz.danik.arctic.values.Globals;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import static cz.danik.arctic.values.Constants.*;

public class Board extends JPanel implements KeyListener, ActionListener {

    // Kind of ez - val=10, freq=20, wgen=24, eachTickTileGoDownBy=TILE_SIZE / 4

    // Nice & smooth - init_delay=50, min_delay=15, val=10, freq=20, wgen=48, eachTickTileGoDownBy=TILE_SIZE / 8

    private Image player;
    private Image playerBreakerBuff;
    private Image playerShooterBuff;
    private Image wall;
    private Image powerUpBreaker;
    private Image powerUpShooter;
    private Image projectile;

    private Font font;
    private Font slimFont;
    private Font hitFont;
    private FontMetrics metrics;
    private FontMetrics slimMetrics;

    private Logic logic;

    Globals globals;


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

        logic = new Logic();

        loadImages();
    }

    private void launch() {
        removeAll();
        logic.initGame(this);
        revalidate();
        repaint();
    }

    private void loadImages() {
        ImageIcon playerIcon = new ImageIcon("src/main/resources/img/player_tile.png");
        player = playerIcon.getImage();

        ImageIcon playerBreakerBuffIcon = new ImageIcon("src/main/resources/img/player_tile_buffed_breaker.png");
        playerBreakerBuff = playerBreakerBuffIcon.getImage();

        ImageIcon playerShooterBuffIcon = new ImageIcon("src/main/resources/img/player_tile_buffed_shooter.png");
        playerShooterBuff = playerShooterBuffIcon.getImage();

        ImageIcon wallIcon = new ImageIcon("src/main/resources/img/wall_tile.png");
        wall = wallIcon.getImage();

        ImageIcon breakerPowerUpIcon = new ImageIcon("src/main/resources/img/power_up_breaker.png");
        powerUpBreaker = breakerPowerUpIcon.getImage();

        ImageIcon shooterPowerUpIcon = new ImageIcon("src/main/resources/img/power_up_shooter.png");
        powerUpShooter = shooterPowerUpIcon.getImage();

        ImageIcon projectileIcon = new ImageIcon("src/main/resources/img/projectile.png");
        projectile = projectileIcon.getImage();
    }

    private void drawPlayer(Graphics g) {
        if(logic.player.getBuff().equals(BREAKER)) {
            g.drawImage(playerBreakerBuff, logic.player.getPosX(), logic.player.getPosY(), this);
        } else if(logic.player.getBuff().equals(SHOOTER)) {
            g.drawImage(playerShooterBuff, logic.player.getPosX(), logic.player.getPosY(), this);
        } else {
            g.drawImage(player, logic.player.getPosX(), logic.player.getPosY(), this);
        }
    }

    private void doDrawing(Graphics graphics) {
        if(Logic.gameRunning) {
            drawPlayer(graphics);

            for (Tile currentProjectile : logic.projectileManager) {
                if (currentProjectile.isPlaced()) {
                    graphics.drawImage(projectile, currentProjectile.getPosX(), currentProjectile.getPosY(), this);
                }
            }

            for (List<Tile> tileList : logic.tileManager) {
                for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
                    Tile currentTile = tileList.get(i);
                    if (currentTile.isPlaced()) {
                        if (currentTile instanceof PowerUp) {
                            String powerUpName = ((PowerUp) currentTile).getName();
                            if (powerUpName.equals(SHOOTER)) {
                                graphics.drawImage(powerUpShooter, (currentTile.getPosX()), currentTile.getPosY(), this);
                            } else if (powerUpName.equals(BREAKER)){
                                graphics.drawImage(powerUpBreaker, (currentTile.getPosX()), currentTile.getPosY(), this);
                            }
                        } else if (currentTile instanceof Wall) {
                            graphics.drawImage(wall, currentTile.getPosX(), currentTile.getPosY(), this);
                        }
                    } else if (!(currentTile instanceof PowerUp)){
                        if (((Wall)currentTile).getJustDestroyedBy().equals(BREAKER)) {
                            drawWords(graphics, String.valueOf(BREAKER_SCORE_VALUE), hitFont, currentTile.getPosX() + (TILE_SIZE / 4) - 7, currentTile.getPosY() + (TILE_SIZE / 2));
                        } else if (((Wall)currentTile).getJustDestroyedBy().equals(SHOOTER)) {
                            drawWords(graphics, String.valueOf(SHOOTER_SCORE_VALUE), hitFont, currentTile.getPosX() + (TILE_SIZE / 4) - 3, currentTile.getPosY() + (TILE_SIZE / 2));
                        }

                    }

                }
            }

            drawWords(graphics, String.valueOf(Globals.SCORE_COUNT), font, (TILE_SIZE / 4), (BOARD_HEIGHT) - 6);

            Toolkit.getDefaultToolkit().sync();

        } else if (logic.gameJustLaunched) {
            gameStart(graphics);
        } else {
            gameOver(graphics);
        }
    }

    private void gameOver(Graphics g) {
        String gameOver = "Game Over";
        String score = "Score: " + Globals.SCORE_COUNT;
        String restartMsg = "Press <" + RESET_BUTTON + "> to restart.";

        drawWords(g, gameOver, font, (BOARD_WIDTH - metrics.stringWidth(gameOver)) / 2, BOARD_HEIGHT / 2);
        drawWords(g, score, font, (BOARD_WIDTH - metrics.stringWidth(score)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE);
        drawWords(g, restartMsg, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(restartMsg)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE * 2);
    }

    private void gameStart(Graphics g) {
        String title = GAME_TITLE;
        String startMsg = "Press <" + START_BUTTON + "> to start.";

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

        //  Left: move left if there's no wall or if at graphics border
        if (key == KeyEvent.VK_LEFT && logic.player.getPosX() > 0 && logic.noWallThere(logic.player.getPosX() - TILE_SIZE)) {
            logic.player.setPosX(logic.player.getPosX() -TILE_SIZE);
        }

        //  Right: move right if there's no wall or if at graphics border
        if (key == KeyEvent.VK_RIGHT && logic.player.getPosX() < BOARD_WIDTH - TILE_SIZE && logic.noWallThere(logic.player.getPosX() + TILE_SIZE)) {
            logic.player.setPosX(logic.player.getPosX() + TILE_SIZE);
        }

        //  SPACE: launch the game at app start; launch projectiles when buffed; DEBUG MODE - launch projectiles
        if (key == KeyEvent.VK_SPACE) {
            if (logic.gameJustLaunched) {
                launch();
            } else if(logic.player.getBuff().equals(SHOOTER) || DEBUG_MODE) {
                logic.player.setLaunchProjectiles(true);
            }
        }

        // R: restart
        if (key == KeyEvent.VK_R) {
            if(!DEBUG_MODE) {
                if(Logic.gameRunning) {
                    launch();
                }
            } else {
                Logic.timer.stop();
                Logic.gameRunning = false;
                launch();
            }
        }

        //  Down: DEBUG MODE - time freeze
        if (key == KeyEvent.VK_DOWN && DEBUG_MODE) {
            if(Logic.timer.isRunning()) {
                Logic.timer.stop();
            } else {
                Logic.timer.start();
            }
        }

        //  B: DEBUG MODE - gain Breaker power-up
        if (key == KeyEvent.VK_B && DEBUG_MODE) {
            logic.player.setBuff(BREAKER);
        }

        //  S: DEBUG MODE - gain Shooter power-up
        if (key == KeyEvent.VK_S && DEBUG_MODE) {
            logic.player.setBuff(SHOOTER);
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

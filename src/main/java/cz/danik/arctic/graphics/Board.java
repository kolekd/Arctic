package cz.danik.arctic.graphics;

import cz.danik.arctic.logic.GameFlow;
import cz.danik.arctic.logic.Logic;
import cz.danik.arctic.model.Tile;
import cz.danik.arctic.model.wall.Wall;
import cz.danik.arctic.model.PowerUp;
import cz.danik.arctic.values.Globals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.ImageObserver;
import java.util.List;

import static cz.danik.arctic.logic.Logic.GO_TO_MENU;
import static cz.danik.arctic.logic.Logic.gameRunning;
import static cz.danik.arctic.values.Constants.*;
import static cz.danik.arctic.values.Globals.DEBUG_MODE;

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
    private Image cursor1;
    private Image cursor2;

    private Font font;
    private Font slimFont;
    private Font hitFont;
    private FontMetrics metrics;
    private FontMetrics slimMetrics;

    private Logic logic;

    private String currentWindow;
    private int cursorAt;

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

        DEBUG_MODE = DEBUG_MODE_DEFAULT_VALUE;
        cursorAt = INITIAL_CURSOR_POSITION;
        currentWindow = MENU_WINDOW;

        loadImages();
    }

    private void launch() {
        currentWindow = GAME_WINDOW;
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

        ImageIcon cursor1Icon  = new ImageIcon("src/main/resources/img/cursor_right.png");
        cursor1 = cursor1Icon.getImage();

        ImageIcon cursor2Icon  = new ImageIcon("src/main/resources/img/cursor_left.png");
        cursor2 = cursor2Icon.getImage();
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
        if(gameRunning) {
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

        } else if (GO_TO_MENU) {
            gameMenu(graphics);
        } else {
            gameOver(graphics);
        }
    }

    private void gameOver(Graphics g) {
        String gameOver = "Game Over";
        String score = "Score: " + Globals.SCORE_COUNT;
        String restartMsg = "Press <" + RESET_BUTTON_TEXT + "> to restart.";

        drawWords(g, gameOver, font, (BOARD_WIDTH - metrics.stringWidth(gameOver)) / 2, BOARD_HEIGHT / 2);
        drawWords(g, score, font, (BOARD_WIDTH - metrics.stringWidth(score)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE);
        drawWords(g, restartMsg, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(restartMsg)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE * 2);
    }

//    private void gameStart(Graphics g) {
//        String title = GAME_TITLE_TEXT;
//        String startMsg = "Press <" + START_BUTTON + "> to start.";
//
//        g.setColor(Color.black);
//        drawWords(g, title, font, (BOARD_WIDTH - metrics.stringWidth(title)) / 2, (BOARD_HEIGHT / 2));
//        drawWords(g, startMsg, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(startMsg)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE * 2);
//    }

    private void gameMenu(Graphics g) {
        String title = GAME_TITLE_TEXT;
        String singlePlayer = SINGLE_PLAYER_TEXT;
        String multiPlayer = MULTI_PLAYER_TEXT;
        String debugMode = DEBUG_MODE_TEXT + DEBUG_MODE;

        drawCursors(g, cursorAt, this);

        g.setColor(Color.black);
        drawWords(g, title, font, (BOARD_WIDTH - metrics.stringWidth(title)) / 2, BOARD_HEIGHT / 3);
        drawWords(g, singlePlayer, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(singlePlayer)) / 2, SINGLE_PLAYER_TEXT_POSITION);
        drawWords(g, multiPlayer, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(multiPlayer)) / 2, MULTI_PLAYER_TEXT_POSITION);
        drawWords(g, debugMode, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(debugMode)) / 2, DEBUG_MODE_TEXT_POSITION);
    }

    private void drawCursors(Graphics graphics, int posY, ImageObserver observer) {
        graphics.drawImage(cursor1, CURSOR_1_X_POSITION, posY, observer);
        graphics.drawImage(cursor2, CURSOR_2_X_POSITION, posY, observer);
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

        if(currentWindow.equals(GAME_WINDOW)) {

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
                if (GO_TO_MENU) {
                    launch();
                } else if(logic.player.getBuff().equals(SHOOTER) || DEBUG_MODE) {
                    logic.player.setLaunchProjectiles(true);
                }
            }

            // R: DEBUG MODE - restart
            if (key == KeyEvent.VK_R && DEBUG_MODE) {
                GameFlow.timer.stop();
                gameRunning = false;
                launch();
            }

            //  Down: DEBUG MODE - time freeze
            if (key == KeyEvent.VK_DOWN && DEBUG_MODE) {
                if(GameFlow.timer.isRunning()) {
                    GameFlow.timer.stop();
                } else {
                    GameFlow.timer.start();
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

        } else if (currentWindow.equals(MENU_WINDOW)) {

            //  ENTER: enter current choice
            if (key == KeyEvent.VK_ENTER) {
                if(cursorAt == SINGLE_PLAYER_CURSOR_POSITION) {
                    launch();
                } else if (cursorAt == MULTI_PLAYER_CURSOR_POSITION) {
                    System.out.println("Multiplayer to be implemented.");
                } else if (cursorAt == DEBUG_MODE_CURSOR_POSITION) {
                    DEBUG_MODE = !DEBUG_MODE;
                }
            }

            //  UP: move up in menu
            if (key == KeyEvent.VK_UP) {
                if(cursorAt > INITIAL_CURSOR_POSITION) {
                    cursorAt -= TILE_SIZE;
                }
            }

            //  DOWN: move down in menu
            if (key == KeyEvent.VK_DOWN) {
                if(cursorAt < MENU_CURSOR_LIMIT) {
                    cursorAt += TILE_SIZE;
                }
            }

        } else if (currentWindow.equals(GAME_OVER_WINDOW)) {


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


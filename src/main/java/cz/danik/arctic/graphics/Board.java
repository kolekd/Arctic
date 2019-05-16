package cz.danik.arctic.graphics;

import cz.danik.arctic.logic.GameFlow;
import cz.danik.arctic.logic.Logic;
import cz.danik.arctic.model.PowerUp;
import cz.danik.arctic.model.Tile;
import cz.danik.arctic.model.wall.Wall;
import cz.danik.arctic.values.Globals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.ImageObserver;
import java.util.List;

import static cz.danik.arctic.logic.GameFlow.PRIMARY_TIMER;
import static cz.danik.arctic.logic.GameFlow.SECONDARY_TIMER;
import static cz.danik.arctic.values.Constants.*;
import static cz.danik.arctic.values.Globals.*;

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

    private Font titleFont;
    private Font font;
    private Font slimFont;
    private Font hitFont;

    private FontMetrics metrics;
    private FontMetrics slimMetrics;
    private FontMetrics titleMetrics;

    private Logic logic;

    private int cursorAt;

    public Board() {
        addKeyListener(this);

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        setBackground(new Color(238,238,238));
        setFont(font);

        titleFont = new Font("Helvetica", Font.BOLD, 40);
        font = new Font("Helvetica", Font.BOLD, 18);
        slimFont = new Font("Helvetica", Font.PLAIN,16);
        hitFont = new Font("Helvetica", Font.BOLD, 13);

        titleMetrics = getFontMetrics(titleFont);
        slimMetrics = getFontMetrics(slimFont);
        metrics = getFontMetrics(font);

        logic = new Logic();
        cursorAt = LINE_1_CURSOR_POSITION;

        DEBUG_MODE = DEBUG_MODE_DEFAULT_VALUE;
        CURRENT_WINDOW = MAIN_MENU_WINDOW;

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
        switch (CURRENT_WINDOW) {
            case GAME_WINDOW:
                gameRunning(graphics);
                break;
            case MAIN_MENU_WINDOW:
                gameMenu(graphics);
                break;
            case GAME_OVER_WINDOW:
                gameOver(graphics);
                break;
            case GAME_PAUSED_WINDOW:
                gamePause(graphics);
                break;
            case MULTIPLAYER_MENU_WINDOW:
                gameMenuMultiplayer(graphics);
                break;
        }
    }

    private void gameRunning(Graphics graphics) {
        drawPlayer(graphics);

        for (Tile currentProjectile : logic.projectileManager) {
            if (currentProjectile.isPlaced()) {
                graphics.drawImage(projectile, currentProjectile.getPosX(), currentProjectile.getPosY(), this);
            }
        }

        for (List<Tile> tileList : logic.tileManager) {
            Tile currentTile;
            for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
                currentTile = tileList.get(i);
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
    }

    private void gameOver(Graphics g) {
        String gameOver = "Game Over";
        String score = "Score: " + Globals.SCORE_COUNT;
        String exit = GO_TO_MENU_KEY_TEXT + "   -->   exit to menu";
        String restartMsg = RESET_KEY_TEXT + "   -->   restart.";

        drawWords(g, gameOver, font, (BOARD_WIDTH - metrics.stringWidth(gameOver)) / 2, BOARD_HEIGHT / 3);
        drawWords(g, score, font, (BOARD_WIDTH - metrics.stringWidth(score)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE);
        drawWords(g, exit, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(exit)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE * 2);
        drawWords(g, restartMsg, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(restartMsg)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE * 3);
    }

    private void gamePause(Graphics g) {
        String title = "Paused";
        String exit = GO_TO_MENU_KEY_TEXT + "   -->   exit to menu";
        String resume = RESUME_KEY_TEXT + "   -->   resume";

        g.setColor(Color.black);
        drawWords(g, title, font, (BOARD_WIDTH - metrics.stringWidth(title)) / 2, (BOARD_HEIGHT / 3));
        drawWords(g, exit, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(exit)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE);
        drawWords(g, resume, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(resume)) / 2, (BOARD_HEIGHT / 2) + TILE_SIZE * 2);
    }

    private void gameMenu(Graphics g) {
        String title = GAME_TITLE_TEXT;
        String singlePlayer = SINGLE_PLAYER_TEXT;
        String multiPlayer = MULTI_PLAYER_TEXT;
        String debugMode = DEBUG_MODE_TEXT + DEBUG_MODE;

        drawCursors(g, cursorAt, this);

        g.setColor(new Color(0, 11, 196));
        drawWords(g, title, titleFont, (BOARD_WIDTH - titleMetrics.stringWidth(title)) / 2, TITLE_TEXT_POSITION);

        g.setColor(Color.black);
        drawWords(g, singlePlayer, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(singlePlayer)) / 2, LINE_1_TEXT_POSITION);
        drawWords(g, multiPlayer, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(multiPlayer)) / 2, LINE_2_TEXT_POSITION);
        drawWords(g, debugMode, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(debugMode)) / 2, LINE_3_TEXT_POSITION);
    }

    private void gameMenuMultiplayer(Graphics g) {
        //TODO

        String title = GAME_TITLE_TEXT;
        String subtitle = MULTI_PLAYER_TEXT;
        String start = START_GAME_TEXT;
        String addPlayer = ADD_PLAYER_TEXT;
        String removePlayer = REMOVE_PLAYER_TEXT;

        drawCursors(g, cursorAt, this);

        g.setColor(new Color(0, 11, 196));
        drawWords(g, title, titleFont, (BOARD_WIDTH - titleMetrics.stringWidth(title)) / 2, TITLE_TEXT_POSITION);

        g.setColor(Color.black);
        drawWords(g, subtitle, slimFont, (BOARD_WIDTH - slimMetrics.stringWidth(subtitle)) / 2, SUBTITLE_TEXT_POSITION);

        drawWords(g, "line 1", slimFont, (BOARD_WIDTH - slimMetrics.stringWidth("line 1")) / 2, LINE_1_TEXT_POSITION);
        drawWords(g, "line 2", slimFont, (BOARD_WIDTH - slimMetrics.stringWidth("line 2")) / 2, LINE_2_TEXT_POSITION);
        drawWords(g, "line 3", slimFont, (BOARD_WIDTH - slimMetrics.stringWidth("line 3")) / 2, LINE_3_TEXT_POSITION);
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
        if(e.getSource().equals(PRIMARY_TIMER)) {
            logic.tickAction();
        } else if (e.getSource().equals(SECONDARY_TIMER)) {
            logic.tickActionSecondary();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (CURRENT_WINDOW) {
            case GAME_WINDOW:

                //  Left: move left if there's no wall or if at graphics border
                if (key == KeyEvent.VK_LEFT && logic.player.getPosX() > 0 && logic.noWallThere(logic.player.getPosX() - TILE_SIZE)) {
                    logic.player.setPosX(logic.player.getPosX() - TILE_SIZE);
                }

                //  Right: move right if there's no wall or if at graphics border
                if (key == KeyEvent.VK_RIGHT && logic.player.getPosX() < BOARD_WIDTH - TILE_SIZE && logic.noWallThere(logic.player.getPosX() + TILE_SIZE)) {
                    logic.player.setPosX(logic.player.getPosX() + TILE_SIZE);
                }

                //  SPACE: launch the game at app start; launch projectiles when buffed; DEBUG MODE - launch projectiles
                if (key == KeyEvent.VK_SPACE) {
                    if (logic.player.getBuff().equals(SHOOTER) || DEBUG_MODE) {
                        logic.player.setLaunchProjectiles(true);
                    }
                }

                // R: DEBUG MODE - restart
                if (key == KeyEvent.VK_R && DEBUG_MODE) {
                    GameFlow.stopTimers();
                    launch();
                }

                //  Down: DEBUG MODE - time freeze
                if (key == KeyEvent.VK_DOWN && DEBUG_MODE) {
                    if (PRIMARY_TIMER.isRunning()) {
                        GameFlow.stopTimers();
                    } else {
                        GameFlow.startTimers();
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

                //  ESC: pause menu
                if (key == KeyEvent.VK_ESCAPE) {
                    GameFlow.stopTimers();
                    CURRENT_WINDOW = GAME_PAUSED_WINDOW;
                }

                break;
            case GAME_PAUSED_WINDOW:
                //  ESC: resume game
                if(key == KeyEvent.VK_ESCAPE) {
                    GameFlow.startTimers();
                    CURRENT_WINDOW = GAME_WINDOW;
                }

                //  Q: exit to menu
                if(key == KeyEvent.VK_Q) {
                    CURRENT_WINDOW = MAIN_MENU_WINDOW;
                }

                break;
            case GAME_OVER_WINDOW:
                //  R: restart
                if (key == KeyEvent.VK_R) {
                    CURRENT_WINDOW = GAME_WINDOW;
                    launch();
                }

                //  Q: exit to menu
                if(key == KeyEvent.VK_Q) {
                    CURRENT_WINDOW = MAIN_MENU_WINDOW;
                }

                break;
            case MAIN_MENU_WINDOW:

                //  ENTER: enter current choice
                if (key == KeyEvent.VK_ENTER) {
                    if (cursorAt == LINE_1_CURSOR_POSITION) {
                        CURRENT_WINDOW = GAME_WINDOW;
                        launch();
                    } else if (cursorAt == LINE_2_CURSOR_POSITION) {
                        //TODO

                        CURRENT_WINDOW = MULTIPLAYER_MENU_WINDOW;
                        cursorAt = LINE_1_CURSOR_POSITION;
                        System.out.println("Multiplayer to be implemented.");
                    } else if (cursorAt == LINE_3_CURSOR_POSITION) {
                        DEBUG_MODE = !DEBUG_MODE;
                    }
                }

                //  UP: move up in menu
                if (key == KeyEvent.VK_UP) {
                    if (cursorAt > LINE_1_CURSOR_POSITION) {
                        cursorAt -= TILE_SIZE;
                    }
                }

                //  DOWN: move down in menu
                if (key == KeyEvent.VK_DOWN) {
                    if (cursorAt < MENU_CURSOR_LIMIT) {
                        cursorAt += TILE_SIZE;
                    }
                }

                break;
            case MULTIPLAYER_MENU_WINDOW:

                //TODO

                //  UP: move up in menu
                if (key == KeyEvent.VK_UP) {
                    if (cursorAt > LINE_1_CURSOR_POSITION) {
                        cursorAt -= TILE_SIZE;
                    }
                }

                //  DOWN: move down in menu
                if (key == KeyEvent.VK_DOWN) {
                    if (cursorAt < MENU_CURSOR_LIMIT) {
                        cursorAt += TILE_SIZE;
                    }
                }

                break;
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


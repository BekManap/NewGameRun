import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

 class BrawlGame extends JPanel implements ActionListener {
    private Timer timer;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Weapon> weapons;
    private int wave;
    private int playerHealth;
    private Boss boss;
    private boolean isGameOver;

    // Хранение состояния клавиш
    private boolean[] keys;

    public BrawlGame() {
        player = new Player(400, 300, 50, 50);
        enemies = new ArrayList<>();
        weapons = new ArrayList<>();
        wave = 1;
        playerHealth = 100;
        isGameOver = false;
        keys = new boolean[256]; // Массив для хранения состояния клавиш

        timer = new Timer(16, this);
        timer.start();
        spawnEnemies();
        spawnWeapons();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keys[e.getKeyCode()] = true; // Устанавливаем состояние клавиши в true при нажатии
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keys[e.getKeyCode()] = false; // Устанавливаем состояние клавиши в false при отпускании
            }
        });
    }

    private void spawnEnemies() {
        for (int i = 0; i < wave * 2; i++) {
            enemies.add(new Enemy(new Random().nextInt(800), new Random().nextInt(600), 40, 40));
        }
        if (wave % 5 == 0) {
            boss = new Boss(200, 200, 80, 80);
        }
    }

    private void spawnWeapons() {
        weapons.add(new Weapon(new Random().nextInt(800), new Random().nextInt(600), 30, 10));
    }

    private void pickUpWeapon() {
        for (Weapon weapon : weapons) {
            if (player.x < weapon.x + weapon.width && player.x + player.width > weapon.x &&
                    player.y < weapon.y + weapon.height && player.y + player.height > weapon.y) {
                weapons.remove(weapon);
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        player.draw(g);
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (Weapon weapon : weapons) {
            weapon.draw(g);
        }
        if (boss != null) {
            boss.draw(g);
        }

        g.setColor(Color.WHITE);
        g.drawString("Здоровье: " + playerHealth, 10, 20);
        g.drawString("Волна: " + wave, 10, 40);
        if (isGameOver) {
            g.drawString("Игра окончена!", 400, 300);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver) return;

        // Движение игрока
        if (keys[KeyEvent.VK_UP]) player.move(0, -5);
        if (keys[KeyEvent.VK_DOWN]) player.move(0, 5);
        if (keys[KeyEvent.VK_LEFT]) player.move(-5, 0);
        if (keys[KeyEvent.VK_RIGHT]) player.move(5, 0);

        for (Enemy enemy : enemies) {
            enemy.moveTowards(player);
            if (enemy.intersects(player)) {
                playerHealth -= 1;
            }
        }

        if (playerHealth <= 0) {
            isGameOver = true;
        }

        if (boss != null && boss.intersects(player)) {
            playerHealth -= 2; // Босс наносит больше урона
        }

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brawl Game");
        BrawlGame game = new BrawlGame();
        frame.add(game);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Player {
    int x, y, width, height;

    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(int dx, int dy) {
        x = Math.max(0, Math.min(800 - width, x + dx));
        y = Math.max(0, Math.min(600 - height, y + dy));
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }
}

class Enemy {
    int x, y, width, height;

    public Enemy(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void moveTowards(Player player) {
        if (x < player.x) x += 1;
        if (x > player.x) x -= 1;
        if (y < player.y) y += 1;
        if (y > player.y) y -= 1;
    }

    public boolean intersects(Player player) {
        return x < player.x + player.width && x + width > player.x && y < player.y + player.height && y + height > player.y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }
}

class Weapon {
    int x, y, width, height;

    public Weapon(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }
}

class Boss {
    int x, y, width, height;

    public Boss(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects(Player player) {
        return x < player.x + player.width && x + width > player.x && y < player.y + player.height && y + height > player.y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(x, y, width, height);
    }
}

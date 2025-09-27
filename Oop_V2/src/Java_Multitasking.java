import org.w3c.dom.Text;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList; //สองตัวนี้แอบเพิ่มมา
import java.util.List; //
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Java_Multitasking {
    public static void main(String[] args){
        //ส่วนของหน้าต่าง input จำนวนอุกกาบาตของผู้ใช้
        /*JFrame input = new JFrame("Input");
        input.setSize(500, 150);
        input.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20)); // จัดกลาง
        JTextField t1 = new JTextField();
        t1.setPreferredSize(new Dimension(input.getWidth()/2, 30));
        JButton b1 = new JButton("Submit");
        b1.setPreferredSize(new Dimension(input.getWidth()/4, 35));
        final int[] ip = {0};
        b1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = t1.getText().trim();
                try {
                    ip[0] = Integer.parseInt(s);
                }catch (NumberFormatException ex){
                    t1.setText("Please enter a number");
                    Timer timer = new Timer(1500, evt -> {
                        t1.setText("");
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });
        input.add(t1);
        input.add(b1);
        input.setLocationRelativeTo(null);
        input.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        input.setVisible(true);*/

        int n = 100;
        BackgroundPanel panel = new BackgroundPanel(n);
        JFrame frame = new JFrame("โปรแกรมอุกาบาต");
        frame.setLocation(300,100);
        frame.setSize(750,650);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
// ... โค้ดส่วน import และ class Java_Multitasking เหมือนเดิม

class BackgroundPanel extends JPanel {
    private final BufferedImage bg;
    private final BufferedImage[] meteorImgs;
    private final Meteor[] meteors;
    private final List<Explosion> explosions = new CopyOnWriteArrayList<>();

    public BackgroundPanel(int num) {
        try {
            bg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/background2.png")));
            meteorImgs = new BufferedImage[] {
                    ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/meteorite0.png"))),
                    ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/meteorite1.png"))),
                    ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/meteorite2.png")))
            };
        } catch (Exception e) {
            throw new RuntimeException("โหลดรูปไม่ได้!", e);
        }

        setPreferredSize(new Dimension(bg.getWidth(), bg.getHeight()));

        // สร้าง meteors แบบสุ่มตำแหน่งและความเร็ว (แก้ไม่ให้ซ้อนกัน)
        meteors = new Meteor[num];
        for (int i = 0; i < num; i++) {
            boolean overlap;
            double x = 0, y = 0;
            int attempts = 0;
            do {
                overlap = false;
                x = Math.random() * (bg.getWidth() - 50);
                y = Math.random() * (bg.getHeight() - 50);

                for (int j = 0; j < i; j++) {
                    Meteor m = meteors[j];
                    Rectangle r1 = new Rectangle((int)x, (int)y, 50, 50);
                    Rectangle r2 = new Rectangle((int)m.x, (int)m.y, m.w, m.h);
                    if (r1.intersects(r2)) {
                        overlap = true;
                        break;
                    }
                }
                attempts++;
                if (attempts > 50) break; // ป้องกัน loop ไม่รู้จบ
            } while (overlap);

            // ความเร็วช้า ๆ แบบเฉื่อย ๆ
            double dx = (Math.random() * 1.2 - 0.6); // -0.6 ถึง 0.6
            double dy = (Math.random() * 1.2 - 0.6); // -0.6 ถึง 0.6
            if (dx == 0) dx = 0.3;
            if (dy == 0) dy = 0.3;

            meteors[i] = new Meteor(x, y, 50, 50, dx, dy);
        }

        // Timer สำหรับอัปเดตตำแหน่งและ repaint
        new Thread(() -> {
            while (true) {
                for (Meteor m : meteors) {
                    m.move(getWidth(), getHeight());
                }
                checkCollisions();

                // อัปเดต explosions
                for (Explosion ex : explosions) ex.update();
                explosions.removeIf(e -> e.done);

                // เรียก repaint ใน EDT
                SwingUtilities.invokeLater(this::repaint);

                try {
                    Thread.sleep(35); // กำหนด fps
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break; // ออก loop ถ้า thread ถูก interrupt
                }
            }
        }).start();
    }

    private void checkCollisions() {
        ArrayList<Integer> toRemove = new ArrayList<>(); // เก็บดัชนีที่ต้องลบ

        for (int i = 0; i < meteors.length; i++) {
            for (int j = i + 1; j < meteors.length; j++) {
                Meteor m1 = meteors[i];
                Meteor m2 = meteors[j];
                Rectangle r1 = new Rectangle((int)m1.x, (int)m1.y, m1.w, m1.h);
                Rectangle r2 = new Rectangle((int)m2.x, (int)m2.y, m2.w, m2.h);

                if (r1.intersects(r2)) {
                    // เปรียบเทียบความเร็วรวม (dx² + dy²)
                    double speed1 = Math.sqrt(m1.dx * m1.dx + m1.dy * m1.dy);
                    double speed2 = Math.sqrt(m2.dx * m2.dx + m2.dy * m2.dy);

                    // ลบอุกกาบาตที่ช้ากว่า
                    if (speed1 < speed2 && !toRemove.contains(i)) {
                        toRemove.add(i);
                    } else if (speed2 < speed1 && !toRemove.contains(j)) {
                        toRemove.add(j);
                    }
                }
            }
        }

        // สร้าง array ใหม่โดยไม่รวม meteors ที่ต้องลบ
        Meteor[] newMeteors = new Meteor[meteors.length - toRemove.size()];
        int idx = 0;
        for (int i = 0; i < meteors.length; i++) {
            if (!toRemove.contains(i)) {
                newMeteors[idx++] = meteors[i];
            }
        }
        System.arraycopy(newMeteors, 0, meteors, 0, newMeteors.length);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.drawImage(bg, 0, 0, null);

        for (int i = 0; i < meteors.length; i++) {
            Meteor m = meteors[i];
            g.drawImage(meteorImgs[i % meteorImgs.length], (int)m.x, (int)m.y, m.w, m.h, null);

        }
    }

    static class Meteor {
        double x, y;
        int w, h;
        double dx, dy;

        public Meteor(double x, double y, int w, int h, double dx, double dy) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.dx = dx;
            this.dy = dy;
        }

        public void move(int panelWidth, int panelHeight) {

            x += dx;
            y += dy;

            // ชนขอบ → เด้ง + เร่ง 20%
            if (x < 0 || x + w > panelWidth) {
                dx = -dx;
                x = Math.max(0, Math.min(x, panelWidth - w));
            }
            if (y < 0 || y + h > panelHeight) {
                dy = -dy;
                y = Math.max(0, Math.min(y, panelHeight - h));
            }
        }
    }

    static class Explosion {
        int x, y;
        int radius = 0;
        int maxRadius = 50;
        boolean done = false;

        public Explosion(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void update() {
            radius += 5;
            if (radius >= maxRadius) done = true;
        }

        public void draw(Graphics g) {
            if (!done) {
                g.setColor(new Color(255, 150, 0, 150));
                g.fillOval(x - radius / 2, y - radius / 2, radius, radius);
            }
        }
    }
}

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Java_Multitasking {
    public static void main(String[] args){
        //ส่วนของหน้าต่าง input จำนวนอุกกาบาตของผู้ใช้
        JFrame input = new JFrame("Input");
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
                if (ip[0] > 0){
                    int n = ip[0];
                    input.dispose();
                    BackgroundPanel panel = new BackgroundPanel(n);
                    JFrame frame = new JFrame("โปรแกรมอุกกาบาต");
                    frame.setLocation(300,100);
                    frame.setSize(750,650);               // คุณยังใช้ขนาดนี้ได้ตามเดิม
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.add(panel);
                    frame.setVisible(true);
                }
            }
        });
        input.add(t1);
        input.add(b1);
        input.setLocationRelativeTo(null);
        input.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        input.setVisible(true);

    }
}

class BackgroundPanel extends JPanel {
    private final BufferedImage bg;
    private final BufferedImage[] meteorImgs;

    // ใช้อาร์เรย์ตามเดิม แต่ “ไม่ลบ” — แค่ซ่อน (visible=false)
    private final Meteor[] meteors;

    // เอฟเฟกต์ระเบิด
    private final List<Explosion> explosions = new CopyOnWriteArrayList<>();

    // ทำ seed ตำแหน่งครั้งเดียว หลัง panel มีขนาดจริง
    private volatile boolean seeded = false;

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

        // สร้างเมเทียร์ด้วยตำแหน่ง placeholder ก่อน (ยังไม่สุ่ม x,y จริง)
        meteors = new Meteor[num];
        for (int i = 0; i < num; i++) {
            double dx = (Math.random() * 1.2 - 0.6);
            double dy = (Math.random() * 1.2 - 0.6);
            if (dx == 0) {
                dx = 0.3;
            }
            if (dy == 0) {
                dy = 0.3;
            }

            meteors[i] = new Meteor(0, 0, 50, 50, dx, dy);
            meteors[i].skinIndex = (int)(Math.random() * meteorImgs.length); // ผูกสกินให้คงที่ต่อตัว
        }

        // ลูปอัปเดต (ไว้เป็น Thread แยกได้ แต่จะรอจน seed เสร็จ)
        new Thread(() -> {
            while (true) {
                if (!seeded) { // ยังไม่ seed → รอเฟรมหน้า
                    SwingUtilities.invokeLater(this::repaint);
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        break;
                    }
                    continue;
                }

                // เคลื่อนที่
                for (int i = 0; i < meteors.length; i++) {
                    Meteor m = meteors[i];
                    if (m != null) {
                        m.move(getWidth(), getHeight());
                    }
                }

                // ตรวจชนกันแล้ว “ซ่อน” 1 ตัวแบบสุ่ม + วางระเบิด (ไม่ลบออกจากอาร์เรย์)
                checkCollisions();

                // เอฟเฟกต์ระเบิด
                for (int i = 0; i < explosions.size(); i++) {
                    explosions.get(i).update();
                }
                explosions.removeIf(e -> e.done);

                // วาดใหม่
                SwingUtilities.invokeLater(this::repaint);

                try {
                    Thread.sleep(7);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "meteor-loop").start();
    }

    // ตรวจชนกัน: ถ้าชน → สุ่มซ่อนถาวร 1 ลูก + จุดระเบิด (ไม่ยุ่งกับอาร์เรย์)
    private void checkCollisions() {
        java.util.Random rnd = new java.util.Random();

        for (int i = 0; i < meteors.length; i++) {
            Meteor m1 = meteors[i];
            if (m1 == null || !m1.visible) {
                continue;
            }

            for (int j = i + 1; j < meteors.length; j++) {
                Meteor m2 = meteors[j];
                if (m2 == null || !m2.visible) continue;

                Rectangle r1 = new Rectangle((int)(m1.x + m1.w*0.2), (int)(m1.y + m1.h*0.2), (int)(m1.w*0.6), (int)(m1.h*0.6));
                Rectangle r2 = new Rectangle((int)(m2.x + m2.w*0.2), (int)(m2.y + m2.h*0.2), (int)(m2.w*0.6), (int)(m2.h*0.6));


                if (r1.intersects(r2)) {
                    // เอฟเฟกต์ระเบิด (ตรงกลางของคู่นั้น)
                    int cx = (int)((m1.x + m2.x) / 2.0);
                    int cy = (int)((m1.y + m2.y) / 2.0);
                    explosions.add(new Explosion(cx, cy));

                    // สุ่มให้หายไป 1 ลูก (ซ่อนถาวร)
                    if (rnd.nextBoolean()) {
                        m1.visible = false;
                    } else {
                        m2.visible = false;
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // SEED ตำแหน่งครั้งแรก (กันซ้อน + กันพิกัดซ้ำ)
        if (!seeded) {
            int pw = getWidth();
            int ph = getHeight();

            if (pw > 0 && ph > 0) {
                java.util.HashSet<String> used = new java.util.HashSet<>();

                for (int i = 0; i < meteors.length; i++) {
                    Meteor m = meteors[i];

                    int tries = 0;
                    while (true) {
                        int rangeW = Math.max(1, pw - m.w);
                        int rangeH = Math.max(1, ph - m.h);

                        int ix = (int) (Math.random() * rangeW);
                        int iy = (int) (Math.random() * rangeH);

                        // ไม่ให้พิกัดเดียวกันเป๊ะ
                        String key = ix + "," + iy;
                        if (used.contains(key)) {
                            if (tries++ > 300) break;
                            continue;
                        }

                        // ไม่ให้ซ้อนกัน (กรอบสี่เหลี่ยม)
                        boolean overlap = false;
                        Rectangle r = new Rectangle(ix, iy, m.w, m.h);
                        for (int j = 0; j < i; j++) {
                            Meteor o = meteors[j];
                            Rectangle ro = new Rectangle((int) o.x, (int) o.y, o.w, o.h);
                            if (r.intersects(ro)) {
                                overlap = true;
                                break;
                            }
                        }
                        if (overlap) {
                            if (tries++ > 300) break;
                            continue;
                        }

                        m.x = ix;
                        m.y = iy;
                        used.add(key);
                        break;
                    }

                    // กันกรณีครบ tries แล้วยังชน/ซ้ำ → ขยับ 1 พิกเซล
                    String finalKey = ((int)m.x) + "," + ((int)m.y);
                    if (used.contains(finalKey)) {
                        m.x = Math.min(Math.max(0, m.x + 1), pw - m.w);
                        m.y = Math.min(Math.max(0, m.y + 1), ph - m.h);
                    } else {
                        used.add(finalKey);
                    }
                }
                seeded = true;
            }
        }

        // วาดพื้นหลัง (สเกลให้พอดีกับ panel ตอนนี้)
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);

        // วาดอุกกาบาต: เฉพาะตัวที่ยัง visible
        for (int i = 0; i < meteors.length; i++) {
            Meteor m = meteors[i];
            if (m != null && m.visible) {
                g.drawImage(meteorImgs[m.skinIndex], (int)m.x, (int)m.y, m.w, m.h, null);
            }
        }

        // วาดเอฟเฟกต์ระเบิด
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).draw(g);
        }
    }

    // ----------------- คลาสย่อย -----------------
    static class Meteor {
        double x, y;
        int w, h;
        double dx, dy;

        boolean visible = true;    // ซ่อน/แสดง (ถ้า false ถือว่าหายไป)
        int skinIndex = 0;         // ใช้เลือกรูปจาก meteorImgs

        public Meteor(double x, double y, int w, int h, double dx, double dy) {
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.dx = dx; this.dy = dy;
        }

        public void move(int panelWidth, int panelHeight) {
            if (panelWidth <= 0 || panelHeight <= 0) return;
            // เดินปกติ
            x += dx;
            y += dy;

            // พารามิเตอร์ควบคุม
            final double BOUNCE_MULT = 1.12; // เร่งนิดหน่อยตอนเด้ง
            final double MAX_SPEED   = 4.0;  // ลิมิตไม่ให้เร็วเกินไป

            // เด้งขอบ + เร่งนิดหน่อย
            if (x < 0 || x + w > panelWidth) {  //ขอบซ้ายขวา
                dx = -dx * BOUNCE_MULT;
                x = Math.max(0, Math.min(x, panelWidth - w));
            }
            if (y < 0 || y + h > panelHeight) { //ขอบบนล่าง
                dy = -dy * BOUNCE_MULT;
                y = Math.max(0, Math.min(y, panelHeight - h));
            }

            // คุมความเร็วสูงสุด (แบบ vector)
            double spd = Math.hypot(dx, dy);
            if (spd > MAX_SPEED) {
                double s = MAX_SPEED / spd;
                dx *= s; dy *= s;
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
            if (radius >= maxRadius) {
                done = true;
            }
        }

        public void draw(Graphics g) {
            if (!done) {
                g.setColor(new Color(255, 150, 0, 150));
                g.fillOval(x - radius / 2, y - radius / 2, radius, radius);
            }
        }
    }
}

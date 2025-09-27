import org.w3c.dom.Text;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

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

        int a = 0;
        BackgroundPanel panel = new BackgroundPanel(a);
        JFrame frame = new JFrame();
        frame.setLocation(300,100);
        frame.setSize(750,650);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
class BackgroundPanel  extends JPanel{
    private final BufferedImage bg;
    private BufferedImage[] meteorImgs;
    int x = 0, y = 0;
    // อุกกาบาตรับเข้ามา
    private final Meteor[] meteors;
    public  BackgroundPanel(int num){
        try {
            bg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/background2.png")));
            meteorImgs = new BufferedImage[] {
                    ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/meteorite0.png"))),
                    ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/meteorite1.png"))),
                    ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/meteorite2.png")))
            };
        }catch (Exception e){
            throw new RuntimeException("โหลดรูปไม่ได้!", e);
        }
        setPreferredSize(new Dimension(bg.getWidth(), bg.getHeight()));
        meteors = new Meteor[num];
    }
    //เอาไว้เพิ่มรูป
    @Override
    public  void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(bg,0,0,null);
        for (int i = 0; i < meteorImgs.length; i++) {
            g.drawImage(meteorImgs[i],50,50,50,50,null);
            g.drawImage(meteorImgs[i],100,150,50,50,null);
            g.drawImage(meteorImgs[i],200,200,50,50,null);
        }

    }
    //ข้อมูลของอุกกาบาต
    public static class Meteor {
        int x , y ;
        int w , h ;
        public Meteor(int x, int y, int w, int h){
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}

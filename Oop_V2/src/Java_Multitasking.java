import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Java_Multitasking {
    public static void main(String[] args){
        BackgroundPanel panel = new BackgroundPanel();
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
    private final BufferedImage meteor0;
    private final BufferedImage meteor1;
    private final BufferedImage meteor2;
    int x = 0, y = 0;
    public  BackgroundPanel(){
        try {
            bg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/background2.png")));
            meteor0 = ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/meteorite0.png")));
            meteor1 = ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/meteorite1.png")));
            meteor2 = ImageIO.read(Objects.requireNonNull(getClass().getResource("/image/meteorite2.png")));
        }catch (Exception e){
            throw new RuntimeException("โหลดรูปไม่ได้!", e);
        }
        setPreferredSize(new Dimension(bg.getWidth(), bg.getHeight()));
    }
    //เอาไว้เพิ่มรูป
    @Override
    public  void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(bg,0,0,null);
        g.drawImage(meteor0,50,50,50,50,null);
        g.drawImage(meteor1,100,150,50,50,null);
        g.drawImage(meteor2,200,200,50,50,null);
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

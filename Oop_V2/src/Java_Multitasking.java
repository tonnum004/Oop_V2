import  javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Java_Multitasking {
    public static void main(String[] args){
        JFrame frame = new JFrame();
        File imgFile = new  File(System.getProperty("user.dir"), "/background2.png");
        File m0  = new  File(System.getProperty("user.dir"), "/meteorite0.png");
        File m1  = new  File(System.getProperty("user.dir"), "/meteorite1.png");
        File m2  = new  File(System.getProperty("user.dir"), "/meteorite2.png");
        frame.setLocation(300,100);
        frame.setSize(750,650);

        frame.setContentPane(new background(imgFile,m0));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
class background extends JPanel {
    private final BufferedImage bg;
    private final BufferedImage meteorFile0;
    int x =  0;
    int y = 0;
    public  background(File file,File meteorFile) {
        try {
            bg = ImageIO.read(file);
            meteorFile0 = ImageIO.read(meteorFile);
        }catch (Exception e){
            throw new RuntimeException("ไม่พบรูปหรืออ่านรูปไม่ได้ : " + file, e);
        }
        setPreferredSize(new Dimension(bg.getWidth(), bg.getHeight()));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this);
        g.drawImage(meteorFile0, x, y,50,50, this);
    }
}
class MyThread extends Thread{
    public void run(){
        while(true){
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
            }
        }
    }
}
import javax.swing.*;
import java.awt.*;

/**
 * Created by rodrigo on 05/03/2017.
 */
public class MainWindow extends JFrame {

    public MainWindow() {

        initUI();
    }

    private void initUI() {

        add(new Surface());

        setTitle("Star");
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void drawNFP()

}

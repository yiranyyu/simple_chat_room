import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    static final int WIDTH = 300;
    static final int HEIGHT = 150;
    private JFrame frame;
    private JTextField usernameField = new JTextField(15);
    private JTextField passwordField = new JTextField(15);
    private GridBagConstraints constraints = new GridBagConstraints();

    public static void main(String[] args) {
        new LoginPanel();
    }

    private void initConstraints() {
        setLayout(new GridBagLayout());
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 3;
        constraints.weighty = 4;
    }

    private void initLayout() {
        add(new JLabel("登录 / 注册"), constraints, 0, 0, 4, 1);
        add(new JLabel("用户名"), constraints, 0, 1, 1, 1);
        add(new JLabel("密 码"), constraints, 0, 2, 1, 1);
        add(new JButton("登录"), constraints, 0, 3, 1, 1);
        add(new JButton("注册"), constraints, 2, 3, 1, 1);
        add(usernameField, constraints, 2, 1, 1, 1);
        add(passwordField, constraints, 2, 2, 1, 1);
    }

    private void initFrame() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this, BorderLayout.WEST);
        frame.setSize(WIDTH, HEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setLocation((screenWidth - WIDTH) / 2, (screenHeight - HEIGHT) / 2);
    }

    public LoginPanel() {
        initConstraints();
        initLayout();
        initFrame();
        addListeners();
        show();
    }

    public void add(Component c, GridBagConstraints constraints, int x, int y, int w, int h) {// 此方法用来添加控件到容器中
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
        add(c, constraints);
    }

    public void show() {
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void addListeners() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                LoginPanel.this.setEnabled(false);
            }
        });
    }

    public String getUsername() {
        return "";
    }

    public String getPassword() {
        return "";
    }
}
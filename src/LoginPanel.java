import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    static final int WIDTH = 300;
    static final int HEIGHT = 150;
    private JFrame frame;
    private JTextField usernameField = new JTextField(15);
    private JTextField passwordField = new JTextField(15);
    private JButton loginButton = new JButton("登录");
    private JButton signupButton = new JButton("注册");
    private GridBagConstraints constraints = new GridBagConstraints();
    private String username;
    private String password;

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
        add(new JLabel("登录 / 注册"), constraints, 0, 0, 1, 1);
        add(new JLabel("用户名"), constraints, 0, 1, 1, 1);
        add(new JLabel("密 码"), constraints, 0, 2, 1, 1);
        add(loginButton, constraints, 0, 3, 1, 1);
        add(signupButton, constraints, 2, 3, 1, 1);
        add(usernameField, constraints, 2, 1, 1, 1);
        add(passwordField, constraints, 2, 2, 1, 1);
    }

    private void initFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this, BorderLayout.CENTER);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2);
    }

    public LoginPanel() {
        initConstraints();
        initLayout();
        initFrame();
        addListeners();
        show();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon backgroundIcon = new ImageIcon("static/login_background.jpg");
        backgroundIcon.setImage(backgroundIcon.getImage().getScaledInstance(this.getWidth(), this.getHeight(),
                Image.SCALE_AREA_AVERAGING));
        Image img = backgroundIcon.getImage();
        g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    public void add(Component c, GridBagConstraints constraints, int x, int y, int w, int h) {
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
        // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // frame.addWindowListener(new WindowAdapter() {
        // @Override
        // public void windowClosed(WindowEvent e) {
        // LoginPanel.this.setEnabled(false);
        // }
        // });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (login()) {
                    LoginPanel.this.setEnabled(false);
                }
            }
        });
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (signup()) {
                    LoginPanel.this.setEnabled(false);
                }
            }
        });
    }

    private boolean login() {
        username = usernameField.getText().strip();
        password = passwordField.getText().strip();
        try {
            API.login(username, password);
            return true;
        } catch (UserNotExistsException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (PasswordErrorException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        return false;
    }

    private boolean signup() {
        username = usernameField.getText().strip();
        password = passwordField.getText().strip();
        try {
            API.signup(username, password);
            return true;
        } catch (UserAlreadyExistsException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (DatabaseInsertFailException e) {
        	JOptionPane.showMessageDialog(this, e.getMessage());
        }
        return false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
import javax.swing.*;
import java.awt.*;

/**
 * Log in interface of client
 *
 * @author 余天予
 */
public class LoginPanel extends JPanel {
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 300;
    private static final int HEIGHT = 150;
    private JFrame frame;
    private JTextField usernameField = new JTextField(15);
    private JTextField passwordField = new JPasswordField(15);
    private JButton loginButton = new JButton("登录");
    private JButton signUpButton = new JButton("注册");
    private GridBagConstraints constraints = new GridBagConstraints();
    private String username;
    private String password;

    /**
     * Construct one login panel
     */
    LoginPanel() {
        initConstraints();
        initLayout();
        initFrame();
        addListeners();
        startShow();
    }

    /**
     * Start of the login panel, this is just for test usage
     *
     * @param args should be ignored by default
     */
    public static void main(String[] args) {
        new LoginPanel();
    }

    /**
     * Init the layout constraints
     */
    private void initConstraints() {
        setLayout(new GridBagLayout());
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 3;
        constraints.weighty = 4;
    }

    /**
     * Init layout of components in panel
     */
    private void initLayout() {
        System.out.println(WIDTH + ", " + HEIGHT);
        add(new JLabel("登录 / 注册"), constraints, 0, 0, 1, 1);
        add(new JLabel("用户名"), constraints, 1, 0, 1, 1);
        add(usernameField, constraints, 1, 1, 1, 2);
        add(new JLabel("密 码"), constraints, 2, 0, 1, 1);
        add(passwordField, constraints, 2, 1, 1, 2);
        add(loginButton, constraints, 3, 0, 1, 1);
        add(signUpButton, constraints, 3, 2, 1, 1);
    }

    /**
     * Init the main frame of login panel
     */
    private void initFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this, BorderLayout.CENTER);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2);
    }

    /**
     * Override the {@link JPanel#paintComponent(Graphics)} to add background image
     *
     * @param g default argument
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon backgroundIcon = new ImageIcon("static/login_background.jpg");
        backgroundIcon.setImage(backgroundIcon.getImage().getScaledInstance(this.getWidth(), this.getHeight(),
                Image.SCALE_AREA_AVERAGING));
        Image img = backgroundIcon.getImage();
        g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    /**
     * Add component to panel
     *  @param c           the component to be added
     * @param constraints layout constraints
     * @param y           y index
     * @param x           x index
     * @param h           height
     * @param w           width
     */
    private void add(Component c, GridBagConstraints constraints, int y, int x, int h, int w) {
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
        add(c, constraints);
    }

    /**
     * Start to show the panel
     */
    void startShow() {
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * Add event listeners
     */
    private void addListeners() {
        // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // frame.addWindowListener(new WindowAdapter() {
        // @Override
        // public void windowClosed(WindowEvent e) {
        // LoginPanel.this.setEnabled(false);
        // }
        // });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        passwordField.addActionListener(arg0 -> {
            if (login()) {
                frame.dispose();
                LoginPanel.this.setEnabled(false);
            }
        });
        loginButton.addActionListener(e -> {
            if (login()) {
                frame.dispose();
                LoginPanel.this.setEnabled(false);
            }
        });
        signUpButton.addActionListener(e -> {
            if (signUp()) {
                frame.dispose();
                LoginPanel.this.setEnabled(false);
            }
        });
    }

    /**
     * Perform login operation with user input <code>username</code> and <code>password</code>
     *
     * @return true if succeed otherwise return false
     */
    private boolean login() {
        username = usernameField.getText().trim();
        password = passwordField.getText().trim();
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

    /**
     * Perform sign up operation with user input <code>username</code> and <code>password</code>
     *
     * @return true if succeed otherwise return false
     */
    private boolean signUp() {
        username = usernameField.getText().trim();
        password = passwordField.getText().trim();
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

    /**
     * Return the logged in username or the registered username
     *
     * @return username
     */
    String getUsername() {
        return username;
    }

    /**
     * Return password of logged in user or the registered user
     *
     * @return password
     */
    String getPassword() {
        return password;
    }
}
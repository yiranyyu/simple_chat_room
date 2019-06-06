import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Log in interface of client
 */
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
        add(new JLabel("登录 / 注册"), constraints, 0, 0, 1, 1);
        add(new JLabel("用户名"), constraints, 0, 1, 1, 1);
        add(new JLabel("密 码"), constraints, 0, 2, 1, 1);
        add(loginButton, constraints, 0, 3, 1, 1);
        add(signupButton, constraints, 2, 3, 1, 1);
        add(usernameField, constraints, 2, 1, 1, 1);
        add(passwordField, constraints, 2, 2, 1, 1);
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
     *
     * @param c           the component to be added
     * @param constraints layout constraints
     * @param x           x index
     * @param y           y index
     * @param w           width
     * @param h           height
     */
    private void add(Component c, GridBagConstraints constraints, int x, int y, int w, int h) {
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

    /**
     * Perform login operation with user input <code>username</code> and <code>password</code>
     *
     * @return true if succeed otherwise return false
     */
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

    /**
     * Perform sign up operation with user input <code>username</code> and <code>password</code>
     *
     * @return true if succeed otherwise return false
     */
    private boolean signUp() {
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
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static net.sf.json.JSONArray.toCollection;

/**
 * Client entry class To start client: run Client#main
 *
 * @author 王潜
 */
public class Client {
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JTextField txtId;
    private JButton btn_send;
    private JButton btnAdd;
    private JPanel AddUserPanel;
    private JPanel userInputPanel;
    private JPanel chatListPanel;
    private JScrollPane dialogueScroll;
    private JScrollPane chatListScroll;

    private UserTab activeTab;
    private boolean isConnected = false;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private MessageListenerThread messageListenerThread;// 负责接收消息的线程
    private Map<String, User> onlineUsers = new HashMap<>();// 所有在线用户

    private Base64.Encoder encoder;
    private Base64.Decoder decoder;

    private String user;

    /**
     * The tabs on classes
     */
    private class UserTab extends JPanel {
        String user;
        ArrayList<Message> messageList;
        String text;
        JLabel lastMessage;

        UserTab(String user) {
            super(new GridLayout(3, 1));
            JLabel userLabel = new JLabel(user);
            userLabel.setFont(userLabel.getFont().deriveFont(26.5f));
            super.add(userLabel);
            messageList = new ArrayList<>();
            lastMessage = new JLabel();
            super.add(lastMessage);
            super.add(new JSeparator());
            this.user = user;
            text = "";

            addListeners();
        }

        private void addListeners() {
            super.addMouseListener(new MouseListener() {
                @Override
                public synchronized void mouseClicked(MouseEvent e) {
                    setActiveTab(UserTab.this);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
        }
    }

    /**
     * Start with login
     */
    private Client() {
        encoder = Base64.getUrlEncoder();
        decoder = Base64.getUrlDecoder();
        startWithLogin();
    }

    /**
     * Used to debug
     *
     * @param debug should not work when false
     */
    private Client(boolean debug) {
        if (debug) {
            user = "aaaa";
            initClientUI();
            UserTab tab1 = new UserTab("gkd");
            UserTab tab2 = new UserTab("kkp");
            chatListPanel.add(tab1);
            chatListPanel.add(tab2);
            messageRecieved(new Message("gkd", "aaaa", "2222rrr", "2133124e2"));
            messageRecieved(new Message("kkp", "aaaa", "we32rrr", "ert4eesrijuhwe"));
            messageRecieved(new Message("kkp", "yghu", "we32rrr", "eriopkjiot4e"));
            messageSent(new Message("aaaa", "kkp", "we32rrr", "ert4eesjklnrwe"));
            messageSent(new Message("aaaa", "kkp", "we32rrr", "ert4eesklmnrwe"));
            txtId.setText("dddd");
            addUser();
            addListeners();
        } else throw new RuntimeException();
    }

    /**
     * Program start entry
     *
     * @param args will be ignored
     */
    public static void main(String[] args) {
        new Client();
    }

    private void startWithLogin() {
        LoginPanel loginPanel = new LoginPanel();
        loginPanel.startShow();
        while (loginPanel.isEnabled()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // do nothing but wait
            }
        }
        System.out.println("Logged in!");
        user = loginPanel.getUsername();
        if(!connectServer(6666, "localhost", user)){
            return;
        }
        initClientUI();
        addListeners();
    }

    /**
     * Init client program UI
     */
    private void initClientUI() {
        initDialogueScroll();
        initChatListScroll();
        initUserInputPanel();
        initAddUserPanel();
        initClientFrame();
    }

    /**
     * Init the configuration of status panel which shows the network configuration
     * and user information.
     */
    private void initAddUserPanel() {
        txtId = new JTextField();
        btnAdd = new JButton("添加");
        AddUserPanel = new JPanel(new GridLayout(1, 3));
        AddUserPanel.add(new JLabel("用户id"));
        AddUserPanel.add(txtId);
        AddUserPanel.add(btnAdd);
        AddUserPanel.setBorder(new TitledBorder("添加聊天对象"));
    }

    /**
     * Init the dialogue panel which displays the user messages
     */
    private void initDialogueScroll() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setForeground(Color.blue);
        dialogueScroll = new JScrollPane(textArea);
        dialogueScroll.setBorder(new TitledBorder("消息显示区"));
    }

    /**
     * Init chat list which displays the currently available users to communicate
     * with
     */
    private void initChatListScroll() {
        //userListModel = new DefaultListModel<>();
        //userList = new JList<>(userListModel);
        chatListPanel = new JPanel(new GridLayout(256, 1));
        chatListScroll = new JScrollPane(chatListPanel);
        chatListScroll.setBorder(new TitledBorder("会话列表"));
    }

    /**
     * Init input panel for user the write message and send it
     */
    private void initUserInputPanel() {
        textField = new JTextField();
        btn_send = new JButton("发送");
        textField.setEnabled(false);
        btn_send.setEnabled(false);
        userInputPanel = new JPanel(new BorderLayout());
        userInputPanel.add(textField, "Center");
        userInputPanel.add(btn_send, "East");
        userInputPanel.setBorder(new TitledBorder("写消息"));
    }

    /**
     * Draw the client main Frame
     */
    private void initClientFrame() {
        double scale = 0.7;
        int width = (int) (screenSize.width * scale);
        int height = (int) (screenSize.height * scale);
        int verticalSplit = (int) (height * 0.8);
        int horizontalSplit = (int) (width * 0.2);

        JSplitPane eastSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dialogueScroll, userInputPanel);
        eastSplit.setDividerLocation(verticalSplit);
        JSplitPane westSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chatListScroll, AddUserPanel);
        westSplit.setDividerLocation(verticalSplit);
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westSplit, eastSplit);
        centerSplit.setDividerLocation(horizontalSplit);

        frame = new JFrame(user + "的聊天");
        frame.setLayout(new BorderLayout());
        frame.add(centerSplit, "Center");
        frame.setSize(width, height);
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
        frame.setVisible(true);
    }


    /**
     * Add listeners to the window and components in ti.
     */
    private void addListeners() {
        // 写消息的文本框中按回车键时事件
        textField.addActionListener(arg0 -> sendMessage());
        // 单击发送按钮时事件
        btn_send.addActionListener(e -> sendMessage());
        //
        btnAdd.addActionListener(e -> addUser());
        txtId.addActionListener(e -> addUser());

        // 关闭窗口时事件
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isConnected) {
                    closeConnection();
                }
                System.exit(0);
            }
        });
    }

    private void addUser() {
        String userName;
        try {
            userName = txtId.getText().trim();
            if (!API.containsName(userName)) {
                JOptionPane.showMessageDialog(frame, "目标用户不存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (Component component : chatListPanel.getComponents()) {
                UserTab tabUser = (UserTab) component;
                if (tabUser.user.equals(userName)) {
                    setActiveTab(tabUser);
                    txtId.setText("");
                    return;
                }
            }
            List<Message> msgList;
            msgList = (List<Message>) toCollection(API.pullMessageList(user, userName), MessageDB.class).stream().map(o -> new Message((MessageDB) o)).collect(Collectors.toList());
            msgList.sort((Message o1, Message o2) -> {
                Timestamp ts1 = Timestamp.valueOf(o1.getTime());
                Timestamp ts2 = Timestamp.valueOf(o2.getTime());
                if (ts1.before(ts2))
                    return -1;
                else if (ts1.after(ts2))
                    return 1;
                else
                    return 0;
            });
            UserTab newTab = new UserTab(userName);
            chatListPanel.add(newTab);
            setActiveTab(newTab);

            for (Message m : msgList) {
                newTab.messageList.add(m);
                drawMessage(m);
            }
            txtId.setText("");
            frame.validate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void drawMessage(Message message) {
        textArea.append(message.getSender() + " " + message.getTime() + "\n");
        textArea.append(message.getText() + "\n");
        textArea.append("\n");
    }

    private synchronized void setActiveTab(UserTab tab) {
        if (activeTab != null) activeTab.text = textField.getText();
        textArea.setText("");
        textField.setEnabled(true);
        btn_send.setEnabled(true);
        activeTab = tab;
        textField.setText(tab.text);
        frame.setTitle(user + " 与 " + tab.user + " 的聊天");
        for (Message message : tab.messageList) {
            drawMessage(message);
        }
        chatListPanel.validate();
    }

    /**
     * The panel changes after a message has been received.
     *
     *
     * @param message the message received.
     */

    private synchronized void messageRecieved(Message message) {
        if (user.equals(message.getReceiver())) {
            for (Component component : chatListPanel.getComponents()) {
                UserTab tabUser = (UserTab) component;
                if (tabUser.user.equals(message.getSender())) {
                    updateMessage(tabUser, message);
                    return;
                }
            }
            UserTab tabUser = new UserTab(message.getSender());
            tabUser.messageList.add(message);
            tabUser.lastMessage.setText(message.getText());
            chatListPanel.add(tabUser, 0);
        }
    }

    /**
     * The panel changes after a message has been sent.
     *
     * @param message The message sent.
     */
    private synchronized void messageSent(Message message) {
        for (Component component : chatListPanel.getComponents()) {
            UserTab tabUser = (UserTab) component;
            if (tabUser.user.equals(message.getReceiver())) {
                updateMessage(tabUser, message);
                return;
            }
        }
    }

    /**
     * Update a new message for a tab
     *
     * @param tabUser the user tab.
     * @param message the message.
     */
    private void updateMessage(UserTab tabUser, Message message) {
        tabUser.messageList.add(message);
        tabUser.lastMessage.setText(message.getText().replaceAll("\\s+", " "));
        chatListPanel.remove(tabUser);
        chatListPanel.add(tabUser, 0);
        if (tabUser == activeTab) {
            drawMessage(message);
        }
        chatListPanel.validate();
    }

    /**
     * Sent current input to all users
     */
    private void sendMessage() {

        String messageText = textField.getText().trim();
        if (messageText.equals("")) {
            JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Message message = new Message(user, activeTab.user, Timestamp.from(Instant.now()).toString(), messageText);
        try {
            try {
                API.sendmsg(user, activeTab.user, messageText);
            } catch (UserNotExistsException ex) {
                throw new Exception("绘画对象不存在");
            }

            sendMessageToServer(encoder.encodeToString(user.getBytes()) + "@" + encoder.encodeToString(activeTab.user.getBytes()) + "@" + encoder.encodeToString(messageText.getBytes()));
            messageSent(message);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }

        textField.setText(null);
    }




    /**
     * Try to connect to server
     *
     * @param port   port to connect
     * @param hostIp IP address of server
     * @param name   username
     * @return true if succeed otherwise return false
     */
    private boolean connectServer(int port, String hostIp, String name) {
        try {
            socket = new Socket(hostIp, port);// 根据端口号和服务器ip建立连接
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 发送客户端用户基本信息(用户名和ip地址)
            sendMessageToServer(encoder.encodeToString(name.getBytes()) + "@" + socket.getLocalAddress().toString());
            // 开启接收消息的线程
            messageListenerThread = new MessageListenerThread(reader, textArea);
            messageListenerThread.start();
            isConnected = true;
            return true;
        } catch (Exception e) {
            System.out.println("与端口号为：" + port + "    IP地址为：" + hostIp + "   的服务器连接失败!" + "\r\n");
            isConnected = false;
            return false;
        }
    }

    /**
     * Send message to server
     *
     * @param message information to send
     */
    private void sendMessageToServer(String message) {
        writer.println(message);
        writer.flush();
    }

    /**
     * Release all resource
     *
     * @throws IOException if error occurred
     */
    private synchronized void releaseResource() throws IOException {
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
        if (socket != null) {
            socket.close();
        }
        isConnected = false;
    }

    /**
     * Close the connection with server
     *
     * @return true if successfully closed otherwise false
     */
    @SuppressWarnings("deprecation")
    private synchronized boolean closeConnection() {
        try {
            sendMessageToServer("CLOSE");// 发送断开连接命令给服务器
            messageListenerThread.stop();// 停止接受消息线程
            releaseResource();
            return true;
        } catch (IOException e1) {
            e1.printStackTrace();
            isConnected = true;
            return false;
        }
    }
//TODO: 监听信息，并调用messageReceived(Message message)方法
    /**
     * Instance of this class will keep listening the message from server
     */
    class MessageListenerThread extends Thread {
        private BufferedReader reader;
        private JTextArea textArea;

        /**
         * Construct with read to listening from and <code>textArea</code> to show
         * received message
         *
         * @param reader   read message from server
         * @param textArea show received message
         */
        MessageListenerThread(BufferedReader reader, JTextArea textArea) {
            this.reader = reader;
            this.textArea = textArea;
        }

        /**
         * Close connection
         *
         * @throws Exception if error occurred
         */
        synchronized void closeConnectionPassively() throws Exception {
            // 清空用户列表
            //userListModel.removeAllElements();
            // 被动的关闭连接释放资源
            releaseResource();
        }

        /**
         * Work entry
         */
        public void run() {
            String message, username, userIp;
            User user;
            while (true) {
                try {
                    message = reader.readLine();
                    StringTokenizer msgTokenizer = new StringTokenizer(message, "@");
                    String command = msgTokenizer.nextToken();
                    switch (command) {
                        case "CLOSE":
                            textArea.append("服务器已关闭!\r\n");
                            closeConnectionPassively();
                            return;
                        case "ADD":
                            if ((username = msgTokenizer.nextToken()) != null
                                    && (userIp = msgTokenizer.nextToken()) != null) {
                                user = new User(username, userIp);
                                onlineUsers.put(username, user);
                                //userListModel.addElement(username);
                            }
                            break;
                        case "DELETE":
                            username = msgTokenizer.nextToken();
                            onlineUsers.remove(username);
                            //userListModel.removeElement(username);
                            break;
                        case "USERLIST":
                            int size = Integer.parseInt(msgTokenizer.nextToken());
                            for (int i = 0; i < size; i++) {
                                username = msgTokenizer.nextToken();
                                userIp = msgTokenizer.nextToken();
                                user = new User(username, userIp);
                                onlineUsers.put(username, user);
                                //userListModel.addElement(username);
                            }
                            break;
                        case "MAX":
                            textArea.append(msgTokenizer.nextToken() + msgTokenizer.nextToken() + "\r\n");
                            closeConnectionPassively();
                            JOptionPane.showMessageDialog(frame, "服务器缓冲区已满！", "错误", JOptionPane.ERROR_MESSAGE);
                            return;
                        default:
                            textArea.append(message + "\r\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}

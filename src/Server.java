import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Server program entry
 * To start server: run <code>Server#main</code>
 *
 * @author 余天予
 */
public class Server {
    private JFrame frame;
    private JTextArea contentArea;
    private JTextField txt_message;
    private JTextField txt_max;
    private JTextField txt_port;
    private JButton btn_start;
    private JButton btn_stop;
    private JButton btn_send;
    private JPanel connectionStatusPanel;
    private JPanel inputPanel;
    private JScrollPane messagePanel;
    private JScrollPane chatListScroll;

    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private boolean isStart = false;

    private ServerSocket serverSocket;
    private ServerThread serverThread;
    private ArrayList<SingleClientThread> clientThreads;

    /**
     * Default constructor, start the server
     */
    private Server() {
        initServerUI();
        addListeners();
    }

    /**
     * Entry of server program
     *
     * @param args will be ignored by default
     */
    public static void main(String[] args) {
        new Server();
    }

    /**
     * Init the GUI of server
     */
    private void initServerUI() {
        initInputPanel();
        initChatListScroll();
        initMessagePanel();
        initConnectionStatusPanel();
        initServerFrame();
    }

    /**
     * Init the input Panel of server program
     */
    private void initInputPanel() {
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        txt_message = new JTextField();
        btn_send = new JButton("发送");
        inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("写消息"));
        inputPanel.add(txt_message, "Center");
        inputPanel.add(btn_send, "East");
    }

    /**
     * Init chat list which displays all activate users
     */
    private void initChatListScroll() {
        chatListScroll = new JScrollPane(userList);
        chatListScroll.setBorder(new TitledBorder("在线用户"));
    }

    /**
     * Init message panel which displays the messages
     */
    private void initMessagePanel() {
        contentArea = new JTextArea();
        contentArea.setForeground(Color.blue);
        contentArea.setEditable(false);
        messagePanel = new JScrollPane(contentArea);
        messagePanel.setBorder(new TitledBorder("消息显示区"));
    }

    /**
     * Init the connection status panel which displays the connection information,
     * which include the port, max_connection etc.
     */
    private void initConnectionStatusPanel() {
        txt_max = new JTextField("30");
        txt_port = new JTextField("6666");
        btn_start = new JButton("启动");
        btn_stop = new JButton("停止");
        btn_stop.setEnabled(false);
        connectionStatusPanel = new JPanel();
        connectionStatusPanel.setLayout(new GridLayout(1, 6));
        connectionStatusPanel.add(new JLabel("人数上限"));
        connectionStatusPanel.add(txt_max);
        connectionStatusPanel.add(new JLabel("端口"));
        connectionStatusPanel.add(txt_port);
        connectionStatusPanel.add(btn_start);
        connectionStatusPanel.add(btn_stop);
        connectionStatusPanel.setBorder(new TitledBorder("配置信息"));
    }

    /**
     * Init server main frame
     */
    private void initServerFrame() {
        frame = new JFrame("服务器");
        // 更改JFrame的图标：
        // frame.setIconImage(Toolkit.getDefaultToolkit().createImage(Server.class.getResource("qq.png")));
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatListScroll, messagePanel);
        centerSplit.setDividerLocation(100);
        frame.setLayout(new BorderLayout());
        frame.add(connectionStatusPanel, "North");
        frame.add(inputPanel, "South");
        frame.add(centerSplit, "Center");
        frame.setSize(600, 400);
        // frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());//设置全屏
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screen_width - frame.getWidth()) / 2, (screen_height - frame.getHeight()) / 2);
        frame.setVisible(true);
    }

    /**
     * Add listeners to the frame and components in it
     */
    private void addListeners() {
        // 关闭窗口时事件
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isStart) {
                    stopServer();
                }
                System.exit(0);
            }
        });

        // 文本框按回车键时事件
        txt_message.addActionListener(e -> sendServerMessageToAllUsers());

        // 单击发送按钮时事件
        btn_send.addActionListener(arg0 -> sendServerMessageToAllUsers());

        // 单击启动服务器按钮时事件
        btn_start.addActionListener(e -> {
            if (isStart) {
                JOptionPane.showMessageDialog(frame, "服务器已处于启动状态，不要重复启动！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int max;
            int port;
            try {
                try {
                    max = Integer.parseInt(txt_max.getText());
                } catch (Exception e1) {
                    throw new Exception("人数上限为正整数！");
                }
                if (max <= 0) {
                    throw new Exception("人数上限为正整数！");
                }
                try {
                    port = Integer.parseInt(txt_port.getText());
                } catch (Exception e1) {
                    throw new Exception("端口号为正整数！");
                }
                if (port <= 0) {
                    throw new Exception("端口号 为正整数！");
                }
                startServer(max, port);
                contentArea.append("服务器已成功启动!人数上限：" + max + ",端口：" + port + "\r\n");
                JOptionPane.showMessageDialog(frame, "服务器成功启动!");
                btn_start.setEnabled(false);
                txt_max.setEnabled(false);
                txt_port.setEnabled(false);
                btn_stop.setEnabled(true);
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(frame, exc.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 单击停止服务器按钮时事件
        btn_stop.addActionListener(e -> {
            if (!isStart) {
                JOptionPane.showMessageDialog(frame, "服务器还未启动，无需停止！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                stopServer();
                btn_start.setEnabled(true);
                txt_max.setEnabled(true);
                txt_port.setEnabled(true);
                btn_stop.setEnabled(false);
                contentArea.append("服务器成功停止!\r\n");
                JOptionPane.showMessageDialog(frame, "服务器成功停止！");
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(frame, "停止服务器发生异常！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * send message to all users
     */
    private void sendServerMessageToAllUsers() {
        if (!isStart) {
            JOptionPane.showMessageDialog(frame, "服务器还未启动,不能发送消息！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (clientThreads.size() == 0) {
            JOptionPane.showMessageDialog(frame, "没有用户在线,不能发送消息！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String message = txt_message.getText().trim();
        if (message.equals("")) {
            JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        sendServerMessageToAllUsersImpl(message);
        contentArea.append("服务器说：" + txt_message.getText() + "\r\n");
        txt_message.setText(null);
    }

    /**
     * Start the backend of server
     * @param max the maximum of available users
     * @param port the port to listening the message from users
     * @throws java.net.BindException if fail to bind the socket
     */
    private void startServer(int max, int port) throws java.net.BindException {
        try {
            clientThreads = new ArrayList<>();
            serverSocket = new ServerSocket(port);
            serverThread = new ServerThread(serverSocket, max);
            serverThread.start();
            isStart = true;
        } catch (BindException e) {
            isStart = false;
            throw new BindException("端口号已被占用，请换一个！");
        } catch (Exception e1) {
            e1.printStackTrace();
            isStart = false;
            throw new BindException("启动服务器异常！");
        }
    }

    /**
     * release client thread listening resource
     * @param i the index of client thread to free withi
     * @throws IOException if error occurred
     */
    @SuppressWarnings("deprecation")
    private void releaseClientThreadResource(int i) throws IOException {
        clientThreads.get(i).stop();
        clientThreads.get(i).reader.close();
        clientThreads.get(i).writer.close();
        clientThreads.get(i).socket.close();
        clientThreads.remove(i);
    }

    /**
     * stop server thread and all client threads and free resources
     */
    @SuppressWarnings("deprecation")
    private void stopServer() {
        try {
            if (serverThread != null) {
                serverThread.stop();
            }
            for (int i = clientThreads.size() - 1; i >= 0; i--) {
                // 给所有在线用户发送关闭命令
                clientThreads.get(i).getWriter().println("CLOSE");
                clientThreads.get(i).getWriter().flush();
                releaseClientThreadResource(i);
            }

            if (serverSocket != null) {
                serverSocket.close();// 关闭服务器端连接
            }
            listModel.removeAllElements();// 清空用户列表
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
            isStart = true;
        }
    }

    /**
     * send message to all users
     * @param message message to send with
     */
    private void sendServerMessageToAllUsersImpl(String message) {
        for (int i = clientThreads.size() - 1; i >= 0; i--) {
            clientThreads.get(i).getWriter().println("服务器：" + message + "(多人发送)");
            clientThreads.get(i).getWriter().flush();
        }
    }

    /**
     * Server thread which listening the message send from clients
     */
    class ServerThread extends Thread {
        private ServerSocket serverSocket;
        private int maxUserNumber;

        /**
         * Construct server thread with server socket to listening and max number of users
         * @param serverSocket socket to listening from
         * @param maxUserNumber max number of available users
         */
        ServerThread(ServerSocket serverSocket, int maxUserNumber) {
            this.serverSocket = serverSocket;
            this.maxUserNumber = maxUserNumber;
        }

        /**
         * work entry of server thread
         */
        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();

                    if (clientThreads.size() == maxUserNumber) {
                        BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter w = new PrintWriter(socket.getOutputStream());

                        // 接收客户端的基本用户信息
                        String inf = r.readLine();
                        StringTokenizer st = new StringTokenizer(inf, "@");
                        User user = new User(st.nextToken(), st.nextToken());

                        // 反馈连接成功信息
                        w.println("MAX@服务器：对不起，" + user.getName() + user.getIp() + "，服务器在线人数已达上限，请稍后尝试连接！");
                        w.flush();

                        // 释放资源
                        r.close();
                        w.close();
                        socket.close();
                        continue;
                    }
                    SingleClientThread client = new SingleClientThread(socket);
                    client.start();// 开启对此客户端服务的线程
                    clientThreads.add(client);
                    listModel.addElement(client.getUser().getName());// 更新在线列表
                    contentArea.append(client.getUser().getName() + client.getUser().getIp() + "上线!\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Thread to listen the message from one certain client
     */
    class SingleClientThread extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private User user;

        /**
         * Init with the socket bind with client
         * @param socket to listen from
         */
        SingleClientThread(Socket socket) {
            try {
                this.socket = socket;
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());
                // 接收客户端的基本用户信息
                String inf = reader.readLine();
                StringTokenizer st = new StringTokenizer(inf, "@");
                user = new User(st.nextToken(), st.nextToken());
                // 反馈连接成功信息
                writer.println(user.getName() + user.getIp() + "与服务器连接成功!");
                writer.flush();
                // 反馈当前在线用户信息
                if (clientThreads.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = clientThreads.size() - 1; i >= 0; i--) {
                        sb.append(clientThreads.get(i).getUser().getName());
                        sb.append('/');
                        sb.append(clientThreads.get(i).getUser().getIp());
                        sb.append("@");
                    }
                    writer.println("USERLIST@" + clientThreads.size() + "@" + sb.toString());
                    writer.flush();
                }
                // 向所有在线用户发送该用户上线命令
                for (int i = clientThreads.size() - 1; i >= 0; i--) {
                    clientThreads.get(i).getWriter().println("ADD@" + user.getName() + user.getIp());
                    clientThreads.get(i).getWriter().flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PrintWriter getWriter() {
            return writer;
        }

        User getUser() {
            return user;
        }

        /**
         * Listen to one client
         */
        @SuppressWarnings("deprecation")
        public void run() {
            String message;
            while (true) {
                try {
                    message = reader.readLine();    // 接收客户端消息
                    if (message.equals("CLOSE"))    // 下线命令
                    {
                        contentArea.append(this.getUser().getName() + this.getUser().getIp() + "下线!\r\n");
                        // 断开连接释放资源
                        reader.close();
                        writer.close();
                        socket.close();

                        // 向所有在线用户发送该用户的下线命令
                        for (int i = clientThreads.size() - 1; i >= 0; i--) {
                            clientThreads.get(i).getWriter().println("DELETE@" + user.getName());
                            clientThreads.get(i).getWriter().flush();
                        }

                        listModel.removeElement(user.getName());// 更新在线列表

                        // 删除此条客户端服务线程
                        for (int i = clientThreads.size() - 1; i >= 0; i--) {
                            if (clientThreads.get(i).getUser() == user) {
                                SingleClientThread temp = clientThreads.get(i);
                                clientThreads.remove(i);// 删除此用户的服务线程
                                temp.stop();// 停止这条服务线程
                                return;
                            }
                        }
                    } else {
                        dispatcherMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Dispatch message received from one client to other client(s)
         * @param message received message
         */
        void dispatcherMessage(String message) {
            StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
            String source = stringTokenizer.nextToken();
            String owner = stringTokenizer.nextToken();
            String content = stringTokenizer.nextToken();
            message = source + "说：" + content;
            contentArea.append(message + "\r\n");
            if (owner.equals("ALL")) {
                for (int i = clientThreads.size() - 1; i >= 0; i--) {
                    clientThreads.get(i).getWriter().println(message + "(多人发送)");
                    clientThreads.get(i).getWriter().flush();
                }
            }
        }
    }
}

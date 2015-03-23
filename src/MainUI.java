import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainUI {
    private JPanel RootPanel;
    private JTextField localPortInput;
    private JTextField remoteIpInput;
    private JTextField remotePortInput;
    private JButton clientBtn;
    private JButton reconnectBtn;
    private JButton hostBtn;
    private JTextField localStatus;
    private JTextField remoteStatus;

    private ProtectServer ps;

    public MainUI() {
        hostBtn.addActionListener((e) -> {
            hostBtn.setEnabled(false);
            clientBtn.setEnabled(false);
            readAddr(true);
            ps.start();
        });
        clientBtn.addActionListener((e) -> {
            hostBtn.setEnabled(false);
            clientBtn.setEnabled(false);
            readAddr(false);
            ps.start();
        });
        reconnectBtn.addActionListener((e) -> {
            try {
                ps.reconnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainUI");
        frame.setContentPane(new MainUI().RootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void setLocalStatus(String msg) {
        localStatus.setText(msg);
    }

    public void setRemoteStatus(String msg) {
        remoteStatus.setText(msg);
    }

    private void readAddr(boolean isHost) {
        int localPort = Integer.parseInt(localPortInput.getText());
        String remoteIp = remoteIpInput.getText();
        int remotePort = Integer.parseInt(remotePortInput.getText());
        ps = new ProtectServer(isHost, this, localPort, remoteIp, remotePort);
    }
}

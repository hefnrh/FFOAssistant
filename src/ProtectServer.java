import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ProtectServer extends Thread {

    private boolean isHost;
    private MainUI ui;
    private int localPort;
    private String remoteIp;
    private int remotePort;

    private ServerSocket ss;
    private LocalThread lt;
    volatile private RemoteThread rt;

    private List<String> msgList;
    private int no;

    public ProtectServer(boolean isHost, MainUI ui, int localPort, String remoteIp, int remotePort) {
        this.isHost = isHost;
        this.localPort = localPort;
        this.ui = ui;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.msgList = new LinkedList<>();
        this.no = 0;
    }

    public void run() {
        try {
            ss = new ServerSocket();
            ss.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
            ui.setLocalStatus(e.toString());
            return;
        }
        if (isHost) {
            try {
                lt = new LocalThread(localPort);
                ui.setLocalStatus("local connected.");
                ss.bind(new InetSocketAddress(remotePort));
                ui.setRemoteStatus("waiting for opponent");
                rt = new RemoteThread(ss);
            } catch (IOException e) {
                e.printStackTrace();
                ui.setLocalStatus(e.toString());
                return;
            }
        } else {
            try {
                rt = new RemoteThread(remoteIp, remotePort);
                ui.setRemoteStatus("remote connected.");
                ss.bind(new InetSocketAddress(localPort));
                ui.setLocalStatus("waiting for local");
                lt = new LocalThread(ss);
            } catch (IOException e) {
                e.printStackTrace();
                ui.setRemoteStatus(e.toString());
                return;
            }
        }
        rt.setLocalThread(lt);
        lt.setRemoteThread(rt);
        lt.start();
        rt.start();
    }


    private String cacheMsg(String msg) {
        msg = (no++) + "_" + msg;
        msgList.add(msg);
        return msg;
    }

    private void removeCachedMsg(String no) {
        for (ListIterator<String> i = msgList.listIterator(); i.hasNext(); ) {
            String cache = i.next();
            if (cache.startsWith(no)) {
                i.remove();
                return;
            }
        }
    }

    public void reconnect() throws IOException {
        RemoteThread rt;
        if (isHost) {
            rt = new RemoteThread(ss);
        } else {
            rt = new RemoteThread(remoteIp, remotePort);
        }
        rt.setLocalThread(lt);
        lt.setRemoteThread(rt);
        rt.start();
        this.rt = rt;
        resendCachedMsg();
    }

    private void resendCachedMsg() throws IOException {
        for (String s : msgList) {
            rt.send(s);
        }
    }

    private class RemoteThread extends Thread {
        LocalThread lt;
        Socket remoteSock;
        BufferedReader remotebr;
        BufferedWriter remotebw;

        RemoteThread(String ip, int port) throws IOException {
            remoteSock = new Socket();
            remoteSock.connect(new InetSocketAddress(ip, port), 3000);
            remotebr = new BufferedReader(new InputStreamReader(remoteSock.getInputStream(), "Unicode"));
            remotebw = new BufferedWriter(new OutputStreamWriter(remoteSock.getOutputStream(), "Unicode"));
        }

        RemoteThread(ServerSocket ss) throws IOException {
            remoteSock = ss.accept();
            remotebr = new BufferedReader(new InputStreamReader(remoteSock.getInputStream(), "Unicode"));
            remotebw = new BufferedWriter(new OutputStreamWriter(remoteSock.getOutputStream(), "Unicode"));
        }

        synchronized void setLocalThread(LocalThread lt) {
            this.lt = lt;

        }

        synchronized void send(String msg) {
            try {
                remotebw.write(msg);
                remotebw.newLine();
                remotebw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String in;
            try {
                while ((in = remotebr.readLine()) != null) {
                    System.out.println("R: " + in);
                    int splitPos = in.indexOf('_');
                    if (splitPos < 0 || splitPos > 10) {
                        removeCachedMsg(in);
                    } else {
                        String no = in.substring(0, splitPos);
                        String msg = in.substring(splitPos + 1);
                        lt.send(msg);
                        send(no);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class LocalThread extends Thread {

        volatile RemoteThread rt;
        Socket localSock;
        BufferedReader localbr;
        BufferedWriter localbw;

        LocalThread(int port) throws IOException {
            localSock = new Socket();
            localSock.connect(new InetSocketAddress("127.0.0.1", port), 3000);
            localbr = new BufferedReader(new InputStreamReader(localSock.getInputStream(), "Unicode"));
            localbw = new BufferedWriter(new OutputStreamWriter(localSock.getOutputStream(), "Unicode"));
        }

        LocalThread(ServerSocket ss) throws IOException {
            localSock = ss.accept();
            localbr = new BufferedReader(new InputStreamReader(localSock.getInputStream(), "Unicode"));
            localbw = new BufferedWriter(new OutputStreamWriter(localSock.getOutputStream(), "Unicode"));
        }

        synchronized void setRemoteThread(RemoteThread rt) {
            this.rt = rt;
        }

        synchronized void send(String msg) {
            try {
                localbw.write(msg);
                localbw.newLine();
                localbw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String in;
            try {
                while ((in = localbr.readLine()) != null) {
                    System.out.println("L: " + in);
                    in = cacheMsg(in);
                    rt.send(in);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

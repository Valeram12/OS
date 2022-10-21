package manager;

import sun.misc.Signal;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

public class Manager {
    private static final ArrayList<ServerThread> serverList = new ArrayList<>();
    private static final ArrayList<Double> value = new ArrayList<>();
    private static final ArrayList<Process> procList = new ArrayList<>();

    private static AFUNIXServerSocket server;

    public static AtomicBoolean finalResult = new AtomicBoolean(false);
    public static AtomicBoolean bool = new AtomicBoolean(false);

    private static void close(){
        System.out.println("Server closed!");
        for (Process p : procList) p.destroy();
        System.exit(0);
    }

    public static void startProc(String func, String p) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "clients.Function" + func);
        processBuilder.directory(new File("C:/Users/valer/Desktop/lab_1/target/classes"));
        procList.add(processBuilder.start());
        Socket sock = server.accept();
        System.out.println("Connected: " + sock);
        serverList.add(new ServerThread(func, sock, p, value));
    }
    public static void run(String parameter) throws IOException {
        Signal.handle(new Signal("INT"), signal -> bool.set(true));


        final File socketFile = new File(new File(System.getProperty("java.io.tmpdir")),
                "junixsocket-test.sock");
        System.out.println(socketFile);

        server = AFUNIXServerSocket.newInstance();
        server.bind(AFUNIXSocketAddress.of(socketFile));
        System.out.println("server: " + server);

        System.out.println("Waiting for connection...");

        System.out.println("Server started\n");

        startProc("F", parameter);
        startProc("G", parameter);

        while(!finalResult.get()) {
            if (bool.get()) {hardFail();}
        }
        close();
    }

    public static void hardFail(){
        System.out.println("Hard fail!");
        close();
    }

    public static void softFail(String func){
        System.out.println("Soft Fail in " + func + ", can't finish computation!");
        close();
    }

}

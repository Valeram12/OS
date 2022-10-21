package manager;

import sun.misc.Signal;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private final BufferedReader input;
    private final BufferedWriter output;
    private final ArrayList<Double> value;
    private final String func;

    private boolean Message(String msg) {
        if (msg != null) {
            if (msg.contains("Exception occurred:")){
                System.out.println(msg);
                Manager.softFail(func);
            }
            if (msg.equals("Stop"))
                return true;
            if (msg.contains("F result: ") || msg.contains("G result: "))
                synchronized (value) {
                    value.add(Double.parseDouble(msg.substring("Answer ?: ".length())));
                }
            System.out.println(msg);
        }
        return false;
    }

    @Override
    public void run() {
        String msg;
        boolean end = false;
        Signal.handle(new Signal("INT"), signal -> Manager.bool.set(true));
        try {
            while (!end) {
                msg = input.readLine();
                end = Message(msg);
            }
            System.out.println("A socket closed\n");
            if (value.size() == 2){
                Double fResult = value.get(0);
                Double sResult = value.get(1);
                System.out.println("Result: " + fResult + "*" +  sResult + "=" + fResult*sResult);
                Manager.finalResult.set(true);
            }

        } catch (IOException e ) {
            System.out.println(e);
            Manager.softFail(func);
        }
    }

    private void send(String msg) {
        try {
            output.write(msg + "\n");
            output.flush();
        } catch (IOException ignored) {
        }
    }
    ServerThread(String func, Socket socket , String p, ArrayList<Double> value) throws IOException {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.value = value;
        this.func = func;
        send(p);
        setDaemon(true);
        start();
    }




}

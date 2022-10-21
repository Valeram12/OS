package functions;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import os.lab1.compfuncs.basic.*;
import java.io.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class FunctionG {
    private static AFUNIXSocket clientSocket;
    private static BufferedReader input;
    private static BufferedWriter output;
    private static final int countOfTry = 3;

    public static void main(String[] args) throws IOException {
        try {
            System.out.println("G started");
            int numberOfAttemp = 0;
            int rVal = (int)(Math.random() * 10);

            final File socketFile = new File(new File(System.getProperty("java.io.tmpdir")),
                    "junixsocket-test.sock");
            clientSocket = AFUNIXSocket.newInstance();
            clientSocket.connect(AFUNIXSocketAddress.of(socketFile));
            System.out.println("Connected");
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            output.write("Client was connected\n");
            output.flush();
            int p = Integer.parseInt(input.readLine());
            TimeUnit.SECONDS.sleep(rVal);
            while (numberOfAttemp < countOfTry) {
                if (rVal < 4) {
                    Optional<Double> result = DoubleOps.trialG(p);
                    if (result.isPresent()) {
                        output.write("G result: " + result.get() + "\n");
                        output.flush();
                        output.write("Stop\n");
                        output.flush();
                    }
                } else {
                    output.write("G failed. Try again\n");
                    output.flush();
                    rVal = (int)(Math.random() * 10);
                    numberOfAttemp++;
                }
            }
            throw new ArithmeticException("G failed again " + countOfTry + " times");
        } catch (IOException | InterruptedException | ArithmeticException e) {
            output.write("Exception occurred: " + e + "\n");
            output.flush();
            clientSocket.close();
            input.close();
            output.close();
        }
    }
}

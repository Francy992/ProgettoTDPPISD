import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class Server {
    @Override
    public String toString() {
        return "Server{" +
                "port=" + port +
                ", balance=" + balance +
                ", server=" + server +
                ", nAcceptedRequest=" + nAcceptedRequest +
                '}';
    }

    private int port;
    private int balance;

    ServerSocket server;
    int nAcceptedRequest;

    public Server(int port) {
        this.port = port;
    }

    public void go() {

        try {
            server = new ServerSocket(port);
            System.out.println("Starting server on port " + port);
        } catch (IOException e) {
            System.out.println("Cannot start server on port " + port);
            e.printStackTrace();
            System.exit(-1);
        }
        CityList cityList = new CityList();
        while (true) {
            try {
                System.out.println("Ready to accept connections...");
                Socket client = server.accept();
                nAcceptedRequest++;
                System.out.println("Accepted connection request n." + nAcceptedRequest + " from:" + client.getRemoteSocketAddress());

                ClientManager cm = new ClientManager(client, cityList, nAcceptedRequest);

                Thread t = new Thread(cm, "Thread" + nAcceptedRequest + " started.");
                t.start();
            } catch (IOException e) {
                //e.printStackTrace();
            }

        }


    }

}
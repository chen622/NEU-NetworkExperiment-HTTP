package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    static final String DIR = "C:\\Users\\chen\\IdeaProjects\\计网实践课\\实验二\\HTTP\\page";
    private Socket socket;

    public ClientThread(Socket clientSocket) {
        socket = clientSocket;
    }

    public void run() {
        String firstRequest = "";
        String uri = "";
        try {
            OutputStream dataWriter = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {

                firstRequest = reader.readLine();
                System.out.println(firstRequest);
                uri = firstRequest.split(" ")[1];
                File file = new File(DIR + uri);

                if (file.exists()) {
                    writer.println("HTTP/1.1 200 OK");
                    writer.flush();
                    if (uri.endsWith(".html")) {
                        writer.println("Content-type : text.html");
                        writer.flush();
                    } else if (uri.endsWith(".jpg")) {
                        writer.println("Content-type : image.jpg");
                        writer.flush();
                    } else if (uri.endsWith(".wmv")) {
                        writer.println("Content-type : video.wmv");
                        writer.flush();

                    } else {
                        writer.println("Conetent-type : application/octect-stream");
                        writer.flush();
                    }
                    FileInputStream in = new FileInputStream(file);
                    writer.println("Content-length : " + in.available());
                    writer.println();
                    writer.flush();


                    byte[] byteBuffer = new byte[1024];
                    int length = 0;
                    while ((length = in.read(byteBuffer)) != -1) {
                        dataWriter.write(byteBuffer, 0, length);
                        if (length < 1024) {
                            dataWriter.flush();
                            break;
                        }
                    }

                    in.close();
                } else {
                    writer.println("HTTP/1.1 404 Not found");
                    writer.println("Content-type : text/plain");
                    writer.println("Content-Length : 0");
                    writer.println();
                    writer.flush();
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

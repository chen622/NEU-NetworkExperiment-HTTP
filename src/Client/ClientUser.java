package Client;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientUser {
    static final String DIR = "f:/Client";
    static final int PORT = 8888;
    Socket socket = null;
    String firstResponse = "";
    String response = "";
    String HTML = "";
    String img = "";
    Set<String> picSet = new HashSet<>();
    Pattern p_image;
    Matcher m_image;
    Boolean isHTML = false;
    PrintWriter writer;
    BufferedReader reader;

    public void main(String fileName) {
        try {
            socket = new Socket("localhost", PORT);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            get(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDirectory(String picUrl) {
        String[] directory = picUrl.replaceAll("\\\\", "/").split("/");
        File file;
        String path = DIR;
        for (int i = 0; i < directory.length - 1; i++) {
            path = path + "/" + directory[i];
            file = new File(path);
            file.mkdir();
        }
    }

    public void get(String fileName) {
        try {
            if (fileName.contains(".html"))
                isHTML = true;
            else
                isHTML = false;
            writer.println("GET /" + fileName + " HTTP/1.1");
            writer.flush();

            firstResponse = reader.readLine();
            System.out.println(firstResponse);
            for (int i = 0; i < 3; i++) {
                response = reader.readLine();
                System.out.println(response);
            }

            String status = firstResponse.split(" ")[1];
            if (status.equals("200")) {
                FileOutputStream out = new FileOutputStream(DIR + "/" + fileName);
                InputStream dataReader = socket.getInputStream();
                int length = 0;
                byte[] byteBuffer = new byte[1024];

                while ((length = dataReader.read(byteBuffer)) != -1) {
                    HTML = HTML + new String(byteBuffer);
                    out.write(byteBuffer, 0, length);
                    if (length < 1024)
                        break;
                }
                if (isHTML) {
                    String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
                    p_image = Pattern.compile
                            (regEx_img, Pattern.CASE_INSENSITIVE);
                    m_image = p_image.matcher(HTML);
                    while (m_image.find()) {
                        // 得到<img />数据
                        img = m_image.group();
                        // 匹配<img>中的src数据
                        Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
                        while (m.find()) {
                            picSet.add(m.group(1));
                        }
                    }
                    Iterator<String> iterator = picSet.iterator();
                    String picPath;
                    while (iterator.hasNext()) {
                        picPath = iterator.next();
                        if (picPath.contains("\\\\"))
                            continue;
                        createDirectory(picPath);
                        System.out.println(picPath);
                        get(picPath);
                    }

                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.email.mailclient.visuals;

import java.io.*;
import java.net.Socket;

public class Client {
    static DataInputStream dis;
    static DataOutputStream dos;
    public static void main(String[] args) throws IOException {
        String serverIP = "localhost";

//        socketImap.close();
        Socket socketSMTP = new Socket(serverIP,25);
        dos = new DataOutputStream(socketSMTP.getOutputStream());
        dis = new DataInputStream(socketSMTP.getInputStream());
        String sender = "trungnd.21it@se.vku.vn";
        String receiver = "vantn.21it@se.vku.vn";
        String subject = "xin chào";
        String content = "Minh la trung day";
        String attachment = "file.txt:mail.sql";

//
        communicate("HELO");
        communicate("MAIL FROM:"+sender);
        communicate("RCPT TO:"+receiver);
        communicate("DATA");
        communicate("SUBJECT:"+subject);
        communicate("CONTENT:"+content);
        communicate("ATTACH:"+attachment);
        dos.writeUTF("QUIT");

        if (dis.readUTF().equals("OK")) {
            Socket socketFTP = new Socket("localhost", 21);
            DataOutputStream dos2 = new DataOutputStream(socketFTP.getOutputStream());
            DataInputStream dis2 = new DataInputStream(socketFTP.getInputStream());
            dos2.writeUTF("FTP " + sender + " " + receiver);
            if (dis2.readUTF().equals("OK")) {
                OutputStream os = socketFTP.getOutputStream();
                for (int i=0; i<attachment.split(":").length; i++) {
                    File file = new File("E:\\" + attachment.split(":")[i]);
                    dos2.writeUTF(attachment.split(":")[i]);
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                        break;
                    }
                    if (!dis2.readUTF().equals("OK")) break;
                }
                // end session
                dos2.writeUTF("QUIT");
                System.out.println(dis2.readUTF());
            }
        }
    }

    public static void communicate(String request) throws IOException {
        dos.writeUTF(request);
        System.out.println("client said: " + request);
        System.out.println("client said: " + request + " - server said: " + dis.readUTF());
    }

}

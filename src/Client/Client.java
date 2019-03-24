package Client;

import java.util.Scanner;

public class Client {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String fileName;
        while(true){
            System.out.println("Please enter the name of file you want to download.");
            if ((fileName=scanner.nextLine())!=null){
                new ClientUser().main(fileName);
            }
        }

    }


}
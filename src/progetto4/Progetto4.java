/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import static java.awt.PageAttributes.MediaType.B;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.System.out;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.UUID;
import javafx.util.Pair;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author gia
 */
public class Progetto4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, ClassNotFoundException {

        Client gio = new Client("giovanni");
        gio.setShamirScheme(3, 5, 64);
        menu(gio);

    }

    public static void menu(Client client) throws IOException, NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        boolean enter=true;
        while (enter) {
            System.out.print("\nSharing-Service: \n[1]Upload file\n[2]Download file\n[3]Visualizza file presenti sul server\n[4]Esci\nScelta: ");
            int choice = scanner.nextInt();
            if (choice == 1) {
                JFileChooser fileChooser = new JFileChooser();
                client.upload(Utility.nameFile(fileChoice("File disponibili per l'upload: ", fileChooser)));
                System.out.println("\nEffettuato upload sui server");
            } else if (choice == 2) {

                if (client.getNameFilesOnline().size() != 0) {
                    System.out.println("\nFile presenti sui server per il download: ");
                    for (int i = 0; i < client.getNameFilesOnline().size(); i++) {
                        System.out.println("[" + i + "] " + client.getNameFilesOnline().get(i));
                    }
                    System.out.print("\nScelta: ");
                    int scelta = scanner.nextInt();
                    System.out.println("\nServer online per il file [ " + client.getNameFilesOnline().get(scelta) + " ] :");
                    ArrayList<String> servers = client.getServerOnline(client.getNameFilesOnline().get(scelta));
                    for (String s : servers) {
                        System.out.println("Server " + s);
                    }
                    System.out.println("\nDownload in corso...");
                    boolean checkMac=client.download(client.getNameFilesOnline().get(scelta));
                    System.out.println("\nFile salvato nella directory Download");
                    System.out.println("Verifica Mac del file: "+checkMac);

                } else {
                    System.out.println("\nNessun file sui server per il download: ");
                }

            } else if(choice==3){
                if (client.getNameFilesOnline().size() != 0) {
                    System.out.println("\nFile presenti sui server per il download: ");
                    for (int i = 0; i < client.getNameFilesOnline().size(); i++) {
                        System.out.println(client.getNameFilesOnline().get(i));
                    }
                }
            }else{
                Path currentRelativePath = Paths.get("src/progetto4");
                String download = currentRelativePath.toAbsolutePath().toString() + "/Download/";
                for(int i=0;i<client.getNameFilesOnline().size();i++){
                    File f=new File(download.toString()+client.getNameFilesOnline().get(i));
                    f.renameTo(new File(currentRelativePath.toAbsolutePath().toString()+"/Repo/"+client.getNameFilesOnline().get(i)));
                }

                enter=false;
            }
        }

    }

    public static String fileChoice(String titolo, JFileChooser fileChooser) {

        Path currentRelativePath = Paths.get("src/progetto4/Repo");
        String s = currentRelativePath.toAbsolutePath().toString();
        fileChooser.setCurrentDirectory(new File(s));

        String filename = "";
        fileChooser.setDialogTitle(titolo);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            filename = fileChooser.getSelectedFile().getPath();

        } else if (result == JFileChooser.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(null, "You selected nothing.");
        } else if (result == JFileChooser.ERROR_OPTION) {
            JOptionPane.showMessageDialog(null, "An error occurred.");
        }

        return filename;
    }
}

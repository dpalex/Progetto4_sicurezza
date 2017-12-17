/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, ClassNotFoundException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        
        menu("giovanni",false);
        
    }

    public static void menu(String id,boolean session) throws IOException, NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, ClassNotFoundException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Client client = new Client(id,session);
        client.setShamirScheme(3, 5, 128);
        Scanner scanner = new Scanner(System.in);
        boolean enter = true;
        while (enter) {
            System.out.print("\nSharing-Service: \n[1]Upload file\n[2]Download file\n[3]Visualizza file presenti sul server\n[4]Esci\n\nScelta: ");
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

                    System.out.println("\nFile salvato nella directory Download");
                    System.out.println("Verifica Mac del file: ");
                    ArrayList<String> checks=client.download(client.getNameFilesOnline().get(scelta));
                    for(int i=0;i<checks.size()-1;i++){
                       System.out.println("Server "+(i+1)+": "+checks.get(i)); 
                    }
                    System.out.println("File Original: "+checks.get(checks.size()-1)); 


                } else {
                    System.out.println("\nNessun file sui server per il download! ");
                }

            } else if (choice == 3) {
                if (client.getNameFilesOnline().size() != 0) {
                    System.out.println("\nFile presenti sui server per il download: ");
                    for (int i = 0; i < client.getNameFilesOnline().size(); i++) {
                        System.out.println(client.getNameFilesOnline().get(i));
                    }
                }else {
                    System.out.println("\nNessun file sui server per il download! ");
                }
            } else {
                String[] files = Utility.getPathFiles("Download");
                Path currentRelativePath = Paths.get("src/progetto4");
                String repo = currentRelativePath.toAbsolutePath().toString() + "/Repo/";
                for (String s : files) {
                    File f = new File(s);
                    f.renameTo(new File(repo+Utility.nameFile(s)));
                }
                String[] folders = Utility.getPathFiles("Servers");
                for (String s : folders) {
                    File f = new File(s);
                    Utility.removeDirectory(f);
                }
                //client.SaveSession();
                enter = false;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
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

        menu("giovanni", false);

    }

    public static void menu(String id, boolean session) throws IOException, NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, ClassNotFoundException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Client client = new Client(id, session);
        if (!session) {
            client.setShamirScheme(3, 7, 256);
        }
        Scanner scanner = new Scanner(System.in);
        boolean enter = true;
        while (enter) {
            System.out.print("\nSharing-Service: \n[1]Upload file\n[2]Download file\n[3]Visualizza file presenti sul server\n[4]Visualizza integrità file\n[5]Refresh directory\n[6]Salva sessione\n[any]Esci\n\nScelta: ");
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
                    String nameFile = client.getNameFilesOnline().get(scelta);
                    System.out.println("\nServer online per il file [ " + nameFile + " ] :");

                    ArrayList<String> servers = client.getServerOnline(client.getNameFilesOnline().get(scelta));

                    System.out.println("\nDownload in corso...");
                    String result = client.download(client.getNameFilesOnline().get(scelta));
                    System.out.println("Verifica Mac del file: " + result);
                    System.out.println("\nFile salvato nella directory Download");

                } else {
                    System.out.println("\nNessun file sui server per il download! ");
                }

            } else if (choice == 3) {
                if (client.getNameFilesOnline().size() != 0) {
                    System.out.println("\nFile presenti sui server per il download: ");
                    for (int i = 0; i < client.getNameFilesOnline().size(); i++) {
                        System.out.println(client.getNameFilesOnline().get(i));

                    }
                } else {
                    System.out.println("\nNessun file sui server per il download! ");
                }
            } else if (choice == 4) {
                if (client.getNameFilesOnline().size() != 0) {
                    System.out.println("\nFile presenti sui server: ");
                    for (int i = 0; i < client.getNameFilesOnline().size(); i++) {
                        String name = client.getNameFilesOnline().get(i);
                        System.out.println("\nNome file: " + name + "   Lista Server Online: ");
                        HashMap<String, String> checkMac = client.getStates(name);
                        for (String s : checkMac.keySet()) {
                            System.out.println("Server [" + s + "] Integrità: " + checkMac.get(s));
                        }

                    }
                } else {
                    System.out.println("\nNessun file sui server");
                }
            } else if (choice == 5) {
                String[] files = Utility.getPathFiles("Download");
                Path currentRelativePath = Paths.get("src/progetto4");
                String repo = currentRelativePath.toAbsolutePath().toString() + "/Repo/";
                for (String s : files) {
                    File f = new File(s);
                    f.renameTo(new File(repo + Utility.nameFile(s)));
                }
                String[] folders = Utility.getPathFiles("Servers");
                for (String s : folders) {
                    File f = new File(s);
                    Utility.removeDirectory(f);
                }
                //client.SaveSession();

            } else if (choice == 6) {
                client.SaveSession();
                enter = false;
            } else {
                String[] files = Utility.getPathFiles("Download");
                Path currentRelativePath = Paths.get("src/progetto4");
                String repo = currentRelativePath.toAbsolutePath().toString() + "/Repo/";
                for (String s : files) {
                    File f = new File(s);
                    f.renameTo(new File(repo + Utility.nameFile(s)));
                }
                String[] folders = Utility.getPathFiles("Servers");
                for (String s : folders) {
                    File f = new File(s);
                    Utility.removeDirectory(f);
                }
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

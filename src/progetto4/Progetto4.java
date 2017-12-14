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
        /*
        gio.upload("Project.docx");
        byte[] down = gio.download("Project.docx");
        System.out.println(down.length);
        Path currentRelativePath = Paths.get("src/progetto4");
        String download = currentRelativePath.toAbsolutePath().toString() + "/Download/";
        Utility.writeFile(download + "Project.docx", down);
        System.out.println("Controllo mac: " + gio.checkMac("Project.docx", down));*/

    }

    public static void menu(Client client) throws IOException, NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Sharing-Service: \n[1]Upload file\n[2]Download file\nScelta: ");
            int choice = scanner.nextInt();
            if (choice == 1) {
                JFileChooser fileChooser = new JFileChooser();
                client.upload(Utility.nameFile(fileChoice("File disponibili per l'upload: ", fileChooser)));
            } else {
                System.out.println("File presenti sui server per il download: ");
                for(int i=0;i<client.getNameFilesOnline().size();i++){
                    System.out.println("["+i+"] "+client.getNameFilesOnline().get(i));
                }
                System.out.print("\nScelta: ");
                
                client.download(client.getNameFilesOnline().get(scanner.nextInt()));
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

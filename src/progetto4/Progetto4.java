
package progetto4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Progetto4 {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, ClassNotFoundException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DataFormatException {
        //menu di test
        menu();
    }
    
    /*
    Metodo che presenta un menu per i servizi proposti, il menu permette di:
    -Istanziare un client:
        In caso questo esista già, viene caricata la sua ultima sessione
        Se non è presente il client, viene chiesto di istanziare uno schema di shamir scegliendo i parametri
    -Effettuare un upload
    -Effettuare un download
    -Visualizzare i file per cui si è già effetuato upload su server
    -Verificare l'integrità di un file e i suoi shares presenti sui server online
    -Resettare l'intera sessione, quindi cancellare tutti i file e tutti i server
    -Salvare la sessione ed uscire
    */
    public static void menu() throws IOException, NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, ClassNotFoundException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DataFormatException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inserisci id utente: ");
        String id = scanner.next();
        Client client = new Client(id);

        if (Utility.loadSession(id) != null) {
            System.out.println("Caricata sessione precedente dell'utente: " + client.id + ". Schema utilizzato: k=" + client.getInfoScheme()[0] + " n=" + client.getInfoScheme()[1] + " dim block=" + client.getInfoScheme()[2]);
        } else {
            System.out.print("\nNuovo utente: " + id + " ShamirScheme:\nK: ");
            int k = scanner.nextInt();
            System.out.print("N: ");
            int n = scanner.nextInt();
            System.out.print("Dim blocchi (in byte): ");
            int dim = scanner.nextInt();
            client.setShamirScheme(k, n, dim);
        }
        boolean enter = true;
        while (enter) {
            System.out.print("\nSharing-Service: \n[1]Upload file\n[2]Download file\n[3]Visualizza file presenti sul server\n[4]Visualizza integrità file\n[5]RESET ALL\n[any]Esci e salva sessione\n\nScelta: ");
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
                    System.out.println("\nServer online per il file [ " + nameFile + " ]:\n");
                    HashMap<String, String> checkMac = client.getStates(nameFile);
                    for (String s : checkMac.keySet()) {
                        System.out.println("Server [" + s + "] Integrità: " + checkMac.get(s));
                    }
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
                System.out.println("Il reset provocherà la cancellazione di tutti i file, i server e la sessione utente! proseguire? [y/n]");
                String tmp = scanner.next();
                if (tmp.matches("y")) {
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
                   
                    File clientToDelete=new File(Paths.get("src/progetto4/Clients").toString()+"/"+client.id);
                    clientToDelete.delete();
                    enter = false;
                }
            } else {
                client.SaveSession();
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

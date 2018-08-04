//Fousekis Konstantinos
//icsd13196


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {

    public static void main(String[] args) {
        //Δήλωση ροων για την ανταλλαγή μηνηματων μεταξυ εξυπηρετητη και clients
        Socket sock = null;
        ServerSocket ss = null;
        Boolean token = false;

        try {
            System.out.println("Threaded Server is Running  ");
            
            //Ανοιγμα πορτας για να δεχεται συνδεσεις απο clients
            ss = new ServerSocket(8080);
            System.out.println("Waiting for a Client..");

            while (true) {
                //Αποδοχή σύνδεσης client
                sock = ss.accept();
                //O server διαβαζει το τύπο token που αποστέλει ο κάθε client για να καταλάβει αν προκειται
                //για παραγγελία(false) ή για σύνδεση του service(true)
                ObjectInputStream instream = new ObjectInputStream(sock.getInputStream());
                
                //Ροή ανάγνωσης και αποθήκευση του περιεχομένου της σε ένα token
                token = (boolean)instream.readObject();
                
                System.out.println("Client Connected ! !");
                 
                //Thread του server το οποίο δέχεται το socket σύνδεσης και το token για την ταυτοποίηση σύνδεσης(ston constructor)
                MyThread serverThread = new MyThread(sock,token);
                //Ξεκίνημα του thread
                serverThread.start();
      
            }

        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

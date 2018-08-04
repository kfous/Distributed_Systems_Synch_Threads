
//Fousekis Konstantinos
//icsd13196

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainService {

    public static void main(String[] args) throws IOException {
        Boolean token = true;

        while (true) {
            try {
                //Σύνδεση στον τοπικό εξυπηρετητή
                Socket socket = new Socket("localhost", 8080);

                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());

                //Αποστολη στο server token true για να καταλαβει οτι προκειται για service client
                outstream.writeObject(token);
                outstream.flush();
                
                //Αρχικοποίηση και ξεκίνημα του παρόντος thread με sleep delay στα 7 δευτερόλεπτα
                ServiceServer srv1 = new ServiceServer(socket);
                srv1.start();
                srv1.sleep(7000);

            } catch (InterruptedException ex) {
                Logger.getLogger(MainService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

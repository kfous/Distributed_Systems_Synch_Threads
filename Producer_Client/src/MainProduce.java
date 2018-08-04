//Fousekis Konstantinos
//icsd13196

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainProduce {

    public static void main(String[] args) throws IOException {
        Boolean token = false;

        while (true) {
            //Client παραγωγής
            try {
                //σύνδεση στο τοπικό εξυπηρετητή στην ανάλογη πορτα
                Socket socket = new Socket("localhost", 8080);

                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                //Αποστολη στο server token false για να καταλαβει οτι προκειται για order
                outstream.writeObject(token);
                outstream.flush();

                //αρχικοποίηση και ξεκίνημα του thread με sleep στα 15 δευτερόλεπτα
                ProduceServer prs1 = new ProduceServer(socket);
                prs1.start();
                prs1.sleep(15000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainProduce.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

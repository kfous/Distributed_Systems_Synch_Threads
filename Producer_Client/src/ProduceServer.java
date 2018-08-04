//Fousekis Konstantinos
//icsd13196

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProduceServer extends Thread {

    private Socket socket;

    public ProduceServer(Socket socket) {
        super();
        this.socket = socket;
        setDaemon(true);  //θα τρέχει μέχρι να σταματήσει το thread που το ξεκίνησε δηλαδή του server
    }

    @Override
    public void run() {

        try {

            //Arxikopoihsh rown eggrafhs - anagnwshs
            ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());

            //Tote ksekina h epikinwnia me ton eksiphrethth stelnontas start
            Message mymsg = new Message("START", 0);
            outstream.writeObject(mymsg);
            outstream.flush();

            Message msgin = (Message) instream.readObject();
            //An h apadhsh einai Waiting
            if (msgin.getC().equals("WAITING")) {
                
                // Υπολογίζεται τι θα βγάλει η παραγωγή , ποτό η φαγητό
                Random rm = new Random();
                int type = 1 + rm.nextInt(10);
                
                Message msgout = new Message("ORDER", type); // μηνυμα παραγγελίας και με το τύπο προιόντος
                outstream.writeObject(msgout);
                outstream.flush();

                //An o server aposteilei ok
                if (msgin.getC().equals("OK")) {
                    //Termatizetai h sindesh stelnontas END
                    Message msg2 = new Message("END", 0);
                    outstream.writeObject(msg2);
                    outstream.flush();
                }
            }
          
           

        } catch (IOException ex) {
            Logger.getLogger(ProduceServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ProduceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

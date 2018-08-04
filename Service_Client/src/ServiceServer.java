//Fousekis Konstantinos
//icsd13196

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceServer extends Thread {

    private Socket socket;

    public ServiceServer(Socket socket) {
        this.socket = socket;
        setDaemon(true);// θα τρέχει μέχρι να σταματήσει το thread που το ξεκίνησε δηλαδή του server
    }

    @Override
    public void run() {
        //Υπολογισμός με τυχαίο τρόπο το αν ο σερβιτόρος που έρχεται στο πάγκο είναι για φαγητά ή για ποτα
        // αν τύχει 1 τότε είναι για φαγητά και 2 για ποτά
        Random rm = new Random();
        int ServiceType = 1 + rm.nextInt(2); // 1 for food 2 for drinks
        get(ServiceType);

    }

    public synchronized void get(int ServiceType) { //sychronized συνάρτηση που δέχεται σαν παράμετρο τον τύπο σερβιτόρου
        ObjectOutputStream outstream = null;
        ObjectInputStream instream = null;
        try {
            //ροές εγγραφής ανάγνωσης
            outstream = new ObjectOutputStream(socket.getOutputStream());
            instream = new ObjectInputStream(socket.getInputStream());
            
            //ανάγνωση command μέσω message αντικειμένου
            Message msg = (Message) instream.readObject();
            
            //Σε περίπτωση που η λίστα περιέχει φαγητόσ το πρώτο κελί της τότε στέλεται οκ και ο αντίστοιχος διαθέσιμος σερβιτόρος
            if (msg.getC().equals("food")) {
                outstream.writeObject(new Message("OK", ServiceType));
                outstream.flush();
                
            } else if (msg.getC().equals("drink")) { // αντίστοιχα και για τα ποτά στη λίστα
                outstream.writeObject(new Message("OK", ServiceType));
                outstream.flush();
                
            } else {//Αν όμως η λίστα είναι κενή τότε 
                if (msg.getC().equals("empty")) {
                    outstream.writeObject(new Message("OK", ServiceType)); // απαντάται οκ
                    outstream.flush();
                }
                msg = (Message) instream.readObject();
                if (msg.getC().equals("wait")) { // και απο το σέρβερ έρχεται μήνυμα αναμονής
                    if (ServiceType == 1) {
                        System.out.println("this FOOD service is waiting");
                    } else {
                        System.out.println("this DRINKS service is waiting");
                    }
                    wait(); // και η αναμονή πραγματοποιείται μέχρι το επόνο notify του παρόντος thread
                }
            }
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outstream.close();
            } catch (IOException ex) {
                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

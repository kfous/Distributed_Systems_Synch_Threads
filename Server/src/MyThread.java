//Fousekis Konstantinos
//icsd13196

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyThread extends Thread {

    private Socket socket;
    private boolean token;
    private static ArrayList<Integer> bench = new ArrayList<Integer>(); //Λίστα πάγκου
    private static int foodCount;
    private static int drinkCount;

    public MyThread(Socket socket, boolean token) {
        this.socket = socket;
        this.token = token;
    }

    @Override
    public synchronized void run() { //sychronized run μεθοδος για την χρήση των εντολών που δυνοδεύονται για την αναμονή εκκίνηση thread
        
        //Αν το token είναι false τότε πρόκειται για σύνδεση με client παραγωγής
        if (token == false) {
            try {

                //Ροές εγγραφής και ανάγνωσης
                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());

                //Antikeimeno typou Mesaage gia anagnwsh mynhmatos pou exei apostalei mesw sindesewn sto 
                //sto socket tou server
                Message msg = (Message) instream.readObject();
                
                //Μηνύματα επικοινωνίας μετάξύ clients-server τυπου start order wait 
                if (msg.getC().equals("START")) {
                    //Epivevewsh stelnontas waiting
                    outstream.writeObject(new Message("WAITING", 0));
                    outstream.flush();

                    do {//Mexri na apostalei apo ton Client to mynhma END

                        //Αν το command  του μυνήματος είναι τύπου order
                        msg = (Message) instream.readObject();
                        if (msg.getC().equals("ORDER")) {
                            
                            //Αν το order type είναι άρτιος αριθμός τότε δηλαδή ποτό
                            if (msg.getType() % 2 == 0) {
                                bench.add(msg.getType());   //καταχώρηση του ποτού στη λίστα
                                notify();                   //ειδοποίηση τυχόν thread για σερβιτορους σε αναμονή
                                System.out.println("A drink is placed on the bench");
                                drinkCount++;   //αύξηση του counter Ποτών
                            } else {
                                bench.add(msg.getType());//αν πρόκειται για φαγητά, δηλαδή ο αριθμός είναι περιττός
                                notify();                //τότε προσθήκη του στη λίστα και ενημέρωση τυχόν thread σε αναμονή
                                System.out.println("A food is placed on the bench");
                                foodCount++;            //αύξηση counter φαγητών
                            }
                        }
                        //Kai apostol;h mynhmatos epityxous  diadiakasias
                        outstream.writeObject(new Message("OK", 0));
                        outstream.flush();

                    } while (msg.getC().equals("END"));

                    System.out.println("Bench has " + drinkCount + " drinks and " + foodCount + " food plates");
                    System.out.println("Connection Closing...");
                    instream.close();
                    outstream.close();

                }

            } catch (IOException | ClassNotFoundException e) {
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {            //σε αντίθετη περίπτωση όπου το token είναι αληθές και αφορά σύνδεση service
            if (token == true) {

                if (!bench.isEmpty()) { // αν ο παγκος δεν είναι αδειος
                    try {
                        ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());

                        if (foodCount > 0) { // και αν υπάρχουν φαγητά στον πάγκο
                            
                            if ((bench.get(0).intValue() % 2 != 0)) {//και αν το πρωτο κελι εχει περριτο αριθμο- φαγητο
                                outstream.writeObject(new Message("food", 0)); //στείλε μήνυμα food
                                outstream.flush();

                                Message msg = (Message) instream.readObject();
                                if (msg.getC().equals("OK") && msg.getType() == 1) { // Αν απαντήσει οκ και έχει έρθει ο σερβιτόρος
                                    bench.remove(0);                                // για τα φαγητά τότε αφαίρεσε το φαγητό απο λίστα
                                    System.out.println(bench);

                                    System.out.println("A food was serviced");
                                    foodCount--; //μείωσε τον μετρητή φαγητών
                                } else {
                                    System.out.println("The food plates service is not avaliable");

                                }
                            }

                        } else {// αλλίως αν ο μετρητής ποτών δεν είναι μηδενικός
                            if (drinkCount > 0) {//
                                // και αν το πρωτο κελι εχει αρτιο αριθμο-ποτο
                                if ((bench.get(0).intValue() % 2 == 0)) {
                                    outstream.writeObject(new Message("drink", 0));// τότε στείλε  στον service client
                                    outstream.flush();                              //ότι υπάρχει ποτό στο πάγκο

                                    Message msg = (Message) instream.readObject();

                                    if (msg.getC().equals("OK") && msg.getType() == 2) { // και αν απαντήσει οκ και
                                        bench.remove(0);                                 // είναι διαθέσιμος ο σερβιτόρος για τα φαγητα
                                        System.out.println(bench);                       // τότε αφαιρείται το ποτό από τον πάγκο

                                        System.out.println("A drink was serviced");
                                        drinkCount--; //μείωσε τον μετρητή ποτών
                                    } else {
                                        System.out.println("The drinks service is not avaliable");

                                    }
                                }
                            }
                        }
                        instream.close();
                        outstream.close();
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (bench.isEmpty() == true) { // αν όμως  ο πάγκος είναι άδειος κατά τη σύνδεση ενός σερβιτόρου

                    try {
                        ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());

                        outstream.writeObject(new Message("empty", 0));// αποστέλεται νάλογο μήνημα
                        outstream.flush();

                        Message msg = (Message) instream.readObject();

                        if (msg.getC().equals("OK") && msg.getType() == 2) { 
                            outstream.writeObject(new Message("wait", 0));
                            outstream.flush();      // και αν σταλεί οκ και γίνει με επιτυχία η επικοινω΄νία
                                                    // αποστέλεται νέο μήνημα αναμονής στον service client
                        } else {
                            if (msg.getC().equals("OK") && msg.getType() == 1) {
                                outstream.writeObject(new Message("wait", 0));
                                outstream.flush();

                            }

                        }

                    } catch (IOException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
    }

}

//Fousekis Konstantinos
//icsd13196

import java.io.Serializable;
//Κλάση που συμβάλει στην αποστολή μυνημάτων μεταξύ clients-server
public class Message implements Serializable {

    private String command;
    private int type;

    public Message() {
        this.command = null;
        this.type = 0;

    }
//constructor gia arxikopoihsh twn idiothtwn simfwna me tis paramatrous pou dexetai

    public Message(String command, int type) {
        this.command = command;         //δεχεται στον constructor της ένα string για τις εντολές επικοινωνίας
        this.type = type;                   // και ένα type για τον τύπο προιόντος και σερβιτόρου

    }

    //Get synarthseis gia thn apostolh twn stoixeiwn otan zhththoun
    public String getC() {
        return command;
    }

    public int getType() {
        return type;
    }

}

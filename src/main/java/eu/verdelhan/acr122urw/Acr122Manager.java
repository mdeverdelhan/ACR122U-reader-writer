package eu.verdelhan.acr122urw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nfctools.mf.MfCardListener;
import org.nfctools.mf.MfReaderWriter;
import org.nfctools.mf.card.MfCard;

/**
 * Test of Mifare Reader/Writer
 */
public class Acr122Manager {
    
    /**
     * Entry point.
     * @param args the command line arguments
     * @see Acr122Manager#printHelpAndExit() 
     */
    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            printHelpAndExit();
        }
        
        switch (args[0]) {
            case "-d":
            case "--dump":
                dumpCards(args);
                break;
            case "-w":
            case "--write":
                writeToCards(args);
                break;
            case "-h":
            case "--help":
            default:
                printHelpAndExit();
                break;
        }
    }
    
    /**
     * Listens for cards using the provided listener.
     * @param listener a listener
     */
    private static void listen(MfCardListener listener) throws IOException {
        Acr122Device acr122;
        try {
            acr122 = new Acr122Device();
        } catch (RuntimeException re) {
            System.out.println("No ACR122 reader found.");
            return;
        }
        acr122.open();
        acr122.getReaderWriter().setCardListener(listener);
        
        acr122.startListening();
        
        System.out.println("Press ENTER to exit");
        System.in.read();
        
        acr122.stopListening();
        acr122.close();
    }
    
    /**
     * Dumps cards.
     * @param args the arguments of the dump command
     */
    private static void dumpCards(String... args) throws IOException {
        // Building the list of keys
        final List<String> keys = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            String k = args[i];
            if (MifareUtils.isValidMifareClassic1KKey(k)) {
                keys.add(k);
            }
        }
        // Adding the common keys
        keys.addAll(MifareUtils.COMMON_MIFARE_CLASSIC_1K_KEYS);
        
        // Card listener for dump
        MfCardListener listener = new MfCardListener() {
            @Override
            public void cardDetected(MfCard mfCard, MfReaderWriter mfReaderWriter) throws IOException {
                System.out.println("Card detected: " + mfCard);
                MifareUtils.dumpMifareClassic1KCard(null, keys);
            }
        };
        
        // Start listening
        listen(listener);
    }
    
    /**
     * Writes to cards.
     * @param args the arguments of the write command
     */
    private static void writeToCards(String... args) throws IOException {
        // Checking arguments
        if (args.length != 5) {
            printHelpAndExit();
        }
        
        final String sector = args[1];
        final String block = args[2];
        final String key = args[3];
        final String data = args[4];
        if (!MifareUtils.isValidMifareClassic1KSectorIndex(sector)
                || !MifareUtils.isValidMifareClassic1KBlockIndex(block)
                || !MifareUtils.isValidMifareClassic1KKey(key)
                || !HexUtils.isHexString(data)) {
            printHelpAndExit();
        }
        
        final int sectorId = Integer.parseInt(sector);
        final int blockId = Integer.parseInt(block);
        
        // Card listener for writing
        MfCardListener listener = new MfCardListener() {
            @Override
            public void cardDetected(MfCard mfCard, MfReaderWriter mfReaderWriter) throws IOException {
                System.out.println("Card detected: " + mfCard);
                MifareUtils.writeToMifareClassic1KCard(null, sectorId, blockId, key, data);
            }
        };

        // Start listening
        listen(listener);
    }
    
    /**
     * Prints help and exits.
     */
    private static void printHelpAndExit() {
        String jarName = "acr122rw.jar";
        
        StringBuilder sb = new StringBuilder("Usage: java -jar ");
        sb.append(jarName).append(" [option]\n");
        
        sb.append("Options:\n");
        sb.append("\t-h, --help\t\t\tshow this help message and exit\n");
        sb.append("\t-d, --dump [KEYS...]\t\tdump Mifare Classic 1K cards using KEYS\n");
        sb.append("\t-w, --write S B KEY DATA\twrite DATA to sector S, block B of Mifare Classic 1K cards using KEY\n");
        
        sb.append("Examples:\n");
        sb.append("\tjava -jar ").append(jarName).append(" --dump FF00A1A0B000 FF00A1A0B001 FF00A1A0B099\n");
        sb.append("\tjava -jar ").append(jarName).append(" --write 13 2 FF00A1A0B001 FFFFFFFFFFFF00000000060504030201");
        
        System.out.println(sb.toString());
        
        System.exit(0);
    }
}

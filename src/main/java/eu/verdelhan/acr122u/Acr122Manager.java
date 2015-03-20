package eu.verdelhan.acr122u;

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
    
    private static final Acr122Device acr122 = new Acr122Device();
    
    /**
     * 
     * @param args
     * @throws IOException 
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
        System.out.println("PRINT HELP");
        System.exit(0);
    }
}

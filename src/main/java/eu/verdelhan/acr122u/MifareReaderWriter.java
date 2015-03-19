package eu.verdelhan.acr122u;

import static eu.verdelhan.acr122u.HexUtils.bytesToHexString;
import static eu.verdelhan.acr122u.HexUtils.hexStringToBytes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nfctools.mf.MfAccess;
import org.nfctools.mf.MfCardListener;
import org.nfctools.mf.MfReaderWriter;
import org.nfctools.mf.block.DataBlock;
import org.nfctools.mf.block.MfBlock;
import org.nfctools.mf.card.MfCard;
import org.nfctools.mf.classic.Key;
import org.nfctools.spi.acs.Acr122ReaderWriter;
import org.nfctools.spi.acs.AcsTerminal;
import org.nfctools.utils.CardTerminalUtils;

/**
 * Test of Mifare Reader/Writer
 */
public class MifareReaderWriter {
    
    private static final AcsTerminal terminal = new AcsTerminal();
    static {
        terminal.setCardTerminal(CardTerminalUtils.getTerminalByName("ACS ACR122"));
    }
    private static final MfReaderWriter readerWriter = new Acr122ReaderWriter(terminal);

    private static final byte[] KEY_A_VALUE = hexStringToBytes("a0a1a2a3a4a5");
    private static final byte[] KEY_B_VALUE = hexStringToBytes("bbbbbbbbbbbb");
   
    private static final String DATA_STRING = "00000000000000000000000000000000";
    private static final byte[] DATA_BYTES = hexStringToBytes(DATA_STRING);
    
    private static class Mifare1KCardListener implements MfCardListener {
        
        @Override
        public void cardDetected(MfCard mfCard, MfReaderWriter mfReaderWriter) throws IOException {
            System.out.println("Card detected: " + mfCard);
            System.out.println("Reading block 0, sector 0...");
            MfAccess accessBlock0Sector0 = new MfAccess(mfCard, 0, 0, Key.A, KEY_A_VALUE);
            MfBlock block = readerWriter.readBlock(accessBlock0Sector0)[0];
            System.out.println("Data:");
            System.out.println(bytesToHexString(block.getData()));
            
            System.out.println("Writing block 2, sector 11...");
            MfAccess accessBlock46 = new MfAccess(mfCard, 11, 2, Key.B, KEY_B_VALUE);
            readerWriter.writeBlock(accessBlock46, new DataBlock(DATA_BYTES));
            System.out.println("Data:");
            System.out.println(DATA_STRING);
            
            System.out.println("VS");
            System.out.println(bytesToHexString(readerWriter.readBlock(accessBlock46)[0].getData()));
        }
        
    }
    
    /**
     * 
     * --dump(-d)
     * --key(-k) 0102030405
     * --write(-w) 12 02 
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        terminal.open();
        System.out.println("Listening for cards...");
        readerWriter.setCardListener(new Mifare1KCardListener());
        System.out.println("Press ENTER to end listening");
        System.in.read();
        terminal.close();
        
    }
    
    private static void processDumpCommand(String... args) {
        List<String> keys = new ArrayList<String>();
        for (String k : args) {
            if (MifareUtils.isValidMifareClassic1KKey(k)) {
                keys.add(k);
            }
        }
        keys.addAll(MifareUtils.COMMON_MIFARE_CLASSIC_1K_KEYS);
        
        
    }
    
    private static void processWriteCommand(String... args) {
        if (args.length != 4) {
            // print help
            // exit
        }
        
        String sectorId = args[0];
        String blockId = args[1];
        String key = args[2];
        String data = args[3];
        
        if (!MifareUtils.isValidMifareClassic1KSectorIndex(sectorId)
                || !MifareUtils.isValidMifareClassic1KBlockIndex(blockId)
                || !MifareUtils.isValidMifareClassic1KKey(key)
                || !HexUtils.isHexString(data)) {
            // printHelp
        }
        
        
        
    }
    
    private static void processArguments(String... args) {
        if (args == null || args.length == 0) {
            // printHelp
            // exit
        }
        
        String option = args[0];
        
        switch (option) {
            case "-d":
            case "--dump":
                // Dump card
                
                break;
            case "-w":
            case "--write":
                // Write block
                break;
            case "-h":
            case "--help":
                // printHelp
            default:
                // Help
                break;
        }
    }
}

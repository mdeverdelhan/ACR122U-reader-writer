package eu.verdelhan.acr122u;

import static eu.verdelhan.acr122u.HexUtils.bytesToHexString;
import static eu.verdelhan.acr122u.HexUtils.hexStringToBytes;
import java.io.IOException;

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
}

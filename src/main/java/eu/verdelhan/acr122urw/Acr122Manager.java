/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Marc de Verdelhan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package eu.verdelhan.acr122urw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.smartcardio.CardException;

import org.nfctools.mf.MfCardListener;
import org.nfctools.mf.MfReaderWriter;
import org.nfctools.mf.card.MfCard;

/**
 * Entry point of the program.
 * <p>
 * Manager for an ACR122 reader/writer.
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
     * Prints information about a card.
     * @param card a card
     */
    private static void printCardInfo(MfCard card) {
        System.out.println("Card detected: "
                + card.getTagType().toString() + " "
                + card.toString());
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
        
        acr122.listen(listener);
        System.out.println("Press ENTER to exit");
        System.in.read();
        
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
            String k = args[i].toUpperCase();
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
                printCardInfo(mfCard);
                try {
                    MifareUtils.dumpMifareClassic1KCard(mfReaderWriter, mfCard, keys);
                } catch (CardException ce) {
                    System.out.println("Card removed or not present.");
                }
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
        final String key = args[3].toUpperCase();
        final String data = args[4].toUpperCase();
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
                printCardInfo(mfCard);
                try {
                    MifareUtils.writeToMifareClassic1KCard(mfReaderWriter, mfCard, sectorId, blockId, key, data);
                } catch (CardException ce) {
                    System.out.println("Card removed or not present.");
                }
            }
        };

        // Start listening
        listen(listener);
    }
    
    /**
     * Prints help and exits.
     */
    private static void printHelpAndExit() {
        String jarName = "acr122urw.jar";
        
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

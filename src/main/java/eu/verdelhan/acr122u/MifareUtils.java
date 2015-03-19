
package eu.verdelhan.acr122u;

import static eu.verdelhan.acr122u.HexUtils.bytesToHexString;
import static eu.verdelhan.acr122u.HexUtils.hexStringToBytes;
import static eu.verdelhan.acr122u.HexUtils.isHexString;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.nfctools.mf.MfException;
import org.nfctools.mf.block.BlockResolver;
import org.nfctools.mf.block.MfBlock;
import org.nfctools.mf.classic.Key;
import org.nfctools.mf.classic.KeyValue;
import org.nfctools.mf.classic.MemoryLayout;
import org.nfctools.mf.classic.MfClassicAccess;
import org.nfctools.mf.classic.MfClassicReaderWriter;

/**
 * Mifare utility class.
 */
public final class MifareUtils {

    /** Mifare Classic 1K sector count */
    public static final int MIFARE_1K_SECTOR_COUNT = 16;
    
    /** Mifare Classic 1K block count (per sector) */
    public static final int MIFARE_1K_PER_SECTOR_BLOCK_COUNT = 4;
    
    /** Common Mifare Classic 1K keys */
    public static final List<String> COMMON_MIFARE_CLASSIC_1K_KEYS = Arrays.asList(
        "001122334455",
        "A0A1A2A3A4A5",
        "B0B1B2B3B4B5",
        "AAAAAAAAAAAA",
        "BBBBBBBBBBBB",
        "AABBCCDDEEFF",
        "FFFFFFFFFFFF"
    );
    
    private MifareUtils() {
    }
    
    /**
     * @param s a string
     * @return true if the provided string is a valid Mifare Classic 1K key, false otherwise
     */
    public static boolean isValidMifareClassic1KKey(String s) {
        return isHexString(s) && (s.length() == 12);
    }
    
    /**
     * @param s a string
     * @return true if the provided string is a valid Mifare Classic 1K sector index, false otherwise
     */
    public static boolean isValidMifareClassic1KSectorIndex(String s) {
        try {
            int sectorIndex = Integer.parseInt(s);
            return sectorIndex >= 0 && sectorIndex < MIFARE_1K_SECTOR_COUNT;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    /**
     * @param s a string
     * @return true if the provided string is a valid Mifare Classic 1K block index, false otherwise
     */
    public static boolean isValidMifareClassic1KBlockIndex(String s) {
        try {
            int sectorIndex = Integer.parseInt(s);
            return sectorIndex >= 0 && sectorIndex < MIFARE_1K_PER_SECTOR_BLOCK_COUNT;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    /**
     * Dumps a Mifare Classic 1K card.
     * @param reader the reader
     * @param keys the keys to be tested for reading
     */
    public static void dumpMifareClassic1KCard(MfClassicReaderWriter reader, String... keys) {
        for (int sectorIndex = 0; sectorIndex < MIFARE_1K_SECTOR_COUNT; sectorIndex++) {
            // For each sector...
            for (int blockIndex = 0; blockIndex < MIFARE_1K_PER_SECTOR_BLOCK_COUNT; blockIndex++) {
                // For each block...
                dumpMifareClassic1KBlock(reader, sectorIndex, blockIndex, keys);
            }
        }
    }
    
    /**
     * Write data to a Mifare Classic 1K card.
     * @param reader the reader
     * @param sectorId the sector to be written
     * @param blockId the block to be written
     * @param key the key to be used for writing
     * @param dataString the data hex string to be written
     */
    public static void writeToMifareClassic1KCard(MfClassicReaderWriter reader, int sectorId, int blockId, String key, String dataString) {
        if (!isValidMifareClassic1KKey(key)) {
            System.out.println("The key " + key + "is not valid.");
            return;
        }
        if (!isHexString(dataString)) {
            System.out.println(dataString + " is not an hex string.");
            return;
        }
        
        byte[] keyBytes = hexStringToBytes(key);
        // Reading with key A
        MfClassicAccess access = new MfClassicAccess(new KeyValue(Key.A, keyBytes), sectorId, blockId);
        String blockData = readMifareClassic1KBlock(reader, access);
        if (blockData == null) {
            // Reading with key B
            access = new MfClassicAccess(new KeyValue(Key.B, keyBytes), sectorId, blockId);
            blockData = readMifareClassic1KBlock(reader, access);
        }
        System.out.print("Old block data: ");
        if (blockData == null) {
            // Failed to read block
            System.out.println("<Failed to read block>");
        } else {
            // Block read
            System.out.println(blockData + " (Key " + access.getKeyValue().getKey() + ": " + key + ")");

            // Writing with same key
            boolean written = false;
            try {
                byte[] data = hexStringToBytes(dataString);
                MfBlock block = BlockResolver.resolveBlock(MemoryLayout.CLASSIC_1K, sectorId, blockId, data);
                written = writeMifareClassic1KBlock(reader, access, block);
            } catch (MfException me) {
                System.out.println(me.getMessage());
            }
            if (written) {
                blockData = readMifareClassic1KBlock(reader, access);
                System.out.print("New block data: ");
                if (blockData == null) {
                    // Failed to read block
                    System.out.println("<Failed to read block>");
                } else {
                    // Block read
                    System.out.println(blockData + " (Key " + access.getKeyValue().getKey() + ": " + key + ")");
                }
            }
        }
    }
    
    /**
     * Reads a Mifare Classic 1K block.
     * @param reader the reader
     * @param access the access
     * @return a string representation of the block data, null if the block can't be read
     */
    private static String readMifareClassic1KBlock(MfClassicReaderWriter reader, MfClassicAccess access) {
        String data = null;
        try {
            MfBlock block = reader.readBlock(access)[0];
            data = bytesToHexString(block.getData());
        } catch (IOException ioe) {
        }
        return data;
    }
    
    /**
     * Writes a Mifare Classic 1K block.
     * @param reader the reader
     * @param access the access
     * @param block the block to be written
     * @return true if the block has been written, false otherwise
     */
    private static boolean writeMifareClassic1KBlock(MfClassicReaderWriter reader, MfClassicAccess access, MfBlock block) {
        boolean written = false;
        try {
            reader.writeBlock(access, block);
            written = true;
        } catch (IOException ioe) {
        }
        return written;
    }
    
    /**
     * Dumps Mifare Classic 1K block data.
     * @param reader the reader
     * @param sectorId the sector to be read
     * @param blockId the block to be read
     * @param keys the keys to be tested for reading
     */
    private static void dumpMifareClassic1KBlock(MfClassicReaderWriter reader, int sectorId, int blockId, String... keys) {
        System.out.print("Sector " + sectorId + " block " + blockId + ": ");
        for (String key : keys) {
            // For each provided key...
            if (isValidMifareClassic1KKey(key)) {
                byte[] keyBytes = hexStringToBytes(key);
                // Reading with key A
                MfClassicAccess access = new MfClassicAccess(new KeyValue(Key.A, keyBytes), sectorId, blockId);
                String blockData = readMifareClassic1KBlock(reader, access);
                if (blockData == null) {
                    // Reading with key B
                    access = new MfClassicAccess(new KeyValue(Key.B, keyBytes), sectorId, blockId);
                    blockData = readMifareClassic1KBlock(reader, access);
                }
                if (blockData != null) {
                    // Block read
                    System.out.println(blockData + " (Key " + access.getKeyValue().getKey() + ": " + key + ")");
                    return;
                }
            }
        }
        // All keys tested, failed to read block
        System.out.println("<Failed to read block>");
    }
}
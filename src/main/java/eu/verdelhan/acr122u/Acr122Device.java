
package eu.verdelhan.acr122u;

import java.io.IOException;
import javax.smartcardio.CardTerminal;
import org.nfctools.spi.acs.Acr122ReaderWriter;
import org.nfctools.spi.acs.AcsTerminal;
import org.nfctools.utils.CardTerminalUtils;

/**
 * An ACR122 device.
 */
public class Acr122Device extends AcsTerminal {

    /** The ACR122 reader/writer */
    private Acr122ReaderWriter readerWriter;
    
    /**
     * Constructor.
     */
    public Acr122Device() {
        CardTerminal terminal = CardTerminalUtils.getTerminalByName("ACR122");
        setCardTerminal(terminal);
        readerWriter = new Acr122ReaderWriter(this);
    }

    /**
     * @return the ACR122 reader/writer
     */
    public Acr122ReaderWriter getReaderWriter() {
        return readerWriter;
    }

    @Override
    public void open() throws IOException {
        System.out.println("Opening device");
        super.open();
    }

    @Override
    public void startListening() {
        System.out.println("Listening for cards...");
        super.startListening();
    }   
    
    @Override
    public void stopListening() {
        System.out.println("End listening");
        super.stopListening();
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing device");
        super.close();
    }
}
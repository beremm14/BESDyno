package serial;

import logging.Logger;
import java.util.zip.CRC32;

/**
 *
 * @author emil
 */
public class CRC {

    private static final Logger LOG = Logger.getLogger(CRC.class.getName());

    public boolean checkCRC(String res) {
        CRC32 crc = new CRC32();
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        
        String toCheck[] = null;
        try {
        toCheck = response.split(">");
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
        }

        byte[] b = toCheck[0].trim().getBytes();
        crc.update(b);
        long checksum = crc.getValue();

        return checksum == Long.parseLong(toCheck[1]);
    }

    public String removeCRC(String s) {
        String rv = null;
        try {
            String[] value = s.split(">");
            rv = value[0].replaceAll(":", "");
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
        }

        return rv;
    }

    public long calcCRC(String res) {
        CRC32 crc = new CRC32();
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");

        String toCheck[] = response.split(">");

        byte[] b = toCheck[0].trim().getBytes();
        crc.update(b);
        return crc.getValue();
    }

    public long getSentCRC(String res) {
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        String value[];
        try {
            value = response.split(">");
            return Long.parseLong(value[1]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
            return 0;
        }
    }
    
}

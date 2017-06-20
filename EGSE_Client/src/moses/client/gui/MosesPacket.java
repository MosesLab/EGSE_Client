package moses.client.gui;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.util.Date;

/**
 * Packet Class for Moses EGSE Software
 *
 * Packet objects can be created by passing in an array of bytes to the
 * constructor (which will be parsed into individual fields by the constructor)
 * or by passing in specific fields to the second constructor (which will build
 * a byte array to be sent).
 *
 * @author Matthew Handley
 */
class MosesPacket {

    /* Define delimiters and main packet types. */
    public static final String StartDelimiter = "%%%%%%%%%%",
            StopDelimiter = "^";

    public static final char TYPE_MAIN_TIMER = 'T',
            TYPE_MAIN_UPLINK = 'U',
            TYPE_MAIN_GOODACK = 'G',
            TYPE_MAIN_BADACK = 'B',
            TYPE_MAIN_SHELL = 'S',
            TYPE_MAIN_POWER = 'P',
            TYPE_MAIN_MDAQ_U = 'M', // MDAQ Up
            TYPE_MAIN_MDAQ_D = 'Q', // MDAQ Down
            TYPE_MAIN_HK_U = 'H', // HK Up
            TYPE_MAIN_HK_D = 'K';   // HK Down

    /* Store packet main types in array, so we can iterate through them. */
    public static final char[] MainTypes = {
        TYPE_MAIN_TIMER,
        TYPE_MAIN_UPLINK,
        TYPE_MAIN_GOODACK,
        TYPE_MAIN_BADACK,
        TYPE_MAIN_SHELL,
        TYPE_MAIN_POWER,
        TYPE_MAIN_MDAQ_U,
        TYPE_MAIN_MDAQ_D,
        TYPE_MAIN_HK_U,
        TYPE_MAIN_HK_D
    };

    /* lookup table for encoding/decoding parity bits */
    private static int[] lookupTable = null;

    /* All the fields of the packet broken out into individual variables */
    private int[] packetTimeStamp = new int[3];   // the packet's time stamp {HH, MM, SS}
    private char packetType;                      // the packet's main type field
    private String packetSubType;                 // the packet's sub type field    
    private int packetDataLength;                 // the length (in bytes) of the data
    private byte[] packetData;                    // the packet's data
    private byte[] packetBytes;                   // the whole packet in byte array form
    private String packetString;                  // the whole packet in human readable string
    private boolean validCheckSum = false, // only true if this packet has a good check sum
            hasStartDelimiter = false, // only true if packet's first byte is the proper delimiter
            hasStopDelimiter = false;      // only true if packet's last byte is the proper delimiter
    /**
     * Constructor for the Packet Class. Takes an array of bytes (must be in the
     * form of a Moses Packet) and parses it into individual fields.
     *
     * @param in_packet A Moses packet in the form of a byte array.
     */

    /* Start and Stop Delimiters are now strings, so they need to be converted to char arrays
     */
    public static final char[] charstart = StartDelimiter.toCharArray();
    public static final char[] charstop = StopDelimiter.toCharArray();

    public MosesPacket(byte[] in_packet) {
        /* make lookup table if it's not already done */
        if (lookupTable == null) {
            buildLookUpTable();
        }

        /* Save the original byte array of the packet */
        packetBytes = in_packet;

        /* Check for Start and Stop Delimiters (v2) */
        hasStartDelimiter = true;
        for (int i = 0; i < charstart.length; i++) {
            if (charstart[i] != packetBytes[i]) {
                hasStartDelimiter = false;
                break;
            }
        }

        int offset;
        offset = packetBytes.length - charstop.length;
        hasStopDelimiter = true;
        for (int i = 0; i < charstop.length; i++) {
            if (charstop[i] != packetBytes[i + offset]) {
                hasStopDelimiter = false;
                break;
            }
        }

//        /* Check for Start and Stop Delimiters */
//        if(packetBytes[0] == StartDelimiter)
//            hasStartDelimiter = true;  
//        
//        if(packetBytes[packetBytes.length -1] == StopDelimiter)
//            hasStopDelimiter = true;  
        /* Parse Time Stamp */
        packetTimeStamp[0] = (packetBytes[charstart.length + 0] - 48) * 10 + (packetBytes[charstart.length + 1] - 48); // hours
        packetTimeStamp[1] = (packetBytes[charstart.length + 2] - 48) * 10 + (packetBytes[charstart.length + 3] - 48); // minutes
        packetTimeStamp[2] = (packetBytes[charstart.length + 4] - 48) * 10 + (packetBytes[charstart.length + 5] - 48); // seconds

        /* Parse Type */
        packetType = (char) packetBytes[charstart.length + 6];

        /* Parse Sub Type */
        packetSubType = (new StringBuilder().append((char) packetBytes[charstart.length + 7])).toString();
        packetSubType += (new StringBuilder().append((char) packetBytes[charstart.length + 8])).toString();
        packetSubType += (new StringBuilder().append((char) packetBytes[charstart.length + 9])).toString();

        /* Parse Length */
        String packetLengthStr = (new StringBuilder().append((char) packetBytes[charstart.length + 10])).toString();
        packetLengthStr += (new StringBuilder().append((char) packetBytes[charstart.length + 11])).toString();
        packetDataLength = Integer.parseInt(packetLengthStr, 16);

        /* Parse Data */
        packetData = new byte[packetDataLength];
        for (int i = 0; i < packetDataLength; i++) {
            packetData[i] = packetBytes[charstart.length + 12 + i];
        }

//        /* encode packet bytes */
//        for(int i = 1; i < packetBytes.length -2; i++)
//        {
//            packetBytes[i] = (byte)encode((char)packetBytes[i]);
//        }
        /* convert packet to string */
        packetString = "";
        for (byte b : packetBytes) {
            packetString += decode((char) b);
        }

        /* Check for a valid check sum */
        checkCheckSum();

    }

    /**
     * Constructor for the Packet Class. Takes the given parameters and builds a
     * Moses-formatted byte array packet.
     *
     * @param in_type The packet's main type. (ie Packet.TYPE_MAIN_TIMER,
     * Packet.TYPE_MAIN_...)
     * @param in_subType The packet's sub type (ie DK2, DK4...)
     * @param in_data
     */
    public MosesPacket(char in_type, String in_subType, byte[] in_data) {
        if (in_data == null || in_data.length == 0) {
            in_data = new byte[]{0x00};
        }

        /* make lookup table if it's not already done */
        if (lookupTable == null) {
            buildLookUpTable();
        }

        /* House keeping stuff */
        hasStartDelimiter = true;
        hasStopDelimiter = true;
        validCheckSum = true;

        /* Grab current time */
        Date now = new Date();
        packetTimeStamp[0] = now.getHours();
        packetTimeStamp[1] = now.getMinutes();
        packetTimeStamp[2] = now.getSeconds();

        packetData = in_data;
        packetType = in_type;
        packetSubType = in_subType;

        /* Get the data's length so we can make the packet array*/
        packetDataLength = in_data.length;
        packetBytes = new byte[13 + charstart.length + charstop.length + packetDataLength];

        for (int i = 0; i < charstart.length; i++) {
            packetBytes[i] = (byte) (charstart[i]);
        }
        int offset;
        offset = packetBytes.length - charstop.length;
        for (int i = 0; i < charstop.length; i++) {
            packetBytes[i + offset] = (byte) (charstop[i]);
        }

        /* Put in the packet time stamp. */
        String timeStampStr = String.format("%02d%02d%02d",
                packetTimeStamp[0],
                packetTimeStamp[1],
                packetTimeStamp[2]);

        packetBytes[charstart.length] = (byte) timeStampStr.charAt(0);
        packetBytes[charstart.length + 1] = (byte) timeStampStr.charAt(1);
        packetBytes[charstart.length + 2] = (byte) timeStampStr.charAt(2);
        packetBytes[charstart.length + 3] = (byte) timeStampStr.charAt(3);
        packetBytes[charstart.length + 4] = (byte) timeStampStr.charAt(4);
        packetBytes[charstart.length + 5] = (byte) timeStampStr.charAt(5);

        /* Put in the packet type and sub type. */
        packetBytes[charstart.length + 6] = (byte) in_type;
        packetBytes[charstart.length + 7] = (byte) in_subType.charAt(0);
        packetBytes[charstart.length + 8] = (byte) in_subType.charAt(1);
        packetBytes[charstart.length + 9] = (byte) in_subType.charAt(2);

        /* Put in the data field's length */
        String lengthStr = String.format("%02x", packetDataLength);
        packetBytes[charstart.length + 10] = (byte) lengthStr.charAt(0);
        packetBytes[charstart.length + 11] = (byte) lengthStr.charAt(1);

        /* Put in the data */
        System.arraycopy(in_data, 0, packetBytes, charstart.length + 12, packetDataLength);

        // Parity Check //
        /* encode packet bytes */
        for (int i = 0; i < packetBytes.length - 1; i++) {
            packetBytes[i] = (byte) encode((char) packetBytes[i]);
        }

        /* Calculate CheckSum */
        packetBytes[packetBytes.length - charstop.length - 1] = generateChecksum();

        /* Convert packet to string format */
        packetString = "";
        for (byte b : packetBytes) {
            packetString += decode((char) b);
        }

    }

    /**
     * Returns whether or not the packet has a start delimiter.
     *
     * @return <tt>true</tt> only if the packet has the proper start delimiter.
     */
    public boolean hasStartDelimiter() {
        return hasStartDelimiter;
    }

    /**
     * Returns whether or not the packet has a stop delimiter.
     *
     * @return <tt>true</tt> only if the packet has the proper stop delimiter.
     */
    public boolean hasStopDelimiter() {
        return hasStopDelimiter;
    }

    /**
     * Returns whether or not the packet has a valid check sum.
     *
     * @return <tt>true</tt> only if the packet has a valid check sum.
     */
    public boolean hasGoodCheckSum() {
        return validCheckSum;
    }

    /**
     * Returns the packet's main type in char form.
     *
     * The char can be compared to the TYPE_MAIN_... public final variables of
     * this class.
     *
     * @return The packet's Main type.
     */
    public char getType() {
        return packetType;
    }

    /**
     * @return The packet's sub type.
     */
    public String getSubType() {
        return packetSubType;
    }

    /**
     * @return Returns the length of the packet's data field.
     */
    public int length() {
        return packetDataLength;
    }

    /**
     * @return Returns the packet's data field.
     */
    public byte[] getData() {
        return packetData;
    }

    /**
     * @return Returns the entire packet in byte array format.
     */
    public byte[] getBytes() {
        return packetBytes;
    }

    /**
     * @return Returns the entire packet ASCII string form.
     */
    @Override
    public String toString() {
        return packetString;
    }

    /**
     * @return Returns the packet's time stamp in string format 'HH:MM:SS'.
     */
    public String getTimeStampStr() {
        return String.format("%02d:%02d:%02d", packetTimeStamp[0], packetTimeStamp[1], packetTimeStamp[2]);
    }

    /**
     * @return Returns the packet's time stamp in int array format [HH, MM, SS].
     */
    public int[] getTimeStamp() {
        return packetTimeStamp;
    }

    /**
     * Generates the checksum for a packet. Should only be called once
     * packetBytes has been filled with
     *
     * @return The check sum
     */
    private void checkCheckSum() {
        char calculatedChecksum = 0;
        int i;

        /* for each byte in the packet, excluding the last 2 */
        for (i = 0; i < packetBytes.length - 2; i++) {
            calculatedChecksum ^= encode(decode((char) packetBytes[i]));
        }
        
        if (decode((char) packetBytes[packetBytes.length - 2]) == decode(calculatedChecksum)) {
            validCheckSum = true;
            System.out.println("The checksum checked out");
        } else {
            validCheckSum = false;
            System.out.println("The checksum did not check out");
            System.out.println("Expected: " + (int) calculatedChecksum);
            System.out.println("Got: " + packetBytes[packetBytes.length - 2]);
            System.out.println("");
        }
    }

    private byte generateChecksum() {
        int i;
        char calculatedChecksum = encode(decode((char) packetBytes[0]));

        for (i = 1; i < packetBytes.length - 2; i++) {
            calculatedChecksum ^= packetBytes[i];
        }

//   	calculatedChecksum ^= packetType;
//    
//   	for(i = 8; i < packetBytes.length - 2; i++)
//        {
//            calculatedChecksum ^= packetBytes[i];
//        }
        return (byte) calculatedChecksum;
    }

    static char encode(char dataByte) {
        return (char) lookupTable[dataByte];
    }

    static char decode(char dataByte) {
        return (char) (dataByte & 0x7F);
    }

    private void buildLookUpTable() {

        lookupTable = new int[128];

        for (int j = 0; j < 128; j++) {
            char sum = 0;

            for (int i = 0; i < 8; i++) {
                sum += (j << i) & 1;	//Calculate parity bit
            }
            lookupTable[j] = (sum % 2 == 0) ? j & 0x7F : j | 0x80;
        }
    }
}

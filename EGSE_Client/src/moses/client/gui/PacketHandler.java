/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moses.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTextArea;

/**
 *
 * @author SSEL
 */
public class PacketHandler {

    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    static SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    static SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    static SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
    static SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
    static SimpleDateFormat secondFormat = new SimpleDateFormat("ss");
    static SimpleDateFormat millisecondFormat = new SimpleDateFormat("SSS");

    /**
     * This Method will take a packet and populate the correct field in the
     * parent MosesClientForm form.
     *
     * @param parent The MosesClientFrom which called this method.
     * @param packet The packet to handle.
     * @return Returns true if packet has handled, false otherwise.
     */
    public static boolean HandlePacket(MosesClientForm parent, MosesPacket packet) {
        switch (packet.getType()) {
            case MosesPacket.TYPE_MAIN_TIMER:
                return HandleTimer(parent, packet);

            case MosesPacket.TYPE_MAIN_UPLINK:
                return HandleUplink(parent, packet);

            case MosesPacket.TYPE_MAIN_GOODACK:
                return HandleGACK(parent, packet);

            case MosesPacket.TYPE_MAIN_BADACK:
                return HandleBACK(parent, packet);

            case MosesPacket.TYPE_MAIN_SHELL:
                return HandleShell(parent, packet);

            case MosesPacket.TYPE_MAIN_POWER:
                return HandlePower(parent, packet);

            case MosesPacket.TYPE_MAIN_MDAQ_U:
                return HandleMDAQUplink(parent, packet);

            case MosesPacket.TYPE_MAIN_MDAQ_D:
                return HandleMDAQDownlink(parent, packet);

            case MosesPacket.TYPE_MAIN_HK_U:
                return HandleHKUplink(parent, packet);

            case MosesPacket.TYPE_MAIN_HK_D:
                return HandleHKDownlink(parent, packet);

            default:
                return false;
        }
    }

    private static boolean HandleTimer(MosesClientForm parent, MosesPacket packet) {
        String subType = packet.getSubType();
        JTextArea tta = parent.getTextAreaTimer();

        if (subType.equals("DK2")) {
            tta.append(
                    String.format("%s Dark Sequence 2\n\n",
                            packet.getTimeStampStr()));
            tta.setCaretPosition(tta.getDocument().getLength());
            return true;
        } else if (subType.equals("DK4")) {
            tta.append(
                    String.format("%s Dark Sequence 4\n\n",
                            packet.getTimeStampStr()));
            tta.setCaretPosition(tta.getDocument().getLength());
            return true;
        } else if (subType.equals("DSP")) {
            tta.append(
                    String.format("%s Data Stop\n\n",
                            packet.getTimeStampStr()));
            tta.setCaretPosition(tta.getDocument().getLength());
            return true;
        } else if (subType.equals("DST")) {
            tta.append(
                    String.format("%s Data Start\n\n",
                            packet.getTimeStampStr()));
            tta.setCaretPosition(tta.getDocument().getLength());
            return true;
        } else if (subType.equals("SLP")) {
            tta.append(
                    String.format("%s Sleep\n\n",
                            packet.getTimeStampStr()));
            tta.setCaretPosition(tta.getDocument().getLength());
            return true;
        } else {
            return false;
        }
    }

    private static boolean HandleUplink(MosesClientForm parent, MosesPacket packet) {
        String subType = packet.getSubType();
        JTextArea tta = parent.getTextAreaTimer();

        if (subType.equals("DST")) {
            tta.append(
                    String.format("%s Data Start\n\n",
                            packet.getTimeStampStr()));
            tta.setCaretPosition(tta.getDocument().getLength());
            return true;
        } else if (subType.equals("DSP")) {
            tta.append(
                    String.format("%s Data Stop\n\n",
                            packet.getTimeStampStr()));
            tta.setCaretPosition(tta.getDocument().getLength());
            return true;
        } else {
            return false;
        }
    }

    private static boolean HandleGACK(MosesClientForm parent, MosesPacket packet) {
        /* convert packet's data into string */
        String dataStr = "";
        byte[] data = packet.getData();

        for (int i = 0; i < data.length; i++) {
            dataStr += (char) data[i];
        }

        /* create string to put in text field */
        String textStr = String.format("Good %s at: %s",
                dataStr,
                packet.getTimeStampStr());

        /* determine which text field to put textStr into */
        switch (dataStr.charAt(0)) {
            case 'P':
                parent.getFieldPowerLastAck().setText(textStr);
                parent.getFieldPowerLastAck().setForeground(Color.green);
                return true;

            case 'H':
                parent.getFieldHKLastAck().setText(textStr);
                parent.getFieldHKLastAck().setForeground(Color.green);
                return true;

            case 'M':
                parent.getFieldMDAQLastAck().setText(textStr);
                parent.getFieldPowerLastAck().setForeground(Color.green);
                return true;

            case 'T':
                parent.getFieldTimerLastAck().setText(textStr);
                parent.getFieldTimerLastAck().setForeground(Color.green);
                return true;

            case 'U':
                parent.getFieldTimerLastAck().setText(textStr);
                parent.getFieldTimerLastAck().setForeground(Color.green);
                return true;

            default:
                return false;
        }
    }

    private static boolean HandleBACK(MosesClientForm parent, MosesPacket packet) {
        /* convert packet's data into string */
        String dataStr = "";
        byte[] data = packet.getData();

        for (int i = 0; i < data.length; i++) {
            dataStr += (char) data[i];
        }

        /* create string to put in text field */
        String textStr = String.format("Bad %s at: %s",
                dataStr,
                packet.getTimeStampStr());

        /* determine which text field to put textStr into */
        switch (dataStr.charAt(0)) {
            case 'P':
                parent.getFieldPowerLastAck().setText(textStr);
                parent.getFieldPowerLastAck().setForeground(Color.red);
                return true;

            case 'H':
                parent.getFieldHKLastAck().setText(textStr);
                parent.getFieldHKLastAck().setForeground(Color.red);
                return true;

            case 'M':
                parent.getFieldMDAQLastAck().setText(textStr);
                parent.getFieldPowerLastAck().setForeground(Color.red);
                return true;

            case 'T':
                parent.getFieldTimerLastAck().setText(textStr);
                parent.getFieldTimerLastAck().setForeground(Color.red);
                return true;

            case 'U':
                parent.getFieldTimerLastAck().setText(textStr);
                parent.getFieldTimerLastAck().setForeground(Color.red);
                return true;

            default:
                return false;
        }
    }

    private static boolean HandleShell(MosesClientForm parent, MosesPacket packet) {
        String subType = packet.getSubType(),
                dataStr = "";
        byte[] data = packet.getData();

        for (int i = 0; i < data.length; i++) {
            dataStr += (char) data[i];
        }

        if (subType.equals("OUT")) {
            try {
                MosesClientForm.xterm_writer.write(dataStr);
                MosesClientForm.xterm_writer.flush();
//            parent.getTextAreaShellRX().append(dataStr + "\n");
//            parent.getTextAreaShellRX().setCaretPosition(
//                    parent.getTextAreaShellRX().getText().length());
            } catch (IOException e) {
                System.out.println("No xterm process");
            }
            return true;
        } else if (subType.equals("ACK")) {
            parent.getTextAreaShellRX().append(dataStr + "\n");
            parent.getTextAreaShellRX().setCaretPosition(
                    parent.getTextAreaShellRX().getText().length());
            return true;
        }
        return false;
    }

    private static boolean HandlePower(MosesClientForm parent, MosesPacket packet) {
        String subType = packet.getSubType(),
                dataStr = "";
        byte[] data = packet.getData();

        for (int i = 0; i < data.length; i++) {
            dataStr += (char) data[i];
        }

        if (subType.equals("SON")) {
            if (dataStr.equals("01")) {
                parent.getFeildPS_1().setText("On");
                return true;
            } else if (dataStr.equals("02")) {
                parent.getFieldPS_2().setText("On");
                return true;
            } else if (dataStr.equals("03")) {
                parent.getFieldPS_3().setText("On");
                return true;
            } else if (dataStr.equals("04")) {
                parent.getFieldPS_4().setText("On");
                return true;
            } else if (dataStr.equals("05")) {
                parent.getFieldPS_5().setText("On");
                return true;
            } else if (dataStr.equals("06")) {
                parent.getFieldPS_6().setText("On");
                return true;
            } else if (dataStr.equals("07")) {
                parent.getFieldPS_7().setText("On");
                return true;
            } else if (dataStr.equals("08")) {
                parent.getFieldPS_8().setText("On");
                return true;
            } else if (dataStr.equals("09")) {
                parent.getFieldPS_9().setText("On");
                return true;
            } else if (dataStr.equals("0A")) {
                parent.getFieldPS_10().setText("On");
                return true;
            } else {
                return false;
            }

        } else if (subType.equals("SOF")) {
            if (dataStr.equals("01")) {
                parent.getFeildPS_1().setText("Off");
                return true;
            } else if (dataStr.equals("02")) {
                parent.getFieldPS_2().setText("Off");
                return true;
            } else if (dataStr.equals("03")) {
                parent.getFieldPS_3().setText("Off");
                return true;
            } else if (dataStr.equals("04")) {
                parent.getFieldPS_4().setText("Off");
                return true;
            } else if (dataStr.equals("05")) {
                parent.getFieldPS_5().setText("Off");
                return true;
            } else if (dataStr.equals("06")) {
                parent.getFieldPS_6().setText("Off");
                return true;
            } else if (dataStr.equals("07")) {
                parent.getFieldPS_7().setText("Off");
                return true;
            } else if (dataStr.equals("08")) {
                parent.getFieldPS_8().setText("Off");
                return true;
            } else if (dataStr.equals("09")) {
                parent.getFieldPS_9().setText("Off");
                return true;
            } else if (dataStr.equals("0A")) {
                parent.getFieldPS_10().setText("Off");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean HandleMDAQUplink(MosesClientForm parent, MosesPacket packet) {
        return false; // we should never recieve an MDAQ Uplink command from F/C
    }

    private static boolean HandleMDAQDownlink(MosesClientForm parent, MosesPacket packet) {
        JTextArea mta = parent.getTextAreaMDAQ();
        String subType = packet.getSubType(),
                dataStr = "";
        byte[] data = packet.getData();

        for (int i = 0; i < data.length; i++) {
            dataStr += (char) data[i];
        }

        if (subType.equals("GSN")) {
            String MDAQString = String.format("%s Get Sequence Name (GSN):\n%s\n\n",
                    packet.getTimeStampStr(),
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GSI")) {
            String MDAQString = String.format("%s Get Sequence Info (GSI):\n%s\n\n",
                    packet.getTimeStampStr(),
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GCS")) {
            String MDAQString = String.format("%s Get Current Sequence (GCS):\n%s\n\n",
                    packet.getTimeStampStr(),
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GFL")) {
            String MDAQString = String.format("%s Get Frame Length (GFL):\n%s\n\n",
                    packet.getTimeStampStr(),
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GFI")) {
            String MDAQString = String.format("%s Get Fram Index (GFI):\n%s\n\n",
                    packet.getTimeStampStr(),
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GOF")) {
            String MDAQString = String.format("%s Get Output Filename (GOF):\n%s\n\n",
                    packet.getTimeStampStr(),
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GST")) {
            String MDAQString = String.format("%s Get Self Status (GST):\n%s\n\n",
                    packet.getTimeStampStr(),
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GTM")) {
            parent.getFieldQGTM().setText(dataStr);
            return true;
        } else if (subType.equals("GC0")) {
            parent.getFieldQGC0().setText(dataStr);
            return true;
        } else if (subType.equals("GPO")) {
            parent.getFieldQGPO().setText(dataStr);
            return true;
        } else if (subType.equals("GSM")) {
            parent.getFieldQGSM().setText(dataStr);
            return true;
        } else if (subType.equals("JMP")) {
            String MDAQString = String.format("%s QJMP: Index does not exist!\n\n",
                    packet.getTimeStampStr());
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("FNJ")) {
            String MDAQString = String.format("%s Index Number Found: %s\n\n",
                    packet.getTimeStampStr(),
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("FNR")) {
            String MDAQString = String.format("%s Replaced Index Numbers: \n%s\n\n",
                    packet.getTimeStampStr(),
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("BSQ")) {
            String MDAQString = String.format("%s Begin Sequenec Confimation (%s)\n\n",
                    packet.getTimeStampStr(),
                    subType);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("ESQ")) {
            String MDAQString = String.format("%s End Sequenec Confimation (%s)\n\n",
                    packet.getTimeStampStr(),
                    subType);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("BRO")) {
            String MDAQString = String.format("%s Begin Read Out (%s)\n\n",
                    packet.getTimeStampStr(),
                    subType);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("ERO")) {
            String MDAQString = String.format("%s End Read Out (%s)\n\n",
                    packet.getTimeStampStr(),
                    subType);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GSH")) {
            String MDAQString = String.format("%s %s\n\n",
                    packet.getTimeStampStr(),
                    getShutterString(dataStr));
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GCN")) {
            String MDAQString = String.format("%s Get Current (Number?) (%s)\n%s\n\n",
                    packet.getTimeStampStr(),
                    subType,
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else if (subType.equals("GCI")) {
            String MDAQString = String.format("%s Get Current (Index?) (%s)\n%s\n\n",
                    packet.getTimeStampStr(),
                    subType,
                    dataStr);
            mta.append(MDAQString);
            mta.setCaretPosition(mta.getDocument().getLength());
            return true;
        } else {
            return false;
        }
    }

    private static boolean HandleHKUplink(MosesClientForm parent, MosesPacket packet) {
        return false; // we should never get an uplink packet from the F/C
    }

    private static boolean HandleHKDownlink(MosesClientForm parent, MosesPacket packet) {
        String subType = packet.getSubType(),
                dataStr = "";
        byte[] data = packet.getData();

        for (int i = 0; i < data.length; i++) {
            dataStr += (char) data[i];
        }

        if (subType.equals("2.5")) {
            if (dataStr.contains("VC")) {
                parent.getFieldkK25V_VC().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("VD")) {
                parent.getFieldkK25V_VD().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("ID")) {
                parent.getFieldkK25V_ID().setText(dataStr.substring(2));
                return true;
            } else {
                return false;
            }
        } else if (subType.equals("+5V")) {
            if (dataStr.contains("VA")) {
                parent.getFieldkKP5V_VA().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("VB")) {
                parent.getFieldkKP5V_VB().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("VC")) {
                parent.getFieldkKP5V_VC().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("VD")) {
                parent.getFieldkKP5V_VD().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("IA")) {
                parent.getFieldkKP5V_IA().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("IB")) {
                parent.getFieldkKP5V_IB().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("ID")) {
                parent.getFieldkKP5V_ID().setText(dataStr.substring(2));
                return true;
            } else {
                return false;
            }
        } else if (subType.equals("-5V")) {
            if (dataStr.contains("VA")) {
                parent.getFieldkKM5V_VA().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("VB")) {
                parent.getFieldkKM5V_VB().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("IA")) {
                parent.getFieldkKM5V_IA().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("IB")) {
                parent.getFieldkKM5V_IB().setText(dataStr.substring(2));
                return true;
            } else {
                return false;
            }
        } else if (subType.equals("12V")) {
            if (dataStr.contains("VA")) {
                parent.getFieldkK12V_VA().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("VB")) {
                parent.getFieldkK12V_VB().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("VC")) {
                parent.getFieldkK12V_VC().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("IA")) {
                parent.getFieldkK12V_IA().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("IB")) {
                parent.getFieldkK12V_IB().setText(dataStr.substring(2));
                return true;
            } else {
                return false;
            }
        } else if (subType.equals("36V")) {
            if (dataStr.contains("VA")) {
                parent.getFieldkK36V_VA().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("VB")) {
                parent.getFieldkK36V_VB().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("IA")) {
                parent.getFieldkK36V_IA().setText(dataStr.substring(2));
                return true;
            } else if (dataStr.contains("IB")) {
                parent.getFieldkK36V_IB().setText(dataStr.substring(2));
                return true;
            } else {
                return false;
            }
        } else if (subType.equals("TMP")) {
            if (dataStr.contains("1")) {
                parent.getFieldkKTMP_1().setText(dataStr.substring(1));
                return true;
            } else if (dataStr.contains("2")) {
                parent.getFieldkKTMP_2().setText(dataStr.substring(1));
                return true;
            } else if (dataStr.contains("3")) {
                parent.getFieldkKTMP_3().setText(dataStr.substring(1));
                return true;
            } else if (dataStr.contains("U")) {
                parent.getFieldkKTMP_U().setText(dataStr.substring(1));
                return true;
            } else if (dataStr.contains("L")) {
                parent.getFieldkKTMP_L().setText(dataStr.substring(1));
                return true;
            } else {
                return false;
            }
        } else if (subType.equals("2.0")) {
            parent.getFieldkK20().setText(dataStr);
            return true;
        } else if (subType.equals("3.3")) {
            parent.getFieldkK33().setText(dataStr);
            return true;
        } else if (subType.equals("AVO")) {
            int dataInt = Integer.parseInt(dataStr, 16);
            parent.getFieldkKAVO().setText(String.format("%d", dataInt));
            return true;
        } else if (subType.equals("AVR")) {
            int dataInt = Integer.parseInt(dataStr, 16);
            parent.getFieldkKAVR().setText(String.format("%d", dataInt));
            return true;
        } else if (subType.equals("AVS")) {
            int dataInt = Integer.parseInt(dataStr, 16);
            parent.getFieldkKAVS().setText(String.format("%d", dataInt));
            return true;
        } else if (subType.equals("BVO")) {
            int dataInt = Integer.parseInt(dataStr, 16);
            parent.getFieldkKBVO().setText(String.format("%d", dataInt));
            return true;
        } else if (subType.equals("BVR")) {
            int dataInt = Integer.parseInt(dataStr, 16);
            parent.getFieldkKBVR().setText(String.format("%d", dataInt));
            return true;
        } else if (subType.equals("BVS")) {
            int dataInt = Integer.parseInt(dataStr, 16);
            parent.getFieldkKBVS().setText(String.format("%d", dataInt));
            return true;
        } else {
            return false;
        }
    }

    private static String getShutterString(String dataStr) {
        String result = "";
        String[] dataSplit;
        long seconds, microSeconds;

        /* determine if this was a shutter open of shutter close event */
        if (dataStr.charAt(0) == 'O' || dataStr.charAt(0) == 'o') {
            result += "Shutter Opened At:\n";
        } else {
            result += "Shutter Closed At:\n";
        }

        dataSplit = dataStr.substring(2, dataStr.indexOf("us")).split("s");

        seconds = Long.parseLong(dataSplit[0]);
        microSeconds = Long.parseLong(dataSplit[1]);

        result += timeStr(seconds, microSeconds);

        return result;
    }

    private static String timeStr(long seconds, long microSeconds) {
        String result = "";

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(seconds * 1000);

        result += yearFormat.format(cal.getTime());
        result += "/";
        result += monthFormat.format(cal.getTime());
        result += "/";
        result += dayFormat.format(cal.getTime());
        result += " ";
        result += hourFormat.format(cal.getTime());
        result += ":";
        result += minuteFormat.format(cal.getTime());
        result += ":";
        result += secondFormat.format(cal.getTime());
        result += ".";
        result += String.format("%06d", microSeconds);

        return result;
    }

}

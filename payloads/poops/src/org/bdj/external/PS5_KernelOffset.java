package org.bdj.external;

import org.bdj.Status;
import java.util.Hashtable;

public class PS5_KernelOffset {

    public static final int FILEDESCENT_SIZE = 0x30;
    public static final int KQ_FDP_OFFSET = 0xA8;
    public static final int PIPE_SIGIO_OFFSET = 0xd8;
    public static final int IN6P_OUTPUTOPTS_OFFSET = 0x120;
    public static final int IP6PO_RHI_RTHDR_OFFSET = 0x70;
    public static final int ROOTVNODE_OFFSET = 0x8;
    public static final int FDT_OFILES_OFFSET = 0x8;
    public static final int P_PID_OFFSET = 0xbc;
    
    private static Hashtable KernelOffsets;
    public static String FW_VERSION;
    
    static {
        initializeOffsets();
    }

    private static void initializeOffsets() {
        KernelOffsets = new Hashtable();

        // ALLPROC, SECURITY_FLAGS, ROOTVNODE, KERNEL_PMAP_STORE, GVMSPACE
        
        // 1.00, 1.01, 1.02
        addFirmwareOffsets("1.00", 0x026D1BF8, 0x06241074, 0x06565540, 0x02F9F2B8, 0x06202E70);
        addFirmwareOffsets("1.01", 0x026D1BF8, 0x06241074, 0x06565540, 0x02F9F2B8, 0x06202E70);
        addFirmwareOffsets("1.02", 0x026D1BF8, 0x06241074, 0x06565540, 0x02F9F2B8, 0x06202E70);

        // 1.05, 1.10, 1.11, 1.12, 1.13, 1.14
        addFirmwareOffsets("1.05", 0x026D1C18, 0x06241074, 0x06565540, 0x02F9F328, 0x06202E70);
        addFirmwareOffsets("1.10", 0x026D1C18, 0x06241074, 0x06565540, 0x02F9F328, 0x06202E70);
        addFirmwareOffsets("1.11", 0x026D1C18, 0x06241074, 0x06565540, 0x02F9F328, 0x06202E70);
        addFirmwareOffsets("1.12", 0x026D1C18, 0x06241074, 0x06565540, 0x02F9F328, 0x06202E70);
        addFirmwareOffsets("1.13", 0x026D1C18, 0x06241074, 0x06565540, 0x02F9F328, 0x06202E70);
        addFirmwareOffsets("1.14", 0x026D1C18, 0x06241074, 0x06565540, 0x02F9F328, 0x06202E70);

        // 2.00, 2.20, 2.25, 2.26, 2.30, 2.50, 2.70
        addFirmwareOffsets("2.00", 0x02701C28, 0x063E1274, 0x067134C0, 0x031338C8, 0x063A2EB0);
        addFirmwareOffsets("2.20", 0x02701C28, 0x063E1274, 0x067134C0, 0x031338C8, 0x063A2EB0);
        addFirmwareOffsets("2.25", 0x02701C28, 0x063E1274, 0x067134C0, 0x031338C8, 0x063A2EB0);
        addFirmwareOffsets("2.26", 0x02701C28, 0x063E1274, 0x067134C0, 0x031338C8, 0x063A2EB0);
        addFirmwareOffsets("2.30", 0x02701C28, 0x063E1274, 0x067134C0, 0x031338C8, 0x063A2EB0);
        addFirmwareOffsets("2.50", 0x02701C28, 0x063E1274, 0x067134C0, 0x031338C8, 0x063A2EB0);
        addFirmwareOffsets("2.70", 0x02701C28, 0x063E1274, 0x067134C0, 0x031338C8, 0x063A2EB0);

        // 3.00, 3.20, 3.21
        addFirmwareOffsets("3.00", 0x0276DC58, 0x06466474, 0x067AB4C0, 0x031BE218, 0x06423F80);
        addFirmwareOffsets("3.20", 0x0276DC58, 0x06466474, 0x067AB4C0, 0x031BE218, 0x06423F80);
        addFirmwareOffsets("3.21", 0x0276DC58, 0x06466474, 0x067AB4C0, 0x031BE218, 0x06423F80);

        // 4.00, 4.02, 4.03, 4.50, 4.51
        addFirmwareOffsets("4.00", 0x027EDCB8, 0x06506474, 0x066E74C0, 0x03257A78, 0x064C3F80);
        addFirmwareOffsets("4.02", 0x027EDCB8, 0x06506474, 0x066E74C0, 0x03257A78, 0x064C3F80);
        addFirmwareOffsets("4.03", 0x027EDCB8, 0x06506474, 0x066E74C0, 0x03257A78, 0x064C3F80);
        addFirmwareOffsets("4.50", 0x027EDCB8, 0x06506474, 0x066E74C0, 0x03257A78, 0x064C3F80);
        addFirmwareOffsets("4.51", 0x027EDCB8, 0x06506474, 0x066E74C0, 0x03257A78, 0x064C3F80);

        // 5.00, 5.02, 5.10
        addFirmwareOffsets("5.00", 0x0291DD00, 0x066466EC, 0x06853510, 0x03398A88, 0x06603FB0);
        addFirmwareOffsets("5.02", 0x0291DD00, 0x066466EC, 0x06853510, 0x03398A88, 0x06603FB0);
        addFirmwareOffsets("5.10", 0x0291DD00, 0x066466EC, 0x06853510, 0x03398A88, 0x06603FB0);

        // 5.50
        addFirmwareOffsets("5.50", 0x0291DD00, 0x066466EC, 0x06853510, 0x03394A88, 0x06603FB0);

        // 6.00, 6.02, 6.50
        addFirmwareOffsets("6.00", 0x02869D20, 0x065968EC, 0x0679F510, 0x032E4358, 0x065540F0);
        addFirmwareOffsets("6.02", 0x02869D20, 0x065968EC, 0x0679F510, 0x032E4358, 0x065540F0);
        addFirmwareOffsets("6.50", 0x02869D20, 0x065968EC, 0x0679F510, 0x032E4358, 0x065540F0);

        // 7.00, 7.01, 7.20, 7.40, 7.60, 7.61
        addFirmwareOffsets("7.00", 0x02859D50, 0x00AC8064, 0x030C7510, 0x02E2C848, 0x02E76090);
        addFirmwareOffsets("7.01", 0x02859D50, 0x00AC8064, 0x030C7510, 0x02E2C848, 0x02E76090);
        addFirmwareOffsets("7.20", 0x02859D50, 0x00AC8064, 0x030C7510, 0x02E2C848, 0x02E76090);
        addFirmwareOffsets("7.40", 0x02859D50, 0x00AC8064, 0x030C7510, 0x02E2C848, 0x02E76090);
        addFirmwareOffsets("7.60", 0x02859D50, 0x00AC8064, 0x030C7510, 0x02E2C848, 0x02E76090);
        addFirmwareOffsets("7.61", 0x02859D50, 0x00AC8064, 0x030C7510, 0x02E2C848, 0x02E76090);

        // 8.00, 8.20, 8.40, 8.60
        addFirmwareOffsets("8.00", 0x02875D50, 0x00AC3064, 0x030FB510, 0x02E48848, 0x02EAA090);
        addFirmwareOffsets("8.20", 0x02875D50, 0x00AC3064, 0x030FB510, 0x02E48848, 0x02EAA090);
        addFirmwareOffsets("8.40", 0x02875D50, 0x00AC3064, 0x030FB510, 0x02E48848, 0x02EAA090);
        addFirmwareOffsets("8.60", 0x02875D50, 0x00AC3064, 0x030FB510, 0x02E48848, 0x02EAA090);

        // 9.00
        addFirmwareOffsets("9.00", 0x02755D50, 0x00D72064, 0x02FDB510, 0x02D28B78, 0x02D8A570);

        // 9.05, 9.20, 9.40, 9.60
        addFirmwareOffsets("9.05", 0x02755D50, 0x00D73064, 0x02FDB510, 0x02D28B78, 0x02D8A570);
        addFirmwareOffsets("9.20", 0x02755D50, 0x00D73064, 0x02FDB510, 0x02D28B78, 0x02D8A570);
        addFirmwareOffsets("9.40", 0x02755D50, 0x00D73064, 0x02FDB510, 0x02D28B78, 0x02D8A570);
        addFirmwareOffsets("9.60", 0x02755D50, 0x00D73064, 0x02FDB510, 0x02D28B78, 0x02D8A570);

        // 10.00, 10.01
        addFirmwareOffsets("10.00", 0x02765D70, 0x00D79064, 0x02FA3510, 0x02CF0EF8, 0x02D52570);
        addFirmwareOffsets("10.01", 0x02765D70, 0x00D79064, 0x02FA3510, 0x02CF0EF8, 0x02D52570);

        // 11.00
        addFirmwareOffsets("11.00", 0x02875D70, 0x00D8C064, 0x030B7510, 0x02E04F18, 0x02E66570);

        // 12.00
        addFirmwareOffsets("12.00", 0x02885E00, 0x00D83064, 0x030D7510, 0x02E1CFB8, 0x02E7E570);

        // untested
        addFirmwareOffsets("13.00", 0x028C5E00, 0x00D99064, 0x03133510, 0x02E74FF8, 0x02ED6570);
        addFirmwareOffsets("13.20", 0x028C5E00, 0x00D99064, 0x03133510, 0x02E74FF8, 0x02ED6570);
    }
    
    private static void addFirmwareOffsets(String fw, int ALLPROC, int SECURITY_FLAGS, int ROOTVNODE, int KERNEL_PMAP_STORE, int GVMSPACE) {
        Hashtable offsets = new Hashtable();
        offsets.put("ALLPROC",           new Long(ALLPROC));
        offsets.put("SECURITY_FLAGS",    new Long(SECURITY_FLAGS));
        offsets.put("ROOTVNODE",         new Long(ROOTVNODE));
        offsets.put("KERNEL_PMAP_STORE", new Long(KERNEL_PMAP_STORE));
        offsets.put("GVMSPACE",          new Long(GVMSPACE));
        KernelOffsets.put(fw, offsets);
    }
    
    public static long getOffset(String key) {
        Hashtable offsets;
    
        if (KernelOffsets.containsKey(FW_VERSION)) {
            offsets = (Hashtable) KernelOffsets.get(FW_VERSION);
        } else {
            String major = FW_VERSION.indexOf('.') != -1 ? FW_VERSION.substring(0, FW_VERSION.indexOf('.')) : FW_VERSION;
            offsets = (Hashtable) KernelOffsets.get(major + ".00");
        }
    
        if (offsets == null) {
            throw new RuntimeException("No offsets available for firmware " + FW_VERSION);
        }
        Long offset = (Long) offsets.get(key);
        if (offset == null) {
            throw new RuntimeException("Offset " + key + " not found for firmware " + FW_VERSION); // key, not offset
        }
        return offset.longValue();
    }
    
}
package org.bdj.external;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AutoloadConfigParser {
    public static List parseCommands(byte[] data) {
        List commands = new ArrayList();
        if (data == null) return commands;
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(data)));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                commands.add(line);
            }
        } catch (Exception e) {
            // Error parsing config
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
        }
        return commands;
    }

    public static List parseCommands(String filePath) {
        // This is deprecated, use the byte[] version with native reading
        return new ArrayList();
    }
}

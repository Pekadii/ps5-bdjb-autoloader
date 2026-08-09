package org.bdj;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Encapsulates the capabilities of the screen.
 */
public class Screen extends Container {
    private static final long serialVersionUID = 0x4141414141414141L;

    /** Message types for color coding */
    public static class MessageType {
        public static final MessageType INFO = new MessageType("INFO", Color.white);
        public static final MessageType SUCCESS = new MessageType("SUCCESS", Color.green);
        public static final MessageType ERROR = new MessageType("ERROR", Color.red);
        public static final MessageType WARNING = new MessageType("WARNING", Color.yellow);

        public static final int STAGE_INIT = 0;
        public static final int STAGE_SANDBOX = 1;
        public static final int STAGE_INTERNAL_LOADER = 2;
        public static final int STAGE_KEXP_LOAD = 3;
        public static final int STAGE_KEXP_RUN = 4;
        public static final int STAGE_KEXP_COMPLETE = 5;
        public static final int STAGE_ELFLDR_START = 6;
        public static final int STAGE_ELFLDR_WAIT = 7;
        public static final int STAGE_AUTOLOADER_FIND = 8;
        public static final int STAGE_AUTOLOADER_SEND = 9;
        public static final int STAGE_COMPLETE = 10;

        private final String name;
        private final Color color;

        private MessageType(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public String toString() {
            return name;
        }
    }

    /** Simple container for a message and its type */
    private static class Message {
        final String text;
        final MessageType type;

        Message(String text, MessageType type) {
            this.text = text;
            this.type = type;
        }
    }
   
    private final Font FONT = new Font("SansSerif", Font.BOLD, 25);
    private final Font ACTIVE_FONT = new Font("SansSerif", Font.BOLD, 27);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 48);
    private final Font PROGRESS_FONT = new Font("SansSerif", Font.BOLD, 28);
    private final Font VERSION_FONT = new Font("SansSerif", Font.PLAIN, 18);

    private final ArrayList messages = new ArrayList();
    private int progressPercent = 0;
    private String progressMessage = "";
    private String title = "PS5 BD-JB v1.5.0-b1 Autoloader " + Version.VERSION + ("stable".equals(Version.BUILD_TYPE) ? "" : "-" + Version.BUILD_TYPE);


    private static final String ELF_LDR_VERSION = "v0.24-148b71c";
    private static final String KEXP_VERSION = "v0.6-c3d0fd9";
    private static final String AUTOLOADER_VERSION = "v0.1.3-78a6f02."; 

    private static final int[] STAGE_PERCENTAGES = {
        0,
        10,
        20,
        30,
        50,
        65,
        80,
        81,
        85,
        90,
        100
    };

    private static final String[] STAGE_LABELS = {
        "Initializing BD-J",
        "Escaping Java Sandbox",
        "Preparing Internal Loader",
        "Loading Kernel Exploit",
        "Running Kernel Exploit",
        "Kernel Exploit Complete",
        "Starting ELF Autoloader",
        "Waiting for ELF Loader",
        "Locating Unified Autoloader",
        "Sending Unified Autoloader ELF",
        "Autoloader Launched"
    };

    private static final Screen instance = new Screen();

    private volatile boolean isPainting = false;
    private volatile boolean isDirty = false;
    private volatile boolean isVisible = true;

    private Image offscreenImage = null;
    private Graphics offscreenGraphics = null;

    /**
     * Default constructor. Declared as private since this class is singleton.
     */
    private Screen() {
        super();
        setBackground(new Color(0x272727));
        setForeground(Color.WHITE);
        
        // Add component listener to track visibility
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                isVisible = true;
                safeRepaint();
            }
            
            public void componentHidden(ComponentEvent e) {
                isVisible = false;
            }
        });
    }

    /**
     * Retrieves the singleton instance of the screen.
     *
     * @return {@code Screen} instance, there is only one in the application.
     */
    public static Screen getInstance() {
        return instance;
    }

    /**
     * Adds a message on the singleton screen instance, immediately repainting it.
     *
     * @param msg Message to add.
     */
    public static void println(String msg) {
        println(msg, MessageType.INFO, true, false);
    }

    /**
     * Adds a message on the singleton screen instance, with control on whether to immediately repaint it or not.
     *
     * @param msg Message to add.
     * @param type Type of the message for color coding.
     * @param repaint Whether to repaint the screen right away or not.
     * @param replaceLast Whether to add a new line or replace the last printed line (useful for progress output).
     */
    public static void println(String msg, MessageType type, boolean repaint, boolean replaceLast) {
        getInstance().print(msg, type, repaint, replaceLast);
    }

    /**
     * Adds a message to this screen instance, immediately repainting it.
     *
     * @param msg Message to add.
     * @param repaint Whether to repaint the screen right away or not.
     * @param replaceLast Whether to add a new line or replace the last printed line.
     */
    public void print(String msg, boolean repaint, boolean replaceLast) {
        print(msg, MessageType.INFO, repaint, replaceLast);
    }

    /**
     * Adds a message to this screen instance with a specific type.
     *
     * @param msg Message to add.
     * @param type Message type for color coding.
     * @param repaint Whether to repaint the screen right away or not.
     * @param replaceLast Whether to add a new line or replace the last printed line.
     */
    public void print(String msg, MessageType type, boolean repaint, boolean replaceLast) {
        if (msg == null) {
            msg = "null";
        }

        synchronized (this) {
            if (replaceLast && messages.size() > 0) {
                messages.remove(messages.size() - 1);
            }
            messages.add(new Message(msg, type));
            if (messages.size() > 16) {
                messages.remove(0);
            }
            isDirty = true;
        }

        if (repaint) {
            safeRepaint();
        }
    }

    /**
     * Sets the current progress and label.
     *
     * @param percent Progress percentage (0-100).
     * @param label Progress label message.
     */
    public void setProgress(int percent, String label) {
        synchronized (this) {
            this.progressPercent = Math.max(0, Math.min(100, percent));
            this.progressMessage = label != null ? label : "";
            isDirty = true;
        }
        safeRepaint();
    }

    /**
     * Sets the UI title.
     *
     * @param title New title.
     */
    public void setTitle(String title) {
        synchronized (this) {
            this.title = title != null ? title : "";
            isDirty = true;
        }
        safeRepaint();
    }

    private long lastPaintTime = 0;
    private static final long PAINT_INTERVAL = 100; // ms

    private void safeRepaint() {
        if (EventQueue.isDispatchThread()) {
            repaint();
        } else {
            long now = System.currentTimeMillis();
            if (now - lastPaintTime >= PAINT_INTERVAL) {
                lastPaintTime = now;
                // Aggressive immediate paint to bypass starvation
                java.awt.Graphics g = getGraphics();
                if (g != null) {
                    paint(g);
                    g.dispose();
                } else {
                    repaint();
                }
            } else {
                repaint();
            }
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Prints the exception's stack trace on this screen instance.
     *
     * @param e Exception whose stack trace to print.
     */
    public void printStackTrace(Throwable e) {
        if (e == null) {
            print("null exception", MessageType.ERROR, true, false);
            return;
        }

        StringTokenizer st;
        StringBuffer sb;

        try {
            StringWriter sw = new StringWriter();
            try {
                PrintWriter pw = new PrintWriter(sw);
                try {
                    e.printStackTrace(pw);
                } finally {
                    pw.close();
                }

                String stackTrace = sw.toString();
                st = new StringTokenizer(stackTrace, "\n", false);
                sb = new StringBuffer(stackTrace.length());
            } finally {
                sw.close();
            }

            synchronized (this) {
                while (st.hasMoreTokens()) {
                    String line = st.nextToken();
                    sb.setLength(0);
                    for (int i = 0; i < line.length(); ++i) {
                        char c = line.charAt(i);
                        if (c == '\t') {
                            sb.append("   ");
                        } else if (c == '\r') {
                            continue;
                        } else {
                            sb.append(c);
                        }
                    }
                    print(sb.toString(), MessageType.ERROR, !st.hasMoreTokens(), false);
                }
            }
        } catch (IOException ioEx) {
            printThrowable(e);

            throw new RuntimeException("Another exception occurred while printing stacktrace. " + ioEx.getClass().getName() + ": " + ioEx.getMessage());
        }
    }

    /**
     * Convenience method to print basic information about an exception, without printing all the stack trace.
     *
     * @param e Exception to print.
     */
    public void printThrowable(Throwable e) {
        if (e == null) {
            print("null throwable", MessageType.ERROR, true, false);
            return;
        }
        print(e.getClass().getName() + ": " + e.getMessage(), MessageType.ERROR, true, false);
    }

    /**
     * Repaint the screen.
     *
     * @param g {@code} Graphics code on which the screen data is painted.
     */
    public void paint(Graphics g) {
        if (g == null) {
            return;
        }

        int pct;
        String pctMsg;
        String currentTitle;

        synchronized (this) {
            if (isPainting || !isDirty) return;
            isPainting = true;
            isDirty = false;

            pct = this.progressPercent;
            pctMsg = this.progressMessage;
            currentTitle = this.title;
        }

        try {
            int width = getWidth();
            int height = getHeight();
            if (width <= 0 || height <= 0) return;

            // Double Buffering Setup
            if (offscreenImage == null || offscreenImage.getWidth(null) != width || offscreenImage.getHeight(null) != height) {
                offscreenImage = createImage(width, height);
                if (offscreenImage != null) {
                    offscreenGraphics = offscreenImage.getGraphics();
                } else {
                    offscreenGraphics = null;
                }
            }

            Graphics targetG = (offscreenGraphics != null) ? offscreenGraphics : g;

            // 1. Draw Background
            targetG.setColor(getBackground());
            targetG.fillRect(0, 0, width, height);

            // 2. Draw Title
            targetG.setFont(TITLE_FONT);
            targetG.setColor(Color.white);
            int titleWidth = targetG.getFontMetrics().stringWidth(currentTitle);
            targetG.drawString(currentTitle, (width - titleWidth) / 2, 80);

            // 3. Draw Log Container
            int logWidth = (int) (width * 0.7);
            int logHeight = (int) (height * 0.70);
            int logX = (width - logWidth) / 2;
            int logY = 150;

            // Container Background
            targetG.setColor(Color.black);
            targetG.fillRect(logX, logY, logWidth, logHeight);

            // Container Border (Blue Accent)
            targetG.setColor(new Color(0x0036AA));
            targetG.drawRect(logX - 1, logY - 1, logWidth + 1, logHeight + 1);
            targetG.drawRect(logX - 2, logY - 2, logWidth + 3, logHeight + 3);

            // Render jailbreak stage list
            drawStageList(targetG, logX, logY, logWidth, pct, pctMsg);

            // 4. Draw Progress Bar
            int pbWidth = (int) (width * 0.7);
            int pbHeight = 46;
            int pbX = (width - pbWidth) / 2;
            int pbY = logY + logHeight + 30;

            drawProgressBar(targetG, pbX, pbY, pbWidth, pbHeight, pct, pctMsg);

            // 5. Draw Dependency Versions
            targetG.setFont(VERSION_FONT);
            targetG.setColor(new Color(0x888888));

            String deps =
                    "elfldr " + ELF_LDR_VERSION +
                    " | kexp " + KEXP_VERSION +
                    " | autoloader " + AUTOLOADER_VERSION;

            targetG.drawString(deps, 12, height - 34);

            // 6. Draw Footer (Version Info)
            targetG.setColor(new Color(0x666666));

            String versionStr =
                    "PS5 BD-JB Autoloader v" + Version.VERSION +
                    ("stable".equals(Version.BUILD_TYPE) ? "" : "-" + Version.BUILD_TYPE) +
                    " by PLK (" + Version.HASH + ", built at " + Version.BUILD_TIME + ")";

            targetG.drawString(versionStr, 12, height - 12);

            // If we used the off-screen buffer, copy it to the real graphics object
            if (offscreenGraphics != null) {
                g.drawImage(offscreenImage, 0, 0, null);
            }
        } finally {
            synchronized (this) {
                isPainting = false;
            }
        }
    }

    private void drawStageList(
            Graphics g,
            int containerX,
            int containerY,
            int containerWidth,
            int currentPercent,
            String currentMessage) {

        g.setFont(FONT);

        int fontHeight = g.getFontMetrics().getHeight();
        int lineSpacing = 10;
        int rowHeight = fontHeight + lineSpacing;

        int iconX = containerX + 35;
        int textX = containerX + 75;
        int startY = containerY + 40;

        int currentStage = 0;

        for (int i = 0; i < STAGE_PERCENTAGES.length; i++) {
            if (currentPercent >= STAGE_PERCENTAGES[i]) {
                currentStage = i;
            } else {
                break;
            }
        }

            boolean failed =
                    currentMessage != null
                    && currentMessage.toLowerCase().indexOf("failed") >= 0;

            boolean allCompleted =
                    currentPercent >= 100 && !failed;

            for (int i = 0; i < STAGE_LABELS.length; i++) {

            boolean failedStage =
                    failed && i == currentStage;

            int y = startY + (i * rowHeight);

            boolean completed;

            if (failed) {
                completed = i < currentStage;
            } else {
                completed = allCompleted || i < currentStage;
            }

            boolean active =
                    !failed
                    && !allCompleted
                    && i == currentStage;

            if (failedStage) {
                g.setColor(Color.red);
                g.setFont(ACTIVE_FONT);
                drawFailedIcon(g, iconX, y - 10);

            } else if (completed) {
                g.setColor(new Color(0x55CC77));
                g.setFont(FONT);
                drawCompletedIcon(g, iconX, y - 9);

            } else if (active) {
                g.setColor(Color.white);
                g.setFont(ACTIVE_FONT);
                drawActiveIcon(g, iconX, y - 10);

            } else {
                g.setColor(new Color(0x666666));
                g.setFont(FONT);
                drawPendingIcon(g, iconX, y - 10);
            }

            g.drawString(STAGE_LABELS[i], textX, y);
        }
    }

        private void drawFailedIcon(Graphics g, int x, int y) {
            g.drawLine(x, y, x + 14, y + 14);
            g.drawLine(x + 14, y, x, y + 14);
        }

        private void drawCompletedIcon(Graphics g, int x, int y) {
            g.drawLine(x, y + 7, x + 5, y + 12);
            g.drawLine(x + 5, y + 12, x + 14, y + 2);
            g.drawLine(x, y + 8, x + 5, y + 13);
            g.drawLine(x + 5, y + 13, x + 14, y + 3);
        }

        private void drawActiveIcon(Graphics g, int x, int y) {
            int[] xPoints = {x, x, x + 13};
            int[] yPoints = {y, y + 16, y + 8};

            g.fillPolygon(xPoints, yPoints, 3);
        }

        private void drawPendingIcon(Graphics g, int x, int y) {
            g.drawOval(x, y, 14, 14);
        }

        /**
         * Helper to draw a styled progress bar.
         */
        private void drawProgressBar(
            Graphics g,
            int x,
            int y,
            int width,
            int height,
            int percent,
            String label) {

        String lowerLabel = label != null ? label.toLowerCase() : "";

        boolean failed =
                lowerLabel.indexOf("failed") >= 0
                || lowerLabel.indexOf("error") >= 0;

        boolean warning =
                lowerLabel.indexOf("warning") >= 0
                || lowerLabel.indexOf("longer than expected") >= 0;

        boolean success =
                percent >= 100 && !failed;

        // Outer shadow
        g.setColor(new Color(0x101010));
        g.fillRoundRect(x - 3, y - 3, width + 6, height + 6, 18, 18);

        // Background
        g.setColor(new Color(0x202020));
        g.fillRoundRect(x, y, width, height, 16, 16);

        // Select progress color
        Color fillColor;

        if (failed) {
            fillColor = new Color(0xB52B2B);
        } else if (warning) {
            fillColor = new Color(0xC99518);
        } else if (success) {
            fillColor = new Color(0x2B9B57);
        } else {
            fillColor = new Color(0x1769D2);
        }

        // Progress fill
        if (percent > 0) {
            int fillWidth = (int) (width * (percent / 100.0));

            // Prevent tiny rounded fill glitches
            if (fillWidth < height) {
                fillWidth = height;
            }

            if (fillWidth > width) {
                fillWidth = width;
            }

            g.setColor(fillColor);
            g.fillRoundRect(x, y, fillWidth, height, 16, 16);

            // Top highlight
            g.setColor(new Color(
                    Math.min(fillColor.getRed() + 30, 255),
                    Math.min(fillColor.getGreen() + 30, 255),
                    Math.min(fillColor.getBlue() + 30, 255)
            ));

            g.fillRoundRect(
                    x + 3,
                    y + 3,
                    Math.max(0, fillWidth - 6),
                    Math.max(1, height / 4),
                    10,
                    10
            );
        }

        // Border
        g.setColor(new Color(0x777777));
        g.drawRoundRect(x, y, width, height, 16, 16);

        // Percentage text inside the bar
        g.setFont(PROGRESS_FONT);
        g.setColor(Color.white);

        String pctStr = percent + "%";
        int pctWidth = g.getFontMetrics().stringWidth(pctStr);
        int pctHeight = g.getFontMetrics().getAscent();

        int pctX = x + ((width - pctWidth) / 2);
        int pctY = y + ((height + pctHeight) / 2) - 4;

        g.drawString(pctStr, pctX, pctY);

        // Progress label below bar
        if (label != null && label.length() > 0) {
            int labelWidth = g.getFontMetrics().stringWidth(label);

            if (failed) {
                g.setColor(new Color(0xFF6666));
            } else if (warning) {
                g.setColor(new Color(0xFFD45A));
            } else if (success) {
                g.setColor(new Color(0x66DD88));
            } else {
                g.setColor(Color.white);
            }

            g.drawString(
                    label,
                    (getWidth() - labelWidth) / 2,
                    y + height + 30
            );
        }
    }

    public static void clear() {
        getInstance().clearMessages();
    }
    
    public void clearMessages() {
        synchronized (this) {
            messages.clear();
            isDirty = true;
        }
        safeRepaint();
    }
}
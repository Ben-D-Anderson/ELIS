package org.elis.recycle.gui;

import java.awt.*;
import java.io.IOException;

public class VirtualKeyboard implements AutoCloseable {

    private final ProcessBuilder processBuilder;
    private final Object processLock = new Object();
    private boolean addedShutdownHook = false;
    private Process process;

    public VirtualKeyboard() {
        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int keyboardHeight = (int) (screenHeight / 2.5);
        this.processBuilder = new ProcessBuilder()
                .command("/usr/bin/onboard",
                        "--size", String.format("%sx%s", width, keyboardHeight),
                        "-y", String.valueOf(screenHeight - keyboardHeight));
    }

    public void start() throws IOException {
        close();
        synchronized (processLock) {
            this.process = this.processBuilder.start();
            if (!addedShutdownHook) {
                Runtime.getRuntime().addShutdownHook(new Thread(this::close));
                addedShutdownHook = true;
            }
        }
    }

    @Override
    public void close() {
        synchronized (processLock) {
            if (this.process != null) {
                this.process.destroyForcibly();
                this.process = null;
            }
        }
    }

}

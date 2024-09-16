package org.elis.recycle.bin;

import org.elis.recycle.files.Config;

import java.io.IOException;
import java.util.function.Consumer;

public class Disposer {

    public static void dispose(Config config, Config.Bin bin, Runnable onSuccess, Consumer<Exception> onFailure) {
        try {
            int angleToTilt = config.getServoTiltAngle();
            if (bin.getAngle() < 180) angleToTilt *= -1;
            moveServos(angleToTilt);
            Thread.sleep(config.getServoWaitTime());
            reset();
            onSuccess.run();
        } catch (Exception e) {
            e.printStackTrace();
            onFailure.accept(e);
        }
    }

    //ran on start-up
    public static void reset() throws IOException, InterruptedException {
        moveServos(0);
    }

    private static void moveServos(int angle) throws IOException, InterruptedException {
        if (Math.abs(angle) > 90) {
            //Unsafe move attempted
            return;
        }
        ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/python3", "/home/elis/IMechE/move_servos.py", String.valueOf(angle));
        Process process = processBuilder.start();
        process.waitFor();
    }

}

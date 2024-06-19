package org.elis.recycle.bin;

import org.elis.recycle.files.Config;

import java.util.function.Consumer;

public class Disposer {

    public static void dispose(Config.Bin bin, Runnable onSuccess, Consumer<Exception> onFailure) {
        try {
            //todo: tilt then reset table
            Thread.sleep(5000);
            onSuccess.run();
        } catch (Exception e) {
            onFailure.accept(e);
        }
    }

    //ran on start-up
    public static void reset() {
        //todo: implement
    }

}

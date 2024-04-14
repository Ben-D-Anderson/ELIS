package org.elis.recycle;

import org.elis.recycle.barcode.CameraException;
import org.elis.recycle.database.Database;
import org.elis.recycle.database.Product;
import org.elis.recycle.gui.MainWindow;

import java.util.Optional;

public class Main {
    public static void main(String[] args) throws CameraException {
        MainWindow frame = new MainWindow(new Database() {
            @Override
            public Optional<Product> findProduct(String barcode) {
                return Optional.empty();
            }
        });
        frame.setVisible(true);

        frame.scanBarcode();

    }
}
package org.elis.recycle.gui;

import org.elis.recycle.barcode.CameraException;
import org.elis.recycle.database.Database;
import org.elis.recycle.database.Product;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.Optional;

public class MainWindow extends JFrame {

    private final BarcodeScanningScreen barcodeScanningScreen = new BarcodeScanningScreen();
    private final Database database;

    public MainWindow(Database database) {
        this.database = database;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ignored) {}

        setLocationRelativeTo(null);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH)) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        if (Toolkit.getDefaultToolkit().isAlwaysOnTopSupported()) {
            setAlwaysOnTop(true);
        }

        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainScreen();
    }

    public void setScreen(JPanel panel) {
        panel.setSize(getSize());
        setContentPane(panel);
        revalidate();
        repaint();
    }

    private void couldNotFindProductMessage(String barcode) {
        JOptionPane.showMessageDialog(this,
                String.format("Could not find product with barcode %s in database.", barcode),
                "Product Not Found", JOptionPane.ERROR_MESSAGE);
    }

    //todo: implement
    public void mainScreen() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Main Screen");
        panel.add(label);
        setScreen(panel);
    }

    //todo: implement
    private void productScreen(Product product) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Product Information For Barcode: " + product.getBarcode());
        panel.add(label);
        setScreen(panel);
    }

    public void scanBarcode() throws CameraException {
        setScreen(barcodeScanningScreen);

        barcodeScanningScreen.start(Duration.ofSeconds(10),
                //barcode callback
                barcode -> {
                    Optional<Product> productOptional = database.findProduct(barcode);
                    SwingUtilities.invokeLater(() -> {
                        if (productOptional.isEmpty()) {
                            mainScreen();
                            couldNotFindProductMessage(barcode);
                        } else {
                            productScreen(productOptional.get());
                        }
                    });
                },
                //timeout callback
                () -> SwingUtilities.invokeLater(this::mainScreen)
        );
    }

}

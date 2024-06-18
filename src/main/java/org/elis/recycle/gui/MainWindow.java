package org.elis.recycle.gui;

import org.elis.recycle.barcode.CameraException;
import org.elis.recycle.database.Database;
import org.elis.recycle.database.Product;
import org.elis.recycle.files.Config;
import org.elis.recycle.gui.screens.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

public class MainWindow extends JFrame {

    private final BarcodeScanningScreen barcodeScanningScreen;
    private final ProductSearchScreen productSearchScreen;
    private final FullLoadingScreen fullLoadingScreen;
    private final MainMenuScreen mainMenuScreen;
    private final ConfigScreen configScreen;
    private final Database database;
    private final Config config;

    public MainWindow(Config config, Database database) {
        super();
        this.config = config;
        this.database = database;
        this.mainMenuScreen = new MainMenuScreen(config, this);
        this.barcodeScanningScreen = new BarcodeScanningScreen(config);
        this.fullLoadingScreen = new FullLoadingScreen(config);
        this.configScreen = new ConfigScreen(this, config, database);
        this.productSearchScreen = new ProductSearchScreen(config, database);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException ignoredAgain) {}
        }

        setLocationRelativeTo(null);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH)) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        mainMenuScreen.setup(getSize());

        if (Toolkit.getDefaultToolkit().isAlwaysOnTopSupported()) {
            setAlwaysOnTop(true);
        }

        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("ELIS");

        mainScreen();
    }

    public void setScreen(JPanel panel) {
        panel.setSize(getSize());
        setContentPane(panel);
        revalidate();
        repaint();
    }

    private void couldNotFindProductMessage(String barcode) {
        errorMessage("Product Not Found", String.format("Could not find product with barcode '%s' in database.", barcode));
    }

    private void errorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void mainScreen() {
        setScreen(mainMenuScreen);
    }

    public void loadingScreen(String loadingText) {
        fullLoadingScreen.setLoadingText(loadingText);
        setScreen(fullLoadingScreen);
    }

    public void configScreen() {
        setScreen(configScreen);
        try {
            configScreen.refreshFromConfig();
        } catch (IOException e) {
            errorMessage("Error Opening Config", "Failed to open the config file.\n\nError: " + e.getMessage());
            mainScreen();
        }
    }

    private void productScreen(Product product) {
        setScreen(new ProductScreen(this, config, product));
    }

    public void searchForProduct() {
        setScreen(productSearchScreen);

        try {
            productSearchScreen.start(this::productScreen, this::mainScreen);
        } catch (IOException e) {
            errorMessage("Virtual Keyboard Error", "Failed to open on-screen keyboard.");
        }
    }

    public void scanBarcode() {
        setScreen(barcodeScanningScreen);

        new Thread(() -> {
            try {
                barcodeScanningScreen.start(
                        Duration.ofSeconds(config.getUserInterfaceSettings()
                                .getBarcodeScanningScreenSettings()
                                .getScanningTimeoutSeconds()),
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
            } catch (CameraException e) {
                SwingUtilities.invokeLater(() -> {
                    mainScreen();
                    errorMessage("Camera Error", e.getMessage());
                });
            }
        }).start();
    }

}

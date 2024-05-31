package org.elis.recycle.gui.screens;

import org.elis.recycle.files.Config;
import org.elis.recycle.gui.MainWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class MainMenuScreen extends JPanel {

    private final Config config;
    private final MainWindow mainWindow;

    public MainMenuScreen(Config config, MainWindow mainWindow) {
        super();
        this.config = config;
        this.mainWindow = mainWindow;

        setLayout(new GridBagLayout());
    }

    public void setup(Dimension screenSize) {
        JLabel scanButton = new JLabel();
        try {
            URL barcodeImageURL = Objects.requireNonNull(getClass().getResource("/barcode_button.png"));
            scanButton.setIcon(new ImageIcon(
                    ImageIO.read(barcodeImageURL)
                            .getScaledInstance(
                                    (int) (screenSize.getWidth() / 4),
                                    (int) (screenSize.getWidth() / 4),
                                    Image.SCALE_SMOOTH)
            ));
            scanButton.setOpaque(false);
        } catch (IOException | NullPointerException e) {
            scanButton.setText("Scan Barcode");
        }
        scanButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
        scanButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingUtilities.invokeLater(mainWindow::scanBarcode);
            }
        });

        JLabel searchButton = new JLabel();
        try {
            URL searchImageURL = Objects.requireNonNull(getClass().getResource("/search_button.png"));

            searchButton.setIcon(new ImageIcon(
                    ImageIO.read(searchImageURL)
                            .getScaledInstance(
                                    (int) (screenSize.getWidth() / 4),
                                    (int) (screenSize.getWidth() / 4),
                                    Image.SCALE_DEFAULT)
            ));
            searchButton.setOpaque(false);
        } catch (IOException | NullPointerException e) {
            searchButton.setText("Search For Product");
        }
        searchButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingUtilities.invokeLater(mainWindow::searchForProduct);
            }
        });

        add(searchButton);
        add(Box.createHorizontalStrut((int) (screenSize.getWidth() / 8)));
        add(scanButton);
    }

}

package org.elis.recycle.gui.screens;

import org.elis.recycle.files.Config;
import org.elis.recycle.gui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;

public class MainMenuScreen extends JPanel {

    private final Config config;
    private final MainWindow mainWindow;

    public MainMenuScreen(Config config, MainWindow mainWindow) {
        super();
        this.config = config;
        this.mainWindow = mainWindow;

        setLayout(new FlowLayout());

        JLabel scanButton = new JLabel();
        try {
            URL barcodeImageURL = Objects.requireNonNull(getClass().getResource("/barcode_button.png"));
            scanButton.setIcon(new ImageIcon(barcodeImageURL));
            scanButton.setOpaque(false);
        } catch (NullPointerException e) {
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
            searchButton.setIcon(new ImageIcon(searchImageURL));
            searchButton.setOpaque(false);
        } catch (NullPointerException e) {
            searchButton.setText("Search For Product");
        }
        searchButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingUtilities.invokeLater(mainWindow::searchForProduct);
            }
        });

        add(scanButton);
        add(searchButton);
    }

}

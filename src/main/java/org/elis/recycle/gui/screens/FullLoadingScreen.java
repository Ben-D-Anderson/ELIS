package org.elis.recycle.gui.screens;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

public class FullLoadingScreen extends JPanel {

    public FullLoadingScreen() {
        super();
        setLayout(new GridBagLayout());

        JLabel loadingGifLabel = new JLabel();
        try {
            URL loadingGifURL = Objects.requireNonNull(getClass().getResource("/loading.gif"));
            loadingGifLabel.setIcon(new ImageIcon(loadingGifURL));
            loadingGifLabel.setOpaque(false);
        } catch (NullPointerException e) {
            loadingGifLabel.setText("Loading...");
        }
        add(loadingGifLabel);
    }

}

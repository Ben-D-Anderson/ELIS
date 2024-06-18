package org.elis.recycle.gui.screens;

import org.elis.recycle.files.Config;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

public class FullLoadingScreen extends JPanel {

    private final JLabel loadingTextLabel = new JLabel("");

    public FullLoadingScreen(Config config) {
        super();
        setLayout(new GridBagLayout());

        loadingTextLabel.setFont(config.getUserInterfaceSettings()
                .getLoadingScreenSettings()
                .getTextFont().asFont());

        GridBagConstraints top = new GridBagConstraints();
        top.anchor = GridBagConstraints.NORTH;
        top.gridy = 1;
        top.weighty = 1;

        JPanel loadingTextLabelPanel = new JPanel();
        loadingTextLabelPanel.setLayout(new GridBagLayout());
        loadingTextLabelPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        loadingTextLabelPanel.add(loadingTextLabel);
        add(loadingTextLabelPanel, top);

        JLabel loadingGifLabel = new JLabel();
        try {
            URL loadingGifURL = Objects.requireNonNull(getClass().getResource("/loading.gif"));
            loadingGifLabel.setIcon(new ImageIcon(loadingGifURL));
            loadingGifLabel.setOpaque(false);
        } catch (NullPointerException e) {
            loadingGifLabel.setText("Loading...");
        }
        GridBagConstraints bottom = new GridBagConstraints();
        bottom.gridy = 2;
        bottom.weighty = 9;
        add(loadingGifLabel, bottom);
    }

    public void setLoadingText(String loadingText) {
        loadingTextLabel.setText(loadingText);
    }

}

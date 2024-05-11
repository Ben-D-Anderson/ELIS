package org.elis.recycle.gui.screens;

import org.elis.recycle.database.Database;
import org.elis.recycle.database.Product;
import org.elis.recycle.files.Config;
import org.elis.recycle.gui.VirtualKeyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class ProductSearchScreen extends JPanel {

    private final JTextField textInputField = new JTextField();
    private final VirtualKeyboard virtualKeyboard;
    private final ProductOptionsPane productOptionsPane = new ProductOptionsPane();
    private final Database database;
    private final Config config;
    private Consumer<Product> chosenConsumer;
    private Runnable backButtonCallback;

    public ProductSearchScreen(Config config, Database database) {
        super();
        this.config = config;
        this.database = database;
        virtualKeyboard = new VirtualKeyboard();
        setLayout(new GridBagLayout());

        JButton backButton = new JButton("Back");
        backButton.setFont(config.getProductSearchScreenSettings().getBackButtonFont().asFont());
        backButton.setFocusPainted(false);
        backButton.addActionListener(actionEvent -> returnToMenu());

        textInputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                textInputField.requestFocusInWindow();
                try {
                    virtualKeyboard.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        textInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchQuery = textInputField.getText();
                loadOptions(database.findProducts(searchQuery));
            }
        });
        textInputField.setBorder(BorderFactory.createCompoundBorder(textInputField.getBorder(),
                BorderFactory.createEmptyBorder(5, 15, 5, 5)));
        textInputField.setFont(config.getProductSearchScreenSettings().getInputBoxFont().asFont());

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.NORTHWEST;
        left.fill = GridBagConstraints.BOTH;
        left.weighty = 0.5;
        left.weightx = 1;
        GridBagConstraints right = new GridBagConstraints();
        right.anchor = GridBagConstraints.NORTHEAST;
        right.weighty = 0.5;
        right.weightx = 14;
        right.fill = GridBagConstraints.BOTH;
        right.gridwidth = GridBagConstraints.REMAINDER;
        GridBagConstraints remainder = new GridBagConstraints();
        remainder.fill = GridBagConstraints.BOTH;
        remainder.gridwidth = GridBagConstraints.REMAINDER;
        remainder.weighty = 14;
        remainder.weightx = 1;

        add(backButton, left);
        add(textInputField, right);
        add(new JScrollPane(productOptionsPane), remainder);
    }

    private static class ProductOptionsPane extends JPanel {
        private ProductOptionsPane() {
            super();
            setBorder(BorderFactory.createCompoundBorder(getBorder(),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            setLayout(new GridLayout(12, 1, 0, 2));
        }
    }

    private class ProductOption extends JPanel {
        private ProductOption(Product product) {
            super();
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createCompoundBorder(getBorder(),
                    BorderFactory.createEmptyBorder(5, 15, 5, 5)));

            GridBagConstraints nameConstraints = new GridBagConstraints();
            nameConstraints.anchor = GridBagConstraints.WEST;
            nameConstraints.weightx = 1;
            nameConstraints.weighty = 1;
            nameConstraints.fill = GridBagConstraints.BOTH;

            JLabel productNameLabel = new JLabel(product.getName());
            productNameLabel.setFont(config.getProductSearchScreenSettings().getProductOptionFont().asFont());

            add(productNameLabel, nameConstraints);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    stop(product);
                }
            });
        }
    }

    private void loadOptions(List<Product> products) {
        productOptionsPane.removeAll();
        for (Product product : products) {
            productOptionsPane.add(new ProductOption(product));
        }
        productOptionsPane.revalidate();
        productOptionsPane.repaint();
    }

    public void start(Consumer<Product> chosenConsumer, Runnable backButtonCallback) throws IOException {
        textInputField.requestFocusInWindow();
        this.chosenConsumer = chosenConsumer;
        this.backButtonCallback = backButtonCallback;
        loadOptions(database.findProducts(""));
        virtualKeyboard.start();
    }

    private void returnToMenu() {
        virtualKeyboard.close();
        if (backButtonCallback != null) {
            backButtonCallback.run();
        }
    }

    private void stop(Product selectedProduct) {
        virtualKeyboard.close();
        if (chosenConsumer != null) {
            chosenConsumer.accept(selectedProduct);
        }
    }

}

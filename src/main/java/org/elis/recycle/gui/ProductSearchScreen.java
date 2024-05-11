package org.elis.recycle.gui;

import org.elis.recycle.database.Database;
import org.elis.recycle.database.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ProductSearchScreen extends JPanel {

    private final JTextField textInputField = new JTextField();
    private final VirtualKeyboard virtualKeyboard;
    private final ProductOptionsPane productOptionsPane = new ProductOptionsPane();
    private Consumer<Product> chosenConsumer;
    private Runnable backButtonCallback;

    public ProductSearchScreen(Database database) {
        super();
        virtualKeyboard = new VirtualKeyboard();
        setLayout(new GridBagLayout());

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 100));
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
            public void keyTyped(KeyEvent e) {
                String searchQuery = textInputField.getText();
                loadOptions(database.findProducts(searchQuery));
            }
        });
        textInputField.setBorder(BorderFactory.createCompoundBorder(textInputField.getBorder(),
                BorderFactory.createEmptyBorder(5, 15, 5, 5)));
        textInputField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));

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
            setLayout(new GridLayout());
        }
    }

    private class ProductOption extends JPanel {
        private ProductOption(Product product) {
            super();
            setLayout(new FlowLayout());
            add(new JLabel(product.getName()));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    stop(product);
                }
            });
            setBackground(Color.BLACK);
            System.out.println("Will add " + product.getName());
        }
    }

    private void loadOptions(List<Product> products) {
        productOptionsPane.removeAll();
        products.forEach(product -> productOptionsPane.add(new ProductOption(product)));
    }

    public void start(Consumer<Product> chosenConsumer, Runnable backButtonCallback) throws IOException {
        textInputField.requestFocusInWindow();
        this.chosenConsumer = chosenConsumer;
        this.backButtonCallback = backButtonCallback;
        loadOptions(new LinkedList<>());
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

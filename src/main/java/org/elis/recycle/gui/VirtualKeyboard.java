package org.elis.recycle.gui;

import org.elis.recycle.files.Config;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;

public class VirtualKeyboard extends JFrame implements AutoCloseable {

    private JTextComponent typingComponent;
    private int caretPosition;
    private JScrollBar scrollBar;
    private int scrollBarValue;
    private final Config config;
    private boolean capsEnabled = true;
    private final LinkedList<JButton> buttons = new LinkedList<>();

    public VirtualKeyboard(Config config) {
        this.config = config;
        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int keyboardHeight = (int) (screenHeight / 3);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(width, keyboardHeight);
        setResizable(false);
        setUndecorated(true);
        setFocusable(false);
        setAlwaysOnTop(true);
        setLayout(new GridLayout(5, 12));
        setLocation(0, screenHeight - keyboardHeight);
        close();

        add(createKeyButton("!"));
        add(createKeyButton("\""));
        add(createKeyButton("£"));
        add(createKeyButton("$"));
        add(createKeyButton("%"));
        add(createKeyButton("^"));
        add(createKeyButton("&"));
        add(createKeyButton("*"));
        add(createKeyButton("("));
        add(createKeyButton(")"));
        add(createKeyButton("-"));
        add(createKeyButton("_"));

        add(createKeyButton("Q"));
        add(createKeyButton("W"));
        add(createKeyButton("E"));
        add(createKeyButton("R"));
        add(createKeyButton("T"));
        add(createKeyButton("Y"));
        add(createKeyButton("U"));
        add(createKeyButton("I"));
        add(createKeyButton("O"));
        add(createKeyButton("P"));
        add(createKeyButton("+"));
        add(createKeyButton("DEL"));

        add(createKeyButton("CAPS"));
        add(createKeyButton("A"));
        add(createKeyButton("S"));
        add(createKeyButton("D"));
        add(createKeyButton("F"));
        add(createKeyButton("G"));
        add(createKeyButton("H"));
        add(createKeyButton("J"));
        add(createKeyButton("K"));
        add(createKeyButton("L"));
        add(createKeyButton("{"));
        add(createKeyButton("}"));

        add(createKeyButton(":"));
        add(createKeyButton(";"));
        add(createKeyButton("Z"));
        add(createKeyButton("X"));
        add(createKeyButton("C"));
        add(createKeyButton("V"));
        add(createKeyButton("B"));
        add(createKeyButton("N"));
        add(createKeyButton("M"));
        add(createKeyButton("="));
        add(createKeyButton("["));
        add(createKeyButton("]"));

        add(createKeyButton("@"));
        add(createKeyButton("#"));
        add(createKeyButton("'"));
        add(createKeyButton("<"));
        add(createKeyButton(">"));
        add(createKeyButton("SPACE"));
        add(createKeyButton(","));
        add(createKeyButton("."));
        add(createKeyButton("/"));
        add(createKeyButton("?"));
        add(createKeyButton("ENTER"));
        add(createKeyButton("CLOSE"));
    }

    public void start(JTextComponent typingComponent, int caretPosition, JScrollBar scrollBar, int scrollBarValue) throws IOException {
        this.typingComponent = typingComponent;
        this.caretPosition = caretPosition;
        this.scrollBar = scrollBar;
        this.scrollBarValue = scrollBarValue;
        setVisible(true);
        setCaps(false);
    }

    @Override
    public void close() {
        setVisible(false);
    }

    private Component createKeyButton(String buttonText) {
        JButton button = new JButton(buttonText);
        button.setFont(config.getUserInterfaceSettings().getVirtualKeyboardFont().asFont());
        button.setFocusable(false);
        button.addActionListener(actionEvent -> {
            String newText = typingComponent.getText();
            if (buttonText.equals("DEL")) {
                if (caretPosition > 0) {
                    newText = typingComponent.getText().substring(0, caretPosition - 1)
                            + typingComponent.getText().substring(caretPosition);
                    caretPosition--;
                }
            } else if (buttonText.equals("ENTER")) {
                newText = typingComponent.getText().substring(0, caretPosition)
                        + "\n" + typingComponent.getText().substring(caretPosition);
                caretPosition++;
            } else if (buttonText.equals("CAPS")) {
                setCaps(!capsEnabled);
                return;
            } else if (buttonText.equals("SPACE")) {
                newText = typingComponent.getText().substring(0, caretPosition)
                        + " " + typingComponent.getText().substring(caretPosition);
                caretPosition++;
            } else if (buttonText.equals("CLOSE")) {
                close();
                return;
            } else {
                newText = typingComponent.getText().substring(0, caretPosition)
                        + button.getText() + typingComponent.getText().substring(caretPosition);
                caretPosition++;
            }
            typingComponent.setText(newText);
            typingComponent.setCaretPosition(caretPosition);
            typingComponent.requestFocus();
            if (scrollBar != null) {
                SwingUtilities.invokeLater(() -> scrollBar.setValue(scrollBarValue));
            }
        });
        buttons.add(button);
        return button;
    }

    private void setCaps(boolean capsEnabled) {
        for (JButton buttonComponent : buttons) {
            if (buttonComponent.getText().length() == 1) {
                SwingUtilities.invokeLater(() -> buttonComponent.setText(capsEnabled ?
                        buttonComponent.getText().toUpperCase() :
                        buttonComponent.getText().toLowerCase()));
            }
        }
        this.capsEnabled = capsEnabled;
    }

}

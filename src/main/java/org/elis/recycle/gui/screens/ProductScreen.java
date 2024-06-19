package org.elis.recycle.gui.screens;

import org.elis.recycle.bin.Disposer;
import org.elis.recycle.database.Product;
import org.elis.recycle.database.RecycleStep;
import org.elis.recycle.files.Config;
import org.elis.recycle.gui.CustomOptionPane;
import org.elis.recycle.gui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductScreen extends JPanel {

    private final MainWindow mainWindow;
    private final Config config;
    private final Product product;
    private final JPanel stepsPanel = new JPanel();
    private final JPanel actionButtonsPanel = new JPanel();
    private final JButton finishButton = new JButton("Finish");
    private static final String SKIP_TEXT = "<html><center>Skip<br />Step</center></html>";
    private static final String NEXT_TEXT = "<html><center>Next<br />Step</center></html>";
    private final JButton nextStepButton = new JButton(NEXT_TEXT);
    private final JButton disposeButton = new JButton("<html><center>Dispose<br />Item</center></html>");
    private final JLabel currentStepInstructionLabel = new JLabel();
    private final Component spacer = Box.createHorizontalStrut(50);
    private int currentStepIndex = 0;
    private long lastClick = 0L;
    private static final long CLICK_COOLDOWN_MILLIS = 200L;

    public ProductScreen(MainWindow mainWindow, Config config, Product product) {
        super();
        this.mainWindow = mainWindow;
        this.config = config;
        this.product = product;

        setLayout(new GridBagLayout());
        actionButtonsPanel.setLayout(new GridBagLayout());
        stepsPanel.setLayout(new GridBagLayout());

        GridBagConstraints topLeft = new GridBagConstraints();
        topLeft.anchor = GridBagConstraints.NORTHWEST;
        topLeft.fill = GridBagConstraints.BOTH;
        topLeft.gridy = 0;
        topLeft.weighty = 0.5;
        topLeft.weightx = 1;
        GridBagConstraints topRight = new GridBagConstraints();
        topRight.anchor = GridBagConstraints.NORTHEAST;
        topRight.gridy = 0;
        topRight.weighty = 0.5;
        topRight.weightx = 14;
        topRight.fill = GridBagConstraints.BOTH;
        topRight.gridwidth = GridBagConstraints.REMAINDER;
        GridBagConstraints remainder = new GridBagConstraints();
        remainder.fill = GridBagConstraints.BOTH;
        remainder.gridwidth = GridBagConstraints.REMAINDER;
        remainder.gridy = 1;
        remainder.weighty = 14;
        remainder.weightx = 1;
        GridBagConstraints centre = new GridBagConstraints();
        centre.weighty = 1;
        centre.weightx = 1;
        GridBagConstraints leftHalf = new GridBagConstraints();
        leftHalf.fill = GridBagConstraints.BOTH;
        leftHalf.anchor = GridBagConstraints.WEST;
        leftHalf.gridy = 1;
        leftHalf.gridx = 1;
        leftHalf.weighty = 1;
        leftHalf.weightx = 1.5;
        GridBagConstraints rightHalf = new GridBagConstraints();
        rightHalf.fill = GridBagConstraints.BOTH;
        rightHalf.anchor = GridBagConstraints.EAST;
        rightHalf.gridy = 1;
        rightHalf.gridx = 2;
        rightHalf.weighty = 1;
        rightHalf.weightx = 1;

        JButton backButton = new JButton("Back");
        backButton.setFont(config.getUserInterfaceSettings()
                .getProductScreenSettings()
                .getBackButtonFont().asFont());
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1, false),
                backButton.getBorder()));
        backButton.setFocusPainted(false);
        backButton.addActionListener(actionEvent -> returnToMenu());

        add(backButton, topLeft);

        JLabel productNameLabel = new JLabel(product.getName());
        productNameLabel.setFont(config.getUserInterfaceSettings()
                .getProductScreenSettings().getProductNameFont().asFont());
        productNameLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1, false),
                BorderFactory.createEmptyBorder(0, 20, 0, 0)));
        add(productNameLabel, topRight);

        currentStepInstructionLabel.setFont(config.getUserInterfaceSettings()
                .getProductScreenSettings().getStepFont().asFont());
        disposeButton.setFont(config.getUserInterfaceSettings()
                .getProductScreenSettings()
                .getDisposeButtonFont().asFont());
        finishButton.setFont(config.getUserInterfaceSettings()
                .getProductScreenSettings()
                .getFinishButtonFont().asFont());
        nextStepButton.setFont(config.getUserInterfaceSettings()
                .getProductScreenSettings()
                .getNextStepButtonFont().asFont());
        disposeButton.setFocusPainted(false);
        finishButton.setFocusPainted(false);
        nextStepButton.setFocusPainted(false);
        disposeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        finishButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        nextStepButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

        disposeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!disposeButton.isEnabled()) return;

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClick < CLICK_COOLDOWN_MILLIS) {
                    return;
                }
                lastClick = currentTime;

                disposeClicked();
            }
        });
        nextStepButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClick < CLICK_COOLDOWN_MILLIS) {
                   return;
                }
                lastClick = currentTime;
                nextStep();
            }
        });
        finishButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClick < CLICK_COOLDOWN_MILLIS) {
                    return;
                }
                lastClick = currentTime;
                returnToMenu();
            }
        });

        JButton beginRecycleButton = new JButton("<html><center>Begin<br />Recycle</center></html>");
        beginRecycleButton.setFont(config.getUserInterfaceSettings()
                .getProductScreenSettings().getBeginRecycleButtonFont().asFont());
        beginRecycleButton.setFocusPainted(false);
        beginRecycleButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 15, 15, 15)));
        beginRecycleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClick < CLICK_COOLDOWN_MILLIS) {
                    return;
                }
                lastClick = currentTime;
                stepsPanel.removeAll();
                actionButtonsPanel.add(disposeButton);
                actionButtonsPanel.add(spacer);
                actionButtonsPanel.add(nextStepButton);
                JPanel currentStepInstructionPanel = new JPanel(new GridBagLayout());
                currentStepInstructionPanel.add(currentStepInstructionLabel);
                stepsPanel.add(currentStepInstructionPanel, leftHalf);
                stepsPanel.add(actionButtonsPanel, rightHalf);

                nextStep();
            }
        });
        stepsPanel.add(beginRecycleButton, centre);

        add(stepsPanel, remainder);
    }

    private void disposeClicked() {
        Optional<RecycleStep> currentRecycleStepOptional = getCurrentStep();
        if (currentRecycleStepOptional.isEmpty()) return;
        RecycleStep recycleStep = currentRecycleStepOptional.get();
        if (!recycleStep.isRequiresWasteDisposal()) return;

        String material = recycleStep.getMaterialToDispose();
        Optional<Config.Bin> binOptional = config.getMaterialBin(material);
        if (binOptional.isEmpty()) {
            CustomOptionPane.showMessageDialog(config,
                    this,
                    "You have not set up a bin supporting this material.",
                    "No Appropriate Bin",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean confirmed = CustomOptionPane.showConfirmDialog(config, this,
                "WARNING: The tilting platform is going to move.\n" +
                "Are all body parts clear of the platform?", "Safety Warning");
        if (!confirmed) return;

        mainWindow.loadingScreen("Disposing Material...");
        new Thread(() -> {
            Disposer.dispose(binOptional.get(), () -> SwingUtilities.invokeLater(() -> {
                disposeButton.setEnabled(false);
                nextStepButton.setText(NEXT_TEXT);
                mainWindow.setScreen(this);
            }), exception -> SwingUtilities.invokeLater(() -> {
                mainWindow.setScreen(this);
                CustomOptionPane.showMessageDialog(config, this,
                        "An unexpected error occurred whilst disposing the item."
                                + ((exception == null) ? "" : "\n\nError: " + exception.getMessage()),
                        "Disposal Error", JOptionPane.ERROR_MESSAGE);
            }));
        }).start();
    }

    private void updateCurrentStepInstructionLabel(String newText) {
        StringBuilder formattedText = new StringBuilder("<html>");
        newText = newText.replace("\n", "<br />");
        while (newText.length() > config.getUserInterfaceSettings()
                .getProductScreenSettings().getInstructionLineLength()) {
            formattedText.append(newText, 0, config.getUserInterfaceSettings()
                    .getProductScreenSettings().getInstructionLineLength());
            if (Character.isAlphabetic(newText.charAt(config.getUserInterfaceSettings()
                    .getProductScreenSettings().getInstructionLineLength() - 1))
                    && Character.isAlphabetic(newText.charAt(config.getUserInterfaceSettings()
                    .getProductScreenSettings().getInstructionLineLength()))) {
                formattedText.append("-");
            }
            formattedText.append("<br />");
            newText = newText.substring(config.getUserInterfaceSettings()
                    .getProductScreenSettings().getInstructionLineLength());
        }
        formattedText.append(newText);
        int paddingToAdd = newText.length() - config.getUserInterfaceSettings()
                .getProductScreenSettings().getInstructionLineLength();
        formattedText.append(" ".repeat(Math.max(0, paddingToAdd)));
        formattedText.append("</html>");
        currentStepInstructionLabel.setText(formattedText.toString());
    }

    private void nextStep() {
        currentStepIndex++;
        getCurrentStep().ifPresentOrElse(currentStep -> {
            updateCurrentStepInstructionLabel(currentStep.getInstruction());
            disposeButton.setEnabled(currentStep.isRequiresWasteDisposal());
            nextStepButton.setText(currentStep.isRequiresWasteDisposal() ? SKIP_TEXT : NEXT_TEXT);
        }, () -> {
            updateCurrentStepInstructionLabel("Finished Recycling Product");
            actionButtonsPanel.remove(disposeButton);
            actionButtonsPanel.remove(spacer);
            actionButtonsPanel.remove(nextStepButton);

            GridBagConstraints centre = new GridBagConstraints();
            centre.weighty = 1;
            centre.weightx = 1;
            actionButtonsPanel.add(finishButton, centre);
        });

    }

    private Optional<RecycleStep> getCurrentStep() {
        if (currentStepIndex > product.getSteps().size()) return Optional.empty();
        return Optional.ofNullable(product.getSteps().get(currentStepIndex - 1));
    }

    private void returnToMenu() {
        mainWindow.mainScreen();
    }

}

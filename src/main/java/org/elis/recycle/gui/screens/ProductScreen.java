package org.elis.recycle.gui.screens;

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

public class ProductScreen extends JPanel {

    private final MainWindow mainWindow;
    private final Product product;
    private final JPanel stepsPanel = new JPanel();
    private final JPanel actionButtonsPanel = new JPanel();
    private final JButton finishButton = new JButton("Finish");
    private static final String SKIP_TEXT = "<html><center>Skip<br />Step</center></html>";
    private static final String NEXT_TEXT = "<html><center>Next<br />Step</center></html>";
    private final JButton nextStepButton = new JButton(NEXT_TEXT);
    private final JButton disposeButton = new JButton("<html><center>Dispose<br />Item</center></html>");
    private final JButton beginRecycleButton = new JButton("<html><center>Begin<br />Recycle</center></html>");
    private final JLabel currentStepInstructionLabel = new JLabel();
    private final Component spacer = Box.createHorizontalStrut(50);
    private int currentStepIndex = 0;
    private long lastClick = 0L;
    private static final long CLICK_COOLDOWN_MILLIS = 200L;
    private StepState state = StepState.BEFORE;

    enum StepState {
        BEFORE,
        DURING,
        AFTER
    }

    public ProductScreen(MainWindow mainWindow, Config config, Product product) {
        super();
        this.mainWindow = mainWindow;
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
        backButton.setFocusPainted(false);
        backButton.addActionListener(actionEvent -> returnToMenu());

        add(backButton, topLeft);

        add(Box.createHorizontalStrut(1), topLeft);

        JLabel productNameLabel = new JLabel(product.getName());
        productNameLabel.setFont(config.getUserInterfaceSettings()
                .getProductScreenSettings().getProductNameFont().asFont());
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
                disposeClicked(config);
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
                state = StepState.DURING;
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

    private void disposeClicked(Config config) {
        Optional<RecycleStep> currentRecycleStepOptional = getCurrentStep();
        if (currentRecycleStepOptional.isEmpty()) return;
        RecycleStep recycleStep = currentRecycleStepOptional.get();
        if (!recycleStep.isRequiresWasteDisposal()) return;

        boolean confirmed = CustomOptionPane.showConfirmDialog(config, this,
                "WARNING: The tilting table is going to move.\n" +
                "Are all body parts clear of the platform?", "Tilting Table Warning");
        System.out.println(confirmed);
        if (!confirmed) return;
        String material = recycleStep.getMaterialToDispose();
    }

    private void nextStep() {
        currentStepIndex++;
        getCurrentStep().ifPresentOrElse(currentStep -> {
            currentStepInstructionLabel.setText(currentStep.getInstruction());
            disposeButton.setEnabled(currentStep.isRequiresWasteDisposal());
            nextStepButton.setText(currentStep.isRequiresWasteDisposal() ? SKIP_TEXT : NEXT_TEXT);
        }, () -> {
            state = StepState.AFTER;
            currentStepInstructionLabel.setText("Finished Recycling Product");
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
        if (state == StepState.DURING) {
            //todo: consider if currently recycling (i.e. state of tilt table)
            //if the table is currently not in the standard position then
            //show a dialogue before moving it back (JInputBox?)
        }
        mainWindow.mainScreen();
    }

}

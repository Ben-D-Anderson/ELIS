package org.elis.recycle.gui.screens;

import org.elis.recycle.database.Product;
import org.elis.recycle.database.RecycleStep;
import org.elis.recycle.files.Config;
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
    private int currentStepIndex = 0;
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
        leftHalf.anchor = GridBagConstraints.WEST;
        leftHalf.fill = GridBagConstraints.BOTH;
        leftHalf.gridx = 1;
        leftHalf.weightx = 1;
        GridBagConstraints rightHalf = new GridBagConstraints();
        rightHalf.anchor = GridBagConstraints.EAST;
        rightHalf.fill = GridBagConstraints.BOTH;
        rightHalf.gridx = 2;
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

        finishButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
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
                stepsPanel.removeAll();
                state = StepState.DURING;
                actionButtonsPanel.add(disposeButton, leftHalf);
                actionButtonsPanel.add(nextStepButton, rightHalf);
                stepsPanel.add(currentStepInstructionLabel, leftHalf);
                stepsPanel.add(actionButtonsPanel, rightHalf);
                nextStep();
            }
        });
        stepsPanel.add(beginRecycleButton, centre);

        add(stepsPanel, remainder);
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
            actionButtonsPanel.remove(nextStepButton);

            GridBagConstraints centre = new GridBagConstraints();
            centre.fill = GridBagConstraints.BOTH;
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

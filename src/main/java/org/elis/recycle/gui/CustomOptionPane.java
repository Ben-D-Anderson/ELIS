package org.elis.recycle.gui;

import org.elis.recycle.files.Config;

import javax.swing.*;
import java.awt.*;

public class CustomOptionPane {

    public static boolean showConfirmDialog(Config config,
                                            Component parent,
                                            String message,
                                            String title) {
        message = "<html><font face='" + config.getUserInterfaceSettings()
                .getPopUpSettings().getMessageFontFamily() + "' size='"
                + config.getUserInterfaceSettings()
                .getPopUpSettings().getMessageFontSize() + "'>"
                + message.replace("\n", "<br />")
                + "</font><br /><br /><br /></html>";
        String[] options = {
                // The YES Button.
                "<html><font face='" + config.getUserInterfaceSettings()
                        .getPopUpSettings().getMessageFontFamily() + "' size='"
                        + config.getUserInterfaceSettings()
                        .getPopUpSettings().getButtonFontSize() + "'>YES</font></html>",
                // The NO button.
                "<html><font face='" + config.getUserInterfaceSettings()
                        .getPopUpSettings().getMessageFontFamily() + "' size='"
                        + config.getUserInterfaceSettings()
                        .getPopUpSettings().getButtonFontSize() + "'>NO</font></html>"};
        return 0 == JOptionPane.showOptionDialog(parent, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
    }

    public static void showMessageDialog(Config config,
                                         Component parent,
                                         String message,
                                         String title,
                                         int messageType) {
        message = "<html><font face='" + config.getUserInterfaceSettings()
                .getPopUpSettings().getMessageFontFamily() + "' size='"
                + config.getUserInterfaceSettings()
                .getPopUpSettings().getMessageFontSize() + "'>"
                + message.replace("\n", "<br />")
                + "</font><br /><br /><br /></html>";
        String[] options = {
                // The OK Button.
                "<html><font face='" + config.getUserInterfaceSettings()
                        .getPopUpSettings().getMessageFontFamily() + "' size='"
                        + config.getUserInterfaceSettings()
                        .getPopUpSettings().getButtonFontSize() + "'>OK</font></html>"};
        JOptionPane.showOptionDialog(parent, message, title,
                JOptionPane.DEFAULT_OPTION, messageType,
                null, options, options[0]);
    }

}

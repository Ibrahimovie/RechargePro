package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author kingfans
 */
public class JTextFieldHintListener implements FocusListener {
    private String hintText;
    private JTextField textField;

    public JTextFieldHintListener(JTextField jTextField, String hintText) {
        this.textField = jTextField;
        this.hintText = hintText;
        jTextField.setText(hintText);
        jTextField.setForeground(Color.GRAY);
    }

    @Override
    public void focusGained(FocusEvent e) {
        String temp = textField.getText();
        if (temp.equals(hintText)) {
            textField.setText("");
            textField.setForeground(Color.BLACK);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        String temp = textField.getText();
        if ("".equals(temp)) {
            textField.setForeground(Color.GRAY);
            textField.setText(hintText);
        }
    }
}

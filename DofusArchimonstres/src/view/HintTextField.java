package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class HintTextField extends JTextField implements FocusListener {

	private static final long serialVersionUID = 1L;
	private final String hint;
	private boolean showingHint;

	public HintTextField(final String hint) {
		super(hint);
		this.hint = hint;
		this.showingHint = true;
		setHorizontalAlignment(JTextField.CENTER);
		setFont(getFont().deriveFont(Font.ITALIC));
		setForeground(Color.gray);
		super.addFocusListener(this);
	}

	public void focusGained(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText("");
			showingHint = false;
			setFont(getFont().deriveFont(Font.PLAIN));
	        setForeground(Color.black);
		}
	}

	public void focusLost(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText(hint);
			setFont(getFont().deriveFont(Font.ITALIC));
			setForeground(Color.gray);
			showingHint = true;
		}
	}

	public String getText() {
		return showingHint ? "" : super.getText();
	}
}
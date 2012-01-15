package org.celllife.idart.gui.patient;

import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TextAdapter {

	private Text text;
	
	public TextAdapter() {
	}

	public TextAdapter(Composite parent, int style) {
		text = new Text(parent, style);
	}
	
	public void setData(String key, Object value){
		text.setData(key, value);
	}

	public void setFocus(){
		text.setFocus();
	}

	public void setBounds(Rectangle bounds){
		text.setBounds(bounds);
	}

	public void setFont(Font font){
		text.setFont(font);
	}

	public void addKeyListener(KeyListener listener){
		text.addKeyListener(listener);
	}

	public void setText(String string){
		text.setText(string);
	}

	public String getText(){
		return text.getText();
	}

	public void removeFocusListener(FocusListener listener){
		text.removeFocusListener(listener);
	}

	public void setEditable(boolean b){
		text.setEditable(b);
	}

	public void setEnabled(boolean b){
		text.setEnabled(b);
	}

	public void addFocusListener(FocusListener listener){
		text.addFocusListener(listener);
	}

	public void forceFocus(){
		text.forceFocus();
	}

}
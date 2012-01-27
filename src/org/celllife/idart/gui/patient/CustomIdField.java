package org.celllife.idart.gui.patient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class CustomIdField extends TextAdapter {
	
	private final class KeyAdapterExtension extends KeyAdapter {
		int limit = Integer.MAX_VALUE;
		private final Text partNext;
		private final Text partThis;
		private final Text partPrevious;

		public KeyAdapterExtension(Text partThis, Text partNext, Text partPrevious) {
			this.partThis = partThis;
			this.partNext = partNext;
			this.partPrevious = partPrevious;
			limit = partThis.getTextLimit();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.keyCode != SWT.BS && e.keyCode != SWT.CR
					&& e.keyCode != SWT.DEL && e.keyCode != SWT.ESC
					&& e.keyCode != SWT.LF && e.keyCode != SWT.TAB 
					&& e.keyCode != SWT.ARROW && e.keyCode != SWT.ARROW_DOWN
					&& e.keyCode != SWT.ARROW_LEFT && e.keyCode != SWT.ARROW_RIGHT
					&& e.keyCode != SWT.ARROW_UP) {
				
				
				int length = partThis.getText().length();
				if (length == limit && partNext != null){
					partNext.setFocus();
					partNext.append(String.valueOf(e.character));
				}
				return;
			}
			
			if (e.keyCode == SWT.BS){
				int length = partThis.getText().length();
				if (length == 0 && partPrevious != null){
					partPrevious.setFocus();
					String text = partPrevious.getText();
					partPrevious.setText(text.substring(0, text.length()-1));
					partPrevious.setSelection(text.length(), text.length());
				}
				return;
			}
			
			if (e.keyCode == SWT.ARROW_LEFT){
				int position = partThis.getCaretPosition();
				if (position == 0 && partPrevious != null){
					partPrevious.setFocus();
					String text = partPrevious.getText();
					partPrevious.setSelection(text.length(), text.length());
				}
				return;
			}
			
			if (e.keyCode == SWT.ARROW_RIGHT){
				int position = partThis.getCaretPosition();
				String text = partThis.getText();
				if (position == text.length() && partNext != null){
					partNext.setFocus();
				}
				return;
			}
		}
	}

	private static final int PART1_LEN = 3;
	private static final int PART2_LEN = 9;
	private static final int PART3_LEN = 6;
	private static final int PART4_LEN = 5;
	private static final double TOTOAL_LEN = PART1_LEN + PART2_LEN + PART3_LEN + PART4_LEN;

	private Text part1;
	private Text part2;
	private Text part3;
	private Text part4;
	
	public CustomIdField(Composite parent, int style) {
		part1 = new Text(parent, style);
		part1.setTextLimit(PART1_LEN);
		
		part2 = new Text(parent, style);
		part2.setTextLimit(PART2_LEN);
		
		part3 = new Text(parent, style);
		part3.setTextLimit(PART3_LEN);
		
		part4 = new Text(parent, style);
		part4.setTextLimit(PART4_LEN);
		
		part1.addKeyListener(new KeyAdapterExtension(part1, part2, null));
		part2.addKeyListener(new KeyAdapterExtension(part2, part3, part1));
		part3.addKeyListener(new KeyAdapterExtension(part3, part4, part2));
		part4.addKeyListener(new KeyAdapterExtension(part4, null, part3));
	}

	@Override
	public void setData(String key, Object value) {
		part1.setData(key, value);	
	}

	@Override
	public void setFocus() {
		part1.setFocus();		
	}

	@Override
	public void setBounds(Rectangle bounds) {
		int factoredWidth = (int) Math.ceil(bounds.width / TOTOAL_LEN);
		int w1 = factoredWidth * PART1_LEN;
		part1.setBounds(bounds.x, bounds.y, w1, bounds.height);
		int w2 = factoredWidth * PART2_LEN;
		part2.setBounds(bounds.x + w1, bounds.y, w2, bounds.height);
		int w3 = factoredWidth * PART3_LEN;
		part3.setBounds(bounds.x + w1+w2, bounds.y, w3, bounds.height);
		int w4 = factoredWidth * PART4_LEN;
		part4.setBounds(bounds.x + w1+w2+w3, bounds.y, w4, bounds.height);
	}

	@Override
	public void setFont(Font font) {
		part1.setFont(font);		
		part2.setFont(font);		
		part3.setFont(font);		
		part4.setFont(font);		
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		part1.addKeyListener(listener);
		part2.addKeyListener(listener);
		part3.addKeyListener(listener);
		part4.addKeyListener(listener);
	}

	@Override
	public void setText(String text) {
		splitText(text);
	}

	private void splitText(String text) {
		part1.setText(getPart(text, PART1_LEN, 0));
		part2.setText(getPart(text, PART2_LEN, PART1_LEN));
		part3.setText(getPart(text, PART3_LEN, PART1_LEN + PART2_LEN));
		part4.setText(getPart(text, PART4_LEN, PART1_LEN + PART2_LEN + PART3_LEN));
	}
	
	private String getPart(String text, int currentPartLength, int previousLength){
		int currentTotalLength = previousLength + currentPartLength;
		if (text.length() >= currentTotalLength){
			return text.substring(previousLength, currentTotalLength);
		} else if (text.length() >= previousLength) {
			return text.substring(previousLength);
		}
		return "";
	}

	@Override
	public String getText() {
		return combineText();
	}

	private String combineText() {
		return part1.getText() + part2.getText() + part3.getText()
				+ part4.getText();
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		part1.removeFocusListener(listener);
		part2.removeFocusListener(listener);
		part3.removeFocusListener(listener);
		part4.removeFocusListener(listener);
	}

	@Override
	public void setEditable(boolean b) {
		part1.setEditable(b);		
		part2.setEditable(b);		
		part3.setEditable(b);		
		part4.setEditable(b);		
	}

	@Override
	public void setEnabled(boolean b) {
		part1.setEnabled(b);		
		part2.setEnabled(b);		
		part3.setEnabled(b);		
		part4.setEnabled(b);		
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		part1.addFocusListener(listener);		
		part2.addFocusListener(listener);		
		part3.addFocusListener(listener);		
		part4.addFocusListener(listener);		
	}

	@Override
	public void forceFocus() {
		part1.forceFocus();		
	}
}

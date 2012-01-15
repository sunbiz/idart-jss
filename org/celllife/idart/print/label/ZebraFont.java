package org.celllife.idart.print.label;

/**
 */
public class ZebraFont {

	// 203 dpi
	private int fontNumber;
	private int width;
	private int height;

	/**
	 * Constructor for ZebraFont.
	 * @param fontNumber int
	 * @param width int
	 * @param height int
	 */
	public ZebraFont(int fontNumber, int width, int height) {
		super();
		this.fontNumber = fontNumber;
		this.width = width;
		this.height = height;
	}

	/**
	 * Method getFontNumber.
	 * @return int
	 */
	public int getFontNumber() {
		return fontNumber;
	}

	/**
	 * Method setFontNumber.
	 * @param fontNumber int
	 */
	public void setFontNumber(int fontNumber) {
		this.fontNumber = fontNumber;
	}

	/**
	 * Method getHeight.
	 * @return int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Method setHeight.
	 * @param height int
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Method getWidth.
	 * @return int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Method setWidth.
	 * @param width int
	 */
	public void setWidth(int width) {
		this.width = width;
	}

}

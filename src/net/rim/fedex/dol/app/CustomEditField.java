package net.rim.fedex.dol.app;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BasicEditField;

public class CustomEditField extends BasicEditField {

	public CustomEditField(String label, String initialValue, int maxNumChars,
			long style) {
		super(label, initialValue, maxNumChars, style);
	}

	private int iRectX = getFont().getAdvance(getLabel());

	public int getPreferredWidth() {
		return super.getMaxSize() * super.getFont().getAdvance("X");
	}

	public int getPreferredHeight() {
		return super.getPreferredHeight();
	}

	public void layout(int width, int height) {
		width = Math.min(width, getPreferredWidth());
		height = Math.min(height, getPreferredHeight());
		super.layout(width, height);
		setExtent(width, height);

	}

	public void paint(Graphics g) {
		super.paint(g);
		if (isFocus()) {
			g.setColor(Color.RED);
			g.drawRect(iRectX, 0, getPreferredWidth(), getPreferredHeight());
		} else {
			g.setColor(Color.BLACK);
			g.drawRect(iRectX, 0, getPreferredWidth(), getPreferredHeight());
		}
	}

}
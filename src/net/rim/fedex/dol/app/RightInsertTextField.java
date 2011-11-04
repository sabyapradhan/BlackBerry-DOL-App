package net.rim.fedex.dol.app;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;

public class RightInsertTextField extends BasicEditField {
    private String lastGoodTxtInputEntry = null;
    private int maxCharInputLength = 20;
    private Field nextFieldForFocus = null;

/**
 * Basic constructor.
 * 
 * @param defaultValue
 */
public RightInsertTextField(String defaultValue) {
    super("", defaultValue);
}

public void paint(Graphics g) {
    String txt = this.getText();
    // 1. (Optional) keeping a check on input length can help
    // minimize custom code needed to handle wrap-around text.
    if (txt.length() > this.getMaxCharInputLength() && this.getMaxCharInputLength() > 0) {
        txt = this.lastGoodTxtInputEntry;
        this.setText(txt);
    } else {
        this.lastGoodTxtInputEntry = txt;
    }

    // 2. get rid of the default cursor painting by coloring over it with
    // the same color as the background
    XYRect xy = g.getClippingRect();
    g.setBackgroundColor(Color.WHITE);
    g.fillRect(xy.x, xy.y, xy.width, xy.height);
    g.clear();

    // 3. Align text to the right.

    g.setColor(Color.BLACK);
    g.drawText(txt, 0, 0, (DrawStyle.RIGHT + DrawStyle.ELLIPSIS), getWidth());

}

/**
 * Look out for 'ENTER' being pressed.
 */
public boolean keyChar(char key, int status, int time) {
    // Prevent new lines in input field.
    if (Characters.ENTER == key) {
        if (this.nextFieldForFocus != null) {
            this.nextFieldForFocus.setFocus();
        }
        return true;
    } else {
        return super.keyChar(key, status, time);
    }
}

public void setMaxCharInputLength(int maxCharInputLength) {
    this.maxCharInputLength = maxCharInputLength;
}

public int getMaxCharInputLength() {
    return maxCharInputLength;
}

/**
 * @param nextFieldForFocus
 *            the nextFieldForFocus to set if 'ENTER' is pressed.
 */
public void setNextFieldForFocus(Field nextFieldForFocus) {
    this.nextFieldForFocus = nextFieldForFocus;
}

}
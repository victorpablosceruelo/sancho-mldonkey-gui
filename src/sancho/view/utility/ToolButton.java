/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ToolButton {
  private boolean active;
  private Image bigActiveImage;
  private Image bigInactiveImage;
  private SelectionListener listener;
  private Image smallActiveImage;
  private Image smallInactiveImage;
  private String text;
  private ToolItem toolItem;
  private int toolItemStyle;
  private String toolTipText;
  private boolean useSmallButtons;

  public ToolButton(ToolBar parent, int style) {
    this(parent, style, -1);
  }

  public ToolButton(ToolBar parent, int style, int index) {
    this.toolItemStyle = SWT.RADIO;

    if (index < 0)
      toolItem = new ToolItem(parent, toolItemStyle);
    else
      toolItem = new ToolItem(parent, style, toolItemStyle);
  }

  public void addSelectionListener(SelectionListener listener) {
    this.listener = listener;
    toolItem.addSelectionListener(listener);
  }

  public void dispose() {
    toolItem.dispose();
  }

  public ToolBar getParent() {
    return toolItem.getParent();
  }

  public Image getSmallActiveImage() {
    return this.smallActiveImage;
  }

  public String getText() {
    return this.text;
  }

  public ToolItem getToolItem() {
    return toolItem;
  }

  public void resetImage() {
    setHotImage(useSmallButtons ? smallActiveImage : bigActiveImage);
    setImage(useSmallButtons ? smallInactiveImage : bigInactiveImage);
  }

  public void resetItem(ToolBar newtoolbar) {
    toolItem.dispose();
    toolItem = new ToolItem(newtoolbar, toolItemStyle);
    setText(text);
    setToolTipText(toolTipText);
    setActive(active);
    addSelectionListener(listener);
    resetImage();
  }

  public void setActive(boolean toggle) {
    toolItem.setSelection(toggle);
    active = toggle;
  }

  public void setBigActiveImage(Image image) {
    bigActiveImage = image;
  }

  public void setBigInactiveImage(Image image) {
    bigInactiveImage = image;
  }

  public void setHotImage(Image image) {
    toolItem.setHotImage(image);
  }

  public void setImage(Image image) {
    toolItem.setImage(image);
  }

  public void setSmallActiveImage(Image image) {
    smallActiveImage = image;
  }

  public void setSmallInactiveImage(Image image) {
    smallInactiveImage = image;
  }

  public void setText(String text) {
    this.text = text;
    toolItem.setText(text);
  }

  public void setToolTipText(String text) {
    this.toolTipText = text;
    toolItem.setToolTipText(text);
  }

  public void useSmallButtons(boolean useSmall) {
    useSmallButtons = useSmall;
  }
}
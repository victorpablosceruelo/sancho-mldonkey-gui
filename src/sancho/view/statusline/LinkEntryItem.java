/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.StatusLine;
import sancho.view.utility.LinkRipper;
import sancho.view.utility.SResources;

public class LinkEntryItem {
  protected Composite linkEntryComposite;
  protected StatusLine statusLine;
  protected Composite composite;
  protected boolean linkEntryToggle = false;

  public LinkEntryItem(StatusLine statusLine) {
    this.linkEntryComposite = statusLine.getLinkEntryComposite();
    this.statusLine = statusLine;
    this.composite = statusLine.getStatusline();
    createContents();
  }

  public void createContents() {
    Composite linkComposite = new Composite(composite, SWT.NONE);
    linkComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_VERTICAL));
    linkComposite.setLayout(new FillLayout());

    final ToolBar toolBar = new ToolBar(linkComposite, SWT.FLAT);

    ToolItem httpAdd = new ToolItem(toolBar, SWT.NONE);

    httpAdd.setImage(SResources.getImage("http-add"));
    httpAdd.setToolTipText(SResources.getString("sl.httpAdd"));
    httpAdd.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        InputDialog dialog = new InputDialog(toolBar.getShell(), SResources.getString("sl.http.title"),
            SResources.getString("sl.http.linkTo"), SResources.S_ES, null);
        dialog.open();

        String result = dialog.getValue();

        if (result != null)
          Sancho.send(OpCodes.S_CONSOLE_MESSAGE, "http " + result);
      }
    });

    ToolItem linkRip = new ToolItem(toolBar, SWT.NONE);

    linkRip.setImage(SResources.getImage("web-link-12"));
    linkRip.setToolTipText(SResources.getString("sl.rip"));
    linkRip.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {

        LinkRipper linkRipper = statusLine.getMainWindow().getLinkRipper();
        if (linkRipper != null) {
          linkRipper.setFocus();
          return;
        }
        linkRipper = statusLine.getMainWindow().openLinkRipper();
        setupLinkRipper(linkRipper);
        linkRipper.open();
      }
    });

    final ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);

    toolItem.setImage(SResources.getImage("up_arrow_green"));
    toolItem.setToolTipText(SResources.getString("sl.linkEntry"));
    toolItem.addSelectionListener(new SelectionAdapter() {
      // hide/show the linkEntry
      public void widgetSelected(SelectionEvent e) {
        GridData g = new GridData(GridData.FILL_HORIZONTAL);
        if (linkEntryToggle) {
          toolItem.setImage(SResources.getImage("up_arrow_green"));
          g.heightHint = 0;
        } else {
          toolItem.setImage(SResources.getImage("down_arrow_green"));
          g.heightHint = 75;
        }
        linkEntryToggle = !linkEntryToggle;
        linkEntryComposite.setLayoutData(g);
        statusLine.getMainWindow().getMainComposite().layout();
      }
    });

  }

  public void setupLinkRipper(LinkRipper linkRipper) {
  }

}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.Option;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.Spinner;
import sancho.view.utility.WidgetFactory;

// ugly - fix this
public class BandwidthDialog extends Dialog {
  int max_download;
  int max_upload;
  int max_slots;
  int max_concurrent;
  int preset;

  Combo presetCombo;

  Spinner upSpinner;
  Spinner downSpinner;
  Spinner slotsSpinner;
  Spinner maxConcurrentSpinner;

  Option upOption;
  Option downOption;
  Option slotsOption;
  Option maxConcurrentOption;

  public BandwidthDialog(Shell shell) {
    super(shell == null ? new Shell() : shell);
    setBlockOnOpen(false);

    if (Sancho.hasCollectionFactory()) {
      ICore core = Sancho.getCore();

      downOption = (Option) core.getOptionCollection().get("max_hard_download_rate");
      upOption = (Option) core.getOptionCollection().get("max_hard_upload_rate");
      slotsOption = (Option) core.getOptionCollection().get("max_upload_slots");
      maxConcurrentOption = (Option) core.getOptionCollection().get("max_concurrent_downloads");

      try {
        if (downOption != null)
          max_download = Integer.parseInt(downOption.getValue());
        if (upOption != null)
          max_upload = Integer.parseInt(upOption.getValue());
        if (slotsOption != null)
          max_slots = Integer.parseInt(slotsOption.getValue());
        if (maxConcurrentOption != null)
          max_concurrent = Integer.parseInt(maxConcurrentOption.getValue());

      } catch (Exception e) {
      }
    }
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(SResources.getString("l.bandwidthSettings"));
    newShell.setImage(SResources.getImage("ProgramIcon"));
  }

  protected Spinner createOption(Composite composite, Option option, int initValue) {
    if (option != null) {
      Label l = new Label(composite, SWT.NONE);
      l.setText(option.getName());
      l.setToolTipText(option.getDescription());
      l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Spinner spinner = new Spinner(composite, SWT.NONE);
      spinner.setMinimum(0);
      spinner.setMaximum(10000);
      spinner.setSelection(initValue);
      spinner.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

      return spinner;
    }
    return null;
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 5, 5, false));

    presetCombo = new Combo(composite, SWT.READ_ONLY);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    presetCombo.setLayoutData(gd);
    String[] resItems = {"bw.default", "bw.preset1", "bw.preset2", "bw.preset3"};

    String[] items = new String[resItems.length];
    for (int i = 0; i < items.length; i++)
      items[i] = SResources.getString(resItems[i]);

    presetCombo.setItems(items);
    presetCombo.select(0);

    presetCombo.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        Combo combo = (Combo) e.widget;
        int a, b, c, d;
        preset = combo.getSelectionIndex();

        a = max_download;

        if (combo.getSelectionIndex() == 0) {
          a = max_download;
          b = max_upload;
          c = max_slots;
          d = max_concurrent;
        } else {
          a = PreferenceLoader.loadInt("bwPreset" + preset + "_download");
          b = PreferenceLoader.loadInt("bwPreset" + preset + "_upload");
          c = PreferenceLoader.loadInt("bwPreset" + preset + "_slots");
          d = PreferenceLoader.loadInt("bwPreset" + preset + "_concurrent");
        }
        downSpinner.setSelection(a);
        upSpinner.setSelection(b);
        slotsSpinner.setSelection(c);
        maxConcurrentSpinner.setSelection(d);
      }

    });

    downSpinner = createOption(composite, downOption, max_download);

    upSpinner = createOption(composite, upOption, max_upload);

    slotsSpinner = createOption(composite, slotsOption, max_slots);

    maxConcurrentSpinner = createOption(composite, maxConcurrentOption, max_concurrent);

    return composite;
  }

  protected void buttonPressed(int buttonId) {
    if (buttonId == Dialog.OK && Sancho.hasCollectionFactory()) {
      ICore core = Sancho.getCore();
      if (upOption != null) {
        if (preset > 0)
          PreferenceLoader.getPreferenceStore().setValue("bwPreset" + preset + "_upload",
              upSpinner.getSelection());

        upOption.setValue(String.valueOf(upSpinner.getSelection()));

      }
      if (downOption != null) {
        if (preset > 0)
          PreferenceLoader.getPreferenceStore().setValue("bwPreset" + preset + "_download",
              downSpinner.getSelection());
        downOption.setValue(String.valueOf(downSpinner.getSelection()));

      }
      if (slotsOption != null) {
        PreferenceLoader.getPreferenceStore().setValue("bwPreset" + preset + "_slots",
            slotsSpinner.getSelection());

        slotsOption.setValue(String.valueOf(slotsSpinner.getSelection()));

      }
      if (maxConcurrentOption != null) {
        PreferenceLoader.getPreferenceStore().setValue("bwPreset" + preset + "_concurrent",
            maxConcurrentSpinner.getSelection());
        maxConcurrentOption.setValue(String.valueOf(maxConcurrentSpinner.getSelection()));

      }
    }

    super.buttonPressed(buttonId);
  }

}
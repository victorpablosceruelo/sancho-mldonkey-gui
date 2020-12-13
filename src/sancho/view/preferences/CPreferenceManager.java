/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.Option;
import sancho.model.mldonkey.OptionCollection;
import sancho.utility.VersionInfo;
import sancho.view.utility.SResources;

public class CPreferenceManager extends PreferenceManager {
  private PreferenceDialog prefDialog;
  private PreferenceStore preferenceStore;

  public CPreferenceManager(PreferenceStore preferenceStore) {
    this.preferenceStore = preferenceStore;
    addNode(getRoot(), new RootPreferencePage(VersionInfo.getName() + ": "
        + SResources.getString("p.node.main")));
    addNode(getRoot(), new DisplayPreferencePage(VersionInfo.getName() + ": "
        + SResources.getString("p.node.display")));
    addWinRegistryPage(getRoot());
  }

  protected IPreferenceNode addNode(IPreferenceNode preferenceNode, CPreferencePage cPreferencePage) {
    cPreferencePage.setPreferenceStore(preferenceStore);
    IPreferenceNode pNode = new PreferenceNode(cPreferencePage.getTitle(), cPreferencePage);
    preferenceNode.add(pNode);
    return pNode;
  }

  protected void addWinRegistryPage(IPreferenceNode rootNode) {
    if (VersionInfo.getOSPlatform().equals("Windows") || Sancho.debug)
      addNode(rootNode, new WinRegPreferencePage(VersionInfo.getName() + ": "
          + SResources.getString("p.node.windowsRegistry")));
  }

  public int open(Shell shell) {
    try {
      initialize(preferenceStore);
    } catch (IOException e) {
      Sancho.pDebug("PM: " + e);
    }

    prefDialog = new PreferenceDialog(shell, this);
    PreferenceDialog.setDefaultImage(SResources.getImage("ProgramIcon"));

    if (Sancho.hasCollectionFactory())
      createMLDonkeyOptions(Sancho.getCore());

    return prefDialog.open();
  }

  private void createMLDonkeyOptions(ICore mldonkey) {
    OptionCollection options = mldonkey.getOptionCollection();
    MLDonkeyPreferenceStore optionsStore = new MLDonkeyPreferenceStore();
    optionsStore.setInput(options);

    Map sections = new HashMap();
    Map plugins = new HashMap();
    MLDonkeyPreferencePage advanced = null;

    // iterate over options
    for (Iterator it = options.keySet().iterator(); it.hasNext();) {
      Option option = (Option) options.get(it.next());
      String section = option.getSection();
      String plugin = option.getPlugin();

      if (((section == null) && (plugin == null))
          || ((section != null) && (section.equalsIgnoreCase("other"))))
        advanced = addAdvancedOption(advanced, option, optionsStore);
      else if (section != null)
        addToMap(sections, section, optionsStore, option);
      else if (plugin != null)
        addToMap(plugins, plugin, optionsStore, option);
    }

    // Sort & create
    addSortedOptions(sections, getRoot());

    // plugins: build branch off of the "Networks" page
    if (plugins.size() != 0) {
      IPreferenceNode pluginOptions = find("Networks");

      if (pluginOptions == null) {
        MLDonkeyPreferencePage emptyItem = new MLDonkeyPreferencePage("Networks",
            FieldEditorPreferencePage.FLAT);
        pluginOptions = new PreferenceNode("Networks", emptyItem);
        emptyItem.setEmpty(true);
        addToRoot(pluginOptions);
      }
      addSortedOptions(plugins, pluginOptions);
    }
    // advanced on the bottom
    if (advanced != null)
      addToRoot((new PreferenceNode("Advanced", advanced)));
  }

  private void addSortedOptions(Map map, IPreferenceNode preferenceNode) {
    String[] sArray = new String[map.keySet().size()];
    map.keySet().toArray(sArray);
    Arrays.sort(sArray, String.CASE_INSENSITIVE_ORDER);

    for (int i = 0; i < sArray.length; i++) {
      MLDonkeyPreferencePage page = (MLDonkeyPreferencePage) map.get(sArray[i]);
      preferenceNode.add((new PreferenceNode(sArray[i], page)));
    }
  }

  private void addToMap(Map map, String key, MLDonkeyPreferenceStore optionsStore, Option option) {
    if (!map.containsKey(key)) {
      MLDonkeyPreferencePage mldonkeyOptions = new MLDonkeyPreferencePage(key, FieldEditorPreferencePage.GRID);
      map.put(key, mldonkeyOptions);
      mldonkeyOptions.setPreferenceStore(optionsStore);
    }
    ((MLDonkeyPreferencePage) map.get(key)).addOption(option);
  }

  private MLDonkeyPreferencePage addAdvancedOption(MLDonkeyPreferencePage advanced, Option option,
      MLDonkeyPreferenceStore optionsStore) {
    if (advanced == null) {
      advanced = new MLDonkeyPreferencePage(SResources.getString("l.advanced") + "*",
          FieldEditorPreferencePage.GRID);
      advanced.setPreferenceStore(optionsStore);
    }
    advanced.addOption(option);
    return advanced;
  }

  public void initialize(PreferenceStore preferenceStore) throws IOException {
    try {
      preferenceStore.load();
    } catch (IOException e) {
      preferenceStore.save();
      preferenceStore.load();
    }
  }

}
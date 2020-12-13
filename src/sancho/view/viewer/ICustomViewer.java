/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IInputProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ViewerSorter;

public interface ICustomViewer extends IInputProvider {
  int[] getColumnIDs();
  void setColumnIDs(String string);
  void setContentProvider(IContentProvider provider);
  void setLabelProvider(IBaseLabelProvider labelProvider);
  void setSorter(ViewerSorter sorter);
  void setInput(Object input);
  void setEditors(boolean b);
  void closeAllTTE();
  void updateDisplay();
  void updateSelection(ISelection selection);
}

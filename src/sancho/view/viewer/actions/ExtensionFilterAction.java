/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.view.viewer.GView;
import sancho.view.viewer.filters.AbstractViewerFilter;
import sancho.view.viewer.filters.FileExtensionViewerFilter;

public class ExtensionFilterAction extends AbstractFilterAction {

	public ExtensionFilterAction(String name, GView gViewer, AbstractEnum enumExtension) {
		super(name, Action.AS_CHECK_BOX, gViewer, enumExtension);
		this.filterClass = FileExtensionViewerFilter.class;
	}

	public AbstractViewerFilter createNewFilter() {
		return new FileExtensionViewerFilter(gView);
	}

}

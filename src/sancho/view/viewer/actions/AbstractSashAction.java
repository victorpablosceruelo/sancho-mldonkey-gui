/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.SashForm;

public abstract class AbstractSashAction extends Action {
	protected SashForm sashForm;

	public AbstractSashAction( SashForm sashForm ) {
		this.sashForm = sashForm;
	}
}

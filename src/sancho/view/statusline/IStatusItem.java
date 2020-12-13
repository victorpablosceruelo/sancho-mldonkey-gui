/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline;

import java.util.Observer;

public interface IStatusItem extends Observer {
	void setConnected(boolean connected);
}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server.users;

import sancho.model.mldonkey.User;
import sancho.view.viewer.table.GTableLabelProvider;

public class ServerUsersTableLabelProvider extends GTableLabelProvider {
	public ServerUsersTableLabelProvider(ServerUsersTableView cTableViewer) {
		super(cTableViewer);
	}

	public String getColumnText(Object element, int columnIndex) {

		User user = (User) element;
		switch (cViewer.getColumnIDs()[columnIndex]) {
			case ServerUsersTableView.NAME :
				return user.getName();
			case ServerUsersTableView.TAGS :
				return user.getTagsString();
			case ServerUsersTableView.ADDR :
				return user.getAddr().toString();
			case ServerUsersTableView.PORT :
				return String.valueOf(user.getPort());
			default :
				return "??";
		}
	}
}
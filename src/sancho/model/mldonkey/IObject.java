/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.model.mldonkey.utility.MessageBuffer;

public interface IObject {
  void read(MessageBuffer messageBuffer);
  void deleteObservers();
}

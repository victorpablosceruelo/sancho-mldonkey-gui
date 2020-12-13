/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class ResourcesImageDescriptor extends ImageDescriptor {

  String imageKey;

  public ResourcesImageDescriptor(String key, Image image) {
    this.imageKey = key + "_IMG";
    SResources.putImage(this.imageKey, image);
  }

  /**
   * Creates and returns a new SWT image for this image descriptor. Note that
   * each call returns a new SWT image object.
   * 
   * @return a new image
   */
  public Image createImage() {
    return createImage(true);
  }

  /**
   * Creates and returns a new SWT image for this image descriptor. Note that
   * if returnMissingImageOnError is true a default image is returned
   * 
   * @param returnMissingImageOnError
   *          The flag that determines if a default image is returned on error
   * @return a new image or null if the image could not be created
   */
  public Image createImage(boolean returnMissingImageOnError) {
    return createImage(returnMissingImageOnError, Display.getCurrent());
  }

  /**
   * Creates and returns a new SWT image for this image descriptor. Note that
   * if returnMissingImageOnError is true a default image is returned
   * 
   * @param returnMissingImageOnError
   *          The flag that determines if a default image is returned on error
   * @param device
   *          the device on which to create the image
   * @return a new image or null if the image could not be created
   * @since 2.0
   */
  public Image createImage(boolean returnMissingImageOnError, Device device) {
    return SResources.getImage(this.imageKey);
  }

  /**
   * Creates and returns a new SWT image for this image descriptor.
   * 
   * @param device
   *          the device on which to create the image
   * @return a new image or null if the image could not be created
   * @since 2.0
   */
  public Image createImage(Device device) {
    return createImage(true, device);
  }

  public ImageData getImageData() {
    return SResources.getImage(this.imageKey).getImageData();
  }
}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class UniformResourceLocator extends ByteArrayTransfer {

    private static final String TYPENAME1 = "UniformResourceLocator";
    private static final String TYPENAME2 = "text/x-moz-url-data"; // testing..
    private static final int TYPEID1 = registerType(TYPENAME1);
    private static final int TYPEID2 = registerType(TYPENAME2);
    private static UniformResourceLocator _instance = new UniformResourceLocator();

    /**
     * @return UniformResourceLocator
     */
    public static UniformResourceLocator getInstance() {
        return _instance;
    }
    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.Transfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
     */
    public Object nativeToJava(TransferData transferData) {

        if (isSupportedType(transferData)) {
            byte[] buffer = (byte[]) super.nativeToJava(transferData);
            if (buffer == null) return null;

            int nullAt = 0;
            for (int i = 0; i < buffer.length && buffer[i] != 0; i++) {
                nullAt++;
            }
            return new String(buffer, 0, nullAt);
        }

        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
     * requires 2+ or gtk crashes
     */
    protected String[] getTypeNames() {
        return new String[] { TYPENAME1, TYPENAME2 };
    }
    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
     */
    protected int[] getTypeIds() {
        return new int[] { TYPEID1, TYPEID2 };
    }
}

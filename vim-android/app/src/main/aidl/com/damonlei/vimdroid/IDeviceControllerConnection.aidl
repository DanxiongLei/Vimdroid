// IWindowInspectorInterface.aidl
package com.damonlei.vimdroid;

import com.damonlei.vimdroid.IDeviceController;

interface IDeviceControllerConnection {
    void bindInspector(IDeviceController controller);
}

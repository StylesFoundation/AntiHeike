package wtf.styles.antiheike.util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModulesScanner {
    public static Collection<String> scanModules() {
        List<String> moduleList = new ArrayList<>();
        WinNT.HANDLE processHandle = null;
        try {
            int pid = Kernel32.INSTANCE.GetCurrentProcessId();
            processHandle = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ, false, pid);
            WinDef.HMODULE[] modules = new WinDef.HMODULE[1024];
            IntByReference needed = new IntByReference();
            if (Psapi.INSTANCE.EnumProcessModules(processHandle, modules, modules.length * Native.POINTER_SIZE, needed)) {
                int moduleCount = needed.getValue() / Native.POINTER_SIZE;
                for (int i = 0; i < moduleCount; i++) {
                    char[] moduleName = new char[1024];
                    Psapi.INSTANCE.GetModuleFileNameExW(processHandle, modules[i], moduleName, moduleName.length);
                    moduleList.add(Native.toString(moduleName));
                }
            }
        } finally {
            if (processHandle != null) {
                Kernel32.INSTANCE.CloseHandle(processHandle);
            }
        }
        return moduleList;
    }

}

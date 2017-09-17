package com.hazemam.InstantScreenOff;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Hazem AbuMostafa on 2017/04/28.
 */
public class AnimationDisabler implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Wait for system services to be loaded:
        if (!lpparam.packageName.equals("android"))
            return;

        XposedBridge.log("[InstantScreenOff]: Ready.");

        // Hook the `animateScreenStateChange(int target, boolean performScreenOffTransition)`
        // method in `server.display.DisplayPowerController` class:
        XposedHelpers.findAndHookMethod(
            XposedHelpers.findClass("com.android.server.display.DisplayPowerController", lpparam.classLoader),
            "animateScreenStateChange", int.class, boolean.class,
            // Using `XC_MethodReplacement` to block original call and replace arguments:
            new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    // Overwrite `performScreenOffTransition` argument to always be 'false':
                    param.args[1] = false;

                    // Recall the original method:
                    return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                }
            }
        );
    }
}

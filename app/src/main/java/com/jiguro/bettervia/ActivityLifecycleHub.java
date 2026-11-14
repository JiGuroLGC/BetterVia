package com.jiguro.bettervia;

import android.app.Activity;
import android.os.Bundle;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import java.util.ArrayList;
import java.util.List;

public final class ActivityLifecycleHub {

    public interface Listener {
        void onActivityCreated(Activity activity, Bundle savedInstanceState);
    }

    private static final class Entry {
        final String key;
        Listener listener;
        boolean enabled;

        Entry(String key, Listener listener, boolean enabled) {
            this.key = key;
            this.listener = listener;
            this.enabled = enabled;
        }
    }

    private static final List<Entry> sEntries = new ArrayList<Entry>();
    private static boolean sInstalled = false;

    private ActivityLifecycleHub() {}

    public static void add(String key, Listener listener, boolean enabled) {
        synchronized (sEntries) {
            Entry found = null;
            for (Entry e : sEntries) {
                if (e.key.equals(key)) {
                    found = e;
                    break;
                }
            }
            if (found == null) {
                sEntries.add(new Entry(key, listener, enabled));
            } else {
                found.listener = listener;
                found.enabled = enabled;
            }
            ensureInstalledLocked();
        }
    }

    public static boolean isEnabled(String key) {
        synchronized (sEntries) {
            for (Entry e : sEntries) {
                if (e.key.equals(key)) return e.enabled;
            }
        }
        return false;
    }

    public static void setEnabled(String key, boolean enabled) {
        synchronized (sEntries) {
            for (Entry e : sEntries) {
                if (e.key.equals(key)) {
                    e.enabled = enabled;
                    break;
                }
            }
            ensureInstalledLocked();
        }
    }

    private static void ensureInstalledLocked() {
        if (sInstalled) return;
        boolean anyEnabled = false;
        for (Entry e : sEntries) {
            if (e.enabled) {
                anyEnabled = true;
                break;
            }
        }
        if (!anyEnabled) return;
        try {
            XposedHelpers.findAndHookMethod(
                    Activity.class,
                    "onCreate",
                    Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            dispatch(
                                    (Activity) param.thisObject,
                                    param.args != null && param.args.length > 0
                                            ? (Bundle) param.args[0]
                                            : null);
                        }
                    });
            sInstalled = true;
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 生命周期分发安装失败: " + t);
        }
    }

    private static void dispatch(Activity activity, Bundle savedInstanceState) {
        Listener[] listeners;
        String[] keys;
        synchronized (sEntries) {
            int n = 0;
            for (Entry e : sEntries) {
                if (e.enabled) n++;
            }
            if (n == 0) return;
            listeners = new Listener[n];
            keys = new String[n];
            int i = 0;
            for (Entry e : sEntries) {
                if (!e.enabled) continue;
                listeners[i] = e.listener;
                keys[i] = e.key;
                i++;
            }
        }
        for (int i = 0; i < listeners.length; i++) {
            try {
                listeners[i].onActivityCreated(activity, savedInstanceState);
            } catch (Throwable t) {
                Hook.bvLog("[BetterVia] 生命周期回调异常(" + keys[i] + "): " + t);
            }
        }
    }
}

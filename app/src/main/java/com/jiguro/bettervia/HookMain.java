package com.jiguro.bettervia;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;
import java.lang.reflect.Method;
public class HookMain implements IXposedHookLoadPackage {
	private final String modulePkg = "com.jiguro.bettervia"; 
	private final String logicClass = "com.jiguro.bettervia.Hook"; 
	private final String logicMethod = "handleLoadPackage"; 
	@Override
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				Context ctx = (Context) param.args[0];
				lpparam.classLoader = ctx.getClassLoader();
				File apk = findApk(ctx, modulePkg);
				if (apk == null)
					throw new RuntimeException("找不到模块 apk");
				PathClassLoader pcl = new PathClassLoader(apk.getAbsolutePath(), ClassLoader.getSystemClassLoader());
				Class<?> clazz = Class.forName(logicClass, true, pcl);
				Object instance = clazz.newInstance();
				Method method = clazz.getDeclaredMethod(logicMethod, XC_LoadPackage.LoadPackageParam.class);
				method.invoke(instance, lpparam);
			}
		});
	}
	private File findApk(Context ctx, String pkg) {
		try {
			Context modCtx = ctx.createPackageContext(pkg,
					Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
			return new File(modCtx.getPackageCodePath());
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
	}
}
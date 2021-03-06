package com.stardust.scriptdroid.ui.main.operation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.execution.ScriptExecutionListener;
import com.stardust.autojs.execution.SimpleScriptExecutionListener;
import com.stardust.autojs.runtime.ScriptInterruptedException;
import com.stardust.autojs.script.FileScriptSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.external.shortcut.Shortcut;
import com.stardust.scriptdroid.external.shortcut.ShortcutActivity;
import com.stardust.scriptdroid.scripts.ScriptFile;
import com.stardust.scriptdroid.scripts.sample.Sample;
import com.stardust.scriptdroid.ui.edit.EditActivity;
import com.stardust.util.AssetsCache;

/**
 * Created by Stardust on 2017/1/23.
 */

public abstract class ScriptFileOperation {

    public static final String ACTION_ON_RUN_FINISHED = "ACTION_ON_RUN_FINISHED";
    public static final String EXTRA_EXCEPTION_MESSAGE = "EXTRA_EXCEPTION_MESSAGE";


    private static final ScriptExecutionListener RUN_ON_EDIT_VIEW_SCRIPT_EXECUTION_LISTENER = new SimpleScriptExecutionListener() {

        @Override
        public void onSuccess(ScriptExecution execution, Object result) {
            App.getApp().sendBroadcast(new Intent(ACTION_ON_RUN_FINISHED));
        }

        @Override
        public void onException(ScriptExecution execution, Exception e) {
            if (ScriptInterruptedException.causedByInterrupted(e)) {
                return;
            }
            App.getApp().sendBroadcast(new Intent(ACTION_ON_RUN_FINISHED)
                    .putExtra(EXTRA_EXCEPTION_MESSAGE, e.getMessage()));
        }

    };


    public static void openByOtherApps(String path) {
        Uri uri = Uri.parse("file://" + path);
        App.getApp().startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "text/plain").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void createShortcut(ScriptFile scriptFile) {
        new Shortcut(App.getApp()).name(scriptFile.getSimplifiedName())
                .targetClass(ShortcutActivity.class)
                .icon(R.drawable.ic_node_js_black)
                .extras(new Intent().putExtra(CommonUtils.EXTRA_KEY_PATH, scriptFile.getPath()))
                .send();
    }


    public static void edit(ScriptFile file) {
        EditActivity.editFile(App.getApp(), file.getSimplifiedName(), file.getPath());
    }

    public static void run(ScriptFile file) {
        AutoJs.getInstance().getScriptEngineService().execute(new FileScriptSource(file));
    }

    public static void run(Context context, Sample file) {
        AutoJs.getInstance().getScriptEngineService().execute(new StringScriptSource(file.name, AssetsCache.get(context.getAssets(), file.path)));
    }

    public static ScriptExecution runOnEditView(ScriptSource scriptSource) {
        return AutoJs.getInstance().getScriptEngineService().execute(scriptSource, RUN_ON_EDIT_VIEW_SCRIPT_EXECUTION_LISTENER);
    }

}
package com.ix.dm.stepcounter.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u00042\u0006\u0010\u0006\u001a\u00020\u0007JI\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00072\u0016\u0010\n\u001a\u0012\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\r2!\u0010\u000e\u001a\u001d\u0012\u0013\u0012\u00110\u0010\u00a2\u0006\f\b\u0011\u0012\b\b\u0012\u0012\u0004\b\b(\u0013\u0012\u0004\u0012\u00020\t0\u000fJ\u0016\u0010\u0014\u001a\n \u0005*\u0004\u0018\u00010\u00150\u00152\u0006\u0010\u0006\u001a\u00020\u0007J\u001a\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\f2\b\u0010\u0018\u001a\u0004\u0018\u00010\fH\u0007J\u0010\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u001bH\u0007\u00a8\u0006\u001c"}, d2 = {"Lcom/ix/dm/stepcounter/util/Constant;", "", "()V", "editor", "Landroid/content/SharedPreferences$Editor;", "kotlin.jvm.PlatformType", "context", "Landroid/content/Context;", "getPermission", "", "permissions", "Ljava/util/ArrayList;", "", "Lkotlin/collections/ArrayList;", "onComplete", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "done", "getSharePref", "Landroid/content/SharedPreferences;", "printLog", "tag", "message", "setUpStatusBar", "activity", "Landroid/app/Activity;", "app_debug"})
public final class Constant {
    public static final com.ix.dm.stepcounter.util.Constant INSTANCE = null;
    
    public final android.content.SharedPreferences getSharePref(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final android.content.SharedPreferences.Editor editor(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.LOLLIPOP)
    public final void setUpStatusBar(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    @android.annotation.SuppressLint(value = {"LogNotTimber"})
    public final void printLog(@org.jetbrains.annotations.NotNull()
    java.lang.String tag, @org.jetbrains.annotations.Nullable()
    java.lang.String message) {
    }
    
    public final void getPermission(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.String> permissions, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onComplete) {
    }
    
    private Constant() {
        super();
    }
}
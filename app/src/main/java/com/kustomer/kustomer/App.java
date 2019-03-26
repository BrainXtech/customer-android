package com.kustomer.kustomer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.kustomer.kustomersdk.Kustomer;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static final String K_KUSTOMER_API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVhZmFmMGUzNmQ4OGZlMDAxYjlmNDU5NiIsInVzZXIiOiI1YWZhZjBlMzdhYTdhMWE1Zjc3N2FjYWEiLCJvcmciOiI1YTVmNmNhM2I1NzNmZDAwMDFhZjczZGQiLCJvcmdOYW1lIjoienp6LWJ4LXRlY2hub2xvZ2llcyIsInVzZXJUeXBlIjoibWFjaGluZSIsInJvbGVzIjpbIm9yZy51c2VyIiwib3JnLmFkbWluIiwib3JnLnRyYWNraW5nIl0sImF1ZCI6InVybjpjb25zdW1lciIsImlzcyI6InVybjphcGkiLCJzdWIiOiI1YWZhZjBlMzdhYTdhMWE1Zjc3N2FjYWEifQ.N464DdmiB55U_CJ-cTlLRsZtunRm2BqEDhiFYuMImCY0";

    @Override
    public void onCreate() {
        super.onCreate();

        Kustomer.init(this, K_KUSTOMER_API_KEY);
        Fabric.with(this, new Crashlytics());
    }

}

package com.hello.hello_matrix_flutter.src.auth;

import android.content.Context;

import org.matrix.android.sdk.api.Matrix;
import org.matrix.android.sdk.api.MatrixConfiguration;
import org.matrix.android.sdk.api.session.Session;

public class SessionHolder {
    public static Context appContext;
    public static Session matrixSession;

    public static Matrix getMatrixInstance() {
        return Matrix.Companion.getInstance(appContext);
    }
}

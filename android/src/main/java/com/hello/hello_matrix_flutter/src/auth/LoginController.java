package com.hello.hello_matrix_flutter.src.auth;

import android.net.Uri;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.matrix.android.sdk.api.MatrixCallback;
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig;
import org.matrix.android.sdk.api.auth.data.LoginFlowResult;
import org.matrix.android.sdk.api.session.Session;

import io.flutter.plugin.common.MethodChannel;
import kotlin.Unit;

public class LoginController {

    public void checkSession(@NonNull MethodChannel.Result result) {
        Session lastSession = SessionHolder.matrixSession;
        if (lastSession == null) {
            result.success(false);
            return;
        } else if (!lastSession.isOpenable()) {
            result.success(false);
            return;
        } else {
            result.success(true);
            return;
        }
    }

    public void login(@NonNull final MethodChannel.Result result, String homeServer, final String userName, final String password) {
        HomeServerConnectionConfig homeServerConnectionConfig = null;
        try {
            homeServerConnectionConfig = new HomeServerConnectionConfig.Builder()
                    .withHomeServerUri(Uri.parse(homeServer))
                    .build();
        } catch (Exception e) {
            result.error("-1", e.getMessage(), false);
            return;
        }

        SessionHolder.getMatrixInstance().authenticationService().getLoginFlow(homeServerConnectionConfig, new MatrixCallback<LoginFlowResult>() {
            @Override
            public void onSuccess(LoginFlowResult loginFlowResult) {
                SessionHolder.getMatrixInstance().authenticationService().getLoginWizard().login(userName, password, (android.os.Build.MANUFACTURER + android.os.Build.MODEL), new MatrixCallback<Session>() {
                    @Override
                    public void onSuccess(Session session) {
                        SessionHolder.matrixSession = session;
                        SessionHolder.matrixSession.open();
                        SessionHolder.matrixSession.startSync(true);
                        result.success(true);
                    }

                    @Override
                    public void onFailure(@NotNull Throwable throwable) {
                        result.error("", throwable.toString(), false);
                        return;
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                result.error("", throwable.toString(), false);
                return;
            }
        });
    }

    public void logout(@NonNull final MethodChannel.Result result){

        if (SessionHolder.matrixSession == null){
            result.error("-1","Session is null",false);
            return;
        }

        try {
            SessionHolder.matrixSession.signOut(true, new MatrixCallback<Unit>() {
                @Override
                public void onSuccess(Unit unit) {
                    SessionHolder.matrixSession = null;
                    result.success(true);
                }

                @Override
                public void onFailure(@NotNull Throwable throwable) {
                    result.success(false);
                }
            });
        }catch (Exception e){
            result.error("-1","Sync is still running",false);
        }
    }

}

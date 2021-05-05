package com.hello.hello_matrix_flutter.src.auth;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.google.gson.JsonObject;
import com.hello.hello_matrix_flutter.src.directory.DirectoryClient;
import com.hello.hello_matrix_flutter.src.directory.DirectoryController;
import com.hello.hello_matrix_flutter.src.directory.DirectoryInstance;
import com.hello.hello_matrix_flutter.src.storage.DataStorage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.matrix.android.sdk.api.MatrixCallback;
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig;
import org.matrix.android.sdk.api.auth.data.LoginFlowResult;
import org.matrix.android.sdk.api.auth.login.LoginWizard;
import org.matrix.android.sdk.api.session.Session;

import java.io.IOException;

import io.flutter.plugin.common.MethodChannel;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class LoginController {

    String _tag = "LoginController";
    DirectoryController directoryController = new DirectoryController();

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

    //step 1->get login (getting profile_
    public void login(@NonNull final MethodChannel.Result result, final String homeServer, final String userName, final String password) {
        DirectoryClient directoryClient = new DirectoryClient();

        //setting the request body
        HttpUrl.Builder urlBuilder = HttpUrl.parse(directoryClient.getEndpoint("profile")).newBuilder();
        urlBuilder.addQueryParameter("email", userName);

        directoryClient.okHttpClient.newCall(directoryClient.getRequest(urlBuilder.build().toString())).enqueue(new Callback() {
            @Override
            @UiThread
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(_tag, "error in getting response using async okhttp call");
                // handleErrorOnMainThread(result);
                handleLoginResponseOnMainThread(result, false);
                return;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (!response.isSuccessful()) {
                    handleLoginResponseOnMainThread(result, false);
                    return;
                }
                try {
                    String profileData = response.body().string();
                    //profile data exist, continue Matrix login
                    syncDirectoryForFirstTime(result, homeServer, userName, password, profileData);
                } catch (IOException e) {
                    handleLoginResponseOnMainThread(result, false);
                    //result.error("", "User not exist", false);
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    //step 2->get syncDirectoryForFirstTime
    private void syncDirectoryForFirstTime(@NonNull final MethodChannel.Result result, final String homeServer, final String userName, final String password, final String profileData) {

        DirectoryClient directoryClient = new DirectoryClient();

        String orgCode = "";
        String userCode = "";
        try {
            JSONObject profileDataJson = new JSONObject(profileData);
            orgCode = profileDataJson.getString("org_prefix");
            userCode = profileDataJson.getString("hello_id");
        } catch (JSONException e) {
            e.printStackTrace();
            handleLoginResponseOnMainThread(result, false);
            return;
        }
        //setting the request body
        HttpUrl.Builder
                urlBuilder = HttpUrl.parse(directoryClient.getEndpoint("global_contacts")).newBuilder();
        urlBuilder.addQueryParameter("orgCode", orgCode);
        urlBuilder.addQueryParameter("userCode", userCode);

        directoryClient.okHttpClient.newCall(directoryClient.getRequest(urlBuilder.build().toString())).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(_tag, "error in getting response using async okhttp call");
                handleLoginResponseOnMainThread(result, false);
                //handleErrorOnMainThread(result);
                return;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (!response.isSuccessful()) {
                    handleLoginResponseOnMainThread(result, false);
                    //handleErrorOnMainThread(result);
                    return;
                }
                try {
                    String directoryData = response.body().string();
                    directoryController.initDB();
                    directoryController.saveNewDataInLocalDbAndUpdateInstance(null, directoryData);
                    mxLogin(result, homeServer, userName, password, profileData);
                    //profile data exist, continue Matrix login
                } catch (IOException | JSONException e) {
                    handleLoginResponseOnMainThread(result, false);
                    e.printStackTrace();
                    return;
                }
            }
        });

    }

  /*  @AnyThread
    private void handleErrorOnMainThread(@NonNull final MethodChannel.Result result) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                result.error("", "User not exist", false);
            }
        });
    }*/

    @AnyThread
    private void handleLoginResponseOnMainThread(@NonNull final MethodChannel.Result result, final boolean value) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                result.success(value);
            }
        });
        return;
    }

    //step 3->try matrix login
    private void mxLogin(@NonNull final MethodChannel.Result result, String homeServer, final String userName, final String password, final String profileData) {
        HomeServerConnectionConfig homeServerConnectionConfig = null;
        try {
            homeServerConnectionConfig = new HomeServerConnectionConfig.Builder()
                    .withHomeServerUri(homeServer)
                    .build();
        } catch (Exception e) {
            handleLoginResponseOnMainThread(result, false);
            //result.error("-1", e.getMessage(), false);
            return;
        }

        LoginFlowResult loginFlowResult = (LoginFlowResult) SessionHolder.getMatrixInstance().authenticationService().getLoginFlow(homeServerConnectionConfig, new Continuation<LoginFlowResult>() {
            @Override
            public @NotNull CoroutineContext getContext() {
                return null;
            }

            @Override
            public void resumeWith(@NotNull Object o) {
                //LoginFlowResult lfResult = (LoginFlowResult) o;
                LoginWizard loginWizard = SessionHolder.getMatrixInstance().authenticationService().getLoginWizard();

            }
        });
        handleLoginResponseOnMainThread(result, false);
        return;

        /*loginWizard.login(userName, password, (android.os.Build.MANUFACTURER + android.os.Build.MODEL), new Continuation<Session>() {
            @Override
            public @NotNull CoroutineContext getContext() {
                handleLoginResponseOnMainThread(result, false);
                return null;
            }

            @Override
            public void resumeWith(@NotNull Object o) {
                Session session = (Session)o;
                SessionHolder.matrixSession = session;
                SessionHolder.matrixSession.open();
                SessionHolder.matrixSession.startSync(true);
                //store profile data
                DataStorage dataStorage = new DataStorage();
                dataStorage.storeStringData(DataStorage.KEY_PROFILE_STORAGE, profileData);
                ///
                //setDisplayNameFromProfile(result, dataStorage);
                handleLoginResponseOnMainThread(result, true);
            }
        });*/

       /* SessionHolder.getMatrixInstance().authenticationService().getLoginFlow(homeServerConnectionConfig, new MatrixCallback<LoginFlowResult>() {
            @Override
            public void onSuccess(LoginFlowResult loginFlowResult) {
                SessionHolder.getMatrixInstance().authenticationService().getLoginWizard().login(userName, password, (android.os.Build.MANUFACTURER + android.os.Build.MODEL), new MatrixCallback<Session>() {
                    @Override
                    public void onSuccess(Session session) {

                    }

                    @Override
                    public void onFailure(@NotNull Throwable throwable) {

                        //result.error("", throwable.toString(), false);
                        return;
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                handleLoginResponseOnMainThread(result, false);
                //result.error("", throwable.toString(), false);
                return;
            }
        });*/
    }

    //step 4->set display name
   /* private void setDisplayNameFromProfile(@NonNull final MethodChannel.Result result, DataStorage dataStorage) {
        Log.i(_tag,dataStorage.getStringData(DataStorage.KEY_PROFILE_STORAGE));
        try {
            JSONObject jsonObject = new JSONObject(dataStorage.getStringData(DataStorage.KEY_PROFILE_STORAGE));
            String displayName = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
            SessionHolder.matrixSession.setDisplayName(SessionHolder.matrixSession.getMyUserId(), displayName, new MatrixCallback<Unit>() {
                @Override
                public void onSuccess(Unit unit) {
                    handleLoginResponseOnMainThread(result, true);
                }

                @Override
                public void onFailure(@NotNull Throwable throwable) {
                    handleLoginResponseOnMainThread(result, false);
                    logout(null);
                    return;
                }
            });
        } catch (JSONException e) {
            handleLoginResponseOnMainThread(result, false);
            e.printStackTrace();
            return;
        }


    }*/

    public void logout(@NonNull final MethodChannel.Result result) {

        if (SessionHolder.matrixSession == null) {
            if (result != null) {
                result.error("-1", "Session is null", false);
            }
            return;
        }

        try {

            SessionHolder.matrixSession.signOut(true, new Continuation<Unit>() {
                @Override
                public @NotNull CoroutineContext getContext() {
                    if (result != null) {
                        result.success(false);
                    }
                    return null;
                }

                @Override
                public void resumeWith(@NotNull Object o) {
                    SessionHolder.matrixSession = null;
                    //erase all SP data
                    DataStorage dataStorage = new DataStorage();
                    dataStorage.eraseAllData();

                    //erase directory
                    new DirectoryController().eraseDirectory();
                    //////////////////////////
                    if (result != null) {
                        result.success(true);
                    }
                }
            });

            /*SessionHolder.matrixSession.signOut(true, new MatrixCallback<Unit>() {
                @Override
                public void onSuccess(Unit unit) {
                    SessionHolder.matrixSession = null;
                    //erase all SP data
                    DataStorage dataStorage = new DataStorage();
                    dataStorage.eraseAllData();

                    //erase directory
                    new DirectoryController().eraseDirectory();
                    //////////////////////////
                    if (result != null) {
                        result.success(true);
                    }

                }

                @Override
                public void onFailure(@NotNull Throwable throwable) {
                    if (result != null) {
                        result.success(false);
                    }

                }
            });*/
        } catch (Exception e) {
            if (result != null) {
                result.error("-1", "Sync is still running", false);
            }

        }
    }

    public void getProfile(@NonNull final MethodChannel.Result result) {
        DataStorage dataStorage = new DataStorage();
        String profileData = dataStorage.getStringData(DataStorage.KEY_PROFILE_STORAGE);
        if (profileData != null) {
            result.success(profileData);
        } else {
            result.error("-1", "Profile data not found", null);
        }
    }

}

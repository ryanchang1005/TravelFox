package com.travelfox.ryan.ui.login;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.travelfox.ryan.R;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.ui.main.MainActivity;
import com.travelfox.ryan.api.request.LoginByGoogleRequest;
import com.travelfox.ryan.api.response.LoginResponse;
import com.travelfox.ryan.utils.PrefUtils;


public class LoginActivity extends BaseActivity {
    public static final int RC_SIGN_IN = 100;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.btnGoogleLogin);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void loginByGoogle(String token) {
        LoginByGoogleRequest request = new LoginByGoogleRequest();
        request.login_platform = LoginByGoogleRequest.LOGIN_PLATFORM_GOOGLE;
        request.token = token;
        getPlannerService().login(request, (statusCode, responseBody, throwable) -> {
            LoginResponse rsp = (LoginResponse) handleBasicOnResponse(statusCode, responseBody, throwable, LoginResponse.class);
            if (rsp != null) {
                getPrefUtils().setString(PrefUtils.USER_ID, rsp.user_id);
                getPrefUtils().setString(PrefUtils.ACCESS_TOKEN, rsp.access_token);
                openActivity(MainActivity.class);
                finish();
            }
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            loginByGoogle(account.getIdToken());
        } catch (ApiException e) {
            e.printStackTrace();
            toast("Google登入失敗");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}
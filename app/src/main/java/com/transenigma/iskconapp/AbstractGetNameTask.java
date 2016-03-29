package com.transenigma.iskconapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class AbstractGetNameTask extends AsyncTask<Void, Void, Void>{

    protected login mActivity;
    public static String GOOGLE_USER_DATA = "No data";
    protected String mScope;
    protected String mEmail = "", fullName = "";
    protected int mRequest;
    //ProgressDialog progressDialog;

    public AbstractGetNameTask(login mActivity, String mEmail, String mScope) {
        this.mActivity = mActivity;
        this.mEmail = mEmail;
        this.mScope = mScope;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            fetchNameFromProfileServer();
        } catch (IOException ex) {
            onError("Following Error occured, please try again. "
                    + ex.getMessage(), ex);
        } catch (JSONException e) {
            onError("Bad response: "
                    + e.getMessage(), e);
        }
        return null;
    }

    protected void onError(String msg, Exception e) {
        if(e != null) {
            Log.e("", "Exception: " , e);
        }
    }

    protected abstract String fetchToken() throws IOException;

    private void fetchNameFromProfileServer() throws IOException, JSONException {
        String token = fetchToken();
        URL url = new URL("https://www.googleapis.com/oauth2" + "/v1/userinfo?access_token=" + token);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        int sc = con.getResponseCode();
        if(sc == 200) {
            InputStream is = con.getInputStream();
            GOOGLE_USER_DATA = readResponse(is);
            is.close();
            //progressDialog = ProgressDialog.show( mActivity, "Please Wait!", "Authenticating", true);
            try {
                JSONObject profileData = new JSONObject(AbstractGetNameTask.GOOGLE_USER_DATA);
                if(profileData.has("name")) {
                    fullName = profileData.getString("name");
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
            saveNewUserToParse();
            return;
        } else if(sc == 401) {
            GoogleAuthUtil.invalidateToken(mActivity, token);
            onError("Server auth error: ", null);
        } else {
            onError("Returned by server: " + sc, null);
            return;
        }
    }

    private void saveNewUserToParse() {
        String legalname = "", surname = "";
        ParseUser user = new ParseUser();
        if(!mEmail.equals("")) {
            user.setUsername(mEmail);
            user.setPassword(mEmail);
            user.put("email",mEmail);
        }
        if(!fullName.equals("")) {
            String[] parts = fullName.split(" ");
            legalname = parts[0]; // 004
            surname = parts[parts.length - 1];
        }
        if(!legalname.equals(""))
            user.put("legalname", legalname);

        if(!surname.equals(""))
            user.put("surname", surname);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Radhe", "Radhe! Parse signup is success");
                    ParseUser.logInInBackground(mEmail,mEmail, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                Log.i("TAG", "Radhe! Parse login is success user is ");
                                //progressDialog.dismiss();
                                mActivity.startActivity(new Intent(mActivity , home.class));
                                mActivity.finish();
                                Toast.makeText(mActivity,"Successfully Logged in. Welcome "+user.getString("legalname"),Toast.LENGTH_LONG).show();
                                login.progressDialogLogin.dismiss();
                            } else {
                                Log.i("TAG", "Radhe! Parse login is failure");
                                login.progressDialogLogin.dismiss();
                                Toast.makeText(mActivity,"Log In Failed",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                    Log.i("Radhe", "Radhe! Parse signup is failure " + e);
                    ParseUser.logInInBackground(mEmail,mEmail, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                Log.i("TAG", "Radhe! Parse login is success user is ");
                                mActivity.startActivity(new Intent(mActivity , home.class));
                                mActivity.finish();
                                Toast.makeText(mActivity,"Successfully Logged in. Welcome "+user.getString("legalname"),Toast.LENGTH_LONG).show();
                                login.progressDialogLogin.dismiss();
                            } else {
                                Log.i("TAG", "Radhe! Parse login is failure");
                                Toast.makeText(mActivity,"Log In Failed",Toast.LENGTH_LONG).show();
                                login.progressDialogLogin.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }

        return new String(bos.toByteArray(), "UTF-8");
    }
}

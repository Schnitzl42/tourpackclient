package at.sti_innsbruck.tourpackclient.logic.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AsyncHttp {

    private ProgressDialog mProgressDialog;


    public void getJSON(AsyncHttpRequestListener listener, Context context, Request request){

        AnimatedRequest animatedRequest = new AnimatedRequest(listener, context, request);
        animatedRequest.execute();
    }


    private class AnimatedRequest extends AsyncTask<Void, String, JSONObject> {

        private AsyncHttpRequestListener listener;
        private Context context;
        private Request request;

        public AnimatedRequest(AsyncHttpRequestListener listener, Context context, Request request){
            this.listener = listener;
            this.context = context;
            this.request = request;
        }

        @Override
        protected void onPreExecute() {
            //show loading dialog
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("searching offers...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }


        @Override
        protected JSONObject doInBackground(Void... args) {
            //retrieve json response
            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                JSONObject result = new JSONObject( response.body().string());
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            //hide loading dialog
            if(mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            //callback with result
            listener.processResult(json);
        }
    }
}

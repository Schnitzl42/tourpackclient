package at.sti_innsbruck.tourpackclient.logic.http;

import org.json.JSONObject;


public interface AsyncHttpRequestListener {

    public void processResult(JSONObject result);
}

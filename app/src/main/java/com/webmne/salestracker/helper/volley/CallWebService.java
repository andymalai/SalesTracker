package com.webmne.salestracker.helper.volley;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;

import org.json.JSONObject;


/**
 * @author Made by Dhruvil Patel
 *         <p>
 *         </p>
 *         This is a custom class that represents the impmementation of Volly
 *         classes
 *         <p>
 *         <li>
 *         <h6>JsonObjectRequest</h6></li> <li>
 *         <h6>JsonArrayRequest</h6></li>
 *         <p>
 *         </p>
 *         <li>You have to give two params in this class as mentioned below</li>
 *         <li>And then write obj.call() to call webservice</li>
 *         <p>
 *         <p>
 *         </p>
 *         <p>
 *         </p>
 *         * <blockquote></blockquote>
 *         <p/>
 *         <code>CallWebService obj = new CallWebService() {
 * @Override public void response(String response) { // TODO Auto-generated
 * method stub
 * <p/>
 * <p/>
 * }
 * @Override public void error(VolleyError error) { // TODO Auto-generated
 * method stub
 * <p>
 * <p>
 * } }; <p></p>
 * <p>
 * obj.setJsonObjectRequest(true); <p></p>
 * obj.setUrl(AppConstants.SERVICE_URL); <p></p> obj.call();</code>
 */
public abstract class CallWebService implements IService {

    public Context context;

    public abstract void response(String response);

    public abstract void error(VolleyError error);

    public abstract void noInternet();

    private String url;
    String response = null;

    JSONObject userObject;

    public static int TYPE_GET = 100;
    public static int TYPE_POST = 200;

    public static int TYPE_JSONOBJECT = 0;
    public static int TYPE_JSONARRAY = 1;
    public static int TYPE_STRING = 2;
    public int type = 0, methodType = 0;


    public CallWebService(Context context, String url, int type) {
        super();
        this.context = context;
        this.url = url;
        this.methodType = type;
    }

    public CallWebService(Context context, String url, int methodType, JSONObject userObject) {
        super();
        this.context = context;
        this.url = url;
        this.methodType = methodType;
        this.userObject = userObject;
    }


    public synchronized final CallWebService start() {
        call();
        return this;
    }

    public void call() {

        switch (methodType) {

            // case  for requesting json object, GET type
            case 100:
                if (Functions.isConnected(context)) {
                    JsonObjectRequest request = new JsonObjectRequest(url, null,
                            new Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject jobj) {

                                    response(jobj.toString());
                                }
                            }, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError e) {
                            // TODO Auto-generated method stub
                            error(e);
                        }
                    });

                    request.setRetryPolicy(
                            new DefaultRetryPolicy(
                                    0,
                                    0,
                                    0));
                    MyApplication.getInstance().addToRequestQueue(request);
                } else {
                    noInternet();
                }

                break;

            // case for requesting json object, POST Type
            case 200:

                if (Functions.isConnected(context)) {
                    JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.POST, url, userObject, new Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jobj) {
                            response(jobj.toString());

                        }
                    }, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError e) {
                            error(e);
                        }
                    });
                    request2.setRetryPolicy(
                            new DefaultRetryPolicy(
                                    0,
                                    0,
                                    0));
                    MyApplication.getInstance().addToRequestQueue(request2);

                } else {
                    noInternet();
                }

                break;

            case 2:

                break;

        }

    }


}

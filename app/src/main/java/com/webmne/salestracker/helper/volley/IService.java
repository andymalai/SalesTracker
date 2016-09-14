package com.webmne.salestracker.helper.volley;

import com.android.volley.VolleyError;

public interface IService {

	public void response(String response);

	public void error(VolleyError error);

	public void noInternet();
}

package com.javastaff.openwhisk;

import com.google.gson.JsonObject;

public class UpperCaseAction {
	public static JsonObject main(JsonObject args) {
		String testo = args.getAsJsonPrimitive("testo").getAsString();
		JsonObject response = new JsonObject();
		response.addProperty("testo", testo.toUpperCase());
		return response;
	}
}

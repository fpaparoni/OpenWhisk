package com.javastaff.openwhisk;

import com.google.gson.JsonObject;

public class HelloAction {
	public static JsonObject main(JsonObject args) {
		String nome = args.getAsJsonPrimitive("nome")!=null ? 
				args.getAsJsonPrimitive("nome").getAsString() : "OpenWhisk";
		JsonObject response = new JsonObject();
		response.addProperty("risposta", "Ciao " + nome + "!");
		System.out.println("HelloAction invocatata con parametro "+nome);
		return response;
	}
}

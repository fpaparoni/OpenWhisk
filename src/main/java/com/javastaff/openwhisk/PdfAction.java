package com.javastaff.openwhisk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.google.gson.JsonObject;

public class PdfAction {
	public static JsonObject main(JsonObject args) throws IOException {
		String testo = args.getAsJsonPrimitive("testo").getAsString();
		PDDocument document = new PDDocument();
		PDPage page=new PDPage();
		
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		contentStream.beginText(); 
		contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
		contentStream.newLineAtOffset(25, 500);
		contentStream.showText(testo);      
		contentStream.endText();
		contentStream.close();
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		document.save(baos);
		JsonObject response = new JsonObject();
		response.addProperty("pdffile", Base64.getEncoder().encodeToString(baos.toByteArray()));
		return response;
	}
}

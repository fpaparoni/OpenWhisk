package com.javastaff.openwhisk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.gson.JsonObject;

/***
 * Action che prende in input un pdf base64
 * applica un immagine come logo e ritorna il pdf modificato come base64
 */
public class LogoStamper {
	
	public static JsonObject main(JsonObject args) throws Exception {
		
		byte[] pdffile=Base64.getDecoder().decode(args.getAsJsonPrimitive("pdffile").getAsString());
		byte[] imageFile=readByte("https://github.com/fpaparoni/OpenWhisk/raw/master/logo.png");
		String filename = "logo.png";
		
		PDDocument document = PDDocument.load(pdffile);
		float scale = 0.5f;
		PDImageXObject ximage = PDImageXObject.createFromByteArray(document, imageFile, filename);
		float deltaX = ximage.getWidth() * scale;
		float deltaY = ximage.getHeight() * scale;

		PDPage page = document.getDocumentCatalog().getPages().get(0);

		PDPageContentStream contentStream = new PDPageContentStream(document, page,
				PDPageContentStream.AppendMode.APPEND, true);
		contentStream.drawImage(ximage, page.getMediaBox().getUpperRightX() - deltaX,
				page.getMediaBox().getUpperRightY() - deltaY, deltaX, deltaY);
		contentStream.close();
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		document.save(baos);
		document.close();
		
		String pdfreturn=Base64.getEncoder().encodeToString(baos.toByteArray());
		JsonObject response = new JsonObject();
		response.addProperty("pdffile", pdfreturn);
		return response;
	}
	
	/**
	 * Legge un file da un url e lo converte in array di byte
	 * 
	 * @param fileUrl
	 * @return
	 * @throws Exception
	 */
	public static byte[] readByte(String fileUrl) throws Exception {
		URL url=new URL(fileUrl);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
		  is = url.openStream ();
		  byte[] byteChunk = new byte[4096];
		  int n;

		  while ( (n = is.read(byteChunk)) > 0 ) {
		    baos.write(byteChunk, 0, n);
		  }
		}
		catch (IOException e) {
		  System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
		  e.printStackTrace ();
		}
		finally {
		  if (is != null) { is.close(); }
		}
		return baos.toByteArray();
	}
}
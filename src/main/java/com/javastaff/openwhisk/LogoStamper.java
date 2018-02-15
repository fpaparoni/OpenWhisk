package com.javastaff.openwhisk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
	
	public static byte[] readByte(String fileUrl) throws Exception {
		URL url=new URL(fileUrl);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
		  is = url.openStream ();
		  byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
		  int n;

		  while ( (n = is.read(byteChunk)) > 0 ) {
		    baos.write(byteChunk, 0, n);
		  }
		}
		catch (IOException e) {
		  System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
		  e.printStackTrace ();
		  // Perform any other exception handling that's appropriate.
		}
		finally {
		  if (is != null) { is.close(); }
		}
		return baos.toByteArray();
	}
	
	public static void main(String[] args) throws Exception {
		String filename = "timbro.png";
		ClassLoader classLoader = LogoStamper.class.getClassLoader();
		File imageFile=new File(classLoader.getResource(filename).getFile());
		FileInputStream fis=new FileInputStream(imageFile);
		System.out.println(imageFile.exists());
//		String pdfFilePath = "/home/federico/pdf-test.pdf";
//		String signatureImagePath = "/home/federico/timbro.png";
//
//		PDDocument document = PDDocument.load(new File(pdfFilePath));
//		float scale = 0.5f;
//		PDImageXObject ximage = PDImageXObject.createFromByteArray(document,
//				Files.readAllBytes(Paths.get(signatureImagePath)), "logo.png");
//		float deltaX = ximage.getWidth() * scale;
//		float deltaY = ximage.getHeight() * scale;
//
//		PDPage page = (PDPage) document.getDocumentCatalog().getPages().get(0);
//
//		PDPageContentStream contentStream = new PDPageContentStream(document, page,
//				PDPageContentStream.AppendMode.APPEND, true);
//		contentStream.drawImage(ximage, page.getMediaBox().getUpperRightX() - deltaX,
//				page.getMediaBox().getUpperRightY() - deltaY, deltaX, deltaY);
//		contentStream.close();
//
//		document.save("/home/federico/pdf-test-output.pdf");
	}
}
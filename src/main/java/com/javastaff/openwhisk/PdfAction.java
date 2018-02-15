package com.javastaff.openwhisk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.google.gson.JsonObject;

/***
 * Action che prende in input un testo e crea il pdf risultante restituendolo in Base64
 */
public class PdfAction {
	public static JsonObject main(JsonObject args) throws IOException {
		String testo = args.getAsJsonPrimitive("testo").getAsString();

		// Inizializza PDF e caratteristiche collegate
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);
		PDFont pdfFont = PDType1Font.HELVETICA;
		float fontSize = 13;
		float leading = 1.5f * fontSize;
		PDRectangle mediabox = page.getMediaBox();
		float margin = 72;
		float width = mediabox.getWidth() - 2 * margin;
		float startX = mediabox.getLowerLeftX() + margin;
		float startY = mediabox.getUpperRightY() - margin;

		// Splitta il testo in diverse linee
		List<String> lines = splitLines(testo, fontSize, pdfFont, width);

		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		contentStream.beginText();
		contentStream.setFont(pdfFont, fontSize);
		contentStream.newLineAtOffset(startX, startY);
		// Aggiunge ogni linea al PDF
		for (String line : lines) {
			contentStream.showText(line);
			contentStream.newLineAtOffset(0, -leading);
		}
		contentStream.endText();
		contentStream.close();

		// Salva output
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		document.save(baos);
		document.close();
		JsonObject response = new JsonObject();
		response.addProperty("pdffile", Base64.getEncoder().encodeToString(baos.toByteArray()));
		return response;
	}

	/**
	 * Splitta il testo da inserire nel PDF in diverse linee
	 * 
	 * @param testo
	 * @param fontSize
	 * @param pdfFont
	 * @param width
	 * @return
	 * @throws IOException
	 */
	public static List<String> splitLines(String testo, float fontSize, PDFont pdfFont, float width)
			throws IOException {
		List<String> lines = new ArrayList<String>();
		int lastSpace = -1;
		while (testo.length() > 0) {
			int spaceIndex = testo.indexOf(' ', lastSpace + 1);
			if (spaceIndex < 0)
				spaceIndex = testo.length();
			String subString = testo.substring(0, spaceIndex);
			float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
			if (size > width) {
				if (lastSpace < 0)
					lastSpace = spaceIndex;
				subString = testo.substring(0, lastSpace);
				lines.add(subString);
				testo = testo.substring(lastSpace).trim();
				lastSpace = -1;
			} else if (spaceIndex == testo.length()) {
				lines.add(testo);
				testo = "";
			} else {
				lastSpace = spaceIndex;
			}
		}
		return lines;
	}

}

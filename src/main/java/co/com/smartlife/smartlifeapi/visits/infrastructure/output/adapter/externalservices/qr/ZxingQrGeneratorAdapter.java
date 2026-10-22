package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.qr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import co.com.smartlife.smartlifeapi.visits.service.output.port.QrGeneratorOutputPort;

@Component
public class ZxingQrGeneratorAdapter implements QrGeneratorOutputPort {

	private static final int SIZE = 300;

	@Override
	public String generateBase64(final String content) {
		try {
			BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(matrix, "PNG", output);
			return Base64.getEncoder().encodeToString(output.toByteArray());
		} catch (WriterException | IOException exception) {
			throw new IllegalStateException("No fue posible generar el código QR", exception);
		}
	}
}
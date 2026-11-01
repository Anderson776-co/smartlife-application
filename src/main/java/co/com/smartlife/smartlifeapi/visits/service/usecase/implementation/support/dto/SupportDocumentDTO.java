package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.dto;

import java.util.Arrays;
import java.util.Objects;

public record SupportDocumentDTO(String fileName, String contentType, byte[] content) {
	public boolean hasContent() {
		return content.length > 0;
	}

	/** Un record con un arreglo debe comparar su contenido y no su referencia. */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SupportDocumentDTO that)) {
			return false;
		}
		return Objects.equals(fileName, that.fileName) && Objects.equals(contentType, that.contentType)
				&& Arrays.equals(content, that.content);
	}

	@Override
	public int hashCode() {
		return 31 * Objects.hash(fileName, contentType) + Arrays.hashCode(content);
	}

	/** No se imprime el contenido del archivo, solo su tamano. */
	@Override
	public String toString() {
		return "SupportDocumentDTO[fileName=" + fileName + ", contentType=" + contentType + ", contentLength="
				+ (content == null ? 0 : content.length) + "]";
	}
}
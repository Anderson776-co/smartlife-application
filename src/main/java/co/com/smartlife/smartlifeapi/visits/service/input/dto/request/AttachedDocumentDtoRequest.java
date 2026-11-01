package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

public record AttachedDocumentDtoRequest(String fileName, String contentType, byte[] content) {
	
		private static final byte[] EMPTY_CONTENT = new byte[0];
		
		public AttachedDocumentDtoRequest {
			fileName = TextHelper.getDefaultWithTrim(fileName);
			contentType = TextHelper.getDefaultWithTrim(contentType).toLowerCase(Locale.ROOT);
			content = ObjectHelper.getDefault(content, EMPTY_CONTENT);
		}
		
		public static AttachedDocumentDtoRequest empty() {
			return new AttachedDocumentDtoRequest(TextHelper.getDefault(), TextHelper.getDefault(), EMPTY_CONTENT);
		}
		
		public boolean hasContent() {
			return content.length > 0;
		}

		/** Un record con un arreglo debe comparar su contenido y no su referencia. */
		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof AttachedDocumentDtoRequest that)) {
				return false;
			}
			return Objects.equals(fileName, that.fileName) && Objects.equals(contentType, that.contentType)
					&& Arrays.equals(content, that.content);
		}

		@Override
		public int hashCode() {
			return 31 * Objects.hash(fileName, contentType) + Arrays.hashCode(content);
		}

		/** No se imprime el contenido del archivo, solo su tamaño. */
		@Override
		public String toString() {
			return "AttachedDocumentDtoRequest[fileName=" + fileName + ", contentType=" + contentType
					+ ", contentLength=" + content.length + "]";
		}
}
package co.com.smartlife.smartlifeapi.users.infrastructure.input.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.shared.service.input.port.MessageResolverInputPort;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Escribe las respuestas de seguridad que se arman antes de llegar a los controladores: 401 cuando no hay sesion y
 * 403 cuando no hay permiso. Los mensajes salen del catalogo (Strapi), no del codigo, y la respuesta va en UTF-8
 * para que las tildes no se pierdan.
 */
@Component
public class SecurityErrorResponseWriter {

	static final String SESSION_REQUIRED_CODE = "USER_MESSAGE_SESSION_REQUIRED";
	static final String ACCESS_DENIED_CODE = "USER_MESSAGE_ACCESS_DENIED";
	static final String GENERAL_ERROR_CODE = "USER_ERROR_TRYING_TO_MAKE_AN_OPERATION";

	private final MessageResolverInputPort messages;

	public SecurityErrorResponseWriter(final MessageResolverInputPort messages) {
		this.messages = messages;
	}

	/** 401: falta la sesion. */
	public void unauthorized(final HttpServletResponse response) throws IOException {
		write(response, HttpStatus.UNAUTHORIZED, SESSION_REQUIRED_CODE);
	}

	/** 403: hay sesion pero no permiso. */
	public void forbidden(final HttpServletResponse response) throws IOException {
		write(response, HttpStatus.FORBIDDEN, ACCESS_DENIED_CODE);
	}

	private void write(final HttpServletResponse response, final HttpStatus status, final String code)
			throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(body(message(code)));
	}

	/**
	 * El mensaje especifico del catalogo; si no esta, el mensaje general del propio catalogo; si tampoco, el texto
	 * vacio por defecto. Nunca es nulo.
	 */
	private String message(final String code) {
		final String specific = messages.resolve(code, TextHelper.getDefault());
		return TextHelper.isEmptyWithTrim(specific) ? messages.resolve(GENERAL_ERROR_CODE, TextHelper.getDefault())
				: specific;
	}

	static String body(final String message) {
		final String list = TextHelper.isEmptyWithTrim(message) ? "[]" : "[\"" + escape(message) + "\"]";
		return "{\"messages\":" + list + ",\"data\":[],\"responseSucceded\":false}";
	}

	/** Escapa lo que JSON no admite dentro de un texto, para que la respuesta sea siempre JSON valido. */
	static String escape(final String text) {
		final StringBuilder out = new StringBuilder(text.length() + 8);
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			switch (c) {
				case '"' -> out.append("\\\"");
				case '\\' -> out.append("\\\\");
				case '\n' -> out.append("\\n");
				case '\r' -> out.append("\\r");
				case '\t' -> out.append("\\t");
				default -> {
					if (c < 0x20) {
						out.append(String.format("\\u%04x", (int) c));
					} else {
						out.append(c);
					}
				}
			}
		}
		return out.toString();
	}
}

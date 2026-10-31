package co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto;

import java.util.ArrayList;
import java.util.List;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import io.swagger.v3.oas.annotations.media.Schema;

public class Response<T> {
	
	@Schema(
	        description = "Lista de mensajes asociados a la respuesta",
	        example = "[\"Operación realizada correctamente\"]"
	    )
	private List<String> messages;
	 @Schema(
		     description = "Información retornada por la operación"
		    )
	private List<T> data;
	 @Schema(
		     description = "Indica si la operación fue exitosa",
		     example = "true"
		    )
	private boolean responseSucceded;
	
	public Response(final boolean responseSucceded) {
		setResponseSucceded(responseSucceded);
		setMessages(new ArrayList<String>());
		setData(new ArrayList<T>());
	}
	
	public Response(final List<String> messages, final List<T> data, final boolean responseSucceded) {
		setResponseSucceded(responseSucceded);
		setMessages(messages);
		setData(data);
	}
	
	public static <T> Response <T> createSuccededResponse(){
		return new Response<>(new ArrayList<String>(), new ArrayList<>(), true);
	}
	
	public static <T> Response <T> createFailedResponse() {
		return new Response<>(new ArrayList<String>(), new ArrayList<>(), false);
	}
	
	public static <T> Response <T> createSuccededResponse(final List<T> data){
		return new Response<>(new ArrayList<String>(), data, true);
	}
	
	public static <T> Response <T> createFailedResponse(final List<T> data) {
		return new Response<>(new ArrayList<String>(), data, false);
	}
	
	public void addMessage(final String message) {
		if(!TextHelper.isEmptyWithTrim(message)) {
			getMessages().add(message);
		}
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(final List<String> messages) {
		this.messages = ObjectHelper.getDefault(messages, new ArrayList<String>());
	}

	public List<T> getData() {
		return data;
	}

	public void setData(final List<T> data) {
		this.data = ObjectHelper.getDefault(data, new ArrayList<T>());
	}

	public boolean isResponseSucceded() {
		return responseSucceded;
	}

	public void setResponseSucceded(final boolean responseSucceded) {
		this.responseSucceded = responseSucceded;
	}


}

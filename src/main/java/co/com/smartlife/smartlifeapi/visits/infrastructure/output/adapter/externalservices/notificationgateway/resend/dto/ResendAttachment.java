package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResendAttachment(@JsonProperty("filename") String fileName,
		@JsonProperty("content") String content,
		@JsonProperty("content_type") String contentType,
		@JsonProperty("content_id") String contentId){

}

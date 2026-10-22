package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.filestorage.r2.adapter;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.FileStorageOutputPort;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class R2FileStorageAdapter implements FileStorageOutputPort {

	private final String endpoint;
	private final String accessKey;
	private final String secretKey;
	private final String bucket;
	private S3Client s3Client;

	@Autowired
	public R2FileStorageAdapter(@Value("${r2.endpoint:}") final String endpoint,
			@Value("${r2.access-key:}") final String accessKey,
			@Value("${r2.secret-key:}") final String secretKey,
			@Value("${r2.bucket:}") final String bucket) {
		this.endpoint = endpoint;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.bucket = bucket;
	}

	public R2FileStorageAdapter(final S3Client s3Client, final String bucket) {
		this.endpoint = "";
		this.accessKey = "";
		this.secretKey = "";
		this.bucket = bucket;
		this.s3Client = s3Client;
	}

	@Override
	public String upload(final String key, final byte[] content, final String contentType) {
		requireBucket();
		client().putObject(
				PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),
				RequestBody.fromBytes(content));
		return key;
	}

	@Override
	public byte[] download(final String key) {
		requireBucket();
		return client().getObjectAsBytes(GetObjectRequest.builder().bucket(bucket).key(key).build())
				.asByteArray();
	}

	private void requireBucket() {
		if (bucket.isBlank()) {
			throw new IllegalStateException("El bucket de R2 no está configurado (r2.bucket)");
		}
	}

	private synchronized S3Client client() {
		if (s3Client == null) {
			if (endpoint.isBlank() || accessKey.isBlank() || secretKey.isBlank()) {
				throw new IllegalStateException(
						"R2 no está configurado (r2.endpoint, r2.access-key y r2.secret-key)");
			}
			s3Client = S3Client.builder()
					.endpointOverride(URI.create(endpoint))
					.region(Region.of("auto"))
					.credentialsProvider(StaticCredentialsProvider
							.create(AwsBasicCredentials.create(accessKey, secretKey)))
					.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
					.requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
					.responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
					.build();
		}
		return s3Client;
	}
}
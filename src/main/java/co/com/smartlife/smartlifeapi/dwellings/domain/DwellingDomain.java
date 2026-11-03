package co.com.smartlife.smartlifeapi.dwellings.domain;

import java.util.UUID;

public record DwellingDomain(UUID id, String dwellingNumber, String block, UserOwnerDomain owner, UrbanizationDwellingDomain urbanization) {

	public DwellingDomain withOwner(final UserOwnerDomain newOwner) {
		return new DwellingDomain(id, dwellingNumber, block, newOwner, urbanization);
	}
}

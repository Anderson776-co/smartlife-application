package co.com.smartlife.smartlifeapi.users.service.usecase.contract;

import co.com.smartlife.smartlifeapi.shared.service.usecase.UseCaseWithReturn;
import co.com.smartlife.smartlifeapi.users.service.input.dto.response.SessionDto;

public interface LoginUseCase extends UseCaseWithReturn<String,SessionDto>{
}

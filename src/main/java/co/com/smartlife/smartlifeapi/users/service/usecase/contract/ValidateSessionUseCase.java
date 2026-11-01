package co.com.smartlife.smartlifeapi.users.service.usecase.contract;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.shared.service.usecase.UseCaseWithReturn;

public interface ValidateSessionUseCase extends UseCaseWithReturn<String, CurrentUser>{

}

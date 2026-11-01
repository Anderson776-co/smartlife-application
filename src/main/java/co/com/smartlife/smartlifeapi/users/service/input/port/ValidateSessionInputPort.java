package co.com.smartlife.smartlifeapi.users.service.input.port;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.shared.service.input.port.InputPortWithReturn;

public interface ValidateSessionInputPort extends InputPortWithReturn<String, CurrentUser>{

}

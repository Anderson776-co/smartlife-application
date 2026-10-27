package co.com.smartlife.smartlifeapi.crosscutting.session;

import java.util.Optional;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;

public final class CurrentUserHolder {
	private static final String KEY = CurrentUserHolder.class.getName();
	
	private CurrentUserHolder() { throw new IllegalStateException("Utility class"); }

    public static void set(final CurrentUser user) {
        RequestContextHolder.currentRequestAttributes()
            .setAttribute(KEY, user, RequestAttributes.SCOPE_REQUEST);
    }

    public static Optional<CurrentUser> get() {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return ObjectHelper.isNull(attributes) ? Optional.empty()
            : Optional.ofNullable((CurrentUser) attributes.getAttribute(KEY, RequestAttributes.SCOPE_REQUEST));
    }

}

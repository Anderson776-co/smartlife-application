package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.config;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocaleConfig {
	
	@Bean
	public LocaleResolver localeResolver(
			@Value("${smartlife.i18n.default-locale}") final String defaultLocale,
			@Value("${smartlife.i18n.supported-locales}") final List<String> supportedLocales) {
		AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
		resolver.setDefaultLocale(Locale.forLanguageTag(defaultLocale));
		resolver.setSupportedLocales(supportedLocales.stream().map(Locale::forLanguageTag).toList());
		return resolver;
	}

}

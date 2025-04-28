package hello.itemservice.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageConfig {

//     스프링 빈 등록 -> MessageSource Bean
	@Bean
	public MessageSource messageSource() {

		ResourceBundleMessageSource messageSource = new
				ResourceBundleMessageSource();
		messageSource.setBasenames("messages","error");
		messageSource.setDefaultEncoding("utf-8");
		return messageSource;
	}
}

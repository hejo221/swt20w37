package wineshop;
import org.salespointframework.EnableSalespoint;
import org.salespointframework.SalespointSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@EnableSalespoint
public class WineShop {

	public static void main(String[] args) {
		SpringApplication.run(WineShop.class, args);
	}

	@Configuration
	static class WebSecurityConfiguration extends SalespointSecurityConfiguration {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();  // for lab purposes, that's ok!
			http
					.authorizeRequests()
						.antMatchers("/", "/user/login", "/catalog", "/resource/css/**").permitAll()
						.anyRequest().authenticated()
						.and()
					.formLogin()
						.loginPage("/user/login")
						.loginProcessingUrl("/login")
						.and()
					.logout()
						.logoutUrl("/logout")
						.logoutSuccessUrl("/");
		}
	}
}

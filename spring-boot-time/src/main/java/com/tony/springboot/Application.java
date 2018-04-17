package com.tony.springboot;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CompositeFilter;

import com.tony.domain.TimeVo;
import com.tony.springboot.service.ServerTimeService;
import com.tony.springboot.service.UnixTimeService;

@SpringBootApplication

@EnableOAuth2Sso


@RestController
public class Application extends WebSecurityConfigurerAdapter {
	@Autowired
	private UnixTimeService unixTimeService;

	@Autowired
	private ServerTimeService serverTimeService;
	
	//搭配@EnableOAuth2Client,we can inject an OAuth2ClientContext and use it to build an authentication filter that we add to our security configuration:
    @Autowired
    OAuth2ClientContext oauth2ClientContext;
    
    //若是沒有method使用RequestMapping(value="/"),則Spring boot預設forward到/index.html

	@RequestMapping(value = "/time", method = RequestMethod.GET)
	public TimeVo getTime() {
		TimeVo timeVo = new TimeVo();
		unixTimeService.populateTime(timeVo);
		serverTimeService.populateTime(timeVo);
		return timeVo;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**","/webjars/**").permitAll().anyRequest().authenticated()
				.and().logout().logoutSuccessUrl("/").permitAll().and().csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
				//Spring預設/logout且只能POST, CSRF DOCUMENT有說明(Adding CSRF will update the LogoutFilter to only use HTTP POST)
				//csrf()-Adds CSRF support
				//The default is that accessing the URL "/logout" will log the user out by invalidating the HTTP Session, cleaning up any rememberMe() authentication that was configured, clearing the SecurityContextHolder, and then redirect to "/login?success". 
				//logoutUrl(String logoutUrl),logoutSuccessUrl(String redirectUrl)可客製化要登出的url和登出成功後的redirect url


		// spring-security裡有一隻Filter-DefaultLoginPageGeneratingFilter會自動mapping
		// /login產生預設的登錄頁面
		/**
		 * For internal use with namespace configuration in the case where a
		 * user doesn't configure a login page. The configuration code will
		 * insert this filter in the chain instead.
		 *
		 * Will only work if a redirect is used to the login page.
		 *
		 * @author Luke Taylor
		 * @since 2.0
		 */

	}

	// 整個驗證流程請參考RFC SPEC
	// https://tools.ietf.org/html/rfc6749#section-4
	//對照SPEC的腳色定義
	//Authorization Server是FB
	//User-Agent是我的瀏覽器,Client是指我的這支Application(Client對Authorization Server的請求(client requests an access token)都是在後端(server side)的Spring Security在做(所以F12的Network觀察不到要求access token)
	
	@RequestMapping("/user")
	public Principal user(Principal principal) {
		 if (principal != null) {
		        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
		        Authentication authentication = oAuth2Authentication.getUserAuthentication();
		        Map<String, String> details = new LinkedHashMap<>();
		        details = (Map<String, String>) authentication.getDetails();
		        System.out.println("details = " + details);  
		        
		        //相當於SPEC的4.1.  Authorization Code Grant
		        //(E)  The authorization server authenticates the client, validates the
//		        authorization code, and ensures that the redirection URI
//		        received matches the URI used to redirect the client in
//		        step (C).  If valid, the authorization server responds back with
//		        an access token and, optionally, a refresh token.
		        //也可參考https://www.oauth.com/oauth2-servers/access-tokens/access-token-response/
		        return principal;
		    }

		return principal;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}


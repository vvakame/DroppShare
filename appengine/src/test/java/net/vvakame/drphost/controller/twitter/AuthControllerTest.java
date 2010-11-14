package net.vvakame.drphost.controller.twitter;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.slim3.tester.ControllerTestCase;

import twitter4j.Twitter;
import twitter4j.http.RequestToken;

/**
 * {@link AuthController} のテスト.
 * 
 * @author vvakame
 */
public class AuthControllerTest extends ControllerTestCase {

	@Test
	public void run() throws NullPointerException, IllegalArgumentException,
			IOException, ServletException {
		tester.start("/twitter/auth");
		assertThat(tester.getController(), instanceOf(AuthController.class));
		assertThat(tester.getDestinationPath().contains("twitter.com"),
				is(true));

		Object twitter = tester.request.getSession().getAttribute("twitter");
		assertThat(twitter, is(instanceOf(Twitter.class)));
		Object requestToken = tester.request.getSession().getAttribute(
				"requestToken");
		assertThat(requestToken, is(instanceOf(RequestToken.class)));
	}
}

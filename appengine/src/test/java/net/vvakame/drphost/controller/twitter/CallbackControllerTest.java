package net.vvakame.drphost.controller.twitter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.slim3.tester.ControllerTestCase;

/**
 * {@link AuthController} のテスト.
 * 
 * @author vvakame
 */
public class CallbackControllerTest extends ControllerTestCase {

	@Test
	public void run() throws NullPointerException, IllegalArgumentException,
			IOException, ServletException {
		tester.start("/twitter/callback");
		assertThat(tester.getController(), instanceOf(CallbackController.class));
		assertThat(tester.isRedirect(), is(true));
		assertThat(tester.getDestinationPath(), is("/twitter/auth"));
	}
}

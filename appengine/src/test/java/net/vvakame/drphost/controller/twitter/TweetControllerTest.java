package net.vvakame.drphost.controller.twitter;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.slim3.tester.ControllerTestCase;

/**
 * {@link AuthController} のテスト.
 * 
 * @author vvakame
 */
public class TweetControllerTest extends ControllerTestCase {

	@Test(expected = IllegalArgumentException.class)
	public void run() throws NullPointerException, IllegalArgumentException,
			IOException, ServletException {
		tester.start("/twitter/tweet");
	}
}

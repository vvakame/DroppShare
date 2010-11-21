package net.vvakame.drphost.controller;

import java.util.logging.Logger;

import javax.servlet.ServletInputStream;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class DrodataController extends Controller {

	static final Logger logger = Logger.getLogger(DrodataController.class
			.getName());

	@Override
	public Navigation run() throws Exception {

		if (isPost()) {
			@SuppressWarnings("unused")
			ServletInputStream inputStream = request.getInputStream();
		}

		return null;
	}
}

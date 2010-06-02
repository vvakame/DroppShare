package net.vvakame.dropphosting.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DroppShareFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		try {
			chain.doFilter(req, res);
		} catch (IOException e) {
			processOnlyDroppShare(req, res, e);
			throw e;
		} catch (ServletException e) {
			processOnlyDroppShare(req, res, e);
			throw e;
		} catch (RuntimeException e) {
			processOnlyDroppShare(req, res, e);
			throw e;
		}
	}

	private void processOnlyDroppShare(ServletRequest req, ServletResponse res,
			Exception e) throws IOException {
		HttpServletRequest httpReq = (HttpServletRequest) req;
		HttpServletResponse httpRes = (HttpServletResponse) res;
		if (httpReq.getHeader("User-Agent").indexOf("DroppShare") != -1) {
			httpRes.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			OutputStream out = httpRes.getOutputStream();
			out.write(e.getMessage().getBytes());
			out.flush();
			out.close();
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}

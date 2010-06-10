package net.vvakame.dropphosting.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import net.vvakame.dropphosting.meta.AppDataSrvMeta;
import net.vvakame.dropphosting.meta.IconDataMeta;
import net.vvakame.dropphosting.meta.OAuthDataMeta;
import net.vvakame.dropphosting.meta.VariantDataMeta;

public class AdminConsoleServlet extends HttpServlet {
	private static final long serialVersionUID = -7504558110741757404L;

	private static final Logger log = Logger
			.getLogger(DataDownloadServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		log.info(req.getParameter("action"));

		if ("delete_icon".equals(req.getParameter("action"))) {
			String fileName = req.getParameter("file_name");

			MemcacheService memcache = MemcacheServiceFactory
					.getMemcacheService();
			memcache.delete(fileName);

			IconDataMeta iMeta = IconDataMeta.get();
			List<Key> keys = Datastore.query(iMeta).filter(
					iMeta.fileName.equal(fileName)).asKeyList();
			Datastore.delete(keys);
		} else if ("delete_variant".equals(req.getParameter("action"))) {
			String screenName = req.getParameter("screen_name");
			String variant = req.getParameter("variant");

			VariantDataMeta vMeta = VariantDataMeta.get();
			List<Key> keys = Datastore.query(vMeta).filter(
					vMeta.screenName.equal(screenName),
					vMeta.variant.equal(variant)).asKeyList();

			AppDataSrvMeta aMeta = AppDataSrvMeta.get();
			keys.addAll(Datastore.query(aMeta).filter(
					aMeta.screenName.equal(screenName),
					aMeta.variant.equal(variant)).asKeyList());

			Datastore.delete(keys);
		} else if ("delete_oauth".equals(req.getParameter("action"))) {
			String screenName = req.getParameter("screen_name");

			OAuthDataMeta oMeta = OAuthDataMeta.get();
			List<Key> keys = Datastore.query(oMeta).filter(
					oMeta.screenName.equal(screenName)).asKeyList();

			Datastore.delete(keys);
		} else if ("delete_session".equals(req.getParameter("action"))) {
			List<Key> keys = Datastore.query("_ah_SESSION").asKeyList();
			Datastore.delete(keys);
		}

		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher("/WEB-INF/admin.jsp");
		rd.forward(req, res);
	}
}

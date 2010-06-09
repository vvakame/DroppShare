package net.vvakame.dropphosting.server;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import net.vvakame.dropphosting.meta.IconDataMeta;
import net.vvakame.dropphosting.model.IconData;

public class IconDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = -7504558110741757404L;

	private static final String PROP_DROP = "dropp";
	private static boolean DEBUG = false;

	private static final Logger log = Logger
			.getLogger(IconDownloadServlet.class.getName());

	public void init() throws ServletException {
		ResourceBundle rb = ResourceBundle.getBundle(PROP_DROP, Locale
				.getDefault());
		DEBUG = Boolean.parseBoolean(rb.getString("debug_mode"));
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String path = req.getPathInfo();
		if (path == null) {
			throw new IllegalArgumentException("file name is not included!");
		}
		String fileName = path.substring(path.lastIndexOf("/") + 1);

		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		IconData iconData = (IconData) memcache.get(fileName);

		if (DEBUG) {
			if (iconData != null) {
				log.info("Hit memcache! icon=" + fileName);
			}
		}

		if (iconData == null) {
			IconDataMeta iMeta = IconDataMeta.get();
			iconData = Datastore.query(iMeta).filter(
					iMeta.fileName.equal(fileName)).asSingle();
			memcache.put(fileName, iconData);
		}

		if (iconData == null) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		res.setContentType("image/png");
		res.setHeader("Cache-Control", "max-age=" + 60 * 60 * 24 * 30); // 最高30日キャッシュ
		res.setDateHeader("Last-Modified", iconData.getCreateAt().getTime());
		res.getOutputStream().write(iconData.getIcon().getImageData());
	}
}

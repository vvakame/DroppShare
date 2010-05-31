package net.vvakame.dropphosting.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vvakame.dropphosting.model.IconData;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class IconDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = -7504558110741757404L;

	private static final Logger log = Logger
			.getLogger(IconDownloadServlet.class.getName());

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
		fileName.toString();

		DatastoreService datastoreService = DatastoreServiceFactory
				.getDatastoreService();

		IconData iconData;
		try {
			iconData = getIconImage(datastoreService, fileName);
		} catch (EntityNotFoundException e) {
			throw new ServletException(e);
		}

		res.setContentType("image/png");
		res.setHeader("Cache-Control", "max-age=" + 60 * 60 * 24 * 30); // 最高30日キャッシュ
		res.setDateHeader("Last-Modified", iconData.getCreateAt().getTime());
		res.getOutputStream().write(iconData.getIcon().getImageData());
	}

	private IconData getIconImage(DatastoreService datastoreService,
			String iconName) throws EntityNotFoundException {
		Query q = new Query("icon");
		q.addFilter("iconNameWithHint", Query.FilterOperator.GREATER_THAN,
				iconName);
		FetchOptions fopt = FetchOptions.Builder.withOffset(0);
		PreparedQuery pq = datastoreService.prepare(q);

		Entity iconEntity = null;
		try {
			iconEntity = pq.asList(fopt).get(0);
		} catch (IndexOutOfBoundsException e) {
			log.info("can't find icon. Query, iconName=" + iconName);
			throw new IllegalStateException("can't find icon. Query, iconName="
					+ iconName);
		}
		String iconNameWithHint = (String) iconEntity
				.getProperty("iconNameWithHint");
		if (!iconNameWithHint.startsWith(iconName)) {
			log.info("can't find icon. Query, iconName=" + iconName);
			throw new IllegalStateException("can't find icon. Query, iconName="
					+ iconName);
		}
		IconData iconData = IconData.getIconData(datastoreService, iconEntity);

		return iconData;
	}
}

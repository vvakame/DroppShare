package net.vvakame.dropphosting.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slim3.datastore.Datastore;

import net.vvakame.dropphosting.meta.IconDataMeta;
import net.vvakame.dropphosting.model.IconData;

public class IconDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = -7504558110741757404L;

	@SuppressWarnings("unused")
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

		IconDataMeta iMeta = IconDataMeta.get();
		IconData iconData = Datastore.query(iMeta).filter(
				iMeta.fileName.equal(fileName)).asSingle();

		// TODO 取れないときの処理

		res.setContentType("image/png");
		res.setHeader("Cache-Control", "max-age=" + 60 * 60 * 24 * 30); // 最高30日キャッシュ
		res.setDateHeader("Last-Modified", iconData.getCreateAt().getTime());
		res.getOutputStream().write(iconData.getIcon().getImageData());
	}
}

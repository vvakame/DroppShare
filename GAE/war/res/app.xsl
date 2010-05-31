<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:output method="html" encoding="UTF-8" indent="yes" />

	<xsl:template match="/">
		<xsl:variable name="author">
			<xsl:value-of select="DroppShare/author" />
		</xsl:variable>
		<xsl:variable name="twitterUri">
			<xsl:value-of select="concat('http://twitter.com/', $author)" />
		</xsl:variable>

		<html>
			<head>
				<link rel="stylesheet" type="text/css" href="res/app.css" />
				<title>
					<xsl:value-of select="$author" />'s application list.
				</title>
			</head>
			<body>
				<div id="header">
					<a>
						<xsl:attribute name="href"><xsl:value-of
							select="$twitterUri" /></xsl:attribute>
						<xsl:value-of select="$author" />
					</a>'s application list.
				</div>

				<xsl:apply-templates />

				<div id="footer">
					Powered by DroppShare
					<a href="market://details?id=net.vvakame.droppshare">[Market]</a>
					<a href="http://github.com/vvakame/DroppShare">[github]</a>
					<br />
					Author vvakame
					<a href="http://twitter.com/vvakame">[Twitter]</a>
					<a href="http://d.hatena.ne.jp/vvakame/">[blog]</a>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="DroppShare">
		<div id="main">
			<xsl:for-each select="AppData">
				<div class="app">
					<xsl:apply-templates select="." />
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="AppData">
		<xsl:variable name="icon">
			<xsl:variable name="pname">
				<xsl:value-of select="packageName" />
			</xsl:variable>
			<xsl:variable name="vcode">
				<xsl:value-of select="versionCode" />
			</xsl:variable>
			<xsl:value-of select="concat('./icon/', $pname, '_v', $vcode , '.png')" />
		</xsl:variable>
		<xsl:variable name="marketUri">
			<xsl:variable name="pname">
				<xsl:value-of select="packageName" />
			</xsl:variable>
			<xsl:value-of select="concat('market://details?id=', $pname)" />
		</xsl:variable>
		<xsl:variable name="cyrketUri">
			<xsl:variable name="pname">
				<xsl:value-of select="packageName" />
			</xsl:variable>
			<xsl:value-of select="concat('http://www.cyrket.com/p/android/', $pname)" />
		</xsl:variable>
		<xsl:variable name="appBrainUri">
			<xsl:variable name="pname">
				<xsl:value-of select="packageName" />
			</xsl:variable>
			<xsl:value-of select="concat('http://www.appbrain.com/app/', $pname)" />
		</xsl:variable>
		<xsl:variable name="androiderUri">
			<xsl:variable name="pname">
				<xsl:value-of select="packageName" />
			</xsl:variable>
			<xsl:value-of select="concat('http://androider.jp/?s=', $pname)" />
		</xsl:variable>
		<xsl:variable name="googleUri">
			<xsl:variable name="appname">
				<xsl:value-of select="appName" />
			</xsl:variable>
			<xsl:value-of select="concat('http://www.google.co.jp/search?q=', $appname)" />
		</xsl:variable>

		<div class="icon">
			<img class="iconImage">
				<xsl:attribute name="src">
					<xsl:value-of select="$icon" />
				</xsl:attribute>
			</img>
		</div>

		<div class="appName">
			<xsl:value-of select="appName" />
		</div>

		<div class="versionName">
			Version
			<xsl:value-of select="versionName" />
		</div>

		<div class="link">
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="$marketUri" />
				</xsl:attribute>
				[Market]
			</a>
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="$cyrketUri" />
				</xsl:attribute>
				[Cyrket]
			</a>
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="$appBrainUri" />
				</xsl:attribute>
				[AppBrain]
			</a>
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="$androiderUri" />
				</xsl:attribute>
				[Androider]
			</a>
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="$googleUri" />
				</xsl:attribute>
				[Google]
			</a>
		</div>

		<xsl:if test="description != ''">
			<div class="description">
				<xsl:value-of select="description" />
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.i8c.be/sag/documentationgenerator/intermediate/v1">
	<xsl:output encoding="UTF-8" method="html"/>

	<!-- Change this value to increase or decrease the dept you want to print for document types -->
	<xsl:variable name="maxRecursionCounter" select="5"/>
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
				<style type="text/css">
					body{
					font-family: Arial;
					font-size: 1.3em;
					}

					/* Header title */
					#title{
					border-bottom: 5px solid #3289C5;
					}

					body header{
					text-align: right;
					}

					/* Folder */
					.folder{
					text-align: right;
					border-bottom: 3px solid black;
					margin-bottom: 2em;
					}

					.service + .folder{
					margin-top: 5em;
					}

					/* Package */
					.package{
					margin-top: 5em;
					}

					.package-name{
					margin: auto;
					text-align: center;
					}

					.package-footer {
					width: 100%;
					margin-top: 2em;
					border-top: 3px solid black;
					}

					.package-footer table{
					width: 100%;
					}

					.package-footer table tr td:nth-child(1){
					float: left;
					}

					.package-footer table tr td:nth-child(2){
					float: right;
					position: relative;
					}

					/* Service */
					.service{
					margin-top: 1em;
					border-top: 3px solid #3289C5;
					}

					.fields,
					.fields table,
					.fields .table-caption{
					width: 100%;
					}

					.fields .table-caption,
					.fields .table-caption > th{
					border-bottom: 2px solid #3289C5;
					text-align: left;
					}

					.table-header th{
					text-align: left;
					}

					.table-header th:nth-child(1){
					width: 10%;
					}

					.table-header th:nth-child(2){
					width: 90%;
					}

					.table-header th{
					//border-bottom: 1px solid #3289C5;
					font-size: 1.2em;
					font-weight: normal;
					text-decoration: underline;
					}

					.fields table tbody tr td{
						font-size:.9em;
					}

					.fields table tbody tr td:nth-child(1){
					font-style: italic;
					word-wrap:break-word;
					}

					.field-type{
					font-weight: bold;
					}

					.field-comment{
					font-family: "Times New Roman";
					}
				</style>
			</head>
			<body>
				<header>
					<div id="title">
						<img src="assets/logo-i8c.png" alt="Integr8 Consulting"/>
					</div>

					<h2>webMethods Integration Server<br/>i8c created Services Reference</h2>
					<div id="date"><xsl:value-of  select="format-date(current-date(),'[MNn] [Y0001]')"/></div>
				</header>
				<main>
					<xsl:apply-templates/>
				</main>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="tns:Package">
		<xsl:choose>
			<xsl:when test="..=/tns:Packages">
				<div class="package">
					<h2 class="package-name">Package: <xsl:value-of select="./@name"/></h2>
					<div class="version">Version <xsl:value-of select="./@version"/></div>
					<xsl:apply-templates/>
					<div class="package-footer">
						<table>
							<tr>
								<td>WM IS i8c created Services Reference Guide</td>
								<td>Version 1.0</td>
							</tr>
						</table>
					</div>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="tns:Folder">
		<xsl:if test="not(..=/tns:Folders)">
			<div class="folder"><xsl:value-of select="./@qualifiedName"/></div>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="tns:Service">
		<xsl:choose>
			<xsl:when test="..=/tns:Services">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<div class="service">
					<h3 class="service-name"><xsl:call-template name="PrintObjectName"/></h3>
					<xsl:apply-templates select="tns:Description"/>
					<xsl:apply-templates select="tns:Input"/>
					<xsl:apply-templates select="tns:Output"/>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="tns:Description">
		<!-- If this isn't empty, show the usage notes -->
		<xsl:if test=". !=''">
			<h4>Description</h4>
			<p><xsl:value-of select="."/></p>
		</xsl:if>
	</xsl:template>
	<xsl:template match="tns:DocumentType">
		<xsl:choose>
			<xsl:when test="..=/tns:DocumentTypes">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<div>
					<h3><xsl:call-template name="PrintObjectName"/></h3>
					<xsl:apply-templates select="tns:Description"/>
					<!-- TODO <xsl:apply-templates select="tns:UsageNotes"/> -->
					<div class="fields">
						<xsl:apply-templates select="./tns:Fields">
								<xsl:with-param name="title" select="'Document'"/>
						</xsl:apply-templates>
					</div>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="tns:Input">
		<div class="fields">
			<xsl:call-template name="PrintFields">
				<xsl:with-param name="title" select="'Input parameters'"/>
			</xsl:call-template>
		</div>
	</xsl:template>
	<xsl:template match="tns:Output">
		<div class="fields">
			<xsl:call-template name="PrintFields">
				<xsl:with-param name="title" select="'Output parameters'"/>
			</xsl:call-template>
		</div>
	</xsl:template>
	<xsl:template name="Field" match="tns:Field">
		<xsl:param name="recursionCounter" select="0"/>
		<tr>
			<td><xsl:value-of select="./@name"/></td>
			<td>
				<span class="field-type"><xsl:value-of select="./@type"/></span>
				<xsl:if test="./tns:Comment"><span class="field-comment"><xsl:value-of select="concat(' ', ./tns:Comment)"/></span></xsl:if>
			</td>
		</tr>
		<xsl:if test="./tns:Fields/*|./tns:Field">
			<tr>
				<td></td>
				<td>
				<xsl:choose>
					<xsl:when test="./tns:Field">
						<xsl:call-template name="PrintFields">
							<xsl:with-param name="recursionCounter" select="$recursionCounter"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="./tns:Fields/*">
						<xsl:apply-templates select="./tns:Fields">
							<xsl:with-param name="recursionCounter" select="$recursionCounter"/>
						</xsl:apply-templates>
					</xsl:when>
				</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PrintObjectName">
		<xsl:value-of select="concat(../../@qualifiedName,':&#x200B;',./@name)"/>
	</xsl:template>
	<xsl:template name="PrintFields" match="tns:Fields">
		<xsl:param name="title" select="''"/>
		<xsl:param name="recursionCounter" select="0"/>
		<xsl:if test="$recursionCounter &lt; $maxRecursionCounter">
			<table>
				<thead>
					<xsl:if test="$title">
						<tr class="table-caption">
							<th colspan="3"><xsl:value-of select="$title"/></th>
						</tr>
					</xsl:if>
					<tr class="table-header">
						<th>Name</th>
						<th>Type, Description</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="tns:Field">
						<xsl:with-param name="recursionCounter" select="$recursionCounter+1"/>
					</xsl:apply-templates>
				</tbody>
			</table>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
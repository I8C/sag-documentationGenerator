<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.i8c.be/sag/documentationgenerator/intermediate/v1">
	<xsl:output encoding="UTF-8" method="xml" indent="yes"/>
	<xsl:template match="/">
		<xsl:element name="Packages">
			<xsl:for-each select="tns:Packages/tns:Package">
				<xsl:element name="{./@name}">
					<xsl:for-each select="tns:Folders/tns:Folder">
						<xsl:element name="{./@qualifiedName}">
							<xsl:call-template name="Component">
								<xsl:with-param name="componentType" select="'DocumentTypes'"/>
								<xsl:with-param name="node" select="tns:DocumentTypes/tns:DocumentType"/>
							</xsl:call-template>
							<xsl:call-template name="Component">
								<xsl:with-param name="componentType" select="'Specifications'"/>
								<xsl:with-param name="node" select="tns:Specifications/tns:Specification"/>
							</xsl:call-template>
							<xsl:call-template name="Component">
								<xsl:with-param name="componentType" select="'Services'"/>
								<xsl:with-param name="node" select="tns:Services/tns:Service"/>
							</xsl:call-template>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
	<xsl:template name="Component">
		<xsl:param name="componentType"/>
		<xsl:param name="node"/>
		<xsl:if test="$node">
			<xsl:element name="{$componentType}">
				<xsl:for-each select="$node">
					<xsl:element name="{./@name}"/>
				</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>

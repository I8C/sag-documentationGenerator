<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.i8c.be/sag/documentationgenerator/intermediate/v1">
	<xsl:output encoding="UTF-8" method="text"/>
	<xsl:param name="newline" select="'&#10;'"/>
	<xsl:template match="/">
		<xsl:value-of select="concat('DOCUMENTATION', $newline, $newline)"/>
		<xsl:for-each select="tns:Packages/tns:Package">
			<xsl:apply-templates/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="tns:Package">
		<xsl:value-of select="concat('Package: ', ./@name, $newline)"/>
		<xsl:apply-templates/>
		<xsl:value-of select="$newline"/>
	</xsl:template>
	<xsl:template match="tns:Folder">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="tns:DocumentType|tns:Specification|tns:Service">
		<xsl:value-of select="concat('- ',../../@qualifiedName,':&#x200B;',./@name,$newline)"/>
		<xsl:apply-templates select="tns:Description"/>
		<xsl:call-template name="TestIO"/>
		<xsl:apply-templates select="tns:UsageNotes"/>
	</xsl:template>
	<xsl:template match="tns:Description">
		<!-- If this isn't empty, show the usage notes -->
		<xsl:if test=". !=''">
			<xsl:value-of select="concat('    Description',$newline,'    ->',.,$newline)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="tns:UsageNotes">
		<!-- If this isn't empty, show the usage notes -->
		<xsl:if test=". !=''">
			<xsl:value-of select="concat('    Usage Notes',$newline,'    ->',.,$newline)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template name="TestIO">
		<!-- If the Service has in OR output, show them with their input type and description -->
		<xsl:if test="./tns:Input/*|./tns:Output/*">
			<xsl:call-template name="PrintIO">
				<xsl:with-param name="IO">Input</xsl:with-param>
				<xsl:with-param name="node" select="tns:Input"/>
			</xsl:call-template>
			<xsl:call-template name="PrintIO">
				<xsl:with-param name="IO">Output</xsl:with-param>
				<xsl:with-param name="node" select="tns:Output"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>	<xsl:template name="PrintIO">
		<xsl:param name="IO"/>
		<xsl:param name="node"/>
		<xsl:value-of select="concat($IO,' Parameters',$newline)"/>
		<xsl:choose>
			<xsl:when test="$node/*">
				<!-- Show Output table if output is found -->
				<xsl:apply-templates select="$node"/>
			</xsl:when>
			<!-- If there is no input, display the result -->
			<xsl:otherwise>
				<xsl:value-of select="concat('No ',$IO,$newline)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>

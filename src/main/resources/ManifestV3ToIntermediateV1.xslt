<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~  Copyright (c) 2017, i8c N.V. (Integr8 Consulting; http://www.i8c.be)
  ~  All Rights Reserved.
  ~  
  ~  Licensed under the Apache License, Version 2.0 (the "License");
  ~  you may not use this file except in compliance with the License.
  ~  You may obtain a copy of the License at
  ~  
  ~  http://www.apache.org/licenses/LICENSE-2.0
  ~  
  ~  Unless required by applicable law or agreed to in writing, software
  ~  distributed under the License is distributed on an "AS IS" BASIS,
  ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~  See the License for the specific language governing permissions and
  ~  limitations under the License.
  -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.i8c.be/sag/documentationgenerator/intermediate/v1">
	<xsl:variable name="base-uri"><xsl:value-of select="base-uri(/)"/></xsl:variable>
	<xsl:output encoding="UTF-8" method="xml" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<xsl:element name="Package">
			<xsl:attribute name="name">
				<xsl:choose>
					<xsl:when test="Values/value[@name='name'] != ''">
						<xsl:value-of select="Values/value[@name='name']"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>PackageNotFound</xsl:text>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:attribute>
			<xsl:attribute name="version">
				<xsl:choose>
					<xsl:when test="Values/value[@name='version'] != ''">
						<xsl:value-of select="Values/value[@name='version']"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>PackageVersionNotFound</xsl:text>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:attribute>
			<xsl:if test="Values/record[@name='requires']/value">
				<xsl:element name="Dependencies">
					<xsl:for-each select="Values/record[@name='requires']/value">
						<xsl:element name="Package">
							<xsl:attribute name="name">
								<xsl:choose>
									<xsl:when test="@name != ''">
										<xsl:value-of select="@name"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>PackageNameNotFound</xsl:text>
									</xsl:otherwise>
								</xsl:choose>	
							</xsl:attribute>
							<xsl:attribute name="version">
								<xsl:choose>
									<xsl:when test=". != ''">
										<xsl:value-of select="."/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>PackageVersionNotFound</xsl:text>
									</xsl:otherwise>
								</xsl:choose>				
							</xsl:attribute>														
						</xsl:element>							
					</xsl:for-each>					
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>

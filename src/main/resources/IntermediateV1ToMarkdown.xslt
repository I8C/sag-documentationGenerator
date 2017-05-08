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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.i8c.be/sag/documentationgenerator/intermediate/v1">
    <xsl:output method="text" encoding="utf-8" />

    <!-- 
    new line character = &#10; 
    Markdown rendering a new line needs to have either 
        2 end of lines following each other for a new paragraph
        2 white spaces at the end and a new line for an actual new line
    -->

	<!-- Change this value to increase or decrease the dept you want to print for document types -->
	<xsl:variable name="maxRecursionCounter" select="5"/>
	<xsl:template match="/">
        <xsl:text>![Integr8 Consulting](assets/logo-i8c.png "Integr8 Consulting")&#10;</xsl:text>
        <xsl:text># webMethods Integration Server&#10;</xsl:text>
        <xsl:text>### i8c generated Services Reference&#10;</xsl:text>
        
        <xsl:value-of select="format-date(current-date(),'[MNn] [Y0001]')"/><xsl:text>&#10;&#10;</xsl:text>
        
        <xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="tns:Package">
		<xsl:choose>
			<xsl:when test="..=/tns:Packages">
				<div class="package">
                    <xsl:value-of select="concat('# Package: ', ./@name, '&#10;')"/>
                    <xsl:value-of select="concat('**Version ', ./@version, '**&#10;')"/>
					<xsl:apply-templates/>
                    <xsl:text>&#10;&#10;</xsl:text>
                    <xsl:text>WM IS i8c generated Services Reference Guide  &#10;</xsl:text>
                    <xsl:value-of select="concat('Version ', ./@version, '&#10;')"/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="tns:Folder">
		<xsl:if test="not(..=/tns:Folders)">
            <xsl:value-of select="concat('## ', ./@qualifiedName, '&#10;')"/>
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
					<xsl:text>### </xsl:text><xsl:call-template name="PrintObjectName"/><xsl:text>&#10;</xsl:text>
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
            <xsl:text>#### Description&#10;&#10;</xsl:text>
			<xsl:call-template name="fixNewLines">
				<xsl:with-param name="str" select="."/>
			</xsl:call-template>
			<xsl:text>&#10;&#10;</xsl:text>
		</xsl:if>
	</xsl:template>
	<xsl:template match="tns:DocumentType">
		<xsl:choose>
			<xsl:when test="..=/tns:DocumentTypes">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<div>
                    <xsl:text>### </xsl:text><xsl:call-template name="PrintObjectName"/><xsl:text>&#10;</xsl:text>
					<xsl:apply-templates select="tns:Description"/>
					<!-- TODO <xsl:apply-templates select="tns:UsageNotes"/> -->
					<xsl:apply-templates select="./tns:Fields">
							<xsl:with-param name="title" select="'Document'"/>
					</xsl:apply-templates>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="tns:Input">
        <xsl:call-template name="PrintFields">
            <xsl:with-param name="title" select="'Input parameters'"/>
        </xsl:call-template>
	</xsl:template>
	<xsl:template match="tns:Output">
        <xsl:call-template name="PrintFields">
            <xsl:with-param name="title" select="'Output parameters'"/>
        </xsl:call-template>
	</xsl:template>
	<xsl:template name="Field" match="tns:Field">
        <xsl:param name="recursionCounter" select="0"/>
        <xsl:for-each select="2 to $recursionCounter">
            <xsl:text>    </xsl:text>
        </xsl:for-each>
        
        <xsl:text>- </xsl:text>
        <xsl:value-of select="concat('_', ./@name, '_')"/>
        <xsl:if test="./@type">
            <xsl:value-of select="concat(', **', ./@type, '**')"/>
        </xsl:if>
		<xsl:choose>
            <xsl:when test="./tns:Comment">
                <xsl:value-of select="concat(', ', ./tns:Comment, '&#10;')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>&#10;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
		<xsl:if test="./tns:Fields/*|./tns:Field">
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
		</xsl:if>
	</xsl:template>

	<xsl:template name="PrintObjectName">
		<xsl:value-of select="concat(../../@qualifiedName,':&#x200B;',./@name)"/>
	</xsl:template>
	<xsl:template name="PrintFields" match="tns:Fields">
		<xsl:param name="title" select="''"/>
		<xsl:param name="recursionCounter" select="0"/>
		<xsl:if test="$recursionCounter &lt; $maxRecursionCounter">
            <xsl:if test="$title">
                <xsl:value-of select="concat('**', $title, '**  &#10;')"/>
                <xsl:text>Name, Type, Description&#10;&#10;</xsl:text>
            </xsl:if>
			<xsl:apply-templates select="tns:Field">
				<xsl:with-param name="recursionCounter" select="$recursionCounter+1"/>
			</xsl:apply-templates>
            <xsl:if test="$recursionCounter=0">
                <xsl:text>&#10;</xsl:text>
            </xsl:if>
		</xsl:if>
	</xsl:template>
	<xsl:template name="fixNewLines">
		<xsl:param name="newLineCharacter" select="'&#xa;'"/><!-- \n -->
		<xsl:param name="str"/>
		<xsl:value-of select="replace($str, $newLineCharacter, concat('  ', $newLineCharacter))"/>
	</xsl:template>
</xsl:stylesheet>

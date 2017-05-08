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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.i8c.be/sag/documentationgenerator/intermediate/v1" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output encoding="UTF-8" method="xml"/>

	<!-- Change this value to increase or decrease the dept you want to print for document types -->
	<xsl:variable name="maxRecursionCounter" select="4"/>
    <xsl:variable name="capitalLetters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
    <xsl:variable name="lowercaseLetters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
    
    <xsl:template match="/">
		<fo:root>
			<xsl:call-template name="PageLayout"/>
			<xsl:call-template name="Cover"/>
			<xsl:call-template name="Main"/>
		</fo:root>
	</xsl:template>
	<xsl:template name="PageLayout">
			<fo:layout-master-set>
				<!-- Cover page settings -->
		        <fo:simple-page-master master-name="Cover" page-height="29.7cm" page-width="21cm" font-family="Arial" margin-top="1.5cm" margin-bottom="0.5cm" margin-left="2cm" margin-right="2cm">
		            <fo:region-body region-name="xsl-region-body" margin-top="8.0cm" margin-bottom="1cm"/>
		            <fo:region-before region-name="xsl-region-before" extent="2.0cm"/>
					<fo:region-after region-name="xsl-region-after" extent="2.0cm"/>
		        </fo:simple-page-master>
		        <!-- Main Pages settings -->
				<fo:simple-page-master master-name="main" page-height="29.7cm" page-width="21cm" font-family="Arial" margin-top="1.5cm" margin-bottom="0.5cm" margin-left="2cm" margin-right="2cm">
					<fo:region-body region-name="xsl-region-body" margin-top="2.5cm" margin-bottom="2.5cm"/>
					<fo:region-before region-name="xsl-region-before" extent="3.5cm"/>
					<fo:region-after region-name="xsl-region-after" extent="2.0cm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
	</xsl:template>
	<xsl:template name="Cover">
		<!-- Cover creation -->
		<fo:page-sequence master-reference="Cover">
			<xsl:call-template name="CoverTopHeader"/>
			<xsl:call-template name="CoverBottomHeader"/>
			<xsl:call-template name="CoverBody"/>
		</fo:page-sequence> 
	</xsl:template>
	<xsl:template name="CoverTopHeader">
		<!-- Top header creation -->
		<fo:static-content flow-name="xsl-region-before">
			<fo:block text-align="right" space-after="-12pt">
				<fo:external-graphic src="file:./pdf-assets/logo-i8c.png" height="2.00cm"/>
			</fo:block>
			<fo:block
				border-before-color="#3289C5"
				border-before-style="solid"
				border-before-width="1pt"></fo:block>
		</fo:static-content>
	</xsl:template>
	<xsl:template name="CoverBottomHeader">
		<!-- Bottom header creation -->
		<fo:static-content flow-name="xsl-region-after">
			<fo:block border-before-color="#3289C5"
				border-before-style="solid"
				border-before-width="1pt">
			</fo:block>
		</fo:static-content>
	</xsl:template>
	<xsl:template name="CoverBody">
		<!-- Body creation -->
		<fo:flow flow-name="xsl-region-body" font-family="Arial" text-align="end">
			<fo:block font-size="20pt" font-family="Arial" font-weight="bold"> 
				webMethods Integration Server
			</fo:block>
			<fo:block font-size="20pt" font-family="Arial" font-weight="bold" space-after="4cm"> 
				i8c generated Services Reference
			</fo:block>
			<fo:block  font-size="12pt"
			text-align="end">
				<xsl:value-of  select="format-date(current-date(),'[MNn] [Y0001]')"/>
			</fo:block>
		</fo:flow>
	</xsl:template>
	<xsl:template name="Main">
		<!-- Main pages creation -->
		<fo:page-sequence master-reference="main">
			<xsl:call-template name="MainTopHeader"/>
			<xsl:call-template name="MainBottomHeader"/>
			<xsl:call-template name="MainBody"/>
		</fo:page-sequence>
	</xsl:template>
	<xsl:template name="MainTopHeader">
		<!-- Top header creation -->
		<fo:static-content flow-name="xsl-region-before">
			<fo:block text-align="end"
				border-after-color="black"
				border-after-style="solid"
				border-after-width="1pt">
				<fo:retrieve-marker retrieve-class-name="folder"/> Folder
			</fo:block>
		</fo:static-content>
	</xsl:template>
	<xsl:template name="MainBottomHeader">
		<!-- Bottom header creation -->
		<fo:static-content flow-name="xsl-region-after" font-size="10pt" font-family="Arial">
			<fo:block border-before-color="black"
					border-before-style="solid"
					border-before-width="1pt">
				<!-- Table for 3 column header -->
				<fo:table table-layout="fixed" width="100%" space-before="4pt">
					<fo:table-column column-width="45%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="45%"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block text-align="left">WM IS i8c generated Services Reference Guide</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right"><fo:page-number/></fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</fo:static-content>
	</xsl:template>
	<xsl:template name="MainBody">
		<!-- Body creation -->
		<fo:flow flow-name="xsl-region-body" font-family="Arial">
			<xsl:apply-templates/>
		</fo:flow>
	</xsl:template>
	<xsl:template name="PrintObjectName">
		<fo:block space-after="4pt" font-size="18" font-weight="bold"
			space-before="20pt"
			border-before-color="#3289C5"
			border-before-style="solid"
			border-before-width="1pt">
			<fo:marker marker-class-name="folder">
                <xsl:call-template name="breakApartLess">
                    <xsl:with-param name="strToBeBroken" select="../../@qualifiedName"/>
                </xsl:call-template>
            </fo:marker>

            <xsl:call-template name="breakApartLess">
                <xsl:with-param name="strToBeBroken" select="concat(../../@qualifiedName,':&#x200B;',./@name)"/>
            </xsl:call-template>
		</fo:block>
	</xsl:template>

	<xsl:template match="tns:Package">
		<xsl:if test="..=/tns:Packages">
			<!-- Stating Package name and version if parent nameSpace if packages -->
			<fo:block font-weight="bold" 
				space-before="10pt" space-after="10pt"
				font-size="18pt" page-break-before="always">
				<xsl:value-of select="concat('Package: ',./@name)"/>
			</fo:block>
			<fo:block >
				<xsl:value-of select="concat('Version: ',./@version)"/>
			</fo:block>
		</xsl:if>
			<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="tns:Folder">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="tns:DocumentType">
		<xsl:call-template name="PrintObjectName"/>
		<xsl:apply-templates select="tns:Description"/>
		<xsl:apply-templates select="tns:Fields">
			<xsl:with-param name="title">Document</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="tns:UsageNotes"/>
	</xsl:template>
	<xsl:template match="tns:Specification|tns:Service">
		<xsl:call-template name="PrintObjectName"/>
		<xsl:apply-templates select="tns:Description"/>
		<!-- If the Service has in OR output, show them with their input type and description -->
		<xsl:choose>
			<xsl:when test="./tns:Input/*|./tns:Output/*">
				<xsl:choose>
					<!-- If there is an input -->
					<xsl:when test="./tns:Input/*">
						<xsl:apply-templates select="./tns:Input"/>
					</xsl:when>
					<!-- If there is no input -->
					<xsl:when test="not(./tns:Input/*)">
						<xsl:call-template name="PrintTitle">
							<xsl:with-param name="title">Input Parameters</xsl:with-param>
						</xsl:call-template>
						<fo:block font-family="Times New Roman">No input</fo:block>
					</xsl:when>
					<!-- If there is an output -->
					<xsl:when test="./tns:Output/*">
						<xsl:apply-templates select="./tns:Output"/>
					</xsl:when>
					<!-- If there is no output -->
					<xsl:when test="not(./tns:Output/*)">
						<xsl:call-template name="PrintTitle">
							<xsl:with-param name="title">Output Parameters</xsl:with-param>
						</xsl:call-template>
						<fo:block font-family="Times New Roman">No Output</fo:block>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
		<xsl:apply-templates select="tns:UsageNotes"/>
	</xsl:template>
	<xsl:template match="tns:Description">
		<!-- If this isn't empty, show the usage notes -->
		<xsl:if test=". !=''">
			<fo:block font-weight="bold">Description</fo:block>
			<fo:block font-family="Times New Roman" linefeed-treatment="preserve">
				<xsl:value-of select="."/>
			</fo:block>
		</xsl:if>
	</xsl:template>
	<xsl:template match="tns:UsageNotes">
		<!-- If this isn't empty, show the usage notes -->
		<xsl:if test=". !=''">
			<fo:block font-weight="bold" space-before="10pt">Usage Notes</fo:block>
			<fo:block font-family="Times New Roman">
                <xsl:call-template name="breakApartLess">
                    <xsl:with-param name="strToBeBroken" select="."/>
                </xsl:call-template>
			</fo:block>
		</xsl:if>
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

		<!-- Display the field -->
		<fo:table-row>
			<fo:table-cell>
				<fo:block font-style="italic" font-size="80%">
					<!--
						To allow text wrapping we insert a Zero-Width Space
						between each character so the natural flow will make it wrap
					-->
					<xsl:call-template name="breakApart">
						<xsl:with-param name="strToBeBroken" select="./@name"/>
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block font-weight="bold">
                    <xsl:call-template name="breakApartLess">
                        <xsl:with-param name="strToBeBroken" select="./@type"/>
                    </xsl:call-template>
                </fo:block>
				<xsl:if test="./tns:Comment">
                    <fo:block font-family="Times New Roman">
                        <xsl:call-template name="breakApartLess">
                            <xsl:with-param name="strToBeBroken" select="concat(' ', ./tns:Comment)"/>
                        </xsl:call-template>
                    </fo:block>
                </xsl:if>
			</fo:table-cell>
		</fo:table-row>

		<!-- Display any sub fields for documents -->
		<xsl:if test="(./tns:Fields/*|./tns:Field) and $recursionCounter+1 &lt; $maxRecursionCounter">
			<fo:table-row>
				<fo:table-cell><fo:block> </fo:block></fo:table-cell>
				<fo:table-cell>
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
						<xsl:otherwise>
							<fo:block>Missing</fo:block>
						</xsl:otherwise>
					</xsl:choose>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PrintTitle">
		<xsl:param name="title"/>
		<fo:block font-size="10" font-weight="bold"
				  space-before="10pt" space-after="10pt"
				  border-after-color="#3289C5"
				  border-after-style="solid"
				  border-after-width="1pt">
            <xsl:call-template name="breakApartLess">
                <xsl:with-param name="strToBeBroken" select="$title"/>
            </xsl:call-template>
		</fo:block>
	</xsl:template>
	<xsl:template name="PrintFields" match="tns:Fields">
		<xsl:param name="title" select="''"/>
		<xsl:param name="recursionCounter" select="0"/>

		<xsl:if test="$recursionCounter &lt; $maxRecursionCounter and ./tns:Field">
			<fo:table table-layout="fixed" width="100%" border-collapse="separate" border-spacing="5pt 2pt">
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="79%"/>
				<fo:table-header>
					<xsl:if test="$title">
						<fo:table-row>
							<fo:table-cell number-columns-spanned="2">
								<fo:block
										border-after-color="#3289C5"
										border-after-style="solid"
										border-after-width="2pt">
									<xsl:value-of select="$title"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:if>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-decoration="underline">
								Name
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-decoration="underline">
								Type, Description
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<xsl:apply-templates select="tns:Field">
						<xsl:with-param name="recursionCounter" select="$recursionCounter+1"/>
					</xsl:apply-templates>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>

	<!-- From https://www.experts-exchange.com/questions/21941108/How-to-word-wrap-break-a-string-in-two-with-XSLT.html#a17242320 -->
    <!-- Place a zero length space between each character -->
	<xsl:template name="breakApart">
		<xsl:param name="breakLength" select="1"/>
		<xsl:param name="seperator" select="'&#8203;'"/><!-- Zero-Width Space -->
		<xsl:param name="strToBeBroken"/>
		<xsl:choose>
			<xsl:when test="string-length($strToBeBroken) &gt; $breakLength">
				<xsl:value-of select="substring($strToBeBroken, 1, $breakLength)"/>
				<xsl:value-of select="$seperator" disable-output-escaping="yes"/>
				<xsl:call-template name="breakApart">
					<xsl:with-param name="strToBeBroken" select="substring($strToBeBroken, $breakLength + 1)"/>
					<xsl:with-param name="breakLength" select="$breakLength"/>
					<xsl:with-param name="seperator" select="$seperator"/>
				</xsl:call-template>

			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$strToBeBroken"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
    
    <!-- Calls breakApartCharacters and breakApartCapitalization -->
    <xsl:template name="breakApartLess">
        <xsl:param name="seperator" select="'&#8203;'"/><!-- Zero-Width Space -->
        <xsl:param name="strToBeBroken"/>
        <xsl:call-template name="breakApartCapitalization">
            <xsl:with-param name="separator" select="$seperator"/>
            <xsl:with-param name="strToBeBroken">
                <xsl:call-template name="breakApartCharacters">
                    <xsl:with-param name="seperator" select="$seperator"/>
                    <xsl:with-param name="strToBeBroken" select="$strToBeBroken"/>
                </xsl:call-template>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- Place a zero length space between each character that matches $breakCharacters -->
    <xsl:template name="breakApartCharacters">
        <xsl:param name="breakCharacters" select="'.:;_-/|\'"/>
        <xsl:param name="seperator" select="'&#8203;'"/><!-- Zero-Width Space -->
        <xsl:param name="strToBeBroken"/>
        <xsl:if test="string-length($strToBeBroken) &gt; 0">
            <xsl:for-each select="0 to (string-length($strToBeBroken) - 1)">
               <xsl:choose> 
                   <xsl:when test="contains($breakCharacters, substring($strToBeBroken, . + 1, 1))">
                       <xsl:value-of select="concat(substring($strToBeBroken, . + 1, 1), $seperator)" />
                   </xsl:when>
                   <xsl:otherwise>
                       <xsl:value-of select="substring($strToBeBroken, . + 1, 1)" />
                   </xsl:otherwise>
               </xsl:choose>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
    
    <!-- Place a zero length space before a capital letter that has lowercase letter before and behind it -->
    <xsl:template name="breakApartCapitalization">
        <xsl:param name="separator" select="'&#8203;'"/><!-- Zero-Width Space -->
        <xsl:param name="strToBeBroken"/>
        <xsl:if test="string-length($strToBeBroken) &gt; 0">
            <xsl:for-each select="0 to (string-length($strToBeBroken) - 1)">
                <xsl:choose>
                    <xsl:when test="contains($lowercaseLetters, substring($strToBeBroken, . + 0, 1))
                        and contains($capitalLetters, substring($strToBeBroken, . + 1, 1))
                        and contains($lowercaseLetters, substring($strToBeBroken, . + 2, 1))">
                        <xsl:value-of select="concat($separator, substring($strToBeBroken, . + 1, 1))" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="substring($strToBeBroken, . + 1, 1)" />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>

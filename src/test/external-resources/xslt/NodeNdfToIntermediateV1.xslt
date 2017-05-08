<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.i8c.be/sag/documentationgenerator/intermediate/v1">
	<xsl:output encoding="UTF-8" method="xml" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<xsl:choose>
			<!-- The node.ndf file contains service information. -->
			<xsl:when test="Values/value[@name='svc_type']='flow' or Values/value[@name='svc_type']='java' or Values/value[@name='svc_type']='AdapterService'">
				<xsl:element name="Service">
					<xsl:attribute name="type">
						<xsl:value-of select="Values/value[@name='svc_type']"/>
					</xsl:attribute>
					<xsl:call-template name="ComponentWithInputOutput"/>			
				</xsl:element>
			</xsl:when>
			<!-- The node.ndf file contains specification information. -->
			<xsl:when test="Values/value[@name='svc_type']='spec'">
				<xsl:element name="Specification">
					<xsl:call-template name="ComponentWithInputOutput"/>	
				</xsl:element>
			</xsl:when>
			<!-- The node.ndf file contains document type information. -->
			<xsl:when test="Values/record[@name='record']">
				<xsl:element name="DocumentType">
					<!-- Get comment. -->
					<xsl:call-template name="DescriptionAndUsageNotes">
						<xsl:with-param name="comment" select="Values/record/value[@name='node_comment']"/>
					</xsl:call-template>
					<xsl:call-template name="Fields">
						<xsl:with-param name="record" select="Values/record"/>
						<xsl:with-param name="elementName" select="'Fields'"/>
					</xsl:call-template>			
				</xsl:element>
			</xsl:when>
			<!-- The node.ndf file has an unexpected structure, add element with name to indicate this. -->
			<xsl:otherwise>
				<xsl:element name="UnknownNodeNdfStructure"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="ComponentWithInputOutput">
		<!-- Get comment. -->
		<xsl:call-template name="DescriptionAndUsageNotes">
			<xsl:with-param name="comment" select="Values/value[@name='node_comment']"/>
		</xsl:call-template>				
		<!-- Get the input signature. -->
		<xsl:call-template name="Fields">
			<xsl:with-param name="record" select="Values/record[@name='svc_sig']/record[@name='sig_in']"/>
			<xsl:with-param name="elementName" select="'Input'"/>
		</xsl:call-template>		
		<!-- Get the output signature. -->		
		<xsl:call-template name="Fields">
			<xsl:with-param name="record" select="Values/record[@name='svc_sig']/record[@name='sig_out']"/>
			<xsl:with-param name="elementName" select="'Output'"/>
		</xsl:call-template>	
	</xsl:template>	
	<!-- When present and not empty, add comment as description and usage notes (if usage notes delimiter is found). -->
	<xsl:template name="DescriptionAndUsageNotes">
		<xsl:param name="comment"/>
		<xsl:if test="$comment != ''">
			<xsl:choose>
				<xsl:when test="contains($comment[0], '-- Usage Notes')">
					<xsl:element name="Description">
						<xsl:value-of select="substring-before($comment, '-- Usage Notes')"/>
					</xsl:element>
					<xsl:element name="UsageNotes">
						<xsl:value-of select="substring-after($comment, '-- Usage Notes')"/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="Description">
						<xsl:value-of select="$comment"/>
					</xsl:element>	
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>	
	</xsl:template>
	<xsl:template name="Fields">
		<xsl:param name="record"/>
		<xsl:param name="elementName"/>
		<xsl:if test="$record/value[@name='field_type']='record' or $record/value[@name='field_type']='recref'">
			<xsl:choose>
				<!-- If an array or list is present and it contains at least one record, process all its records recursively. -->
				<xsl:when test="$record/array[@name='rec_fields'] or $record/list[@name='rec_fields']">
					<xsl:variable name="nodeToUse" select="$record/array[@name='rec_fields'] | $record/list[@name='rec_fields']"/>
					<xsl:if test="$nodeToUse/record">
						<xsl:element name="{$elementName}">
							<xsl:for-each select="$nodeToUse/record">
								<xsl:call-template name="Field">
									<xsl:with-param name="record" select="."/>
								</xsl:call-template>		
							</xsl:for-each>				
						</xsl:element>			
					</xsl:if>							
				</xsl:when>
				<!-- Unexpected structure, add field with comment to indicate this. -->
				<xsl:otherwise>
					<xsl:element name="Fields">
						<xsl:element name="Field">
							<xsl:attribute name="name">
								<xsl:text>RecordContentError</xsl:text>
							</xsl:attribute>
							<xsl:element name="Comment">
								<xsl:text>This is a dummy field to indicate that the content of the record could not be retrieved </xsl:text>
								<xsl:choose>
									<xsl:when test="$record/value[@name='rec_ref'] | $record/value[@name='recref']">
										<xsl:text>because it's an unresolved reference to document type &apos;</xsl:text>
										<xsl:value-of select="$record/value[@name='rec_ref']"/><xsl:value-of select="$record/value[@name='recref']"/>
										<xsl:text>&apos;.</xsl:text>
									</xsl:when>
									<xsl:when test="$record/value[@name='rec_ref_skipped'] | $record/value[@name='rec_ref_not_found']">
										<xsl:text>because it's an unresolved reference. </xsl:text>
										<xsl:value-of select="$record/value[@name='rec_ref_skipped']"/><xsl:value-of select="$record/value[@name='rec_ref_not_found']"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>because it has an unexpected structure.</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:element>							
						</xsl:element>						
					</xsl:element>					
				</xsl:otherwise>
			</xsl:choose>		
		</xsl:if>
	</xsl:template>		
	<xsl:template name="Field">
		<xsl:param name="record"/>
		<xsl:element name="Field">
			<!-- name -->
			<xsl:attribute name="name">
				<xsl:choose>
					<xsl:when test="$record/value[@name='field_name'] != ''">
						<xsl:value-of select="$record/value[@name='field_name']"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>FieldNameNotFound</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>		
			<!-- optional -->
			<xsl:if test="$record/value[@name='field_opt'] != ''">
				<xsl:attribute name="optional"><xsl:value-of select="$record/value[@name='field_opt']"/></xsl:attribute>
			</xsl:if>
			<!-- nillable -->
			<xsl:if test="$record/value[@name='nillable'] != ''">
				<xsl:attribute name="nillable"><xsl:value-of select="$record/value[@name='nillable']"/></xsl:attribute>
			</xsl:if>			
			<!-- Try to get the basic xsd type (does not work with custom types), or the webMethods type. -->
			<xsl:choose>
				<xsl:when test="$record/record[@name='field_content_type']/array[@name='targetNames']/record/value[@name='ncName']">
					<xsl:attribute name="type"><xsl:value-of select="$record/record[@name='field_content_type']/array[@name='targetNames']/record/value[@name='ncName']"/></xsl:attribute>
				</xsl:when>
				<xsl:when test="$record/value[@name='field_type']">
					<xsl:attribute name="type"><xsl:value-of select="$record/value[@name='field_type']"/></xsl:attribute>
				</xsl:when>
			</xsl:choose>		
			<!-- list -->
			<xsl:if test="$record/value[@name='field_dim'] = '1'">
				<xsl:attribute name="list">true</xsl:attribute>
			</xsl:if>
			<!-- comment -->
			<xsl:if test="$record/value[@name='node_comment'] != ''">
				<xsl:element name="Comment"><xsl:value-of select="$record/value[@name='node_comment']"/></xsl:element>
			</xsl:if>
			<!-- options -->
			<xsl:if test="$record/array[@name='field_options']/value">
				<xsl:element name="Options">
					<xsl:for-each select="$record/array[@name='field_options']/value">
						<xsl:element name="Option">
							<xsl:value-of select="."/>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:if>			
			<!-- enumeration -->
			<xsl:if test="$record/record[@name='field_content_type']/array[@name='enumeration']">
				<xsl:element name="Enumeration">
					<xsl:for-each select="$record/record[@name='field_content_type']/array[@name='enumeration']/record">
						<xsl:element name="Value">
							<xsl:value-of select="value[@name='lexRep']"/>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:if>
			<!-- fields -->
			<xsl:call-template name="Fields">
				<xsl:with-param name="record" select="$record"/>
				<xsl:with-param name="elementName" select="'Fields'"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>

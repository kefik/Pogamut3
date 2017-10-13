<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : MessageTransformer.xslt.xml
    Created on : 14. cervenec 2008, 11:17
    Author     : knight
    Description:
        Transforms the MessageObjectsList.xml file to multiple files
-->
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="2.0">
    <xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">	
		<xsl:text disable-output-escaping="yes">
			<![CDATA[<!DOCTYPE messages SYSTEM "GBMessages.dtd">
			
			<messages>
			]]>
		</xsl:text>

		<xsl:copy-of select="document('config/Config.xml')"/>
	
		<xsl:text disable-output-escaping="yes">
			<![CDATA[<infomessages>]]>
		</xsl:text>	
	
		<xsl:for-each select="document('../../../target/gb2004/messages/InfoMessages.xml')/files/file">
			<xsl:copy-of select="document(@name)/messageobject"/>
		</xsl:for-each>
	
		<xsl:text disable-output-escaping="yes">
			<![CDATA[</infomessages>]]>
		</xsl:text>
	
		<xsl:text disable-output-escaping="yes">
			<![CDATA[<commands>]]>
		</xsl:text>
	
		<xsl:for-each select="document('../../../target/gb2004/messages/Commands.xml')/files/file">
			<xsl:copy-of select="document(@name)/commandobject"/>
		</xsl:for-each>
	
		<xsl:text disable-output-escaping="yes">
			<![CDATA[</commands>]]>
		</xsl:text>
	
		<xsl:text disable-output-escaping="yes">
			<![CDATA[</messages>]]>
		</xsl:text>
				
	</xsl:template>
	
</xsl:stylesheet>
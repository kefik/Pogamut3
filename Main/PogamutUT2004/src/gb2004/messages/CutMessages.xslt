<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : CutMessages.xslt
    Created on : 14. cervenec 2008, 11:17
    Author     : knight
    Description:
        Transforms the MessageObjectsList.xml file to multiple files
-->
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="2.0">
    <xsl:output method="xml" indent="yes"/>
   
 
    <!-- target directory for java files. !-->
    <xsl:variable name="commandsJavaDir">gbcommands/</xsl:variable> 
    <xsl:variable name="infoJavaDir">gbinfomessages/</xsl:variable>
           
   
    <xsl:template match="messageobject">      
    	<xsl:param name="filename" select="concat($infoJavaDir, @name,'.xml')"/>
<xsl:result-document href="{$filename}" method="xml" indent="yes">
<xsl:text disable-output-escaping="yes">
<![CDATA[<!DOCTYPE messageobject SYSTEM "../GBMessages.dtd">]]>

</xsl:text>
<xsl:copy-of select ="current()" />                              
</xsl:result-document>
    </xsl:template>
    
    <xsl:template match="commandobject">
    	<xsl:param name="filename" select="concat($commandsJavaDir, @name,'.xml')"/>
    	<xsl:param name="test" select="node()"/>
<xsl:result-document href="{$filename}" method="xml" indent="yes">
<xsl:text disable-output-escaping="yes">
<![CDATA[<!DOCTYPE commandobject SYSTEM "../GBMessages.dtd">]]>

</xsl:text>
<xsl:copy-of select ="current()" />
</xsl:result-document>
    </xsl:template>
    
</xsl:stylesheet>

  
  

<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : Utils.xslt
    Author     : Jimmy
    Description:
        Contains various utility templates.
-->
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="2.0">
    <xsl:output method="text" indent="yes"/>
    
    <!--
    	zeroValue($type)
    		- outputs "zero"/null value for a variable of certain $type
    		- example: $type == "double", outputs "0"
    		- default value of all objects (including numbers) is null 
     -->
     <xsl:template name="zeroValue">
        <xsl:param name="type"/>	 
     	<xsl:choose>
			<xsl:when test="$type = 'boolean'">false</xsl:when>
			<xsl:when test="$type = 'char'">' '</xsl:when>
			<xsl:when test="$type = 'byte'">0</xsl:when>
			<xsl:when test="$type = 'int'">0</xsl:when>
			<xsl:when test="$type = 'long'">0</xsl:when>
			<xsl:when test="$type = 'float'">0</xsl:when>
			<xsl:when test="$type = 'double'">0</xsl:when>
			<xsl:when test="$type = 'String'">null</xsl:when>
			<xsl:otherwise>null</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
    	stringValue($type)
    		- outputs String value for the certain $type
    		  - useful for construction of the prototype messages in GameBots style
    		- example: $type == "Point3d", outputs "0,0,0"
    		- default value of all unknown types is null 
     -->
     <xsl:template name="stringValue">
        <xsl:param name="type"/>
        <xsl:choose>
			<xsl:when test="$type = 'Boolean'">False</xsl:when>
			<xsl:when test="$type = 'boolean'">False</xsl:when>
			<xsl:when test="$type = 'Integer'">0</xsl:when>
			<xsl:when test="$type = 'int'">0</xsl:when>
			<xsl:when test="$type = 'Float'">0</xsl:when>
			<xsl:when test="$type = 'float'">0</xsl:when>
			<xsl:when test="$type = 'Double'">0</xsl:when>
			<xsl:when test="$type = 'double'">0</xsl:when>
			<xsl:when test="$type = 'Point3d'">0,0,0</xsl:when>
			<xsl:when test="$type = 'Vector3d'">0,0,0</xsl:when>
			<xsl:when test="$type = 'Location'">0,0,0</xsl:when>
			<xsl:when test="$type = 'Velocity'">0,0,0</xsl:when>
			<xsl:when test="$type = 'Rotation'">0,0,0</xsl:when>
			<xsl:when test="$type = 'UnrealId'">unreal_id</xsl:when>
			<xsl:when test="$type = 'ItemType'">xWeapons.FlakCannonPickup</xsl:when>
			<xsl:when test="$type = 'String'">text</xsl:when>
   			<xsl:otherwise>null</xsl:otherwise>
   		</xsl:choose>
   	</xsl:template>

	<!--
		uppercaseFirst($str)
			- outputs $str with first char being in upper-case 
	 -->
	<xsl:template name="uppercaseFirst">
        <xsl:param name="str"/>
        <xsl:param name="strLen" select="string-length($str)"/>
        <xsl:variable name="firstLetter" select="substring($str,1,1)"/>
        <xsl:variable name="restString" select="substring($str,2,$strLen)"/>
        <xsl:variable name="lower" select="'abcdefghijklmnopqrstuvwxyz'"/>
        <xsl:variable name="upper" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
        <xsl:variable name="translate" select="translate($firstLetter,$lower,$upper)"/>
        <xsl:value-of select="concat($translate,$restString)"/>
    </xsl:template>   
    
    <!--
		lowercaseFirst($str)
			- outputs $str with first char being in lower-case 
	 -->
	<xsl:template name="lowercaseFirst">
        <xsl:param name="str"/>
        <xsl:param name="strLen" select="string-length($str)"/>
        <xsl:variable name="firstLetter" select="substring($str,1,1)"/>
        <xsl:variable name="restString" select="substring($str,2,$strLen)"/>
        <xsl:variable name="lower" select="'abcdefghijklmnopqrstuvwxyz'"/>
        <xsl:variable name="upper" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
        <xsl:variable name="translate" select="translate($firstLetter,$upper,$lower)"/>
        <xsl:value-of select="concat($translate,$restString)"/>
    </xsl:template>
           
</xsl:stylesheet>

  
  
<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : Interfaces.xslt
    Author     : Jimmy
    Description:
        Provides templates for handling interface joining.
        E.g. '' + 'a, b' -> 'a,b' | 'a' + 'b' -> 'a, b', etc.
-->
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="2.0">
    <xsl:output method="text" indent="yes"/>
   
    <xsl:template name="addInterfaces">
    	<!-- Can be "", mustn't start or end with ',' -->
    	<xsl:param name="interfaces"/>
    	<!-- Can be "", mustn't start or end with ','  -->
    	<xsl:param name="interfacesToAdd"/>
    	
    	<xsl:choose>
	    	<xsl:when test="normalize-space($interfaces) = ''">
	    		<xsl:choose>
		    		<xsl:when test="normalize-space($interfacesToAdd) = ''">
		    			<xsl:value-of></xsl:value-of>
		    		</xsl:when>
		    		<xsl:otherwise>
		    			<xsl:value-of select="$interfacesToAdd"/>
		    		</xsl:otherwise>
		    	</xsl:choose>
	    	</xsl:when>
	    	<xsl:otherwise>
	    		<xsl:choose>
		    		<xsl:when test="normalize-space($interfacesToAdd) = ''">
		    			<xsl:value-of select="$interfaces"/>
		    		</xsl:when>
		    		<xsl:otherwise>    		
		    			<xsl:value-of select="concat($interfaces, ', ', $interfacesToAdd)"/>
		    		</xsl:otherwise>
		    	</xsl:choose>
	    	</xsl:otherwise>
	    </xsl:choose>
    </xsl:template>
           
</xsl:stylesheet>

  
  
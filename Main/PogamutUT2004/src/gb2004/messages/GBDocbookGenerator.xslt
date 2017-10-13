<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : MessageTransformer.xslt.xml
    Created on : 17. březen 2008, 10:53
    Author     : ik
    Description:
        Transforms the MessageObjectsList.xml file fo set of Java wrappers for 
        the GB messages.
-->
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="2.0">
    <xsl:output method="html" indent="yes"/>
    
    <xsl:template match="messages">
        <variablelist>
            <xsl:apply-templates/>
        </variablelist>
    </xsl:template>
    
    <!-- Transforms a command definition to a Docbook documentation. !-->
    
    <xsl:template match="commands">
        <section>
            <title>Commands</title>
            <variablelist>
				<xsl:apply-templates/>
            </variablelist>
        </section>
    </xsl:template>
    
    <xsl:template match="infomessages">
        <section>
            <title>Messages</title>
            <variablelist>
            <xsl:apply-templates/>
            </variablelist>
        </section>
    </xsl:template>
    
    <!-- Transforms a message definition to a Docbook documentation. !-->
    <xsl:template name="printMessage" match="messageobject">

	<xsl:if test="gbpackage[@name='GameBots2004']">
        <varlistentry>
            <term>
                <guimenuitem><xsl:value-of select="@message"/></guimenuitem> -
                <guimenuitem><xsl:value-of select="@name"/></guimenuitem> --
                Supported in packages: <xsl:apply-templates select="gbpackage"/>
                <guimenuitem>Connections: </guimenuitem> <xsl:apply-templates select="group"/>
            </term>           
           <listitem>
                <para>
                    <xsl:value-of select="documentation"/>
                </para>
                <variablelist>
                    <xsl:apply-templates select="property[not(@jflex)]"/>
                </variablelist>
            </listitem>
        </varlistentry>
		</xsl:if>
    </xsl:template>
    
        <xsl:template name="printCommand" match="commandobject">
		<xsl:if test="gbpackage[@name='GameBots2004']">
        <varlistentry>
            <term>
                <guimenuitem><xsl:value-of select="@command"/></guimenuitem> -
                <guimenuitem><xsl:value-of select="@name"/></guimenuitem> --
                Supported in packages: <xsl:apply-templates select="gbpackage"/>
                <guimenuitem>Connections: </guimenuitem> <xsl:apply-templates select="group"/>
            </term>
       
           <listitem>
                <para>
                    <xsl:value-of select="documentation"/>
                </para>
                <variablelist>
                    <xsl:apply-templates select="property"/>
                </variablelist>
            </listitem>
        </varlistentry>
		</xsl:if>
    </xsl:template>


 <xsl:template name="printProperty" match="property">
                  <varlistentry>
                            <term>
                                <guimenuitem><xsl:value-of select="@name"/> (<xsl:value-of select="@type"/>)</guimenuitem>
                            </term>
                            <listitem>
                                <para>
                                    <xsl:value-of select="documentation"/>
                                </para>
                            </listitem>
                        </varlistentry>
  </xsl:template>

 <xsl:template name="printGroup" match="group">
                            
          <xsl:value-of select="@name"/>, 

  </xsl:template>  
  
 <xsl:template name="printPackage" match="gbpackage">
                            
          <xsl:value-of select="@name"/>, 

  </xsl:template>  
  </xsl:stylesheet>
  
<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : MessageTransformer.xslt.xml
    Created on : 7th November 2010
    Author     : Jimmy
    Description:
        Transforms the MessageObjectsList.xml file fo set of Java wrappers for the GB messages.
-->
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0">
    <xsl:output method="text" indent="yes"/>

<!--
	INCLUDES
 -->
 	<xsl:include href="Interfaces.xslt" />
 	<xsl:include href="Utils.xslt" />
  <!--<xsl:include href="../../../../../../tools/docbook-xsl-1.73.2/xhtml/docbook.xsl"/>!-->
  <!--<xsl:include href="../../../../../../tools/docbook-xsl2-snapshot/base/html/html.xsl"/>!-->

<!--
	CONFIGURATION
 -->
    <!-- target directory for java files. !-->
    <xsl:variable name="commandsJavaDir">../../../src/main/java/cz/cuni/amis/pogamut/ut2004/communication/messages/gbcommands/</xsl:variable>
    <xsl:variable name="infoJavaDir">../../../src/main/java/cz/cuni/amis/pogamut/ut2004/communication/messages/gbinfomessages/</xsl:variable>

<!-- 
	FUNCTIONS	
 -->
 	<!--
 		typeMessage()
 			Used to create type name of the message property
 			CONTEXT: messageobject/property 
 	 -->
 	<xsl:template name="typeMessage">
 		<xsl:value-of select="@type" />        
 	</xsl:template>
 	
 	<!--
 		typeSharedMessage()
 			Used to create type name of the message property that is "shared", e.g., "VelocityProperty".
 			CONTEXT: messageobject/property 
 	 -->
 	<xsl:template name="typeSharedMessage"><xsl:call-template name="uppercaseFirst">
 			<xsl:with-param name="str" select="@type" />
 	</xsl:call-template>Property</xsl:template>
 	
 	<!--
 		typeCommand()
 			Used to create type name of the command property, contains type-masking,
 			instead of primitive types (boolean, int, float, double) outputs Boolean, 
 			Integer, Float or Double.
 			CONTEXT: commandobject/param 
 	 -->
 	<xsl:template name="typeCommand">
 		<xsl:choose>
            <xsl:when test="@type = 'boolean'">Boolean</xsl:when>
        	<xsl:when test="@type = 'int'">Integer</xsl:when>
        	<xsl:when test="@type = 'float'">Float</xsl:when>
        	<xsl:when test="@type = 'double'">Double</xsl:when>
        	<xsl:otherwise>
        		<xsl:value-of select="@type"/>
        	</xsl:otherwise>
       	</xsl:choose>
 	</xsl:template>
 	
 	<!--
 		getterCall($name, $type)
 			Generates call to a getter, e.g., getLocation(), isVisible(), based on the type.
 			@param name
 				name of the field
 			@param type
 				type of the field
 			CONTEXT: any 
 	 -->
 	<xsl:template name="getterCall">
 		<xsl:param name="name" />
 		<xsl:param name="type" />
 		<xsl:choose>
 			<xsl:when test="$type='boolean'"><xsl:text>is</xsl:text></xsl:when>
 			<xsl:when test="$type='Boolean'"><xsl:text>is</xsl:text></xsl:when>
 			<xsl:otherwise><xsl:text>get</xsl:text></xsl:otherwise>
 		</xsl:choose><xsl:call-template name="uppercaseFirst"><xsl:with-param name="str" select="$name"/></xsl:call-template>()
 	</xsl:template>
 	
 	<!--
 		getterDeclarationMessage($javaVisibility)
 			Generates declaration for the property getter, e.g.	public boolean getVisible()
 			@param javaVisibility
 				public, protected, private, ""
 			CONTEXT: messageobject/property 
 	 -->
 	<xsl:template name="getterDeclarationMessage">
 		<xsl:param name="javaVisibility" />
 		/**
         * <xsl:apply-templates select="documentation"/> 
         */
        <xsl:value-of select="$javaVisibility" />
        <xsl:text> </xsl:text>
        <xsl:call-template name="typeMessage" />
        <xsl:choose>
            <xsl:when test="@type='boolean'">
                <xsl:text> is</xsl:text>
            </xsl:when>
            <xsl:when test="@type='Boolean'">
                <xsl:text> is</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text> get</xsl:text>
            </xsl:otherwise>
        </xsl:choose><xsl:call-template name="uppercaseFirst">
            <xsl:with-param name="str" select="@name"/>
        </xsl:call-template>()
 	</xsl:template>
 	
 	<!--
 		getterDeclarationCommand($javaVisibility)
 			Generates declaration for the param getter, e.g. public Location getLocation()
 			@param javaVisibility
 				public, protected, private, ""
 			CONTEXT: commandobject/param 
 	 -->
 	<xsl:template name="getterDeclarationCommand">
 		<xsl:param name="javaVisibility" />
 		/**
         * <xsl:apply-templates select="documentation"/> 
         */
        <xsl:value-of select="$javaVisibility" />
        <xsl:text> </xsl:text>
        <xsl:call-template name="typeCommand" />
        <xsl:choose>
            <xsl:when test="@type='boolean'">
                <xsl:text> is</xsl:text>
            </xsl:when>
            <xsl:when test="@type='Boolean'">
                <xsl:text> is</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text> get</xsl:text>
            </xsl:otherwise>
        </xsl:choose><xsl:call-template name="uppercaseFirst">
            <xsl:with-param name="str" select="@name"/>
        </xsl:call-template>()
 	</xsl:template>
 	
 	<!--
 		setterDeclarationMessage($javaVisibility)
 			Generates declaration for the property setter, e.g.	public boolean setVisible(boolean Visible)
 			@param javaVisibility
 				public, protected, private, ""
 			CONTEXT: messageobject/property 
 	 -->
 	<xsl:template name="setterDeclarationMessage"> 		
 		<xsl:param name="javaVisibility" />
 		
 		/**
         * <xsl:apply-templates select="documentation"/> 
         */
        <xsl:value-of select="$javaVisibility" />
        <xsl:text> </xsl:text>
        void
        set<xsl:call-template name="uppercaseFirst">
            <xsl:with-param name="str" select="@name"/>
        </xsl:call-template>(<xsl:call-template name="typeMessage" /><xsl:text> </xsl:text><xsl:value-of select="@name" />)
 	</xsl:template>
 	
 	<!--
 		setterDeclarationCommand($javaVisibility)
 			Generates declaration for the param setter, e.g. public Jump setDoubleJump(Boolean DoubleJump)
 			@param javaVisibility
 				public, protected, private, ""
 			CONTEXT: commandobject/param
 	 -->
 	<xsl:template name="setterDeclarationCommand">
 		<xsl:param name="javaVisibility" />
 		
 		/**
         * <xsl:apply-templates select="documentation"/> 
         */
        <xsl:value-of select="$javaVisibility" />
        <xsl:text> </xsl:text>
        <xsl:value-of select="../@name" />
        <xsl:text> </xsl:text>
        set<xsl:call-template name="uppercaseFirst">
            <xsl:with-param name="str" select="@name"/>
        </xsl:call-template>(<xsl:call-template name="typeCommand" /><xsl:text> </xsl:text><xsl:value-of select="@name" />)
 	</xsl:template>

<!--
	SUBTEMPLATES (templates that makes part of the result files) 
 -->
 
 	<!--
 		header()
 			Used to write a notice at the beginning of the file that the file is auto-generated.
 			CONTEXT: commandobject or messageobject 
 	 -->
 	<xsl:template name="header">
	 	/**
         IMPORTANT !!!

         DO NOT EDIT THIS FILE. IT IS GENERATED FROM approriate xml file in <xsl:choose>
                      <xsl:when test="@command">xmlresources/gbcommands</xsl:when>
                      <xsl:when test="@message">xmlresources/gbinfomessages</xsl:when>
                      <xsl:otherwise>!ERROR!</xsl:otherwise>
                  </xsl:choose> BY
         THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         Use Ant task process-gb-messages after that to generate .java files again.
         
         IMPORTANT END !!!
        */
 	</xsl:template>
 	
 	<!--
 		javapackage($package)
	 		Used to output 'package' declation at the beginning of the .java class file.
	 		@param package
	 			FQPN of the package where the class resides.
	 		CONTEXT: any 
 	 -->
 	<xsl:template name="javapackage">
 		<xsl:param name="package"/>
 		<xsl:text>package </xsl:text><xsl:value-of select="$package"/><xsl:text>;</xsl:text>
 	</xsl:template>
 	
 	<!--
 		javaimport($package)
	 		Used to create 'import' declaration at the beginning of the .java class file.
	 		@param package
	 			FQCN of the class / FQPN (with *) of the package that should be imported.
	 		CONTEXT: any 
 	 -->
 	<xsl:template name="javaimport">
        <xsl:param name="package"/>
        <xsl:text>import </xsl:text><xsl:value-of select="$package"/><xsl:text>;</xsl:text>
    </xsl:template>
 	
 	<!--
 		fillImportsMessage($category, $type)
	 		Used to create all 'import' declarations at the beginning of the .java class file of the message of specific category and type.
	 		@param category
	 			possible values: base, static, local, shared
	 		@param type
	 			possible values: abstract, impl, proxy
 			CONTEXT: messageobject
 	 -->
 	<xsl:template name="fillImportsMessage">
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		
 		// --- IMPORTS FROM /messages/settings/javasettings/javaimport BEGIN
			<xsl:for-each select="/messages/settings/javasettings/javaimport">
			    <xsl:call-template name="javaimport">
			        <xsl:with-param name="package" select="@import"/>
			    </xsl:call-template>
			</xsl:for-each>
		// --- IMPORTS FROM /messages/settings/javasettings/javaimport END
		
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] BEGIN
			<xsl:for-each select="extra/code/java/javapart/classcategory[@name='all']/../javaimport">
			    <xsl:call-template name="javaimport">
			        <xsl:with-param name="package" select="@import"/>
			    </xsl:call-template>
			</xsl:for-each>	
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name='all'] END
		
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=<xsl:value-of select="$category" />]+classtype[@name=<xsl:value-of select="$type" />] BEGIN
		<xsl:for-each select="extra/code/java/javapart/classcategory[@name=$category]">
			<xsl:for-each select="../classtype[@name=$type]">
				<xsl:for-each select="../javaimport">
				    <xsl:call-template name="javaimport">
				        <xsl:with-param name="package" select="@import"/>
				    </xsl:call-template>
			    </xsl:for-each>
		    </xsl:for-each>
		</xsl:for-each>
		// --- IMPORTS FROM extra/code/java/javapart/classcategory[@name=<xsl:value-of select="$category" />]+classtype[@name=<xsl:value-of select="$type" />] END
    </xsl:template>
    
    <!--
 		fillImportsCommand()
	 		Used to create all 'import' declarations at the beginning of the .java class file of the command.
 			CONTEXT: commandobject
 	 -->
 	<xsl:template name="fillImportsCommand">
		<xsl:for-each select="/messages/settings/javasettings/javaimport">
		    <xsl:call-template name="javaimport">
		        <xsl:with-param name="package" select="@import"/>
		    </xsl:call-template>
		</xsl:for-each>
		<xsl:for-each select="extra/code/java/javaimport">
		    <xsl:call-template name="javaimport">
		        <xsl:with-param name="package" select="@import"/>
		    </xsl:call-template>
		</xsl:for-each>		
    </xsl:template>
    
    <!-- 
    
    	Event -> event (& impl)
    
    	Mover -> base & abstract
    	
    	MoverMessage -> base & message
    	     internally has 
    	     MoverMessageLocal  -> local  & message
    	     MoverMessageShared -> shared & message
    	     MoverMessageStatic -> static & message
    	
    	MoverCompositeImpl -> composite
    	
    	MoverLocal  -> local  & abstract
    	MoverShared -> shared & abstract
    	MoverStatic -> static & abstract
    	
    	MoverLocalImpl  -> local  & impl
    	MoverSharedImpl -> shared & impl
    	MoverStaticImpl -> static & impl
    
     -->
    
    <!--
 		javaDocClassMessage($category, $type)
	 		Used to output javadoc for the whole class of specific category and type.
	 		@param category
	 			possible values: base, composite, local, shared, static
	 		@param type
	 			possible values: abstract, message, impl (not relevant for type == composite)
 			CONTEXT: messageobject
 	 -->
 	<xsl:template name="javaDocClassMessage">
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		/**
         *  <xsl:choose>
         		<xsl:when test="$category='event'">
         			Definition of the event <code><xsl:value-of select="@message"/></code>.
         		</xsl:when>
            	<xsl:when test="$category='base'">
            		<xsl:choose>
            			<xsl:when test="$type='abstract'">
            				Abstract definition of the GameBots2004 message <code><xsl:value-of select="@message"/></code>.  
            			</xsl:when>
            			<xsl:when test="$type='message'">
             				Implementation of the GameBots2004 message <code><xsl:value-of select="@message"/> contains also its Local/Shared/Static subpart class definitions.</code>.  
            			</xsl:when>
            			<xsl:otherwise>
            				UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-JAVADOC-CLASS-MESSAGE
            			</xsl:otherwise>
            		</xsl:choose>
            	</xsl:when>
            	<xsl:when test="$category='composite'">
            		Composite implementation of the <code><xsl:value-of select="@message"/></code> abstract message. It wraps Local/Shared/Static parts in single object
            		allowing to presenting a nice facade for users.
            	</xsl:when>
            	<xsl:when test="$category='static'">
            		<xsl:choose>
            			<xsl:when test="$type='abstract'">
            				Abstract definition of the static part of the GameBots2004 message <code><xsl:value-of select="@message"/></code>.  
            			</xsl:when>
            			<xsl:when test="$type='impl'">
            				Implementation of the static part of the GameBots2004 message <code><xsl:value-of select="@message"/></code>.  
            			</xsl:when>
            			<xsl:when test="$type='message'">
            				Implementation of the static part of the GameBots2004 message <code><xsl:value-of select="@message"/></code>, used
            				to facade <code><xsl:value-of select="@message"/>Message</code>.  
            			</xsl:when>
            			<xsl:otherwise>
            				UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-JAVADOC-CLASS-MESSAGE
            			</xsl:otherwise>
            		</xsl:choose>
            	</xsl:when>
            	<xsl:when test="$category='local'">
            		<xsl:choose>
            			<xsl:when test="$type='abstract'">
            				Abstract definition of the local part of the GameBots2004 message <code><xsl:value-of select="@message"/></code>.  
            			</xsl:when>
            			<xsl:when test="$type='impl'">
            				Implementation of the local part of the GameBots2004 message <code><xsl:value-of select="@message"/></code>.  
            			</xsl:when>
            			<xsl:when test="$type='message'">
            				Implementation of the local part of the GameBots2004 message <code><xsl:value-of select="@message"/></code>, used
            				to facade <code><xsl:value-of select="@message"/>Message</code>.  
            			</xsl:when> 
            			<xsl:otherwise>
            				UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-JAVADOC-CLASS-MESSAGE
            			</xsl:otherwise>         			
            		</xsl:choose>
            	</xsl:when>
            	<xsl:when test="$category='shared'">
            		<xsl:choose>
            			<xsl:when test="$type='abstract'">
            				Abstract definition of the shared part of the GameBots2004 message <code><xsl:value-of select="@message"/></code>.  
            			</xsl:when>
            			<xsl:when test="$type='impl'">
            				Implementation of the shared part of the GameBots2004 message <code><xsl:value-of select="@message"/></code>.  
            			</xsl:when>
            			<xsl:when test="$type='message'">
            				Implementation of the shared part of the GameBots2004 message <code><xsl:value-of select="@message"/></code>, used
            				to facade <code><xsl:value-of select="@message"/>Message</code>.  
            			</xsl:when> 
            			<xsl:otherwise>
            				UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-JAVADOC-CLASS-MESSAGE
            			</xsl:otherwise>
            		</xsl:choose>
            	</xsl:when>
            </xsl:choose>
         *
         *  <![CDATA[<p></p><p></p>]]>
         *  Complete message documentation:               
         *  <xsl:apply-templates select="documentation"/>
         */
 	</xsl:template>
 	
 	<!--
 		javaDocClassCommand()
	 		Used to output javadoc for the command.
 			CONTEXT: commandobject
 	 -->
 	<xsl:template name="javaDocClassCommand">
 		/**
 		 * Representation of the GameBots2004 command <code><xsl:value-of select="@command"/></code>.
 		 *
 		 * <xsl:apply-templates select="documentation"/>
         */
 	</xsl:template>
    
    <!--
 		annotationsMessage($category, $type)
	 		Used to create all annotations for a concrete class based on its category and type.
	 		@param category
	 			possible values: base, composite, local, shared, static
	 		@param type
	 			possible values: abstract, message, impl (not relevant for type == composite)
 			CONTEXT: messageobject
 	 -->
 	<xsl:template name="annotationsMessage">
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		
 		<xsl:for-each select="annotation">
			@<xsl:value-of select="@name"/>
        </xsl:for-each>       
 	</xsl:template>
 	
 	<!--
 		annotationsCommand()
	 		Used to create all annotations for a command.
 			CONTEXT: commandobject
 	 -->
 	<xsl:template name="annotationsCommand">
 		<xsl:for-each select="annotation">
			@<xsl:value-of select="@name"/>
        </xsl:for-each>
 	</xsl:template>
 	
 	<!--
 		classDeclarationMessage($category, $type)
	 		Used to output "public class XXX" for a class of specific category and type.
	 		@param category
	 			possible values: base, composite, local, shared, static
	 		@param type
	 			possible values: abstract, message, impl (not relevant for type == composite)
 			CONTEXT: messageobject
 	 -->
 	<xsl:template name="classDeclarationMessage">
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		<xsl:text>public </xsl:text>
 		<xsl:choose>
 			<xsl:when test="$category = 'composite'"></xsl:when>
 			<xsl:otherwise>
 				<xsl:if test="$type='abstract'">
		 			<xsl:text>abstract </xsl:text>
		 		</xsl:if>
 			</xsl:otherwise>
 		</xsl:choose> 		
 		<xsl:text>class </xsl:text>
 		<xsl:call-template name="classNameMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
 		<xsl:text> </xsl:text>
	</xsl:template>
	
	<!--
 		classDeclarationCommand()
	 		Used to output "public class XXX" for a command.
 			CONTEXT: commandobject
 	 -->
 	<xsl:template name="classDeclarationCommand">
 		<xsl:text>public class </xsl:text>
 		<xsl:call-template name="classNameCommand" />
 		<xsl:text> </xsl:text>
	</xsl:template>
    
    <!-- 
    	classNameMessage($category, $proxy)
 			Generates class name for the message with respect to its category and whether it is a abstract/impl/proxy class.
 			@param category
 				possible values: base, composite, local, shared, static
 			@param type
 				possible values: abstract, message, impl (not relevant for type == composite)
 			CONTEXT: messageobject
 	-->
    <xsl:template name="classNameMessage">
    	<xsl:param name="category" />
    	<xsl:param name="type" />    	
    	<xsl:choose>
    		<xsl:when test="$category='event'"><xsl:value-of select="@name" /></xsl:when>
    		<xsl:when test="$category='base'"><xsl:choose>
	    		<xsl:when test="$type='abstract'"><xsl:value-of select="@name" /></xsl:when>
	    		<xsl:when test="$type='message'"><xsl:value-of select="@name" />Message</xsl:when>	    		
	    		<xsl:otherwise>
	    			UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-FOR-CLASS-NAME
	    		</xsl:otherwise>
		    </xsl:choose></xsl:when>
		    <xsl:when test="$category='composite'"><xsl:value-of select="@name" />CompositeImpl</xsl:when>
		    <xsl:otherwise><xsl:choose>
		    	<!-- local/shared/static -->
		    	<xsl:when test="$type='abstract'"><xsl:value-of select="@name" /><xsl:call-template name="uppercaseFirst"><xsl:with-param name="str" select="$category" /></xsl:call-template></xsl:when>
		    	<xsl:when test="$type='impl'"><xsl:value-of select="@name" /><xsl:call-template name="uppercaseFirst"><xsl:with-param name="str" select="$category" /></xsl:call-template>Impl</xsl:when>
		    	<xsl:when test="$type='message'"><xsl:value-of select="@name" /><xsl:call-template name="uppercaseFirst"><xsl:with-param name="str" select="$category" /></xsl:call-template>Message</xsl:when>
		    	<xsl:otherwise>
	    			UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-FOR-CLASS-NAME
	    		</xsl:otherwise>
		    </xsl:choose></xsl:otherwise>    				   
    	</xsl:choose>    	
    </xsl:template>
    
    <!-- 
    	classNameCommand()
 			Generates name for the command.
 			CONTEXT: commandobject
 	-->
    <xsl:template name="classNameCommand">
    	<xsl:value-of select="@name" />
    </xsl:template>
    
    <!--
    	extendsMessage($category, $type)
    		Generates "extends part" for a class the message is extending with respect to its category and whether it is a abstract/impl/proxy class.
    		@param category
 				possible values: base, composite, local, shared, static,
 			@param type
 				possibdle values: abstract, message, impl (not relevant for type == composite) 
			CONTEXT: messageobject
     -->
    <xsl:template name="extendsMessage">
    	<xsl:param name="category" />
    	<xsl:param name="type" />
  		<xsl:choose>
  			<xsl:when test="$category='event'">
  				extends <xsl:value-of select="@extends" />
  			</xsl:when>
  			<xsl:when test="$category='base'">  
  				extends 
  				<xsl:choose>
  					<xsl:when test="$type='abstract'">
  						<xsl:value-of select="@extends" />
  					</xsl:when>
  					<xsl:when test="$type='message'">
  						<xsl:call-template name="classNameMessage"><xsl:with-param name="category">base</xsl:with-param><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>
  					</xsl:when>
  					<xsl:otherwise>
  						UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-FOR-EXTENDS-MESSAGE
  					</xsl:otherwise>
  				</xsl:choose>
  			</xsl:when>
  			<xsl:when test="$category='composite'">
  				extends <xsl:call-template name="classNameMessage"><xsl:with-param name="category">base</xsl:with-param><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>
  			</xsl:when>
  			<xsl:otherwise>
  				<!-- local/shared/static -->
  				<xsl:choose>
  					<xsl:when test="$type='abstract'">
  						extends <xsl:value-of select="@extends" />
  					</xsl:when>
  					<xsl:when test="$type='impl'">
  						extends
  						<xsl:call-template name="classNameMessage"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>
  					</xsl:when>
  					<xsl:when test="$type='message'">
	  					extends
  						<xsl:call-template name="classNameMessage"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>
  					</xsl:when>
  					<xsl:otherwise>
  						UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-FOR-EXTENDS-MESSAGE
  					</xsl:otherwise>
  				</xsl:choose>
  			</xsl:otherwise>
   		</xsl:choose>
    </xsl:template>
    
    <!--
    	extendsCommand()
    		Generates "extends part" for a class the command is extending.
			CONTEXT: commandobject
     -->
    <xsl:template name="extendsCommand">
		extends <xsl:value-of select="@extends" />
    </xsl:template>
    
    <!-- 
    	additionalInterfacesMessage($category, $type)
 			Generates nothing or ,interf1, interf2, ..." for the message with respect to passed 'category', its
 			interfaces are taken from actual elements 'implements'.
 			@param category
 				possible values: event, base, local, static, shared
 			@param type
 				possible values: abstract, message, impl (not relevant for type == composite)
 			CONTEXT: messageobject
 	-->
    <xsl:template name="additionalInterfacesMessage">
    	<!-- TEMPLATE PARAMS -->
    	<xsl:param name="category" />
    	<xsl:param name="type" />
    	
    	<!-- TEMPLATE BODY -->    
    	<xsl:if test="./implements and not(normalize-space(./implements/@interfaces) = '')">
	    	,<xsl:text> </xsl:text><xsl:value-of select="./implements/@interfaces" />
	    </xsl:if>
	    <xsl:for-each select="./interface/classcategory[@name='all']/..">
	    	,<xsl:value-of select="@name"/>
	    </xsl:for-each>
	    <xsl:for-each select="./interface/classcategory[@name=$category]/..">
	    	<xsl:for-each select="./classtype[@name=$type]/..">
	    		,<xsl:value-of select="@name"/>
	    	</xsl:for-each>
	    </xsl:for-each>    	
    </xsl:template>
    
    
    <!-- 
    	interfacesMessage($category, $type)
 			Generates nothing or "implements interf1, interf2, ..." for the message with respect to whether it is abstract/impl/proxy type.
 			@param category
 				possible values: event, base, local, static, shared
 			@param type
 				possible values: abstract, message, impl (not relevant for type == composite)
 			CONTEXT: messageobject
 	-->
    <xsl:template name="interfacesMessage">
   		<!-- TEMPLATE PARAMS -->
    	<xsl:param name="category" />    
    	<xsl:param name="type" />
    	
    	<!-- TEMPLATE BODY -->
    	<xsl:choose>
    		<xsl:when test="$category='event'">
    			implements IWorldEvent, IWorldChangeEvent
    			<xsl:call-template name="additionalInterfacesMessage">
    				<xsl:with-param name="category">event</xsl:with-param>
    				<xsl:with-param name="type">impl</xsl:with-param>
    			</xsl:call-template>
    		</xsl:when>
  			<xsl:when test="$category='base'">
  				<xsl:choose>
  					<xsl:when test="$type='abstract'">
  						implements IWorldEvent, IWorldChangeEvent, ICompositeWorldObject
  						<xsl:call-template name="additionalInterfacesMessage">
  							<xsl:with-param name="category">base</xsl:with-param>
  							<xsl:with-param name="type">abstract</xsl:with-param>
  						</xsl:call-template>
  					</xsl:when>
  					<xsl:when test="$type='message'">
  						implements IWorldObjectUpdatedEvent, ICompositeWorldObjectUpdatedEvent
  						<xsl:call-template name="additionalInterfacesMessage">
  							<xsl:with-param name="category">base</xsl:with-param>
  							<xsl:with-param name="type">message</xsl:with-param>
  						</xsl:call-template>
  					</xsl:when>
  				</xsl:choose>
  			</xsl:when>
  			<xsl:when test="$category='composite'">  				
  			</xsl:when>
  			<xsl:otherwise>
  				<!-- local/shared/static -->
  				<xsl:choose>
  					<xsl:when test="$type='abstract'">
  						implements I<xsl:call-template name="uppercaseFirst"><xsl:with-param name="str" select="$category" /></xsl:call-template>WorldObject
  						<xsl:call-template name="additionalInterfacesMessage">
  							<xsl:with-param name="category" select="$category" />
  							<xsl:with-param name="type">abstract</xsl:with-param>
  						</xsl:call-template>
  					</xsl:when>
  					<xsl:when test="$type='impl'">  
  						<xsl:call-template name="additionalInterfacesMessage">
  							<xsl:with-param name="category" select="$category" />
  							<xsl:with-param name="type">impl</xsl:with-param>
  						</xsl:call-template>						
  					</xsl:when>
  					<xsl:when test="$type='message'">	  	
  						<xsl:call-template name="additionalInterfacesMessage">
  							<xsl:with-param name="category" select="$category" />
  							<xsl:with-param name="type">message</xsl:with-param>
  						</xsl:call-template>				
  					</xsl:when>
  					<xsl:otherwise>
  						UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-FOR-INTERFACES-MESSAGE
  					</xsl:otherwise>
  				</xsl:choose>
  			</xsl:otherwise>
   		</xsl:choose>
    </xsl:template>
    
    <!-- 
    	interfacesCommand()
 			Generates nothing or "implements interf1, interf2, ..." for the command.
 			CONTEXT: commandobject
 	-->
    <xsl:template name="interfacesCommand">
    	<!-- list of interfaces, some from the implements tag, some determined by the message type.
-->
   		<xsl:variable name="interfaces" />
        <xsl:variable name="interfaces2" />
        <xsl:variable name="interfaces3">
        	<xsl:call-template name="addInterfaces">
        		<xsl:with-param name="interfaces" select="$interfaces2"/>
        		<xsl:with-param name="interfacesToAdd" select="./implements/@interfaces"/>
        	</xsl:call-template>
        </xsl:variable>
        <xsl:choose>
           	<xsl:when test="normalize-space($interfaces3) = ''">
           	</xsl:when>
           	<xsl:otherwise>
            	implements <xsl:value-of select="$interfaces3"/>
   	        </xsl:otherwise>
       	</xsl:choose>
	</xsl:template>
 	
 	<!-- 
 		prototypeCreatorMessage($category, $type)
 			Generates prototype string with the message.
 			@param category
 				possible values: event, base, local, static, shared
 			@param type
 				possible values: abstract, message, impl (not relevant for type == composite)
 			CONTEXT: messageobject
 	-->
    <xsl:template name="prototypeCreatorMessage">
    	<xsl:param name="category" />
    	<xsl:param name="type" />
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"<xsl:value-of select="@message"/><xsl:for-each select="property"><xsl:if test="$category='event' or $category='base' or $category=@category or @category='all' or @category='time'"><xsl:if test="not(@jflex)"><xsl:text> {</xsl:text><xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:call-template name="stringValue">
    			<xsl:with-param name="type" select="@type"/>
    		</xsl:call-template><xsl:text>} </xsl:text></xsl:if></xsl:if></xsl:for-each>";
    </xsl:template>
    
    <!-- 
 		prototypeCreatorCommand()
 			Generates prototype string with the message.
 			CONTEXT: commandobject
 	-->
    <xsl:template name="prototypeCreatorCommand">
    	/** Example how the message looks like - used during parser tests. */
    	public static final String PROTOTYPE =
    		"<xsl:value-of select="@message"/><xsl:for-each select="property"><xsl:if test="not(@jflex)"><xsl:text> {</xsl:text><xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:call-template name="stringValue">
    			<xsl:with-param name="type" select="@type"/>
    		</xsl:call-template><xsl:text>} </xsl:text></xsl:if></xsl:for-each>";
    </xsl:template>
    
    
    <!--
    	constructorCreatorParameterless($classname)
    		Generates parameterless constructor of the message
    		@param className 
    			name of the class
    		CONTEXT: messageobject
     -->
    <xsl:template name="constructorCreatorParameterless">
    	<!-- TEMPLATE PARAMS -->
    	<xsl:param name="className" />
    	
    	<!-- TEMPLATE BODY -->
    	/**
    	 * Parameter-less contructor for the message.
    	 */
		public <xsl:value-of select="$className"/>()
		{
		}
	</xsl:template>
    
    <!--
    	constructorCreatorAllPropertiesFromCategory($category, $classname)
    		Generates constructor that contains all properties from the $categroy.
    		@param category 
    			possible values all, static, local, shared
    		@param className 
    			name of the class
    		CONTEXT: messageobject
     -->
    <xsl:template name="constructorCreatorAllPropertiesFromCategory">
    	<!-- TEMPLATE PARAMS -->
    	<xsl:param name="category" />
    	<xsl:param name="className" />
    	
    	<!-- TEMPLATE BODY -->
    	
    	/**
		 * Creates new instance of the message <xsl:value-of select="@name"/>.
		 * <xsl:apply-templates select="documentation"/>
		 * <p>Corresponding GameBots message
		 *   <xsl:if test="not($category='all')">(<xsl:value-of select="$category" /> part)</xsl:if>
		 *   is
		 *   <code><xsl:value-of select="@message"/></code>.
		 * </p>
 	  	 * <xsl:for-each select="property">
		 *   <xsl:if test="$category='all' or @category='all' or @category='time' or @category=$category">
		 *     @param <xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:apply-templates select="documentation"/>
		 *   </xsl:if>
		 * </xsl:for-each>
		 */
		public <xsl:value-of select="$className"/>(
			<xsl:for-each select="property">
				<xsl:if test="$category='all' or @category='all' or @category='time' or @category=$category">
	           		<xsl:call-template name="typeMessage" />
	             		<xsl:text> </xsl:text>
	             		<xsl:value-of select="@name"/>
	                <xsl:if test="following-sibling::property[$category='all' or @category='all' or @category='time' or @category=$category]">
						<xsl:text>,  </xsl:text>
					</xsl:if>
				 </xsl:if>
			</xsl:for-each>
		) {
			<xsl:for-each select="property">
				<xsl:if test="$category='all' or @category='all' or @category='time' or @category=$category">
					this.<xsl:value-of select="@name"/> = <xsl:value-of select="@name"/>;
				</xsl:if>
			</xsl:for-each>
		}
    </xsl:template>
    
     <!--
    	constructorCreatorClones($category)
    		Generates constructor that clones all properties from the $categroy.
    		@param category 
    			which properties to clone - possible values: all, static, local, shared
    		@param type
    			message, part
    		@param className 
    			name of the class
    		CONTEXT: messageobject
     -->
    <xsl:template name="constructorCreatorClones">
    	<!-- TEMPLATE PARAMS -->
    	<xsl:param name="category" />
    	<xsl:param name="type" />
    	<xsl:param name="className" />
    	
    	<!-- TEMPLATE VARIABLES -->
    	<xsl:variable name="classNameAbstract"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">base</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNamePart"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category" select="$category" />
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->
    	<xsl:if test="not((@type='ObjectUpdate') and (($category='all') or ($category='shared')))">
    	/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public <xsl:value-of select="$className"/>(<xsl:value-of select="$classNameAbstract"/> original) {		
			<xsl:for-each select="property">
           		<xsl:if test="$category='all' or @category='all' or @category='time' or @category=$category">
					this.<xsl:value-of select="@name"/> = original.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
				</xsl:if>
			</xsl:for-each>
			this.SimTime = original.getSimTime();			
		}
		</xsl:if>
		
		<xsl:if test="not($className = $classNameAbstract)">
		/**
		 * Cloning constructor from the full message.
		 *
		 * @param original
		 */
		public <xsl:value-of select="$className"/>(<xsl:value-of select="$className"/> original) {		
			<xsl:for-each select="property">
           		<xsl:if test="$category='all' or @category='all' or @category='time' or @category=$category">
					this.<xsl:value-of select="@name"/> = original.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
				</xsl:if>
			</xsl:for-each>
			<xsl:if test="(@type='ObjectUpdate') and (($category='all') or ($category='shared'))">
				this.TeamId = original.getTeamId();
			</xsl:if>
			this.SimTime = original.getSimTime();
		}
		</xsl:if>
		
		<xsl:if test="not($category='all')">
			/**
			 * Cloning constructor from the message part.
			 *
			 * @param original
			 */
			public <xsl:value-of select="$className"/>(<xsl:value-of select="$classNamePart"/> original) {
				<xsl:for-each select="property">
	           		<xsl:if test="$category='all' or @category='all' or @category='time' or @category=$category">
						this.<xsl:value-of select="@name"/> = original.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					</xsl:if>
				</xsl:for-each>
			}
		</xsl:if>
	</xsl:template>
 	
 	<!-- 
 		constructorsCreatorMessageEvent()
	 		Generates constructor(s) for the message class for the EVENT.
 			CONTEXT: messageobject
 	-->
    <xsl:template name="constructorsCreatorMessageEvent">
		<!-- TEMPLATE VARIABLES -->    	
    	<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">event</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->    	
    	<xsl:call-template name="constructorCreatorParameterless">
			<xsl:with-param name="className" select="$className" />
		</xsl:call-template>
		
		<xsl:if test="not(count(property) = 0)">
			<xsl:call-template name="constructorCreatorAllPropertiesFromCategory">
				<xsl:with-param name="category">all</xsl:with-param>
				<xsl:with-param name="className" select="$className" />
			</xsl:call-template>
		</xsl:if>
		
		<xsl:call-template name="constructorCreatorClones">
			<xsl:with-param name="category">all</xsl:with-param>
			<xsl:with-param name="type">impl</xsl:with-param>
			<xsl:with-param name="className" select="$className" />
		</xsl:call-template>
    </xsl:template>
    
    <!-- 
 		constructorsCreatorMesaageBase($type)
	 		Generates constructor(s) for the message category BASE.
 			@param type 
 				possible values are abstract, message
 			CONTEXT: messageobject
 	-->
    <xsl:template name="constructorsCreatorMessageBase">
    	<!-- TEMPLATE PARAMS -->
    	<xsl:param name="type" />
    	
		<!-- TEMPLATE VARIABLES -->    	
    	<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">base</xsl:with-param>
    		<xsl:with-param name="type" select="$type" />
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->    	
    	<xsl:call-template name="constructorCreatorParameterless">
			<xsl:with-param name="className" select="$className" />
		</xsl:call-template>
		
		<xsl:choose>
			<xsl:when test="$type='abstract'">
				// abstract message, it does not have any more constructors				
			</xsl:when>
			<xsl:when test="$type='message'">
				<xsl:if test="not(count(property) = 0)">
					<xsl:call-template name="constructorCreatorAllPropertiesFromCategory">
						<xsl:with-param name="category">all</xsl:with-param>
						<xsl:with-param name="className" select="$className" />
					</xsl:call-template>
				</xsl:if>
				<xsl:call-template name="constructorCreatorClones">
					<xsl:with-param name="category">all</xsl:with-param>
					<xsl:with-param name="type">message</xsl:with-param>
					<xsl:with-param name="className" select="$className" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				UNSUPPORTED-COMBINATION-base/<xsl:value-of select="$type"/>-FOR-CONSTRUCTORS-CREATOR-MESSAGE-BASE
  			</xsl:otherwise>
		</xsl:choose>
    </xsl:template>
 	
 	<!-- 
 		constructorsCreatorMessageComposite()
	 		Generates constructor(s) for the message class of the COMPOSITE category.
 			CONTEXT: messageobject
 	-->
    <xsl:template name="constructorsCreatorMessageComposite">
		<!-- TEMPLATE VARIABLES -->    	
    	<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">composite</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameStaticImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">static</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameLocalImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">local</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameSharedImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">shared</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->    	
    	<xsl:call-template name="constructorCreatorParameterless">
			<xsl:with-param name="className" select="$className" />
		</xsl:call-template>
		
		/**
		 * Composite-impl constructor. It assembles the message from its three fragments - local/shared/static.
		 *
		 * @param partLocal local-part of the message
		 * @param partShared shared-part of the message
		 * @param partStatic static-part of the message
		 */
		public <xsl:value-of select="$className" />(
			<xsl:value-of select="$classNameLocalImpl" /> partLocal,
			<xsl:value-of select="$classNameSharedImpl" /> partShared,
			<xsl:value-of select="$classNameStaticImpl" /> partStatic
		) {
			this.partLocal  = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
		
		/**
		 * Cloning constructor.
		 *
		 * @param original		 
		 */
		public <xsl:value-of select="$className" />(<xsl:value-of select="$className" /> original) {
			this.partLocal = partLocal;
			this.partShared = partShared;
			this.partStatic = partStatic;
		}
    </xsl:template>
    
    <!-- 
 		constructorsCreatorSharedMessage()
	 		Generates constructors for the Message/Impl message class.
 			CONTEXT: messageobject
 	-->
    <xsl:template name="constructorsCreatorSharedMessage">
    	<!-- TEMPLATE VARIABLES -->
    	<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">shared</xsl:with-param>
    		<xsl:with-param name="type">message</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->
		public <xsl:value-of select="$className"/>()
		{
			<xsl:for-each select="property[@category='shared']">
				propertyMap.put(my<xsl:value-of select="@name"/>.getPropertyId(), my<xsl:value-of select="@name"/>);
			</xsl:for-each>
		}		
    </xsl:template>
    
    <!-- 
 		constructorsCreatorSharedImpl()
	 		Generates constructors for the Shared/Impl message class.
 			CONTEXT: messageobject
 	-->
    <xsl:template name="constructorsCreatorSharedImpl">
    	<!-- TEMPLATE VARIABLES -->
    	<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">shared</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameAbstract"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">shared</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    
    	<!-- TEMPLATE BODY -->
    	public <xsl:value-of select="$className"/>(<xsl:value-of select="$className"/> source) {
			<xsl:for-each select="property[@category='all']">
				this.<xsl:value-of select="@name" /> = source.
					<xsl:call-template name="getterCall">
						<xsl:with-param name="name" select="@name" />
						<xsl:with-param name="type" select="@type" />
					</xsl:call-template>;
			</xsl:for-each>
			<xsl:for-each select="property[@category='shared']">
				this.my<xsl:value-of select="@name" /> = source.my<xsl:value-of select="@name" />;
			</xsl:for-each>
		}
		
		public <xsl:value-of select="$className"/>(WorldObjectId objectId, Collection&lt;ISharedProperty&gt; properties) {
			this.Id = (UnrealId)objectId;
			NullCheck.check(this.Id, "objectId");
		
			if (properties.size() != <xsl:value-of select="count(property[@category='shared'])" />) {
				throw new PogamutException("Not enough properties passed to the constructor.", <xsl:value-of select="$className"/>.class);
			}
		
			//we have to do some checking in this one to know that we get all properties required
			for ( ISharedProperty property : properties ) {
				PropertyId pId = property.getPropertyId();
				if ( !objectId.equals( property.getObjectId() )) {
					//properties for different objects
					throw new PogamutException("Trying to create a <xsl:value-of select="$className"/> with different WorldObjectId properties : " + 
											    this.Id.getStringId() + " / " + property.getObjectId().getStringId() , this);
				}
				if (!<xsl:value-of select="$classNameAbstract"/>.SharedPropertyTokens.contains(pId.getPropertyToken())) {
				// property that does not belong here
				throw new PogamutException("Trying to create a <xsl:value-of select="$className"/> with invalid property (invalid property token): " + 
					this.Id.getStringId() + " / " + property.getPropertyId().getPropertyToken().getToken() , this);
				}
				propertyMap.put(property.getPropertyId(), property);
				
				<xsl:for-each select="property[@category='shared']">
					if (pId.getPropertyToken().getToken().equals("<xsl:value-of select="@name" />"))
					{
						this.my<xsl:value-of select="@name" /> = (<xsl:call-template name="typeSharedMessage" />)property;
					}
				</xsl:for-each>
			}
		}
    </xsl:template>
 	
 	<!-- 
 		constructorsCreatorMessagePart($category, $type)
	 		Generates constructor(s) for the message class of specific category (part) and its type.
	 		@param category
	 			possible values: local, static, shared
	 		@param type
	 			possible values: abstract, impl
 			CONTEXT: messageobject
 	-->
    <xsl:template name="constructorsCreatorMessagePart">
    	<!-- TEMPLATE PARAMETERS -->
    	<xsl:param name="category" />
    	<xsl:param name="type" />
	
		<!-- TEMPLATE VARIABLES -->
		<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category" select="$category"/>
    		<xsl:with-param name="type" select="$type" />
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->
		<xsl:choose>
			<xsl:when test="$type='abstract'">			
				<xsl:call-template name="constructorCreatorParameterless">
					<xsl:with-param name="className" select="$className" />
				</xsl:call-template>	
				// abstract definition of the <xsl:value-of select="$category" />-part of the message, no more constructors is needed
			</xsl:when>
			<xsl:when test="$type='impl'">
				<xsl:choose>
					<xsl:when test="($category='static') or ($category='local')">
						<xsl:call-template name="constructorCreatorParameterless">
							<xsl:with-param name="className" select="$className" />
						</xsl:call-template>
						<xsl:if test="not(count(property[@category='all']) + count(property[@category='time'] + count(property[@category=$category])) = 0)">
							<xsl:call-template name="constructorCreatorAllPropertiesFromCategory">
								<xsl:with-param name="category"  select="$category"  />
								<xsl:with-param name="className" select="$className" />
							</xsl:call-template>
						</xsl:if>
						<xsl:call-template name="constructorCreatorClones">
							<xsl:with-param name="category"  select="$category"  />
							<xsl:with-param name="type">impl</xsl:with-param>
							<xsl:with-param name="className" select="$className" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$category='shared'">
						<xsl:call-template name="constructorsCreatorSharedImpl" />
					</xsl:when>
					<xsl:otherwise>
						UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-FOR-CONSTRUCTORS-CREATOR-MESSAGE-PART
		  			</xsl:otherwise>	
				</xsl:choose>				
			</xsl:when>		
			<xsl:when test="$type='message'">
			 	<xsl:call-template name="constructorsCreatorSharedMessage" />
			</xsl:when>
			<xsl:otherwise>
				UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-FOR-CONSTRUCTORS-CREATOR-MESSAGE-PART
  			</xsl:otherwise>	
		</xsl:choose>  
    </xsl:template>
    
 	<!-- 
 		constructorsCreatorCommand()
 			Generates constructor(s) for the command class.
 			CONTEXT: commandobject
 	-->
    <xsl:template name="constructorsCreatorCommand">
		/**
		 * Creates new instance of command <xsl:value-of select="@name"/>.
		 * <xsl:apply-templates select="documentation"/>
		 * <p>Corresponding GameBots message for this command is
		 * <code><xsl:value-of select="@command"/></code>.</p>
		 *
		 * <xsl:for-each select="property">
		 *    @param <xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:apply-templates select="documentation"/></xsl:for-each>
		 */
		public <xsl:value-of select="@name"/>(
			<xsl:for-each select="property">
				<xsl:call-template name="typeCommand" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="@name"/>
				<xsl:if test="position() != last()">
					<xsl:text>,  </xsl:text>
				</xsl:if>
			</xsl:for-each>
		) {
			<xsl:for-each select="property">
				this.<xsl:value-of select="@name"/> = <xsl:value-of select="@name"/>;
            </xsl:for-each>
		}

		<xsl:if test="not(count(property) = 0)">
			/**
			 * Creates new instance of command <xsl:value-of select="@name"/>.
			 * <xsl:apply-templates select="documentation"/>
			 * <p>Corresponding GameBots message for this command is
			 * <code><xsl:value-of select="@command"/></code>.</p>
			 * <xsl:text><![CDATA[<p></p>]]></xsl:text>
			 * <b>WARNING:</b> this is empty-command constructor, you have to use setters to fill it up with data that should be sent to GameBots2004!
		     */
		    public <xsl:value-of select="@name"/>() {
		    }
		</xsl:if>	
		
		/**
		 * Cloning constructor.
		 *
		 * @param original
		 */
		public <xsl:value-of select="@name"/>(<xsl:value-of select="@name"/> original) {
		   <xsl:for-each select="property">
		        this.<xsl:value-of select="@name"/> = original.<xsl:value-of select="@name"/>;
		   </xsl:for-each>
		}
    </xsl:template>
    
    <!-- 
    	propertyDeclaraction()
    		Outputs declarations of one property.
 			CONTEXT: messageobject/property
 	-->
    <xsl:template name="propertyDeclaration">
    	<!-- TEMPLATE BODY -->
	    /**
         * <xsl:apply-templates select="documentation"/> 
         */
        protected
        <xsl:text> </xsl:text>
        <xsl:call-template name="typeMessage" />        
        <xsl:text> </xsl:text>
        <xsl:value-of select="@name"/> =
       	<xsl:choose>
       		<xsl:when test="@default">
       			<xsl:value-of select="@default"/>
       		</xsl:when>
       		<xsl:otherwise>
       			<xsl:call-template name="zeroValue"><xsl:with-param name="type" select="@type" /></xsl:call-template>
       		</xsl:otherwise>
		</xsl:choose>;
	</xsl:template>
	
	<!-- 
    	propertySharedDeclaraction()
    		Outputs declarations of one shared-property. Does not end with semicolon, does not initialize any value.
 			CONTEXT: messageobject/property
 	-->
    <xsl:template name="propertySharedDeclaration">
    	<!-- TEMPLATE BODY -->
	    /**
         * <xsl:apply-templates select="documentation"/> 
         */
        protected
        <xsl:text> </xsl:text>
        <xsl:call-template name="typeSharedMessage" />        
        <xsl:text> </xsl:text>
        my<xsl:value-of select="@name"/>          
	</xsl:template>
	
	<!--
		propertiesShared($type)
    		Outputs declarations of ALL shared-properties + all getters + special methods.
    		@param type
    			possible values: impl, message 
 			CONTEXT: messageobject/property 
	 -->
	<xsl:template name="propertiesShared">
		<!-- TEMPLATE PARAMS -->
		<xsl:param name="type" />
		
		<!-- TEMPLATE BODY -->
		<!-- Generate utility stuff. -->
		protected HashMap&lt;PropertyId, ISharedProperty&gt; propertyMap = new HashMap&lt;PropertyId, ISharedProperty&gt;(
			<xsl:value-of select="count(property[@category='shared'])" />
		);
		
		@Override
		public ISharedProperty getProperty(PropertyId id) {
			return propertyMap.get(id);
		}

		@Override
		public Map&lt;PropertyId, ISharedProperty&gt; getProperties() {
			return propertyMap;
		}
	
		<!-- Generate normal property declarations + getters.
-->
		<xsl:for-each select="property[@category='all']">
			<xsl:if test="$type='impl'">
				<!-- Declaration only for 'impl' ... 'message' does not need them. -->
				<xsl:call-template name="propertyDeclaration" />
			</xsl:if>
			<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public </xsl:with-param></xsl:call-template> {
  			return <xsl:value-of select="@name" />;
  		}
  		</xsl:for-each>
					
		<!-- Generate shared property declarations + getters. -->
		<xsl:choose>
			<xsl:when test="$type='message'">
				<xsl:for-each select="property[@category='shared']">
					<xsl:call-template name="propertySharedDeclaration" />
					= new
					<xsl:call-template name="typeSharedMessage" />
					(
						getId(), 
						"<xsl:value-of select="@name"/>", 
						<xsl:value-of select="@name"/>, 
						<xsl:value-of select="../@name" />.class
					);
					<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public </xsl:with-param></xsl:call-template> {
			  			return my<xsl:value-of select="@name" />.getValue();
			  		}
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="$type='impl'">
				<xsl:for-each select="property[@category='shared']">
					<xsl:call-template name="propertySharedDeclaration" />
					= null;
					
					<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public </xsl:with-param></xsl:call-template> {
			  			return my<xsl:value-of select="@name" />.getValue();
			  		}
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				UNSUPPORTED-COMBINATION-shared/<xsl:value-of select="$type"/>-PROPERTIES-SHARED
			</xsl:otherwise>
		</xsl:choose>
		
	
	</xsl:template>

    <!-- 
    	propertiesMessage($category, $type)
    		Outputs declarations of all properties (+ their getters) of the object with respect to the passed category of the class and the type.
    		@param category
    			possible values: event, base, local, static, shared
    		@param type
    			possible values: abstract, message, impl (not relevant for type == composite)
 			CONTEXT: messageobject
 	-->
    <xsl:template name="propertiesMessage">
    	<xsl:param name="category" />
    	<xsl:param name="type" />
    	
    	<xsl:if test="@type='ObjectUpdate'">
    		<!-- ID property handling for ObjectUpdate messages -->
    		<xsl:choose>    		
    			<xsl:when test="./property[@name='Id']">
    				<!-- Property 'Id' is declared, therefore getter will be present in all message types
-->
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="$category='shared' and $type='impl'">
						<!-- Create the field as we're using it elsewhere... -->
						private UnrealId Id = <xsl:value-of select="/messages/settings/javasettings/javapackageinfomessages/@package" />.<xsl:value-of select="@name"/>.<xsl:value-of select="@name"/>Id;
						
						public UnrealId getId() {
							return Id;
						}
					</xsl:if> 
					<xsl:if test="$type='abstract'">
						<!-- Auto-create one for a abstract message -->
						public static final UnrealId <xsl:value-of select="@name"/>Id = UnrealId.get("<xsl:value-of select="@name"/>Id");
					</xsl:if>
					<xsl:if test="not($category='shared' and $type='impl')">
						<!-- For every message category/type combination generate getter for an Id
-->
						public UnrealId getId() {						
							return <xsl:value-of select="/messages/settings/javasettings/javapackageinfomessages/@package" />.<xsl:value-of select="@name"/>.<xsl:value-of select="@name"/>Id;
						}
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
	   	</xsl:if>
	   	
	   	<!-- Property 'Time' Handling ... this property is declared automatically by XSLT
-->
	   	<xsl:if test="($type='abstract') or ($category='event')">
	   		<!-- Generate Time declaration for abstract types -->
			protected long SimTime;
				
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */	
			@Override
			public long getSimTime() {
				return SimTime;
			}
						
			/**
			 * Used by Yylex to slip correct time of the object or programmatically.
			 */
			protected void setSimTime(long SimTime) {
				this.SimTime = SimTime;
			}
	   	</xsl:if>
	   	<xsl:if test="($type='impl') and not($category='event')">
	   		<!-- Generate public setter of the Time property for impls -->
   			<xsl:if test="not($category='event')">
   				<!-- But only if it is not Event -->
   				@Override
   				public void setSimTime(long SimTime) {
					super.setSimTime(SimTime);
				}
   			</xsl:if>
	   	</xsl:if>
	   		
	   	<!-- OTHER PROPERTIES + GETTERS -->
	   			
    	<xsl:choose>
    		<xsl:when test="$category='base'">
    			<xsl:choose>
    				<xsl:when test="$type='abstract'">
    					<!-- Generate abstract getters for the property -->
		    			<xsl:for-each select="property">
		    				<!-- Abstract property getter !-->
		   					<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public abstract</xsl:with-param></xsl:call-template>;
		    			</xsl:for-each>
    				</xsl:when>
    				<xsl:when test="$type='message'">
    				
    					protected ITeamId TeamId;
    					
    					/**
    					 * Used by Yylex to slip corretn TeamId.
    					 */
    					protected void setTeamId(ITeamId TeamId) {
    					    this.TeamId = TeamId;
    					}
    				
    					public ITeamId getTeamId() {
							return TeamId;
						}
    	
    					<!-- Generate property declarations + getters. -->
    					<xsl:for-each select="property">
    						<xsl:call-template name="propertyDeclaration" />
    						
    						/**
		 					 * Whether property '<xsl:value-of select="@name"/>' was received from GB2004.
		 					 */
							protected boolean <xsl:value-of select="@name"/>_Set = false;
							
    						@Override
		    				<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public </xsl:with-param></xsl:call-template> {
		    					return <xsl:value-of select="@name" />;
		    				}
		    			</xsl:for-each>
		    			
		    			private <xsl:call-template name="classNameMessage">
							<xsl:with-param name="category">local</xsl:with-param>
							<xsl:with-param name="type">abstract</xsl:with-param>
						</xsl:call-template> localPart = null;
		    			
		    			@Override
						public <xsl:call-template name="classNameMessage">
									<xsl:with-param name="category">local</xsl:with-param>
									<xsl:with-param name="type">abstract</xsl:with-param>
								</xsl:call-template> 
						getLocal() {
							if (localPart != null) return localPart;
							return localPart = new 
								<xsl:call-template name="classNameMessage">
									<xsl:with-param name="category">local</xsl:with-param>
									<xsl:with-param name="type">message</xsl:with-param>
								</xsl:call-template>();
						}
					
						private <xsl:call-template name="classNameMessage">
							<xsl:with-param name="category">shared</xsl:with-param>
							<xsl:with-param name="type">abstract</xsl:with-param>
						</xsl:call-template> sharedPart = null;
					
						@Override
						public <xsl:call-template name="classNameMessage">
									<xsl:with-param name="category">shared</xsl:with-param>
									<xsl:with-param name="type">abstract</xsl:with-param>
								</xsl:call-template> 
						getShared() {
							if (sharedPart != null) return sharedPart;							
							return sharedPart = new 
								<xsl:call-template name="classNameMessage">
									<xsl:with-param name="category">shared</xsl:with-param>
									<xsl:with-param name="type">message</xsl:with-param>
								</xsl:call-template>();
						}
					
						private <xsl:call-template name="classNameMessage">
									<xsl:with-param name="category">static</xsl:with-param>
									<xsl:with-param name="type">abstract</xsl:with-param>
								</xsl:call-template> staticPart = null; 
					
						@Override
						public <xsl:call-template name="classNameMessage">
									<xsl:with-param name="category">static</xsl:with-param>
									<xsl:with-param name="type">abstract</xsl:with-param>
								</xsl:call-template> 
						getStatic() {
							if (staticPart != null) return staticPart;
							return staticPart = new 
								<xsl:call-template name="classNameMessage">
									<xsl:with-param name="category">static</xsl:with-param>
									<xsl:with-param name="type">message</xsl:with-param>
								</xsl:call-template>();
						}
    				</xsl:when>
    			</xsl:choose>    			
    		</xsl:when>
    		<xsl:when test="$category='event'">
    			<!-- Generate property declarations + getters. -->
  				<xsl:for-each select="property">
  					<xsl:call-template name="propertyDeclaration" />
  					<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public </xsl:with-param></xsl:call-template> {
    					return <xsl:value-of select="@name" />;
    				}
    			</xsl:for-each>
    		</xsl:when>
    		<xsl:when test="$category='composite'">
    			<!-- Composite ... generate static/local/shared parts. -->
    			protected 
    			<xsl:call-template name="classNameMessage">
    				<xsl:with-param name="category">static</xsl:with-param>
    				<xsl:with-param name="type">impl</xsl:with-param>
    			</xsl:call-template>
    			partStatic;
    			
    			@Override
				public <xsl:call-template name="classNameMessage">
    				<xsl:with-param name="category">static</xsl:with-param>
    				<xsl:with-param name="type">abstract</xsl:with-param>
    			</xsl:call-template> getStatic() {
					return partStatic;
				}
    			
    			protected
    			<xsl:call-template name="classNameMessage">
    				<xsl:with-param name="category">local</xsl:with-param>
    				<xsl:with-param name="type">impl</xsl:with-param>
    			</xsl:call-template>
    			partLocal;
    	
    			@Override
				public <xsl:call-template name="classNameMessage">
    				<xsl:with-param name="category">local</xsl:with-param>
    				<xsl:with-param name="type">abstract</xsl:with-param>
    			</xsl:call-template> getLocal() {
					return partLocal;
				}
			
    			<xsl:call-template name="classNameMessage">
    				<xsl:with-param name="category">shared</xsl:with-param>
    				<xsl:with-param name="type">impl</xsl:with-param>
    			</xsl:call-template>
    			partShared;
    			
				@Override
				public <xsl:call-template name="classNameMessage">
    				<xsl:with-param name="category">shared</xsl:with-param>
    				<xsl:with-param name="type">abstract</xsl:with-param>
    			</xsl:call-template> getShared() {
					return partShared;
				}
			
				<!-- Generate property declarations + getters. -->
  				<xsl:for-each select="property">
  					@Override
    				<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public </xsl:with-param></xsl:call-template> {
    					return 
    						<xsl:choose>
    							<xsl:when test="@category='static'">
    								partStatic.
    							</xsl:when>
    							<xsl:when test="@category='local'">
    								partLocal.
    							</xsl:when>
    							<xsl:when test="@category='shared'">
    								partShared.
    							</xsl:when>
    							<xsl:when test="@category='all'">
    								partStatic.
    							</xsl:when>
    						</xsl:choose>
    						<xsl:call-template name="getterCall">
    							<xsl:with-param name="name" select="@name" />
    							<xsl:with-param name="type" select="@type" />
    						</xsl:call-template>;
    				}
    			</xsl:for-each>
    		</xsl:when>
    		<xsl:otherwise>
    			<xsl:choose>
	    			<!-- category == static/local/shared -->
	    			<xsl:when test="$type='abstract'">
	    				<xsl:if test="$category='shared'">
	    					<!-- Generate identifiers of respective shared properties. -->
	    					<xsl:for-each select="property[@category='shared']">
	    						public static final Token <xsl:value-of select="@name" />PropertyToken = Tokens.get("<xsl:value-of select="@name" />");
	    					</xsl:for-each>	
							
							public static final Set&lt;Token&gt; SharedPropertyTokens;
	
							static {
								Set&lt;Token&gt; tokens = new HashSet&lt;Token&gt;();
								<xsl:for-each select="property[@category='shared']">
									tokens.add(<xsl:value-of select="@name" />PropertyToken);
								</xsl:for-each>
								SharedPropertyTokens = Collections.unmodifiableSet(tokens);
							}
	    				</xsl:if>
	    			
	    				@Override
		    			public abstract 
		    			<xsl:call-template name="classNameMessage">
		    				<xsl:with-param name="category" select="$category" />
		    				<xsl:with-param name="type" select="$type" />
		    			</xsl:call-template> clone();
		    			
						@Override
						public Class getCompositeClass() {
							return <xsl:value-of select="@name" />.class;
						}
	
						<!-- Generate abstract getters for the property -->
		    			<xsl:for-each select="property[(@category='all') or (@category=$category)]">
		   					<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public abstract</xsl:with-param></xsl:call-template>;
		    			</xsl:for-each>
	    			</xsl:when>
	    			<xsl:when test="$type='impl'">
	    				@Override
	    				public 
	    				<xsl:call-template name="classNameMessage">
	    					<xsl:with-param name="category" select="$category" />
	    					<xsl:with-param name="type" select="$type" />
	    				</xsl:call-template> clone() {
	    					return new 
	    					<xsl:call-template name="classNameMessage">
	    						<xsl:with-param name="category" select="$category" />
	    						<xsl:with-param name="type" select="$type" />
	    					</xsl:call-template>(this);
	    				}
	    				
	    				<xsl:choose>
	    					<xsl:when test="$category='static' or $category='local'">
	    						<!-- Generate property declarations + getters. -->
				  				<xsl:for-each select="property[@category='all' or @category=$category]">	  					
				  					<xsl:call-template name="propertyDeclaration" />
				  					<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public </xsl:with-param></xsl:call-template> {
				    					return <xsl:value-of select="@name" />;
				    				}
				    			</xsl:for-each>
	    					</xsl:when>
	    					<xsl:when test="$category='shared'">
	    						<xsl:call-template name="propertiesShared">
	    							<xsl:with-param name="type" select="$type" />
	    						</xsl:call-template>
	    					</xsl:when>
	    				</xsl:choose>
		    		</xsl:when>
		    		<xsl:when test="$type='message'">
		    			@Override
		    			public 
		    			<xsl:call-template name="classNameMessage">
		    				<xsl:with-param name="category" select="$category" />
		    				<xsl:with-param name="type" select="$type" />
		    			</xsl:call-template> clone() {
		    				return this;
		    			}
		    			<xsl:if test="$category='local'">
		    				public <xsl:call-template name="classNameMessage">
	    						<xsl:with-param name="category">local</xsl:with-param>
	    						<xsl:with-param name="type">message</xsl:with-param>
	    					</xsl:call-template> getLocal() {
								return this;
					    	}
							public ISharedWorldObject getShared() {
							 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
							}
							public IStaticWorldObject getStatic() {
							    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
							}
		    			</xsl:if>
		    			<xsl:choose>		    			
		    				<xsl:when test="$category='static' or $category='local'">
		    					<xsl:for-each select="property[@category='all' or @category=$category]">
				  					<xsl:call-template name="getterDeclarationMessage"><xsl:with-param name="javaVisibility">public </xsl:with-param></xsl:call-template> {
				    					return <xsl:value-of select="@name" />;
				    				}
				    			</xsl:for-each>
		    				</xsl:when>
		    				<xsl:when test="$category='shared'">
		    					<xsl:call-template name="propertiesShared">
	    							<xsl:with-param name="type" select="$type" />
	    						</xsl:call-template>
		    				</xsl:when>
		    			</xsl:choose>
		    		</xsl:when>
		    		<xsl:otherwise>
		    			UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-FOR-PROPERTIES-MESSAGE
		    		</xsl:otherwise>
		    	</xsl:choose>	    		
    		</xsl:otherwise>
    	</xsl:choose>
    </xsl:template>
    
     <!-- 
 		paramsCommand()
    		Outputs declarations of all params (+ their getters/setters) of the command.
 			CONTEXT: commandobject
 	-->
    <xsl:template name="paramsCommand">    	
    	<xsl:for-each select="property">
	        /**
	        <xsl:apply-templates select="documentation"/> 
	        */
	        protected
	        <xsl:text> </xsl:text>
	        <xsl:call-template name="typeCommand" />        
	        <xsl:text> </xsl:text>
	        <xsl:value-of select="@name"/> =
	       	<xsl:choose>
	       		<xsl:when test="@default">
	       			<xsl:value-of select="@default"/>
	       		</xsl:when>
	       		<xsl:otherwise>
	        		null
	        	</xsl:otherwise>
			</xsl:choose>;
	
	        <!-- Getter !-->
	        <xsl:call-template name="getterDeclarationCommand"><xsl:with-param name="javaVisibility">public</xsl:with-param></xsl:call-template>
	        {
	            return
	        	<xsl:text> </xsl:text>
	        	<xsl:value-of select="@name"/>;
	        }
	        
	        <!-- Setter !-->
	        <xsl:call-template name="setterDeclarationCommand"><xsl:with-param name="javaVisibility">public</xsl:with-param></xsl:call-template>
			{
				this.<xsl:value-of select="@name"/> = <xsl:value-of select="@name"/>;
				return this;
			}
		</xsl:for-each>
    </xsl:template>
    
    <!-- 
    
        ===========
    	UPDATE PART
    	===========
    
     -->
     
    <!-- 
    	updateClassNamePart($category)
 			Generates inner-class name of the update class for a specified part of the message
 			@param category
 				possible values: base, local, shared, static
 			CONTEXT: messageobject
 	-->
    <xsl:template name="updateClassNamePart">
    	<!-- TEMPLATE PARAMS -->
    	<xsl:param name="category" />
    	
    	<!-- TEMPLATE BODY -->
    	<xsl:value-of select="@name" /><xsl:call-template name="uppercaseFirst">
    		<xsl:with-param name="str" select="$category" />
    	</xsl:call-template>Update
    </xsl:template>
    
    <!-- 
    	updateClassNameAbstract()
 			Generates inner-class name of the update class for a Base/Abstract.
 			CONTEXT: messageobject
 	-->
    <xsl:template name="updateClassNameAbstract">
    	<!-- TEMPLATE BODY -->
    	<xsl:value-of select="@name" />Update
    </xsl:template>
    
    <!--
    	updateProperty($prefix)

    		SUBROUTINE FOR THE updateMesage...($category, $type)

    		Updates value of one property... implementation of this template heavily relies on the actual implementation of the updateMesage($category, $type) template.
    		
    		@param prefix
    			prefix for "this"

    		CONTEXT: messageobject/property	
     -->
     <xsl:template name="updateProperty">
     	<!-- TEMPLATE PARAMS -->
     	<xsl:param name="prefix" />
     	
     	<!-- TEMPLATE BODY -->
     
     	<xsl:if test="@nullable">
     		if (<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> != null) {
     	</xsl:if>
	     <xsl:choose>
			<xsl:when test="@type = 'Boolean'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'boolean'">
				if (toUpdate.<xsl:value-of select="@name"/> != <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) {
				    toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Integer'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'int'">
				if (toUpdate.<xsl:value-of select="@name"/> != <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) {
				    toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Long'">
				if (toUpdate.<xsl:value-of select="@name"/> != <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) {
				    toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'long'">
				if (toUpdate.<xsl:value-of select="@name"/> != <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) {
				    toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Float'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'float'">
				if (toUpdate.<xsl:value-of select="@name"/> != <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) {
				    toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Double'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'double'">
				if (toUpdate.<xsl:value-of select="@name"/> != <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) {
				    toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Point3d'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Vector3d'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Location'">
	            if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Velocity'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Rotation'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Color'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Point2D'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'Dimension2D'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'UnrealId'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:when test="@type = 'String'">
				if (!SafeEquals.equals(toUpdate.<xsl:value-of select="@name"/>, <xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)) {
					toUpdate.<xsl:value-of select="@name"/>=<xsl:value-of select="$prefix" /><xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>;
					updated = true;
				}
			</xsl:when>
			<xsl:otherwise>UNSUPPORTED TYPE '<xsl:value-of select="@type"/>'</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="@nullable">
     		}
     	</xsl:if>
    </xsl:template>
    
    <!--
 		updateMessageBaseAbstract()
 			Outputs update methods / classes for 'ObjectUpdate' type of the message of Base/Abstract category/part.
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="updateMessageBaseAbstract">
 		<!-- TEMPLATE VARIABLE -->
 		<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">base</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameMessage"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">base</xsl:with-param>
    		<xsl:with-param name="type">message</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameLocalAbstract"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">local</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameSharedAbstract"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">shared</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameStaticAbstract"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">static</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameLocalImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">local</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameSharedImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">shared</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameStaticImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">static</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="updateClassName"><xsl:call-template name="updateClassNameAbstract" /></xsl:variable>
    	<xsl:variable name="updateClassNameLocal"><xsl:call-template name="updateClassNamePart">
    		<xsl:with-param name="category">local</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="updateClassNameShared"><xsl:call-template name="updateClassNamePart">
    		<xsl:with-param name="category">shared</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="updateClassNameStatic"><xsl:call-template name="updateClassNamePart">
    		<xsl:with-param name="category">static</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->
    	
    	<xsl:if test="./property[@name='Visible']">
		 	@Override
			public IWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements IWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(<xsl:value-of select="$className" /> obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private <xsl:value-of select="$className" /> obj;
				private long time;
		
				@Override
				public WorldObjectId getId() {
					return obj.getId();
				}
		
		        /**
		         * Simulation time in MILLI SECONDS !!!
		         */
				@Override
				public long getSimTime() {
					return time;
				}
		
				@Override
				public IWorldObjectUpdateResult&lt;IWorldObject&gt; update(IWorldObject obj) {
					if (obj == null) {
						throw new PogamutException("Can't 'disappear' null!", this);
					}
					if (!(obj instanceof <xsl:value-of select="$classNameMessage" />)) {
						throw new PogamutException("Can't update different class than <xsl:value-of select="$classNameMessage" />, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					<xsl:value-of select="$classNameMessage" /> toUpdate = (<xsl:value-of select="$classNameMessage" />)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}
				}
		
			}
	
		</xsl:if>
    	
    	public static class <xsl:value-of select="$updateClassName" /> extends GBObjectUpdate implements ICompositeWorldObjectUpdatedEvent, IGBWorldObjectEvent {	
			private <xsl:value-of select="$className" /> object;
			private long time;
			private ITeamId teamId;
			
			public <xsl:value-of select="$updateClassName" />(<xsl:value-of select="$className" /> source, long eventTime, ITeamId teamId) {
				this.object = source;
				this.time = eventTime;
				this.teamId = teamId;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */ 
			@Override
			public long getSimTime() {
				return time;
			}
	
			@Override
			public IWorldObject getObject() {
				return object;
			}
	
			@Override
			public WorldObjectId getId() {
				return object.getId();
			}
	
			@Override
			public ILocalWorldObjectUpdatedEvent getLocalEvent() {
				return new <xsl:value-of select="$classNameLocalImpl" />.<xsl:value-of select="$updateClassNameLocal" />((<xsl:value-of select="$classNameLocalAbstract" />)object.getLocal(), time);
			}
	
			@Override
			public ISharedWorldObjectUpdatedEvent getSharedEvent() {
				return new <xsl:value-of select="$classNameSharedImpl" />.<xsl:value-of select="$updateClassNameShared" />((<xsl:value-of select="$classNameSharedAbstract" />)object.getShared(), time, teamId);
			}
	
			@Override
			public IStaticWorldObjectUpdatedEvent getStaticEvent() {
				return new <xsl:value-of select="$classNameStaticImpl" />.<xsl:value-of select="$updateClassNameStatic" />((<xsl:value-of select="$classNameStaticAbstract" />)object.getStatic(), time);
			}
			
		}
    </xsl:template>
    
    <!--
 		updateMessageBaseMessage()
 			Outputs update methods / classes for 'ObjectUpdate' type of the message of Base/Message category/part.
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="updateMessageBaseMessage">
 		<!-- TEMPLATE VARIABLE -->
 		<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">base</xsl:with-param>
    		<xsl:with-param name="type">message</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameLocalImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">local</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameSharedImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">shared</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameStaticImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">static</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="updateClassNameLocal"><xsl:call-template name="updateClassNamePart">
    		<xsl:with-param name="category">local</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="updateClassNameShared"><xsl:call-template name="updateClassNamePart">
    		<xsl:with-param name="category">shared</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="updateClassNameStatic"><xsl:call-template name="updateClassNamePart">
    		<xsl:with-param name="category">static</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->
 	
		@Override
		public IWorldObjectUpdateResult&lt;IWorldObject&gt; update(IWorldObject object) {
			if (object == null)
			{
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.CREATED, this);
			}
			if (!( object instanceof <xsl:value-of select="$className" />) ) {
				throw new PogamutException("Can't update different class than <xsl:value-of select="$className" />, got class " + object.getClass().getSimpleName() + "!", this);		
			}
			<xsl:value-of select="$className" /> toUpdate = (<xsl:value-of select="$className" />)object;
			
			boolean updated = false;
			
			// UPDATING LOCAL PROPERTIES
			<xsl:for-each select="property[@category='local']">
         		<xsl:call-template name="updateProperty"><xsl:with-param name="prefix"></xsl:with-param></xsl:call-template>
         	</xsl:for-each>
         	
         	// UPDATING SHARED PROPERTIES
         	<xsl:for-each select="property[@category='shared']">
         		<xsl:call-template name="updateProperty"><xsl:with-param name="prefix"></xsl:with-param></xsl:call-template>
         	</xsl:for-each>
         	
         	// UPDATE TIME
         	toUpdate.SimTime = SimTime;
			
			if (updated) {
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult&lt;IWorldObject&gt;(IWorldObjectUpdateResult.Result.UPDATED, toUpdate);
			} else {
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult&lt;IWorldObject&gt;(IWorldObjectUpdateResult.Result.SAME, toUpdate);
			}
		}
		
		@Override
		public ILocalWorldObjectUpdatedEvent getLocalEvent() {
			return new <xsl:value-of select="$classNameLocalImpl" />.<xsl:value-of select="$updateClassNameLocal" />(this.getLocal(), SimTime);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new <xsl:value-of select="$classNameSharedImpl" />.<xsl:value-of select="$updateClassNameShared" />(this.getShared(), SimTime, this.getTeamId());
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new <xsl:value-of select="$classNameStaticImpl" />.<xsl:value-of select="$updateClassNameStatic" />(this.getStatic(), SimTime);
		}
 	</xsl:template>
 	
 	<!--
 		updateMessageComposite()
 			Outputs update methods / classes for 'ObjectUpdate' type of the message of Composite category.
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="updateMessageComposite">
 		<!-- NO NEED TO DO ANYTHING... -->
 	</xsl:template>
 	
 	<!--
 		updateMessageLocalAbstract()
 			Outputs update methods / classes for 'ObjectUpdate' type of the message of Local/Impl category/part.
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="updateMessageLocalAbstract">
 		<!-- TEMPLATE VARIABLE -->
 		<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">local</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameLocalImpl"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">local</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->
    	
    	public <xsl:value-of select="$className" /> getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL");
		}
 	
 		<xsl:if test="./property[@name='Visible']">
		 	@Override
			public ILocalWorldObjectUpdatedEvent createDisappearEvent() {
				return new ObjectDisappeared(this, getSimTime());
			}
			
			public static class ObjectDisappeared implements ILocalWorldObjectUpdatedEvent
			{
				
				public ObjectDisappeared(<xsl:value-of select="$className" /> obj, long time) {
					this.obj = obj;
					this.time = time;
				}
				
				private <xsl:value-of select="$className" /> obj;
				private long time;
		
				@Override
				public WorldObjectId getId() {
					return obj.getId();
				}
		
		        /**
		         * Simulation time in MILLI SECONDS !!!
		         */
				@Override
				public long getSimTime() {
					return time;
				}
		
				@Override
				public IWorldObjectUpdateResult&lt;ILocalWorldObject&gt; update(ILocalWorldObject obj) 
				{
					if (obj == null) {
						throw new PogamutException("Can't 'disappear' null!", this);
					}
					if (!(obj instanceof <xsl:value-of select="$classNameLocalImpl" />)) {
						throw new PogamutException("Can't update different class than <xsl:value-of select="$classNameLocalImpl" />, got class " + obj.getClass().getSimpleName() + "!", this);
					}
					<xsl:value-of select="$classNameLocalImpl" /> toUpdate = (<xsl:value-of select="$classNameLocalImpl" />)obj;
					if (toUpdate.Visible) {
						toUpdate.Visible = false;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.UPDATED, obj);
					} else {
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult(IWorldObjectUpdateResult.Result.SAME, obj);
					}	
				}
				
			}
	
		</xsl:if>
	
 	</xsl:template>
 	
 	<!--
 		updateMessageLocalImpl()
 			Outputs update methods / classes for 'ObjectUpdate' type of the message of Local/Impl category/part.
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="updateMessageLocalImpl">
 		<!-- TEMPLATE VARIABLE -->
 		<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">local</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="updateClassName"><xsl:call-template name="updateClassNamePart">
    		<xsl:with-param name="category">local</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<xsl:variable name="classNameLocalAbstract"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">local</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	
    	<!-- TEMPLATE BODY -->
    	
    	public <xsl:value-of select="$className" /> getLocal() {
			return this;
    	}
		public ISharedWorldObject getShared() {
		 	throw new UnsupportedOperationException("Could not return LOCAL as SHARED");
		}
		public IStaticWorldObject getStatic() {
		    throw new UnsupportedOperationException("Could not return LOCAL as STATIC");
		}
 	
		public static class <xsl:value-of select="$updateClassName" /> implements ILocalWorldObjectUpdatedEvent, IGBWorldObjectEvent
		{
			protected long time;
			
			protected <xsl:value-of select="$classNameLocalAbstract" /> data = null; //contains object data for this update
			
			public <xsl:value-of select="$updateClassName" />(<xsl:value-of select="$classNameLocalAbstract" /> moverLocal, long time)
			{
				this.data = moverLocal;
				this.time = time;
			}
			
			@Override
			public IWorldObjectUpdateResult&lt;ILocalWorldObject&gt; update(
					ILocalWorldObject object) 
			{
				if ( object == null)
				{
					data = new <xsl:value-of select="$className" />(data); //we always return Impl object
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult&lt;ILocalWorldObject&gt;(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				if ( object instanceof <xsl:value-of select="$className" /> )
				{
					<xsl:value-of select="$className" /> toUpdate = (<xsl:value-of select="$className" />)object;
					
					boolean updated = false;
					
					// UPDATING LOCAL PROPERTIES
					<xsl:for-each select="property[@category='local']">
		         		<xsl:call-template name="updateProperty"><xsl:with-param name="prefix">data.</xsl:with-param></xsl:call-template>
		         	</xsl:for-each>
					
					data = toUpdate; //the updating has finished
					
					if ( updated )
					{
						toUpdate.SimTime = this.time;
						return new IWorldObjectUpdateResult.WorldObjectUpdateResult&lt;ILocalWorldObject&gt;(IWorldObjectUpdateResult.Result.UPDATED, data);
					}
					
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult&lt;ILocalWorldObject&gt;(IWorldObjectUpdateResult.Result.SAME, data);
				}
				throw new PogamutException("Unsupported object type for update. Expected <xsl:value-of select="$className" /> for object " + object.getId() +", not object of class " + object.getClass().getSimpleName() + ".", this);
			}
	
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return this.time;
			}
	
			@Override
			public IWorldObject getObject() {
				return data;
			}
	
			@Override
			public WorldObjectId getId() {
				return data.getId();
			}
			
		}	
 	</xsl:template>
    
    <!--
 		updateMessageSharedImpl()
 			Outputs update methods / classes for 'ObjectUpdate' type of the message of Shared/Impl category/part.
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="updateMessageSharedImpl">
 		<!-- TEMPLATE VARIABLE -->
 		<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">shared</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="updateClassName"><xsl:call-template name="updateClassNamePart">
    		<xsl:with-param name="category">shared</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<xsl:variable name="classNameSharedAbstract"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">shared</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<xsl:variable name="classNameComposite"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">composite</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->
    	public static class <xsl:value-of select="$updateClassName" /> implements ISharedWorldObjectUpdatedEvent
		{
	
			private <xsl:value-of select="$classNameSharedAbstract" /> object;
			private long time;
			private ITeamId teamId;
			
			public <xsl:value-of select="$updateClassName" />(<xsl:value-of select="$classNameSharedAbstract" /> data, long time, ITeamId teamId)
			{
				this.object = data;
				this.time = time;
				this.teamId = teamId;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return this.time;
			}
	
			@Override
			public WorldObjectId getId() {
				return object.getId();
			}
	
			@Override
			public ITeamId getTeamId() {
				return teamId;
			}
			
			@Override
			public Class getCompositeObjectClass()
			{
				return object.getCompositeClass();
			}
	
			@Override
			public Collection&lt;ISharedPropertyUpdatedEvent&gt; getPropertyEvents() {
				LinkedList&lt;ISharedPropertyUpdatedEvent&gt; events = new LinkedList&lt;ISharedPropertyUpdatedEvent&gt;();
				
				for ( ISharedProperty property : object.getProperties().values() )
				{
					if ( property != null)
					{
						events.push( property.createUpdateEvent(time, teamId) );
					}
				}
				return events;
			}
			
		}
	
    </xsl:template>
    
    <!--
 		updateMessageStaticImpl()
 			Outputs update methods / classes for 'ObjectUpdate' type of the message of Static/Impl category/part.
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="updateMessageStaticImpl">
 		<!-- TEMPLATE VARIABLE -->
 		<xsl:variable name="className"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">static</xsl:with-param>
    		<xsl:with-param name="type">impl</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="updateClassName"><xsl:call-template name="updateClassNamePart">
    		<xsl:with-param name="category">static</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	<xsl:variable name="classNameStaticAbstract"><xsl:call-template name="classNameMessage">
    		<xsl:with-param name="category">static</xsl:with-param>
    		<xsl:with-param name="type">abstract</xsl:with-param>
    	</xsl:call-template></xsl:variable>
    	
    	<!-- TEMPLATE BODY -->
    	public static class <xsl:value-of select="$updateClassName" /> implements IStaticWorldObjectUpdatedEvent
		{
			
			private <xsl:value-of select="$classNameStaticAbstract" /> data;
			private long time;
			
			public <xsl:value-of select="$updateClassName" />(<xsl:value-of select="$classNameStaticAbstract" /> source, long time)
			{
				this.data = source;
				this.time = time;
			}
			
			/**
			 * Simulation time in MILLI SECONDS !!!
			 */
			@Override
			public long getSimTime() {
				return time;
			}
	
			@Override
			public WorldObjectId getId() {
				return data.getId();
			}
			
			@Override
			public IWorldObjectUpdateResult&lt;IStaticWorldObject&gt; update(
					IStaticWorldObject object) {
				if ( object == null)
				{
					data = new <xsl:value-of select="$className" />(data);
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult&lt;IStaticWorldObject&gt;(IWorldObjectUpdateResult.Result.CREATED, data);
				}
				//since static objects can't be updated, we'll just check that the object stays the same
				if ( object instanceof <xsl:value-of select="$className" />)
				{
					<xsl:value-of select="$className" /> orig = (<xsl:value-of select="$className" />)object;
					//since these errors usually mean error in gamebots, we will just print an error message
					if ( data.isDifferentFrom(orig) )
					{
						//data.isDifferentFrom(orig);
						//throw new PogamutException("Trying to modify static object " + this.data.getId().toString() , this);
						System.out.println("!!!!!ERROR!!!!!! in static object modification. Object class : <xsl:value-of select="$className" /> to see which property was different see !!!!PROPERTY UPDATE ERROR!!!!");
					}
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult&lt;IStaticWorldObject&gt;(IWorldObjectUpdateResult.Result.SAME, data);
				}				
				throw new PogamutException("Unexpected object type for update, <xsl:value-of select="$className" /> expected not class " + object.getClass().getSimpleName() + ".", this);
			}
		}
	
    </xsl:template>
    
 	<!--
 		toStringMessage($category, $type)
 			Outputs toString() method.
 			@param category
 				possible values: base, composite, local, shared, static
 			@param type
 				possible values: abstract, message, impl (not relevant for type == composite) 		
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="toStringMessage">
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		
 	    public String toString() {
            return
            	super.toString() + "[" +
            	<xsl:choose>
            		<xsl:when test="($category='base')  or ($category='event')">
            			<xsl:for-each select="property">
		              		<xsl:if test="not(@jflex)">
		              			"<xsl:value-of select="@name"/> = " + String.valueOf(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) + " | " + 
		              		</xsl:if>
						</xsl:for-each>
            		</xsl:when>
            		<xsl:when test="$category='composite'">
            			"Static = " + String.valueOf(partStatic) + " | Local = " + String.valueOf(partLocal) + " | Shared = " + String.valueOf(partShared) + " ]" +
            		</xsl:when>
            		<xsl:otherwise>
            			<xsl:for-each select="property[(@category='all') or (@category=$category)]">
		              		<xsl:if test="not(@jflex)">
		              			"<xsl:value-of select="@name"/> = " + String.valueOf(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) + " | " + 
		              		</xsl:if>
						</xsl:for-each>
            		</xsl:otherwise>
            	</xsl:choose>
				"]";           		
        }
 	</xsl:template>
 	
 	
 	<!-- 
 		comparation operator($name1, $name2)
 		prints a correct comparation operator
 		either $name1 == $name2 or AdvancedEquals.equalsOrNull($name1,$name2)
 		CONTEXT: property
 	 -->
 	 <xsl:template name="propertiesCompare">
 	 	<xsl:param name="name1" />
 	 	<xsl:param name="name2" />
 	 	
 	 	<xsl:variable name="mapType">
 	 		<xsl:text>Map&lt;UnrealId, NavPointNeighbourLink&gt;</xsl:text>
 	 	</xsl:variable>
 	 	
 	 	<xsl:variable name="propertyType">
 	 		<xsl:choose>
 	 			<xsl:when test="@type='boolean'">PRIMITIVE</xsl:when>
 	 			<xsl:when test="@type='int'">PRIMITIVE</xsl:when>
 	 			<xsl:when test="@type='float'">PRIMITIVE</xsl:when>
 	 			<xsl:when test="@type='double'">PRIMITIVE</xsl:when>
 	 			<xsl:when test="@type='long'">PRIMITIVE</xsl:when>
 	 			<xsl:when test="@type=$mapType">MAP</xsl:when>
 	 			<xsl:otherwise>OBJECT</xsl:otherwise>
 	 		</xsl:choose>
 	 	</xsl:variable>
 	 	
 	 	
 	 	<xsl:choose>
 	 		<xsl:when test="$propertyType='PRIMITIVE'">
 	 			<xsl:value-of select="$name1" />.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type" /></xsl:call-template>
 	 			== <xsl:value-of select="$name2" />.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type" /></xsl:call-template>
 	 		</xsl:when>
 	 		<xsl:when test="$propertyType='MAP'">
 	 			<xsl:value-of select="$name1" />.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type" /></xsl:call-template>.keySet().containsAll(
 	 			<xsl:value-of select="$name2" />.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type" /></xsl:call-template>.keySet() ) &amp;&amp;
 	 			<xsl:value-of select="$name2" />.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type" /></xsl:call-template>.keySet().containsAll(
 	 			<xsl:value-of select="$name1" />.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type" /></xsl:call-template>.keySet() ) 
 	 		</xsl:when>
 	 		<xsl:otherwise>
 	 			AdvancedEquals.equalsOrNull(<xsl:value-of select="$name1" />.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type" /></xsl:call-template>, <xsl:value-of select="$name2" />.<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type" /></xsl:call-template>)
 	 		</xsl:otherwise>
 	 	</xsl:choose>
 	 </xsl:template>
 	
 	
 	<!-- isDifferentFrom method output
 		isDifferentFrom template ($type) .. category is presumed static
 	 -->
 	 
 	 <xsl:template name="isDifferentFrom">
 	 	<xsl:param name="type" />
 	 	<xsl:variable name="className">
 			<xsl:call-template name="classNameMessage">
 			<xsl:with-param name="type">abstract</xsl:with-param>
 			<xsl:with-param name="category">static</xsl:with-param>
 			</xsl:call-template>
 		</xsl:variable>
 		
 		@Override
 		public boolean isDifferentFrom(IStaticWorldObject other)
 		{
 			if (other == null) //early fail
 			{
 				return true;
 			}
 			else if (other == this) //early out
 			{
 				return false;
 			}
 			else
 			{
 				<xsl:value-of select="$className" /> obj = (<xsl:value-of select="$className" />) other;

 				<xsl:for-each select="property[(@category='all') or (@category='static')]">
 				<xsl:choose>
 					<xsl:when test="@name='OutgoingEdges' or @name='IncomingEdges'">
 						//Skipping outgoing and incoming edges tests because the navGraph is sent only once
 					</xsl:when>
 					<xsl:otherwise>
 						if ( !(<xsl:call-template name="propertiesCompare"><xsl:with-param name="name1">this</xsl:with-param><xsl:with-param name="name2">obj</xsl:with-param></xsl:call-template>) )
						{
							System.out.println("!!!!!PROPERTY UPDATE ERROR!!!! on property <xsl:value-of select="@name" /> on object class <xsl:value-of select="$className"/>");
							return true;
						}
 					</xsl:otherwise>
 				</xsl:choose>				
				</xsl:for-each>
 			}
 			return false;
 		}
 	 </xsl:template>
 	
 	<!--
 		not used atm
 		equalsMessage($category, $type)
 			Outputs equals(Object other) method.
 			@param category
 				possible values: base, composite, local, shared, static
 			@param type
 				possible values: abstract, message, impl (not relevant for type == composite) 		
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="equalsMessage">
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		<xsl:variable name="className">
 			<xsl:call-template name="classNameMessage">
 				<xsl:with-param name="category"><xsl:value-of select="$category" /></xsl:with-param>
 				<xsl:with-param name="type">abstract</xsl:with-param>
 			</xsl:call-template>
 		</xsl:variable>
 		
 		@Override
 		public boolean equals( Object other )
 		{
 			if (!(other instanceof <xsl:value-of select="$className"/>))
 			{
 				return false;
 			}
 			else
 			{
 				/* debug :category : <xsl:value-of select="$category"></xsl:value-of> */
 				<xsl:value-of select="$className" /> obj = (<xsl:value-of select="$className" />) other;
 				<xsl:choose>
 				<xsl:when test="$category='event'">
 					/* event category */
 				</xsl:when>
 				<xsl:when test="$category='composite' or $category='base'">
 					/* composite category */
 					<xsl:for-each select="property">
 						/* <xsl:value-of select="@name"></xsl:value-of> */
 						if ( !(<xsl:call-template name="propertiesCompare"><xsl:with-param name="name1">this</xsl:with-param><xsl:with-param name="name2">obj</xsl:with-param></xsl:call-template>) )
 						{
 							return false;
 						}
 					</xsl:for-each>
 				</xsl:when>
 				<xsl:when test="$category='local' or $category='shared' or $category='static'">
	 				<xsl:for-each select="property[(@category='all') or (@category=$category)]">
 						if ( !(<xsl:call-template name="propertiesCompare"><xsl:with-param name="name1">this</xsl:with-param><xsl:with-param name="name2">obj</xsl:with-param></xsl:call-template>) )
	 					{
	 						return false;
	 					}
	 				</xsl:for-each>
	 			</xsl:when>
	 			<xsl:otherwise>
	 				 category <xsl:value-of select="$category"/> 
	 			</xsl:otherwise>
	 			</xsl:choose>
 				return true;
 			}
 		}
 	</xsl:template>
 	
 	<!--
 			Outputs toString() method. 		
 			CONTEXT: commandobject 
 	 -->
 	<xsl:template name="toStringCommand">
 	    public String toString() {
            return toMessage();
        }
 	</xsl:template>
 	
 	<!--
 			Outputs toJsonLiteral() method. 		
 			CONTEXT: messageobject/abstract 
 	 -->
 	<xsl:template name="toJsonLiteral">
 	    public String toJsonLiteral() {
            return "<xsl:value-of select="lower-case(@name)"/>( "
            		<xsl:for-each select="property">
		              	<xsl:if test="not(@jflex)">
		              		<xsl:if test="position() = 1">+</xsl:if>
		              		<xsl:if test="not(position() = 1)">+ ", " + </xsl:if>
		              		<xsl:choose>
								<xsl:when test="@type = 'Point3d'">
									(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"[" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getX() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getY() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getZ() + "]" 
									)
								</xsl:when>
								<xsl:when test="@type = 'Vector3d'">
									(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"[" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getX() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getY() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getZ() + "]" 
									)
								</xsl:when>
								<xsl:when test="@type = 'Location'">
								    (<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"[" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getX() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getY() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getZ() + "]" 
									)
								</xsl:when>
								<xsl:when test="@type = 'Velocity'">
								    (<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"[" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getX() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getY() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getZ() + "]" 
									)
								</xsl:when>
								<xsl:when test="@type = 'Rotation'">
									(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"[" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getPitch() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getYaw() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getRoll() + "]" 
									)								    
								</xsl:when>
								<xsl:when test="@type = 'Color'">
								    (<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"[" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getRed() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getGreen() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getBlue() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getAlpha() + "]" 
									)
								</xsl:when>
								<xsl:when test="@type = 'Point2D'">
								    (<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"[" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getX() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getY() + "]" 
									)
								</xsl:when>
								<xsl:when test="@type = 'Dimension2D'">
								    (<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"[" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getWidth() + ", " + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getHeight() + "]" 
									)
								</xsl:when>
								<xsl:when test="@type = 'UnrealId'">
									(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"\"" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getStringId() + "\"" 
									)
								</xsl:when>
								<xsl:when test="@type = 'ItemType'">
									(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"\"" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>.getName() + "\"" 
									)
								</xsl:when>
								<xsl:when test="@type = 'String'">
									(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> == null ? "null" :
										"\"" + <xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template> + "\"" 
									)
								</xsl:when>
								<xsl:otherwise>
								    String.valueOf(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>)									
								</xsl:otherwise>
							</xsl:choose>
		              	</xsl:if>
		            </xsl:for-each>
                   + ")";
        }
 	</xsl:template>
 	
 	<!--
 		toHtmlStringMessage($category, $type)
 			Outputs message as HTML snippet.		
 			@param category
 				possible values: base, composite, event, local, static, shared
 			@param type
 				possible values: abstract, impl, message
 			CONTEXT: messageobject 
 	 -->
 	<xsl:template name="toHtmlStringMessage">
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		
 		public String toHtmlString() {
 			return super.toString() + "[<![CDATA[<br/>]]>" +
            	<xsl:choose>
            		<xsl:when test="($category='base') or ($category='event')">
            			<xsl:for-each select="property">
		              		<xsl:if test="not(@jflex)">
		              			"<![CDATA[<b>]]><xsl:value-of select="@name"/><![CDATA[</b>]]> = " + String.valueOf(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) + " <![CDATA[<br/>]]> " + 
		              		</xsl:if>
						</xsl:for-each>
            		</xsl:when>
            		<xsl:when test="$category='composite'">
            			"Static = " + String.valueOf(partStatic) + " <![CDATA[<br/>]]> Local = " + String.valueOf(partLocal) + " <![CDATA[<br/>]]> Shared = " + String.valueOf(partShared) + " ]" +
            		</xsl:when>
            		<xsl:otherwise>
            			<xsl:for-each select="property[(@category='all') or (@category=$category)]">
		              		<xsl:if test="not(@jflex)">
		              			"<![CDATA[<b>]]><xsl:value-of select="@name"/><![CDATA[</b>]]> = " + String.valueOf(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) + " <![CDATA[<br/>]]> " + 
		              		</xsl:if>
						</xsl:for-each>
            		</xsl:otherwise>
            	</xsl:choose>
				"<![CDATA[<br/>]]>]";     
		}
 	</xsl:template>
 	
 	<!--
 		toHtmlStringCommand()
 			Outputs command as HTML snippet.		
 			CONTEXT: commandobject 
 	 -->
 	<xsl:template name="toHtmlStringCommand">
 		public String toHtmlString() {
			return super.toString() + "[<![CDATA[<br/>]]>" +
            	<xsl:for-each select="property">
            	"<![CDATA[<b>]]><xsl:value-of select="@name"/><![CDATA[</b>]]> = " +
            	String.valueOf(<xsl:call-template name="getterCall"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template>) +
            	" <![CDATA[<br/>]]> " +
            	</xsl:for-each> 
            	"<![CDATA[<br/>]]>]"
            ;
		}
 	</xsl:template>
 	
 	<!--
 		toMessageCommand()
 			Outputs command's toMessage() method that serializes the command to the GameBots2004 string.
 			CONTEXT: commandobject 
 	 -->
 	<xsl:template name="toMessageCommand">
		public String toMessage() {
     		StringBuffer buf = new StringBuffer();
     		buf.append("<xsl:value-of select="@command"/>");
     		<xsl:for-each select="property">
	      		<xsl:choose>
					<xsl:when test="@type = 'Point3d'">
						if (<xsl:value-of select="@name"/> != null) {
							buf.append(" {<xsl:value-of select="@name"/> " +
								<xsl:value-of select="@name"/>.getX() + "," +
								<xsl:value-of select="@name"/>.getY() + "," +
								<xsl:value-of select="@name"/>.getZ() + "}");
						}
					</xsl:when>
					<xsl:when test="@type = 'Vector3d'">
						if (<xsl:value-of select="@name"/> != null) {
							buf.append(" {<xsl:value-of select="@name"/> " +
								<xsl:value-of select="@name"/>.getX() + "," +
								<xsl:value-of select="@name"/>.getY() + "," +
								<xsl:value-of select="@name"/>.getZ() + "}");
						}
					</xsl:when>
					<xsl:when test="@type = 'Location'">
					    if (<xsl:value-of select="@name"/> != null) {
					        buf.append(" {<xsl:value-of select="@name"/> " +
					            <xsl:value-of select="@name"/>.getX() + "," +
					            <xsl:value-of select="@name"/>.getY() + "," +
					            <xsl:value-of select="@name"/>.getZ() + "}");
					    }
					</xsl:when>
					<xsl:when test="@type = 'Velocity'">
					    if (<xsl:value-of select="@name"/> != null) {
					        buf.append(" {<xsl:value-of select="@name"/> " +
					            <xsl:value-of select="@name"/>.getX() + "," +
					            <xsl:value-of select="@name"/>.getY() + "," +
					            <xsl:value-of select="@name"/>.getZ() + "}");
					    }
					</xsl:when>
					<xsl:when test="@type = 'Rotation'">
					    if (<xsl:value-of select="@name"/> != null) {
					        buf.append(" {<xsl:value-of select="@name"/> " +
					            <xsl:value-of select="@name"/>.getPitch() + "," +
					            <xsl:value-of select="@name"/>.getYaw() + "," +
					            <xsl:value-of select="@name"/>.getRoll() + "}");
					    }
					</xsl:when>
					<xsl:when test="@type = 'Color'">
					    if (<xsl:value-of select="@name"/> != null) {
					        buf.append(" {<xsl:value-of select="@name"/> " +
					            <xsl:value-of select="@name"/>.getRed() + "," +
					            <xsl:value-of select="@name"/>.getGreen() + "," +
					            <xsl:value-of select="@name"/>.getBlue() + "," +
					            <xsl:value-of select="@name"/>.getAlpha() + "}");
					    }
					</xsl:when>
					<xsl:when test="@type = 'Point2D'">
					    if (<xsl:value-of select="@name"/> != null) {
					        buf.append(" {<xsl:value-of select="@name"/> " +
					            <xsl:value-of select="@name"/>.getX() + "," +
					            <xsl:value-of select="@name"/>.getY() + "}");
					    }
					</xsl:when>
					<xsl:when test="@type = 'Dimension2D'">
					    if (<xsl:value-of select="@name"/> != null) {
					        buf.append(" {<xsl:value-of select="@name"/> " +
					            <xsl:value-of select="@name"/>.getWidth() + "," +
					            <xsl:value-of select="@name"/>.getHeight() + "}");
					    }
					</xsl:when>
					<xsl:when test="@type = 'UnrealId'">
						if (<xsl:value-of select="@name"/> != null) {
							buf.append(" {<xsl:value-of select="@name"/> " + <xsl:value-of select="@name"/>.getStringId() + "}");
						}
					</xsl:when>
					<xsl:otherwise>
						if (<xsl:value-of select="@name"/> != null) {
							buf.append(" {<xsl:value-of select="@name"/> " + <xsl:value-of select="@name"/> + "}");
						}
					</xsl:otherwise>
				</xsl:choose>
     		</xsl:for-each>
   			return buf.toString();
   		}
 	</xsl:template>
 	
 	<!--
 		extraJavaMessage($category, $type)
	 		Used to output additional java code for the message of specific category and type.
	 		@param category
	 			possible values: base, composite, local, shared, static
	 		@param type
	 			possible values: abstract, message, impl (not relevant for type == composite)
 			CONTEXT: messageobject
 	 -->
 	<xsl:template name="extraJavaMessage">
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		
 		// --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=all]) ---
        	<xsl:value-of select="extra/code/java/javapart/classcategory[@name='all']/../.."/>
		// --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=all]) ---
		
	    // --- Extra Java from XML BEGIN (extra/code/java/javapart/classcategory[@name=<xsl:value-of select="$category" />+classtype[@name=<xsl:value-of select="$type" />]) ---
	        <xsl:for-each select="extra/code/java/javapart/classcategory[@name=$category]">
				<xsl:for-each select="../classtype[@name=$type]">
					<xsl:value-of select="../.." />
			    </xsl:for-each>
			</xsl:for-each>
	    // --- Extra Java from XML END (extra/code/java/javapart/classcategory[@name=<xsl:value-of select="$category" />+classtype[@name=<xsl:value-of select="$type" />]) ---        	            	
 	</xsl:template>
 	
 	<!--
 		extraJavaCommand()
	 		Used to output additional java code for the command.
	 		
 			CONTEXT: commandobject
 	 -->
 	<xsl:template name="extraJavaCommand">
 		// --- Extra Java from XML BEGIN (extra/code/java)
        	<xsl:value-of select="extra/code/java"/>
		// --- Extra Java from XML END (extra/code/java)
 	</xsl:template>

<!-- 
	======================
	 ASSEMBLY SUBROUTINES
	======================
 -->
 
 	<!--    
    	classMessageHeader($category, $type)
    		Outputs class header for the message of specified category/type, e.g.: public abstract class MoverLocal implements ILocalWorldObject, ILocalViewable
    		@param category
	 			possible values: base, composite, local, shared, static
	 		@param type
	 			possible values: abstract, message, impl (not relevant for type == composite)    		
    		CONTEXT: message
     -->
 	<xsl:template name="classMessageHeader">
 		<!-- TEMPLATE PARAMS -->
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		
 		<!-- TEMPLATE BODY -->
 		<xsl:call-template name="javaDocClassMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>  		
   		<xsl:call-template name="annotationsMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
 		<xsl:call-template name="classDeclarationMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
	    <xsl:call-template name="extendsMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template> 
	    <xsl:call-template name="interfacesMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
	    {
 	</xsl:template>
 	
 	<!--    
    	classMessageFooter($category, $type)
    		Outputs class footer for the message of specified category/type
    		@param category
	 			possible values: base, composite, local, shared, static
	 		@param type
	 			possible values: abstract, message, impl (not relevant for type == composite)    		
    		CONTEXT: message
     -->
 	<xsl:template name="classMessageFooter">
 		<!-- TEMPLATE PARAMS -->
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		
 		<!-- TEMPLATE BODY -->
 		<xsl:if test="$category='static'">
 			<xsl:call-template name="isDifferentFrom"><xsl:with-param name="type" select="$type" /></xsl:call-template>
 		</xsl:if>
 		<!-- <xsl:call-template name="equalsMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
-->
		<xsl:call-template name="toStringMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
	    <xsl:call-template name="toHtmlStringMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
	    
	    <xsl:if test="($category='base' and $type='abstract') or ($category='event')">
	    	<xsl:call-template name="toJsonLiteral" />
	    </xsl:if>
	    
		<xsl:call-template name="extraJavaMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
		}
 	</xsl:template>
 
 	<!--    
    	fileMessageHeader($category, $type)
    		Outputs complete java body for the Event message.
    		@param category
	 			possible values: base, composite, local, shared, static
	 		@param type
	 			possible values: abstract, message, impl (not relevant for type == composite)    		
    		CONTEXT: message 
     -->
 	<xsl:template name="fileMessageHeader">
 		<!-- TEMPLATE PARAMS -->
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		
 		<!-- TEMPLATE BODY -->
 	
		<xsl:call-template name="javapackage"><xsl:with-param name="package" select="/messages/settings/javasettings/javapackageinfomessages/@package" /></xsl:call-template>

   		<xsl:call-template name="fillImportsMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
 	</xsl:template>

<!--
    =======================================
	MAIN SUBROUTINE FOR THE 'messageobject'
    =======================================
       RESPECTIVE CATEGORIES AND TYPES
    =======================================
 -->
 
 	<!--    
    	classMessageEvent()
    	
    		Outputs complete java body for the Event message.
    		
    		CONTEXT: message
     -->
 	<xsl:template name="classMessageEvent">
 		<!-- TEMPLATE VARIABLES -->
 		<xsl:variable name="category">event</xsl:variable>
 		<xsl:variable name="type">impl</xsl:variable>
 	
 		<!-- TEMPLATE BODY -->
    	<xsl:call-template name="classMessageHeader"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	    	
    	<xsl:call-template name="prototypeCreatorMessage"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	<xsl:call-template name="constructorsCreatorMessageEvent" />
    	<xsl:call-template name="propertiesMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
    	
    	<xsl:call-template name="classMessageFooter"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>    	 	
    </xsl:template>
 
 	<!--    
    	classMessageBaseAbstract()
    	
    		Outputs complete java body for the Base/Abstract part of the ObjectUpdate.
    		
    		CONTEXT: message
     -->
 	<xsl:template name="classMessageBaseAbstract">
 		<!-- TEMPLATE VARIABLES -->
 		<xsl:variable name="category">base</xsl:variable>
 		<xsl:variable name="type">abstract</xsl:variable>
 	
 		<!-- TEMPLATE BODY -->
    	<xsl:call-template name="classMessageHeader"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	    	
    	<xsl:call-template name="prototypeCreatorMessage"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	<xsl:call-template name="constructorsCreatorMessageBase"><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	<xsl:call-template name="propertiesMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
    	<xsl:call-template name="updateMessageBaseAbstract" />
    	
    	<xsl:call-template name="classMessageFooter"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    </xsl:template>
 
 	<!--    
    	classMessageMessagePart($category)
    		Outputs complete java body for the static|local|shared/Message part of the ObjectUpdate.
    		@param category
    			possible values: static, local, shared
    		
    		CONTEXT: message
     -->
 	<xsl:template name="classMessageMessagePart">
 		<!-- TEMPLATE PARAMS -->
 		<xsl:param name="category" />
 		
 		<!-- TEMPLATE VARIABLES -->
 		<xsl:variable name="type">message</xsl:variable>
 	
 		<!-- TEMPLATE BODY -->
    	<xsl:call-template name="classMessageHeader"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	    	
    	<xsl:if test="$category='shared'">
    		<xsl:call-template name="constructorsCreatorSharedMessage" />
    	</xsl:if>
    	<xsl:call-template name="propertiesMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
    	
    	<xsl:call-template name="classMessageFooter"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    </xsl:template>
    
    <!--    
    	classMessageBaseMessage()
    		Outputs complete java body for the Base/Message part of the ObjectUpdate.
    		@param category
    			possible values: static, local, shared
    		
    		CONTEXT: message
     -->
 	<xsl:template name="classMessageBaseMessage">
 		<!-- TEMPLATE VARIABLES -->
 		<xsl:variable name="category">base</xsl:variable>
 		<xsl:variable name="type">message</xsl:variable>
 	
 		<!-- TEMPLATE BODY -->
    	<xsl:call-template name="classMessageHeader"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	    	
    	<xsl:call-template name="constructorsCreatorMessageBase"><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	<xsl:call-template name="propertiesMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
    	
    	<xsl:call-template name="classMessageMessagePart"><xsl:with-param name="category">local</xsl:with-param></xsl:call-template>
    	<xsl:call-template name="classMessageMessagePart"><xsl:with-param name="category">static</xsl:with-param></xsl:call-template>
    	<xsl:call-template name="classMessageMessagePart"><xsl:with-param name="category">shared</xsl:with-param></xsl:call-template>
    	
    	<xsl:call-template name="updateMessageBaseMessage" />
    	
    	<xsl:call-template name="classMessageFooter"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    </xsl:template>
    
    <!--    
    	classMessageComposite()
    		Outputs complete java body for the Base/Message part of the ObjectUpdate.
    		@param category
    			possible values: static, local, shared
    		
    		CONTEXT: message
     -->
 	<xsl:template name="classMessageComposite">
 		<!-- TEMPLATE VARIABLES -->
 		<xsl:variable name="category">composite</xsl:variable>
 		<xsl:variable name="type">impl</xsl:variable>
 	
 		<!-- TEMPLATE BODY -->
    	<xsl:call-template name="classMessageHeader"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	    	
    	<xsl:call-template name="constructorsCreatorMessageComposite" />
    	<xsl:call-template name="propertiesMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
    	
    	<xsl:call-template name="updateMessageComposite" />
    	
    	<xsl:call-template name="classMessageFooter"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    </xsl:template>
    
    <!--    
    	classMessagePart($category, $type)
    		Outputs complete java body for the Base/Message part of the ObjectUpdate.
    		@param category
    			possible values: static, local, shared
    		@param type
    			possible values: abstract, impl
    		
    		CONTEXT: message
     -->
 	<xsl:template name="classMessagePart">
 		<!-- TEMPLATE PARAMS -->
 		<xsl:param name="category" />
 		<xsl:param name="type" />
 		
 		<!-- TEMPLATE BODY -->
    	<xsl:call-template name="classMessageHeader"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	    	
    	<xsl:call-template name="constructorsCreatorMessagePart"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
    	
    	<xsl:call-template name="propertiesMessage"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
    	
    	<xsl:if test="$type='impl'">
    		<xsl:choose>
	    		<xsl:when test="$category='static'">
	    			<xsl:call-template name="updateMessageStaticImpl" />
	    		</xsl:when>
	    		<xsl:when test="$category='local'">
	    			<xsl:call-template name="updateMessageLocalImpl" />
	    		</xsl:when>
	    		<xsl:when test="$category='shared'">
	    			<xsl:call-template name="updateMessageSharedImpl" />
	    		</xsl:when>
	    		<xsl:otherwise>
	    			UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-CLASS-MESSAGE-PART
	    		</xsl:otherwise>
    		</xsl:choose>
    	</xsl:if>
    	<xsl:if test="($type='abstract') and ($category='local')">
    		<xsl:call-template name="updateMessageLocalAbstract" />
    	</xsl:if>
    	
    	<xsl:call-template name="classMessageFooter"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    </xsl:template>
    
<!--
    =======================================
	MAIN SUBROUTINE FOR THE 'messageobject'
    =======================================
--> 
    
	 <!--    
	    classMessage($filename, $category, $type)
	    
	    @param filename
	    	where to output the result file ... relative/full path to the .java file
	    @param category
	    	possible values: base, event, composite, local, shared, static
	    @param type
	    	possible values: abstract, message, impl (not relevant for type == composite)
	    	
	    VALID COMBINATIONS FOR THE 'ObjectUpdate':      any except 'event'
	    
	    VALID COMBINATIONS FOR THE 'Batch' and 'Event': event+impl
	    
	    CONTEXT: messageobject
	-->  
 	<xsl:template name="classMessage">
 		<!-- TEMPLATE PARAMETERS -->
    	<xsl:param name="filename"/>
    	<xsl:param name="category"/>
    	<xsl:param name="type"/>
    	
    	<!-- TEMPLATE BODY -->
    	<xsl:result-document href="{$filename}" method="text" indent="yes">
    	 	<xsl:call-template name="fileMessageHeader"><xsl:with-param name="category" select="$category" /><xsl:with-param name="type" select="$type" /></xsl:call-template>
    	 	
			<xsl:choose>
				<xsl:when test="$category='base'">
					<xsl:choose>
						<xsl:when test="$type='abstract'">
							<xsl:call-template name="classMessageBaseAbstract" />
						</xsl:when>
						<xsl:when test="$type='message'">
							<xsl:call-template name="classMessageBaseMessage" />
						</xsl:when>
						<xsl:otherwise>
							UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-CLASS-MESSAGE
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$category='event'">
					<xsl:choose>
						<xsl:when test="$type='impl'">
							<xsl:call-template name="classMessageEvent" />
						</xsl:when>
						<xsl:otherwise>
							UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-CLASS-MESSAGE
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$category='composite'">
					<xsl:choose>
						<xsl:when test="$type='impl'">
							<xsl:call-template name="classMessageComposite" />
						</xsl:when>
						<xsl:otherwise>
							UNSUPPORTED-COMBINATION-<xsl:value-of select="$category"/>/<xsl:value-of select="$type"/>-CLASS-MESSAGE
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="classMessagePart"><xsl:with-param name="category" select="$category"/><xsl:with-param name="type" select="$type"/></xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>    	 	
    	 	
    	</xsl:result-document>
 	</xsl:template>
 	
<!--
    =======================================
	MAIN SUBROUTINE FOR THE 'commandobject'
    =======================================
 -->
 
	<!-- 
    	classCommand($filename)
	    @param filename
    		where to output the result file ... relative/full path to the .java dile
	    CONTEXT: commandobject
	-->  
 	<xsl:template name="classCommand">
 		<!-- TEMPLATE PARAMETERS -->
    	<xsl:param name="filename"/>
    	
    	<!-- TEMPLATE BODY -->
    	<xsl:result-document href="{$filename}" method="text" indent="yes">
    	 	
    	 	<xsl:call-template name="header"/>
    	 	
    	 	<xsl:call-template name="javapackage"><xsl:with-param name="package" select="/messages/settings/javasettings/javapackagecommands/@package" /></xsl:call-template>

    		<xsl:call-template name="fillImportsCommand" />

			<xsl:call-template name="javaDocClassCommand" />  		
    		
    		<xsl:call-template name="annotationsCommand" />
    		
    		<!-- CLASS HEADER -->
    		<xsl:call-template name="classDeclarationCommand" />
	        <xsl:call-template name="extendsCommand" /> 
	        <xsl:call-template name="interfacesCommand" />
	        {
	        	<!-- CLASS BODY -->
		        <xsl:call-template name="prototypeCreatorCommand"/>
		        
		        <xsl:call-template name="constructorsCreatorCommand" />
		        	            
	            <xsl:call-template name="paramsCommand" />
	            
	            <xsl:call-template name="toStringCommand" />
	            
	            <xsl:call-template name="toHtmlStringCommand" />
	            
	            <xsl:call-template name="toMessageCommand" />

				<xsl:call-template name="extraJavaCommand" />
	        }
    	</xsl:result-document>
 	</xsl:template>
 	
<!-- 
		              ==================
			     ============================
			======================================
		====||                                  ||====
	========||           MAIN PROGRAM           ||========      
		====||                                  ||====
			======================================
			     ============================
			          ==================	
-->

    <!-- 
    	MAIN TEMPLATE translating message/command objects to java files.
    	CONTEXT: /all/ 
    !-->
    <xsl:template match="//messageobject|//commandobject">
    	<xsl:choose>
    		<xsl:when test="local-name() = 'messageobject'">
    		    <!-- ======================= -->
    			<!-- MESSAGE OBJECT HANDLING -->
    			<!-- ======================= -->
    			<xsl:choose>
    			
    				<xsl:when test="@type='Batch'">
    					<!-- ====================== -->
		    			<!-- MESSAGE OBJECT - BATCH -->
		    			<!-- ====================== -->
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'.java')" />
    						<xsl:with-param name="category">event</xsl:with-param>
    						<xsl:with-param name="type">impl</xsl:with-param>
    					</xsl:call-template>
    				</xsl:when>
    				
    				<xsl:when test="@type='Event'">
    					<!-- ====================== -->
		    			<!-- MESSAGE OBJECT - EVENT -->
		    			<!-- ====================== -->
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'.java')" />
    						<xsl:with-param name="category">event</xsl:with-param>
    						<xsl:with-param name="type">impl</xsl:with-param>
    					</xsl:call-template>
    				</xsl:when>
    				
    				<xsl:when test="@type='ObjectUpdate'">
    					<!-- ============================== -->
		    			<!-- MESSAGE OBJECT - OBJECT UPDATE -->
		    			<!-- ============================== -->	
		    			
						<!-- CATEGORY: BASE -->
		    			<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'.java')" />
    						<xsl:with-param name="category">base</xsl:with-param>
    						<xsl:with-param name="type">abstract</xsl:with-param>
    					</xsl:call-template>
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'Message.java')" />
    						<xsl:with-param name="category">base</xsl:with-param>
    						<xsl:with-param name="type">message</xsl:with-param>
    					</xsl:call-template>
    					
    					<!-- CATEGORY: COMPOSITE -->
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'CompositeImpl.java')" />
    						<xsl:with-param name="category">composite</xsl:with-param>
    						<xsl:with-param name="type">impl</xsl:with-param>
    					</xsl:call-template>
    					
    					<!-- CATEGORY: LOCAL -->
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'Local.java')" />
    						<xsl:with-param name="category">local</xsl:with-param>
    						<xsl:with-param name="type">abstract</xsl:with-param>
    					</xsl:call-template>
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'LocalImpl.java')" />
    						<xsl:with-param name="category">local</xsl:with-param>
    						<xsl:with-param name="type">impl</xsl:with-param>
    					</xsl:call-template>
    					
    					<!-- CATEGORY: STATIC -->
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'Static.java')" />
    						<xsl:with-param name="category">static</xsl:with-param>
    						<xsl:with-param name="type">abstract</xsl:with-param>
    					</xsl:call-template>
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'StaticImpl.java')" />
    						<xsl:with-param name="category">static</xsl:with-param>
    						<xsl:with-param name="type">impl</xsl:with-param>
    					</xsl:call-template>
    					
    					<!-- CATEGORY: SHARED -->
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'Shared.java')" />
    						<xsl:with-param name="category">shared</xsl:with-param>
    						<xsl:with-param name="type">abstract</xsl:with-param>
    					</xsl:call-template>
    					<xsl:call-template name="classMessage">
    						<xsl:with-param name="filename" select="concat($infoJavaDir,@name,'SharedImpl.java')" />
    						<xsl:with-param name="category">shared</xsl:with-param>
    						<xsl:with-param name="type">impl</xsl:with-param>
    					</xsl:call-template>
    				</xsl:when>

    				<xsl:otherwise>
    					<xsl:variable name="filename" select="concat($infoJavaDir, @name,'.java')"/>
    					<xsl:result-document href="{$filename}" method="text" indent="yes">
    						MESSAGE OBJECT OF UNSUPPORTED TYPE: <xsl:value-of select="@type"/>
    						Supported:                          Batch, Event, ObjectUpdate (Case sensitive!)
    					</xsl:result-document>
    				</xsl:otherwise>

    			</xsl:choose>    			
    		</xsl:when>
    		
    		<xsl:otherwise>
    			<!-- ======================= -->
    			<!-- COMMAND OBJECT HANDLING -->
    			<!-- ======================= -->
    			<xsl:call-template name="classCommand">
    				<xsl:with-param name="filename" select="concat($commandsJavaDir, @name,'.java')" />
    			</xsl:call-template>
    		</xsl:otherwise>
    	</xsl:choose>
    </xsl:template>   
    
<!-- 
		              =================
			     ===========================
			=====================================
		====||                                 ||====
	========||        DOCBOOK TEMPLATES        ||========      
		====||                                 ||====
			=====================================
			     ===========================
			          =================	
--> 
    <xsl:template match="para">
		<![CDATA[<p>]]>
			<xsl:apply-templates select="."/>
		<![CDATA[</p>]]>
	</xsl:template>

	<xsl:template match="itemizedlist">
		<![CDATA[<ul>]]>
			<xsl:apply-templates select="."/>
		<![CDATA[</ul>]]>
	</xsl:template>

	<xsl:template match="listitem">
		<![CDATA[<li>]]>
			<xsl:apply-templates select="."/>
		<![CDATA[</li>]]>
    </xsl:template>

	<xsl:template match="simplelist">
		<![CDATA[<ul>]]><xsl:apply-templates/><![CDATA[</ul>]]>
    </xsl:template>

	<xsl:template match="member">
		<![CDATA[<li>]]><xsl:apply-templates/><![CDATA[</li>]]>
    </xsl:template>

<!-- 
	                  ==================
			     ============================
			======================================
		====||                                  ||====
	========||               ROOT               ||========      
		====||                                  ||====
			======================================
			     ============================
			          ==================	
 -->

 	<xsl:template match="/">
 	
 <!-- 
			              ==================
				     ============================
				======================================
			====||                                  ||====
		========||    COMPOSITE OBJECT CREATOR      ||========      
			====||                                  ||====
				======================================
				     ============================
				          ==================	
	
		COMPOSITE OBJECT CREATOR
			outputs file for UT2004CompositeObjectCreator
		  	CONTEXT: all
 --> 
 	
    	<xsl:result-document href="{concat($infoJavaDir, 'UT2004CompositeObjectCreator.java')}" method="text" indent="yes">
    		/**
         	IMPORTANT !!!

         	DO NOT EDIT THIS FILE. IT IS GENERATED FROM 
         	THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         	Use Ant task process-gb-messages after that to generate this file again.
         
         	IMPORTANT END !!!
        	*/
 			   	
    		package <xsl:value-of select="/messages/settings/javasettings/javapackageinfomessages/@package"/>;

            <xsl:call-template name="fillImportsCommand"/>

            public class UT2004CompositeObjectCreator {
	
				public static interface ICompositeWorldObjectCreator&lt;T extends ICompositeWorldObject&gt; {
					
					public T create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart);
					
				}
				<xsl:for-each select="//messageobject[@type='ObjectUpdate']">
				
					public static class <xsl:value-of select="@name" />Creator implements ICompositeWorldObjectCreator&lt;<xsl:value-of select="@name" />&gt; {
						
						@Override
						public <xsl:call-template name="classNameMessage"><xsl:with-param name="category">base</xsl:with-param><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>
							   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
						{
							return 
								new
								<xsl:call-template name="classNameMessage"><xsl:with-param name="category">composite</xsl:with-param><xsl:with-param name="type">impl</xsl:with-param></xsl:call-template> 
								( 
								 new
								  <xsl:call-template name="classNameMessage"><xsl:with-param name="category">local</xsl:with-param><xsl:with-param name="type">impl</xsl:with-param></xsl:call-template>
								 ((<xsl:call-template name="classNameMessage"><xsl:with-param name="category">local</xsl:with-param><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>
								  )localPart
								 ), 
								 new
								  <xsl:call-template name="classNameMessage"><xsl:with-param name="category">shared</xsl:with-param><xsl:with-param name="type">impl</xsl:with-param></xsl:call-template>
								 (
								 	sharedPart.getId(), sharedPart.getProperties().values()
								 ), 
								 new
								  <xsl:call-template name="classNameMessage"><xsl:with-param name="category">static</xsl:with-param><xsl:with-param name="type">impl</xsl:with-param></xsl:call-template>
								 ((<xsl:call-template name="classNameMessage"><xsl:with-param name="category">static</xsl:with-param><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>
								  )staticPart
								 )
								)
							;
						}
					}
				</xsl:for-each>
				
				private static Map&lt;Class, ICompositeWorldObjectCreator&gt; creators = new HashMap&lt;Class, ICompositeWorldObjectCreator&gt;();
				
				static {
					<xsl:for-each select="//messageobject[@type='ObjectUpdate']">
						creators.put(
							<xsl:call-template name="classNameMessage"><xsl:with-param name="category">base</xsl:with-param><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>
							.class, 
							new <xsl:value-of select="@name" />Creator());
					</xsl:for-each>
				}
				
				
				public static ICompositeWorldObject createObject(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart)
				{
					NullCheck.check(localPart,  "localPart");
					NullCheck.check(sharedPart, "sharedPart");
					NullCheck.check(staticPart, "staticPart");
					
					NullCheck.check(localPart.getCompositeClass(),  "localPart.getCompositeClass()");
					NullCheck.check(sharedPart.getCompositeClass(), "sharedPart.getCompositeClass()");
					NullCheck.check(staticPart.getCompositeClass(), "staticPart.getCompositeClass()");
					
					if ( localPart.getCompositeClass() != sharedPart.getCompositeClass() || sharedPart.getCompositeClass() != staticPart.getCompositeClass()) {
						throw new PogamutException("CompositeObject cannot be created, because the objectParts belong to different compositeObject classes : "
								+ localPart.getCompositeClass() + "," + sharedPart.getCompositeClass() + "," + staticPart.getCompositeClass() , localPart);
					}
					
					ICompositeWorldObjectCreator creator = creators.get(localPart.getCompositeClass());
					if (creator == null) {
						throw new PogamutException("There is no ICompositeWorldObjectCreator registered for class " + localPart.getCompositeClass(), UT2004CompositeObjectCreator.class);
					} 
					
					return creator.create(localPart, sharedPart, staticPart);
				}
			}
    	</xsl:result-document>
    	
<!-- 
			              ==================
				     ============================
				======================================
			====||                                  ||====
		========||      SHARED OBJECT CREATOR       ||========      
			====||                                  ||====
				======================================
				     ============================
				          ==================	
	
		COMPOSITE OBJECT CREATOR
			outputs file for UT2004CompositeObjectCreator
		  	CONTEXT: all
 --> 
    	<xsl:result-document href="{concat($infoJavaDir, 'UT2004SharedObjectCreator.java')}" method="text" indent="yes">
    		/**
         	IMPORTANT !!!

         	DO NOT EDIT THIS FILE. IT IS GENERATED FROM 
         	THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         	Use Ant task process-gb-messages after that to generate this file again.
         
         	IMPORTANT END !!!
        	*/
 			   	
    		package <xsl:value-of select="/messages/settings/javasettings/javapackageinfomessages/@package"/>;

            <xsl:call-template name="fillImportsCommand"/>

            public class UT2004SharedObjectCreator {
	
				public static interface ISharedObjectCreator&lt;T extends ICompositeWorldObject&gt;
				{
					public ISharedWorldObject create(WorldObjectId id, Collection&lt;ISharedProperty&gt; c);
				}
				
				<xsl:for-each select="//messageobject[@type='ObjectUpdate']">
					public static class <xsl:value-of select="@name" />SharedCreator implements ISharedObjectCreator&lt;<xsl:call-template name="classNameMessage"><xsl:with-param name="category">base</xsl:with-param><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>&gt;
					{
						@Override
						public ISharedWorldObject create(WorldObjectId id, Collection&lt;ISharedProperty&gt; c) {
							return new <xsl:call-template name="classNameMessage"><xsl:with-param name="category">shared</xsl:with-param><xsl:with-param name="type">impl</xsl:with-param></xsl:call-template>(id, c);
						}
					}
				</xsl:for-each>
				
				private static HashMap&lt;Class, ISharedObjectCreator&gt; map = new HashMap&lt;Class, ISharedObjectCreator&gt;();
	
				static {
					<xsl:for-each select="//messageobject[@type='ObjectUpdate']">
						map.put(
							<xsl:call-template name="classNameMessage"><xsl:with-param name="category">base</xsl:with-param><xsl:with-param name="type">abstract</xsl:with-param></xsl:call-template>
							.class, 
							new <xsl:value-of select="@name" />SharedCreator()
						);
					</xsl:for-each>					
				}				
				
				public static ISharedWorldObject create(Class msgClass, WorldObjectId objectId, Collection&lt;ISharedProperty&gt; properties )
				{
					NullCheck.check(msgClass, "msgClass");
					NullCheck.check(objectId, "objectId");
					NullCheck.check(properties, "properties");
					
					ISharedObjectCreator creator = map.get(msgClass);
					if (creator == null) {
					    throw new PogamutException("There is no shared obejct creator for class " + msgClass + ".", UT2004SharedObjectCreator.class);
					}
					return creator.create(objectId, properties);
				}
			}
    	</xsl:result-document>
    	
<!-- 
			              ==================
				     ============================
				======================================
			====||                                  ||====
		========||         UTILITY PROGRAM          ||========      
			====||                                  ||====
				======================================
				     ============================
				          ==================	
	
		UTILITY PROGRAM
			outputs separate files that contains prototypes of all InfoMessage(s)
		  	CONTEXT: all
 --> 
 		<xsl:result-document href="{concat($infoJavaDir, 'InfoMessages.java')}" method="text" indent="yes">
    		/**
         	IMPORTANT !!!

         	DO NOT EDIT THIS FILE. IT IS GENERATED FROM 
         	THE JavaClassesGenerator.xslt. MODIFY THAT FILE INSTEAD OF THIS ONE.
         
         	Use Ant task process-gb-messages after that to generate this file again.
         
         	IMPORTANT END !!!
        	*/
 			   	
    		package <xsl:value-of select="/messages/settings/javasettings/javapackageinfomessages/@package"/>;

            <xsl:call-template name="fillImportsCommand"/>

            /**
             * Serves for the purpose of testing the parser. Warning - first item in PROTOTYPES is null!
             */
            public class InfoMessages {
            	public static final String[] PROTOTYPES =
            		new String[]{
            			null // dummy
            			<xsl:for-each select="//messageobject">

            				<xsl:text>,</xsl:text>
            				<xsl:value-of select="@name"/><xsl:text>.PROTOTYPE</xsl:text>
            			</xsl:for-each>
            		};

            	/** Serves to initialize map */
            	private static final Map<![CDATA[<]]>String,Class<![CDATA[<]]>? extends InfoMessage>> prototypeMap =
            		new HashMap<![CDATA[<]]>String,Class<![CDATA[<]]>? extends InfoMessage>>();

            	/** Unmodifiable map message.PROTOTYPE -> message.class */
           		public static final Map<![CDATA[<]]>String,Class<![CDATA[<]]>? extends InfoMessage>> PROTOTYPE_MAP;

            	static {
            		<xsl:for-each select="//messageobject">
            			prototypeMap.put(<xsl:value-of select="@name"/>.PROTOTYPE, <xsl:value-of select="@name"/>.class);
           			</xsl:for-each>
					PROTOTYPE_MAP = Collections.unmodifiableMap(prototypeMap);
            	}
            }

            <xsl:apply-templates/>

    	</xsl:result-document>
    </xsl:template>

<!-- 

 ===================
  END OF STYLESHEET
 ===================

-->
</xsl:stylesheet>

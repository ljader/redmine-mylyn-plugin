<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
    <xsl:output method="xml"
        encoding="UTF-8"
        indent="yes"
        omit-xml-declaration="no"/>
    
    <xsl:template match="@*">
        <xsl:attribute name="{name(.)}">
            <xsl:choose>
                <xsl:when test="name(.)='name' and .='**/*.java' and parent::node()/../@dir[.='aspectj/']">
                    <xsl:text>**/*.aj</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>
    
    <xsl:template match="comment()">
        <xsl:text>
        </xsl:text>
        <xsl:comment>
            <xsl:value-of select="."/>
        </xsl:comment>
    </xsl:template>
    
    <xsl:template match="text()">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="*">
        <xsl:copy >
            <xsl:apply-templates select="@*|self::node()/*|comment()|text()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="javac">

        <!-- Ant Task -->
        <xsl:element name="taskdef">
            <xsl:attribute name="resource"><xsl:text>org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties</xsl:text></xsl:attribute>
        </xsl:element>
        
        <!-- ArgFile -->
        <xsl:element name="property">
            <xsl:attribute name="name"><xsl:text>ajcArgFile</xsl:text></xsl:attribute>
            <xsl:attribute name="value" />
        </xsl:element>
        
        <!-- AspectJ Compiler -->
        <xsl:element name="iajc">
            <xsl:attribute name="destDir"><xsl:value-of select="@destdir"/></xsl:attribute>
            <xsl:attribute name="argfiles"><xsl:text>${ajcArgFile}</xsl:text></xsl:attribute>
            <xsl:attribute name="failonerror"><xsl:value-of select="@failonerror"/></xsl:attribute>
            <xsl:attribute name="verbose"><xsl:value-of select="@verbose"/></xsl:attribute>
            <!--
            <xsl:attribute name="fork"><xsl:text>true</xsl:text></xsl:attribute>
            <xsl:attribute name="maxmem"><xsl:text>512</xsl:text></xsl:attribute>
            -->
            <xsl:attribute name="debug"><xsl:value-of select="@debug"/></xsl:attribute>
            <xsl:attribute name="bootclasspath"><xsl:value-of select="@bootclasspath"/></xsl:attribute>
            <xsl:attribute name="source"><xsl:value-of select="@source"/></xsl:attribute>
            <xsl:attribute name="target"><xsl:value-of select="@target"/></xsl:attribute>
            
            <!-- classpath -->
            <xsl:element name="forkclasspath">
                <xsl:attribute name="refid"><xsl:value-of select="./classpath/@refid"/></xsl:attribute>
            </xsl:element>
            <xsl:element name="forkclasspath">
                <xsl:attribute name="path"><xsl:text>${buildHome}/lib/aspectjtools.jar</xsl:text></xsl:attribute>
            </xsl:element>
            <xsl:element name="classpath">
                <xsl:attribute name="refid"><xsl:value-of select="./classpath/@refid"/></xsl:attribute>
            </xsl:element>
            
            <!-- source folder -->
            <xsl:apply-templates select="./src"/>
            
        </xsl:element>
    </xsl:template>
    
    
</xsl:stylesheet>

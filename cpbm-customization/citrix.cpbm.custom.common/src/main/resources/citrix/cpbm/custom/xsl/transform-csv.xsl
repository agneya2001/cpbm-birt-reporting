<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	 <xsl:output method="text"/>
    <xsl:param name="delimiter" select="','"/>
    <xsl:template match="/">
        <!-- Subsequent lines of output contain data. -->
        <xsl:for-each select="child::*">
            <xsl:for-each select="@*|*">
                 
                <!-- Remove newlines, leading, trailing and extra white space. -->
                <xsl:variable name="normalized" select="normalize-space(.)"/>
                <xsl:choose>
                     
                    <!-- Double-quotes must be escaped with a double-quote. -->
                    <xsl:when test="contains($normalized, ',')">
                     
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$normalized"/>
                    </xsl:otherwise>
                </xsl:choose>
                 
                <!-- Append comma to all but last column in row. -->
                <xsl:if test="position() != last()">
                    <xsl:value-of select="$delimiter"/>
                </xsl:if>
            </xsl:for-each>
             
            <!-- Append newline to end of row. -->
            <xsl:text>&#xa;</xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
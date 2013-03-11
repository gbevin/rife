<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rife="http://www.uwyn.com/rife/com.uwyn.rife.template.XsltExtension"
	extension-element-prefixes="rife"
	version="1.0">

	<xsl:template match="out">
		<result>[!V 'out'/]<rife:block name="out"><sentence><xsl:value-of select="."/> ... <rife:value name="and">and </rife:value><rife:value name="goodbye"/>!</sentence></rife:block>
			<rife:blockvalue name="goodbye">goodbye</rife:blockvalue>
		</result>
	</xsl:template>
</xsl:stylesheet>


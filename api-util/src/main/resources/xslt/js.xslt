<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template name="ApiMethodInfo" match="api">
        <xsl:variable name="methodName" select="methodName"/>
        <xsl:variable name="requiredParamNumber" select="0"/>// Auto Generated.  DO NOT EDIT!
/**
* @class ${pkg}.api.<xsl:value-of select="methodName"></xsl:value-of>
* @param  {Object} $
* @param  {Object} can
* @param  {Object} _
* @param  {can.Construct} Comm
* @param  {Object} SecurityType
* @return {can.Construct}
*/
define([
    'jquery',
    'can',
    'underscore',
    '${pkg}.framework.comm',
    '${pkg}.api.security.type'
],
function($, can, _, Comm, SecurityType) {
    'use strict';

    return Comm.extend({
        api: {
            METHOD_NAME: '<xsl:value-of select="methodName"></xsl:value-of>',
            SECURITY_TYPE: SecurityType.<xsl:value-of select="securityLevel"></xsl:value-of>,
            REQUIRED: {<xsl:if test="count(parameterInfoList/parameterInfo[isRequired = 'true'])&gt;0">
                    <xsl:for-each select="parameterInfoList/parameterInfo"><xsl:variable name="isLast" select="position()=last()"/><xsl:call-template name="RequiredParameterList">
                        <xsl:with-param name="isLast" select="$isLast" /></xsl:call-template></xsl:for-each>
                </xsl:if>
            },
            OPTIONAL: {<xsl:if test="count(parameterInfoList/parameterInfo[isRequired = 'false' and injectOnly = 'false'])&gt;0">
                    <xsl:for-each select="parameterInfoList/parameterInfo"><xsl:variable name="isLast" select="position()=last()"/><xsl:call-template name="UnRequiredParameterList">
                        <xsl:with-param name="isLast" select="$isLast" /></xsl:call-template></xsl:for-each>
                </xsl:if>
            },
            VERIFY:{<xsl:if test="count(parameterInfoList/parameterInfo[verifyRegex != ''])&gt;0">
                    <xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="ParameterSetterVerifier"></xsl:call-template></xsl:for-each>
                </xsl:if>
            },
            ERROR_CODE: {<xsl:for-each select="errorCodeList/errorCode"><xsl:variable name="isLast" select="position()=last()"/><xsl:call-template name="ErrorCode">
        <xsl:with-param name="isLast" select="$isLast" /></xsl:call-template></xsl:for-each>
            }
        }
    });
});
</xsl:template>
<xsl:template name="RequiredParameterList">
    <xsl:param name="isLast"/>
    <xsl:if test="isRequired = 'true'">
                '<xsl:value-of select="name"/>': <xsl:call-template name="ParseType">
        <xsl:with-param name="type">
            <xsl:call-template name="getLastName">
                <xsl:with-param name="name" select="type"/>
            </xsl:call-template>
        </xsl:with-param>
    </xsl:call-template><xsl:if test="not($isLast)">,</xsl:if></xsl:if>
</xsl:template>
<xsl:template name="UnRequiredParameterList">
    <xsl:param name="isLast"/>
    <xsl:if test="isRequired = 'false' and injectOnly = 'false'">
                '<xsl:value-of select="name"/>': <xsl:call-template name="ParseType">
        <xsl:with-param name="type">
            <xsl:call-template name="getLastName">
                <xsl:with-param name="name" select="type"/>
            </xsl:call-template>
        </xsl:with-param>
    </xsl:call-template><xsl:if test="not($isLast)">,</xsl:if></xsl:if>
</xsl:template>
<xsl:template name="ParameterSetterVerifier">
    <xsl:if test="verifyRegex != ''">
                '<xsl:value-of select="name"/>': function (data) {
                    return /<xsl:value-of select="verifyRegex"/>/.test(data);
                }</xsl:if></xsl:template>
<xsl:template name ="getLastName">
    <xsl:param name="name"/>
    <xsl:choose>
        <xsl:when test="contains($name, '$')">
            <xsl:call-template name="getLastName">
                <xsl:with-param name="name" select="substring-after($name, '$')"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:choose>
                <xsl:when test="contains($name, '.')">
                    <xsl:call-template name="getLastName">
                        <xsl:with-param name="name" select="substring-after($name, '.')"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$name"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<xsl:template name="ApiCode" match="codeList">// Auto Generated.  DO NOT EDIT!

/**
 * 本类定义了接口有可能的返回值集合, 其中0为成功, 负数值为所有接口都有可能返回的通用code, 正数值是接口相关的code(请参见接口文档).
 */
define([], function(){
    return {<xsl:for-each select="code"><xsl:if test="isDesign='true'">
        // 模块: <xsl:value-of select="service"/>
        '<xsl:value-of select="code"/>': '<xsl:value-of select="desc"/>',</xsl:if>
    </xsl:for-each>
    }
});</xsl:template>
    <xsl:template name="string-replace-all">
        <xsl:param name="text" />
        <xsl:param name="replace" />
        <xsl:param name="by" />
        <xsl:choose>
            <xsl:when test="contains($text, $replace)">
                <xsl:value-of select="substring-before($text,$replace)" />
                <xsl:value-of select="$by" />
                <xsl:call-template name="string-replace-all">
                    <xsl:with-param name="text" select="substring-after($text,$replace)" />
                    <xsl:with-param name="replace" select="$replace" />
                    <xsl:with-param name="by" select="$by" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="ParseType">
        <xsl:param name="type"/>
        <xsl:choose>
            <xsl:when test="$type = 'boolean'">'boolean'</xsl:when>
            <xsl:when test="$type = 'byte'">'byte'</xsl:when>
            <xsl:when test="$type = 'short'">'short'</xsl:when>
            <xsl:when test="$type = 'char'">'char'</xsl:when>
            <xsl:when test="$type = 'int'">'int'</xsl:when>
            <xsl:when test="$type = 'long'">'long'</xsl:when>
            <xsl:when test="$type = 'float'">'float'</xsl:when>
            <xsl:when test="$type = 'double'">'double'</xsl:when>
            <xsl:when test="$type = 'string'">'string'</xsl:when>
            <xsl:otherwise>'json'</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="ErrorCode">
        <xsl:param name="isLast"/>
                '<xsl:value-of select="code"/>': '<xsl:value-of select="desc"/>'<xsl:if test="not($isLast)">,</xsl:if></xsl:template>
</xsl:stylesheet>
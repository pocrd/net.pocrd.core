<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template name="ApiMethodInfo" match="api">
    <xsl:variable name="methodName" select="methodName"/>
    <xsl:variable name="requiredParamNumber" select="0"/>// Auto Generated.  DO NOT EDIT!
    
using System;<xsl:if test="./parameterInfoList/parameterInfo[isList='true']">
using System.Collections.Generic;</xsl:if><xsl:if test="./parameterInfoList/parameterInfo[type='date']">
using System.Globalization;
</xsl:if>
using Newtonsoft.Json.Linq;
<xsl:if test="count(parameterInfoList/parameterInfo[isRequired='true' and isRsaEncrypt='true'])&gt;0"></xsl:if>
using ${pkg}.API.Response;
using ${pkg}.Util;
using ${pkg};

namespace ${pkg}.API.Request
{
    /**
     * <xsl:value-of select="description"/>
     * <xsl:if test="string-length(detail)&gt;0"><xsl:value-of select="detail"/></xsl:if>
     * @author <xsl:choose><xsl:when test="string-length(methodOwner)&gt;0"><xsl:value-of select="methodOwner"/></xsl:when><xsl:when test="string-length(groupOwner)&gt;0"><xsl:value-of select="groupOwner"/></xsl:when><xsl:otherwise>rendong</xsl:otherwise></xsl:choose>
     *
     */
    public class <xsl:call-template name="getClassName">
                   <xsl:with-param name="name" select="methodName" />
                 </xsl:call-template> : BaseRequest<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text><xsl:call-template name="getReturnValueType"><xsl:with-param name="name" select="returnType"/></xsl:call-template><xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text> 
    {<xsl:for-each select="parameterInfoList/parameterInfo[injectOnly = 'false']"><xsl:call-template name="RegexPatternField"/></xsl:for-each>
     <xsl:if test="count(parameterInfoList/parameterInfo[isRequired='true' and isRsaEncrypt='true'])&gt;0">
        private RsaHelper rsaHelper = null;</xsl:if>
        
        /**
         * 当前请求的构造函数，以下参数为该请求的必填参数<xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="RequiredParameterComment"><xsl:with-param name="methodName" select="$methodName"/></xsl:call-template></xsl:for-each>
         */
        public <xsl:call-template name="getClassName">
                   <xsl:with-param name="name" select="methodName" />
                 </xsl:call-template>(<xsl:call-template name="RequiredParameter" />) 
                    : base("<xsl:value-of select="methodName"></xsl:value-of>", SecurityType.<xsl:value-of select="securityLevel"></xsl:value-of>)
        {<xsl:if test="count(parameterInfoList/parameterInfo[isRequired='true'])&gt;0">
            try 
            {<xsl:if test="count(parameterInfoList/parameterInfo[isRequired='true' and isRsaEncrypt='true'])&gt;0">
                if (rsaHelper == null) 
                {
                    rsaHelper = new RsaHelper(ApiContext.getContentRsaPubKey());
                }</xsl:if><xsl:for-each select="parameterInfoList/parameterInfo">    <xsl:call-template name="RequiredParameterSetter"><xsl:with-param name="methodName" select="$methodName"/></xsl:call-template></xsl:for-each>
            }
            catch(Exception e)
            {
                throw new LocalException("SERIALIZE_ERROR", LocalException.SERIALIZE_ERROR, e);
            }</xsl:if>
        }<xsl:if test="count(parameterInfoList/parameterInfo[isRequired = 'true'])&gt;0">
        
        /**
         * 私有的默认构造函数，请勿使用
         */
        private <xsl:call-template name="getClassName">
          <xsl:with-param name="name" select="methodName" />
        </xsl:call-template>() : base("<xsl:value-of select="methodName"></xsl:value-of>", SecurityType.<xsl:value-of select="securityLevel"></xsl:value-of>)
        {
        }
      </xsl:if>
    
        <xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="NotRequiredParameterSetter"/>
        </xsl:for-each>
        
        /**
         * 当前请求有可能的异常返回值
         */
        public int HandleError()
        {
            switch (response.code) 
            {
                <xsl:for-each select="errorCodeList/errorCode"><xsl:call-template name="ErrorCode"/></xsl:for-each>
                default:
                    break;
            }
            return response.code;
        }

        /**
         * 不要直接调用这个方法，API使用者应该访问基类的getResponse()获取接口的返回值
         */
        protected override <xsl:call-template name="getReturnValueType"><xsl:with-param name="name" select="returnType"/></xsl:call-template> GetResult(JObject json) 
        {<xsl:choose><xsl:when test="'string'=returnType">
            return null;</xsl:when>
            <xsl:otherwise>
            try
            {
                return <xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template>.Deserialize(json);
            }
            catch (Exception e)
            {
                logger.Error("<xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template> deserialize failed.", e);
            }
            return null;</xsl:otherwise>
            </xsl:choose>
        }
        <xsl:if test="'string'=returnType">
        internal override void FillResponse(string rawString) 
        {
            response.code = 0;
            response.length = rawString.Length;
            response.message = "Success";
            response.result = new RawString(rawString);
        }
        </xsl:if>
    }
}
  </xsl:template>
    <xsl:template name ="getClassName">
    <xsl:param name="name"></xsl:param>
    <xsl:variable name="first" select="substring-before($name, '.')"/>
    <xsl:variable name="second" select="substring-after($name, '.')"/>
    <xsl:value-of select="translate(substring($first, 1, 1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/><xsl:value-of select="substring($first, 2)"/>_<xsl:value-of select="translate(substring($second, 1, 1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/><xsl:value-of select="substring($second, 2)"/>
  </xsl:template>
  <xsl:template name ="getEntityName">
    <xsl:param name="name"></xsl:param>
    <xsl:variable name="subname">
      <xsl:call-template name="getLastName">
        <xsl:with-param name="name" select="$name"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:call-template name="string-replace-all">
      <xsl:with-param name="text" select="$subname" />
      <xsl:with-param name="replace" select="'_'" />
      <xsl:with-param name="by" select="''" />
    </xsl:call-template>.<xsl:value-of select="$subname"/>
  </xsl:template>
  <xsl:template name="getReturnValueType">
    <xsl:param name="name"/>
      <xsl:choose>
          <xsl:when test="'string' = $name">RawString</xsl:when>
          <xsl:otherwise><xsl:call-template name="getLastName"><xsl:with-param name="name" select="$name"></xsl:with-param></xsl:call-template></xsl:otherwise>
      </xsl:choose>
  </xsl:template>
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
  <xsl:template name="RequiredParameterComment">
    <xsl:param name="methodName"/>
    <xsl:if test="isRequired = 'true'">
         * @param <xsl:value-of select="name"/><xsl:value-of select="' '"/><xsl:value-of select="description"/></xsl:if>
  </xsl:template>
  <xsl:template name="RequiredParameter">
      <xsl:for-each select="parameterInfoList/parameterInfo[isRequired='true']">
          <xsl:if test="isList = 'true'">
              <xsl:choose>
                  <xsl:when test="type = 'boolean'">bool[] </xsl:when>
                  <xsl:when test="type = 'byte'">byte[]  </xsl:when>
                  <xsl:when test="type = 'short'">short[] </xsl:when>
                  <xsl:when test="type = 'char'">char[] </xsl:when>
                  <xsl:when test="type = 'float'">float <xsl:value-of select="name" /></xsl:when>
                  <xsl:when test="type = 'double'">double[] </xsl:when>
                  <xsl:when test="type = 'int'">int[] </xsl:when>
                  <xsl:when test="type = 'long'">long[] </xsl:when>
                  <xsl:otherwise>
                      <xsl:text disable-output-escaping="yes">List&lt;</xsl:text>
                      <xsl:call-template name="ParseType">
                          <xsl:with-param name="type">
                              <xsl:call-template name="getLastName">
                                  <xsl:with-param name="name" select="type"/>
                              </xsl:call-template>
                          </xsl:with-param>
                      </xsl:call-template>
                      <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
                  </xsl:otherwise>
              </xsl:choose>
          </xsl:if>
          <xsl:if test="isList = 'false'">
              <xsl:call-template name="ParseType">
                  <xsl:with-param name="type">
                      <xsl:call-template name="getLastName">
                          <xsl:with-param name="name" select="type"/>
                      </xsl:call-template>
                  </xsl:with-param>
              </xsl:call-template>
          </xsl:if>
          <xsl:value-of select="' '"/>
          <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="name"/></xsl:call-template>
          <xsl:if test="position()!=last()">
              <xsl:value-of select="', '"/>
          </xsl:if>
      </xsl:for-each>
  </xsl:template>
  <xsl:template name="renameKeyword">
    <xsl:param name="name"/>
    <xsl:choose>
      <xsl:when test="$name= 'interface'">r_interface</xsl:when>
      <xsl:when test="$name= 'in'">r_in</xsl:when>
      <xsl:otherwise><xsl:value-of select="$name"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="ParseType">
    <xsl:param name="type"/>
    <xsl:choose>
      <xsl:when test="$type = 'date'">DateTime</xsl:when>
      <xsl:when test="$type = 'boolean'">bool</xsl:when>
      <xsl:when test="$type = '&lt;T&gt;'">JsonSerializable</xsl:when>
      <xsl:otherwise><xsl:value-of select="$type"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="ParseValue">
    <xsl:param name="type"/>
    <xsl:param name="value"/>
    <xsl:choose>
      <xsl:when test="$type = 'boolean'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.ToString()</xsl:when>
      <xsl:when test="$type = 'byte'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.ToString()</xsl:when>
      <xsl:when test="$type = 'short'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.ToString()</xsl:when>
      <xsl:when test="$type = 'char'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.ToString()</xsl:when>
      <xsl:when test="$type = 'int'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.ToString()</xsl:when>
      <xsl:when test="$type = 'long'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.ToString()</xsl:when>
      <xsl:when test="$type = 'float'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.ToString()</xsl:when>
      <xsl:when test="$type = 'double'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.ToString()</xsl:when>
      <xsl:when test="$type = 'string'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template></xsl:when>
      <xsl:when test="$type = 'date'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.GetUTCinMillionsecond()</xsl:when>
        <xsl:otherwise>
        <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.Serialize().ToString()</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="RequiredParameterSetter">
    <xsl:param name="methodName"/>
    <xsl:if test="isRequired = 'true'">
      <xsl:choose><xsl:when test="isList = 'true'">
                JArray <xsl:value-of select="name" />Array = new JArray();<xsl:call-template name="RequiredParameterSerialize">
          <xsl:with-param name="type" select="type" />
          <xsl:with-param name="name" select="name" />
          <xsl:with-param name="isList" select="isList" />
      </xsl:call-template>
        <xsl:choose>
            <xsl:when test="isRsaEncrypt='true'">
                parameters.Put("<xsl:value-of select="name" />", rsaHelper.EncryptData(<xsl:value-of select="name" />Array.ToString(), "utf-8"));</xsl:when>
            <xsl:otherwise>
                parameters.Put("<xsl:value-of select="name" />", <xsl:value-of select="name" />Array.ToString());</xsl:otherwise>
        </xsl:choose>
        </xsl:when><xsl:otherwise><xsl:choose><xsl:when test="isRsaEncrypt='true'">
                parameters.Put("<xsl:value-of select="name"/>", rsaHelper.EncryptData(<xsl:call-template name="ParseValue">
            <xsl:with-param name="type">
                <xsl:call-template name="getLastName">
                    <xsl:with-param name="name" select="type"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="name"/></xsl:call-template>, "utf-8"));</xsl:when>
        <xsl:otherwise>
                parameters.Put("<xsl:value-of select="name"/>", <xsl:call-template name="ParseValue">
            <xsl:with-param name="type">
                <xsl:call-template name="getLastName">
                    <xsl:with-param name="name" select="type"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="name"/></xsl:call-template>);</xsl:otherwise></xsl:choose>
        </xsl:otherwise></xsl:choose>
      <xsl:if test="string-length(verifyRegex)&gt;0">  if (!r_<xsl:value-of select="name"></xsl:value-of>.matcher(<xsl:call-template name="ParseValue">
      <xsl:with-param name="type">
        <xsl:call-template name="getLastName">
          <xsl:with-param name="name" select="type"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="value" select="name"/></xsl:call-template>).matches()) 
        {
            setVerifyError("<xsl:value-of select="name"></xsl:value-of>", "<xsl:value-of select="verifyMsg"></xsl:value-of>");
        }</xsl:if></xsl:if>
  </xsl:template>
  <xsl:template name="RequiredParameterSerialize">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:variable name="checkedName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:if test="$isList='true'">
                if (<xsl:value-of select="$checkedName" /> != null)
                {
                    foreach (<xsl:call-template name="ParseType"><xsl:with-param name="type"><xsl:call-template name="getLastName"><xsl:with-param name="name" select="type"/></xsl:call-template>
            </xsl:with-param></xsl:call-template> entry in <xsl:value-of select="$checkedName" />)
                    {
                        <xsl:call-template name="JsonSetter">
                            <xsl:with-param name="name" select="name" />
                            <xsl:with-param name="type" select="type" />
                            <xsl:with-param name="isList" select="isList" />
                        </xsl:call-template>
                    }
                }</xsl:if>
  </xsl:template>
  <xsl:template name="NotRequiredParameterSetter">
    <xsl:if test="isRequired = 'false' and injectOnly = 'false'">
      <xsl:variable name="name" select="name"/>
      <xsl:variable name="first" select="substring(name, 1, 1)"/>
      <xsl:variable name="firstUpper" select="translate($first,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
        
        /**
         * 当前请求的非必填参数
         * @param <xsl:value-of select="name"/><xsl:value-of select="' '"/><xsl:value-of select="description"/>
         */
        public void Set<xsl:value-of select="$firstUpper"/><xsl:value-of select="substring(name, 2)"/>(<xsl:if test="isList = 'true'">
        <xsl:choose>
            <xsl:when test="type = 'boolean'">bool[] </xsl:when>
            <xsl:when test="type = 'byte'">byte[]  </xsl:when>
            <xsl:when test="type = 'short'">short[] </xsl:when>
            <xsl:when test="type = 'char'">char[] </xsl:when>
            <xsl:when test="type = 'float'">float <xsl:value-of select="name" /></xsl:when>
            <xsl:when test="type = 'double'">double[] </xsl:when>
            <xsl:when test="type = 'int'">int[] </xsl:when>
            <xsl:when test="type = 'long'">long[] </xsl:when>
            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">List&lt;</xsl:text>
                <xsl:call-template name="ParseType">
                    <xsl:with-param name="type">
                        <xsl:call-template name="getLastName">
                            <xsl:with-param name="name" select="type"/>
                        </xsl:call-template>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:if>
        <xsl:if test="isList = 'false'">
            <xsl:call-template name="ParseType">
                <xsl:with-param name="type">
                    <xsl:call-template name="getLastName">
                        <xsl:with-param name="name" select="type"/>
                    </xsl:call-template>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:value-of select="' '"/>
        <xsl:value-of select="name"/>) 
        {
            try 
            {<xsl:choose><xsl:when test="isList = 'true'">
                JArray <xsl:value-of select="name" />Array = new JArray();<xsl:call-template name="RequiredParameterSerialize">
            <xsl:with-param name="type" select="type" />
            <xsl:with-param name="name" select="name" />
            <xsl:with-param name="isList" select="isList" />
        </xsl:call-template>
            <xsl:choose><xsl:when test="isRsaEncrypt='true'">
                if (rsaHelper == null) 
                {
                    rsaHelper = new RsaHelper(ApiContext.getContentRsaPubKey());
                }
                parameters.Put("<xsl:value-of select="name" />", rsaHelper.EncryptData(<xsl:value-of select="name" />Array.ToString(), "utf-8"));
            </xsl:when>
            <xsl:otherwise>
                parameters.Put("<xsl:value-of select="name" />", <xsl:value-of select="name" />Array.ToString());
            </xsl:otherwise></xsl:choose>
        </xsl:when><xsl:otherwise><xsl:choose><xsl:when test="isRsaEncrypt='true'">
                if (rsaHelper == null) 
                {
                    rsaHelper = new RsaHelper(ApiContext.getContentRsaPubKey());
                }
                parameters.Put("<xsl:value-of select="name"/>", rsaHelper.EncryptData(<xsl:call-template name="ParseValue">
            <xsl:with-param name="type">
                <xsl:call-template name="getLastName">
                    <xsl:with-param name="name" select="type"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="name"/></xsl:call-template>, "utf-8"));
        </xsl:when>
        <xsl:otherwise>
                parameters.Put("<xsl:value-of select="name"/>", <xsl:call-template name="ParseValue">
            <xsl:with-param name="type">
                <xsl:call-template name="getLastName">
                    <xsl:with-param name="name" select="type"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="name"/></xsl:call-template>);</xsl:otherwise></xsl:choose>
            </xsl:otherwise></xsl:choose><xsl:if test="string-length(verifyRegex)&gt;0">  
                if (!r_<xsl:value-of select="name"></xsl:value-of>.matcher(<xsl:call-template name="ParseValue">
            <xsl:with-param name="type">
                <xsl:call-template name="getLastName">
                    <xsl:with-param name="name" select="type"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="name"/></xsl:call-template>).matches()) 
                {
                    SetVerifyError("<xsl:value-of select="name"></xsl:value-of>", "<xsl:value-of select="verifyMsg"></xsl:value-of>");
                }</xsl:if>
            }
            catch (Exception e)
            {
                throw new LocalException("SERIALIZE_ERROR", LocalException.SERIALIZE_ERROR, e);
            }
        }</xsl:if>
  </xsl:template>
  <xsl:template name="RegexPatternField">
    <xsl:if test="string-length(verifyRegex)&gt;0">private java.util.regex.Pattern r_<xsl:value-of select="name"></xsl:value-of> = java.util.regex.Pattern.compile("<xsl:call-template name="string-replace-all">
      <xsl:with-param name="text" select="verifyRegex" />
      <xsl:with-param name="replace" select="'\'" />
      <xsl:with-param name="by" select="'\\'" />
    </xsl:call-template>");
    </xsl:if>
  </xsl:template>
  <xsl:template name="ErrorCode">
                /* <xsl:value-of select="desc"/> */
                case ApiCode.<xsl:value-of select="name"/>: {
                    break;
                }
  </xsl:template>
  <xsl:template name="ApiCode" match="codeList">// Auto Generated.  DO NOT EDIT!
namespace ${pkg}.API.Request
{
    /**
     * 本类定义了接口有可能的返回值集合, 其中0为成功, 负数值为所有接口都有可能返回的通用code, 正数值是接口相关的code(请参见接口文档).
     */
    public class ApiCode 
    {
        <xsl:for-each select="code"><xsl:if test="isDesign='true'">
        /* <xsl:value-of select="desc"/> | 接口组名称:<xsl:value-of select="service"/> */
        public const int <xsl:value-of select="name"/> = <xsl:value-of select="code"/>;</xsl:if>
        </xsl:for-each>
    }
}
</xsl:template>
<xsl:template name="resp" match="respStruct"><xsl:call-template name="ApiResponse"/></xsl:template>
<xsl:template name="req" match="reqStruct"><xsl:call-template name="ApiResponse"/></xsl:template>

<xsl:template name="ApiResponse">// Auto Generated.  DO NOT EDIT!
using System;
<xsl:if test="./fieldList/field[isList='true']">
using System.Collections.Generic;
</xsl:if>
<xsl:if test="./fieldList/field[type='date']">
using System.Globalization;
</xsl:if>
using Newtonsoft.Json.Linq;

using ${pkg}.Util;

namespace ${pkg}.API.Response
{
    public class <xsl:value-of select="name" /> : JsonSerializable
    {
        <xsl:for-each select="fieldList/field">
            <xsl:call-template name="ResponseField">
              <xsl:with-param name="type" select="type" />
              <xsl:with-param name="name" select="name" />
              <xsl:with-param name="isList" select="isList" />
              <xsl:with-param name="desc" select="desc" />
            </xsl:call-template>
        </xsl:for-each>

        /**
         * 反序列化函数，用于从json字符串反序列化本类型实例
         */
        public static <xsl:value-of select="name" /> Deserialize(string json) 
        {
            if (json != null <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> json.Length != 0) 
            {
                return Deserialize(JObject.Parse(json));
            }
            return null;
        }
        
        /**
         * 反序列化函数，用于从json节点对象反序列化本类型实例
         */
        public static <xsl:value-of select="name" /> Deserialize(JObject json) 
        {
            if (json != null) 
            {
                <xsl:value-of select="name" /> result = new <xsl:value-of select="name" />();
                JToken element = null;
                <xsl:choose>
                    <xsl:when test="contains(name,'Api_RawString') or contains(name,'Api_JSONString')">result.value = json.ToString();
                    </xsl:when>
                    <xsl:when test="contains(name,'Api_DateResp')">
                        result.value = json["value"].ToString("yyyy-MM-dd HH:mm:ss");
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:for-each select="fieldList/field">
                            <xsl:call-template name="DeserializeField">
                                <xsl:with-param name="type" select="type" />
                                <xsl:with-param name="name" select="name" />
                                <xsl:with-param name="isList" select="isList" />
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
                return result;
            }
            return null;
        }
        
        /**
         * 序列化函数，用于从对象生成数据字典
         */
        public JObject Serialize()
        {
            JObject json = new JObject();
            <xsl:for-each select="fieldList/field">
              <xsl:call-template name="SerializeField">
                <xsl:with-param name="type" select="type" />
                <xsl:with-param name="name" select="name" />
                <xsl:with-param name="isList" select="isList" />
              </xsl:call-template>
            </xsl:for-each>
            return json;
        }
    }
}
  </xsl:template>
  <xsl:template name="ResponseField">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:param name="desc"/>
        /**
         * <xsl:value-of select="desc" />
         */
    <xsl:choose>
      <xsl:when test="$isList='true'">
        <xsl:choose>
          <xsl:when test="$type = 'boolean'">    public bool[] <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>;</xsl:when>
          <xsl:when test="$type = 'byte'">    public byte[] <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>;</xsl:when>
          <xsl:when test="$type = 'short'">    public short[] <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>;</xsl:when>
          <xsl:when test="$type = 'char'">    public char[] <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>;</xsl:when>
          <xsl:when test="$type = 'float'">    public float[] <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>;</xsl:when>
          <xsl:when test="$type = 'double'">    public double[] <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>;</xsl:when>
          <xsl:when test="$type = 'int'">    public int[] <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>;</xsl:when>
          <xsl:when test="$type = 'long'">    public long[] <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>;</xsl:when>
          <xsl:otherwise>    public List<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text><xsl:call-template name="ParseType">
              <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param></xsl:call-template><xsl:text disable-output-escaping="yes"><![CDATA[> ]]></xsl:text>
              <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>;
            </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>    public <xsl:call-template name="ParseType"><xsl:with-param name="type"><xsl:value-of select="type" /></xsl:with-param></xsl:call-template><xsl:text disable-output-escaping="yes"><![CDATA[ ]]></xsl:text><xsl:value-of select="name" />;
      </xsl:otherwise></xsl:choose>
  </xsl:template>
  <xsl:template name="DeserializeField">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:choose>
      <xsl:when test="$isList='true'">
                /* <xsl:value-of select="desc" /> */
                element = json["<xsl:value-of select="name" />"];
                if (element != null)
                {
                    var <xsl:value-of select="$name" />Array = (JArray)element;
                    int len = <xsl:value-of select="$name" />Array.Count;
                    <xsl:choose>
                      <xsl:when test="$type = 'boolean'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new bool[len];</xsl:when>
                      <xsl:when test="$type = 'byte'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new byte[len];</xsl:when>
                      <xsl:when test="$type = 'short'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new short[len];</xsl:when>
                      <xsl:when test="$type = 'char'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new char[len];</xsl:when>
                      <xsl:when test="$type = 'float'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new float[len];</xsl:when>
                      <xsl:when test="$type = 'double'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new double[len];</xsl:when>
                      <xsl:when test="$type = 'int'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new int[len];</xsl:when>
                      <xsl:when test="$type = 'long'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new long[len];</xsl:when>
                      <xsl:when test="$type = 'string'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new List<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>string<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>();</xsl:when>
                      <xsl:otherwise>result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = new List<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
                      <xsl:value-of select="$type" /><xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>();</xsl:otherwise>
                    </xsl:choose>
                    for (int i = 0; i <xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text> len; i++)
                    {
                        <xsl:call-template name="JsonGetter">
                          <xsl:with-param name="type" select="$type" />
                          <xsl:with-param name="name" select="$name" />
                          <xsl:with-param name="isList" select="$isList" />
                        </xsl:call-template>
                    }
                }
      </xsl:when>
      <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="$type!='&lt;T&gt;'">
                /* <xsl:value-of select="desc" /> */
                element = json["<xsl:value-of select="name" />"];
                if (element != null)
                {
                    <xsl:call-template name="JsonGetter">
                      <xsl:with-param name="type" select="type" />
                      <xsl:with-param name="name" select="name" />
                      <xsl:with-param name="isList" select="isList" />
                    </xsl:call-template>
                }
            </xsl:when>
          </xsl:choose>
      </xsl:otherwise></xsl:choose>
  </xsl:template>
  <xsl:template name="JsonGetter">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:choose>
      <xsl:when test="$isList = 'true'">
        <xsl:choose>
          <xsl:when test="$type = 'boolean'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>[i] = (bool)<xsl:value-of select="name" />Array[i];</xsl:when>
          <xsl:when test="$type = 'byte'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>[i] = (byte)<xsl:value-of select="name" />Array[i];</xsl:when>
          <xsl:when test="$type = 'short'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>[i] = (short)<xsl:value-of select="name" />Array[i];</xsl:when>
          <xsl:when test="$type = 'char'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>[i] = (char)<xsl:value-of select="name" />Array[i];</xsl:when>
          <xsl:when test="$type = 'float'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>[i] = (float)<xsl:value-of select="name" />Array[i];</xsl:when>
          <xsl:when test="$type = 'double'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>[i] = (double)<xsl:value-of select="name" />Array[i];</xsl:when>
          <xsl:when test="$type = 'int'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>[i] = (int)<xsl:value-of select="name" />Array[i];</xsl:when>
          <xsl:when test="$type = 'long'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>[i] = (long)<xsl:value-of select="name" />Array[i];</xsl:when>
          <xsl:when test="$type = 'string'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>.Add((string)<xsl:value-of select="name" />Array[i]);</xsl:when>
          <xsl:when test="$type = 'date'">
            DateTime dt;
            if (!String.IsNullOrEmpty((string)entry) <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text>
                DateTime.TryParseExact(
                (string)entry,
                "yyyy-MM-dd HH:mm:ss",
                CultureInfo.InvariantCulture,
                DateTimeStyles.None,
                out dt))
            {
                result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>.Add(dt);
            } 
            else
            {
                Console.WriteLine("invalid datetime: {0}", (string)entry);
            }
          </xsl:when>
          <xsl:when test="$type = 'Api_DynamicEntity'">JObject jo = (JObject)<xsl:value-of select="name" />Array[i];
                        if (jo != null) 
                        {
                            Api_DynamicEntity de = <xsl:value-of select="type" />.Deserialize(jo);
                            JToken e = jo["entity"];
                            if (e != null) 
                            {
                                <xsl:for-each select="extInfo/keyValue/item"><xsl:if test="position() != 1"> else </xsl:if>if ("<xsl:value-of select="key"></xsl:value-of>".Equals(de.typeName)) 
                                    {
                                        de.entity = <xsl:value-of select="value"></xsl:value-of>.Deserialize((JObject)e);
                                    }</xsl:for-each>
                                result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>.Add(de);
                            }
                        }</xsl:when>
            <xsl:otherwise>result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>.Add(<xsl:value-of select="type" />.Deserialize((JObject)<xsl:value-of select="name" />Array[i]));</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$type = 'boolean'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = (bool)element;</xsl:when>
          <xsl:when test="$type = 'byte'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = (byte)element;</xsl:when>
          <xsl:when test="$type = 'short'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = (short)element;</xsl:when>
          <xsl:when test="$type = 'char'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = (char)element;</xsl:when>
          <xsl:when test="$type = 'float'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = (float)element;</xsl:when>
          <xsl:when test="$type = 'double'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = (double)element;</xsl:when>
          <xsl:when test="$type = 'int'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = (int)element;</xsl:when>
          <xsl:when test="$type = 'long'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = (long)element;</xsl:when>
          <xsl:when test="$type = 'string'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = (string)element;</xsl:when>
          <xsl:when test="$type = 'date'">
            DateTime dt;
            if (!String.IsNullOrEmpty((string)element) <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text>
                DateTime.TryParseExact(
                (string)element,
                "yyyy-MM-dd HH:mm:ss",
                CultureInfo.InvariantCulture,
                DateTimeStyles.None,
                out dt))
            {
                result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = dt;
            } 
            else
            {
                Console.WriteLine("invalid datetime: {0}", (string)element);
            }
          </xsl:when>
          <xsl:when test="$type = 'Api_DynamicEntity'">result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = <xsl:value-of select="type" />.Deserialize((JObject)element);
                    JToken e = ((JObject)element)["entity"];
                    if (e != null) 
                    {
                        <xsl:for-each select="extInfo/keyValue/item"><xsl:if test="position() != 1"> else </xsl:if>if ("<xsl:value-of select="key"></xsl:value-of>".Equals(result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>.typeName)) 
                            {
                                result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template>.entity = <xsl:value-of select="value"></xsl:value-of>.Deserialize((JObject)e);
                            }</xsl:for-each>
                    }</xsl:when>
            <xsl:otherwise>result.<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template> = <xsl:value-of select="type" />.Deserialize((JObject)element);</xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose></xsl:template>
  <xsl:template name="SerializeField">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:variable name="checkedName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:choose>
      <xsl:when test="$isList='true'">
            /* <xsl:value-of select="desc" /> */
            if (this.<xsl:value-of select="$checkedName" /> != null) 
            {
                var <xsl:value-of select="$name" />Array = new JArray();
                foreach (var entry in this.<xsl:value-of select="$checkedName" />)
                {
                    <xsl:call-template name="JsonSetter">
                      <xsl:with-param name="type" select="$type" />
                      <xsl:with-param name="name" select="$name" />
                      <xsl:with-param name="isList" select="$isList" />
                    </xsl:call-template>
                }
                json["<xsl:value-of select="$name" />"] = <xsl:value-of select="$name" />Array;
            }
      </xsl:when>
      <xsl:otherwise>
            /* <xsl:value-of select="desc" /> */
            <xsl:call-template name="JsonSetter">
              <xsl:with-param name="type" select="type" />
              <xsl:with-param name="name" select="name" />
              <xsl:with-param name="isList" select="isList" />
            </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="JsonSetter">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:variable name="checkedName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:choose>
      <xsl:when test="$isList = 'true'">
        <xsl:choose>
          <xsl:when test="$type = 'boolean'"><xsl:value-of select="$name" />Array.Add(entry);</xsl:when>
          <xsl:when test="$type = 'byte'"><xsl:value-of select="$name" />Array.Add(entry);</xsl:when>
          <xsl:when test="$type = 'char'"><xsl:value-of select="$name" />Array.Add(entry);</xsl:when>
          <xsl:when test="$type = 'short'"><xsl:value-of select="$name" />Array.Add(entry);</xsl:when>
          <xsl:when test="$type = 'float'"><xsl:value-of select="$name" />Array.Add(entry);</xsl:when>
          <xsl:when test="$type = 'double'"><xsl:value-of select="$name" />Array.Add(entry);</xsl:when>
          <xsl:when test="$type = 'int'"><xsl:value-of select="$name" />Array.Add(entry);</xsl:when>
          <xsl:when test="$type = 'long'"><xsl:value-of select="$name" />Array.Add(entry);</xsl:when>
          <xsl:when test="$type = 'string'"><xsl:value-of select="$name" />Array.Add(entry);</xsl:when>
          <xsl:otherwise>if (entry != null)
                    {
                        <xsl:value-of select="$name" />Array.Add(entry.Serialize());
                    }</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$type = 'boolean'">json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />;
          </xsl:when>
          <xsl:when test="$type = 'byte'">json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />;
          </xsl:when>
          <xsl:when test="$type = 'char'">if (this.<xsl:value-of select="$checkedName" /> != Char.MinValue)
            {
                json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />.ToString();
            }
          </xsl:when>
          <xsl:when test="$type = 'short'">json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />;
          </xsl:when>
          <xsl:when test="$type = 'float'">json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />;
          </xsl:when>
          <xsl:when test="$type = 'double'">json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />;
          </xsl:when>
          <xsl:when test="$type = 'int'">json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />;
          </xsl:when>
          <xsl:when test="$type = 'long'">json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />;
          </xsl:when>
          <xsl:when test="$type = 'date'">if (this.<xsl:value-of select="$checkedName" /> != DateTime.MinValue)
          {
              json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName"/>.ToString("yyyy-MM-dd HH:mm:ss");
          }
          </xsl:when>
          <xsl:when test="$type = 'string'">if (this.<xsl:value-of select="$checkedName" /> != null) { json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />; }
          </xsl:when>
          <xsl:otherwise>if (this.<xsl:value-of select="$checkedName" /> != null) { json["<xsl:value-of select="name" />"] = this.<xsl:value-of select="$checkedName" />.Serialize(); }
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template name="ApiMethodInfo" match="api">
    <xsl:variable name="methodName" select="methodName"/>
    <xsl:variable name="requiredParamNumber" select="0"/>// Auto Generated.  DO NOT EDIT!

package ${pkg}.api.request;
<xsl:if test="./parameterInfoList/parameterInfo[isList='true']">
import java.util.List;</xsl:if><xsl:if test="./parameterInfoList/parameterInfo[type='date']">
import java.util.Date;</xsl:if>
import com.google.gson.*;
<xsl:if test="count(parameterInfoList/parameterInfo[isRequired='true' and isRsaEncrypt='true'])&gt;0">
import ${pkg}.ApiContext;
import ${pkg}.util.Base64Util;
import ${pkg}.util.RsaHelper;</xsl:if>
import ${pkg}.LocalException;
import ${pkg}.BaseRequest;
import ${pkg}.SecurityType;
import ${pkg}.api.resp.*;

/**
 * <xsl:value-of select="description"/>
 * <xsl:if test="string-length(detail)&gt;0"><xsl:value-of select="detail"/></xsl:if>
 * @author <xsl:choose><xsl:when test="string-length(methodOwner)&gt;0"><xsl:value-of select="methodOwner"/></xsl:when><xsl:when test="string-length(groupOwner)&gt;0"><xsl:value-of select="groupOwner"/></xsl:when><xsl:otherwise>rendong</xsl:otherwise></xsl:choose>
 *
 */
public class <xsl:call-template name="getClassName">
               <xsl:with-param name="name" select="methodName" />
             </xsl:call-template> extends BaseRequest<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text><xsl:call-template name="getReturnValueType"><xsl:with-param name="name" select="returnType"/></xsl:call-template><xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text> {
    <xsl:for-each select="parameterInfoList/parameterInfo[injectOnly = 'false']"><xsl:call-template name="RegexPatternField"/></xsl:for-each>
      <xsl:if test="count(parameterInfoList/parameterInfo[isRequired='true' and isRsaEncrypt='true'])&gt;0">
    private RsaHelper rsaHelper = null;
      </xsl:if>
    /**
     * 当前请求的构造函数，以下参数为该请求的必填参数<xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="RequiredParameterComment"><xsl:with-param name="methodName" select="$methodName"/></xsl:call-template></xsl:for-each>
     */
    public <xsl:call-template name="getClassName">
               <xsl:with-param name="name" select="methodName" />
             </xsl:call-template>(<xsl:call-template name="RequiredParameter" />) {
        super("<xsl:value-of select="methodName"></xsl:value-of>", SecurityType.<xsl:value-of select="securityLevel"></xsl:value-of>);<xsl:if test="count(parameterInfoList/parameterInfo[isRequired = 'true'])&gt;0">

        try {<xsl:if test="count(parameterInfoList/parameterInfo[isRequired='true' and isRsaEncrypt='true'])&gt;0">
            if (rsaHelper == null) {
                rsaHelper = new RsaHelper(ApiContext.getContentRsaPubKey());
            }</xsl:if><xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="RequiredParameterSetter"><xsl:with-param name="methodName" select="$methodName"/></xsl:call-template></xsl:for-each>
        } catch(Exception e) {
            throw new LocalException("SERIALIZE_ERROR", LocalException.SERIALIZE_ERROR, e);
        }</xsl:if>
    }
    <xsl:if test="count(parameterInfoList/parameterInfo[isRequired = 'true'])&gt;0">
    /**
     * 私有的默认构造函数，请勿使用
     */
    private <xsl:call-template name="getClassName">
               <xsl:with-param name="name" select="methodName" />
             </xsl:call-template>() {
        super("<xsl:value-of select="methodName"></xsl:value-of>", SecurityType.<xsl:value-of select="securityLevel"></xsl:value-of>);
    }
    </xsl:if>

    <xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="NotRequiredParameterSetter"/>
    </xsl:for-each>
    /**
     * 当前请求有可能的异常返回值
     */
    public int handleError() {
        switch (response.code) {<xsl:for-each select="errorCodeList/errorCode"><xsl:call-template name="ErrorCode"/></xsl:for-each>
        }
        return response.code;
    }

    /**
     * 不要直接调用这个方法，API使用者应该访问基类的getResponse()获取接口的返回值
     */
    @Override
    protected <xsl:call-template name="getReturnValueType"><xsl:with-param name="name" select="returnType"/></xsl:call-template> getResult(JsonObject json) {
        <xsl:choose>
            <xsl:when test="'string'=returnType">return null;</xsl:when>
        <xsl:otherwise>try {
            return <xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template>.deserialize(json);
        } catch (Exception e) {
            logger.error("<xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template> deserialize failed.", e);
        }
        return null;</xsl:otherwise>
        </xsl:choose>
    }
    <xsl:if test="'string'=returnType">
    @Override
    protected void fillResponse(String rawString) {
        response.code = 0;
        response.length = rawString.length();
        response.message = "Success";
        response.result = rawString;
    }
    </xsl:if>
    <xsl:if test="count(exportParams/item)&gt;0 or count(parameterInfoList/parameterInfo[string-length(serviceInjection)&gt;0])&gt;0">
    /******************************************** 以下功能处理接口依赖 既A接口的输出作为B接口的输入 ********************************************/
    <xsl:if test="count(exportParams/item)&gt;0">
    private static final String[] exportParams = new String[] { <xsl:for-each select="exportParams/item">"<xsl:value-of select="."/>"<xsl:if test="position()!=last()"><xsl:value-of select="', '"/></xsl:if></xsl:for-each> };

    protected String[] getExportParams() {
        return exportParams;
    }
    </xsl:if><xsl:if test="count(parameterInfoList/parameterInfo[string-length(serviceInjection)&gt;0])&gt;0">
    private static final String[] importParams = new String[] { <xsl:for-each select="parameterInfoList/parameterInfo[string-length(serviceInjection)&gt;0]">"<xsl:value-of select="serviceInjection"/>"<xsl:if test="position()!=last()"><xsl:value-of select="', '"/></xsl:if></xsl:for-each> };

    protected String[] getImportParams() {
        return importParams;
    }

    private BaseRequest[] dependencies = null;

    protected BaseRequest[] getDependencies() {
        return dependencies;
    }

    public static DependencyBuilder createDependencyBuilder() {
        return new DependencyBuilder();
    }

    public static class DependencyBuilder extends AbstractDependencyBuilder {

        private DependencyBuilder() {

        }

        public DependencyBuilder depends(BaseRequest dependency) {
            addDependency(<xsl:call-template name="getClassName">
               <xsl:with-param name="name" select="methodName" />
             </xsl:call-template>.importParams, dependency);
            return this;
        }

        public <xsl:call-template name="getClassName">
               <xsl:with-param name="name" select="methodName" />
             </xsl:call-template> build(<xsl:call-template name="RequiredParameter" />) {
            <xsl:call-template name="getClassName">
               <xsl:with-param name="name" select="methodName" />
             </xsl:call-template> request = new <xsl:call-template name="getClassName">
               <xsl:with-param name="name" select="methodName" />
             </xsl:call-template>(<xsl:for-each select="parameterInfoList/parameterInfo[isRequired='true']">
                                    <xsl:call-template name="renameKeyword">
                                      <xsl:with-param name="name" select="name"/>
                                    </xsl:call-template>
                                    <xsl:if test="position()!=last()">
                                      <xsl:value-of select="', '"/>
                                    </xsl:if>
                                  </xsl:for-each>);
            request.dependencies = new BaseRequest[dependencies.size()];
            dependencies.toArray(request.dependencies);
            checkDependency(new String[] { <xsl:for-each select="parameterInfoList/parameterInfo[isRequired='true' and string-length(serviceInjection)&gt;0]">"<xsl:value-of select="serviceInjection"/>"<xsl:if test="position()!=last()"><xsl:value-of select="', '"/></xsl:if></xsl:for-each> }, request);
            return request;
        }
    }
    </xsl:if></xsl:if>
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
          <xsl:when test="'string' = $name">String</xsl:when>
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
     * @param <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="name"/></xsl:call-template><xsl:value-of select="' '"/><xsl:value-of select="description"/></xsl:if>
  </xsl:template>
  <xsl:template name="RequiredParameter">
      <xsl:for-each select="parameterInfoList/parameterInfo[isRequired='true']">
          <xsl:if test="isList = 'true'">
              <xsl:choose>
                  <xsl:when test="type = 'boolean'">boolean[] </xsl:when>
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
      <xsl:otherwise><xsl:value-of select="$name"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="ParseType">
    <xsl:param name="type"/>
    <xsl:choose>
      <xsl:when test="$type = 'string'">String</xsl:when>
      <xsl:when test="$type = 'date'">Date</xsl:when>
      <xsl:when test="$type = '&lt;T&gt;'">JsonSerializable</xsl:when>
      <xsl:otherwise><xsl:value-of select="$type"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="ParseValue">
    <xsl:param name="type"/>
    <xsl:param name="value"/>
    <xsl:choose>
      <xsl:when test="$type = 'boolean'">String.valueOf(<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>)</xsl:when>
      <xsl:when test="$type = 'byte'">String.valueOf(<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>)</xsl:when>
      <xsl:when test="$type = 'short'">String.valueOf(<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>)</xsl:when>
      <xsl:when test="$type = 'char'">String.valueOf((int)<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>)</xsl:when>
      <xsl:when test="$type = 'int'">String.valueOf(<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>)</xsl:when>
      <xsl:when test="$type = 'long'">String.valueOf(<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>)</xsl:when>
      <xsl:when test="$type = 'float'">String.valueOf(<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>)</xsl:when>
      <xsl:when test="$type = 'double'">String.valueOf(<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>)</xsl:when>
      <xsl:when test="$type = 'string'"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template></xsl:when>
      <xsl:when test="$type = 'date'">String.valueOf(<xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.getTime())</xsl:when>
        <xsl:otherwise>
        <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$value"/></xsl:call-template>.serialize().toString()</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="RequiredParameterSetter">
    <xsl:param name="methodName"/>
    <xsl:if test="isRequired = 'true'">
      <xsl:choose><xsl:when test="isList = 'true'">
            JsonArray <xsl:value-of select="name" />Array = new JsonArray();<xsl:call-template name="RequiredParameterSerialize">
          <xsl:with-param name="type" select="type" />
          <xsl:with-param name="name" select="name" />
          <xsl:with-param name="isList" select="isList" />
      </xsl:call-template>
        <xsl:choose>
            <xsl:when test="isRsaEncrypt='true'">
            params.put("<xsl:value-of select="name" />", Base64Util.encodeToString(rsaHelper.encrypt(<xsl:value-of select="name" />Array.toString().getBytes("UTF-8"))));
            </xsl:when>
            <xsl:otherwise>
                params.put("<xsl:value-of select="name" />", <xsl:value-of select="name" />Array.toString());
            </xsl:otherwise>
        </xsl:choose>
        </xsl:when><xsl:otherwise><xsl:choose><xsl:when test="isRsaEncrypt='true'">
            params.put("<xsl:value-of select="name"/>", Base64Util.encodeToString(rsaHelper.encrypt(<xsl:call-template name="ParseValue">
            <xsl:with-param name="type">
                <xsl:call-template name="getLastName">
                    <xsl:with-param name="name" select="type"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="name"/></xsl:call-template>.getBytes("UTF-8"))));
        </xsl:when>
        <xsl:otherwise>
            params.put("<xsl:value-of select="name"/>", <xsl:call-template name="ParseValue">
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
      <xsl:with-param name="value" select="name"/></xsl:call-template>).matches()) {
            setVerifyError("<xsl:value-of select="name"></xsl:value-of>", "<xsl:value-of select="verifyMsg"></xsl:value-of>");
        }</xsl:if></xsl:if>
  </xsl:template>
  <xsl:template name="RequiredParameterSerialize">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:variable name="checkedName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:if test="$isList='true'">
            if (<xsl:value-of select="$checkedName" /> != null) {
                for (<xsl:call-template name="ParseType"><xsl:with-param name="type"><xsl:call-template name="getLastName"><xsl:with-param name="name" select="type"/></xsl:call-template>
        </xsl:with-param></xsl:call-template> value : <xsl:value-of select="$checkedName" />) {
                    <xsl:call-template name="JsonSetter">
                        <xsl:with-param name="name" select="name" />
                        <xsl:with-param name="type" select="type" />
                        <xsl:with-param name="isList" select="isList" />
                </xsl:call-template>
                }
            }
    </xsl:if>
  </xsl:template>
  <xsl:template name="NotRequiredParameterSetter">
    <xsl:if test="isRequired = 'false' and injectOnly = 'false'">
      <xsl:variable name="name" select="name"/>
      <xsl:variable name="first" select="substring(name, 1, 1)"/>
      <xsl:variable name="firstUpper" select="translate($first,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    /**
     * 当前请求的非必填参数
     * @param <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="name"/></xsl:call-template><xsl:value-of select="' '"/><xsl:value-of select="description"/>
     */
    public void set<xsl:value-of select="$firstUpper"/><xsl:value-of select="substring(name, 2)"/>(<xsl:if test="isList = 'true'">
        <xsl:choose>
            <xsl:when test="type = 'boolean'">boolean[] </xsl:when>
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
        <xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="name"/></xsl:call-template>) {
        try {<xsl:choose><xsl:when test="isList = 'true'">
            JsonArray <xsl:value-of select="name" />Array = new JsonArray();<xsl:call-template name="RequiredParameterSerialize">
            <xsl:with-param name="type" select="type" />
            <xsl:with-param name="name" select="name" />
            <xsl:with-param name="isList" select="isList" />
        </xsl:call-template>
            <xsl:choose><xsl:when test="isRsaEncrypt='true'">
                    if (rsaHelper == null) {
                        rsaHelper = new RsaHelper(ApiContext.getContentRsaPubKey());
                    }
                    params.put("<xsl:value-of select="name" />", Base64Util.encodeToString(rsaHelper.encrypt(<xsl:value-of select="name" />Array.toString().getBytes("UTF-8"))));
                </xsl:when>
                <xsl:otherwise>
                    params.put("<xsl:value-of select="name" />", <xsl:value-of select="name" />Array.toString());
                </xsl:otherwise>
            </xsl:choose>
        </xsl:when><xsl:otherwise><xsl:choose><xsl:when test="isRsaEncrypt='true'">
            if (rsaHelper == null) {
                rsaHelper = new RsaHelper(ApiContext.getContentRsaPubKey());
            }
            params.put("<xsl:value-of select="name"/>", Base64Util.encodeToString(rsaHelper.encrypt(<xsl:call-template name="ParseValue">
            <xsl:with-param name="type">
                <xsl:call-template name="getLastName">
                    <xsl:with-param name="name" select="type"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="name"/></xsl:call-template>.getBytes("UTF-8"))));
        </xsl:when>
        <xsl:otherwise>
            params.put("<xsl:value-of select="name"/>", <xsl:call-template name="ParseValue">
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
            <xsl:with-param name="value" select="name"/></xsl:call-template>).matches()) {
            setVerifyError("<xsl:value-of select="name"></xsl:value-of>", "<xsl:value-of select="verifyMsg"></xsl:value-of>");
            }</xsl:if>
        } catch(Exception e) {
            throw new LocalException("SERIALIZE_ERROR", LocalException.SERIALIZE_ERROR, e);
        }
    }
    </xsl:if>
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
            }</xsl:template>
  <xsl:template name="ApiCode" match="codeList">// Auto Generated.  DO NOT EDIT!
package ${pkg}.api.request;

/**
 * 本类定义了接口有可能的返回值集合, 其中0为成功, 负数值为所有接口都有可能返回的通用code, 正数值是接口相关的code(请参见接口文档).
 */
public class ApiCode {
    <xsl:for-each select="code"><xsl:if test="isDesign='true'">
    /* <xsl:value-of select="desc"/> | 接口组名称:<xsl:value-of select="service"/> */
    public static final int <xsl:value-of select="name"/> = <xsl:value-of select="code"/>;</xsl:if>
    </xsl:for-each>
}</xsl:template>
    <xsl:template name="resp" match="respStruct"><xsl:call-template name="ApiResponse"/></xsl:template>
    <xsl:template name="req" match="reqStruct"><xsl:call-template name="ApiResponse"/></xsl:template>
  <xsl:template name="ApiResponse">// Auto Generated.  DO NOT EDIT!
package ${pkg}.api.resp;
<xsl:if test="./fieldList/field[isList='true']">
import java.util.ArrayList;
import java.util.List;
</xsl:if><xsl:if test="./fieldList/field[type='date']">
import java.util.Date;
</xsl:if>
import com.google.gson.*;
import ${pkg}.util.JsonSerializable;

public class <xsl:value-of select="name" /> implements JsonSerializable {
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
    public static <xsl:value-of select="name" /> deserialize(String json) {
        if (json != null <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> json.length() != 0) {
            return deserialize(new JsonParser().parse(json).getAsJsonObject());
        }
        return null;
    }

    /**
     * 反序列化函数，用于从json节点对象反序列化本类型实例
     */
    public static <xsl:value-of select="name" /> deserialize(JsonObject json) {
        if (json != null <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> !json.isJsonNull()) {
            <xsl:value-of select="name" /> result = new <xsl:value-of select="name" />();
            JsonElement element = null;
            <xsl:choose>
                <xsl:when test="contains(name,'Api_RawString') or contains(name,'Api_JSONString')">result.value = json.toString();
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
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
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
          <xsl:when test="$type = 'boolean'">public boolean[] <xsl:value-of select="name" />;</xsl:when>
          <xsl:when test="$type = 'byte'">public byte[] <xsl:value-of select="name" />;</xsl:when>
          <xsl:when test="$type = 'short'">public short[] <xsl:value-of select="name" />;</xsl:when>
          <xsl:when test="$type = 'char'">public char[] <xsl:value-of select="name" />;</xsl:when>
          <xsl:when test="$type = 'float'">public float[] <xsl:value-of select="name" />;</xsl:when>
          <xsl:when test="$type = 'double'">public double[] <xsl:value-of select="name" />;</xsl:when>
          <xsl:when test="$type = 'int'">public int[] <xsl:value-of select="name" />;</xsl:when>
          <xsl:when test="$type = 'long'">public long[] <xsl:value-of select="name" />;</xsl:when>
          <xsl:otherwise>public List<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text><xsl:call-template name="ParseType">
              <xsl:with-param name="type"><xsl:value-of select="type" /></xsl:with-param></xsl:call-template><xsl:text disable-output-escaping="yes"><![CDATA[> ]]></xsl:text>
              <xsl:value-of select="name" />;</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>public <xsl:call-template name="ParseType"><xsl:with-param name="type"><xsl:value-of select="type" /></xsl:with-param></xsl:call-template><xsl:text disable-output-escaping="yes"><![CDATA[ ]]></xsl:text><xsl:value-of select="name" />;
      </xsl:otherwise></xsl:choose></xsl:template>
  <xsl:template name="DeserializeField">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:choose>
      <xsl:when test="$isList='true'">
            /* <xsl:value-of select="desc" /> */
            element = json.get("<xsl:value-of select="name" />");
            if (element != null <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> !element.isJsonNull()) {
                JsonArray <xsl:value-of select="$name" />Array = element.getAsJsonArray();
                int len = <xsl:value-of select="$name" />Array.size();
                <xsl:choose>
                  <xsl:when test="$type = 'boolean'">result.<xsl:value-of select="$name" /> = new boolean[len];</xsl:when>
                  <xsl:when test="$type = 'byte'">result.<xsl:value-of select="$name" /> = new byte[len];</xsl:when>
                  <xsl:when test="$type = 'short'">result.<xsl:value-of select="$name" /> = new short[len];</xsl:when>
                  <xsl:when test="$type = 'char'">result.<xsl:value-of select="$name" /> = new char[len];</xsl:when>
                  <xsl:when test="$type = 'float'">result.<xsl:value-of select="$name" /> = new float[len];</xsl:when>
                  <xsl:when test="$type = 'double'">result.<xsl:value-of select="$name" /> = new double[len];</xsl:when>
                  <xsl:when test="$type = 'int'">result.<xsl:value-of select="$name" /> = new int[len];</xsl:when>
                  <xsl:when test="$type = 'long'">result.<xsl:value-of select="$name" /> = new long[len];</xsl:when>
                  <xsl:when test="$type = 'string'">result.<xsl:value-of select="$name" /> = new ArrayList<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>String<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>(len);</xsl:when>
                  <xsl:when test="$type = 'date'">result.<xsl:value-of select="$name" /> = new Date[len];</xsl:when>
                  <xsl:otherwise>result.<xsl:value-of select="name" /> = new ArrayList<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
                  <xsl:value-of select="$type" /><xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>(len);</xsl:otherwise>
          </xsl:choose>
                for (int i = 0; i<xsl:text disable-output-escaping="yes"><![CDATA[ < ]]></xsl:text>len; i++) {
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
            element = json.get("<xsl:value-of select="name" />");
            if (element != null <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> !element.isJsonNull()) {
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
          <xsl:when test="$type = 'boolean'">result.<xsl:value-of select="name" />[i] = <xsl:value-of select="name" />Array.get(i).getAsBoolean();</xsl:when>
          <xsl:when test="$type = 'byte'">result.<xsl:value-of select="name" />[i] = <xsl:value-of select="name" />Array.get(i).getAsByte();</xsl:when>
          <xsl:when test="$type = 'short'">result.<xsl:value-of select="name" />[i] = <xsl:value-of select="name" />Array.get(i).getAsShort();</xsl:when>
          <xsl:when test="$type = 'char'">result.<xsl:value-of select="name" />[i] = <xsl:value-of select="name" />Array.get(i).getAsCharacter();</xsl:when>
          <xsl:when test="$type = 'float'">result.<xsl:value-of select="name" />[i] = <xsl:value-of select="name" />Array.get(i).getAsFloat();</xsl:when>
          <xsl:when test="$type = 'double'">result.<xsl:value-of select="name" />[i] = <xsl:value-of select="name" />Array.get(i).getAsDouble();</xsl:when>
          <xsl:when test="$type = 'int'">result.<xsl:value-of select="name" />[i] = <xsl:value-of select="name" />Array.get(i).getAsInt();</xsl:when>
          <xsl:when test="$type = 'long'">result.<xsl:value-of select="name" />[i] = <xsl:value-of select="name" />Array.get(i).getAsLong();</xsl:when>
          <xsl:when test="$type = 'string'">if (<xsl:value-of select="name" />Array.get(i) != null) {
                        result.<xsl:value-of select="name" />.add(<xsl:value-of select="name" />Array.get(i).getAsString());
                    } else {
                        result.<xsl:value-of select="name" />.add(i, null);
                    }</xsl:when>
          <xsl:when test="$type = 'date'">result.<xsl:value-of select="name" />[i] = new Date(<xsl:value-of select="name" />Array.get(i).getAsLong());</xsl:when>
          <xsl:when test="$type = 'Api_DynamicEntity'">JsonObject jo = <xsl:value-of select="name" />Array.get(i).getAsJsonObject();
                    if (jo != null <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> !jo.isJsonNull()) {
                        Api_DynamicEntity de = <xsl:value-of select="type" />.deserialize(jo);
                        JsonElement e = jo.getAsJsonObject().get("entity");
                        if (e != null <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> !e.isJsonNull()) {
                        <xsl:for-each select="extInfo/keyValue/item"><xsl:if test="position() != 1"> else </xsl:if>if ("<xsl:value-of select="key"></xsl:value-of>".equals(de.typeName)) {
                            de.entity = <xsl:value-of select="value"></xsl:value-of>.deserialize(e.getAsJsonObject());
                        }</xsl:for-each>
                            result.<xsl:value-of select="name" />.add(de);
                        }
                    }</xsl:when>
          <xsl:otherwise>JsonObject jo = <xsl:value-of select="name" />Array.get(i).getAsJsonObject();
                    if (jo != null <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> !jo.isJsonNull()) {
                        result.<xsl:value-of select="name" />.add(<xsl:value-of select="type" />.deserialize(jo));
                    }</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$type = 'boolean'">result.<xsl:value-of select="$name" /> = element.getAsBoolean();</xsl:when>
          <xsl:when test="$type = 'byte'">result.<xsl:value-of select="$name" /> = element.getAsByte();</xsl:when>
          <xsl:when test="$type = 'short'">result.<xsl:value-of select="$name" /> = element.getAsShort();</xsl:when>
          <xsl:when test="$type = 'char'">result.<xsl:value-of select="$name" /> = element.getAsCharacter();</xsl:when>
          <xsl:when test="$type = 'float'">result.<xsl:value-of select="$name" /> = element.getAsFloat();</xsl:when>
          <xsl:when test="$type = 'double'">result.<xsl:value-of select="$name" /> = element.getAsDouble();</xsl:when>
          <xsl:when test="$type = 'int'">result.<xsl:value-of select="$name" /> = element.getAsInt();</xsl:when>
          <xsl:when test="$type = 'long'">result.<xsl:value-of select="$name" /> = element.getAsLong();</xsl:when>
          <xsl:when test="$type = 'string'">result.<xsl:value-of select="$name" /> = element.getAsString();</xsl:when>
          <xsl:when test="$type = 'date'">result.<xsl:value-of select="$name" /> = new Date(element.getAsLong());</xsl:when>
          <xsl:when test="$type = 'Api_DynamicEntity'">result.<xsl:value-of select="$name" /> = <xsl:value-of select="type" />.deserialize(element.getAsJsonObject());
                JsonElement e = element.getAsJsonObject().get("entity");
                if (e != null <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> !e.isJsonNull()) {
                    <xsl:for-each select="extInfo/keyValue/item"><xsl:if test="position() != 1"> else </xsl:if>if ("<xsl:value-of select="key"></xsl:value-of>".equals(result.dynamicEntity.typeName)) {
                        result.dynamicEntity.entity = <xsl:value-of select="value"></xsl:value-of>.deserialize(e.getAsJsonObject());
                    }</xsl:for-each>
                }</xsl:when>
          <xsl:otherwise>result.<xsl:value-of select="$name" /> = <xsl:value-of select="type" />.deserialize(element.getAsJsonObject());</xsl:otherwise>
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
        if (this.<xsl:value-of select="$checkedName" /> != null) {
            JsonArray <xsl:value-of select="$name" />Array = new JsonArray();
            for (<xsl:call-template name="ParseType"><xsl:with-param name="type"><xsl:call-template name="getLastName"><xsl:with-param name="name" select="$type"/></xsl:call-template>
        </xsl:with-param></xsl:call-template> value : this.<xsl:value-of select="$checkedName" />) {
                <xsl:call-template name="JsonSetter">
                  <xsl:with-param name="type" select="$type" />
                  <xsl:with-param name="name" select="$name" />
                  <xsl:with-param name="isList" select="$isList" />
                </xsl:call-template>
            }
            json.add("<xsl:value-of select="$name" />", <xsl:value-of select="$name" />Array);
        }
      </xsl:when>
      <xsl:otherwise>
        /* <xsl:value-of select="desc" /> */
        <xsl:call-template name="JsonSetter">
          <xsl:with-param name="type" select="type" />
          <xsl:with-param name="name" select="name" />
          <xsl:with-param name="isList" select="isList" />
        </xsl:call-template>
      </xsl:otherwise></xsl:choose></xsl:template>
  <xsl:template name="JsonSetter">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:variable name="checkedName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:choose>
      <xsl:when test="$isList = 'true'">
        <xsl:choose>
          <xsl:when test="$type = 'boolean'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value));</xsl:when>
          <xsl:when test="$type = 'byte'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value));</xsl:when>
          <xsl:when test="$type = 'char'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value));</xsl:when>
          <xsl:when test="$type = 'short'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value));</xsl:when>
          <xsl:when test="$type = 'float'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value));</xsl:when>
          <xsl:when test="$type = 'double'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value));</xsl:when>
          <xsl:when test="$type = 'int'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value));</xsl:when>
          <xsl:when test="$type = 'long'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value));</xsl:when>
          <xsl:when test="$type = 'string'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value));</xsl:when>
          <xsl:when test="$type = 'date'"><xsl:value-of select="$name" />Array.add(new JsonPrimitive(value.getTime()));</xsl:when>
          <xsl:otherwise>if (value != null) {
                    <xsl:value-of select="$name" />Array.add(value.serialize());
                }</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$type = 'boolean'">json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />);
          </xsl:when>
          <xsl:when test="$type = 'byte'">json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />);
          </xsl:when>
          <xsl:when test="$type = 'char'">json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />);
          </xsl:when>
          <xsl:when test="$type = 'short'">json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />);
          </xsl:when>
          <xsl:when test="$type = 'float'">json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />);
          </xsl:when>
          <xsl:when test="$type = 'double'">json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />);
          </xsl:when>
          <xsl:when test="$type = 'int'">json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />);
          </xsl:when>
          <xsl:when test="$type = 'long'">json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />);
          </xsl:when>
          <xsl:when test="$type = 'string'">if (this.<xsl:value-of select="$checkedName" /> != null) { json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />); }
          </xsl:when>
          <xsl:when test="$type = 'date'">json.addProperty("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />.getTime());
          </xsl:when>
          <xsl:otherwise>if (this.<xsl:value-of select="$checkedName" /> != null) { json.add("<xsl:value-of select="name" />", this.<xsl:value-of select="$checkedName" />.serialize()); }
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose></xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template name="ApiMethodInfo" match="api">
    <xsl:variable name="methodName" select="methodName"/>
    <xsl:variable name="parameterList">
      <xsl:for-each select="parameterInfoList/parameterInfo">
        <xsl:call-template name="RequiredParameter"><xsl:with-param name="methodName" select="$methodName"/></xsl:call-template>
      </xsl:for-each>
    </xsl:variable>// Auto Generated.  DO NOT EDIT!

#import "${prefix}ApiCode.h"
#import "${prefix}BaseRequest.h"
<xsl:choose><xsl:when test="not('string'=returnType)">#import "${prefix}<xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template>.h"</xsl:when></xsl:choose><xsl:for-each select="parameterInfoList/parameterInfo[substring(type, 1, 4) = 'Api_' and injectOnly = 'false']">
#import "${prefix}<xsl:value-of select="type" />.h"</xsl:for-each>

/*
 * <xsl:value-of select="description"/>
 * <xsl:if test="string-length(detail)&gt;0"><xsl:value-of select="detail"/></xsl:if>
 * @author <xsl:choose><xsl:when test="string-length(methodOwner)&gt;0"><xsl:value-of select="methodOwner"/></xsl:when><xsl:when test="string-length(groupOwner)&gt;0"><xsl:value-of select="groupOwner"/></xsl:when><xsl:otherwise>rendong</xsl:otherwise></xsl:choose>
 */
@interface ${prefix}<xsl:call-template name="getClassName">
               <xsl:with-param name="name" select="methodName" />
             </xsl:call-template> : ${prefix}BaseRequest
{
<xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="RegexPatternField_H"/></xsl:for-each>
}

/*
 * 当前请求的构造函数，以下参数为该请求的必填参数<xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="RequiredParameterComment"><xsl:with-param name="methodName" select="$methodName"/></xsl:call-template></xsl:for-each>
 */
- (id) init<xsl:choose>
             <xsl:when test="contains($parameterList, ' ')">With<xsl:value-of select="translate(substring($parameterList, 1, 1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/><xsl:value-of select="substring(substring($parameterList,0,string-length($parameterList)), 2)"/>
             </xsl:when>
           </xsl:choose>;
<xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="NotRequiredParameterSetter_H"/>
</xsl:for-each>
/*
 * 当前请求有可能的异常返回值
 */
- (NSInteger) handleError;

/*
 * 获取服务端返回的请求结果实体
 */
<xsl:choose><xsl:when test="'string'=returnType">- (NSString *) result;</xsl:when>
<xsl:otherwise>- (${prefix}<xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template> *) result;</xsl:otherwise></xsl:choose>

@end
/************ split .h and .m file ************/
// Auto Generated.  DO NOT EDIT!

#import "${prefix}<xsl:call-template name="getClassName"><xsl:with-param name="name" select="methodName" /></xsl:call-template>.h"
<xsl:choose><xsl:when test="not('string'=returnType)">#import "${prefix}<xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template>.h"</xsl:when></xsl:choose>

/*
 * <xsl:value-of select="description"/>
 * @author CodeGenerator@pocrd.net
 */
@implementation ${prefix}<xsl:call-template name="getClassName"><xsl:with-param name="name" select="methodName" /></xsl:call-template>

/*
 * 当前请求的构造函数，以下参数为该请求的必填参数<xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="RequiredParameterComment"><xsl:with-param name="methodName" select="$methodName"/></xsl:call-template></xsl:for-each>
 */
- (id) init<xsl:choose>
              <xsl:when test="contains($parameterList, ' ')">With<xsl:value-of select="translate(substring($parameterList, 1, 1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/><xsl:value-of select="substring(substring($parameterList,0,string-length($parameterList)), 2)"/>
             </xsl:when>
           </xsl:choose>
{
    if (self = [super initWithMethodName:@"<xsl:value-of select="methodName"/>" securityType:SecurityType_<xsl:value-of select="securityLevel"/>])
    {
        <xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="RegexPatternField_M"/></xsl:for-each>
        <xsl:value-of select="'  '"/><xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="RequiredParameterSetter"><xsl:with-param
          name="methodName" select="$methodName"/></xsl:call-template></xsl:for-each>
    }
    return self;
}
<xsl:for-each select="parameterInfoList/parameterInfo"><xsl:call-template name="NotRequiredParameterSetter_M"/>
</xsl:for-each>
/*
 * 当前请求有可能的异常返回值
 */
- (NSInteger) handleError
{
    switch (_response.code)
    {<xsl:for-each select="errorCodeList/errorCode"><xsl:call-template name="ErrorCode"/></xsl:for-each>
    }
    return _response.code;
}

/*
 * 获取服务端返回的请求结果实体
 */
<xsl:choose><xsl:when test="'string'=returnType">- (NSString *) result</xsl:when>
<xsl:otherwise>- (${prefix}<xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template> *) result</xsl:otherwise></xsl:choose>
{
    if (_response<xsl:text disable-output-escaping="yes"><![CDATA[ && ]]></xsl:text>![_response isKindOfClass:[NSNull class]]) {
        return <xsl:choose><xsl:when test="'string'=returnType">(NSString *)</xsl:when>
      <xsl:otherwise>(${prefix}<xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template> *)</xsl:otherwise></xsl:choose> _response.result;
    }
    return nil;
}

/*
 * 将服务端返回的json数据反序列化为实体
 */
- (void) deserializeResponse:(NSDictionary *) json
{
    @try
    {
      _response.result = <xsl:choose><xsl:when test="'string'=returnType">[json objectForKey:@"raw_string"];</xsl:when>
          <xsl:otherwise>[${prefix}<xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template> deserialize:json];</xsl:otherwise></xsl:choose>
    }
    @catch (NSException *exception)
    {
        //${prefix}Debug(@"%@%@",exception.reason,@"<xsl:choose><xsl:when test="'string'=returnType">NSString</xsl:when><xsl:otherwise>${prefix}<xsl:call-template name="getLastName"><xsl:with-param name="name" select="returnType"/></xsl:call-template></xsl:otherwise></xsl:choose> deserialize failed");
    }
}
@end
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
    <xsl:param name="methodName"/>
    <xsl:if test="isRequired = 'true'">
      <xsl:value-of select="name"/>:(<xsl:call-template name="ParseType">
        <xsl:with-param name="type">
          <xsl:call-template name="getLastName">
            <xsl:with-param name="name" select="type"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="isList"/>
      </xsl:call-template>)<xsl:value-of select="name"/><xsl:value-of select="' '"/>
    </xsl:if>
  </xsl:template>
  <xsl:template name="ParseType">
    <xsl:param name="type"/>
    <xsl:param name="isList"/>
    <xsl:if test="isList = 'true'">NSArray *</xsl:if>
    <xsl:if test="isList = 'false'">
      <xsl:choose>
        <xsl:when test="$type = 'boolean'">BOOL</xsl:when>
        <xsl:when test="$type = 'byte'">char</xsl:when>
        <xsl:when test="$type = 'char'">unsigned short</xsl:when>
        <xsl:when test="$type = 'short'">short</xsl:when>
        <xsl:when test="$type = 'double'">double</xsl:when>
        <xsl:when test="$type = 'float'">float</xsl:when>
        <xsl:when test="$type = 'int'">NSInteger</xsl:when>
        <xsl:when test="$type = 'long'">long long</xsl:when>
        <xsl:when test="$type = 'string'">NSString *</xsl:when>
        <xsl:when test="$type = 'date'">long long</xsl:when>
        <xsl:when test="$type = '&lt;T&gt;'">${prefix}BaseEntity *</xsl:when>
        <xsl:otherwise>${prefix}<xsl:value-of select="$type"/> *</xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
  <xsl:template name="ToStringValue">
    <xsl:param name="type"/>
    <xsl:param name="value"/>
    <xsl:param name="isList"/>
    <xsl:if test="isList = 'true'"><xsl:value-of select="$value"/> == nil ? @"" : [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:<xsl:value-of select="$value"/> options:0 error:nil] encoding:NSUTF8StringEncoding]</xsl:if>
    <xsl:if test="isList = 'false'">
      <xsl:choose>
        <xsl:when test="$type = 'boolean'"><xsl:value-of select="$value"/> ? @ "true" : @"false"</xsl:when>
        <xsl:when test="$type = 'byte'">[[NSNumber numberWithChar:<xsl:value-of select="$value"/>] stringValue]</xsl:when>
        <xsl:when test="$type = 'char'">[[NSNumber numberWithUnsignedShort:<xsl:value-of select="$value"/>] stringValue]</xsl:when>
        <xsl:when test="$type = 'short'">[[NSNumber numberWithShort:<xsl:value-of select="$value"/>] stringValue]</xsl:when>
        <xsl:when test="$type = 'double'">[NSString stringWithFormat:@"%.2f",<xsl:value-of select="$value"/>]</xsl:when>
        <xsl:when test="$type = 'float'">[NSString stringWithFormat:@"%.2f",<xsl:value-of select="$value"/>]</xsl:when>
        <xsl:when test="$type = 'int'">[[NSNumber numberWithInteger:<xsl:value-of select="$value"/>] stringValue]</xsl:when>
        <xsl:when test="$type = 'long'">[[NSNumber numberWithLongLong:<xsl:value-of select="$value"/>] stringValue]</xsl:when>
        <xsl:when test="$type = 'string'"><xsl:value-of select="$value"/></xsl:when>
        <xsl:when test="$type = 'date'">[[NSNumber numberWithLongLong:<xsl:value-of select="$value"/>] stringValue]</xsl:when>
        <xsl:when test="substring($type, 1, 4) = 'Api_'"><xsl:value-of select="$value"/> == nil ? @"" : [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:[<xsl:value-of select="$value"/> serialize] options:0 error:nil] encoding:NSUTF8StringEncoding]</xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$value"/> == nil ? @"" : [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:<xsl:value-of select="$value"/> options:0 error:nil] encoding:NSUTF8StringEncoding]
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
  <xsl:template name="ResponseField">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:param name="desc"/>
    <xsl:variable name="objcName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:if test="$isList = 'true'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, strong) NSMutableArray *<xsl:value-of select="$objcName"/>;
    </xsl:if>
    <xsl:if test="$isList = 'false'">
      <xsl:choose>
        <xsl:when test="$type = 'boolean'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, assign) BOOL <xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = 'byte'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, assign) char <xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = 'char'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, assign) unsigned short <xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = 'short'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, assign) short <xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = 'double'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, assign) double <xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = 'float'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, assign) float <xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = 'int'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, assign) NSInteger <xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = 'long'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, assign) long long <xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = 'string'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, strong) NSString *<xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = 'date'">
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, assign) long long <xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:when test="$type = '&lt;T&gt;'">
@property(nonatomic, strong) ${prefix}BaseEntity *<xsl:value-of select="$objcName"/>;
</xsl:when>
        <xsl:otherwise>
/* <xsl:value-of select="$desc" /> */
@property(nonatomic, strong) ${prefix}<xsl:value-of select="$type"/> *<xsl:value-of select="$objcName"/>;
</xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
  <xsl:template name="RequiredParameterSetter">
    <xsl:param name="methodName"/>
    <xsl:if test="isRequired = 'true'">
        [self setParameter:<xsl:call-template name="ToStringValue">
      <xsl:with-param name="type">
        <xsl:call-template name="getLastName">
          <xsl:with-param name="name" select="type"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="value" select="name"/><xsl:with-param name="isList"/></xsl:call-template> withName:@"<xsl:value-of select="name"/>"];
      <xsl:if test="string-length(verifyRegex)&gt;0">  if (![_prd_<xsl:value-of select="name"></xsl:value-of> evaluateWithObject:<xsl:call-template name="ToStringValue">
      <xsl:with-param name="type">
        <xsl:call-template name="getLastName">
          <xsl:with-param name="name" select="type"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="value" select="name"/><xsl:with-param name="isList"/></xsl:call-template>]) {
            [self addVerifyErrorWithName:@"<xsl:value-of select="name"></xsl:value-of>" value:@"<xsl:value-of select="verifyMsg"></xsl:value-of>"];
        }</xsl:if></xsl:if>
  </xsl:template>
  <xsl:template name="NotRequiredParameterSetter_H">
    <xsl:if test="isRequired = 'false' and injectOnly = 'false'">
      <xsl:variable name="name" select="name"/>
      <xsl:variable name="first" select="substring(name, 1, 1)"/>
      <xsl:variable name="firstUpper" select="translate($first,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
/*
 * 当前请求的非必填参数
 * @param <xsl:value-of select="name"/><xsl:value-of select="' '"/><xsl:value-of select="description"/>
 */
- (void) set<xsl:value-of select="$firstUpper"></xsl:value-of><xsl:value-of select="substring(name, 2)"/>:(<xsl:call-template name="ParseType">
      <xsl:with-param name="type">
        <xsl:call-template name="getLastName">
          <xsl:with-param name="name" select="type"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="isList"/>
    </xsl:call-template>)<xsl:value-of select="name"/>;
    </xsl:if>
  </xsl:template>
  <xsl:template name="NotRequiredParameterSetter_M">
    <xsl:if test="isRequired = 'false' and injectOnly = 'false'">
      <xsl:variable name="name" select="name"/>
      <xsl:variable name="first" select="substring(name, 1, 1)"/>
      <xsl:variable name="firstUpper" select="translate($first,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
/*
 * 当前请求的非必填参数
 * @param <xsl:value-of select="name"/><xsl:value-of select="' '"/><xsl:value-of select="description"/>
 */
- (void) set<xsl:value-of select="$firstUpper"></xsl:value-of><xsl:value-of select="substring(name, 2)"/>:(<xsl:call-template name="ParseType">
      <xsl:with-param name="type">
        <xsl:call-template name="getLastName">
          <xsl:with-param name="name" select="type"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="isList"/>
    </xsl:call-template>)<xsl:value-of select="name"/>
{
    [self setParameter:<xsl:call-template name="ToStringValue">
    <xsl:with-param name="type">
      <xsl:call-template name="getLastName">
        <xsl:with-param name="name" select="type"/>
      </xsl:call-template>
    </xsl:with-param>
    <xsl:with-param name="value" select="name"/><xsl:with-param name="isList"/></xsl:call-template> withName:@"<xsl:value-of select="name"/>"];
    <xsl:if test="string-length(verifyRegex)&gt;0">if (<xsl:value-of select="name"/><xsl:text disable-output-escaping="yes"><![CDATA[ && ]]></xsl:text>![_prd_<xsl:value-of select="name"></xsl:value-of> evaluateWithObject:<xsl:call-template name="ToStringValue">
    <xsl:with-param name="type">
      <xsl:call-template name="getLastName">
        <xsl:with-param name="name" select="type"/>
      </xsl:call-template>
    </xsl:with-param>
    <xsl:with-param name="value" select="name"/><xsl:with-param name="isList"/></xsl:call-template>])
    {
        [self addVerifyErrorWithName:@"<xsl:value-of select="name"/>" value:@"<xsl:value-of select="verifyMsg"></xsl:value-of>"];
    }</xsl:if>
}
    </xsl:if>
  </xsl:template>
  <xsl:template name="RegexPatternField_H">
    <xsl:if test="string-length(verifyRegex)&gt;0">    NSPredicate *_prd_<xsl:value-of select="name"></xsl:value-of>;</xsl:if>
  </xsl:template>
  <xsl:template name="RegexPatternField_M">
    <xsl:if test="string-length(verifyRegex)&gt;0">_prd_<xsl:value-of select="name"></xsl:value-of> = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", @"<xsl:value-of select="verifyRegex"/>"];
    </xsl:if>
  </xsl:template>
  <xsl:template name="ErrorCode">
        /* <xsl:value-of select="desc" /> */
        case ${prefix}ApiCode_<xsl:value-of select="name"/>: {
            break;
        }</xsl:template>
  <xsl:template name="ApiCode" match="codeList">// Auto Generated.  DO NOT EDIT!

/*
 * 本类定义了接口有可能的返回值集合, 其中0为成功, 负数值为所有接口都有可能返回的通用code, 正数值是接口相关的code(请参见接口文档).
 */
typedef enum {
    <xsl:for-each select="code"><xsl:if test="isDesign='true'">
    /* <xsl:value-of select="desc"/> | 接口组名称:<xsl:value-of select="service"/> */
    ${prefix}ApiCode_<xsl:value-of select="name"/> = <xsl:value-of select="code"/>,</xsl:if>
    </xsl:for-each>
}${prefix}ApiCode;


@interface ${prefix}CodeConverter : NSObject
+ (NSString *) description:(NSInteger) code;
@end
/************ split .h and .m file ************/
#import "${prefix}ApiCode.h"

@implementation ${prefix}CodeConverter

+ (NSString *) description:(NSInteger) code
{
    switch (code)
    {<xsl:for-each select="code">
        case ${prefix}ApiCode_<xsl:value-of select="name"/>: {
          <xsl:choose>
              <xsl:when test="contains(desc,'&#xA;')">            return @"<xsl:value-of select="substring-before(desc, '&#xA;')"/>";
              </xsl:when>
              <xsl:otherwise>            return @"<xsl:value-of select="desc"/>";
              </xsl:otherwise>
          </xsl:choose>
        }</xsl:for-each>
    }
    return nil;
}

@end</xsl:template>
  <xsl:template name="resp" match="respStruct"><xsl:call-template name="ApiResponse"/></xsl:template>
  <xsl:template name="req" match="reqStruct"><xsl:call-template name="ApiResponse"/></xsl:template>
  <xsl:template name="ApiResponse">// Auto Generated.  DO NOT EDIT!

<xsl:for-each select="fieldList/field"><xsl:if test="substring(type, 1, 4) = 'Api_'">
@class ${prefix}<xsl:value-of select="type" />;</xsl:if></xsl:for-each>

#import "${prefix}BaseEntity.h"

@interface ${prefix}<xsl:value-of select="name" /> : ${prefix}BaseEntity<xsl:for-each select="fieldList/field">
    <xsl:call-template name="ResponseField">
      <xsl:with-param name="type" select="type" />
      <xsl:with-param name="name" select="name" />
      <xsl:with-param name="isList" select="isList" />
      <xsl:with-param name="desc" select="desc" />
    </xsl:call-template>
</xsl:for-each>

// 反序列化函数，用于从json字符串反序列化本类型实例
+ (${prefix}<xsl:value-of select="name" /> *) deserializeWithJsonData:(NSData *) jsonData;

// 反序列化函数，用于从json节点对象反序列化本类型实例
+ (${prefix}<xsl:value-of select="name" /> *) deserialize:(NSDictionary *)json;

// 序列化函数，用于从对象生成数据字典
- (NSMutableDictionary *) serialize;

@end
/************ split .h and .m file ************/
// Auto Generated.  DO NOT EDIT!

#import "${prefix}<xsl:value-of select="name" />.h"<xsl:for-each select="fieldList/field"><xsl:if test="substring(type, 1, 4) = 'Api_'">
#import "${prefix}<xsl:value-of select="type" />.h"</xsl:if></xsl:for-each><xsl:for-each select="fieldList/field/extInfo/keyValue/item"><xsl:if test="substring(value, 1, 4) = 'Api_'">
#import "${prefix}<xsl:value-of select="value" />.h"</xsl:if></xsl:for-each>
#import "${prefix}LocalException.h"

@implementation ${prefix}<xsl:value-of select="name" />
<xsl:if test="count(fieldList/field[name='description'])&gt;0">
@synthesize description;</xsl:if>
<xsl:if test="./fieldList/field[isList='true']">
- (id) init
{
    if (self = [super init])
    {
        <xsl:for-each select="fieldList/field[isList='true']">self.<xsl:value-of select="name" /> = [[NSMutableArray alloc] init];
        </xsl:for-each>
    }
    return (self);
}
</xsl:if>

/*
 * 反序列化函数，用于从json字符串反序列化本类型实例
 */
+ (${prefix}<xsl:value-of select="name" /> *) deserializeWithJsonData:(NSData *) jsonData
{
    NSError *error = nil;
    NSDictionary *jsonDict = [NSJSONSerialization JSONObjectWithData:jsonData
                                                             options:NSJSONReadingMutableLeaves
                                                               error:<xsl:text disable-output-escaping="yes"><![CDATA[&]]></xsl:text>error];
    if (error)
    {
        @throw [[${prefix}LocalException alloc] initWithCode:LocalExceptionCode_DESERIALIZE_ERR message:@"${prefix}<xsl:value-of select="name" /> 反序列化失败"];
    }
    return [${prefix}<xsl:value-of select="name" /> deserialize:jsonDict];
}

/*
 * 反序列化函数，用于从json节点对象反序列化本类型实例
 */
+ (${prefix}<xsl:value-of select="name" /> *) deserialize:(NSDictionary *)json
{
    if (!([json isKindOfClass:[NSNull class]] || json == nil)) {
        ${prefix}<xsl:value-of select="name" /> *result = [[${prefix}<xsl:value-of select="name" /> alloc] init];
      <xsl:choose>
          <xsl:when test="contains(name,'Api_RawString') or contains(name,'Api_JSONString')">        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:json options:kNilOptions error:NULL];
        result.value = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
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
    return nil;
}

/*
 * 序列化函数，用于从对象生成数据字典
 */
- (NSMutableDictionary *) serialize
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    <xsl:for-each select="fieldList/field">
      <xsl:call-template name="SerializeField">
        <xsl:with-param name="type" select="type" />
        <xsl:with-param name="name" select="name" />
        <xsl:with-param name="isList" select="isList" />
      </xsl:call-template>
    </xsl:for-each>
    return dict;
}

@end
  </xsl:template>
  <xsl:template name="DeserializeField">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:variable name="objcName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:choose>
      <xsl:when test="$isList='true'">
        /* <xsl:value-of select="desc" /> */
        NSArray *<xsl:value-of select="$objcName" />Array = [json objectForKey:@"<xsl:value-of select="name" />"];
        if (<xsl:value-of select="$objcName" />Array<xsl:text disable-output-escaping="yes"><![CDATA[ && ]]></xsl:text>![<xsl:value-of select="$objcName" />Array isKindOfClass:[NSNull class]]) {
            for (NSInteger i = 0; i<xsl:text disable-output-escaping="yes"><![CDATA[ < ]]></xsl:text>[<xsl:value-of select="$objcName" />Array count]; i++){
            <xsl:call-template name="JsonGetter">
              <xsl:with-param name="type" select="type" />
              <xsl:with-param name="name" select="name" />
              <xsl:with-param name="isList" select="isList" />
            </xsl:call-template>
            }
        }
      </xsl:when>
      <xsl:otherwise>
          <xsl:choose>
              <xsl:when test="$type!='&lt;T&gt;'">
        /* <xsl:value-of select="desc" /> */<xsl:text disable-output-escaping="yes">&#xD;&#xA;<![CDATA[    ]]></xsl:text>
        <xsl:call-template name="JsonGetter">
          <xsl:with-param name="type" select="type" />
          <xsl:with-param name="name" select="name" />
          <xsl:with-param name="isList" select="isList" />
        </xsl:call-template>
              </xsl:when>
          </xsl:choose>
      </xsl:otherwise></xsl:choose></xsl:template>
  <xsl:template name="JsonGetter">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:variable name="objcName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:choose>
      <xsl:when test="$isList = 'true'">
        <xsl:choose>
          <xsl:when test="$type = 'boolean'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'byte'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'char'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'short'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'float'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'double'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'int'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'long'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'string'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'date'">    [result.<xsl:value-of select="$objcName" /> addObject:[<xsl:value-of select="$objcName" />Array objectAtIndex:i]];</xsl:when>
          <xsl:when test="$type = 'Api_DynamicEntity'">    NSDictionary *dict = [<xsl:value-of select="$objcName" />Array objectAtIndex:i];
                if (dict<xsl:text disable-output-escaping="yes"><![CDATA[ && ]]></xsl:text>![dict isKindOfClass:[NSNull class]]) {
                    ${prefix}<xsl:value-of select="type" /> *e = [${prefix}<xsl:value-of select="type" /> deserialize:dict];
                    [result.<xsl:value-of select="$objcName" /> addObject:e];
                <xsl:for-each select="extInfo/keyValue/item"><xsl:if test="position() != 1"> else </xsl:if>if ([@"<xsl:value-of select="key"></xsl:value-of>" isEqualToString:e.typeName]) {
                    e.entity = [${prefix}<xsl:value-of select="value" /> deserialize:[dict objectForKey:@"entity"]];
                }</xsl:for-each>
                }</xsl:when>
          <xsl:otherwise>    NSDictionary *dict = [<xsl:value-of select="$objcName" />Array objectAtIndex:i];
                if (dict<xsl:text disable-output-escaping="yes"><![CDATA[ && ]]></xsl:text>![dict isKindOfClass:[NSNull class]]) {
                    [result.<xsl:value-of select="$objcName" /> addObject:[${prefix}<xsl:value-of select="type" /> deserialize:dict]];
                }</xsl:otherwise></xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
              <xsl:when test="$type = 'boolean'">    result.<xsl:value-of select="$objcName" /> = [[json objectForKey:@"<xsl:value-of select="name" />"] boolValue];
              </xsl:when>
              <xsl:when test="$type = 'byte'">    result.<xsl:value-of select="$objcName" /> = [(NSNumber *)[json objectForKey:@"<xsl:value-of select="name" />"] charValue];
              </xsl:when>
              <xsl:when test="$type = 'char'">    {
            NSString * v = [json objectForKey:@"<xsl:value-of select="name" />"];
            if (v != nil <xsl:text disable-output-escaping="yes"><![CDATA[ && ]]></xsl:text> v.length == 1) {
                result.charValue = [v characterAtIndex:0];
            }
        }
              </xsl:when>
              <xsl:when test="$type = 'short'">    result.<xsl:value-of select="$objcName" /> = [[json objectForKey:@"<xsl:value-of select="name" />"] shortValue];
              </xsl:when>
              <xsl:when test="$type = 'float'">    result.<xsl:value-of select="$objcName" /> = (int)(([[json objectForKey:@"<xsl:value-of select="name" />"] floatValue] + 0.00005) * 100) / 100.00;
              </xsl:when>
              <xsl:when test="$type = 'double'">    result.<xsl:value-of select="$objcName" /> = (int)(([[json objectForKey:@"<xsl:value-of select="name" />"] doubleValue] + 0.00005) * 100) / 100.00;
              </xsl:when>
              <xsl:when test="$type = 'int'">    result.<xsl:value-of select="$objcName" /> = [[json objectForKey:@"<xsl:value-of select="name" />"] integerValue];
              </xsl:when>
              <xsl:when test="$type = 'long'">    result.<xsl:value-of select="$objcName" /> = [[json objectForKey:@"<xsl:value-of select="name" />"] longLongValue];
              </xsl:when>
              <xsl:when test="$type = 'string'">    result.<xsl:value-of select="$objcName" /> = [json objectForKey:@"<xsl:value-of select="name" />"];
        if ([result.<xsl:value-of select="$objcName" /> isKindOfClass:[NSNull class]]) { result.<xsl:value-of select="$objcName" /> = nil; }
              </xsl:when>
              <xsl:when test="$type = 'date'">    result.<xsl:value-of select="$objcName" /> = [[json objectForKey:@"<xsl:value-of select="name" />"] longLongValue];
              </xsl:when>
              <xsl:when test="$type = 'Api_DynamicEntity'">    NSDictionary *de = [json objectForKey:@"<xsl:value-of select="name" />"];
        result.<xsl:value-of select="$objcName" /> = [${prefix}<xsl:value-of select="type" /> deserialize:de];
        if ([result.<xsl:value-of select="$objcName" /> isKindOfClass:[NSNull class]]) {
            result.<xsl:value-of select="$objcName" /> = nil;
        } else {
            <xsl:for-each select="extInfo/keyValue/item"><xsl:if test="position() != 1"> else </xsl:if>if ([@"<xsl:value-of select="key"></xsl:value-of>" isEqualToString:result.<xsl:value-of select="$objcName" />.typeName]) {
                result.<xsl:value-of select="$objcName" />.entity = [${prefix}<xsl:value-of select="value" /> deserialize:[de objectForKey:@"entity"]];
            }</xsl:for-each>
        }
              </xsl:when>
              <xsl:otherwise>    result.<xsl:value-of select="$objcName" /> = [${prefix}<xsl:value-of select="type" /> deserialize:[json objectForKey:@"<xsl:value-of select="name" />"]];
        if ([result.<xsl:value-of select="$objcName" /> isKindOfClass:[NSNull class]]) { result.<xsl:value-of select="$objcName" /> = nil; }
              </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose></xsl:template>
  <xsl:template name="renameKeyword">
      <xsl:param name="name"/>
      <xsl:choose>
          <xsl:when test="$name = 'id'">identify</xsl:when>
          <xsl:when test="$name = 'operator'">optname</xsl:when>
          <xsl:when test="substring($name,1,3) = 'new'">a<xsl:value-of select="$name"/></xsl:when>
          <xsl:otherwise><xsl:value-of select="$name"/></xsl:otherwise>
      </xsl:choose>
  </xsl:template>
  <xsl:template name="SerializeField">
    <xsl:param name="type"/>
    <xsl:param name="name"/>
    <xsl:param name="isList"/>
    <xsl:variable name="objcName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:choose>
      <xsl:when test="$isList='true'">
    /* <xsl:value-of select="desc" /> */
    if (self.<xsl:value-of select="$objcName" />
          <xsl:text disable-output-escaping="yes"><![CDATA[ && ]]></xsl:text>![self.<xsl:value-of select="$objcName" /> isKindOfClass:[NSNull class]]) {
        NSMutableArray *<xsl:value-of select="$objcName" />Array = [NSMutableArray array];
        for (NSInteger i = 0; i<xsl:text disable-output-escaping="yes"><![CDATA[ < ]]></xsl:text>[self.<xsl:value-of select="$objcName" /> count]; i++) {
            <xsl:choose>
                <xsl:when test="$type = 'boolean'">NSNumber</xsl:when>
                <xsl:when test="$type = 'byte'">NSNumber</xsl:when>
                <xsl:when test="$type = 'char'">NSNumber</xsl:when>
                <xsl:when test="$type = 'short'">NSNumber</xsl:when>
                <xsl:when test="$type = 'float'">NSNumber</xsl:when>
                <xsl:when test="$type = 'double'">NSNumber</xsl:when>
                <xsl:when test="$type = 'int'">NSNumber</xsl:when>
                <xsl:when test="$type = 'long'">NSNumber</xsl:when>
                <xsl:when test="$type = 'string'">NSString</xsl:when>
                <xsl:when test="$type = 'date'">NSNumber</xsl:when>
                <xsl:otherwise>${prefix}<xsl:value-of select="$type" /></xsl:otherwise>
            </xsl:choose> *value = self.<xsl:value-of select="$objcName" />[i];
            if (value<xsl:text disable-output-escaping="yes"><![CDATA[ && ]]></xsl:text>![value isKindOfClass:[NSNull class]]) {
                <xsl:call-template name="JsonSetter">
                  <xsl:with-param name="type" select="type" />
                  <xsl:with-param name="name" select="name" />
                  <xsl:with-param name="isList" select="isList" />
                </xsl:call-template>
            }
        }
        [dict setObject:<xsl:value-of select="$objcName" />Array forKey:@"<xsl:value-of select="$name" />"];
    }
      </xsl:when>
      <xsl:otherwise>
    /* <xsl:value-of select="desc" /> */<xsl:text disable-output-escaping="yes">&#xD;&#xA;<![CDATA[    ]]></xsl:text>
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
    <xsl:variable name="objcName"><xsl:call-template name="renameKeyword"><xsl:with-param name="name" select="$name"/></xsl:call-template></xsl:variable>
    <xsl:choose>
      <xsl:when test="$isList = 'true'">
        <xsl:choose>
          <xsl:when test="$type = 'boolean'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:when test="$type = 'byte'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:when test="$type = 'char'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:when test="$type = 'short'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:when test="$type = 'float'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:when test="$type = 'double'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:when test="$type = 'int'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:when test="$type = 'long'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:when test="$type = 'string'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:when test="$type = 'date'">[<xsl:value-of select="$objcName" />Array addObject:value];</xsl:when>
          <xsl:otherwise>if (value != nil) {
              [<xsl:value-of select="$objcName" />Array addObject:[value serialize]];
              }</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$type = 'boolean'">[dict setObject:[NSNumber numberWithBool:self.<xsl:value-of select="$objcName" />] forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:when test="$type = 'byte'">[dict setObject:[NSNumber numberWithChar:self.<xsl:value-of select="$objcName" />] forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:when test="$type = 'char'">[dict setObject:[NSNumber numberWithUnsignedShort:self.<xsl:value-of select="$objcName" />] forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:when test="$type = 'short'">[dict setObject:[NSNumber numberWithShort:self.<xsl:value-of select="$objcName" />] forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:when test="$type = 'float'">[dict setObject:[NSNumber numberWithFloat:self.<xsl:value-of select="$objcName" />] forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:when test="$type = 'double'">[dict setObject:[NSNumber numberWithDouble:self.<xsl:value-of select="$objcName" />] forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:when test="$type = 'int'">[dict setObject:[NSNumber numberWithInteger:self.<xsl:value-of select="$objcName" />] forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:when test="$type = 'long'">[dict setObject:[NSNumber numberWithLongLong:self.<xsl:value-of select="$objcName" />] forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:when test="$type = 'string'">if (self.<xsl:value-of select="$objcName" /> != nil) [dict setObject:self.<xsl:value-of select="$objcName" /> forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:when test="$type = 'date'">[dict setObject:[NSNumber numberWithLongLong:self.<xsl:value-of select="$objcName" />] forKey:@"<xsl:value-of select="name" />"];
          </xsl:when>
          <xsl:otherwise>if (self.<xsl:value-of select="$objcName" /> != nil) [dict setObject:[self.<xsl:value-of select="$objcName" /> serialize] forKey:@"<xsl:value-of select="name" />"];
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose></xsl:template>
</xsl:stylesheet>

package net.pocrd.core.generator;

import net.pocrd.annotation.ConsoleArgument;
import net.pocrd.annotation.ConsoleJoinPoint;
import net.pocrd.annotation.ConsoleOption;
import net.pocrd.define.ConstField;
import net.pocrd.entity.CompileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;

/**
 * Created by guankaiqiang521 on 2014/9/25.
 */
@ConsoleJoinPoint(command = "api-jssdk-gen", desc = "生成javascript客户端接口文件")
public class ApiSdkJavaScriptGenerator extends ApiCodeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ApiSdkJavaGenerator.class);

    private String xslt;
    private String output;
    private String packagePrefix;

    private ApiSdkJavaScriptGenerator(String xslt, String output, String packagePrefix) {
        this.xslt = xslt;
        this.output = output;
        this.packagePrefix = packagePrefix;
    }

    public static class Builder {
        private String xslt          = null;
        private String output        = "~/tmp";
        private String packagePrefix = "net.pocrd.m";

        public Builder setXsltPath(String xslt) {
            this.xslt = xslt;
            return this;
        }

        public Builder setOutputPath(String output) {
            this.output = output;
            return this;
        }

        public Builder setPackagePrefix(String packagePrefix) {
            this.packagePrefix = packagePrefix;
            return this;
        }

        public ApiSdkJavaScriptGenerator build() {
            return new ApiSdkJavaScriptGenerator(xslt, output, packagePrefix);
        }
    }

    @Override
    protected InputStream customizeXslt(InputStream xslt) {
        BufferedReader reader = null;
        InputStream swapStream = null;
        try {
            reader = new BufferedReader(new InputStreamReader(xslt, ConstField.UTF8));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line.replace("${pkg}", packagePrefix) + "\r\n");
            }
            if (CompileConfig.isDebug) {
                System.out.println(out.toString());   //Prints the string content read from input stream
            }
            swapStream = new ByteArrayInputStream(out.toString().getBytes(ConstField.UTF8));
        } catch (Exception e) {
            logger.error("transform file failed!", e);
            throw new RuntimeException("transform file failed!", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (xslt != null) {
                    xslt.close();
                }
            } catch (IOException e) {
                logger.error("close failed!", e);
                throw new RuntimeException("close failed!", e);
            }
        }
        return swapStream;
    }

    @Override
    public void generate(InputStream apiInfo) {
        InputStream defaultXslt = null;
        try {
            defaultXslt = ApiSdkJavaScriptGenerator.class.getResourceAsStream("/xslt/js.xslt");
            Transformer trans = TransformerFactory.newInstance().newTransformer(
                    getXsltSource(xslt, new StreamSource(customizeXslt(defaultXslt))));
            trans.setOutputProperty("omit-xml-declaration", "yes");
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(apiInfo);
            generateJsRequest(trans, document);
        } catch (Exception e) {
            logger.error("generate failed!", e);
            throw new RuntimeException("generate failed!", e);
        } finally {
            try {
                if (defaultXslt != null) {
                    defaultXslt.close();
                }
                if (apiInfo != null) {
                    apiInfo.close();
                }
            } catch (IOException e) {
                logger.error("close failed!", e);
                throw new RuntimeException("close failed!", e);
            }
        }
    }

    private void generateJsRequest(Transformer trans, Document doc) {
        try {
            XPath path = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList)path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
            int len = nl.getLength();
            String outputPath = output;
            FileUtil.recreateDir(outputPath);
            for (int i = 0; i < len; i++) {
                Element e = (Element)nl.item(i);
                String methodName = e.getElementsByTagName("methodName").item(0).getFirstChild().getNodeValue();
                String moduleName = e.getElementsByTagName("groupName").item(0).getFirstChild().getNodeValue();
                File dir = new File(outputPath + File.separator + moduleName);
                if (!dir.exists()) {
                    if (dir.mkdirs()) {
                        logger.info("creade folder:{}", dir.getAbsoluteFile());
                        System.out.println("creade folder:" + dir.getAbsolutePath());
                    } else {
                        logger.error("create folder:{} failed!", dir.getName());
                        throw new RuntimeException(String.format("create folder:%s failed!", path));
                    }
                }
                String fileName =
                        outputPath + File.separator + moduleName + File.separator + packagePrefix + ".api."
                                + methodName + ".js";
                File source = new File(fileName);
                if (!source.exists()) {
                    trans.transform(new DOMSource(e), new StreamResult(source));
                }
            }
            Node n = (Node)path.evaluate("//Document/codeList", doc, XPathConstants.NODE);
            trans.transform(new DOMSource(n),
                    new StreamResult(outputPath + File.separator + packagePrefix + ".api.apiCode.js"));
        } catch (Exception e) {
            logger.error("generate api js client failed", e);
            throw new RuntimeException("generate api js client failed", e);
        }
    }

    public static void execute(
            @ConsoleOption(name = "g", desc = "需要生成的api组名", sample = "groupA,groupB") String groups,
            @ConsoleOption(name = "s", desc = "需要生成的安全级别", sample = "None,UserLogin") String securityTypes,
            @ConsoleOption(name = "ra", desc = "不生成的接口列表", sample = "user.getInfo,device.register") String rejectApis,
            @ConsoleOption(name = "aa", desc = "需要生成的接口列表, 半角逗号分隔", sample = "user.getInfo,device.register") String acceptApis,
            @ConsoleOption(name = "o", desc = "输出文件目录") String outputPath,
            @ConsoleOption(name = "jar", desc = "根据jar文件生成时给出jar文件地址") String jarFile,
            @ConsoleOption(name = "url", desc = "根据在线文档生成时给出文档url", sample = "http://www.pocrd.net/info.api?raw") String url,
            @ConsoleArgument(name = "prefix", desc = "生成代码的文件名前缀", sample = "net.pocrd.js") String prefix
    ) {
        ApiCodeGenerator gen = new Builder().setPackagePrefix(prefix).setOutputPath(outputPath).build();
        gen.setApiGroups(groups);
        gen.setSecurityTypes(securityTypes);
        gen.setRejectApis(rejectApis);
        gen.setAcceptApis(acceptApis);
        if (jarFile != null) {
            gen.generateViaJar(jarFile);
        } else if (url != null) {
            gen.generateWithApiInfo(url);
        } else {
            throw new RuntimeException("either jar or url must be specified.");
        }
    }
}

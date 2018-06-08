package net.pocrd.core.generator;

import net.pocrd.annotation.ConsoleArgument;
import net.pocrd.annotation.ConsoleJoinPoint;
import net.pocrd.annotation.ConsoleOption;
import net.pocrd.define.ConstField;
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
@ConsoleJoinPoint(command = "api-csharp-gen", desc = "生成C#客户端接口/实体文件")
public class ApiSdkCSharpGenerator extends ApiCodeGenerator {
    private static final Logger logger  = LoggerFactory.getLogger(ApiSdkCSharpGenerator.class);
    private static final String REQUEST = "Request";
    private static final String RESP    = "Response";
    private static final String API     = "API";

    private String xslt;
    private String output;
    private String packagePrefix;

    private ApiSdkCSharpGenerator(String xslt, String output, String packagePrefix) {
        this.xslt = xslt;
        this.output = output;
        this.packagePrefix = packagePrefix;
    }

    public static class Builder {
        private String xslt          = null;
        private String output        = "~/tmp";
        private String packagePrefix = "PoCRD.Client";

        public Builder setXsltPath(String xslt) {
            this.xslt = xslt;
            return this;
        }

        public Builder setOutputPath(String output) {
            this.output = output;
            return this;
        }

        public Builder setPackagePrefix(String packagePrefix) {
            if (packagePrefix != null) { this.packagePrefix = packagePrefix; }
            return this;
        }

        public ApiSdkCSharpGenerator build() {
            return new ApiSdkCSharpGenerator(xslt, output, packagePrefix);
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
                out.append(line.replace("${pkg}", packagePrefix) + "\r");
            }
            System.out.println(out.toString());   //Prints the string content read from input stream
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
    public void generate(InputStream inputStream) {
        InputStream defaultXslt = null;
        try {
            defaultXslt = ApiSdkCSharpGenerator.class.getResourceAsStream("/xslt/csharp.xslt");
            Transformer trans = TransformerFactory.newInstance().newTransformer(
                    getXsltSource(xslt, new StreamSource(customizeXslt(defaultXslt))));
            trans.setOutputProperty("omit-xml-declaration", "yes");
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            generateEntity(trans, document);
            generateRequest(trans, document);
        } catch (Exception e) {
            logger.error("generate failed!", e);
            throw new RuntimeException("generate failed!", e);
        } finally {
            try {
                if (defaultXslt != null) {
                    defaultXslt.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("close failed!", e);
                throw new RuntimeException("close failed!", e);
            }
        }
    }

    private void generateEntity(Transformer trans, Document doc) {
        try {

            XPath path = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList)path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
            int len = nl.getLength();
            String outputPath = output + File.separator + API + File.separator + RESP;
            FileUtil.recreateDir(outputPath);
            for (int i = 0; i < len; i++) {
                NodeList pl = ((Element)((Element)nl.item(i)).getElementsByTagName("respStructList").item(0))
                        .getElementsByTagName("respStruct");
                int l = pl.getLength();
                for (int j = 0; j < l; j++) {
                    Element e = (Element)pl.item(j);
                    String className = e.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
                    String fileName = outputPath + File.separator + className + ".cs";
                    File source = new File(fileName);
                    if (!source.exists()) {
                        trans.transform(new DOMSource(e), new StreamResult(source));
                    }
                }

                NodeList ns = ((Element)nl.item(i)).getElementsByTagName("reqStructList");
                if (ns != null && ns.getLength() != 0) {
                    NodeList rl = ((Element)ns.item(0)).getElementsByTagName("reqStruct");
                    l = rl.getLength();
                    for (int j = 0; j < l; j++) {
                        Element e = (Element)rl.item(j);
                        String className = e.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
                        String fileName = outputPath + File.separator + className + ".cs";
                        File source = new File(fileName);
                        if (!source.exists()) {
                            trans.transform(new DOMSource(e), new StreamResult(source));
                        }
                    }
                }
            }
            //通用返回数据结构
            nl = (NodeList)path.evaluate("//Document/respStructList/respStruct", doc, XPathConstants.NODESET);
            len = nl.getLength();
            for (int i = 0; i < len; i++) {
                Element e = (Element)nl.item(i);
                String className = e.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
                String fileName = outputPath + File.separator + className + ".cs";
                File source = new File(fileName);
                if (!source.exists()) {
                    trans.transform(new DOMSource(e), new StreamResult(source));
                }
            }

        } catch (Exception e) {
            logger.error("generate api csharp client failed", e);
            throw new RuntimeException("generate api csharp client failed", e);
        }
    }

    private void generateRequest(Transformer trans, Document doc) {
        try {
            XPath path = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList)path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
            int len = nl.getLength();
            String outputPath = output + File.separator + API + File.separator + REQUEST;
            FileUtil.recreateDir(outputPath);
            for (int i = 0; i < len; i++) {
                Element e = (Element)nl.item(i);
                String methodName = e.getElementsByTagName("methodName").item(0).getFirstChild().getNodeValue();
                int index = methodName.indexOf('.');
                methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1, index) + "_"
                        + methodName.substring(index + 1,
                        index + 2).toUpperCase() + methodName.substring(
                        index + 2);
                String fileName = outputPath + File.separator + methodName + ".cs";
                File source = new File(fileName);
                if (!source.exists()) {
                    trans.transform(new DOMSource(e), new StreamResult(source));
                }
            }
            Node n = (Node)path.evaluate("//Document/codeList", doc, XPathConstants.NODE);
            trans.transform(new DOMSource(n), new StreamResult(outputPath + File.separator + "ApiCode.cs"));
        } catch (Exception e) {
            logger.error("generate api csharp client failed", e);
            throw new RuntimeException("generate api csharp client failed", e);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            if ("generateViaJar".equals(args[0]) && args.length == 3 && args[1].length() > 0) {
                new ApiSdkCSharpGenerator.Builder().setOutputPath(args[2]).build().generateViaJar(args[1]);
                return;
            }

            if ("generateViaUrl".equals(args[0]) && args.length == 3 && args[1].length() > 0) {
                new ApiSdkCSharpGenerator.Builder().setOutputPath(args[2]).build().generateWithApiInfo(args[1]);
                return;
            }
        }

        throw new RuntimeException("error parameter.  args[0]:generateViaJar  args[1]:jar path  args[2]:output path");
    }

    public static void execute(
            @ConsoleOption(name = "g", desc = "需要生成的api组名", sample = "groupA,groupB") String groups,
            @ConsoleOption(name = "s", desc = "需要生成的安全级别", sample = "None,UserLogin") String securityTypes,
            @ConsoleOption(name = "ra", desc = "不生成的接口列表", sample = "user.getInfo,device.register") String rejectApis,
            @ConsoleOption(name = "aa", desc = "需要生成的接口列表, 半角逗号分隔", sample = "user.getInfo,device.register") String acceptApis,
            @ConsoleOption(name = "o", desc = "输出文件目录") String outputPath,
            @ConsoleOption(name = "jar", desc = "根据jar文件生成时给出jar文件地址") String jarFile,
            @ConsoleOption(name = "url", desc = "根据在线文档生成时给出文档url", sample = "http://api-stage.yit.com/apigw/info.api?raw")
                    String url,
            @ConsoleOption(name = "xsl", desc = "指定代码生成模板", sample = "http://api-stage.yit.com/apigw/info.api?raw")
                    String xsltPath,
            @ConsoleArgument(name = "package", desc = "生成代码的C#命名空间", sample = "PoCRD.App") String packageName
    ) {
        ApiCodeGenerator gen = new Builder().setXsltPath(xsltPath).setPackagePrefix(packageName).setOutputPath(
                outputPath == null ? "." : outputPath)
                .build();
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

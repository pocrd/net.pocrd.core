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
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.HashSet;

/**
 * Created by guankaiqiang521 on 2014/9/25.
 */
@ConsoleJoinPoint(command = "api-oc-gen", desc = "生成Objective-C客户端接口文件")
public class ApiSdkObjectiveCGenerator extends ApiCodeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ApiSdkObjectiveCGenerator.class);

    private String xslt;
    private String output;
    private String classPrefix;

    private ApiSdkObjectiveCGenerator(String xslt, String output, String classPrefix) {
        this.xslt = xslt;
        this.output = output;
        this.classPrefix = classPrefix;
    }

    public static class Builder {
        private String xslt        = null;
        private String output      = "~/tmp";
        private String classPrefix = "PoC";

        public Builder setXsltPath(String xslt) {
            this.xslt = xslt;
            return this;
        }

        public Builder setOutputPath(String output) {
            this.output = output;
            return this;
        }

        public Builder setClassPrefix(String classPrefix) {
            this.classPrefix = classPrefix;
            return this;
        }

        public ApiSdkObjectiveCGenerator build() {
            return new ApiSdkObjectiveCGenerator(xslt, output, classPrefix);
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
                out.append(line.replace("${prefix}", classPrefix) + "\r\n");
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
            defaultXslt = ApiSdkObjectiveCGenerator.class.getResourceAsStream("/xslt/objc.xslt");
            Transformer trans = TransformerFactory.newInstance().newTransformer(
                    getXsltSource(xslt, new StreamSource(customizeXslt(defaultXslt))));
            trans.setOutputProperty("omit-xml-declaration", "yes");
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(apiInfo);
            generateObjcEntity(trans, document);
            generateObjcRequest(trans, document);
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

    private static final String RESP           = "Response";
    private static final String REQ            = "Request";
    private static final String SPLITER        = "/************ split .h and .m file ************/";
    private static final int    SPLITER_LENGTH = SPLITER.length();

    /**
     * out put resource to file
     *
     * @param name     文件名称
     * @param resource 数据
     */
    static void output2File(String name, String resource) {
        File source = new File(name);
        if (!source.exists()) {
            boolean isSuccess = false;
            try {
                isSuccess = source.createNewFile();
            } catch (IOException e) {
                logger.error("can not create file!fileName:" + name);
                throw new RuntimeException("can not create file!fileName:" + name, e);
            }
            if (isSuccess) {
                OutputStreamWriter fw = null;
                try {
                    fw = new OutputStreamWriter(new FileOutputStream(source), "UTF-8");
                    fw.write(resource);
                } catch (Exception e) {
                    logger.error("write file failed!fileName:" + name);
                    throw new RuntimeException("write file failed!fileName:" + name, e);
                } finally {
                    if (fw != null) {
                        try {
                            fw.close();
                        } catch (IOException e) {
                            logger.error("close file failed!fileName:" + name);
                            throw new RuntimeException("close file failed!fileName:" + name, e);
                        }
                    }
                }
            } else {
                logger.error("create file failed! file name:" + name);
                throw new RuntimeException("can not create file!fileName:" + name);
            }
        }
    }

    /**
     * 生成对应的h和m文件
     *
     * @param name   文件名不含.h .m后缀
     * @param reader 源代码
     */
    private void generateObjcClass(String name, BufferedReader reader) {
        String hfileName = name + ".h";
        String mfileName = name + ".m";
        String line = null;
        StringBuilder code = new StringBuilder();
        try {
            line = reader.readLine();
            HashSet<String> importSet = new HashSet<>();
            while (line != null) {
                if (SPLITER.equals(line.trim())) {
                    output2File(hfileName, code.toString());
                    code.setLength(0);
                    importSet.clear();
                } else {
                    if (line.startsWith("#import ")) {
                        if (!importSet.contains(line)) {
                            code.append(line).append(System.lineSeparator());
                            importSet.add(line);
                        }
                    } else {
                        code.append(line).append(System.lineSeparator());
                    }
                }
                line = reader.readLine();
                if (line == null) {
                    output2File(mfileName, code.toString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("generateObjcClass failed. " + name, e);
        }
    }

    private void generateObjcEntity(Transformer trans, Document doc) {
        XPath path = XPathFactory.newInstance().newXPath();
        NodeList nl = null;
        try {
            nl = (NodeList)path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        int len = nl.getLength();
        String outputPath = output + File.separator + RESP + File.separator;
        FileUtil.recreateDir(outputPath);
        HashSet<String> respSet = new HashSet();
        for (int i = 0; i < len; i++) {
            //返回结构体
            NodeList pl = ((Element)((Element)nl.item(i)).getElementsByTagName("respStructList").item(0)).getElementsByTagName("respStruct");
            int l = pl.getLength();
            for (int j = 0; j < l; j++) {
                Element e = (Element)pl.item(j);
                String className = classPrefix + e.getElementsByTagName("name").item(
                        0).getFirstChild().getNodeValue();
                String fileName = outputPath + className;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
                try {
                    trans.transform(new DOMSource(e), new StreamResult(bw));
                } catch (TransformerException e1) {
                    logger.error("transformer failed! class name:" + className, e1);
                    throw new RuntimeException("transformer failed! class name:" + className, e1);
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
                generateObjcClass(fileName, br);
                respSet.add(className + ".h");
            }
            //请求结构体
            NodeList ns = ((Element)nl.item(i)).getElementsByTagName("reqStructList");
            if (ns != null && ns.getLength() != 0) {
                pl = ((Element)ns.item(0)).getElementsByTagName("reqStruct");
                l = pl.getLength();
                for (int j = 0; j < l; j++) {
                    Element e = (Element)pl.item(j);
                    String className = classPrefix + e.getElementsByTagName("name").item(
                            0).getFirstChild().getNodeValue();
                    String fileName = outputPath + className;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
                    try {
                        trans.transform(new DOMSource(e), new StreamResult(bw));
                    } catch (TransformerException e1) {
                        logger.error("transformer failed!class name:" + className, e1);
                        throw new RuntimeException("transformer failed!class name:" + className, e1);
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
                    generateObjcClass(fileName, br);
                    respSet.add(className + ".h");
                }
            }
        }
        //通用返回对象结构体
        try {
            nl = (NodeList)path.evaluate("//Document/respStructList/respStruct", doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        len = nl.getLength();
        for (int i = 0; i < len; i++) {
            Element e = (Element)nl.item(i);
            String className = classPrefix + e.getElementsByTagName("name").item(
                    0).getFirstChild().getNodeValue();
            String fileName = outputPath + className;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
            try {
                trans.transform(new DOMSource(e), new StreamResult(bw));
            } catch (TransformerException e1) {
                logger.error("transformer failed!class name:" + className, e1);
                throw new RuntimeException("transformer failed!class name:" + className, e1);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
            generateObjcClass(fileName, br);
            respSet.add(className + ".h");
        }
        //ApiResponseInclude.h文件生成
        String fileName = outputPath + classPrefix + "ApiResponseInclude.h";
        File source = new File(fileName);
        OutputStreamWriter fw = null;
        try {
            fw = new OutputStreamWriter(new FileOutputStream(source), "UTF-8");
            StringWriter sw = new StringWriter().append(
                    "// Auto Generated.  DO NOT EDIT!\n#ifndef " + classPrefix
                            + "ApiResponseInclude_h\n#define " + classPrefix + "ApiResponseInclude_h\n");
            for (String resp : respSet) {
                sw.append("#import \"" + resp + "\"\n");
            }
            sw.append("#endif\n");
            fw.write(sw.toString());
        } catch (Exception e) {
            logger.error("create ApiResponseInclude.h failed!", e);
            throw new RuntimeException("create ApiResponseInclude.h failed!", e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    logger.error("close file failed!", e);
                    throw new RuntimeException("close file failed!", e);
                }
            }
        }
    }

    private void generateObjcRequest(Transformer trans, Document doc) {
        XPath path = XPathFactory.newInstance().newXPath();
        //生成request
        NodeList nl = null;
        try {
            nl = (NodeList)path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        int len = nl.getLength();
        String outputPath = output + File.separator + REQ + File.separator;
        FileUtil.recreateDir(outputPath);
        HashSet<String> reqSet = new HashSet<String>();
        for (int i = 0; i < len; i++) {
            Element e = (Element)nl.item(i);
            String methodName = e.getElementsByTagName("methodName").item(0).getFirstChild().getNodeValue();
            int index = methodName.indexOf('.');
            methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1, index) + "_" + methodName.substring(index + 1,
                    index + 2).toUpperCase() + methodName.substring(
                    index + 2);
            String className = classPrefix + methodName;
            String fileName = outputPath + className;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
            try {
                trans.transform(new DOMSource(e), new StreamResult(bw));
            } catch (TransformerException e1) {
                logger.error("transformer failed!class name:" + className, e1);
                throw new RuntimeException("transformer failed!class name:" + className, e1);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
            generateObjcClass(fileName, br);
            reqSet.add(className + ".h");
        }
        //生成ApiCode
        Node n = null;
        try {
            n = (Node)path.evaluate("//Document/codeList", doc, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        String className = classPrefix + "ApiCode";
        String fileName = outputPath + className;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
        try {
            trans.transform(new DOMSource(n), new StreamResult(bw));
        } catch (TransformerException e) {
            logger.error("transformer failed!class name:" + className, e);
            throw new RuntimeException("transformer failed!class name:" + className, e);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
        generateObjcClass(fileName, br);
        reqSet.add(className + ".h");
        //生成ApiRequestInclude.h
        fileName = outputPath + classPrefix + "ApiRequestInclude.h";
        File source = new File(fileName);
        OutputStreamWriter fw = null;
        try {
            fw = new OutputStreamWriter(new FileOutputStream(source), "UTF-8");
            StringWriter sw = new StringWriter().
                    append("// Auto Generated.  DO NOT EDIT!\n" +
                            "#ifndef " + classPrefix + "ApiRequestInclude_h\n" +
                            "#define " + classPrefix + "ApiRequestInclude_h\n");
            for (String req : reqSet) {
                sw.append("#import \"" + req + "\"\n");
            }
            sw.append("#endif\n");
            fw.write(sw.toString());
        } catch (Exception e) {
            logger.error("create 生成ApiRequestInclude.h failed!", e);
            throw new RuntimeException("create 生成ApiRequestInclude.h failed!", e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    logger.error("close file failed!", e);
                    throw new RuntimeException("close file failed!", e);
                }
            }
        }
    }

    public static void execute(
            @ConsoleOption(name = "g", desc = "需要生成的api组名", sample = "groupA,groupB") String groups,
            @ConsoleOption(name = "s", desc = "需要生成的安全级别", sample = "None,UserLogin") String securityTypes,
            @ConsoleOption(name = "ra", desc = "不生成的接口列表, 半角逗号分隔", sample = "user.getInfo,device.register") String rejectApis,
            @ConsoleOption(name = "o", desc = "输出文件目录") String outputPath,
            @ConsoleOption(name = "jar", desc = "根据jar文件生成时给出jar文件地址") String jarFile,
            @ConsoleOption(name = "url", desc = "根据在线文档生成时给出文档url", sample = "http://www.pocrd.net/info.api?raw") String url,
            @ConsoleOption(name = "xsl", desc = "指定代码生成模板", sample = "http://www.pocrd.net/info.api?raw") String xsltPath,
            @ConsoleArgument(name = "prefix", desc = "oc类名前缀", sample = "net.pocrd.js") String prefix
    ) {
        ApiCodeGenerator gen = new Builder().setClassPrefix(prefix).setOutputPath(outputPath).setXsltPath(xsltPath).build();
        gen.setApiGroups(groups);
        gen.setSecurityTypes(securityTypes);
        gen.setRejectApis(rejectApis);
        if (jarFile != null) {
            gen.generateViaJar(jarFile);
        } else if (url != null) {
            gen.generateWithApiInfo(url);
        } else {
            throw new RuntimeException("either jar or url must be specified.");
        }
    }
}

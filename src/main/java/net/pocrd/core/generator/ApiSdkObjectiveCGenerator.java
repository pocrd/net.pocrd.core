package net.pocrd.core.generator;

import net.pocrd.define.ConstField;
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
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by guankaiqiang521 on 2014/9/25.
 */
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
        private String xslt = null;
        private String output = "~/tmp";
        private String classPrefix = "FQ";

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
    protected InputStream transformInputStream(InputStream inputStream) {
        BufferedReader reader = null;
        InputStream swapStream = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, ConstField.UTF8));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line.replace("${prefix}", classPrefix) + "\r\n");
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
                if (inputStream != null) {
                    inputStream.close();
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
            defaultXslt = ApiSdkObjectiveCGenerator.class.getResourceAsStream("/xslt/objc.xslt");
            Transformer trans = TransformerFactory.newInstance().newTransformer(
                    getXsltSource(xslt, new StreamSource(transformInputStream(defaultXslt))));
            trans.setOutputProperty("omit-xml-declaration", "yes");
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
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
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("close failed!", e);
                throw new RuntimeException("close failed!", e);
            }
        }
    }

    private static final String RESP = "Response";
    private static final String REQ = "Request";
    private static final String SPLITER = "/************ split .h and .m file ************/";
    private static final int SPLITER_LENGTH = SPLITER.length();

    /**
     * out put resource to file
     *
     * @param name     文件名称
     * @param resource 数据
     */
    private static void output2File(String name, String resource) {
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
     * @param name 文件名不含.h .m后缀
     * @param code 源代码
     */
    private void generateObjcClass(String name, String code) {
        String hfileName = name + ".h";
        String mfileName = name + ".m";
        int s = code.indexOf(SPLITER);
        if (s > 0) {
            output2File(hfileName, code.substring(0, s).trim());
            output2File(mfileName, code.substring(s + SPLITER_LENGTH).trim());
        }
    }

    private void generateObjcEntity(Transformer trans, Document doc) {
        XPath path = XPathFactory.newInstance().newXPath();
        NodeList nl = null;
        try {
            nl = (NodeList) path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
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
            NodeList pl = ((Element) ((Element) nl.item(i)).getElementsByTagName("respStructList").item(0)).getElementsByTagName("respStruct");
            int l = pl.getLength();
            for (int j = 0; j < l; j++) {
                Element e = (Element) pl.item(j);
                String className = classPrefix + e.getElementsByTagName("name").item(
                        0).getFirstChild().getNodeValue();
                String fileName = outputPath + className;
                StringWriter sw = new StringWriter();
                try {
                    trans.transform(new DOMSource(e), new StreamResult(sw));
                } catch (TransformerException e1) {
                    logger.error("transformer failed!class name:" + className, e1);
                    throw new RuntimeException("transformer failed!class name:" + className, e1);
                }
                generateObjcClass(fileName, sw.toString());
                respSet.add(className + ".h");
            }
            //请求结构体
            NodeList ns = ((Element) nl.item(i)).getElementsByTagName("reqStructList");
            if (ns != null && ns.getLength() != 0) {
                pl = ((Element) ns.item(0)).getElementsByTagName("reqStruct");
                l = pl.getLength();
                for (int j = 0; j < l; j++) {
                    Element e = (Element) pl.item(j);
                    String className = classPrefix + e.getElementsByTagName("name").item(
                            0).getFirstChild().getNodeValue();
                    String fileName = outputPath + className;
                    StringWriter sw = new StringWriter();
                    try {
                        trans.transform(new DOMSource(e), new StreamResult(sw));
                    } catch (TransformerException e1) {
                        logger.error("transformer failed!class name:" + className, e1);
                        throw new RuntimeException("transformer failed!class name:" + className, e1);
                    }
                    generateObjcClass(fileName, sw.toString());
                    respSet.add(className + ".h");
                }
            }
        }
        //通用返回对象结构体
        try {
            nl = (NodeList) path.evaluate("//Document/respStructList/respStruct", doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        len = nl.getLength();
        for (int i = 0; i < len; i++) {
            Element e = (Element) nl.item(i);
            String className = classPrefix + e.getElementsByTagName("name").item(
                    0).getFirstChild().getNodeValue();
            String fileName = outputPath + className;
            StringWriter sw = new StringWriter();
            try {
                trans.transform(new DOMSource(e), new StreamResult(sw));
            } catch (TransformerException e1) {
                logger.error("transformer failed!class name:" + className, e1);
                throw new RuntimeException("transformer failed!class name:" + className, e1);
            }
            generateObjcClass(fileName, sw.toString());
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
            nl = (NodeList) path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        int len = nl.getLength();
        String outputPath = output + File.separator + REQ + File.separator;
        FileUtil.recreateDir(outputPath);
        HashSet<String> reqSet = new HashSet<String>();
        for (int i = 0; i < len; i++) {
            Element e = (Element) nl.item(i);
            String methodName = e.getElementsByTagName("methodName").item(0).getFirstChild().getNodeValue();
            int index = methodName.indexOf('.');
            methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1, index) + "_" + methodName.substring(index + 1,
                    index + 2).toUpperCase() + methodName.substring(
                    index + 2);
            StringWriter sw = new StringWriter();
            String className = classPrefix + methodName;
            String fileName = outputPath + className;
            try {
                trans.transform(new DOMSource(e), new StreamResult(sw));
            } catch (TransformerException e1) {
                logger.error("transformer failed!class name:" + className, e1);
                throw new RuntimeException("transformer failed!class name:" + className, e1);
            }
            generateObjcClass(fileName, sw.toString());
            reqSet.add(className + ".h");
        }
        //生成ApiCode
        Node n = null;
        try {
            n = (Node) path.evaluate("//Document/codeList", doc, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        String className = classPrefix + "ApiCode";
        String fileName = outputPath + className;
        StringWriter sw = new StringWriter();
        try {
            trans.transform(new DOMSource(n), new StreamResult(sw));
        } catch (TransformerException e) {
            logger.error("transformer failed!class name:" + className, e);
            throw new RuntimeException("transformer failed!class name:" + className, e);
        }
        generateObjcClass(fileName, sw.toString());
        reqSet.add(className + ".h");
        //生成ApiRequestInclude.h
        fileName = outputPath + classPrefix + "ApiRequestInclude.h";
        File source = new File(fileName);
        OutputStreamWriter fw = null;
        try {
            fw = new OutputStreamWriter(new FileOutputStream(source), "UTF-8");
            sw = new StringWriter().
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

    public static void main(String[] args) {
        if (args.length > 0) {
            if ("generateViaJar".equals(args[0]) && args.length == 3 && args[1].length() > 0) {
                new Builder().setOutputPath(args[2]).build().generateViaJar(args[1]);
                return;
            }
        }
        System.out.println("error parameter.  args[0]:generateViaJar  args[1]:jar path  args[2]:output path");
    }
}

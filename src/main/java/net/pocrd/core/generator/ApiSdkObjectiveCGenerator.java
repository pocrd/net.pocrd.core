package net.pocrd.core.generator;

import net.pocrd.define.ConstField;
import net.pocrd.entity.CodeGenConfig;
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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.HashSet;

/**
 * Created by guankaiqiang521 on 2014/9/25.
 */
public class ApiSdkObjectiveCGenerator extends ApiCodeGenerator {
    private static final Logger                    logger   = LoggerFactory.getLogger(ApiSdkObjectiveCGenerator.class);
    private static       ApiSdkObjectiveCGenerator instance = new ApiSdkObjectiveCGenerator();
    private ApiSdkObjectiveCGenerator() {}
    public static ApiSdkObjectiveCGenerator getInstance() {
        return instance;
    }
    @Override
    public InputStream transformInputStream(InputStream inputStream) {
        BufferedReader reader = null;
        InputStream swapStream = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, ConstField.UTF8));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line.replace("${prefix}", CodeGenConfig.getInstance().getApiSdkObjcClassPrefix()) + "\r\n");
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
                    getXsltSource(CodeGenConfig.getInstance().getObjcXsltSite(), new StreamSource(transformInputStream(defaultXslt))));
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
            nl = (NodeList)path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        int len = nl.getLength();
        String outputPath = CodeGenConfig.getInstance().getApiSdkObjcLocation() + File.separator + RESP + File.separator;
        FileUtil.recreateDir(outputPath);
        HashSet<String> respSet = new HashSet();
        for (int i = 0; i < len; i++) {
            //返回结构体
            NodeList pl = ((Element)((Element)nl.item(i)).getElementsByTagName("respStructList").item(0)).getElementsByTagName("respStruct");
            int l = pl.getLength();
            for (int j = 0; j < l; j++) {
                Element e = (Element)pl.item(j);
                String className = CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + e.getElementsByTagName("name").item(
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
            NodeList ns = ((Element)nl.item(i)).getElementsByTagName("reqStructList");
            if (ns != null && ns.getLength() != 0) {
                pl = ((Element)ns.item(0)).getElementsByTagName("reqStruct");
                l = pl.getLength();
                for (int j = 0; j < l; j++) {
                    Element e = (Element)pl.item(j);
                    String className = CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + e.getElementsByTagName("name").item(
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
            nl = (NodeList)path.evaluate("//Document/respStructList/respStruct", doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        len = nl.getLength();
        for (int i = 0; i < len; i++) {
            Element e = (Element)nl.item(i);
            String className = CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + e.getElementsByTagName("name").item(
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
        String fileName = outputPath + CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + "ApiResponseInclude.h";
        File source = new File(fileName);
        OutputStreamWriter fw = null;
        try {
            fw = new OutputStreamWriter(new FileOutputStream(source), "UTF-8");
            StringWriter sw = new StringWriter().append(
                    "// Auto Generated.  DO NOT EDIT!\n#ifndef " + CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + "ApiResponseInclude_h\n#define " + CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + "ApiResponseInclude_h\n");
            for (String resp : respSet) {
                sw.append("#import <" + CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + "ServerAPIFramework_iOS/" + resp + ">\n");
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
        String outputPath = CodeGenConfig.getInstance().getApiSdkObjcLocation() + File.separator + REQ + File.separator;
        FileUtil.recreateDir(outputPath);
        HashSet<String> reqSet = new HashSet<String>();
        for (int i = 0; i < len; i++) {
            Element e = (Element)nl.item(i);
            String methodName = e.getElementsByTagName("methodName").item(0).getFirstChild().getNodeValue();
            int index = methodName.indexOf('.');
            methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1, index) + "_" + methodName.substring(index + 1,
                                                                                                                                index + 2).toUpperCase() + methodName.substring(
                    index + 2);
            StringWriter sw = new StringWriter();
            String className = CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + methodName;
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
            n = (Node)path.evaluate("//Document/codeList", doc, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            logger.error("evaluate node failed!", e);
            throw new RuntimeException("evaluate node failed!", e);
        }
        String className = CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + "ApiCode";
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
        fileName = outputPath + CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + "ApiRequestInclude.h";
        File source = new File(fileName);
        OutputStreamWriter fw = null;
        try {
            fw = new OutputStreamWriter(new FileOutputStream(source), "UTF-8");
            sw = new StringWriter().
                    append("// Auto Generated.  DO NOT EDIT!\n" +
                                   "#ifndef " + CodeGenConfig.getInstance().
                            getApiSdkObjcClassPrefix() + "ApiRequestInclude_h\n" +
                                   "#define " + CodeGenConfig.getInstance().
                            getApiSdkObjcClassPrefix() + "ApiRequestInclude_h\n");
            for (String req : reqSet) {
                sw.append("#import <" + CodeGenConfig.getInstance().getApiSdkObjcClassPrefix() + "ServerAPIFramework_iOS/" + req + ">\n");
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
}

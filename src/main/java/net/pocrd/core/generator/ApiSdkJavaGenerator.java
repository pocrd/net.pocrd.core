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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.List;

/**
 * Created by guankaiqiang521 on 2014/9/25.
 */
public class ApiSdkJavaGenerator extends ApiCodeGenerator {
    private static final Logger logger  = LoggerFactory.getLogger(ApiSdkJavaGenerator.class);
    private static final String REQUEST = "request";
    private static final String RESP    = "resp";
    private static final String API     = "api";

    private String xslt;
    private String output;
    private String packagePrefix;

    private ApiSdkJavaGenerator(String output, String packagePrefix) {
        this.output = output;
        this.packagePrefix = packagePrefix;
    }

    private void setXslt(String xslt) {
        this.xslt = xslt;
    }

    public static class Builder {
        private String xslt          = null;
        private String output        = "~/tmp";
        private String packagePrefix = "net.pocrd.m.app.client";

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

        public ApiSdkJavaGenerator build() {
            ApiSdkJavaGenerator g = new ApiSdkJavaGenerator(output, packagePrefix);
            g.setXslt(xslt);
            return g;
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
    public void generate(InputStream apiInfo) {
        InputStream defaultXslt = null;
        try {
            defaultXslt = ApiSdkJavaGenerator.class.getResourceAsStream("/xslt/java.xslt");
            Transformer trans = TransformerFactory.newInstance().newTransformer(
                    getXsltSource(xslt, new StreamSource(customizeXslt(defaultXslt))));
            trans.setOutputProperty("omit-xml-declaration", "yes");
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(apiInfo);
            generateJavaEntity(trans, document);
            generateJavaRequest(trans, document);
            // generateJavaFramework();
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

    private void generateJavaEntity(Transformer trans, Document doc) {
        try {

            XPath path = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList)path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
            int len = nl.getLength();
            String outputPath = output + File.separator + API + File.separator + RESP;
            FileUtil.recreateDir(outputPath);
            for (int i = 0; i < len; i++) {
                NodeList pl = ((Element)((Element)nl.item(i)).getElementsByTagName("respStructList").item(0)).getElementsByTagName("respStruct");
                int l = pl.getLength();
                for (int j = 0; j < l; j++) {
                    Element e = (Element)pl.item(j);
                    String className = e.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
                    String fileName = outputPath + File.separator + className + ".java";
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
                        String fileName = outputPath + File.separator + className + ".java";
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
                String fileName = outputPath + File.separator + className + ".java";
                File source = new File(fileName);
                if (!source.exists()) {
                    trans.transform(new DOMSource(e), new StreamResult(source));
                }
            }

        } catch (Exception e) {
            logger.error("generate api java client failed", e);
            throw new RuntimeException("generate api java client failed", e);
        }
    }

    private void generateJavaRequest(Transformer trans, Document doc) {
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
                methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1, index) + "_" + methodName.substring(index + 1,
                        index + 2).toUpperCase() + methodName.substring(
                        index + 2);
                String fileName = outputPath + File.separator + methodName + ".java";
                File source = new File(fileName);
                if (!source.exists()) {
                    trans.transform(new DOMSource(e), new StreamResult(source));
                }
            }
            Node n = (Node)path.evaluate("//Document/codeList", doc, XPathConstants.NODE);
            trans.transform(new DOMSource(n), new StreamResult(outputPath + File.separator + "ApiCode.java"));
        } catch (Exception e) {
            logger.error("generate api java client failed", e);
            throw new RuntimeException("generate api java client failed", e);
        }
    }

    //TODO 找到一个更好的方式去处理非request resp的java类
    private void generateJavaFramework() {
        String url = ApiSdkJavaGenerator.class.getResource("/template/java").getFile();
        List<File> dirList = FileUtil.listDirInpath(url);
        String outputPath = output;
        if (dirList != null) {
            for (File file : dirList) {
                FileUtil.recreateDir(outputPath + file.toURI().getPath().replace(url, ""));
            }
            List<File> fileList = FileUtil.listFileInpath(url, "java");
            if (fileList != null) {
                for (File file : fileList) {
                    String fileName = outputPath + file.toURI().getPath().replace(url, "");
                    File target = new File(fileName);
                    if (!target.exists()) {
                        InputStream stream = null;
                        OutputStream fileOutputStream = null;
                        try {
                            byte[] buffer = new byte[4096];
                            int len;
                            fileOutputStream = new FileOutputStream(new File(fileName));
                            stream = customizeXslt(new FileInputStream(file));
                            while ((len = stream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, len);
                            }
                        } catch (Exception e) {
                            logger.error(String.format("transform file:%s failed!", fileName), e);
                            throw new RuntimeException(String.format("transform file:%s failed!", fileName), e);
                        } finally {
                            try {
                                if (stream != null) {
                                    stream.close();
                                }
                                if (fileOutputStream != null) {
                                    fileOutputStream.close();
                                }
                            } catch (IOException e) {
                                logger.error(String.format("transform file:%s failed!", fileName), e);
                                throw new RuntimeException("close stream failed!", e);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 4) {
            if ("jar".equals(args[0])) {
                new Builder().setPackagePrefix(args[2]).setOutputPath(args[3]).build().generateViaJar(args[1]);
                return;
            } else if ("url".equals(args[0])) {
                new Builder().setPackagePrefix(args[2]).setOutputPath(args[3]).build().generateWithApiInfo(args[1]);
                return;
            }
        }
        System.out.println("error parameter. {jar/url} {jar/url path} {package prefix} {output path}");
    }
}

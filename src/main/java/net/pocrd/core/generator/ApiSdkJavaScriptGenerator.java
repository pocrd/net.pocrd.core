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
public class ApiSdkJavaScriptGenerator extends ApiCodeGenerator {
    private static final Logger                    logger   = LoggerFactory.getLogger(ApiSdkJavaGenerator.class);
    private static final ApiSdkJavaScriptGenerator instance = new ApiSdkJavaScriptGenerator();

    private ApiSdkJavaScriptGenerator() {
    }

    public static ApiSdkJavaScriptGenerator getInstance() {
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
                out.append(line.replace("${pkg}", CodeGenConfig.getInstance().getApiSdkJsPkgName()) + "\r\n");
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
            defaultXslt = ApiSdkJavaScriptGenerator.class.getResourceAsStream("/xslt/js.xslt");
            Transformer trans = TransformerFactory.newInstance().newTransformer(
                    getXsltSource(CodeGenConfig.getInstance().getJsXsltSite(), new StreamSource(transformInputStream(defaultXslt))));
            trans.setOutputProperty("omit-xml-declaration", "yes");
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            generateJsRequest(trans, document);
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

    private void generateJsRequest(Transformer trans, Document doc) {
        try {
            XPath path = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList)path.evaluate(getApiEvaluate(), doc, XPathConstants.NODESET);
            int len = nl.getLength();
            String outputPath = CodeGenConfig.getInstance().getApiSdkJsLocation();
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
                String fileName = outputPath + File.separator + moduleName + File.separator + CodeGenConfig.getInstance().getApiSdkJsPkgName() + ".api." + methodName + ".js";
                File source = new File(fileName);
                if (!source.exists()) {
                    trans.transform(new DOMSource(e), new StreamResult(source));
                }
            }
            Node n = (Node)path.evaluate("//Document/codeList", doc, XPathConstants.NODE);
            trans.transform(new DOMSource(n), new StreamResult(outputPath +  File.separator + CodeGenConfig.getInstance().getApiSdkJsPkgName() +  ".api.apiCode.js"));
        } catch (Exception e) {
            logger.error("generate api js client failed", e);
            throw new RuntimeException("generate api js client failed", e);
        }
    }
}

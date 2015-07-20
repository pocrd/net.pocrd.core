package net.pocrd.core.generator;

import net.pocrd.entity.CodeGenConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * 优先使用配置的apiInfoXslSite地址下的xsl对模板进行渲染,如果未下载到或未配置则使用默认地址进行
 * Created by guankaiqiang521 on 2014/9/24.
 */
public class HtmlApiDocGenerator extends ApiCodeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(HtmlApiDocGenerator.class);
    private HtmlApiDocGenerator() {}
    private static HtmlApiDocGenerator instance = new HtmlApiDocGenerator();
    public static HtmlApiDocGenerator getInstance() {
        return instance;
    }

    /**
     * do nothing
     *
     * @param inputStream
     *
     * @return
     */
    @Override
    public InputStream transformInputStream(InputStream inputStream) {
        return inputStream;
    }

    @Override
    public void generate(InputStream inputStream) {
        InputStream defaultStream = null;
        try {
            defaultStream = HtmlApiDocGenerator.class.getResourceAsStream("/xslt/apiInfo.xsl");
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Source xslSource = getXsltSource(CodeGenConfig.getInstance().getApiInfoXslSite(), new StreamSource(transformInputStream(defaultStream)));
            Transformer trasform = tFactory.newTransformer(xslSource);
            trasform.transform(new StreamSource(inputStream), new StreamResult(CodeGenConfig.getInstance().getHtmlApiDocLocation()));
        } catch (Exception e) {
            logger.error("generator doc failed!", e);
            throw new RuntimeException("generator doc failed!", e);
        } finally {
            try {
                if (defaultStream != null) {
                    defaultStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("close failed!", e);
                throw new RuntimeException("generator doc failed!", e);
            }
        }
    }
}

package net.pocrd.core.generator;

import net.pocrd.annotation.ConsoleJoinPoint;
import net.pocrd.annotation.ConsoleOption;
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
 * Created by guankaiqiang521 on 2014/9/24.
 */
@ConsoleJoinPoint(command = "api-doc-gen", desc = "生成html格式的api文档")
public class HtmlApiDocGenerator extends ApiCodeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(HtmlApiDocGenerator.class);

    private String xslt;
    private String output;

    private HtmlApiDocGenerator(String xslt, String output) {
        this.xslt = xslt;
        this.output = output;
    }

    public static class Builder {
        private String xslt   = null;
        private String output = "~/tmp";

        public Builder setXsltPath(String xslt) {
            this.xslt = xslt;
            return this;
        }

        public Builder setOutputPath(String output) {
            this.output = output;
            return this;
        }

        public HtmlApiDocGenerator build() {
            return new HtmlApiDocGenerator(xslt, output);
        }
    }

    /**
     * do nothing
     *
     * @param xslt
     */
    @Override
    protected InputStream customizeXslt(InputStream xslt) {
        return xslt;
    }

    @Override
    public void generate(InputStream apiInfo) {
        InputStream defaultStream = null;
        try {
            defaultStream = HtmlApiDocGenerator.class.getResourceAsStream("/xslt/apiInfo.xsl");
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Source xslSource = getXsltSource(xslt, new StreamSource(customizeXslt(defaultStream)));
            Transformer trasform = tFactory.newTransformer(xslSource);
            FileUtil.recreateDir(output);
            trasform.transform(new StreamSource(apiInfo), new StreamResult(output + "/apidoc.html"));
        } catch (Exception e) {
            logger.error("generator doc failed!", e);
            throw new RuntimeException("generator doc failed!", e);
        } finally {
            try {
                if (defaultStream != null) {
                    defaultStream.close();
                }
                if (apiInfo != null) {
                    apiInfo.close();
                }
            } catch (IOException e) {
                logger.error("close failed!", e);
                throw new RuntimeException("generator doc failed!", e);
            }
        }
    }

    public static void execute(
            @ConsoleOption(name = "o", desc = "输出文件目录") String outputPath,
            @ConsoleOption(name = "jar", desc = "根据jar文件生成时给出jar文件地址") String jarFile,
            @ConsoleOption(name = "url", desc = "根据在线文档生成时给出文档url", sample = "http://www.pocrd.net/info.api?raw") String url
    ) {
        ApiCodeGenerator gen = new Builder().setOutputPath(outputPath).build();
        if (jarFile != null) {
            gen.generateViaJar(jarFile);
        } else if (url != null) {
            gen.generateWithApiInfo(url);
        } else {
            throw new RuntimeException("either jar or url must be specified.");
        }
    }
}

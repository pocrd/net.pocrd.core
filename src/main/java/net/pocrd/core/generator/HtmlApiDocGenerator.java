package net.pocrd.core.generator;

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

    public static void main(String[] args) {
        if (args.length == 3) {
            if ("jar".equals(args[0])) {
                new Builder().setOutputPath(args[2]).build().generateViaJar(args[1]);
                return;
            } else if ("url".equals(args[0])) {
                new Builder().setOutputPath(args[2]).build().generateWithApiInfo(args[1]);
                return;
            }
        }
        System.out.println("error parameter. {jar/url} {jar/url path} {output path}");
    }
}

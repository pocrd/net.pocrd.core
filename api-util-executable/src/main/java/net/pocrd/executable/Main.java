package net.pocrd.executable;

import net.pocrd.entity.CompileConfig;

import static net.pocrd.core.generator.ApiLogParserGenerator.generate;

/**
 * Created by rendong on 2017/6/13.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 2 && args[0] != null && args[1] != null) {
            generate(args[0], args[1]);
        } else {
            if (CompileConfig.isDebug) {
                generate("http://115.28.160.84/info.api?raw", "/Users/rendong/Desktop");
            } else {
                System.out.println("error parameter.  args[0]:source url   args[1]:output path");
            }
        }
    }
}

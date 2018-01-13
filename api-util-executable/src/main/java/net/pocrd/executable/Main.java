package net.pocrd.executable;

import net.pocrd.annotation.ConsoleArgument;
import net.pocrd.annotation.ConsoleJoinPoint;
import net.pocrd.annotation.ConsoleOption;
import net.pocrd.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by rendong on 2017/6/13.
 */
public class Main {
    static class Command {
        String                    name;
        String                    desc;
        Method                    method;
        List<String>              methodArgs;
        TreeMap<String, Argument> args;
        TreeMap<String, Option>   options;

        Command(String name, String desc, Method method, List<String> methodArgs) {
            this.name = name;
            this.desc = desc;
            this.method = method;
            this.methodArgs = methodArgs;
            args = new TreeMap<String, Argument>();
            options = new TreeMap<String, Option>();
        }
    }

    static class Option {
        String name;
        String desc;
        String sample;

        Option(String name, String desc, String sample) {
            this.name = name;
            this.desc = desc;
            this.sample = sample;
        }
    }

    static class Argument {
        String name;
        String desc;
        String sample;

        Argument(String name, String desc, String sample) {
            this.name = name;
            this.desc = desc;
            this.sample = sample;
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("net.pocrd.core.generator.ApiCodeGenerator");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Class<?>[] cs = ClassUtil.getAllClassesInPackage("net.pocrd.core.generator");
        String cmd = null;
        Map<String, String> options = new HashMap<String, String>();
        List<String> arguments = new LinkedList<String>();
        TreeMap<String, Command> commands = new TreeMap<String, Command>(new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg != null && arg.length() > 0) {
                    if (arg.startsWith("-")) {
                        String nextArg = args.length > i + 1 ? args[i + 1] : null;
                        if (nextArg != null && nextArg.length() > 0 && !nextArg.startsWith("-")) {
                            options.put(arg.substring(1), nextArg);
                            i++;
                        } else {
                            options.put(arg.substring(1), null);
                        }
                    } else if (cmd == null) {
                        cmd = arg;
                    } else {
                        arguments.add(arg);
                    }
                }
            }
        }
        for (Class<?> c : cs) {
            ConsoleJoinPoint cjp = c.getAnnotation(ConsoleJoinPoint.class);
            if (cjp != null) {
                Method m = null;
                try {
                    Method[] ms = c.getMethods();
                    for (Method mm : ms) {
                        if ("execute".equals(mm.getName())) {
                            m = mm;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("cannot find execute method for " + c.getName());
                    throw new RuntimeException(e);
                }
                if (m == null) {
                    System.out.println("cannot find execute method for " + c.getName());
                    return;
                }
                List<String> methodArgs = new ArrayList<>(m.getParameterCount());
                Command command = new Command(cjp.command(), cjp.desc(), m, methodArgs);
                for (Parameter p : m.getParameters()) {
                    Annotation[] as = p.getAnnotations();
                    if (as != null && as.length > 0) {
                        for (Annotation a : as) {
                            if (a instanceof ConsoleOption) {
                                ConsoleOption co = (ConsoleOption)a;
                                command.options.put(co.name(), new Option(co.name(), co.desc(), co.sample()));
                                methodArgs.add(co.name());
                                break;
                            } else if (a instanceof ConsoleArgument) {
                                ConsoleArgument ca = (ConsoleArgument)a;
                                command.args.put(ca.name(), new Argument(ca.name(), ca.desc(), ca.sample()));
                                methodArgs.add(ca.name());
                                break;
                            }
                        }
                    }
                }
                if (command.args.size() + command.options.size() != m.getParameterCount()) {
                    throw new RuntimeException("发现未被标记的参数" + c.getName() + "  " + m.getName());
                }
                commands.put(command.name, command);
            }
        }
        StringBuilder sb = new StringBuilder("Usage:\n");
        String indent = "    ";
        String space = "  ";
        if (cmd == null || !commands.containsKey(cmd)) {
            for (String key : commands.keySet()) {
                Command c = commands.get(key);
                sb.append(indent).append(c.name).append("  ");
                for (int i = c.name.length(); i < 30; i++) {
                    sb.append(" ");
                }
                sb.append(c.desc).append("\n");
            }
            System.out.println(sb.toString());
        } else {
            Command c = commands.get(cmd);
            if (!options.containsKey("?") && !options.containsKey("h") && !options.containsKey("help") && arguments.size() == c.args.size()) {
                try {
                    Object[] objs = new String[c.methodArgs.size()];
                    for (int i = 0; i < c.methodArgs.size(); i++) {
                        String key = c.methodArgs.get(i);
                        objs[i] = c.options.containsKey(key) ? options.get(key) : arguments.remove(0);
                    }
                    c.method.invoke(null, objs);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sb.append(indent).append(cmd);
            for (Option option : c.options.values()) {
                sb.append(space).append("[-").append(option.name).append("]");
            }
            for (Argument argument : c.args.values()) {
                sb.append(space).append("<").append(argument.name).append(">");
            }
            sb.append("\n");
            for (Option option : c.options.values()) {
                sb.append(indent).append(indent).append(option.name).append(" ");
                for (int i = option.name.length(); i < 15; i++) {
                    sb.append(" ");
                }
                sb.append(option.desc);
                if (option.sample != null && option.sample.length() > 0) {
                    sb.append(space).append("eg. ").append(option.sample);
                }
                sb.append("\n");
            }
            for (Argument argument : c.args.values()) {
                sb.append(indent).append(indent).append(argument.name).append(" ");
                for (int i = argument.name.length(); i < 15; i++) {
                    sb.append(" ");
                }
                sb.append(argument.desc);
                if (argument.sample != null && argument.sample.length() > 0) {
                    sb.append(space).append("eg. ").append(argument.sample);
                }
                sb.append("\n");
            }
            System.out.println(sb.toString());
        }
    }
}

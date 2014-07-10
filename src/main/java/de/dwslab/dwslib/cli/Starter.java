package de.dwslab.dwslib.cli;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Main entry point for calling from the command line.
 *
 * @author Daniel Fleischhacker (daniel@informatik.uni-mannheim.de)
 */
public class Starter {
    public static void main(String[] args) throws InvocationTargetException {
        if (args.length < 1) {
            System.err.println("No class name to call provided");
            System.exit(1);
        }

        String className = args[0];

        // if no period is used in the given class name, we assume it to be in package de.dwslab.dwslib.cli
        if (!className.contains(".")) {
            className = "de.dwslab.dwslib.cli." + className;
        }

        try {
            Class<?> classToStart = Class.forName(className);
            Method methodToCall = classToStart.getMethod("main", String[].class);
            String[] arguments = Arrays.copyOfRange(args, 1, args.length);

            Object[] wrappedArguments = new Object[]{arguments};

            methodToCall.invoke(null, wrappedArguments);
        }
        catch (ClassNotFoundException e) {
            System.err.println("Unable to load class: " + className);
            System.exit(2);
        }
        catch (NoSuchMethodException e) {
            System.err.println("The loaded class " + className + " has no valid main method.");
            System.exit(2);
        }
        catch (InvocationTargetException e) {
            throw e;
        }
        catch (IllegalAccessException e) {
            System.err.println("Unable to call main method of class " + className);
            e.printStackTrace();
            System.exit(3);
        }
    }
}

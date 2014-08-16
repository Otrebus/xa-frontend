package se.neava.compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import se.neava.compiler.GravelParser.ProgramContext;
import se.neava.compiler.type.*;

public class CompileTester {

    public static void main(String[] args) 
    {
        GravelLexer lexer = new GravelLexer(new ANTLRInputStream("extern void test(int);\r\n" + 
                "extern void uartSetCallback(function (char, char[]) -> void);\r\n" + 
                "extern void uartSend(char, char[]);\r\n" + 
                "\r\n" + 
                "extern void toggleLed(void);\r\n" + 
                "\r\n" + 
                "Main main;\r\n" + 
                "\r\n" + 
                "MessageHandler msgHndlr;\r\n" + 
                "\r\n" + 
                "Blinker blnkr;\r\n" + 
                "\r\n" + 
                "class MessageHandler\r\n" + 
                "{\r\n"
                + "char[20] classvar;" + 
                "    char toUpper(char c)\r\n" + 
                "    {\r\n" + 
                "        return c + (char) 40;\r\n" + 
                "    }\r\n" + 
                "\r\n" + 
                "    char test(char b)\r\n" + 
                "    {\r\n" + 
                "        char x;\r\n" + 
                "        char y;\r\n" + 
                "        char z;\r\n" + 
                "        return x + y + z + b;\r\n" + 
                "    }\r\n" + 
                "\r\n" + 
                "    void handleMessage(char length, char[] msg)\r\n" + 
                "    {\r\n" + 
                "        int i;\r\n" + 
                "        i = (int) 0;\r\n" + 
                "            classvar[i] = msgHndlr.toUpper(msg[i]);\r\n" + 
                "        return;\r\n" + 
                "    }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "class Main\r\n" + 
                "{\r\n" + 
                "    void main()\r\n" + 
                "    {\r\n" + 
                "        uartSetCallback(msgHndlr.handleMessage);\r\n" + 
                "        return;\r\n" + 
                "    }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "class Blinker\r\n" + 
                "{\r\n" + 
                "    int nBlinks;\r\n" + 
                "    int period;\r\n" + 
                "\r\n" + 
                "    void startBlinking(int blinks, int argPeriod)\r\n" + 
                "    {\r\n" + 
                "        nBlinks = blinks;\r\n" + 
                "        period = argPeriod;\r\n" + 
                "        if(nBlinks > (int) 0)\r\n" + 
                "            blink();\r\n" + 
                "        return;\r\n" + 
                "    }\r\n" + 
                "    \r\n" + 
                "    void blink()\r\n" + 
                "    {\r\n" + 
                "        toggleLed();\r\n" + 
                "        nBlinks = nBlinks - (int) 1;\r\n" + 
                "        if(nBlinks > (int) 0)\r\n" + 
                "            after period msec before (long) 10 msec blink();\r\n" + 
                "        return;\r\n" + 
                "    }\r\n" + 
                "}"));
        
        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);
     
        // Pass the tokens to the parser
        GravelParser parser = new GravelParser(tokens);
     
        CodeGeneratorVisitor visitor = new CodeGeneratorVisitor();
        visitor.visit(parser.program());
        if(visitor.error())
        {
            System.out.println("Compile error(s)!");
            System.out.println(visitor.dumpErrors());
        }
        else
            System.out.println(visitor.getCode());
    }

}

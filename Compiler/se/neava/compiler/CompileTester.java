package se.neava.compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import se.neava.compiler.GravelParser.ProgramContext;

public class CompileTester {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        GravelLexer lexer = new GravelLexer(new ANTLRInputStream("extern void uartSetCallback(function (char, char[]) -> void);\r\n" + 
                "extern void uartSend(char, char[]);\r\n" + 
                "\r\n" + 
                "extern void toggleLed(void);\r\n" + 
                "\r\n" + 
                "Main m;\r\n" + 
                "\r\n" + 
                "MessageHandler msgHndlr;\r\n" + 
                "\r\n" + 
                "Blinker blnkr;\r\n" + 
                "\r\n" + 
                "class MessageHandler\r\n" + 
                "{\r\n" + 
                "    char toUpper(char c)\r\n" + 
                "    {\r\n" + 
                "        return c + 40;\r\n" + 
                "    }\r\n" + 
                "\r\n" + 
                "    void handleMessage(char length, char[] msg)\r\n" + 
                "    {\r\n" + 
                "        int i = 0;\r\n" + 
                "        while(i < length)\r\n" + 
                "        {\r\n" + 
                "            msg[i] = toUpper(msg[i]);\r\n" + 
                "            i = i + 1;\r\n" + 
                "        }\r\n" + 
                "        uartSend(length, msg);\r\n" + 
                "        blnkr.blink();\r\n" + 
                "    }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "class Main\r\n" + 
                "{\r\n" + 
                "    void main()\r\n" + 
                "    {\r\n" + 
                "        uartSetCallback(msgHndlr.handleMessage);\r\n" + 
                "    }\r\n" + 
                "}\r\n" + 
                "\r\n" + 
                "class Blinker\r\n" + 
                "{\r\n" + 
                "    int nBlinks = 0;\r\n" + 
                "    int period;\r\n" + 
                "\r\n" + 
                "    void startBlinking(int blinks, int argPeriod)\r\n" + 
                "    {\r\n" + 
                "        nBlinks = blinks;\r\n" + 
                "        period = argPeriod;\r\n" + 
                "        if(nBlinks > 0)\r\n" + 
                "        blink();\r\n" + 
                "    }\r\n" + 
                "    \r\n" + 
                "    void blink()\r\n" + 
                "    {\r\n" + 
                "        toggleLed();\r\n" + 
                "        nBlinks = nBlinks - 1;\r\n" + 
                "        if(nBlinks > 0)\r\n" + 
                "            after period msec before 10 msec blink();\r\n" + 
                "    }\r\n" + 
                "}"));
        
        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);
     
        // Pass the tokens to the parser
        GravelParser parser = new GravelParser(tokens);
     
        // Specify our entry point
        ProgramContext gravelContext = parser.program();
     
        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        GravelListener listener = new CodeGeneratorListener();
        walker.walk(listener, gravelContext);
    }

}
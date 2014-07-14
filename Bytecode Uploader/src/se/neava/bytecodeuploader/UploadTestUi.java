import se.neava.bytecodeuploader.BytecodeUploader;

    public static void main(String[] args) 
    {
        File file = new File("input.asm");
        System.out.println(file.getAbsolutePath());
        try 
        { 
            byte[] bytes = Files.readAllBytes(file.toPath());
            String text = new String(bytes,"UTF-8");

            new Assembler().assemble(text);
            BytecodeUploader bu = new BytecodeUploader(null, 9600);
            bu.transmitCode("abcdefghijklmnopqrstuvwxyz".getBytes("UTF8"), 5);
        } 
        catch (NoPortsFoundException | SerialPortException | BusyException | IOException | ParseException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
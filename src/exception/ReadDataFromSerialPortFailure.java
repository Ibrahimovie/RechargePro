
package exception;


public class ReadDataFromSerialPortFailure extends Exception {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {

        return "从串口读取数据时出错！";

    }

}



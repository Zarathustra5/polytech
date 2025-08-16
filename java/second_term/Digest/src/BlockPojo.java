import java.io.Serializable;

// Элемент цепочки блоков
public class BlockPojo implements Serializable{
    public String message;
    public String hash;
    public BlockPojo prevBlockPojo;

    public String getMessage() {
        return message;
    }
}
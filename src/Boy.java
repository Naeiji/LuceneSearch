import org.tartarus.snowball.ext.PorterStemmer;

public class Boy {
    public static void main(String[] args) {
        PorterStemmer ps = new PorterStemmer();
        ps.setCurrent("riding");
        ps.stem();
        System.out.println(ps.getCurrent());


    }
}

import controller.LibraryController;
import dto.BookCreateDto;
import model.BookBase;
import model.EBook;
import model.PrintedBook;
import utils.ReflectionUtils;

public class Main {

    public static void main(String[] args) {

        LibraryController controller = new LibraryController();

        //  CREATE 
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("Clean Code");
        dto.setPrice(4500);
        dto.setCategoryId(1);
        dto.setType("PRINTED");
        dto.setPages(350);

        int id = controller.createBook(dto);
        System.out.println("Created book id = " + id);

        //  READ 
        System.out.println("=== ALL BOOKS ===");
        for (BookBase b : controller.listBooks()) {
            System.out.println(b.shortInfo());
        }

        //  REFLECTION 
        System.out.println("\n=== REFLECTION: EBook ===");
        ReflectionUtils.printClassInfo(EBook.class);

        System.out.println("\n=== REFLECTION: PrintedBook ===");
        ReflectionUtils.printClassInfo(PrintedBook.class);
    }
}









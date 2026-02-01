package service;

import dto.BookCreateDto;
import exception.DatabaseOperationException;
import exception.InvalidInputException;
import model.BookBase;
import model.Category;
import model.EBook;
import model.PrintedBook;
import repository.BookRepository;
import repository.CategoryRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {

    private final BookRepository bookRepository = new BookRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public int addBook(BookCreateDto dto) {
        if (dto == null) {
            throw new InvalidInputException("dto is null");
        }

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("title must not be empty");
        }
        if (dto.getPrice() <= 0) {
            throw new InvalidInputException("price must be > 0");
        }
        if (dto.getCategoryId() <= 0) {
            throw new InvalidInputException("categoryId must be > 0");
        }
        if (dto.getType() == null || dto.getType().trim().isEmpty()) {
            throw new InvalidInputException("type is required (EBOOK / PRINTED)");
        }

        // 1) находим Category по id и НЕ даём ему быть null 
        Category category = categoryRepository.findById(dto.getCategoryId());
        if (category == null) {
            throw new InvalidInputException("Category not found, id=" + dto.getCategoryId());
        }

        // 2) создаём объект книги по type 
        String type = dto.getType().trim();
        BookBase book;

        if ("EBOOK".equalsIgnoreCase(type)) {
            if (dto.getFileSizeMb() <= 0) {
                throw new InvalidInputException("fileSizeMb must be > 0");
            }
            book = new EBook(0, dto.getTitle(), dto.getPrice(), category, dto.getFileSizeMb());

        } else if ("PRINTED".equalsIgnoreCase(type)) {
            if (dto.getPages() <= 0) {
                throw new InvalidInputException("pages must be > 0");
            }
            book = new PrintedBook(0, dto.getTitle(), dto.getPrice(), category, dto.getPages());

        } else {
            throw new InvalidInputException("Unknown type: " + dto.getType());
        }

        // если у тебя validate() требует category != null — теперь всё ок
        book.validate();

        // 3) сохраняем в БД 
        try {
            return bookRepository.create(book);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to add book", e);
        }
    }

    // чтобы controller.listBooks() работал
    public List<BookBase> getAllBooks() {
        try {
            return bookRepository.findAll();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to load books", e);
        }
    }

    //цикл (самый дорогой)
    public BookBase getMostExpensive() {
        BookBase max = null;

        for (BookBase book : getAllBooks()) {
            if (max == null || book.getPrice() > max.getPrice()) {
                max = book;
            }
        }
        return max;
    }

    public List<BookBase> sortByPriceAsc() {
        return getAllBooks().stream()
                .sorted(Comparator.comparingDouble(BookBase::getPrice))
                .collect(Collectors.toList());
    }

    public List<BookBase> filterByType(String type) {
        String t = (type == null) ? "" : type.trim();
        return getAllBooks().stream()
                .filter(b -> b.getType().equalsIgnoreCase(t))
                .collect(Collectors.toList());
    }

    public List<BookBase> searchByTitle(String text) {
        String q = (text == null) ? "" : text.trim().toLowerCase();
        return getAllBooks().stream()
                .filter(b -> b.getTitle() != null && b.getTitle().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }
}






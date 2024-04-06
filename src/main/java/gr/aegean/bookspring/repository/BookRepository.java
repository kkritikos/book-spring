package gr.aegean.bookspring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gr.aegean.bookspring.model.Book;

public interface BookRepository extends JpaRepository<Book, String>{
	List<Book> findByTitle(String title);
	List<Book> findByPublisher(String publisher);
	List<Book> findByTitleAndPublisher(String title, String publisher);
}

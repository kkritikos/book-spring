package gr.aegean.bookspring.controller;

import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import gr.aegean.bookspring.model.Book;
import gr.aegean.bookspring.repository.BookRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {
	private final BookRepository repo;
	
	BookController(BookRepository repo){
		this.repo = repo;
	}
	
	@GetMapping(produces = {"application/json", "application/xml"})
	List<Book> getBooks(@RequestParam(required = false) String title, @RequestParam(required = false) String publisher){
		boolean titleNotEmpty = (title != null && !title.isBlank());
		boolean publisherNotEmpty = (publisher != null && !publisher.isBlank());
		
		if (!titleNotEmpty && !publisherNotEmpty) return repo.findAll();
		else {
			if (titleNotEmpty && !publisherNotEmpty) return repo.findByTitle(title);
			else if (titleNotEmpty) return repo.findByTitleAndPublisher(title,publisher);
			else return repo.findByPublisher(publisher);
		}
	}
	
	@GetMapping(value = "{id}", produces = {"application/json", "application/xml"})
	Book getBook(@PathVariable String id) {
		return repo.findById(id).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with given id does not exist!"));
	}
	
	@PostMapping(consumes = {"application/json", "application/xml"})
	ResponseEntity<?> insertBook(@Valid @RequestBody Book book) {
		if (repo.findById(book.getIsbn()).isPresent())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book with given id already exists!");
		else {
			repo.save(book);
			try {
				String url = "http://" + InetAddress.getLocalHost().getHostName() + ":8080/api/books/" + book.getIsbn();
				return ResponseEntity.created(new URI(url)).build();
			}
			catch(Exception e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong while generating the response!");
			}
		}
	}
	
	@PutMapping(value = "{id}", consumes = {"application/json", "application/xml"})
	ResponseEntity<?> updateBook(@PathVariable String id, @Valid @RequestBody Book book) {
		if (!book.getIsbn().equals(id))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trying to update book with wrong id!");
		else return repo.findById(id)
	      .map(oldBook -> {
	          oldBook.setTitle(book.getTitle());
	          oldBook.setPublisher(book.getPublisher());
	          oldBook.setCategory(book.getCategory());
	          oldBook.setAuthors(book.getAuthors());
	          oldBook.setDate(book.getDate());
	          oldBook.setLanguage(book.getLanguage());
	          oldBook.setSummary(book.getSummary());
	          repo.save(oldBook);
	          return ResponseEntity.noContent().build();
	        })
	      .orElseThrow(() -> 
	      	new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with given id does not exist!"));
	}
	
	@DeleteMapping("{id}")
	ResponseEntity<?> deleteBook(@PathVariable String id) {
		return repo.findById(id)
			    .map(oldBook -> {
			         repo.deleteById(id);
			         return ResponseEntity.noContent().build();
			    })
			    .orElseThrow(() -> 
			      	 new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with given id does not exist!"));
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(
	  MethodArgumentNotValidException ex) {
	    Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getAllErrors().forEach((error) -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getDefaultMessage();
	        System.out.println("Fieldname is: " + fieldName + " ErrorMessage:" + errorMessage);
	        errors.put(fieldName, errorMessage);
	    });
	    return errors;
	}
}

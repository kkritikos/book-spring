package gr.aegean.bookspring.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Entity
public class Book {
	@Id
	@NotBlank(message = "isbn cannot be blank!")
	@Pattern(
		regexp = "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]$", 
		message = "isbn is invalid"
	)
	private String isbn = null;
	@Basic(optional = false)
	@NotBlank(message = "title cannot be blank!")
    private String title = null;
    private String category = null;
    @Basic(optional = false)
    @NotBlank(message = "publisher cannot be blank!")
    private String publisher = null;
    private String language = null;
    private String summary = null;
    private String date = null;
    @NotEmpty
    @Basic(fetch = FetchType.EAGER)
    private List<String> authors = new ArrayList<String>();
    
    public Book() {}
    
    private Book(Builder builder) {
    	this.isbn = builder.isbn;
    	this.title = builder.title;
    	this.authors = builder.authors;
    	this.publisher = builder.publisher;
    	setCategory(builder.category);
    	setLanguage(builder.language);
    	setSummary(builder.summary);
    	setDate(builder.date);
    	setAuthors(builder.authors);
    }
    
    public static class Builder{
    	private String isbn = null;
        private String title = null;
        private String category = null;
        private List<String> authors = new ArrayList<String>();
        private String publisher = null;
        private String language = null;
        private String summary = null;
        private String date = null;
        
        private static void checkSingleValue(String value, String message) throws IllegalArgumentException{
        	if (value == null || value.trim().equals("")) throw new IllegalArgumentException(message + " cannot be null or empty");
        }
        
        public Builder(String isbn, String title, String publisher) throws IllegalArgumentException{
        	checkSingleValue(isbn, "ISBN");
        	checkSingleValue(title, "Title");
        	checkSingleValue(publisher, "Publisher");
        	
        	this.isbn = isbn;
        	this.title = title;
        	this.publisher = publisher;
        }
        
        public Builder category(String value) {
        	this.category = value;
        	return this;
        }
        
        public Builder language(String value) {
        	this.language = value;
        	return this;
        }
        
        public Builder summary(String value) {
        	this.summary = value;
        	return this;
        }
        
        public Builder date(String value) {
        	this.date = value;
        	return this;
        }
        
        public Builder authors(List<String> authors) {
        	this.authors = authors;
        	return this;
        }
        
        public Book build() {
        	return new Book(this);
        }
    }
    
    public String toString(){
    	return "Book(" + isbn + ", " + title + ", " + authors + ")";
    }

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) throws IllegalArgumentException{
		Builder.checkSingleValue(isbn, "ISBN");
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) throws IllegalArgumentException{
		Builder.checkSingleValue(title, "Title");
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		if (category == null || category.trim().equals("")) this.category = null;
		else this.category = category.trim();
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) throws IllegalArgumentException{
		Builder.checkSingleValue(publisher, "Publisher");
		this.publisher = publisher;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		if (language == null || language.trim().equals("")) this.language = null;
		else this.language = language.trim();
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		if (summary == null || summary.trim().equals("")) this.summary = null;
		else this.summary = summary.trim();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		if (date == null || date.trim().equals("")) this.date = null;
		else this.date = date.trim();
	}
	
	public boolean equals(Object o) {
		if (o instanceof Book) {
			Book b = (Book)o;
			if (b.getIsbn().equals(isbn)) return true;
		}
		
		return false;
	}
}

package com.dwes.book.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dwes.book.entities.Book;
import com.dwes.book.errors.BookNotFoundException;
import com.dwes.book.errors.BookUnSupportedFieldPatchException;
import com.dwes.book.repos.BookRepository;

@Validated
@RestController
public class BookController {

	@Autowired
	private BookRepository repository;
	
	@GetMapping("/books")
	public ResponseEntity<?> getBooks(){
		List<Book> books = repository.findAll();
		
		if(books.isEmpty()) {
			return ResponseEntity.notFound().build();
		}else {
			return ResponseEntity.ok(books);
		}
	}
	
	
	@GetMapping("/books/{id}")
	public ResponseEntity<Book> getaBook(@PathVariable @Min(1) Long id){
		Book book = repository.findById(id).orElse(null);
		if(book == null) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.ok(book);
			
		}
	}
	@PostMapping("/books")
		public ResponseEntity<Book> newBook(@Valid @RequestBody Book newbook){
			Book saved = repository.save(newbook);
			return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
	
	@PutMapping("/books/{id}")
	public  ResponseEntity<Book> SaveOrUpdate(@Valid @RequestBody Book newbook,@PathVariable Long id) {
		return repository.findById(id)
                .map(x -> {
                    x.setName(newbook.getName());
                    x.setAuthor(newbook.getAuthor());
                    x.setPrice(newbook.getPrice());
                    return ResponseEntity.ok(repository.save(x));
                })
                .orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
	}
	
	
	@PatchMapping("/books/{id}")
    Book patch(@RequestBody Map<String, String> update, @PathVariable Long id) {

        return repository.findById(id)
                .map(x -> {

                    String author = update.get("author");
                    if (!StringUtils.isEmpty(author)) {
                        x.setAuthor(author);

                        // better create a custom method to update a value = :newValue where id = :id
                        return repository.save(x);
                    } else {
                        throw new BookUnSupportedFieldPatchException(update.keySet());
                    }

                })
                .orElseGet(() -> {
                    throw new BookNotFoundException(id);
                });

    }
    
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }	
}

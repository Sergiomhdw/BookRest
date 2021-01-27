package com.dwes.book.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dwes.book.entities.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

}

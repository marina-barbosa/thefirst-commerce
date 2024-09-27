package com.gema.thefirst.repositories;

import com.gema.thefirst.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}

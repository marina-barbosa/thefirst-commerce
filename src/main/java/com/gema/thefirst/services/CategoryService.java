package com.gema.thefirst.services;

import com.gema.thefirst.dto.CategoryDTO;
import com.gema.thefirst.entities.Category;
import com.gema.thefirst.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Transactional(readOnly = true)
  public List<CategoryDTO> findAll() {
    List<Category> result = categoryRepository.findAll();
    return result.stream().map(x -> new CategoryDTO(x)).toList();
  }

}

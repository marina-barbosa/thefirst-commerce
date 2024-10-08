package com.gema.thefirst.services;

import com.gema.thefirst.dto.CategoryDTO;
import com.gema.thefirst.dto.ProductDTO;
import com.gema.thefirst.dto.ProductMinDTO;
import com.gema.thefirst.entities.Category;
import com.gema.thefirst.entities.Product;
import com.gema.thefirst.repositories.ProductRepository;
import com.gema.thefirst.services.exceptions.DatabaseException;
import com.gema.thefirst.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.gema.thefirst.constants.Constants.FALHA_INTEGRIDADE_REFERENCIAL;
import static com.gema.thefirst.constants.Constants.RECURSO_NAO_ENCONTRADO;

@Service
public class ProductService {

  @Autowired
  private ProductRepository productRepository;

  @Transactional(readOnly = true)
  public ProductDTO findById(Long id) {
    Product product = productRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException(RECURSO_NAO_ENCONTRADO));
    return new ProductDTO(product);
  }

  @Transactional(readOnly = true)
  public Page<ProductMinDTO> findAll(String name, Pageable pageable) {
    Page<Product> result = productRepository.searchByName(name, pageable);
    return result.map(x -> new ProductMinDTO(x));
  }

  @Transactional
  public ProductDTO insert(ProductDTO dto) {
    Product entity = new Product();
    copyDtoToEntity(dto, entity);
    entity = productRepository.save(entity);
    return new ProductDTO(entity);
  }

  @Transactional
  public ProductDTO update(Long id, ProductDTO dto) {
    try {
      Product entity = productRepository.getReferenceById(id);
      copyDtoToEntity(dto, entity);
      entity = productRepository.save(entity);
      return new ProductDTO(entity);
    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException(RECURSO_NAO_ENCONTRADO);
    }
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public void delete(Long id) {
    if (!productRepository.existsById(id)) {
      throw new ResourceNotFoundException(RECURSO_NAO_ENCONTRADO);
    }
    try {
      productRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      throw new DatabaseException(FALHA_INTEGRIDADE_REFERENCIAL);
    }
  }

  private void copyDtoToEntity(ProductDTO dto, Product entity) {
    entity.setId(dto.getId());
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setPrice(dto.getPrice());
    entity.setImgUrl(dto.getImgUrl());

    entity.getCategories().clear();
    for (CategoryDTO catDto : dto.getCategories()) {
      Category cat = new Category();
      cat.setId(catDto.getId());
      entity.getCategories().add(cat);
    }
  }

}

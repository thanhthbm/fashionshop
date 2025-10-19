package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.dto.CategoryDTO;
import com.thanhthbm.fashionshop.dto.CategoryTypeDTO;
import com.thanhthbm.fashionshop.entity.Category;
import com.thanhthbm.fashionshop.entity.CategoryType;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import com.thanhthbm.fashionshop.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  @Autowired
  private CategoryRepository categoryRepository;

  public Category getCategory(UUID id) {
    Optional<Category> category = categoryRepository.findById(id);
    return category.orElse(null);
  }

  public Category createCategory(CategoryDTO categoryDTO ) {
    Category category = mapToEntity(categoryDTO);
    return categoryRepository.save(category);
  }

  private Category mapToEntity(CategoryDTO categoryDTO) {
    Category category = Category.builder()
        .code(categoryDTO.getCode())
        .description(categoryDTO.getDescription())
        .name(categoryDTO.getName())
        .build();
    if (null != categoryDTO.getCategoryTypeList()){
      List<CategoryType> categoryTypes = mapToCategoryTypesList(categoryDTO.getCategoryTypeList(), category);
      category.setCategoryTypes( categoryTypes );
    }

    return category;
  }

  private List<CategoryType> mapToCategoryTypesList(List<CategoryTypeDTO> categoryTypeList, Category category) {
    return categoryTypeList.stream().map(categoryTypeDTO -> {
      CategoryType categoryType = new CategoryType();
      categoryType.setCode(categoryTypeDTO.getCode());
      categoryType.setDescription(categoryTypeDTO.getDescription());
      categoryType.setName(categoryTypeDTO.getName());
      categoryType.setCategory(category);
      return categoryType;
    }).collect(Collectors.toList());
  }

  public List<Category> getAllCategories() {
    return  categoryRepository.findAll();
  }

  public Category updateCategory(CategoryDTO categoryDto, UUID categoryId) {
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(()-> new ResourceNotFoundException("Category not found with Id "+categoryDto.getId()));

    if(null != categoryDto.getName()){
      category.setName(categoryDto.getName());
    }
    if(null != categoryDto.getCode()){
      category.setCode(categoryDto.getCode());
    }
    if(null != categoryDto.getDescription()){
      category.setDescription(categoryDto.getDescription());
    }

    List<CategoryType> existing = category.getCategoryTypes();
    List<CategoryType> list= new ArrayList<>();

    if(categoryDto.getCategoryTypeList() != null){
      categoryDto.getCategoryTypeList().forEach(categoryTypeDto -> {
        if(null != categoryTypeDto.getId()){
          Optional<CategoryType> categoryType = existing.stream().filter(t -> t.getId().equals(categoryTypeDto.getId())).findFirst();
          CategoryType categoryType1= categoryType.get();
          categoryType1.setCode(categoryTypeDto.getCode());
          categoryType1.setName(categoryTypeDto.getName());
          categoryType1.setDescription(categoryTypeDto.getDescription());
          list.add(categoryType1);
        }
        else{
          CategoryType categoryType = new CategoryType();
          categoryType.setCode(categoryTypeDto.getCode());
          categoryType.setName(categoryTypeDto.getName());
          categoryType.setDescription(categoryTypeDto.getDescription());
          categoryType.setCategory(category);
          list.add(categoryType);
        }
      });
    }
    category.setCategoryTypes(list);

    return  categoryRepository.save(category);
  }

  public void deleteCategory(UUID categoryId) {
    categoryRepository.deleteById(categoryId);
  }
}

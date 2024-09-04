package com.emazon.stock_api_service.domain.usecase;

import com.emazon.stock_api_service.domain.api.IArticleServicePort;
import com.emazon.stock_api_service.domain.exception.ArticleUseCaseException;
import com.emazon.stock_api_service.domain.exception.ResourceNotFoundException;
import com.emazon.stock_api_service.domain.model.Article;
import com.emazon.stock_api_service.domain.model.Brand;
import com.emazon.stock_api_service.domain.model.Category;
import com.emazon.stock_api_service.domain.spi.IArticlePersistencePort;
import com.emazon.stock_api_service.domain.spi.IBrandPersistencePort;
import com.emazon.stock_api_service.domain.spi.ICategoryPersistencePort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.emazon.stock_api_service.util.ArticleConstants.*;
import static com.emazon.stock_api_service.util.BrandConstants.BRAND_NOT_FOUND;
import static com.emazon.stock_api_service.util.CategoryConstants.CATEGORY_NOT_FOUND;

public class ArticleUseCase implements IArticleServicePort {
    private final IArticlePersistencePort articlePersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;
    private final IBrandPersistencePort brandPersistencePort;

    public ArticleUseCase(IArticlePersistencePort articlePersistencePort
    , ICategoryPersistencePort categoryPersistencePort
    ,IBrandPersistencePort brandPersistencePort) {
        this.articlePersistencePort = articlePersistencePort;
        this.categoryPersistencePort=categoryPersistencePort;
        this.brandPersistencePort=brandPersistencePort;
    }

    @Override
    public void createArticle(Article article) {
        validate(article);
        article.setBrand(brandPersistencePort
                .getBrandById(article.getBrand().getId()));
        List<Category> categoriesToAdd = new ArrayList<>();
        for(Category category : article.getCategories()) {
            categoriesToAdd.add(categoryPersistencePort
                    .getCategoryById(category.getId()));
        }
        article.setCategories(categoriesToAdd);
        articlePersistencePort.createArticle(article);
    }

    @Override
    public Article getArticleById(Long id) {
        if(Boolean.FALSE.equals(idExists(id))) {
            throw new ResourceNotFoundException(ARTICLE_NOT_FOUND);
        }
        return articlePersistencePort.getArticleById(id);
    }

    @Override
    public List<Article> getArticles() {
        return List.of();
    }

    @Override
    public List<Article> getArticlesByCategoryName(String name) {
        return List.of();
    }

    @Override
    public List<Article> getArticlesByBrandName(String name) {
        return List.of();
    }

    @Override
    public void validate(Article article) {
        List<String> errorList=new ArrayList<>();
        validateBrand(article.getBrand(),errorList);
        validateCategories(article.getCategories(),errorList);
        if(!errorList.isEmpty()) {
            throw new ArticleUseCaseException(errorList);
        }
    }

    public void validateArticle(Article article,List<String> errorList){
//        there's no restrictions in the article, it could all be null
    }
    public void validateBrand(Brand brand, List<String> errorList){
        //the brand object is null, or its id attribute is null
        if(brand==null || brand.getId()==null){
            errorList.add(BRAND_OBLIGATORY);
        }
        else if(Boolean.FALSE.equals(brandIdExists(brand.getId()))){
            errorList.add(BRAND_NOT_FOUND);
        }
    }
    public void validateCategories(List<Category> categories,List<String> errorList){
        //we separate both conditionals so we don't have to go into the else
        if(categories==null || categories.isEmpty()){
            if(MINIMUM_CATEGORIES_ASSOCIATED>0){
                errorList.add(MINIMUM_CATEGORY);
            }
        }
        else{
            List<Long> categoryIds=categories.stream()
                    .map(Category::getId)
                    .toList();
            validateRepeatedCategories(categoryIds,errorList);
            if(MINIMUM_CATEGORIES_ASSOCIATED>categoryIds.size()){
                errorList.add(MINIMUM_CATEGORY);
            }
            else if(MAXIMUM_CATEGORIES_ASSOCIATED<categoryIds.size()){
                errorList.add(MAXIMUM_CATEGORY);
            }
        }
    }

    private void validateRepeatedCategories(
            List<Long> categoryIds,List<String> errorList){
        Map<Long, Long> hashMap = new HashMap<>();
        for(Long categoryId : categoryIds) {
            if(Boolean.TRUE.equals(categoryIdExists(categoryId))) {
                if(!hashMap.containsKey(categoryId)) {
                    hashMap.put(categoryId, 1L);
                }
                //we check if the id exists 1 time
                else if(hashMap.get(categoryId)==1L){
                    errorList.add(CATEGORY_REPEATED);
                    hashMap.put(categoryId,2L);
                }
            }
            else{
                errorList.add(CATEGORY_NOT_FOUND);
            }
        }
    }

    public Boolean idExists(Long id) {
        return articlePersistencePort.articleIdExists(id);
    }
    public Boolean nameExists(String name) {
        return articlePersistencePort.articleNameExists(name);
    }
    public Boolean categoryNameExists(String name) {
        return categoryPersistencePort.categoryNameExists(name);
    }
    public Boolean categoryIdExists(Long id) {
        return categoryPersistencePort.categoryIdExists(id);
    }
    public Boolean brandIdExists(Long id) {
        return brandPersistencePort.brandIdExists(id);
    }
}

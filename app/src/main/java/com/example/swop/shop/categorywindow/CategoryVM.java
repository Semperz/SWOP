package com.example.swop.shop.categorywindow;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.swop.data.remote.models.CategoryDto;
import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.data.repository.CategoryRepository;
import com.example.swop.data.repository.ProductRepository;
import com.example.swop.data.util.ApiCallback;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CategoryVM extends AndroidViewModel {
    private final MutableLiveData<List<ProductDto>> productsLiveData = new MutableLiveData<>();
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public CategoryVM(@NonNull Application application) {
        super(application);
        this.productRepo = new ProductRepository(application);
        this.categoryRepo = new CategoryRepository(application);
    }

    public LiveData<List<ProductDto>> getProductsLiveData() {
        return productsLiveData;
    }

    public void loadProductsByCategoryName(String categoryName) {
        categoryRepo.getAll(new ApiCallback<List<CategoryDto>>() {
            @Override
            public void onSuccess(List<CategoryDto> categories) {
                Optional<CategoryDto> match = categories.stream()
                        .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                        .findFirst();
                if (match.isPresent()) {
                    long categoryId = match.get().getId();
                    loadProductsByCategoryId(categoryId);
                } else {
                    Log.w("CategoryVM", "Categoría no encontrada: " + categoryName);
                    productsLiveData.postValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("CategoryVM", "Error al obtener categorías", t);
                productsLiveData.postValue(Collections.emptyList());
            }
        });
    }

    private void loadProductsByCategoryId(long categoryId) {
        productRepo.getByCategory(categoryId, new ApiCallback<List<ProductDto>>() {
            @Override
            public void onSuccess(List<ProductDto> result) {
                productsLiveData.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("CategoryVM", "Error al cargar productos", t);
                productsLiveData.postValue(Collections.emptyList());
            }
        });
    }
}

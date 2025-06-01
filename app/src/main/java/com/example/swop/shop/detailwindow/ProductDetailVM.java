package com.example.swop.shop.detailwindow;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.data.repository.ProductRepository;
import com.example.swop.data.util.ApiCallback;

public class ProductDetailVM extends AndroidViewModel {

    private final MutableLiveData<ProductDto> productLiveData = new MutableLiveData<>();
    private final ProductRepository productRepository;

    public ProductDetailVM(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application.getApplicationContext()); // o inyectar si usas DI
    }

    public LiveData<ProductDto> getProductLiveData() {
        return productLiveData;
    }


    public void loadProductById(Long productId) {
        productRepository.getById(productId, new ApiCallback<ProductDto>() {
            @Override
            public void onSuccess(ProductDto product) {
                productLiveData.postValue(product);
            }

            @Override
            public void onFailure(Throwable t) {
                productLiveData.postValue(null); // o manejar error distinto
            }
        });
    }
}

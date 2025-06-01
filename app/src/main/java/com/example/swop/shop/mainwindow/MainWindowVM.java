package com.example.swop.shop.mainwindow;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.data.repository.ProductRepository;
import com.example.swop.data.util.ApiCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainWindowVM extends AndroidViewModel {

    private final ProductRepository productRepository;
    private final MutableLiveData<List<ProductDto>> productsLiveData = new MutableLiveData<>();

    public LiveData<List<ProductDto>> getProductsLiveData() {
        return productsLiveData;
    }

    public MainWindowVM(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application.getApplicationContext());
        loadRandomProducts();
    }

    // 3 productos aleatorios (recomendados)
    public void loadRandomProducts() {
        productRepository.getAll(new ApiCallback<List<ProductDto>>() {
            @Override
            public void onSuccess(List<ProductDto> allProducts) {
                if (allProducts == null || allProducts.isEmpty()) {
                    productsLiveData.postValue(Collections.emptyList());
                    return;
                }

                List<ProductDto> randomProducts = new ArrayList<>();
                Random rnd = new Random();

                // No repetir -> Set
                Set<Integer> indices = new HashSet<>();
                int maxItems = Math.min(3, allProducts.size());

                while (indices.size() < maxItems) {
                    indices.add(rnd.nextInt(allProducts.size()));
                }

                for (int i : indices) {
                    randomProducts.add(allProducts.get(i));
                }

                productsLiveData.postValue(randomProducts);
                Log.d("MainWindowVM", "Productos recibidos: " + randomProducts.size());
            }

            @Override
            public void onFailure(Throwable t) {
                // Opcional: manejar error aquí (logs, mensaje, etc)
                productsLiveData.postValue(Collections.emptyList());
            }
        });
    }

    // Busca productos filtrando por nombre
    public void searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadRandomProducts();
            return;
        }

        productRepository.getAll(new ApiCallback<List<ProductDto>>() {
            @Override
            public void onSuccess(List<ProductDto> allProducts) {
                if (allProducts == null || allProducts.isEmpty()) {
                    productsLiveData.postValue(Collections.emptyList());
                    return;
                }

                List<ProductDto> filtered = new ArrayList<>();
                String q = query.toLowerCase();

                for (ProductDto p : allProducts) {
                    if (p.getName() != null && p.getName().toLowerCase().contains(q)) {
                        filtered.add(p);
                    }
                }
                productsLiveData.postValue(filtered);
            }

            @Override
            public void onFailure(Throwable t) {
                productsLiveData.postValue(Collections.emptyList());
            }
        });
    }
}

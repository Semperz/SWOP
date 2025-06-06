package com.example.swop.bids;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.swop.data.remote.models.BidDto;
import com.example.swop.data.remote.models.BidStatus;
import com.example.swop.data.remote.models.CustomerDto;
import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.data.repository.BidRepository;
import com.example.swop.data.repository.CustomerRepository;
import com.example.swop.data.repository.ProductRepository;
import com.example.swop.data.util.ApiCallback;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class BidsVM extends AndroidViewModel {

    private final ProductRepository productRepo;
    private final BidRepository bidRepo;
    private final CustomerRepository customerRepo;

    private final MutableLiveData<ProductDto> currentProduct = new MutableLiveData<>();
    private final MutableLiveData<BidDto> highestBid = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> bidSuccess = new MutableLiveData<>();

    public BidsVM(@NonNull Application app) {
        super(app);
        productRepo = new ProductRepository(app);
        bidRepo = new BidRepository(app);
        customerRepo = new CustomerRepository(app);
        loadRandomProduct();
    }

    public MutableLiveData<ProductDto> getSelectedProduct() {
        return currentProduct;
    }

    public MutableLiveData<BidDto> getHighestBid() {
        return highestBid;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Boolean> getBidSuccess() {
        return bidSuccess;
    }

    public void clearMessage() {
        message.postValue(null);
    }

    public BigDecimal getMinimumBidAmount() {
        BidDto bid = highestBid.getValue();
        if (bid != null && bid.getBidAmount() != null) {
            return bid.getBidAmount();
        } else {
            ProductDto product = currentProduct.getValue();
            if (product != null && product.getPrice() != null) {
                return product.getPrice().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.ZERO;
            }
        }
    }

    private void loadRandomProduct() {
        productRepo.getAll(new ApiCallback<>() {
            @Override
            public void onSuccess(List<ProductDto> products) {
                if (products == null || products.isEmpty()) {
                    message.postValue("No hay productos disponibles.");
                    return;
                }
                ProductDto selected = products.get(new Random().nextInt(products.size()));
                currentProduct.postValue(selected);

                // Actualiza el stock (ejemplo: -1 al entrar)
                ProductDto updatedProduct = new ProductDto(selected.getId(), selected.getSku(), selected.getName(),
                        selected.getPrice(), selected.getWeight(), selected.getDescriptions(),
                        selected.getThumbnail(), selected.getImage(), selected.getCategoryIds(),
                        (selected.getStock() - 1), selected.getCreateDate()
                );
                Log.d("ProductUpdate", "Intentando actualizar producto con ID: " + updatedProduct.getId());
                Log.d("ProductUpdate", "Producto DTO: " + new Gson().toJson(updatedProduct));
                Log.d("ProductUpdate", "ListaCat" + updatedProduct.getCategoryIds());
                Log.d("ProductUpdate", "ListaCatSelected" + selected.getCategoryIds());
                productRepo.update(selected.getId(), updatedProduct, new ApiCallback<ProductDto>() {
                    @Override
                    public void onSuccess(ProductDto result) {
                        Log.d("ProductUpdate", "Producto actualizado: " + result.getName());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("ProductUpdate", "Error actualizando producto", throwable);
                    }
                });

                loadHighestBid(selected);
            }

            @Override
            public void onFailure(Throwable t) {
                message.postValue("Error cargando productos.");
            }
        });
    }

    private void loadHighestBid(ProductDto product) {
        bidRepo.getHighest(product.getId(), new ApiCallback<>() {
            @Override
            public void onSuccess(BidDto bid) {
                if (bid != null) {
                    highestBid.postValue(bid);
                } else {
                    // No hay puja: usar mitad del precio
                    BigDecimal halfPrice = product.getPrice().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                    BidDto pseudoBid = new BidDto(
                            null,
                            product.getId(),
                            null,
                            halfPrice,
                            null,
                            null
                    );
                    highestBid.postValue(pseudoBid);
                }
            }

            @Override
            public void onFailure(Throwable t) {
               Log.e("BidsVM", "Error cargando puja más alta", t);
                message.postValue("Error cargando puja más alta.");
            }
        });
    }

    public void placeOrUpdateBid(BigDecimal amount, Runnable onFinish) {
        ProductDto product = currentProduct.getValue();
        if (product == null) {
            message.postValue("Producto no cargado.");
            if (onFinish != null) onFinish.run();
            return;
        }

        customerRepo.me(new ApiCallback<CustomerDto>() {
            @Override
            public void onSuccess(CustomerDto customer) {
                Long customerId = customer.getId();
                Long productId = product.getId();

                bidRepo.getByProduct(productId, new ApiCallback<List<BidDto>>() {
                    @Override
                    public void onSuccess(List<BidDto> bids) {
                        BidDto existingBid = null;

                        if (bids != null) {
                            for (BidDto b : bids) {
                                if (b.getCustomerId().equals(customerId)) {
                                    existingBid = b;
                                    break;
                                }
                            }
                        }

                        if (existingBid != null) {
                            // Actualizar puja existente
                            existingBid.setBidAmount(amount);
                            existingBid.setBidTime(LocalDateTime.now());
                            bidRepo.updateBid(existingBid.getId(), existingBid, new ApiCallback<BidDto>() {
                                @Override
                                public void onSuccess(BidDto updatedBid) {
                                    bidSuccess.postValue(true);
                                    loadHighestBid(product);
                                    if (onFinish != null) onFinish.run();
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    message.postValue("El servidor no pudo actualizar la puja. Intenta más tarde.");
                                    if (onFinish != null) onFinish.run();
                                }
                            });
                        } else {
                            // No hay puja previa: permitir placeBid con la cantidad pasada
                            BigDecimal minBid = product.getPrice().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                            if (amount.compareTo(minBid) < 0) {
                                message.postValue("La cantidad debe ser al menos la mitad del precio del producto.");
                                if (onFinish != null) onFinish.run();
                                return;
                            }
                            BidDto newBid = new BidDto(
                                    null,
                                    productId,
                                    customerId,
                                    amount,
                                    LocalDateTime.now(),
                                    BidStatus.PENDING
                            );
                            bidRepo.placeBid(newBid, new ApiCallback<BidDto>() {
                                @Override
                                public void onSuccess(BidDto createdBid) {
                                    bidSuccess.postValue(true);
                                    loadHighestBid(product);
                                    if (onFinish != null) onFinish.run();
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    message.postValue("El servidor no pudo crear la puja. Intenta más tarde.");
                                    if (onFinish != null) onFinish.run();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (t.getMessage() != null && t.getMessage().contains("HTTP 204")) {
                            // No hay pujas: permitir placeBid con la cantidad pasada
                            BigDecimal minBid = product.getPrice().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                            if (amount.compareTo(minBid) < 0) {
                                message.postValue("La cantidad debe ser al menos la mitad del precio del producto.");
                                if (onFinish != null) onFinish.run();
                                return;
                            }
                            BidDto newBid = new BidDto(
                                    null,
                                    productId,
                                    customerId,
                                    amount,
                                    LocalDateTime.now(),
                                    BidStatus.PENDING
                            );
                            bidRepo.placeBid(newBid, new ApiCallback<BidDto>() {
                                @Override
                                public void onSuccess(BidDto createdBid) {
                                    bidSuccess.postValue(true);
                                    loadHighestBid(product);
                                    if (onFinish != null) onFinish.run();
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    message.postValue("El servidor no pudo crear la puja. Intenta más tarde.");
                                    if (onFinish != null) onFinish.run();
                                }
                            });
                        } else {
                            message.postValue("El servidor no pudo cargar las pujas. Intenta más tarde.");
                            if (onFinish != null) onFinish.run();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                message.postValue("El servidor no pudo obtener el usuario. Intenta más tarde.");
                if (onFinish != null) onFinish.run();
            }
        });
    }
}
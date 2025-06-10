package com.example.swop.shop.cartwindow;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.swop.data.remote.models.CustomerDto;
import com.example.swop.data.remote.models.OrderDetailDto;
import com.example.swop.data.remote.models.OrderDto;
import com.example.swop.data.remote.models.OrderStatus;
import com.example.swop.data.remote.models.PaymentMethod;
import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.data.repository.CustomerRepository;
import com.example.swop.data.repository.OrderRepository;
import com.example.swop.data.repository.ProductRepository;
import com.example.swop.data.session.SessionManager;
import com.example.swop.data.util.ApiCallback;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CartVM extends AndroidViewModel {

    private final MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> totalLiveData = new MutableLiveData<>(0.0);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final CustomerRepository customerRepo;
    private final SessionManager sessionManager;

    public CartVM(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application.getApplicationContext());
        orderRepository = new OrderRepository(application.getApplicationContext());
        sessionManager = new SessionManager(application.getApplicationContext());
        customerRepo = new CustomerRepository(application.getApplicationContext());
    }

    public LiveData<List<CartItem>> getCartItemsLiveData() {
        return cartItemsLiveData;
    }

    public LiveData<Double> getTotalLiveData() {
        return totalLiveData;
    }


    public void loadCart() {
        String userId = sessionManager.getUserEmail();
        if (userId == null) {
            cartItemsLiveData.postValue(new ArrayList<>());
            totalLiveData.postValue(0.0);
            return;
        }
        Map<Long, Integer> cart = CartPreferences.getCart(getApplication(), userId);
        List<Long> orderedIds = new ArrayList<>(cart.keySet()); // lista auxiliar para mantener el orden
        List<CartItem> items = new ArrayList<>(Collections.nCopies(orderedIds.size(), null));
        double[] total = {0.0};
        if (cart.isEmpty()) {
            cartItemsLiveData.postValue(new ArrayList<>());
            totalLiveData.postValue(0.0);
            return;
        }
        final int[] loadedCount = {0};
        for (int i = 0; i < orderedIds.size(); i++) {
            final int index = i;
            long productId = orderedIds.get(i);
            int quantity = cart.get(productId);
            productRepository.getById(productId, new ApiCallback<>() {
                @Override
                public void onSuccess(ProductDto product) {
                    items.set(index, new CartItem(product, quantity));
                    total[0] += product.getPrice().doubleValue() * quantity;
                    loadedCount[0]++;
                    if (loadedCount[0] == orderedIds.size()) {
                        // Todos los productos cargados
                        cartItemsLiveData.postValue(new ArrayList<>(items));
                        totalLiveData.postValue(total[0]);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    loadedCount[0]++;
                    if (loadedCount[0] == orderedIds.size()) {
                        cartItemsLiveData.postValue(
                                items.stream().filter(Objects::nonNull).collect(Collectors.toList())
                        );
                        totalLiveData.postValue(total[0]);
                    }
                }
            });
        }
    }

    public void increaseQuantity(ProductDto product) {
        String userId = sessionManager.getUserEmail();
        if (userId == null) return;
        Map<Long, Integer> cart = CartPreferences.getCart(getApplication(), userId);
        int current = cart.getOrDefault(product.getId(), 1);
        int stock = product.getStock() != null ? product.getStock() : Integer.MAX_VALUE;
        if (current < stock) {
            cart.put(product.getId(), current + 1);
            CartPreferences.saveCart(getApplication(), userId, cart);
            loadCart();
        } else {
            // No hay suficiente stock
            Toast.makeText(getApplication(), "No hay suficiente stock", Toast.LENGTH_LONG).show();
        }
    }

    public void decreaseQuantity(ProductDto product) {
        String userId = sessionManager.getUserEmail();
        if (userId == null) return;
        Map<Long, Integer> cart = CartPreferences.getCart(getApplication(), userId);
        int current = cart.getOrDefault(product.getId(), 1);
        if (current > 1) {
            cart.put(product.getId(), current - 1);
        } else {
            cart.remove(product.getId());
        }
        CartPreferences.saveCart(getApplication(), userId, cart);
        loadCart();
    }

    public void clearCart() {
        String userId = sessionManager.getUserEmail();
        if (userId == null) return;
        CartPreferences.clearCart(getApplication(), userId);
        loadCart();
    }


    public void createOrder(PaymentMethod paymentMethod, ApiCallback<OrderDto> callback) {
        List<CartItem> cartItems = cartItemsLiveData.getValue();
        if (cartItems == null || cartItems.isEmpty()) {
            callback.onFailure(new Exception("Carrito vacío"));
            return;
        }

        // Obtener datos del usuario actual
        customerRepo.me(new ApiCallback<>() {
            @Override
            public void onSuccess(CustomerDto customer) {
                Set<OrderDetailDto> details = new HashSet<>();
                double total = 0.0;
                for (CartItem item : cartItems) {
                    OrderDetailDto detail = new OrderDetailDto();
                    detail.setProduct(item.getProduct().getId());
                    detail.setPrice(item.getProduct().getPrice());
                    detail.setQuantity(item.getQuantity());
                    detail.setSku(item.getProduct().getSku());
                    details.add(detail);
                    total += item.getProduct().getPrice().doubleValue() * item.getQuantity();
                }

                OrderDto order = new OrderDto();
                order.setOrderDetails(details);
                order.setAmount(BigDecimal.valueOf(total));
                order.setOrderDate(LocalDateTime.now());
                order.setOrderStatus(OrderStatus.PROCESSED);
                order.setPaymentMethod(paymentMethod);
                order.setShippingAddress(customer.getDefaultShippingAddress());
                order.setOrderAddress(customer.getDefaultShippingAddress());
                order.setOrderEmail(customer.getEmail());
                order.setFromAuction(false);

                orderRepository.create(order, new ApiCallback<>() {
                    @Override
                    public void onSuccess(OrderDto order) {
                        // Restar stock a cada producto
                        for (CartItem item : cartItems) {
                            ProductDto product = item.getProduct();
                            int newStock = (product.getStock() != null ? product.getStock() : 0) - item.getQuantity();
                            if (newStock < 0) newStock = 0;
                            ProductDto updatedProduct = new ProductDto();
                            updatedProduct.setName(product.getName());
                            updatedProduct.setDescriptions(product.getDescriptions());
                            updatedProduct.setPrice(product.getPrice());
                            updatedProduct.setSku(product.getSku());
                            updatedProduct.setCategoryIds(product.getCategoryIds());
                            updatedProduct.setImage(product.getImage());
                            updatedProduct.setStock(newStock);
                            updatedProduct.setAuctionEndTime(product.getAuctionEndTime());
                            updatedProduct.setWeight(product.getWeight());
                            updatedProduct.setCreateDate(product.getCreateDate());
                            updatedProduct.setThumbnail(product.getThumbnail());

                            productRepository.update(product.getId(), updatedProduct, new ApiCallback<>() {
                                @Override
                                public void onSuccess(ProductDto result) {

                                }

                                @Override
                                public void onFailure(Throwable t) {
                                }
                            });
                        }
                        callback.onSuccess(order);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onFailure(t);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

}
package com.example.swop.bids;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.swop.data.remote.models.BidDto;
import com.example.swop.data.remote.models.BidStatus;
import com.example.swop.data.remote.models.CustomerDto;
import com.example.swop.data.remote.models.OrderDetailDto;
import com.example.swop.data.remote.models.OrderDto;
import com.example.swop.data.remote.models.OrderStatus;
import com.example.swop.data.remote.models.PaymentMethod;
import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.data.repository.BidRepository;
import com.example.swop.data.repository.CustomerRepository;
import com.example.swop.data.repository.OrderRepository;
import com.example.swop.data.repository.ProductRepository;
import com.example.swop.data.util.ApiCallback;
import com.example.swop.data.util.NotifUtils;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BidsVM extends AndroidViewModel {

    private final ProductRepository productRepo;
    private final BidRepository bidRepo;
    private final CustomerRepository customerRepo;

    private final OrderRepository orderRepo;
    private final MutableLiveData<Long> timeLeftMillis = new MutableLiveData<>();

    private final MutableLiveData<ProductDto> currentProduct = new MutableLiveData<>();
    private final MutableLiveData<BidDto> highestBid = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> bidSuccess = new MutableLiveData<>();


    public BidsVM(@NonNull Application app) {
        super(app);
        productRepo = new ProductRepository(app);
        bidRepo = new BidRepository(app);
        customerRepo = new CustomerRepository(app);
        orderRepo = new OrderRepository(app);
        loadAuctionProduct();
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
    public MutableLiveData<Long> getTimeLeftMillis() { return timeLeftMillis; }

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

    public void loadAuctionProduct() {
        productRepo.getAuctedProduct(new ApiCallback<ProductDto>() {
            @Override
            public void onSuccess(ProductDto product) {
                if (product == null || product.getAuctionEndTime() == null) {
                    currentProduct.postValue(null);
                    timeLeftMillis.postValue(0L);
                    message.postValue("No hay producto en subasta actualmente.");
                    return;
                }
                currentProduct.postValue(product);
                updateTimeLeft(product.getAuctionEndTime());
                loadHighestBid(product);
            }

            @Override
            public void onFailure(Throwable t) {
                message.postValue("Error cargando producto en subasta.");
            }
        });
    }

    private void updateTimeLeft(LocalDateTime auctionEndTime) {
        // Restar 2 horas manualmente por UTC
        LocalDateTime adjustedEndTime = auctionEndTime.minusHours(2);
        ZonedDateTime endUtc = adjustedEndTime.atZone(java.time.ZoneOffset.UTC);
        ZonedDateTime nowUtc = ZonedDateTime.now(java.time.ZoneOffset.UTC);
        long millis = Duration.between(nowUtc, endUtc).toMillis();
        timeLeftMillis.postValue(Math.max(millis, 0));
    }

    private void loadHighestBid(ProductDto product) {
        bidRepo.getHighest(product.getId(), new ApiCallback<BidDto>() {
            @Override
            public void onSuccess(BidDto bid) {
                if (bid != null) {
                    highestBid.postValue(bid);
                } else {
                    BigDecimal halfPrice = product.getPrice().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                    BidDto pseudoBid = new BidDto(null, product.getId(), null, halfPrice, null, null);
                    highestBid.postValue(pseudoBid);
                }
            }

            @Override
            public void onFailure(Throwable t) {
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

    public void nextAuctionProduct() {
        ProductDto oldProduct = currentProduct.getValue();
        if (oldProduct == null) {
            message.postValue("No hay producto anterior.");
            return;
        }

        LocalDateTime lastEndTime = oldProduct.getAuctionEndTime();

        customerRepo.me(new ApiCallback<>() {
            @Override
            public void onSuccess(CustomerDto me) {
                Long myId = me.getId();
                bidRepo.getHighest(oldProduct.getId(), new ApiCallback<BidDto>() {
                    @Override
                    public void onSuccess(BidDto highest) {
                        if (highest != null) {
                            Log.d("BidsVM", "Puja ganadora: " + highest.getBidAmount() + " por el cliente " + highest.getCustomerId());
                            createOrderForWinner(oldProduct, highest);
                            message.postValue("Subasta finalizada. Ganador: " + highest.getCustomerId() + " con puja de " + highest.getBidAmount());

                            if (highest.getCustomerId().equals(myId)) {
                                NotifUtils.show(
                                        getApplication().getApplicationContext(),
                                        "¡Has ganado la subasta!",
                                        "Felicidades, has ganado el producto " + oldProduct.getName()
                                );
                            } else {
                                NotifUtils.show(
                                        getApplication().getApplicationContext(),
                                        "Subasta finalizada",
                                        "No has ganado la subasta de " + oldProduct.getName()
                                );
                            }

                            // Eliminar todas las pujas del producto
                            bidRepo.getByProduct(oldProduct.getId(), new ApiCallback<List<BidDto>>() {
                                @Override
                                public void onSuccess(List<BidDto> bids) {
                                    if (bids != null) {
                                        for (BidDto bid : bids) {
                                            bidRepo.deleteBid(bid.getId(), new ApiCallback<>() {
                                                @Override public void onSuccess(Boolean ok) {}
                                                @Override public void onFailure(Throwable t) {}
                                            });
                                        }
                                    }
                                }
                                @Override public void onFailure(Throwable t) {}
                            });
                        } else {
                            NotifUtils.show(
                                    getApplication().getApplicationContext(),
                                    "Subasta finalizada",
                                    "No hubo pujas para " + oldProduct.getName()
                            );
                        }

                        // Poner auctionEndTime a null en el producto anterior
                        oldProduct.setAuctionEndTime(null);
                        productRepo.update(oldProduct.getId(), oldProduct, new ApiCallback<>() {
                            @Override
                            public void onSuccess(ProductDto updatedOld) {
                                // Obtener todos los productos
                                productRepo.getAll(new ApiCallback<>() {
                                    @Override
                                    public void onSuccess(List<ProductDto> products) {
                                        if (products == null || products.isEmpty()) {
                                            message.postValue("No hay productos disponibles para subastar.");
                                            loadAuctionProduct();
                                            return;
                                        }
                                        // Elegir uno aleatorio distinto al anterior
                                        products.removeIf(p -> p.getId().equals(oldProduct.getId()));
                                        if (products.isEmpty()) {
                                            message.postValue("No hay productos nuevos para subastar.");
                                            loadAuctionProduct();
                                            return;
                                        }
                                        ProductDto newProduct = products.get(new Random().nextInt(products.size()));
                                        // Asignar auctionEndTime: misma hora que terminó el anterior + 1 día
                                        if (lastEndTime != null) {
                                            LocalDateTime newEnd = lastEndTime.plusDays(1);
                                            newProduct.setAuctionEndTime(newEnd);
                                        }
                                        productRepo.update(newProduct.getId(), newProduct, new ApiCallback<ProductDto>() {
                                            @Override
                                            public void onSuccess(ProductDto p) {
                                                loadAuctionProduct();
                                            }

                                            @Override
                                            public void onFailure(Throwable t) {
                                                message.postValue("Error al actualizar el nuevo producto en subasta.");
                                                loadAuctionProduct();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        message.postValue("Error al obtener productos.");
                                        loadAuctionProduct();
                                    }
                                });
                            }
                            @Override
                            public void onFailure(Throwable t) {
                                message.postValue("Error al actualizar el producto anterior.");
                                loadAuctionProduct();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        message.postValue("Error al obtener la puja ganadora.");
                        loadAuctionProduct();
                    }
                });
            }
            @Override
            public void onFailure(Throwable t) {
                message.postValue("No se pudo obtener el usuario actual.");
                loadAuctionProduct();
            }
        });
    }

    private void createOrderForWinner(ProductDto product, BidDto winningBid) {
        if (winningBid == null) return;

        customerRepo.getById(winningBid.getCustomerId(), new ApiCallback<>() {
            @Override
            public void onSuccess(CustomerDto customer) {
                OrderDetailDto detail = new OrderDetailDto();
                detail.setProduct(product.getId());
                detail.setPrice(winningBid.getBidAmount());
                detail.setQuantity(1);
                detail.setSku(product.getSku());

                Set<OrderDetailDto> details = new HashSet<>();
                details.add(detail);

                OrderDto order = new OrderDto();
                order.setOrderDetails(details);
                order.setAmount(winningBid.getBidAmount());
                order.setOrderDate(java.time.LocalDateTime.now());
                order.setOrderStatus(OrderStatus.PROCESSED);
                order.setPaymentMethod(PaymentMethod.PAYPAL);
                order.setShippingAddress(customer.getDefaultShippingAddress());
                order.setOrderAddress(customer.getDefaultShippingAddress());
                order.setOrderEmail(customer.getEmail());
                order.setFromAuction(true);
                Log.d("OrderDebug", new Gson().toJson(order));
                orderRepo.create(order, new ApiCallback<>() {
                    @Override
                    public void onSuccess(OrderDto createdOrder) {
                        Log.d("OrderDebug", "Pedido creado: " + createdOrder.getId());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("OrderDebug", "Error al crear el pedido: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplication(), "Error al obtener el cliente ganador.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
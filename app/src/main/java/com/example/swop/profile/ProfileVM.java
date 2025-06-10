package com.example.swop.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.swop.data.remote.models.CustomerDto;
import com.example.swop.data.repository.CustomerRepository;
import com.example.swop.data.session.SessionManager;
import com.example.swop.data.util.ApiCallback;

public class ProfileVM extends AndroidViewModel {

    private final CustomerRepository customerRepo;
    private final MutableLiveData<CustomerDto> customerLiveData = new MutableLiveData<>();

    public ProfileVM(@NonNull Application app) {
        super(app);
        customerRepo = new CustomerRepository(app);
        loadCustomer();
    }

    public MutableLiveData<CustomerDto> getCustomerLiveData() {
        return customerLiveData;
    }

    public void loadCustomer() {
        customerRepo.me(new ApiCallback<>() {
            @Override
            public void onSuccess(CustomerDto result) {
                customerLiveData.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                customerLiveData.postValue(null);
            }
        });
    }

    public void updateCustomer(CustomerDto updated, ApiCallback<CustomerDto> callback) {
        customerRepo.updateMe(updated, new ApiCallback<>() {
            @Override
            public void onSuccess(CustomerDto result) {
                if (callback != null) callback.onSuccess(result);
                loadCustomer(); // recarga el perfil actualizado
            }

            @Override
            public void onFailure(Throwable t) {
                if (callback != null) callback.onFailure(t);
            }
        });
    }

    public void logout() {
        new SessionManager(getApplication()).clear();
    }
}

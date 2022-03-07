package com.services.customer;

import com.services.amqp.RabbitMQMessageProducer;
import com.services.clients.fraud.FraudCheckResponse;
import com.services.clients.fraud.FraudClient;
import com.services.clients.notification.NotificationClient;
import com.services.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (Objects.requireNonNull(fraudCheckResponse).isFraudster()){
            throw new IllegalStateException("fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to Microservices...",
                        customer.getFirstName())
        );

        rabbitMQMessageProducer.publish(notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key");
    }
}

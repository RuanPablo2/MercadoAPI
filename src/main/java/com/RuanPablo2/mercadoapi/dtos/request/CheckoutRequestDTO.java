package com.RuanPablo2.mercadoapi.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequestDTO {

    @Valid
    @NotNull(message = "Endereço é obrigatório")
    private AddressDTO shippingAddress;

    @NotBlank(message = "Método de pagamento é obrigatório")
    private String paymentMethod;
}
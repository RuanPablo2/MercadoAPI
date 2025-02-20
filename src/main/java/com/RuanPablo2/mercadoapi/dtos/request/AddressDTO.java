package com.RuanPablo2.mercadoapi.dtos.request;

import com.RuanPablo2.mercadoapi.entities.Address;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AddressDTO {

    @NotBlank(message = "Rua é obrigatória")
    private String street;

    @NotBlank(message = "Número é obrigatório")
    private String number;

    private String complement;

    @NotBlank(message = "Bairro é obrigatório")
    private String neighborhood;

    @NotBlank(message = "Cidade é obrigatória")
    private String city;

    @NotBlank(message = "Estado é obrigatório")
    private String state;

    @NotBlank(message = "CEP é obrigatório")
    private String zipCode;

    public AddressDTO(Address entity) {
        street = entity.getStreet();
        number = entity.getNumber();
        complement = entity.getComplement();
        neighborhood = entity.getNeighborhood();
        city = entity.getCity();
        state = entity.getState();
        zipCode = entity.getZipCode();
    }
}
package br.com.cotiinformatica.api_clientes.dtos;

public record ClienteRequest(

        String nome, //nome do cliente
        String cpf, //cpf do cliente
        EnderecoRequest[] enderecos //array de endereços
) {
}

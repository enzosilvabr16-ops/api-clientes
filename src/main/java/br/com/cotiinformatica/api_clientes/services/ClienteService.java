package br.com.cotiinformatica.api_clientes.services;

import br.com.cotiinformatica.api_clientes.entities.Cliente;
import br.com.cotiinformatica.api_clientes.repositories.ClienteRepository;

public class ClienteService {
    //metodo para desenvolver as regras de negocio para cadastrar cliente
    public void cadastrarCliente(String nome, String cpf) throws Exception {

    //verificar se o nome está preenchido
    if(nome == null || nome.trim().length() < 6) {
        throw new IllegalArgumentException("O nome do cliente é obrigatório e deve ter pelo menos 6 caracteres");
    }
    //verificar ser o cpf está preenchido
        if (cpf== null) { //TODO validar o formato cpf
            throw new IllegalArgumentException("O cpf do cliente é obrigatório");
        }
    //verificar se o cpf já está cadastrado no bd
    var clienteRepository = new ClienteRepository();
        if(clienteRepository.cpfExistente(cpf)) {
            throw new IllegalArgumentException("O cpf djá está cadastrado. Tente outro.");
        }

        var cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(cpf);
    //Salvando o cliente no bd
    clienteRepository.inserir(cliente);
    }
}

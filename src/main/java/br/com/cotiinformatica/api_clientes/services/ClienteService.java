package br.com.cotiinformatica.api_clientes.services;

import br.com.cotiinformatica.api_clientes.dtos.ClienteRequest;
import br.com.cotiinformatica.api_clientes.entities.Cliente;
import br.com.cotiinformatica.api_clientes.repositories.ClienteRepository;

import java.util.List;

public class ClienteService {
    //metodo para desenvolver as regras de negocio para cadastrar cliente
    public void cadastrarCliente(ClienteRequest request) throws Exception {

    //verificar se o nome está preenchido
    if(request.nome() == null || request.nome().trim().length() < 6) {
        throw new IllegalArgumentException("O nome do cliente é obrigatório e deve ter pelo menos 6 caracteres");
    }
    //verificar ser o cpf está preenchido
        if (request.cpf()== null) { //TODO validar o formato cpf
            throw new IllegalArgumentException("O cpf do cliente é obrigatório");
        }
    //verificar se o cpf já está cadastrado no bd
    var clienteRepository = new ClienteRepository();
        if(clienteRepository.cpfExistente(request.cpf())) {
            throw new IllegalArgumentException("O cpf já está cadastrado. Tente outro.");
        }

        var cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setCpf(request.cpf());
    //Salvando o cliente no bd
    clienteRepository.inserir(cliente);
    }

    //metodo pra executar a pesquisa de clientes por nome
    public List<Cliente> pesquisarClientes(String nome) throws Exception {

        //verificar se o nome do cliente tem pelo menos 5 caracteres
        if(nome == null || nome.trim().length() < 5) {
            throw new IllegalArgumentException("O nome do cliente para pesquisa deve ter pelo menos 5 caracteres.");
        }

        //consultar os clientes no banco de dados
        var clienteRepository = new ClienteRepository();
        var lista = clienteRepository.Listar(nome);

        //retornar a lista de clientes
        return lista;
    }
}

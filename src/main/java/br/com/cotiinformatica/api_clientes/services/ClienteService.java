package br.com.cotiinformatica.api_clientes.services;

import br.com.cotiinformatica.api_clientes.dtos.ClienteRequest;
import br.com.cotiinformatica.api_clientes.entities.Cliente;
import br.com.cotiinformatica.api_clientes.entities.Endereco;
import br.com.cotiinformatica.api_clientes.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    //metodo para desenvolver as regras de negocio para cadastrar cliente
    public void cadastrarCliente(ClienteRequest request) throws Exception {

    //verificar se o nome está preenchido
    if(request.nome() == null || request.nome().trim().length() < 6) {
        throw new IllegalArgumentException("O nome do cliente é obrigatório e deve ter pelo menos 6 caracteres.");
    }
    //verificar ser o cpf está preenchido
        if (request.cpf()== null) { //TODO validar o formato cpf
            throw new IllegalArgumentException("O cpf do cliente é obrigatório");
        }

        if (request.enderecos() == null || request.enderecos().length == 0) {
            throw new IllegalArgumentException("O cliente tem que ter pelo menos 1 endereco para ser cadastrado.");

        }

    //verificar se o cpf já está cadastrado no bd
       if(clienteRepository.cpfExistente(request.cpf())) {
            throw new IllegalArgumentException("O cpf já está cadastrado. Tente outro.");
        }


        var cliente = new Cliente();
       cliente.setEnderecos(new ArrayList<>());

        cliente.setNome(request.nome());
        cliente.setCpf(request.cpf());

        for(var item : request.enderecos()) {
            var endereco = new Endereco();
            endereco.setLogradouro(item.logradouro());
            endereco.setNumero(item.numero());
            endereco.setComplemento(item.complemento());
            endereco.setBairro(item.bairro());
            endereco.setCidade(item.cidade());
            endereco.setUf(item.uf());
            endereco.setCep(item.cep());

            cliente.getEnderecos().add(endereco);



        }
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

        var lista = clienteRepository.Listar(nome);

        //retornar a lista de clientes
        return lista;


    }

}
